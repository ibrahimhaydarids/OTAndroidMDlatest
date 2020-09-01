package com.ids.fixot.model;


/**
 * Created by user on 3/31/2017.
 */

public class ExecutedOrders {


    private String StockID, Action, QuantityExecuted, AvgPrice, SecurityID, StockName, StockSymbol,tradeTypeID;


    public ExecutedOrders() {

    }

    public String getAction() {
        return Action;
    }

    public ExecutedOrders(String stockID, String action, String quantityExecuted, String avgPrice, String securityID, String stockName, String stockSymbol, String tradeTypeID) {
        StockID = stockID;
        Action = action;
        QuantityExecuted = quantityExecuted;
        AvgPrice = avgPrice;
        SecurityID = securityID;
        StockName = stockName;
        StockSymbol = stockSymbol;
        this.tradeTypeID = tradeTypeID;
    }

    public void setAction(String action) {
        Action = action;
    }

    public String getQuantityExecuted() {
        return QuantityExecuted;
    }

    public void setQuantityExecuted(String quantityExecuted) {
        QuantityExecuted = quantityExecuted;
    }

    public String getAvgPrice() {
        return AvgPrice;
    }

    public void setAvgPrice(String avgPrice) {
        AvgPrice = avgPrice;
    }

    public String getSecurityID() {
        return SecurityID;
    }

    public void setSecurityID(String securityID) {
        SecurityID = securityID;
    }

    public String getStockID() {
        return StockID;
    }

    public void setStockID(String stockID) {
        StockID = stockID;
    }

    public String getStockName() {
        return StockName;
    }

    public void setStockName(String stockName) {
        StockName = stockName;
    }

    public String getStockSymbol() {
        return StockSymbol;
    }

    public void setStockSymbol(String stockSymbol) {
        StockSymbol = stockSymbol;
    }

    public String getTradeTypeID() {
        return tradeTypeID;
    }

    public void setTradeTypeID(String tradeTypeID) {
        this.tradeTypeID = tradeTypeID;
    }
}
