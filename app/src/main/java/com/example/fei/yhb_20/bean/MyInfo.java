package com.example.fei.yhb_20.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Email luckyliangfei@gmail.com
 * Created by fei on 3/20/15.
 */
public class MyInfo implements Serializable{
    private static final long serialVersionUID = 7901432275363141385L;
    private ArrayList<String> mycollections,myTreasure;

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
