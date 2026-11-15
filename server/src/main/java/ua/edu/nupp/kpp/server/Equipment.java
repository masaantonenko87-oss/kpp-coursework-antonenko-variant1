package ua.edu.nupp.kpp.server;

public class Equipment {
    private int id;
    private String name;
    private String serialNumber;
    private int criticality;

    public Equipment() {
    }

    public Equipment(String name, String serialNumber, int criticality) {
        this.name = name;
        this.serialNumber = serialNumber;
        this.criticality = criticality;
    }

    public Equipment(int id, String name, String serialNumber, int criticality) {
        this.id = id;
        this.name = name;
        this.serialNumber = serialNumber;
        this.criticality = criticality;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public int getCriticality() {
        return criticality;
    }

    public void setCriticality(int criticality) {
        this.criticality = criticality;
    }

    @Override
    public String toString() {
        return "Equipment{id=" + id + ", name='" + name + "', serialNumber='" + serialNumber + "', criticality=" + criticality + "}";
    }
}
