package ua.edu.nupp.kpp.server;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class EquipmentServlet extends HttpServlet {
    private final EquipmentService equipmentService = new EquipmentService();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json; charset=UTF-8");

        try {
            String idParam = req.getParameter("id");
            if (idParam != null && !idParam.trim().isEmpty()) {
                int id = Integer.parseInt(idParam.trim());
                Equipment equipment = equipmentService.getById(id);
                if (equipment == null) {
                    sendJson(resp, HttpServletResponse.SC_NOT_FOUND, Map.of("error", "Обладнання з ID " + id + " не знайдено."));
                    return;
                }
                sendJson(resp, HttpServletResponse.SC_OK, equipment);
            } else {
                List<Equipment> list = equipmentService.getAll();
                sendJson(resp, HttpServletResponse.SC_OK, list);
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
                throw new IllegalArgumentException("Не вказано параметр action (add, update, delete).");
            }

            switch (action.trim().toLowerCase()) {
                case "add" -> {
                    Equipment equipment = new Equipment();
                    equipment.setName(req.getParameter("name"));
                    equipment.setSerialNumber(req.getParameter("serialNumber"));
                    equipment.setCriticality(parseRequiredInt(req.getParameter("criticality"), "criticality"));

                    equipmentService.add(equipment);
                    sendJson(resp, HttpServletResponse.SC_CREATED, Map.of("message", "Обладнання успішно додано.", "item", equipment));
                }
                case "update" -> {
                    int id = parseRequiredInt(req.getParameter("id"), "id");
                    Equipment equipment = new Equipment();
                    equipment.setId(id);
                    equipment.setName(req.getParameter("name"));
                    equipment.setSerialNumber(req.getParameter("serialNumber"));
                    equipment.setCriticality(parseRequiredInt(req.getParameter("criticality"), "criticality"));

                    boolean updated = equipmentService.update(equipment);
                    if (!updated) {
                        sendJson(resp, HttpServletResponse.SC_NOT_FOUND, Map.of("error", "Обладнання з ID " + id + " не знайдено."));
                        return;
                    }
                    sendJson(resp, HttpServletResponse.SC_OK, Map.of("message", "Дані обладнання успішно оновлено."));
                }
                case "delete" -> {
                    int id = parseRequiredInt(req.getParameter("id"), "id");
                    boolean deleted = equipmentService.delete(id);
                    if (!deleted) {
                        sendJson(resp, HttpServletResponse.SC_NOT_FOUND, Map.of("error", "Обладнання з ID " + id + " не знайдено."));
                        return;
                    }
                    sendJson(resp, HttpServletResponse.SC_OK, Map.of("message", "Обладнання успішно видалено."));
                }
                default -> throw new IllegalArgumentException("Невідома дія action: " + action);
            }
        } catch (IllegalArgumentException e) {
            sendJson(resp, HttpServletResponse.SC_BAD_REQUEST, Map.of("error", e.getMessage()));
        } catch (SQLException e) {
            sendJson(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, Map.of("error", "Помилка бази даних: " + e.getMessage()));
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