package com.example.fei.yhb_20.bean;

/**
 * Email luckyliangfei@gmail.com
 * Created by fei on 2/22/15.
 */
public class Merchant extends BaseUser{
    private String merchantName,license,photoPath,name;

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

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }
}
