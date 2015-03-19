package com.example.fei.yhb_20.bean;

import java.io.Serializable;

/**
 * Email luckyliangfei@gmail.com
 * Created by fei on 3/18/15.
 */
public class CommentItem implements Serializable{
    private static final long serialVersionUID = 2767385268829230696L;
    private String objectId,comment,name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
