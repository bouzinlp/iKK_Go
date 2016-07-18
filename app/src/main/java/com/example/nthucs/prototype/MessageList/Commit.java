package com.example.nthucs.prototype.MessageList;

import android.graphics.Bitmap;



/**
 * Created by admin on 2016/7/17.
 */
public class Commit {
    public Bitmap postedImg;
    public String personName;
    public String personComment;
    public String postID;

    public Commit(Bitmap img,String name,String comment,String postID){
        this.postedImg=img;
        this.personName=name;
        this.personComment=comment;
        this.postID = postID;
    }

    public Bitmap getBitmap() {
        return postedImg;
    }

    public void setBitmap(Bitmap bitmap) {
        this.postedImg = bitmap;
    }

    public String getName() {
        return personName;
    }

    public void setName(String name) {
        this.personName = name;
    }

    public String getCommit() {
        return personComment;
    }

    public void setCommit(String comment) {
        this.personComment = comment;
    }

    public String getPostID() {
        return postID;
    }

    public void setPostID(String id) {
        this.postID = id;
    }

}
