package com.ids.fixot.model;

public class SubscriberUser {

    private int DeviceID,ID,InvestorID;
    private String Email,key;
    private int Status;

    public int getDeviceID() {
        return DeviceID;
    }

    public void setDeviceID(int deviceID) {
        DeviceID = deviceID;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getInvestorID() {
        return InvestorID;
    }

    public void setInvestorID(int investorID) {
        InvestorID = investorID;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getStatus() {
        return Status;
    }

    public void setStatus(int status) {
        Status = status;
    }
}
