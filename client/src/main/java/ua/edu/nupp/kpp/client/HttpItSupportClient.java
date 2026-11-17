package ua.edu.nupp.kpp.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpItSupportClient {

    private static final String BASE_URL = "http://localhost:8080/it-support-server";

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public HttpItSupportClient() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    // ==================== 1. КАТЕГОРІЇ (/categories) ====================

    public List<Category> getAllCategories() throws Exception {
        String json = sendGet("/categories");
        return objectMapper.readValue(json, new TypeReference<List<Category>>() {});
    }

    public void addCategory(String name) throws Exception {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("action", "add");
        params.put("name", name);
        sendPost("/categories", params);
    }

    public void updateCategory(int id, String name) throws Exception {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("action", "update");
        params.put("id", String.valueOf(id));
        params.put("name", name);
        sendPost("/categories", params);
    }

    public void deleteCategory(int id) throws Exception {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("action", "delete");
        params.put("id", String.valueOf(id));
        sendPost("/categories", params);
    }

    // ==================== 2. КОРИСТУВАЧІ (/users) ====================

    public List<User> getAllUsers() throws Exception {
        String json = sendGet("/users");
        return objectMapper.readValue(json, new TypeReference<List<User>>() {});
    }

    public void addUser(String name, String email, String role) throws Exception {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("action", "add");
        params.put("name", name);
        params.put("email", email);
        params.put("role", role);
        sendPost("/users", params);
    }

    public void updateUser(int id, String name, String email, String role) throws Exception {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("action", "update");
        params.put("id", String.valueOf(id));
        params.put("name", name);
        params.put("email", email);
        params.put("role", role);
        sendPost("/users", params);
    }

    public void deleteUser(int id) throws Exception {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("action", "delete");
        params.put("id", String.valueOf(id));
        sendPost("/users", params);
    }

    // ==================== 3. ОБЛАДНАННЯ (/equipment) ====================

    public List<Equipment> getAllEquipment() throws Exception {
        String json = sendGet("/equipment");
        return objectMapper.readValue(json, new TypeReference<List<Equipment>>() {});
    }

    public void addEquipment(String name, String serialNumber, int criticality) throws Exception {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("action", "add");
        params.put("name", name);
        params.put("serialNumber", serialNumber);
        params.put("criticality", String.valueOf(criticality));
        sendPost("/equipment", params);
    }

    public void updateEquipment(int id, String name, String serialNumber, int criticality) throws Exception {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("action", "update");
        params.put("id", String.valueOf(id));
        params.put("name", name);
        params.put("serialNumber", serialNumber);
        params.put("criticality", String.valueOf(criticality));
        sendPost("/equipment", params);
    }

    public void deleteEquipment(int id) throws Exception {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("action", "delete");
        params.put("id", String.valueOf(id));
        sendPost("/equipment", params);
    }

    // ==================== 4. ЗАЯВКИ (/requests) ====================

    public List<Request> getAllRequests() throws Exception {
        String json = sendGet("/requests");
        return objectMapper.readValue(json, new TypeReference<List<Request>>() {});
    }

    public void addRequest(int userId, int equipmentId, int categoryId, int urgency, int impact) throws Exception {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("action", "add");
        params.put("userId", String.valueOf(userId));
        params.put("equipmentId", String.valueOf(equipmentId));
        params.put("categoryId", String.valueOf(categoryId));
        params.put("urgency", String.valueOf(urgency));
        params.put("impact", String.valueOf(impact));
        sendPost("/requests", params);
    }

    public void updateRequest(int id, int userId, int equipmentId, int categoryId, int urgency, int impact) throws Exception {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("action", "update");
        params.put("id", String.valueOf(id));
        params.put("userId", String.valueOf(userId));
        params.put("equipmentId", String.valueOf(equipmentId));
        params.put("categoryId", String.valueOf(categoryId));
        params.put("urgency", String.valueOf(urgency));
        params.put("impact", String.valueOf(impact));
        sendPost("/requests", params);
    }

    public void deleteRequest(int id) throws Exception {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("action", "delete");
        params.put("id", String.valueOf(id));
        sendPost("/requests", params);
    }

    public void transitionRequest(int id, RequestState newStatus) throws Exception {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("action", "transition");
        params.put("id", String.valueOf(id));
        params.put("status", newStatus.name());
        sendPost("/requests", params);
    }

    // ==================== ДОПОМІЖНІ HTTP-МЕТОДИ ====================

    private String sendGet(String endpoint) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + endpoint))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        checkResponse(response);
        return response.body();
    }

    private String sendPost(String endpoint, Map<String, String> params) throws Exception {
        String formBody = params.entrySet().stream()
                .map(entry -> encode(entry.getKey()) + "=" + encode(entry.getValue()))
                .collect(Collectors.joining("&"));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + endpoint))
                .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                .POST(HttpRequest.BodyPublishers.ofString(formBody, StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        checkResponse(response);
        return response.body();
    }

    private String encode(String value) {
        return URLEncoder.encode(value == null ? "" : value, StandardCharsets.UTF_8);
    }

    private void checkResponse(HttpResponse<String> response) throws Exception {
        if (response.statusCode() >= 400) {
            String body = response.body();
            try {
                JsonNode node = objectMapper.readTree(body);
                if (node.has("error")) {
                    throw new Exception(node.get("error").asText());
                }
            } catch (Exception parsedOrOther) {
                if (parsedOrOther.getMessage() != null && !parsedOrOther.getMessage().isBlank()) {
                    throw parsedOrOther;
                }
            }
            throw new Exception("Помилка сервера (HTTP " + response.statusCode() + "): " + body);
        }
    }
}