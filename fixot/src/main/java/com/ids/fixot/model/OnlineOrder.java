package com.ids.fixot.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.ids.fixot.MyApplication;
import com.ids.fixot.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 3/23/2017.
 */

public class OnlineOrder implements Parcelable {

    public static final Creator CREATOR
            = new Creator() {
        public OnlineOrder createFromParcel(Parcel in) {
            return new OnlineOrder(in);
        }

        public OnlineOrder[] newArray(int size) {
            return new OnlineOrder[size];
        }
    };
    private int ID, adminID, application, brokerEmployeeID, brokerID, durationID, forwardContractID,MarketID;
    private int reference, userID, userTypeID, triggerPriceTypeID, triggerPriceDirectionID, tradingSessionID, tradeTypeID,MarketStatus,AuctionMarketStatus,RelatedOnlineOrderID,AdvancedOrderTypeID;
    private int tStamp, statusID, quantityExecuted, quantity, maxfloor,orderTypeID, operationTypeID,OrderStatusTypeID;
    private double averagePrice, oldPrice, price, triggerPrice;
    private String statusDescription, rejectionCause, TradeTypeDescription,orderDateTime, goodUntilDate, executedDateTime, tradeID, portfolioNumber;
    private String stockID, investorID, key, targetSubID, orderNumber, stockName, stockSymbol, InstrumentID,nameEn,nameAr;
    private boolean canDelete, canUpdate, isInvalidOrder, hasUsersRestricted, hasErrors,IsSubOrder,IsParentOrder,IsAdvancedOrder;
    private List<ValueItem> allvalueItems = new ArrayList<ValueItem>();
    private String securityId,orderTypeDescription,AdvancedOrderTypeDescription,RelatedOnlineOrderNumber,priceFormatted;
    private ArrayList<OnlineOrder> arraySubOrders =new ArrayList<>();

    public OnlineOrder() {

    }

    // "De-parcel object
    public OnlineOrder(Parcel in) {

        ID = in.readInt();
        adminID = in.readInt();
        application = in.readInt();
        brokerEmployeeID = in.readInt();
        brokerID = in.readInt();
        durationID = in.readInt();
        forwardContractID = in.readInt();

        reference = in.readInt();
        userID = in.readInt();
        userTypeID = in.readInt();
        triggerPriceTypeID = in.readInt();
        triggerPriceDirectionID = in.readInt();
        tradingSessionID = in.readInt();
        tradeTypeID = in.readInt();

        tStamp = in.readInt();
        statusID = in.readInt();
        quantityExecuted = in.readInt();
        quantity = in.readInt();
        maxfloor = in.readInt();
        orderTypeID = in.readInt();
        orderTypeDescription = in.readString();
        OrderStatusTypeID = in.readInt();
        operationTypeID = in.readInt();

        averagePrice = in.readDouble();
        oldPrice = in.readDouble();
        price = in.readDouble();
        triggerPrice = in.readDouble();

        statusDescription = in.readString();
        rejectionCause = in.readString();
        orderDateTime = in.readString();
        goodUntilDate = in.readString();
        executedDateTime = in.readString();
        tradeID = in.readString();
        portfolioNumber = in.readString();

        stockID = in.readString();
        investorID = in.readString();
        key = in.readString();
        targetSubID = in.readString();
        orderNumber = in.readString();
        stockName = in.readString();
        stockSymbol = in.readString();
        InstrumentID = in.readString();

        MarketStatus = in.readInt();
        AuctionMarketStatus = in.readInt();
        RelatedOnlineOrderID = in.readInt();
        AdvancedOrderTypeID = in.readInt();
        IsSubOrder = in.readByte() != 0;
        IsParentOrder = in.readByte() != 0;
        IsAdvancedOrder = in.readByte() != 0;
        AdvancedOrderTypeDescription = in.readString();
        RelatedOnlineOrderNumber = in.readString();

        priceFormatted = in.readString();


        TradeTypeDescription = in.readString();
        MarketID = in.readInt();
        nameEn = in.readString();
        nameAr = in.readString();
        canDelete = in.readByte() != 0;
        canUpdate = in.readByte() != 0;
        isInvalidOrder = in.readByte() != 0;
        hasUsersRestricted = in.readByte() != 0;
        hasErrors = in.readByte() != 0;
        in.readList(allvalueItems, getClass().getClassLoader());
        in.readList(arraySubOrders, getClass().getClassLoader());
    }

