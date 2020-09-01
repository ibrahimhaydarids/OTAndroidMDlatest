package com.ids.fixot.model;


import com.ids.fixot.R;

/**
 * Created by Amal on 3/23/2017.
 */

public class Ordertypes {

    private int OrderTypeID;
    private int IsEditable,Enabled,IsAdvanced;
    private String DescriptionAr,DescriptionEn;


    public Ordertypes() {
    }

    public int getOrderTypeID() {
        return OrderTypeID;
    }

    public void setOrderTypeID(int orderTypeID) {
        OrderTypeID = orderTypeID;
    }

    public int getIsEditable() {
        return IsEditable;
    }

    public void setIsEditable(int isEditable) {
        IsEditable = isEditable;
    }

    public int getEnabled() {
        return Enabled;
    }

    public void setEnabled(int enabled) {
        Enabled = enabled;
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

    public int getIsAdvanced() {
        return IsAdvanced;
    }

    public void setIsAdvanced(int isAdvanced) {
        IsAdvanced = isAdvanced;
    }


    public Ordertypes(int orderTypeID, int isEditable, int enabled, int isAdvanced, String descriptionAr, String descriptionEn) {
        OrderTypeID = orderTypeID;
        IsEditable = isEditable;
        Enabled = enabled;
        IsAdvanced = isAdvanced;
        DescriptionAr = descriptionAr;
        DescriptionEn = descriptionEn;
    }
}
