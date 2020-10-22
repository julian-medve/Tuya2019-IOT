package com.test2019.tyapp.longhuan.model;


public class MainMenuModel {
    private String mTitle;
    private int mIcon;

    public MainMenuModel(String title, int icon) {
        this.mTitle = title;
        this.mIcon = icon;
    }

    public String getTitle() {
        return mTitle;
    }

    public int getIcon() {
        return mIcon;
    }
}
