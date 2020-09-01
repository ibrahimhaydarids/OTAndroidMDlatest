package com.ids.fixot.model;


import java.util.ArrayList;

public class BalanceSummaryParent {


    private String GroupId, Key, Symbol;
    private ArrayList<BalanceSummary> arraySummary;

    public BalanceSummaryParent() {

    }


    public String getGroupId() {
        return GroupId;
    }

    public void setGroupId(String groupId) {
        GroupId = groupId;
    }

    public String getKey() {
        return Key;
    }

    public void setKey(String key) {
        Key = key;
    }

    public String getSymbol() {
        return Symbol;
    }

    public void setSymbol(String symbol) {
        Symbol = symbol;
    }

    public ArrayList<BalanceSummary> getArraySummary() {
        return arraySummary;
    }

    public void setArraySummary(ArrayList<BalanceSummary> arraySummary) {
        this.arraySummary = arraySummary;
    }

    public BalanceSummaryParent(String groupId, String key, String symbol, ArrayList<BalanceSummary> arraySummary) {
        GroupId = groupId;
        Key = key;
        Symbol = symbol;
        this.arraySummary = arraySummary;
    }
}
