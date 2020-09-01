package com.ids.fixot.model;


import java.util.ArrayList;

public class NewsFiltered {


    private String date;
    private ArrayList<NewsItem> newsList=new ArrayList<>();

    public NewsFiltered(String date, ArrayList<NewsItem> newsList) {
        this.date = date;
        this.newsList = newsList;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public ArrayList<NewsItem> getNewsList() {
        return newsList;
    }

    public void setNewsList(ArrayList<NewsItem> newsList) {
        this.newsList = newsList;
    }
}
