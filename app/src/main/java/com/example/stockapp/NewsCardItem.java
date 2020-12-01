package com.example.stockapp;

public class NewsCardItem {
    private String mImageUrl;
    private String mSource;
    private String mTimeGap;
    private String mTitle;

    public NewsCardItem(String imageUrl, String source, String timeGap, String title){
        mImageUrl = imageUrl;
        mSource = source;
        mTimeGap = timeGap;
        mTitle = title;
    }

    public String getmImageUrl(){
        return mImageUrl;
    }

    public String getmSource(){
        return mSource;
    }

    public String getmTimeGap(){
        return mTimeGap;
    }

    public  String getmTitle(){
        return mTitle;
    }

}
