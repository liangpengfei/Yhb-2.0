package com.example.fei.yhb_20.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *  在这个类中只是所有的基本信息，不应该有商户和个人之分
 * Email luckyliangfei@gmail.com
 * Created by fei on 3/20/15.
 */
public class MyInfo implements Serializable{
    private static final long serialVersionUID = 7901432275363141385L;
    //收藏   关注
    private ArrayList<String> mycollections,myTreasure;
    private String gender;
    private String hometown;
    private String wallpaper;

    public String getWallpaper() {
        return wallpaper;
    }

    public void setWallpaper(String wallpaper) {
        this.wallpaper = wallpaper;
    }

    public String getHometown() {
        return hometown;
    }

    public void setHometown(String hometown) {
        this.hometown = hometown;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public ArrayList<String> getMycollections() {
        return mycollections;
    }

    public void setMycollections(ArrayList<String> mycollections) {
        this.mycollections = mycollections;
    }

    public ArrayList<String> getMyTreasure() {
        return myTreasure;
    }

    public void setMyTreasure(ArrayList<String> myTreasure) {
        this.myTreasure = myTreasure;
    }
}
