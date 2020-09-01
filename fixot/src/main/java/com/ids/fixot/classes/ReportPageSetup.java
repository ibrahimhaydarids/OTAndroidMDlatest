package com.ids.fixot.classes;


public class ReportPageSetup {
    private int ID,TypeID;
    private String NameEn,NameAr,ReportLink;
    private boolean HasFromDate,HasTodate;

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getTypeID() {
        return TypeID;
    }

    public void setTypeID(int typeID) {
        TypeID = typeID;
    }

    public String getNameEn() {
        return NameEn;
    }

    public void setNameEn(String nameEn) {
        NameEn = nameEn;
    }

    public String getNameAr() {
        return NameAr;
    }

    public void setNameAr(String nameAr) {
        NameAr = nameAr;
    }

    public String getReportLink() {
        return ReportLink;
    }

    public void setReportLink(String reportLink) {
        ReportLink = reportLink;
    }

    public boolean isHasFromDate() {
        return HasFromDate;
    }

    public void setHasFromDate(boolean hasFromDate) {
        HasFromDate = hasFromDate;
    }

    public boolean isHasTodate() {
        return HasTodate;
    }

    public void setHasTodate(boolean hasTodate) {
        HasTodate = hasTodate;
    }
}
