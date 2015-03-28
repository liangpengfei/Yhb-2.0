package com.example.fei.yhb_20.bean;

import java.util.ArrayList;

import cn.bmob.v3.BmobObject;

/**
 * 这个类用来存储其他人关注当前用户的一些信息
 * Email luckyliangfei@gmail.com
 * Created by fei on 3/22/15.
 */
public class OtherInfo extends BmobObject {

    private static final long serialVersionUID = 5864852877794549107L;
//    private BaseUser user;
    private ArrayList<String> followerIds;
    private ArrayList<String> followingIds;
    private String userId;

    public ArrayList<String> getFollowingIds() {
        return followingIds;
    }

    public void setFollowingIds(ArrayList<String> followingIds) {
        this.followingIds = followingIds;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public ArrayList<String> getFollowerIds() {
        return followerIds;
    }

    public void setFollowerIds(ArrayList<String> followerIds) {
        this.followerIds = followerIds;
    }


//
//    public BaseUser getUser() {
//        return user;
//    }
//
//    public void setUser(BaseUser user) {
//        this.user = user;
//    }
}
