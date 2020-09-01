package com.ids.fixot.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by Amal on 3/23/2017.
 */

public class Trade implements Parcelable {

    public static final Creator<Trade> CREATOR = new Creator<Trade>() {
        @Override
        public Trade createFromParcel(Parcel in) {
            return new Trade(in);
        }

        @Override
        public Trade[] newArray(int size) {
            return new Trade[size];
        }
    };
    private int reference, id, quantity, orderType,advancedOrderType,maxFloor,relatedOrderId, portfolioNumber, portfolioId, availableShareCount, statusTypeId,
            operationTypeID, durationTypeId, executedQuantity, tradeTypeID,isSubOrder;
    private double price,triggerPrice, cost, commission, overallTotal, purchasePower;
    private StockQuotation stockQuotation;
    private String date, durationType, goodUntilDate,ordertypeValueEn,ordertypeValueAr;
    private ArrayList<OnlineOrder> arraySubOrders=new ArrayList<>();
    private OnlineOrder relatedOrder=new OnlineOrder();

    public Trade() {
    }

    protected Trade(Parcel in) {

        reference = in.readInt();
        id = in.readInt();
        quantity = in.readInt();
        orderType = in.readInt();
        portfolioNumber = in.readInt();
        portfolioId = in.readInt();
        availableShareCount = in.readInt();
        statusTypeId = in.readInt();
        operationTypeID = in.readInt();
        durationTypeId = in.readInt();
        executedQuantity = in.readInt();
        tradeTypeID = in.readInt();

        price = in.readDouble();
        triggerPrice = in.readDouble();
        cost = in.readDouble();
        commission = in.readDouble();
        overallTotal = in.readDouble();
        purchasePower = in.readDouble();

        stockQuotation = in.readParcelable(getClass().getClassLoader());

        date = in.readString();
        durationType = in.readString();
        goodUntilDate = in.readString();
        ordertypeValueEn = in.readString();
        ordertypeValueAr = in.readString();

        relatedOrderId =in.readInt();
        advancedOrderType=in.readInt();
        maxFloor=in.readInt();
        relatedOrder = in.readParcelable(getClass().getClassLoader());
        try{in.readList(arraySubOrders, getClass().getClassLoader());}catch (Exception e){}
    }


    public int getAdvancedOrderType() {
        return advancedOrderType;
    }

    public void setAdvancedOrderType(int advancedOrderType) {
        this.advancedOrderType = advancedOrderType;
    }

    public OnlineOrder getRelatedOrder() {
        return relatedOrder;
    }

    public void setRelatedOrder(OnlineOrder relatedOrder) {
        this.relatedOrder = relatedOrder;
    }

    public int getMaxFloor() {
        return maxFloor;
    }

    public void setMaxFloor(int maxFloor) {
        this.maxFloor = maxFloor;
    }

    public int getRelatedOrderId() {
        return relatedOrderId;
    }

    public void setRelatedOrderId(int relatedOrderId) {
        this.relatedOrderId = relatedOrderId;
    }

    public double getIsSubOrder() {
        return isSubOrder;
    }

    public void setIsSubOrder(int isSubOrder) {
        this.isSubOrder = isSubOrder;
    }

    public ArrayList<OnlineOrder> getArraySubOrders() {
        return arraySubOrders;
    }

    public void setArraySubOrders(ArrayList<OnlineOrder> arraySubOrders) {
        this.arraySubOrders = arraySubOrders;
    }

    public double getTriggerPrice() {
        return triggerPrice;
    }

    public void setTriggerPrice(double triggerPrice) {
        this.triggerPrice = triggerPrice;
    }

    public int getReference() {
        return reference;
    }

    public void setReference(int reference) {
        this.reference = reference;
    }

    public int getAvailableShareCount() {
        return availableShareCount;
    }

    public void setAvailableShareCount(int availableShareCount) {
        this.availableShareCount = availableShareCount;
    }

    public int getStatusTypeId() {
        return statusTypeId;
    }

