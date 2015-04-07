package com.example.fei.yhb_20.utils;

/**
 * 在这个里面放置全局变量，以统一风格，以后可能要合并到其他的类中去
 * Email luckyliangfei@gmail.com
 * Created by fei on 2/12/15.
 */
public class GV {
    public static final char PERSON = 0;
    public static final char MERCHANT = 1;
    public static final char CLASS_PRESSED = 0;
    public static final char MAIN_PRESSED = 1;

    public static Class myClass;
    public static String content,merchantName;
    public static float rating;
    public static String registName;
    public static String serialNumber;
    public static String registMerchantName;

    public static String getRegistMerchantName() {
        return registMerchantName;
    }

    public static void setRegistMerchantName(String registMerchantName) {
        GV.registMerchantName = registMerchantName;
    }

    public static String getRegistName() {
        return registName;
    }

    public static void setRegistName(String registName) {
        GV.registName = registName;
    }

    public static String getSerialNumber() {
        return serialNumber;
    }

    public static void setSerialNumber(String serialNumber) {
        GV.serialNumber = serialNumber;
    }

    public static String getMerchantName() {
        return merchantName;
    }

    public static void setMerchantName(String merchantName) {
        GV.merchantName = merchantName;
    }

    public static float getRating() {
        return rating;
    }

    public static void setRating(float rating) {
        GV.rating = rating;
    }

    public static String getContent() {
        return content;
    }

    public static void setContent(String content) {
        GV.content = content;
    }

    public static Class getMyClass() {
        return myClass;
    }

    public static void setMyClass(Class myClass) {
        GV.myClass = myClass;
    }
}
