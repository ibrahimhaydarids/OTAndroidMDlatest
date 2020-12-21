package com.ids.fixot.model;

/**
 * Created by user on 3/31/2017.
 */

public class MarginPayment {


    private String amounDue, tradeDate, settlementDate;


    public MarginPayment() {

    }

    public MarginPayment(String amounDue, String tradeDate, String settlementDate) {
        this.amounDue = amounDue;
        this.tradeDate = tradeDate;
        this.settlementDate = settlementDate;
    }

    public String getAmounDue() {
        return amounDue;
    }

    public void setAmounDue(String amounDue) {
        this.amounDue = amounDue;
    }

    public String getTradeDate() {
        return tradeDate;
    }

    public void setTradeDate(String tradeDate) {
        this.tradeDate = tradeDate;
    }

    public String getSettlementDate() {
        return settlementDate;
    }

    public void setSettlementDate(String settlementDate) {
        this.settlementDate = settlementDate;
    }
}