    public int getTradeTypeColor() {

        if (this.tradeTypeID == MyApplication.ORDER_BUY) {// shira2

            return R.color.green_color;

        } else if (this.tradeTypeID == MyApplication.ORDER_SELL)// bay3
            return R.color.red_color;

        return R.color.colorValues;
    }

    public String getSecurityId() {
        return securityId;
    }

    public void setSecurityId(String securityId) {
        this.securityId = securityId;
    }

    public List<ValueItem> getAllvalueItems() {
        return allvalueItems;
    }

    public void setAllvalueItems(ArrayList<ValueItem> allvalueItems) {
        this.allvalueItems = allvalueItems;
    }

    public int getMarketID() {
        return MarketID;
    }

    public void setMarketID(int marketID) {
        MarketID = marketID;
    }

    public String getTradeTypeDescription() {
        return TradeTypeDescription;
    }

    public void setTradeTypeDescription(String tradeTypeDescription) {
        TradeTypeDescription = tradeTypeDescription;
    }

    public void setAllvalueItems(List<ValueItem> allvalueItems) {
        this.allvalueItems = allvalueItems;
    }

    public String getStatusDescription() {
        return statusDescription;
    }

    public void setStatusDescription(String statusDescription) {
        this.statusDescription = statusDescription;
    }

    public ArrayList<OnlineOrder> getArraySubOrders() {
        return arraySubOrders;
    }

    public void setArraySubOrders(ArrayList<OnlineOrder> arraySubOrders) {
        this.arraySubOrders = arraySubOrders;
    }

    public String getStockName() {
        return stockName;
    }

    public void setStockName(String stockName) {
        this.stockName = stockName;
    }

    public String getStockSymbol() {
        return stockSymbol;
    }

    public void setStockSymbol(String stockSymbol) {
        this.stockSymbol = stockSymbol;
    }

    public String getInstrumentID() {
        return InstrumentID;
    }

    public void setInstrumentID(String instrumentID) {
        InstrumentID = instrumentID;
    }

    public int getId() {
        return ID;
    }

    public String getInvestorID() {
        return investorID;
    }

    public void setInvestorID(String investorID) {
        this.investorID = investorID;
    }

    public int getAdminID() {
        return adminID;
    }

    public void setAdminID(int adminID) {
        this.adminID = adminID;
    }

    public int getApplication() {
        return application;
    }

    public void setApplication(int application) {
        this.application = application;
    }

    public int getBrokerEmployeeID() {
        return brokerEmployeeID;
    }

    public void setBrokerEmployeeID(int brokerEmployeeID) {
        this.brokerEmployeeID = brokerEmployeeID;
    }

    public int getBrokerID() {
        return brokerID;
    }

    public void setBrokerID(int brokerID) {
        this.brokerID = brokerID;
    }

    public int getDurationID() {
        return durationID;
    }

    public void setDurationID(int durationID) {
        this.durationID = durationID;
    }

    public int getForwardContractID() {
        return forwardContractID;
    }

    public void setForwardContractID(int forwardContractID) {
        this.forwardContractID = forwardContractID;
    }

    public String getTargetSubID() {
        return targetSubID;
    }

    public void setTargetSubID(String targetSubID) {
        this.targetSubID = targetSubID;
    }

    public int getReference() {
        return reference;
    }

