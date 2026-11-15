package ua.edu.nupp.kpp.server;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

public class CategoryService {
    private static final Logger LOG = Logger.getLogger(CategoryService.class.getName());
    private final CategoryDAO categoryDAO = new CategoryDAO();

    public void add(Category category) throws SQLException {
        validate(category, false);
        categoryDAO.add(category);
        LOG.info("Додано нову категорію ID=" + category.getId() + " (" + category.getName() + ")");
    }

    public List<Category> getAll() throws SQLException {
        return categoryDAO.getAll();
    }

    public Category getById(int id) throws SQLException {
        return categoryDAO.getById(id);
    }

    public boolean update(Category category) throws SQLException {
        validate(category, true);
        boolean updated = categoryDAO.update(category);
        if (updated) {
            LOG.info("Оновлено категорію ID=" + category.getId());
        } else {
            LOG.warning("Не вдалося оновити категорію: запис з ID=" + category.getId() + " не знайдено.");
        }
        return updated;
    }

    public boolean delete(int id) throws SQLException {
        boolean deleted = categoryDAO.delete(id);
        if (deleted) {
            LOG.info("Видалено категорію ID=" + id);
        } else {
            LOG.warning("Не вдалося видалити категорію: запис з ID=" + id + " не знайдено.");
        }
        return deleted;
    }

    private void validate(Category category, boolean checkId) {
        if (category == null) {
            failValidation("Дані категорії не можуть бути порожніми.");
        }
        if (checkId && category.getId() <= 0) {
            failValidation("Некоректний ID категорії.");
        }
        if (category.getName() == null || category.getName().trim().isEmpty()) {
            failValidation("Назва категорії не може бути порожньою.");
        }
    }

    private void failValidation(String message) {
        LOG.warning("Помилка валідації категорії: " + message);
        throw new IllegalArgumentException(message);
    }
}