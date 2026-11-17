package ua.edu.nupp.kpp.client;

import javafx.application.Application;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.List;

public class MainApp extends Application {

    private final HttpItSupportClient apiClient = new HttpItSupportClient();
    private final EquipmentCsvImporter csvImporter = new EquipmentCsvImporter();

    private final ObservableList<Category> categoriesData = FXCollections.observableArrayList();
    private final ObservableList<User> usersData = FXCollections.observableArrayList();
    private final ObservableList<Equipment> equipmentData = FXCollections.observableArrayList();
    private final ObservableList<Request> requestsData = FXCollections.observableArrayList();

    @FunctionalInterface
    private interface ThrowingAction {
        void run() throws Exception;
    }

    @Override
    public void start(Stage primaryStage) {
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        tabPane.getTabs().addAll(
                createCategoriesTab(),
                createUsersTab(),
                createEquipmentTab(),
                createRequestsTab()
        );

        Scene scene = new Scene(tabPane, 1050, 650);
        primaryStage.setTitle("Інформаційна система обліку заявок ІТ-підтримки (Варіант 1)");
        primaryStage.setScene(scene);
        primaryStage.show();

        reloadAllData();
    }

    // ==================== ВКЛАДКА 1: КАТЕГОРІЇ ====================

    private Tab createCategoriesTab() {
        TableView<Category> table = new TableView<>(categoriesData);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        TableColumn<Category, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getId()));
        idCol.setMaxWidth(80);

        TableColumn<Category, String> nameCol = new TableColumn<>("Назва категорії");
        nameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getName()));

        table.getColumns().addAll(idCol, nameCol);

        TextField nameField = new TextField();
        nameField.setPromptText("Введіть назву категорії");

        table.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, selected) -> {
            if (selected != null) {
                nameField.setText(selected.getName());
            }
        });

        Button addBtn = new Button("Додати");
        addBtn.setOnAction(e -> safe(() -> {
            apiClient.addCategory(nameField.getText());
            clearFields(nameField);
            reloadCategories();
        }));

        Button updateBtn = new Button("Змінити");
        updateBtn.setOnAction(e -> safe(() -> {
            Category selected = requireSelection(table, "категорію");
            apiClient.updateCategory(selected.getId(), nameField.getText());
            reloadCategories();
        }));

        Button deleteBtn = new Button("Видалити");
        deleteBtn.setOnAction(e -> safe(() -> {
            Category selected = requireSelection(table, "категорію");
            apiClient.deleteCategory(selected.getId());
            clearFields(nameField);
            reloadCategories();
        }));

        Button refreshBtn = new Button("Оновити");
        refreshBtn.setOnAction(e -> safe(this::reloadCategories));

        Button clearBtn = new Button("Очистити");
        clearBtn.setOnAction(e -> {
            table.getSelectionModel().clearSelection();
            clearFields(nameField);
        });

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(8);
        form.add(new Label("Назва категорії:"), 0, 0);
        form.add(nameField, 1, 0);
        GridPane.setHgrow(nameField, Priority.ALWAYS);

        HBox buttons = new HBox(10, addBtn, updateBtn, deleteBtn, refreshBtn, clearBtn);
        buttons.setAlignment(Pos.CENTER_LEFT);

        VBox layout = new VBox(12, table, form, buttons);
        layout.setPadding(new Insets(12));
        VBox.setVgrow(table, Priority.ALWAYS);

        return new Tab("Категорії", layout);
    }

    // ==================== ВКЛАДКА 2: КОРИСТУВАЧІ ====================

    private Tab createUsersTab() {
        TableView<User> table = new TableView<>(usersData);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        TableColumn<User, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getId()));
        idCol.setMaxWidth(80);

        TableColumn<User, String> nameCol = new TableColumn<>("ПІБ користувача");
        nameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getName()));

        TableColumn<User, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEmail()));

        TableColumn<User, String> roleCol = new TableColumn<>("Роль");
        roleCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getRole()));

        table.getColumns().addAll(idCol, nameCol, emailCol, roleCol);

        TextField nameField = new TextField();
        nameField.setPromptText("Прізвище та ім'я");
        TextField emailField = new TextField();
        emailField.setPromptText("user@example.com");

        ComboBox<String> roleCombo = new ComboBox<>(FXCollections.observableArrayList("User", "Admin", "Engineer"));
        roleCombo.setEditable(true);
        roleCombo.setPromptText("Роль");
        roleCombo.setValue("User");

        table.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, selected) -> {
            if (selected != null) {
                nameField.setText(selected.getName());
                emailField.setText(selected.getEmail());
                roleCombo.setValue(selected.getRole());
            }
        });

        Button addBtn = new Button("Додати");
        addBtn.setOnAction(e -> safe(() -> {
            String role = roleCombo.getEditor().getText().isBlank() ? roleCombo.getValue() : roleCombo.getEditor().getText();
            apiClient.addUser(nameField.getText(), emailField.getText(), role);
            clearFields(nameField, emailField);
            reloadUsers();
        }));

        Button updateBtn = new Button("Змінити");
        updateBtn.setOnAction(e -> safe(() -> {
            User selected = requireSelection(table, "користувача");
            String role = roleCombo.getEditor().getText().isBlank() ? roleCombo.getValue() : roleCombo.getEditor().getText();
            apiClient.updateUser(selected.getId(), nameField.getText(), emailField.getText(), role);
            reloadUsers();
        }));

        Button deleteBtn = new Button("Видалити");
        deleteBtn.setOnAction(e -> safe(() -> {
            User selected = requireSelection(table, "користувача");
            apiClient.deleteUser(selected.getId());
            clearFields(nameField, emailField);
            reloadUsers();
        }));

        Button refreshBtn = new Button("Оновити");
        refreshBtn.setOnAction(e -> safe(this::reloadUsers));

        Button clearBtn = new Button("Очистити");
        clearBtn.setOnAction(e -> {
            table.getSelectionModel().clearSelection();
            clearFields(nameField, emailField);
            roleCombo.setValue("User");
        });

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(8);
        form.add(new Label("ПІБ:"), 0, 0);
        form.add(nameField, 1, 0);
        form.add(new Label("Email:"), 2, 0);
        form.add(emailField, 3, 0);
        form.add(new Label("Роль:"), 4, 0);
        form.add(roleCombo, 5, 0);
        GridPane.setHgrow(nameField, Priority.ALWAYS);
        GridPane.setHgrow(emailField, Priority.ALWAYS);

        HBox buttons = new HBox(10, addBtn, updateBtn, deleteBtn, refreshBtn, clearBtn);
        buttons.setAlignment(Pos.CENTER_LEFT);

        VBox layout = new VBox(12, table, form, buttons);
        layout.setPadding(new Insets(12));
        VBox.setVgrow(table, Priority.ALWAYS);

        return new Tab("Користувачі", layout);
    }

    // ==================== ВКЛАДКА 3: ОБЛАДНАННЯ ====================

    private Tab createEquipmentTab() {
        TableView<Equipment> table = new TableView<>(equipmentData);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        TableColumn<Equipment, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getId()));
        idCol.setMaxWidth(80);

        TableColumn<Equipment, String> nameCol = new TableColumn<>("Назва обладнання");
        nameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getName()));

        TableColumn<Equipment, String> serialCol = new TableColumn<>("Серійний номер");
        serialCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getSerialNumber()));

        TableColumn<Equipment, Integer> critCol = new TableColumn<>("Критичність (1..100)");
        critCol.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getCriticality()));

        table.getColumns().addAll(idCol, nameCol, serialCol, critCol);

        TextField nameField = new TextField();
        nameField.setPromptText("Ноутбук Lenovo");
        TextField serialField = new TextField();
        serialField.setPromptText("SN-12345-X");
        TextField critField = new TextField();
        critField.setPromptText("1..100");

        table.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, selected) -> {
            if (selected != null) {
                nameField.setText(selected.getName());
                serialField.setText(selected.getSerialNumber());
                critField.setText(String.valueOf(selected.getCriticality()));
            }
        });

        Button addBtn = new Button("Додати");
        addBtn.setOnAction(e -> safe(() -> {
            int criticality = parseIntField(critField.getText(), "Критичність");
            apiClient.addEquipment(nameField.getText(), serialField.getText(), criticality);
            clearFields(nameField, serialField, critField);
            reloadEquipment();
        }));

        Button updateBtn = new Button("Змінити");
        updateBtn.setOnAction(e -> safe(() -> {
            Equipment selected = requireSelection(table, "одиницю обладнання");
            int criticality = parseIntField(critField.getText(), "Критичність");
            apiClient.updateEquipment(selected.getId(), nameField.getText(), serialField.getText(), criticality);
            reloadEquipment();
        }));

        Button deleteBtn = new Button("Видалити");
        deleteBtn.setOnAction(e -> safe(() -> {
            Equipment selected = requireSelection(table, "одиницю обладнання");
            apiClient.deleteEquipment(selected.getId());
            clearFields(nameField, serialField, critField);
            reloadEquipment();
        }));

        Button refreshBtn = new Button("Оновити");
        refreshBtn.setOnAction(e -> safe(this::reloadEquipment));

        Button clearBtn = new Button("Очистити");
        clearBtn.setOnAction(e -> {
            table.getSelectionModel().clearSelection();
            clearFields(nameField, serialField, critField);
        });

        Button importCsvBtn = new Button("Імпорт з CSV (тест)");
        importCsvBtn.setOnAction(e -> safe(() -> {
            Equipment imported = csvImporter.importFirstTestRow(apiClient);
            reloadEquipment();
            showInfo("Тестовий рядок успішно імпортовано з sample-data/equipment.csv:\n"
                    + imported.getName() + " (" + imported.getSerialNumber() + "), критичність: " + imported.getCriticality());
        }));

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(8);
        form.add(new Label("Назва:"), 0, 0);
        form.add(nameField, 1, 0);
        form.add(new Label("Серійний №:"), 2, 0);
        form.add(serialField, 3, 0);
        form.add(new Label("Критичність:"), 4, 0);
        form.add(critField, 5, 0);
        GridPane.setHgrow(nameField, Priority.ALWAYS);
        GridPane.setHgrow(serialField, Priority.ALWAYS);

        HBox buttons = new HBox(10, addBtn, updateBtn, deleteBtn, refreshBtn, clearBtn, importCsvBtn);
        buttons.setAlignment(Pos.CENTER_LEFT);

        VBox layout = new VBox(12, table, form, buttons);
        layout.setPadding(new Insets(12));
        VBox.setVgrow(table, Priority.ALWAYS);

        return new Tab("Обладнання", layout);
    }

    // ==================== ВКЛАДКА 4: ЗАЯВКИ ====================

    private Tab createRequestsTab() {
        TableView<Request> table = new TableView<>(requestsData);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        TableColumn<Request, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getId()));
        idCol.setMaxWidth(60);

        TableColumn<Request, Integer> userCol = new TableColumn<>("Користувач ID");
        userCol.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getUserId()));

        TableColumn<Request, Integer> eqCol = new TableColumn<>("Обладнання ID");
        eqCol.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getEquipmentId()));

        TableColumn<Request, Integer> catCol = new TableColumn<>("Категорія ID");
        catCol.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getCategoryId()));

        TableColumn<Request, Integer> urgCol = new TableColumn<>("Терміновість (U)");
        urgCol.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getUrgency()));

        TableColumn<Request, Integer> impCol = new TableColumn<>("Вплив (M)");
        impCol.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getImpact()));

        TableColumn<Request, Double> prioCol = new TableColumn<>("Індекс пріоритету (I)");
        prioCol.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getPriorityIndex()));

        TableColumn<Request, RequestState> statusCol = new TableColumn<>("Статус");
        statusCol.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getStatus()));

        table.getColumns().addAll(idCol, userCol, eqCol, catCol, urgCol, impCol, prioCol, statusCol);

        ComboBox<User> userCombo = new ComboBox<>(usersData);
        userCombo.setPromptText("Оберіть користувача");
        userCombo.setMaxWidth(Double.MAX_VALUE);

        ComboBox<Equipment> equipmentCombo = new ComboBox<>(equipmentData);
        equipmentCombo.setPromptText("Оберіть обладнання");
        equipmentCombo.setMaxWidth(Double.MAX_VALUE);

        ComboBox<Category> categoryCombo = new ComboBox<>(categoriesData);
        categoryCombo.setPromptText("Оберіть категорію");
        categoryCombo.setMaxWidth(Double.MAX_VALUE);

        TextField urgencyField = new TextField();
        urgencyField.setPromptText("1..100");

        TextField impactField = new TextField();
        impactField.setPromptText("1..100");

        ComboBox<RequestState> stateCombo = new ComboBox<>(FXCollections.observableArrayList(RequestState.values()));
        stateCombo.setPromptText("Новий статус");

        table.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, selected) -> {
            if (selected != null) {
                usersData.stream().filter(u -> u.getId() == selected.getUserId()).findFirst().ifPresent(userCombo::setValue);
                equipmentData.stream().filter(eq -> eq.getId() == selected.getEquipmentId()).findFirst().ifPresent(equipmentCombo::setValue);
                categoriesData.stream().filter(cat -> cat.getId() == selected.getCategoryId()).findFirst().ifPresent(categoryCombo::setValue);
                urgencyField.setText(String.valueOf(selected.getUrgency()));
                impactField.setText(String.valueOf(selected.getImpact()));
                stateCombo.setValue(selected.getStatus());
            }
        });

        Button addBtn = new Button("Додати");
        addBtn.setOnAction(e -> safe(() -> {
            User user = requireComboValue(userCombo, "користувача");
            Equipment eq = requireComboValue(equipmentCombo, "обладнання");
            Category cat = requireComboValue(categoryCombo, "категорію");
            int urgency = parseIntField(urgencyField.getText(), "Терміновість (U)");
            int impact = parseIntField(impactField.getText(), "Вплив (M)");

            apiClient.addRequest(user.getId(), eq.getId(), cat.getId(), urgency, impact);
            clearFields(urgencyField, impactField);
            reloadRequests();
        }));

        Button updateBtn = new Button("Змінити");
        updateBtn.setOnAction(e -> safe(() -> {
            Request selected = requireSelection(table, "заявку");
            User user = requireComboValue(userCombo, "користувача");
            Equipment eq = requireComboValue(equipmentCombo, "обладнання");
            Category cat = requireComboValue(categoryCombo, "категорію");
            int urgency = parseIntField(urgencyField.getText(), "Терміновість (U)");
            int impact = parseIntField(impactField.getText(), "Вплив (M)");

            apiClient.updateRequest(selected.getId(), user.getId(), eq.getId(), cat.getId(), urgency, impact);
            reloadRequests();
        }));

        Button deleteBtn = new Button("Видалити");
        deleteBtn.setOnAction(e -> safe(() -> {
            Request selected = requireSelection(table, "заявку");
            apiClient.deleteRequest(selected.getId());
            clearFields(urgencyField, impactField);
            reloadRequests();
        }));

        Button refreshBtn = new Button("Оновити");
        refreshBtn.setOnAction(e -> safe(this::reloadAllData));

        Button clearBtn = new Button("Очистити");
        clearBtn.setOnAction(e -> {
            table.getSelectionModel().clearSelection();
            userCombo.setValue(null);
            equipmentCombo.setValue(null);
            categoryCombo.setValue(null);
            clearFields(urgencyField, impactField);
            stateCombo.setValue(null);
        });

        Button transitionBtn = new Button("Змінити статус");
        transitionBtn.setOnAction(e -> safe(() -> {
            Request selected = requireSelection(table, "заявку");
            RequestState targetState = requireComboValue(stateCombo, "новий статус");
            apiClient.transitionRequest(selected.getId(), targetState);
            reloadRequests();
        }));

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(8);
        form.add(new Label("Користувач:"), 0, 0);
        form.add(userCombo, 1, 0);
        form.add(new Label("Обладнання:"), 2, 0);
        form.add(equipmentCombo, 3, 0);
        form.add(new Label("Категорія:"), 4, 0);
        form.add(categoryCombo, 5, 0);

        form.add(new Label("Терміновість (U):"), 0, 1);
        form.add(urgencyField, 1, 1);
        form.add(new Label("Вплив (M):"), 2, 1);
        form.add(impactField, 3, 1);

        GridPane.setHgrow(userCombo, Priority.ALWAYS);
        GridPane.setHgrow(equipmentCombo, Priority.ALWAYS);
        GridPane.setHgrow(categoryCombo, Priority.ALWAYS);

        HBox crudButtons = new HBox(10, addBtn, updateBtn, deleteBtn, refreshBtn, clearBtn);
        crudButtons.setAlignment(Pos.CENTER_LEFT);

        HBox stateBox = new HBox(10, new Label("Перехід життєвого циклу заявки:"), stateCombo, transitionBtn);
        stateBox.setAlignment(Pos.CENTER_LEFT);
        stateBox.setPadding(new Insets(6, 10, 6, 10));
        stateBox.setStyle("-fx-background-color: #f0f4f8; -fx-border-color: #cbd5e1; -fx-border-radius: 4; -fx-background-radius: 4;");

        VBox layout = new VBox(12, table, form, crudButtons, stateBox);
        layout.setPadding(new Insets(12));
        VBox.setVgrow(table, Priority.ALWAYS);

        return new Tab("Заявки", layout);
    }

    // ==================== ЗАВАНТАЖЕННЯ ДАНИХ ТА ОБРОБКА ПОМИЛОК ====================

    private void reloadAllData() {
        safe(() -> {
            reloadCategories();
            reloadUsers();
            reloadEquipment();
            reloadRequests();
        });
    }

    private void reloadCategories() throws Exception {
        List<Category> list = apiClient.getAllCategories();
        categoriesData.setAll(list);
    }

    private void reloadUsers() throws Exception {
        List<User> list = apiClient.getAllUsers();
        usersData.setAll(list);
    }

    private void reloadEquipment() throws Exception {
        List<Equipment> list = apiClient.getAllEquipment();
        equipmentData.setAll(list);
    }

    private void reloadRequests() throws Exception {
        List<Request> list = apiClient.getAllRequests();
        requestsData.setAll(list);
    }

    private void safe(ThrowingAction action) {
        try {
            action.run();
        } catch (Exception ex) {
            String message = ex.getMessage();
            if (message == null || message.isBlank()) {
                message = "Не вдалося виконати запит. Перевірте, чи запущено Tomcat та MySQL.";
            }
            showError(message);
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Помилка виконання операції");
        alert.setHeaderText("Операцію не виконано");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Імпорт з CSV");
        alert.setHeaderText("Операцію виконано успішно");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private <T> T requireSelection(TableView<T> table, String entityName) throws Exception {
        T selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            throw new Exception("Спочатку оберіть " + entityName + " у таблиці!");
        }
        return selected;
    }

    private <T> T requireComboValue(ComboBox<T> comboBox, String fieldName) throws Exception {
        T value = comboBox.getValue();
        if (value == null) {
            throw new Exception("Оберіть значення для поля «" + fieldName + "»!");
        }
        return value;
    }

    private int parseIntField(String text, String fieldName) throws Exception {
        if (text == null || text.isBlank()) {
            throw new Exception("Поле «" + fieldName + "» не може бути порожнім!");
        }
        try {
            return Integer.parseInt(text.trim());
        } catch (NumberFormatException e) {
            throw new Exception("Поле «" + fieldName + "» повинно містити ціле число!");
        }
    }

    private void clearFields(TextField... fields) {
        for (TextField field : fields) {
            field.clear();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}