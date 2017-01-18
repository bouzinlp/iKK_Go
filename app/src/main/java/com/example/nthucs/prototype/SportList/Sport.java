package com.example.nthucs.prototype.SportList;

import java.util.Date;
import java.util.Locale;

/**
 * Created by user on 2016/7/27.
 */
public class Sport implements java.io.Serializable {

    private long id;

    private long userID;

    // sport's name
    private String title;

    // sport's description
    private String content;

    // sport's estimated consumption of calorie
    private float calorie;

    // whether this sport is selected
    private boolean selected;

    // sport list's establish time
    private long datetime;

    // sport event's total time
    private long totalTime;

    public Sport() {
        this.title = new String();
        this.content = new String();
    }

    public long getId() {return this.id;}
    public void setId(long id) {this.id = id;}

    public long getUserID(){return this.userID;}
    public void setUserID(long userID){this.userID = userID;}

    public String getTitle() {return title;}
    public void setTitle(String title) {this.title = title;}

    public String getContent() {return content;}
    public void setContent(String content) {this.content = content;}

    public float getCalorie() {return this.calorie;}
    public void setCalorie(float calorie) {this.calorie = calorie;}

    public boolean isSelected() {return selected;}
    public void setSelected(boolean selected) {this.selected = selected;}

    public long getDatetime() {return datetime;}
    public String getLocaleDatetime() {return String.format(Locale.getDefault(), "%tF  %<tR", new Date(datetime));}
    public void setDatetime(long datetime) {this.datetime = datetime;}

    public long getTotalTime() {return totalTime;}
    public void setTotalTime(long totalTime) {this.totalTime = totalTime;}
}
