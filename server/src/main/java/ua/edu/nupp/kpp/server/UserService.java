package ua.edu.nupp.kpp.server;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

public class UserService {
    private static final Logger LOG = Logger.getLogger(UserService.class.getName());
    private final UserDAO userDAO = new UserDAO();

    public void add(User user) throws SQLException {
        validate(user, false);
        userDAO.add(user);
        LOG.info("Додано нового користувача ID=" + user.getId() + " (" + user.getName() + ")");
    }

    public List<User> getAll() throws SQLException {
        return userDAO.getAll();
    }

    public User getById(int id) throws SQLException {
        return userDAO.getById(id);
    }

    public boolean update(User user) throws SQLException {
        validate(user, true);
        boolean updated = userDAO.update(user);
        if (updated) {
            LOG.info("Оновлено дані користувача ID=" + user.getId());
        } else {
            LOG.warning("Не вдалося оновити користувача: запис з ID=" + user.getId() + " не знайдено.");
        }
        return updated;
    }

    public boolean delete(int id) throws SQLException {
        boolean deleted = userDAO.delete(id);
        if (deleted) {
            LOG.info("Видалено користувача ID=" + id);
        } else {
            LOG.warning("Не вдалося видалити користувача: запис з ID=" + id + " не знайдено.");
        }
        return deleted;
    }

    private void validate(User user, boolean checkId) {
        if (user == null) {
            failValidation("Дані користувача не можуть бути порожніми.");
        }
        if (checkId && user.getId() <= 0) {
            failValidation("Некоректний ID користувача.");
        }
        if (user.getName() == null || user.getName().trim().isEmpty()) {
            failValidation("Ім'я користувача не може бути порожнім.");
        }
        if (user.getEmail() == null || user.getEmail().trim().isEmpty() || !user.getEmail().contains("@")) {
            failValidation("Вказано некоректний email користувача.");
        }
        if (user.getRole() == null || user.getRole().trim().isEmpty()) {
            failValidation("Роль користувача не може бути порожньою.");
        }
    }

    private void failValidation(String message) {
        LOG.warning("Помилка валідації користувача: " + message);
        throw new IllegalArgumentException(message);
    }
}