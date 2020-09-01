package com.ids.fixot.model;

public class NotificationSettings {
    boolean Buy,CashEvents,Sell,StockEvents;
    String key;
    int UserID,Lang;

    public boolean isBuy() {
        return Buy;
    }

    public void setBuy(boolean buy) {
        Buy = buy;
    }

    public boolean isCashEvents() {
        return CashEvents;
    }

    public void setCashEvents(boolean cashEvents) {
        CashEvents = cashEvents;
    }

    public boolean isSell() {
        return Sell;
    }

    public void setSell(boolean sell) {
        Sell = sell;
    }

    public boolean isStockEvents() {
        return StockEvents;
    }

    public void setStockEvents(boolean stockEvents) {
        StockEvents = stockEvents;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getUserID() {
        return UserID;
    }

    public void setUserID(int userID) {
        UserID = userID;
    }

    public int getLang() {
        return Lang;
    }

    public void setLang(int lang) {
        Lang = lang;
    }
}