    public void setStatusTypeId(int statusTypeId) {
        this.statusTypeId = statusTypeId;
    }

    public int getOperationTypeID() {
        return operationTypeID;
    }

    public void setOperationTypeID(int operationTypeID) {
        this.operationTypeID = operationTypeID;
    }

    public double getPurchasePower() {
        return purchasePower;
    }

    public void setPurchasePower(double purchasePower) {
        this.purchasePower = purchasePower;
    }

    public int getPortfolioNumber() {
        return portfolioNumber;
    }

    public void setPortfolioNumber(int portfolioNumber) {
        this.portfolioNumber = portfolioNumber;
    }

    public int getPortfolioId() {
        return portfolioId;
    }

    public void setPortfolioId(int portfolioId) {
        this.portfolioId = portfolioId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public double getCommission() {
        return commission;
    }

    public void setCommission(double commission) {
        this.commission = commission;
    }

    public double getOverallTotal() {
        return overallTotal;
    }

    public void setOverallTotal(double overallTotal) {
        this.overallTotal = overallTotal;
    }

    public StockQuotation getStockQuotation() {
        return stockQuotation;
    }

    public void setStockQuotation(StockQuotation stockQuotation) {
        this.stockQuotation = stockQuotation;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getOrderType() {
        return orderType;
    }

    public void setOrderType(int orderType) {
        this.orderType = orderType;
    }

    public int getDurationTypeId() {
        return durationTypeId;
    }

    public void setDurationTypeId(int durationTypeId) {
        this.durationTypeId = durationTypeId;
    }

    public String getDurationType() {
        return durationType;
    }

    public void setDurationType(String durationType) {
        this.durationType = durationType;
    }

    public int getExecutedQuantity() {
        return executedQuantity;
    }

    public void setExecutedQuantity(int executedQuantity) {
        this.executedQuantity = executedQuantity;
    }

    public String getOrdertypeValueEn() {
        return ordertypeValueEn;
    }

    public void setOrdertypeValueEn(String ordertypeValueEn) {
        this.ordertypeValueEn = ordertypeValueEn;
    }
    public String getOrdertypeValueAr() {
        return ordertypeValueAr;
    }

    public void setOrdertypeValueAr(String ordertypeValueAr) {
        this.ordertypeValueAr = ordertypeValueAr;
    }
    public String getGoodUntilDate() {
        return goodUntilDate;
    }

    public void setGoodUntilDate(String goodUntilDate) {
        this.goodUntilDate = goodUntilDate;
    }

    public int getTradeTypeID() {
        return tradeTypeID;
    }

    public void setTradeTypeID(int tradeTypeID) {
        this.tradeTypeID = tradeTypeID;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeInt(reference);
        dest.writeInt(id);
        dest.writeInt(quantity);
        dest.writeInt(orderType);
        dest.writeInt(portfolioNumber);
        dest.writeInt(portfolioId);
        dest.writeInt(availableShareCount);
        dest.writeInt(statusTypeId);
        dest.writeInt(operationTypeID);
        dest.writeInt(durationTypeId);
        dest.writeInt(executedQuantity);
        dest.writeInt(tradeTypeID);

        dest.writeDouble(price);
        dest.writeDouble(triggerPrice);

        dest.writeDouble(cost);
        dest.writeDouble(commission);
        dest.writeDouble(overallTotal);
        dest.writeDouble(purchasePower);
        dest.writeParcelable(stockQuotation, flags);

        dest.writeString(date);
        dest.writeString(durationType);
        dest.writeString(goodUntilDate);
        dest.writeString(ordertypeValueEn);
        dest.writeString(ordertypeValueAr);

        dest.writeInt(relatedOrderId);
        dest.writeInt(advancedOrderType);
        dest.writeInt(maxFloor);
        dest.writeParcelable(relatedOrder, flags);
        try{dest.writeList(arraySubOrders);}catch (Exception e){}

    }
}
