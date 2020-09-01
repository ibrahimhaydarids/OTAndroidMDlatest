package com.ids.fixot.model;



import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Amal on 11/29/2017.
 */

public class Lookups implements Parcelable {

    public static final Creator CREATOR = new Creator() {
        public Lookups createFromParcel(Parcel source) {
            return new Lookups(source);
        }

        public Lookups[] newArray(int size) {
            return new Lookups[size];
        }
    };
    private String DescriptionAr,DescriptionEn,NameAr,NameEn;
    private int ID,Value;
    public Lookups() {
    }

    public Lookups(Parcel source) {

        readFromParcel(source);

    }


    public Lookups(String descriptionAr, String descriptionEn, String nameAr, String nameEn, int ID, int value) {
        DescriptionAr = descriptionAr;
        DescriptionEn = descriptionEn;
        NameAr = nameAr;
        NameEn = nameEn;
        this.ID = ID;
        Value = value;
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

    public String getNameAr() {
        return NameAr;
    }

    public void setNameAr(String nameAr) {
        NameAr = nameAr;
    }

    public String getNameEn() {
        return NameEn;
    }

    public void setNameEn(String nameEn) {
        NameEn = nameEn;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
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

        dest.writeString(this.DescriptionAr);
        dest.writeString(this.DescriptionEn);
        dest.writeInt(this.ID);
        dest.writeString(this.NameAr);
        dest.writeString(this.NameEn);
        dest.writeInt(this.Value);

    }

    private void readFromParcel(Parcel source) {

        this.DescriptionAr = source.readString();
        this.DescriptionEn = source.readString();
        this.ID = source.readInt();
        this.NameAr = source.readString();
        this.NameEn = source.readString();
        this.Value = source.readInt();

    }

}
