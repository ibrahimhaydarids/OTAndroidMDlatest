package com.ids.fixot.classes;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Amal on 11/29/2017.
 */

public class RequestTicketTypes implements Parcelable {

    public static final Creator CREATOR = new Creator() {
        public com.ids.fixot.model.Lookups createFromParcel(Parcel source) {
            return new com.ids.fixot.model.Lookups(source);
        }

        public com.ids.fixot.model.Lookups[] newArray(int size) {
            return new com.ids.fixot.model.Lookups[size];
        }
    };
    private String DescriptionAr,DescriptionEn;
    private int CashTicketTypeID;
    public RequestTicketTypes() {
    }

    public RequestTicketTypes(Parcel source) {

        readFromParcel(source);

    }


    public RequestTicketTypes(String descriptionAr, String descriptionEn, int CashTicketTypeID) {
        DescriptionAr = descriptionAr;
        DescriptionEn = descriptionEn;
        this.CashTicketTypeID = CashTicketTypeID;

    }

    public static Creator getCREATOR() {
        return CREATOR;
    }

    public String getDescriptionAr() {
        return DescriptionAr;
    }

    public void setDescriptionAr(String descriptionAr) {
        DescriptionAr = descriptionAr;
    }

    public String getDescriptionEn() {
        return DescriptionEn;
    }

    public void setDescriptionEn(String descriptionEn) {
        DescriptionEn = descriptionEn;
    }

    public int getCashTicketTypeID() {
        return CashTicketTypeID;
    }

    public void setCashTicketTypeID(int cashTicketTypeID) {
        CashTicketTypeID = cashTicketTypeID;
    }

    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(this.DescriptionAr);
        dest.writeString(this.DescriptionEn);
        dest.writeInt(this.CashTicketTypeID);


    }

    private void readFromParcel(Parcel source) {

        this.DescriptionAr = source.readString();
        this.DescriptionEn = source.readString();
        this.CashTicketTypeID = source.readInt();


    }

}
