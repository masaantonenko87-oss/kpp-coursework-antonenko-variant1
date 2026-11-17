package ua.edu.nupp.kpp.server;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@WebServlet("/categories")
public class CategoryServlet extends HttpServlet {
    private final CategoryService categoryService = new CategoryService();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json; charset=UTF-8");

        try {
            String idParam = req.getParameter("id");
            if (idParam != null && !idParam.trim().isEmpty()) {
                int id = parseRequiredInt(idParam, "id");
                Category category = categoryService.getById(id);
                if (category == null) {
                    sendJson(resp, HttpServletResponse.SC_NOT_FOUND, Map.of("error", "Категорію з ID " + id + " не знайдено."));
                    return;
                }
                sendJson(resp, HttpServletResponse.SC_OK, category);
            } else {
                List<Category> categories = categoryService.getAll();
                sendJson(resp, HttpServletResponse.SC_OK, categories);
            }
        } catch (IllegalArgumentException e) {
            sendJson(resp, HttpServletResponse.SC_BAD_REQUEST, Map.of("error", e.getMessage()));
        } catch (SQLException e) {
            sendJson(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, Map.of("error", "Помилка бази даних: " + e.getMessage()));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json; charset=UTF-8");

        try {
            String action = req.getParameter("action");
            if (action == null || action.trim().isEmpty()) {
                throw new IllegalArgumentException("Не вказано параметр action (add, update, delete).");
            }

            switch (action.trim().toLowerCase()) {
                case "add" -> {
                    Category category = new Category();
                    category.setName(req.getParameter("name"));
                    categoryService.add(category);
                    sendJson(resp, HttpServletResponse.SC_CREATED, Map.of("message", "Категорію успішно додано.", "item", category));
                }
                case "update" -> {
                    int id = parseRequiredInt(req.getParameter("id"), "id");
                    Category category = new Category();
                    category.setId(id);
                    category.setName(req.getParameter("name"));

                    boolean updated = categoryService.update(category);
                    if (!updated) {
                        sendJson(resp, HttpServletResponse.SC_NOT_FOUND, Map.of("error", "Категорію з ID " + id + " не знайдено."));
                        return;
                    }
                    sendJson(resp, HttpServletResponse.SC_OK, Map.of("message", "Категорію успішно оновлено."));
                }
                case "delete" -> {
                    int id = parseRequiredInt(req.getParameter("id"), "id");
                    boolean deleted = categoryService.delete(id);
                    if (!deleted) {
                        sendJson(resp, HttpServletResponse.SC_NOT_FOUND, Map.of("error", "Категорію з ID " + id + " не знайдено."));
                        return;
                    }
                    sendJson(resp, HttpServletResponse.SC_OK, Map.of("message", "Категорію успішно видалено."));
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