package com.example.fei.yhb_20.bean;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobRelation;

/**
 * Email luckyliangfei@gmail.com
 * Created by fei on 2/22/15.
 */
public class BaseUser extends BmobUser {
    private String phone,motto,avatarPaht;
    private int attribute;
    private BmobRelation post;
    private MyInfo myInfo;

    public BmobRelation getPost() {
        return post;
    }

    public void setPost(BmobRelation post) {
        this.post = post;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMotto() {
        return motto;
    }

    public void setMotto(String motto) {
        this.motto = motto;
    }

    public String getAvatarPaht() {
        return avatarPaht;
    }

    public void setAvatarPaht(String avatarPaht) {
        this.avatarPaht = avatarPaht;
    }

    public int getAttribute() {
        return attribute;
    }

    public void setAttribute(int attribute) {
        this.attribute = attribute;
    }

    public MyInfo getMyInfo() {
        return myInfo;
    }

    public void setMyInfo(MyInfo myInfo) {
        this.myInfo = myInfo;
    }
}
