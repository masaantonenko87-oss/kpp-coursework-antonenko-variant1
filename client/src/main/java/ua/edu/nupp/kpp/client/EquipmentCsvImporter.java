package ua.edu.nupp.kpp.client;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class EquipmentCsvImporter {

    public Equipment importFirstTestRow(HttpItSupportClient apiClient) throws Exception {
        Path csvPath = Path.of("sample-data", "equipment.csv");

        if (!Files.exists(csvPath)) {
            throw new Exception("Файл не знайдено за шляхом: " + csvPath.toAbsolutePath());
        }

        List<String> lines = Files.readAllLines(csvPath, StandardCharsets.UTF_8);
        if (lines.size() < 2) {
            throw new Exception("Файл equipment.csv повинен містити заголовок і хоча б один тестовий рядок.");
        }

        // Шукаємо перший непорожній рядок після заголовка
        String testLine = null;
        for (int i = 1; i < lines.size(); i++) {
            String candidate = lines.get(i).replace("\uFEFF", "").trim();
            if (!candidate.isEmpty()) {
                testLine = candidate;
                break;
            }
        }

        if (testLine == null) {
            throw new Exception("У файлі equipment.csv немає жодного непорожнього тестового рядка.");
        }

        String[] parts = testLine.split(";");
        if (parts.length != 3) {
            throw new Exception("Неправильний формат рядка CSV (очікується 3 поля через ';'): " + testLine);
        }

        // Порядок у твоєму файлі: inventory_number; name; criticality
        String serialNumber = parts[0].trim();
        String name = parts[1].trim();
        int criticality;
        try {
            criticality = Integer.parseInt(parts[2].trim());
        } catch (NumberFormatException e) {
            throw new Exception("Критичність у CSV повинна бути цілим числом, отримано: " + parts[2]);
        }

        // Формуємо об'єкт Equipment відповідно до полів бази даних
        Equipment equipment = new Equipment(0, name, serialNumber, criticality);

        // Передаємо на сервер через HTTP-клієнт
        apiClient.addEquipment(
                equipment.getName(),
                equipment.getSerialNumber(),
                equipment.getCriticality()
        );

        return equipment;
    }
}