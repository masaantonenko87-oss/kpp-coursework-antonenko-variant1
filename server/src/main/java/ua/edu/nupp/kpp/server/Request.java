package ua.edu.nupp.kpp.server;

public class Request {
    private int id;
    private int userId;
    private int equipmentId;
    private int categoryId;
    private int urgency;
    private int impact;
    private double priorityIndex;
    private RequestState status;

    public Request() {
    }

    public Request(int userId, int equipmentId, int categoryId, int urgency, int impact, double priorityIndex, RequestState status) {
        this.userId = userId;
        this.equipmentId = equipmentId;
        this.categoryId = categoryId;
        this.urgency = urgency;
        this.impact = impact;
        this.priorityIndex = priorityIndex;
        this.status = status;
    }

    public Request(int id, int userId, int equipmentId, int categoryId, int urgency, int impact, double priorityIndex, RequestState status) {
        this.id = id;
        this.userId = userId;
        this.equipmentId = equipmentId;
        this.categoryId = categoryId;
        this.urgency = urgency;
        this.impact = impact;
        this.priorityIndex = priorityIndex;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(int equipmentId) {
        this.equipmentId = equipmentId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public int getUrgency() {
        return urgency;
    }

    public void setUrgency(int urgency) {
        this.urgency = urgency;
    }

    public int getImpact() {
        return impact;
    }

    public void setImpact(int impact) {
        this.impact = impact;
    }

    public double getPriorityIndex() {
        return priorityIndex;
    }

    public void setPriorityIndex(double priorityIndex) {
        this.priorityIndex = priorityIndex;
    }

    public RequestState getStatus() {
        return status;
    }

    public void setStatus(RequestState status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Request{id=" + id + ", userId=" + userId + ", equipmentId=" + equipmentId +
                ", categoryId=" + categoryId + ", urgency=" + urgency + ", impact=" + impact +
                ", priorityIndex=" + priorityIndex + ", status=" + status + "}";
    }
}
