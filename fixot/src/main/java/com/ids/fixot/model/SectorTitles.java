package com.ids.fixot.model;

import java.util.ArrayList;

/**
 * Created by Amal on 4/4/2017.
 */

public class SectorTitles {


    private String sectorId, sectorNameEn, sectorNameAr,symbolEn,symbolAr;

    public SectorTitles(String sectorId, String sectorNameEn, String sectorNameAr, String symbolEn, String symbolAr) {
        this.sectorId = sectorId;
        this.sectorNameEn = sectorNameEn;
        this.sectorNameAr = sectorNameAr;
        this.symbolEn = symbolEn;
        this.symbolAr = symbolAr;
    }

    public String getSectorId() {
        return sectorId;
    }

    public void setSectorId(String sectorId) {
        this.sectorId = sectorId;
    }

    public String getSectorNameEn() {
        return sectorNameEn;
    }

    public void setSectorNameEn(String sectorNameEn) {
        this.sectorNameEn = sectorNameEn;
    }

    public String getSectorNameAr() {
        return sectorNameAr;
    }

    public void setSectorNameAr(String sectorNameAr) {
        this.sectorNameAr = sectorNameAr;
    }

    public String getSymbolEn() {
        return symbolEn;
    }

    public void setSymbolEn(String symbolEn) {
        this.symbolEn = symbolEn;
    }

    public String getSymbolAr() {
        return symbolAr;
    }

    public void setSymbolAr(String symbolAr) {
        this.symbolAr = symbolAr;
    }
}
