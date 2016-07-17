package com.example.nthucs.prototype.MessageList;

import android.graphics.Bitmap;
import android.widget.ImageView;

/**
 * Created by admin on 2016/7/17.
 */
public class Commit {
    private Bitmap postedImg;
    private String personName;
    private String personComment;

    public Commit(Bitmap img,String name,String comment){
        this.postedImg=img;
        this.personName=name;
        this.personComment=comment;
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
}
