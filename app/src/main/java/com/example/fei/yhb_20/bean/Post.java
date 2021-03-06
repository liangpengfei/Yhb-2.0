package com.example.fei.yhb_20.bean;

import java.io.Serializable;
import java.util.ArrayList;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobRelation;

/**
 * Email luckyliangfei@gmail.com
 * Created by fei on 3/4/15.
 */
public class Post extends BmobObject implements Serializable {

    private static final long serialVersionUID = -2594277656564773968L;
    private String merchantName, content, paths, postName, activityTiem, province, city, district, ownerId;
    private float rating;
    private ArrayList<Integer> numberFooter;
    private ArrayList<String> thumnailsName;
    private ArrayList<CommentItem> commentItems;
    private BmobRelation comment;
    private String detailPos;

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public ArrayList<CommentItem> getCommentItems() {
        return commentItems;
    }

    public void setCommentItems(ArrayList<CommentItem> commentItems) {
        this.commentItems = commentItems;
    }

    public BmobRelation getComment() {
        return comment;
    }

    public void setComment(BmobRelation comment) {
        this.comment = comment;
    }

    public ArrayList<String> getThumnailsName() {
        return thumnailsName;
    }

    public void setThumnailsName(ArrayList<String> thumnailsName) {
        this.thumnailsName = thumnailsName;
    }

    public ArrayList getNumberFooter() {
        return numberFooter;
    }

    public void setNumberFooter(ArrayList numberFooter) {
        this.numberFooter = numberFooter;
    }

    public String getActivityTiem() {
        return activityTiem;
    }

    public void setActivityTiem(String activityTiem) {
        this.activityTiem = activityTiem;
    }

    private BaseUser user;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public BaseUser getUser() {
        return user;
    }

    public void setUser(BaseUser user) {
        this.user = user;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPaths() {
        return paths;
    }

    public void setPaths(String paths) {
        this.paths = paths;
    }

    public String getPostName() {
        return postName;
    }

    public void setPostName(String postName) {
        this.postName = postName;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getDetailPos() {
        return detailPos;
    }

    public void setDetailPos(String detailPos) {
        this.detailPos = detailPos;
    }

    @Override
    public String toString() {
        return "Post{" +
                "merchantName='" + merchantName + '\'' +
                ", content='" + content + '\'' +
                ", paths='" + paths + '\'' +
                ", postName='" + postName + '\'' +
                ", activityTiem='" + activityTiem + '\'' +
                ", province='" + province + '\'' +
                ", city='" + city + '\'' +
                ", district='" + district + '\'' +
                ", ownerId='" + ownerId + '\'' +
                ", rating=" + rating +
                ", numberFooter=" + numberFooter +
                ", thumnailsName=" + thumnailsName +
                ", commentItems=" + commentItems +
                ", user=" + user +
                '}';
    }
}
