package com.example.fei.yhb_20.bean;

import com.example.fei.yhb_20.ui.MerchantInfoPage;

import java.util.ArrayList;

/**
 * Email luckyliangfei@gmail.com
 * Created by fei on 2/22/15.
 */
public class Merchant extends BaseUser{
    private String merchantName,license,name;
    private MerchantInfo merchantInfo;
    private ArrayList<String> photoPaths;

    public MerchantInfo getMerchantInfo() {
        return merchantInfo;
    }

    public void setMerchantInfo(MerchantInfo merchantInfo) {
        this.merchantInfo = merchantInfo;
    }

    public ArrayList<String> getPhotoPaths() {
        return photoPaths;
    }

    public void setPhotoPaths(ArrayList<String> photoPaths) {
        this.photoPaths = photoPaths;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

}