    public void setReference(int reference) {
        this.reference = reference;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public int getUserTypeID() {
        return userTypeID;
    }

    public void setUserTypeID(int userTypeID) {
        this.userTypeID = userTypeID;
    }

    public int getTriggerPriceTypeID() {
        return triggerPriceTypeID;
    }

    public void setTriggerPriceTypeID(int triggerPriceTypeID) {
        this.triggerPriceTypeID = triggerPriceTypeID;
    }

    public int getTriggerPriceDirectionID() {
        return triggerPriceDirectionID;
    }

    public void setTriggerPriceDirectionID(int triggerPriceDirectionID) {
        this.triggerPriceDirectionID = triggerPriceDirectionID;
    }

    public String getOrderTypeDescription() {
        return orderTypeDescription;
    }

    public void setOrderTypeDescription(String orderTypeDescription) {
        this.orderTypeDescription = orderTypeDescription;
    }


    public int getTradingSessionID() {
        return tradingSessionID;
    }

    public void setTradingSessionID(int tradingSessionID) {
        this.tradingSessionID = tradingSessionID;
    }

    public int getTradeTypeID() {
        return tradeTypeID;
    }

    public void setTradeTypeID(int tradeTypeID) {
        this.tradeTypeID = tradeTypeID;
    }

    public String getTradeID() {
        return tradeID;
    }

    public void setTradeID(String tradeID) {
        this.tradeID = tradeID;
    }

    public int gettStamp() {
        return tStamp;
    }

    public void settStamp(int tStamp) {
        this.tStamp = tStamp;
    }

    public String getStockID() {
        return stockID;
    }

    public void setStockID(String stockID) {
        this.stockID = stockID;
    }

    public int getStatusID() {
        return statusID;
    }

    public void setStatusID(int statusID) {
        this.statusID = statusID;
    }

    public int getQuantityExecuted() {
        return quantityExecuted;
    }

    public void setQuantityExecuted(int quantityExecuted) {
        this.quantityExecuted = quantityExecuted;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }


    public int getMaxfloor() {
        return maxfloor;
    }

    public void setMaxfloor(int maxfloor) {
        this.maxfloor = maxfloor;
    }

    public void setSubOrder(boolean subOrder) {
        IsSubOrder = subOrder;
    }


    public boolean isParentOrder() {
        return IsParentOrder;
    }

    public void setParentOrder(boolean parentOrder) {
        IsParentOrder = parentOrder;
    }


    public boolean isAdvancedOrder() {
        return IsAdvancedOrder;
    }

    public void setAdvancedOrder(boolean advancedOrder) {
        IsAdvancedOrder = advancedOrder;
    }

    public String getPortfolioNumber() {
        return portfolioNumber;
    }

    public void setPortfolioNumber(String portfolioNumber) {
        this.portfolioNumber = portfolioNumber;
    }

    public int getOrderTypeID() {
        return orderTypeID;
    }

    public void setOrderTypeID(int orderTypeID) {
        this.orderTypeID = orderTypeID;
    }

    public int getOrderStatusTypeID() {
        return OrderStatusTypeID;
    }

    public void setOrderStatusTypeID(int orderStatusTypeID) {
        OrderStatusTypeID = orderStatusTypeID;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public int getOperationTypeID() {
        return operationTypeID;
    }

    public void setOperationTypeID(int operationTypeID) {
        this.operationTypeID = operationTypeID;
    }

    public double getAveragePrice() {
        return averagePrice;
    }

    public void setAveragePrice(double averagePrice) {
        this.averagePrice = averagePrice;
    }

    public double getOldPrice() {
        return oldPrice;
    }

    public void setOldPrice(double oldPrice) {
        this.oldPrice = oldPrice;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getTriggerPrice() {
        return triggerPrice;
    }

    public void setTriggerPrice(double triggerPrice) {
        this.triggerPrice = triggerPrice;
    }

    public String getRejectionCause() {
        return rejectionCause;
    }

    public void setRejectionCause(String rejectionCause) {
        this.rejectionCause = rejectionCause;
    }

    public String getOrderDateTime() {
        return orderDateTime;
    }

    public void setOrderDateTime(String orderDateTime) {
        this.orderDateTime = orderDateTime;
    }

    public String getGoodUntilDate() {
        return goodUntilDate;
    }

    public void setGoodUntilDate(String goodUntilDate) {
        this.goodUntilDate = goodUntilDate;
    }

    public String getExecutedDateTime() {
        return executedDateTime;
    }

    public void setExecutedDateTime(String executedDateTime) {
        this.executedDateTime = executedDateTime;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public boolean isCanDelete() {
        return canDelete;
    }

    public void setCanDelete(boolean canDelete) {
        this.canDelete = canDelete;
    }

    public boolean isCanUpdate() {
        return canUpdate;
    }

    public void setCanUpdate(boolean canUpdate) {
        this.canUpdate = canUpdate;
    }

    public boolean isInvalidOrder() {
        return isInvalidOrder;
    }

    public void setInvalidOrder(boolean invalidOrder) {
        isInvalidOrder = invalidOrder;
    }

    public boolean isHasUsersRestricted() {
        return hasUsersRestricted;
    }

    public void setHasUsersRestricted(boolean hasUsersRestricted) {
        this.hasUsersRestricted = hasUsersRestricted;
    }

    public boolean isHasErrors() {
        return hasErrors;
    }

    public void setHasErrors(boolean hasErrors) {
        this.hasErrors = hasErrors;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }


    public int getMarketStatus() {
        return MarketStatus;
    }

    public void setMarketStatus(int marketStatus) {
        MarketStatus = marketStatus;
    }

    public int getAuctionMarketStatus() {
        return AuctionMarketStatus;
    }

    public void setAuctionMarketStatus(int auctionMarketStatus) {
        AuctionMarketStatus = auctionMarketStatus;
    }

    public int getRelatedOnlineOrderID() {
        return RelatedOnlineOrderID;
    }

    public void setRelatedOnlineOrderID(int relatedOnlineOrderID) {
        RelatedOnlineOrderID = relatedOnlineOrderID;
    }

    public int getAdvancedOrderTypeID() {
        return AdvancedOrderTypeID;
    }

    public void setAdvancedOrderTypeID(int advancedOrderTypeID) {
        AdvancedOrderTypeID = advancedOrderTypeID;
    }

    public Boolean isSubOrder() {
        return IsSubOrder;
    }

    public String getPriceFormatted() {
        return priceFormatted;
    }

    public void setPriceFormatted(String priceFormatted) {
        this.priceFormatted = priceFormatted;
    }

    public String getAdvancedOrderTypeDescription() {
        return AdvancedOrderTypeDescription;
    }

    public void setAdvancedOrderTypeDescription(String advancedOrderTypeDescription) {
        AdvancedOrderTypeDescription = advancedOrderTypeDescription;
    }

    public String getRelatedOnlineOrderNumber() {
        return RelatedOnlineOrderNumber;
    }

    public void setRelatedOnlineOrderNumber(String relatedOnlineOrderNumber) {
        RelatedOnlineOrderNumber = relatedOnlineOrderNumber;
    }

    public String getNameEn() {
        return nameEn;
    }

    public void setNameEn(String nameEn) {
        this.nameEn = nameEn;
    }

    public String getNameAr() {
        return nameAr;
    }

    public void setNameAr(String nameAr) {
        this.nameAr = nameAr;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeInt(ID);
        dest.writeInt(adminID);
        dest.writeInt(application);
        dest.writeInt(brokerEmployeeID);
        dest.writeInt(brokerID);
        dest.writeInt(durationID);
        dest.writeInt(forwardContractID);

        dest.writeInt(reference);
        dest.writeInt(userID);
        dest.writeInt(userTypeID);
        dest.writeInt(triggerPriceTypeID);
        dest.writeInt(triggerPriceDirectionID);
        dest.writeInt(tradingSessionID);
        dest.writeInt(tradeTypeID);

        dest.writeInt(tStamp);
        dest.writeInt(statusID);
        dest.writeInt(quantityExecuted);
        dest.writeInt(quantity);
        dest.writeInt(maxfloor);
        dest.writeInt(orderTypeID);
        dest.writeString(orderTypeDescription);
        dest.writeInt(OrderStatusTypeID);
        dest.writeInt(operationTypeID);

        dest.writeDouble(averagePrice);
        dest.writeDouble(oldPrice);
        dest.writeDouble(price);
        dest.writeDouble(triggerPrice);

        dest.writeString(statusDescription);
        dest.writeString(rejectionCause);
        dest.writeString(orderDateTime);
        dest.writeString(goodUntilDate);
        dest.writeString(executedDateTime);
        dest.writeString(tradeID);
        dest.writeString(portfolioNumber);

        dest.writeString(stockID);
        dest.writeString(investorID);
        dest.writeString(key);
        dest.writeString(targetSubID);
        dest.writeString(orderNumber);
        dest.writeString(stockName);
        dest.writeString(stockSymbol);
        dest.writeString(InstrumentID);



        dest.writeInt(MarketStatus);
        dest.writeInt(AuctionMarketStatus);
        dest.writeInt(RelatedOnlineOrderID);
        dest.writeInt(AdvancedOrderTypeID);
        dest.writeByte((byte) (isSubOrder() ? 1 : 0));
        dest.writeByte((byte) (isParentOrder() ? 1 : 0));
        dest.writeByte((byte) (isAdvancedOrder() ? 1 : 0));
        dest.writeString(AdvancedOrderTypeDescription);
        dest.writeString(RelatedOnlineOrderNumber);
        dest.writeString(priceFormatted);
        dest.writeString(TradeTypeDescription);
        dest.writeInt(MarketID);

        dest.writeString(nameEn);
        dest.writeString(nameAr);
        dest.writeByte((byte) (canDelete ? 1 : 0));
        dest.writeByte((byte) (canUpdate ? 1 : 0));
        dest.writeByte((byte) (isInvalidOrder ? 1 : 0));
        dest.writeByte((byte) (hasUsersRestricted ? 1 : 0));
        dest.writeByte((byte) (hasErrors ? 1 : 0));
        dest.writeList(allvalueItems);
        dest.writeList(arraySubOrders);

    }

    public OnlineOrder(int durationID, int forwardContractID, int reference, int tradingSessionID, int tradeTypeID, int relatedOnlineOrderID, int advancedOrderTypeID, int statusID, int quantity, int orderTypeID, int operationTypeID, double price, double triggerPrice, String goodUntilDate, String stockID, boolean isSubOrder) {
        this.durationID = durationID;
        this.forwardContractID = forwardContractID;
        this.reference = reference;
        this.tradingSessionID = tradingSessionID;
        this.tradeTypeID = tradeTypeID;
        RelatedOnlineOrderID = relatedOnlineOrderID;
        AdvancedOrderTypeID = advancedOrderTypeID;
        this.statusID = statusID;
        this.quantity = quantity;
        this.orderTypeID = orderTypeID;
        this.operationTypeID = operationTypeID;
        this.price = price;
        this.triggerPrice = triggerPrice;
        this.goodUntilDate = goodUntilDate;
        this.stockID = stockID;
        IsSubOrder = isSubOrder;
    }

    public OnlineOrder(int ID, int adminID, int application, int brokerEmployeeID, int brokerID, int durationID, int forwardContractID, int reference, int userID, int userTypeID, int triggerPriceTypeID, int triggerPriceDirectionID, int tradingSessionID, int tradeTypeID, int tStamp, int statusID, int quantityExecuted, int quantity, int orderTypeID, int operationTypeID, int orderStatusTypeID, double averagePrice, double oldPrice, double price, double triggerPrice, String statusDescription, String rejectionCause, String orderDateTime, String goodUntilDate, String executedDateTime, String tradeID, String portfolioNumber, String stockID, String investorID, String key, String targetSubID, String orderNumber, String stockName, String stockSymbol, String instrumentID, boolean canDelete, boolean canUpdate, boolean isInvalidOrder, boolean hasUsersRestricted, boolean hasErrors, List<ValueItem> allvalueItems, String securityId, String orderTypeDescription,int maxfloor,int advancedOrderTypeID) {
        this.ID = ID;
        this.adminID = adminID;
        this.application = application;
        this.brokerEmployeeID = brokerEmployeeID;
        this.brokerID = brokerID;
        this.durationID = durationID;
        this.forwardContractID = forwardContractID;
        this.reference = reference;
        this.userID = userID;
        this.userTypeID = userTypeID;
        this.triggerPriceTypeID = triggerPriceTypeID;
        this.triggerPriceDirectionID = triggerPriceDirectionID;
        this.tradingSessionID = tradingSessionID;
        this.tradeTypeID = tradeTypeID;
        this.tStamp = tStamp;
        this.statusID = statusID;
        this.quantityExecuted = quantityExecuted;
        this.quantity = quantity;
        this.maxfloor=maxfloor;
        this.orderTypeID = orderTypeID;
        this.operationTypeID = operationTypeID;
        OrderStatusTypeID = orderStatusTypeID;
        this.averagePrice = averagePrice;
        this.oldPrice = oldPrice;
        this.price = price;
        this.triggerPrice = triggerPrice;
        this.statusDescription = statusDescription;
        this.rejectionCause = rejectionCause;
        this.orderDateTime = orderDateTime;
        this.goodUntilDate = goodUntilDate;
        this.executedDateTime = executedDateTime;
        this.tradeID = tradeID;
        this.portfolioNumber = portfolioNumber;
        this.stockID = stockID;
        this.investorID = investorID;
        this.key = key;
        this.targetSubID = targetSubID;
        this.orderNumber = orderNumber;
        this.stockName = stockName;
        this.stockSymbol = stockSymbol;
        InstrumentID = instrumentID;



        this.canDelete = canDelete;
        this.canUpdate = canUpdate;
        this.isInvalidOrder = isInvalidOrder;
        this.hasUsersRestricted = hasUsersRestricted;
        this.hasErrors = hasErrors;
        this.allvalueItems = allvalueItems;
        this.securityId = securityId;
        this.orderTypeDescription = orderTypeDescription;
        this.AdvancedOrderTypeID=advancedOrderTypeID;

    }
}




