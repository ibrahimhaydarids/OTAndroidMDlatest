package com.ids.fixot.model;



import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Amal on 11/29/2017.
 */

public class StockAlerts implements Parcelable {

    public static final Creator CREATOR = new Creator() {
        public StockAlerts createFromParcel(Parcel source) {
            return new StockAlerts(source);
        }

        public StockAlerts[] newArray(int size) {
            return new StockAlerts[size];
        }
    };
    private String Date,ExecutionDate,OperatorDescriptionAr,OperatorDescriptionEn,StatusDescriptionAr,StatusDescriptionEn,StockID,SybmolAr,SybmolEn,TypeDescriptionAr,TypeDescriptionEn;
    private int ID,OperatorID,StatusID,TypeID,UserID,Value;
    public StockAlerts() {
    }

    public StockAlerts(Parcel source) {

        readFromParcel(source);

    }

    public static Creator getCREATOR() {
        return CREATOR;
    }


    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getExecutionDate() {
        return ExecutionDate;
    }

    public void setExecutionDate(String executionDate) {
        ExecutionDate = executionDate;
    }

    public String getOperatorDescriptionAr() {
        return OperatorDescriptionAr;
    }

    public void setOperatorDescriptionAr(String operatorDescriptionAr) {
        OperatorDescriptionAr = operatorDescriptionAr;
    }

    public String getOperatorDescriptionEn() {
        return OperatorDescriptionEn;
    }

    public void setOperatorDescriptionEn(String operatorDescriptionEn) {
        OperatorDescriptionEn = operatorDescriptionEn;
    }

    public String getStatusDescriptionAr() {
        return StatusDescriptionAr;
    }

    public void setStatusDescriptionAr(String statusDescriptionAr) {
        StatusDescriptionAr = statusDescriptionAr;
    }

    public String getStatusDescriptionEn() {
        return StatusDescriptionEn;
    }

    public void setStatusDescriptionEn(String statusDescriptionEn) {
        StatusDescriptionEn = statusDescriptionEn;
    }

    public String getStockID() {
        return StockID;
    }

    public void setStockID(String stockID) {
        StockID = stockID;
    }

    public String getSybmolAr() {
        return SybmolAr;
    }

    public void setSybmolAr(String sybmolAr) {
        SybmolAr = sybmolAr;
    }

    public String getSybmolEn() {
        return SybmolEn;
    }

    public void setSybmolEn(String sybmolEn) {
        SybmolEn = sybmolEn;
    }

    public String getTypeDescriptionAr() {
        return TypeDescriptionAr;
    }

    public void setTypeDescriptionAr(String typeDescriptionAr) {
        TypeDescriptionAr = typeDescriptionAr;
    }

    public String getTypeDescriptionEn() {
        return TypeDescriptionEn;
    }

    public void setTypeDescriptionEn(String typeDescriptionEn) {
        TypeDescriptionEn = typeDescriptionEn;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getOperatorID() {
        return OperatorID;
    }

    public void setOperatorID(int operatorID) {
        OperatorID = operatorID;
    }

    public int getStatusID() {
        return StatusID;
    }

    public void setStatusID(int statusID) {
        StatusID = statusID;
    }

    public int getTypeID() {
        return TypeID;
    }

    public void setTypeID(int typeID) {
        TypeID = typeID;
    }

    public int getUserID() {
        return UserID;
    }

    public void setUserID(int userID) {
        UserID = userID;
    }

    public int getValue() {
        return Value;
    }

    public void setValue(int value) {
        Value = value;
    }

    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(this.Date);
        dest.writeString(this.ExecutionDate);
        dest.writeInt(this.ID);
        dest.writeString(this.OperatorDescriptionAr);
        dest.writeString(this.OperatorDescriptionEn);
        dest.writeInt(this.OperatorID);

        dest.writeString(this.StatusDescriptionAr);
        dest.writeString(this.StatusDescriptionEn);
        dest.writeInt(this.StatusID);
        dest.writeString(this.StockID);
        dest.writeString(this.SybmolAr);
        dest.writeString(this.SybmolEn);
        dest.writeString(this.TypeDescriptionAr);
        dest.writeString(this.TypeDescriptionEn);
        dest.writeInt(this.TypeID);
        dest.writeInt(this.UserID);
        dest.writeInt(this.Value);
    }

    private void readFromParcel(Parcel source) {

        this.Date = source.readString();
        this.ExecutionDate = source.readString();
        this.ID = source.readInt();
        this.OperatorDescriptionAr = source.readString();
        this.OperatorDescriptionEn = source.readString();
        this.OperatorID = source.readInt();

        this.StatusDescriptionAr = source.readString();
        this.StatusDescriptionEn = source.readString();
        this.StatusID = source.readInt();
        this.StockID = source.readString();
        this.SybmolAr = source.readString();
        this.SybmolEn = source.readString();
        this.TypeDescriptionAr = source.readString();
        this.TypeDescriptionEn = source.readString();
        this.TypeID = source.readInt();
        this.UserID = source.readInt();
        this.Value = source.readInt();


    }

}
