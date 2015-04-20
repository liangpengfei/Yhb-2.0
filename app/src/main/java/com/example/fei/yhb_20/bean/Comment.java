package com.example.fei.yhb_20.bean;

import java.io.Serializable;

import cn.bmob.v3.BmobObject;

/**
 * Email luckyliangfei@gmail.com
 * Created by fei on 4/19/15.
 */
public class Comment extends BmobObject implements Serializable {

    private static final long serialVersionUID = 6881622069000166766L;
    private Post post;
    private String content;
    private String userId;
    private String userName;

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {

        this.content = content;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
