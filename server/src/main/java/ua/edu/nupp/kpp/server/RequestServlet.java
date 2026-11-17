package ua.edu.nupp.kpp.server;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class RequestServlet extends HttpServlet {
    private final RequestService requestService = new RequestService();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json; charset=UTF-8");

        try {
            String idParam = req.getParameter("id");
            if (idParam != null && !idParam.trim().isEmpty()) {
                int id = Integer.parseInt(idParam.trim());
                Request request = requestService.getById(id);
                if (request == null) {
                    sendJson(resp, HttpServletResponse.SC_NOT_FOUND, Map.of("error", "Заявку з ID " + id + " не знайдено."));
                    return;
                }
                sendJson(resp, HttpServletResponse.SC_OK, request);
            } else {
                List<Request> requests = requestService.getAll();
                sendJson(resp, HttpServletResponse.SC_OK, requests);
            }
        } catch (NumberFormatException e) {
            sendJson(resp, HttpServletResponse.SC_BAD_REQUEST, Map.of("error", "Некоректний числовий формат ID."));
        } catch (SQLException e) {
            sendJson(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, Map.of("error", "Помилка бази даних: " + e.getMessage()));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json; charset=UTF-8");

        try {
            String action = req.getParameter("action");
            if (action == null || action.trim().isEmpty()) {
                throw new IllegalArgumentException("Не вказано параметр action (add, update, delete, transition).");
            }

            switch (action.trim().toLowerCase()) {
                case "add" -> {
                    Request request = new Request();
                    request.setUserId(parseRequiredInt(req.getParameter("userId"), "userId"));
                    request.setEquipmentId(parseRequiredInt(req.getParameter("equipmentId"), "equipmentId"));
                    request.setCategoryId(parseRequiredInt(req.getParameter("categoryId"), "categoryId"));
                    request.setUrgency(parseRequiredInt(req.getParameter("urgency"), "urgency"));
                    request.setImpact(parseRequiredInt(req.getParameter("impact"), "impact"));
                    // RequestService вимагає, щоб нова заявка створювалася зі статусом NEW
                    request.setStatus(RequestState.NEW);

                    requestService.add(request);
                    sendJson(resp, HttpServletResponse.SC_CREATED, Map.of("message", "Заявку успішно створено.", "item", request));
                }
                case "update" -> {
                    int id = parseRequiredInt(req.getParameter("id"), "id");
                    Request existing = requestService.getById(id);
                    if (existing == null) {
                        sendJson(resp, HttpServletResponse.SC_NOT_FOUND, Map.of("error", "Заявку з ID " + id + " не знайдено."));
                        return;
                    }

                    Request request = new Request();
                    request.setId(id);
                    request.setUserId(parseRequiredInt(req.getParameter("userId"), "userId"));
                    request.setEquipmentId(parseRequiredInt(req.getParameter("equipmentId"), "equipmentId"));
                    request.setCategoryId(parseRequiredInt(req.getParameter("categoryId"), "categoryId"));
                    request.setUrgency(parseRequiredInt(req.getParameter("urgency"), "urgency"));
                    request.setImpact(parseRequiredInt(req.getParameter("impact"), "impact"));

                    String statusParam = req.getParameter("status");
                    if (statusParam != null && !statusParam.trim().isEmpty()) {
                        request.setStatus(parseRequestState(statusParam));
                    } else {
                        request.setStatus(existing.getStatus());
                    }

                    requestService.update(request);
                    sendJson(resp, HttpServletResponse.SC_OK, Map.of("message", "Заявку успішно оновлено.", "item", request));
                }
                case "transition" -> {
                    int id = parseRequiredInt(req.getParameter("id"), "id");
                    String statusParam = req.getParameter("status");
                    if (statusParam == null || statusParam.trim().isEmpty()) {
                        statusParam = req.getParameter("newState");
                    }
                    RequestState newState = parseRequestState(statusParam);
                    requestService.changeState(id, newState);
                    sendJson(resp, HttpServletResponse.SC_OK, Map.of("message", "Стан заявки ID=" + id + " змінено на " + newState + "."));
                }
                case "delete" -> {
                    int id = parseRequiredInt(req.getParameter("id"), "id");
                    boolean deleted = requestService.delete(id);
                    if (!deleted) {
                        sendJson(resp, HttpServletResponse.SC_NOT_FOUND, Map.of("error", "Заявку з ID " + id + " не знайдено."));
                        return;
                    }
                    sendJson(resp, HttpServletResponse.SC_OK, Map.of("message", "Заявку успішно видалено."));
                }
                default -> throw new IllegalArgumentException("Невідома дія action: " + action);
            }
        } catch (IllegalArgumentException | IllegalStateException e) {
            sendJson(resp, HttpServletResponse.SC_BAD_REQUEST, Map.of("error", e.getMessage()));
        } catch (SQLException e) {
            sendJson(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, Map.of("error", "Помилка бази даних: " + e.getMessage()));
        }
    }

    private RequestState parseRequestState(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Не вказано статус заявки (NEW, ASSIGNED, IN_PROGRESS, CLOSED).");
        }
        try {
            return RequestState.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Невідомий статус '" + value + "'. Допустимі значення: NEW, ASSIGNED, IN_PROGRESS, CLOSED.");
        }
    }

    private int parseRequiredInt(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Параметр '" + fieldName + "' є обов'язковим.");
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Параметр '" + fieldName + "' має бути цілим числом.");
        }
    }

    private void sendJson(HttpServletResponse resp, int statusCode, Object payload) throws IOException {
        resp.setStatus(statusCode);
        objectMapper.writeValue(resp.getWriter(), payload);
    }
}