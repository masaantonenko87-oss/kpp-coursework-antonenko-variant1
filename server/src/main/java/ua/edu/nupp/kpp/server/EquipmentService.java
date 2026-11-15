package ua.edu.nupp.kpp.server;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

public class EquipmentService {
    private static final Logger LOG = Logger.getLogger(EquipmentService.class.getName());
    private final EquipmentDAO equipmentDAO = new EquipmentDAO();

    public void add(Equipment equipment) throws SQLException {
        validate(equipment, false);
        equipmentDAO.add(equipment);
        LOG.info("Додано нове обладнання ID=" + equipment.getId() + " (" + equipment.getName() + ")");
    }

    public List<Equipment> getAll() throws SQLException {
        return equipmentDAO.getAll();
    }

    public Equipment getById(int id) throws SQLException {
        return equipmentDAO.getById(id);
    }

    public boolean update(Equipment equipment) throws SQLException {
        validate(equipment, true);
        boolean updated = equipmentDAO.update(equipment);
        if (updated) {
            LOG.info("Оновлено обладнання ID=" + equipment.getId());
        } else {
            LOG.warning("Не вдалося оновити обладнання: запис з ID=" + equipment.getId() + " не знайдено.");
        }
        return updated;
    }

    public boolean delete(int id) throws SQLException {
        boolean deleted = equipmentDAO.delete(id);
        if (deleted) {
            LOG.info("Видалено обладнання ID=" + id);
        } else {
            LOG.warning("Не вдалося видалити обладнання: запис з ID=" + id + " не знайдено.");
        }
        return deleted;
    }

    private void validate(Equipment equipment, boolean checkId) {
        if (equipment == null) {
            failValidation("Дані обладнання не можуть бути порожніми.");
        }
        if (checkId && equipment.getId() <= 0) {
            failValidation("Некоректний ID обладнання.");
        }
        if (equipment.getName() == null || equipment.getName().trim().isEmpty()) {
            failValidation("Назва обладнання не може бути порожньою.");
        }
        if (equipment.getSerialNumber() == null || equipment.getSerialNumber().trim().isEmpty()) {
            failValidation("Серійний номер обладнання не може бути порожнім.");
        }
        if (equipment.getCriticality() < 0 || equipment.getCriticality() > 100) {
            failValidation("Критичність обладнання має бути в межах від 0 до 100.");
        }
    }

    private void failValidation(String message) {
        LOG.warning("Помилка валідації обладнання: " + message);
        throw new IllegalArgumentException(message);
    }
}