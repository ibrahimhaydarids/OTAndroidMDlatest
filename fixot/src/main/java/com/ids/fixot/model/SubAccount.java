package com.ids.fixot.model;

import com.ids.fixot.MyApplication;

public class SubAccount {

    private int portfolioId, userId,MarketID;
    private String investorId,subAccount, name, nameAr, portfolioName,portfolioNameAr, portfolioNumber;
    private boolean isDefault;

    public SubAccount() {


    }

    public String getPortfolioNameAr() {
        return portfolioNameAr;
    }

    public void setPortfolioNameAr(String portfolioNameAr) {
        this.portfolioNameAr = portfolioNameAr;
    }

    public int getMarketID() {
        return MarketID;
    }

    public void setMarketID(int marketID) {
        MarketID = marketID;
    }

    public int getPortfolioId() {
        return portfolioId;
    }

    public void setPortfolioId(int portfolioId) {
        this.portfolioId = portfolioId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getInvestorId() {
        return investorId;
    }

    public void setInvestorId(String investorId) {
        this.investorId = investorId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameAr() {
        return nameAr;
    }

    public void setNameAr(String nameAr) {
        this.nameAr = nameAr;
    }

    public String getPortfolioName() {
        return portfolioName;
    }

    public void setPortfolioName(String portfolioName) {
        this.portfolioName = portfolioName;
    }

    public String getPortfolioNumber() {
        return portfolioNumber;
    }

    public void setPortfolioNumber(String portfolioNumber) {
        this.portfolioNumber = portfolioNumber;
    }

    public String getSubAccount() {
        return subAccount;
    }

    public void setSubAccount(String subAccount) {
        this.subAccount = subAccount;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }

    @Override
    public String toString() {
        String text = investorId + " - " + (MyApplication.lang == MyApplication.ARABIC ? nameAr : name) + " - " + portfolioName;
        return text;
    }
}
