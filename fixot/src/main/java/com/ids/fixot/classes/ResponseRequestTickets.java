package com.ids.fixot.classes;


import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Amal on 11/29/2017.
 */

public class ResponseRequestTickets implements Parcelable {

    public static final Creator CREATOR = new Creator() {
        public com.ids.fixot.model.Lookups createFromParcel(Parcel source) {
            return new com.ids.fixot.model.Lookups(source);
        }

        public com.ids.fixot.model.Lookups[] newArray(int size) {
            return new com.ids.fixot.model.Lookups[size];
        }
    };
    private String Amount,StatusDescription,TicketDate,TypeDescription;
    private int StatusID,TypeID;
    private Boolean canCancel;
    public ResponseRequestTickets() {
    }

    public ResponseRequestTickets(Parcel source) {

        readFromParcel(source);

    }


    public Boolean getCanCancel() {
        return canCancel;
    }

    public void setCanCancel(Boolean canCancel) {
        this.canCancel = canCancel;
    }

    public static Creator getCREATOR() {
        return CREATOR;
    }

    public String getAmount() {
        return Amount;
    }

    public void setAmount(String amount) {
        Amount = amount;
    }

    public void setStatusDescription(String statusDescription) {
        StatusDescription = statusDescription;
    }

    public void setTicketDate(String ticketDate) {
        TicketDate = ticketDate;
    }

    public void setTypeDescription(String typeDescription) {
        TypeDescription = typeDescription;
    }

    public void setStatusID(int statusID) {
        StatusID = statusID;
    }

    public void setTypeID(int typeID) {
        TypeID = typeID;
    }

    public String getStatusDescription() {
        return StatusDescription;
    }

    public String getTicketDate() {
        return TicketDate;
    }

    public String getTypeDescription() {
        return TypeDescription;
    }

    public int getStatusID() {
        return StatusID;
    }

    public int getTypeID() {
        return TypeID;
    }

    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(this.Amount);
        dest.writeString(this.StatusDescription);
        dest.writeString(this.TicketDate);
        dest.writeString(this.TypeDescription);
        dest.writeString(this.StatusDescription);
        dest.writeInt(this.StatusID);
        dest.writeInt(this.TypeID);
        dest.writeByte((byte) (canCancel ? 1 : 0));

    }

    private void readFromParcel(Parcel source) {

        this.Amount = source.readString();
        this.StatusDescription = source.readString();
        this.TicketDate = source.readString();
        this.TypeDescription = source.readString();
        this.StatusDescription = source.readString();

        this.StatusID = source.readInt();
        this.TypeID = source.readInt();
        this.canCancel = source.readByte() != 0;


    }

}
