package com.lifeifanzs.memorableintent.Bean;

public class Theme {

    private String mColor = "WHITE";
    private String mUri;
    private int mPager;
    private boolean mStatusColorIsLight;

    public boolean isStatusColorIsLight() {
        return mStatusColorIsLight;
    }

    public void setStatusColorIsLight(boolean statusColorIsLight) {
        mStatusColorIsLight = statusColorIsLight;
    }

    //    public Theme(){};


    public int getPager() {
        return mPager;
    }

    public void setPager(int pager) {
        mPager = pager;
    }

    public String getUri() {
        return mUri;
    }

    public void setUri(String uri) {
        mUri = uri;
    }

    public String getColor() {
        return mColor;
    }

    public void setColor(String color) {
        mColor = color;
    }
}
