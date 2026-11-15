package ua.edu.nupp.kpp.server;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

public class RequestService {
    private static final Logger LOG = Logger.getLogger(RequestService.class.getName());
    private final RequestDAO requestDAO = new RequestDAO();
    private final UserDAO userDAO = new UserDAO();
    private final EquipmentDAO equipmentDAO = new EquipmentDAO();
    private final CategoryDAO categoryDAO = new CategoryDAO();

    public void add(Request request) throws SQLException {
        validate(request, false);
        calculateAndSetPriority(request);
        requestDAO.add(request);
        LOG.info("Створено нову заявку ID=" + request.getId()
                + ", статус=" + request.getStatus()
                + ", індекс пріоритету=" + request.getPriorityIndex());
    }

    public List<Request> getAll() throws SQLException {
        return requestDAO.getAll();
    }

    public Request getById(int id) throws SQLException {
        return requestDAO.getById(id);
    }

    public boolean update(Request request) throws SQLException {
        validate(request, true);
        calculateAndSetPriority(request);
        boolean updated = requestDAO.update(request);
        if (updated) {
            LOG.info("Оновлено заявку ID=" + request.getId()
                    + ", перерахований індекс пріоритету=" + request.getPriorityIndex());
        } else {
            LOG.warning("Не вдалося оновити заявку: запис з ID=" + request.getId() + " не знайдено.");
        }
        return updated;
    }

    public boolean delete(int id) throws SQLException {
        boolean deleted = requestDAO.delete(id);
        if (deleted) {
            LOG.info("Видалено заявку ID=" + id);
        } else {
            LOG.warning("Не вдалося видалити заявку: запис з ID=" + id + " не знайдено.");
        }
        return deleted;
    }

    public boolean isAllowedTransition(RequestState current, RequestState next) {
        if (current == null || next == null) {
            return false;
        }
        return switch (current) {
            case NEW -> next == RequestState.ASSIGNED;
            case ASSIGNED -> next == RequestState.IN_PROGRESS;
            case IN_PROGRESS -> next == RequestState.CLOSED;
            case CLOSED -> false;
        };
    }

    public void changeState(int requestId, RequestState newState) throws SQLException {
        if (newState == null) {
            String msg = "Новий стан заявки не може бути порожнім.";
            LOG.warning("Відхилено зміну стану заявки ID=" + requestId + ": " + msg);
            throw new IllegalArgumentException(msg);
        }
        Request existing = requestDAO.getById(requestId);
        if (existing == null) {
            String msg = "Заявку з ID " + requestId + " не знайдено.";
            LOG.warning("Відхилено зміну стану: " + msg);
            throw new IllegalArgumentException(msg);
        }
        if (!isAllowedTransition(existing.getStatus(), newState)) {
            String msg = "Недопустимий перехід стану заявки: з " + existing.getStatus() + " у " + newState;
            LOG.warning("Відхилено зміну стану заявки ID=" + requestId + ": " + msg);
            throw new IllegalStateException(msg);
        }
        requestDAO.updateState(requestId, newState);
        LOG.info("Змінено стан заявки ID=" + requestId + ": " + existing.getStatus() + " -> " + newState);
    }

    private void calculateAndSetPriority(Request request) throws SQLException {
        Equipment equipment = equipmentDAO.getById(request.getEquipmentId());
        double index = PriorityCalculator.calculate(
                request.getUrgency(),
                request.getImpact(),
                equipment.getCriticality()
        );
        request.setPriorityIndex(index);
    }

    private void validate(Request request, boolean checkId) throws SQLException {
        if (request == null) {
            failValidation("Дані заявки не можуть бути порожніми.");
        }
        if (checkId && request.getId() <= 0) {
            failValidation("Некоректний ID заявки.");
        }
        if (request.getUrgency() < 0 || request.getUrgency() > 100) {
            failValidation("Терміновість заявки має бути в межах від 0 до 100.");
        }
        if (request.getImpact() < 0 || request.getImpact() > 100) {
            failValidation("Вплив заявки має бути в межах від 0 до 100.");
        }
        if (request.getStatus() == null) {
            failValidation("Статус заявки не може бути порожнім.");
        }
        if (!checkId && request.getStatus() != RequestState.NEW) {
            failValidation("Нова заявка повинна створюватися зі статусом NEW.");
        }
        if (request.getUserId() <= 0 || userDAO.getById(request.getUserId()) == null) {
            failValidation("Користувача з ID " + request.getUserId() + " не існує.");
        }
        if (request.getEquipmentId() <= 0 || equipmentDAO.getById(request.getEquipmentId()) == null) {
            failValidation("Обладнання з ID " + request.getEquipmentId() + " не існує.");
        }
        if (request.getCategoryId() <= 0 || categoryDAO.getById(request.getCategoryId()) == null) {
            failValidation("Категорії з ID " + request.getCategoryId() + " не існує.");
        }
    }

    private void failValidation(String message) {
        LOG.warning("Помилка валідації заявки: " + message);
        throw new IllegalArgumentException(message);
    }
}