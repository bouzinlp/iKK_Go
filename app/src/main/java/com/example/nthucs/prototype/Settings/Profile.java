package com.example.nthucs.prototype.Settings;

import java.util.Date;
import java.util.Locale;

/**
 * Created by USER12345678 on 2016/7/24.
 */
public class Profile implements java.io.Serializable {

    private long id;

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

    public Profile() {

    }

    public long getId() {return this.id;}
    public void setId(long id) {this.id = id;}

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
}
