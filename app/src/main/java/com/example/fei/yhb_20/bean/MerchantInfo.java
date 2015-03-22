package com.example.fei.yhb_20.bean;

import java.io.Serializable;

/**
 * 这个里面存放一些商户特有的信息
 * Email luckyliangfei@gmail.com
 * Created by fei on 3/20/15.
 */
public class MerchantInfo implements Serializable{
    private static final long serialVersionUID = -8207586760818581660L;
    private String address,phone,onTime,sort;
    private boolean isAuthenticated;

    public boolean isAuthenticated() {
        return isAuthenticated;
    }

    public void setAuthenticated(boolean isAuthenticated) {
        this.isAuthenticated = isAuthenticated;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getOnTime() {
        return onTime;
    }

    public void setOnTime(String onTime) {
        this.onTime = onTime;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

}
