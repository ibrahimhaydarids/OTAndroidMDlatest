package com.ids.fixot.model;

public class ApplicationList {
    private int id,technicalOrderType,defaultAdminID,DefaultBrokerEmployeeID;
    private String name;
    private boolean isEditable;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTechnicalOrderType() {
        return technicalOrderType;
    }

    public void setTechnicalOrderType(int technicalOrderType) {
        this.technicalOrderType = technicalOrderType;
    }

    public int getDefaultAdminID() {
        return defaultAdminID;
    }

    public void setDefaultAdminID(int defaultAdminID) {
        this.defaultAdminID = defaultAdminID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isEditable() {
        return isEditable;
    }


    public int getDefaultBrokerEmployeeID() {
        return DefaultBrokerEmployeeID;
    }

    public void setDefaultBrokerEmployeeID(int defaultBrokerEmployeeID) {
        DefaultBrokerEmployeeID = defaultBrokerEmployeeID;
    }

    public void setEditable(boolean editable) {
        isEditable = editable;
    }
}
