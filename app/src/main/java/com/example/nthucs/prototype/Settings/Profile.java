package com.example.nthucs.prototype.Settings;

import java.util.Date;
import java.util.Locale;

/**
 * Created by USER12345678 on 2016/7/24.
 */
public class Profile implements java.io.Serializable {

    private long id;
    private long userID;
    // profile's establish time
    private long datetime;

    // profile's last modify time
    private long lastModify;

    // one man's birth day
    private long birthDay;

    // male or female
    private String sex;

    // how tall
    private float height;

    // how weight
    private float weight;

    // current weight loss goal
    private float weightLossGaol;

    // weekly loss weight target
    private float weeklyLossWeight;

    private long addedTime;

    public Profile() {
        this.datetime = 0;
        this.lastModify = 0;
        this.birthDay = 0;
        this.sex = new String("null");
        this.height = 0;
        this.weight = 0;
        this.weightLossGaol = 0;
        this.weeklyLossWeight = 0;
    }

    public long getId() {return this.id;}
    public void setId(long id) {this.id = id;}

    public void setUserFBID(long userFBID){this.userID = userFBID;}
    public long getUserID(){return this.userID;}

    public long getDatetime() {return datetime;}
    public String getLocaleDatetime() {return String.format(Locale.getDefault(), "%tF  %<tR", new Date(datetime));}
    public void setDatetime(long datetime) {this.datetime = datetime;}

    public long getLastModify() {return lastModify;}
    public void setLastModify(long lastModify) {this.lastModify = lastModify;}

    public long getBirthDay() {return birthDay;}
    public void setBirthDay(long birthDay) {this.birthDay = birthDay;}

    public String getSex() {return sex;}
    public void setSex(String sex) {this.sex = sex;}

    public float getHeight() {return height;}
    public void setHeight(float height) {this.height = height;}

    public float getWeight() {return weight;}
    public void setWeight(float weight) {this.weight = weight;}

    public float getWeightLossGoal() {return weightLossGaol;}
    public void setWeightLossGoal(float weightLossGaol) {this.weightLossGaol = weightLossGaol;}

    public float getWeeklyLossWeight() {return weeklyLossWeight;}
    public void setWeeklyLossWeight(float weeklyLossWeight) {this.weeklyLossWeight = weeklyLossWeight;}

    public long getAddedTime(){return addedTime;}
    public void setAddedTime(long addedTime){this.addedTime = addedTime;}
}
