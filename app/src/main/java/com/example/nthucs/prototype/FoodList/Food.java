package com.example.nthucs.prototype.FoodList;

import android.net.Uri;

import java.util.Date;
import java.util.Locale;

public class Food implements java.io.Serializable {

    private long id;
    // food's information about calorie, portions, grams
    private float calorie;
    private float portions;
    private float grams;

    // food's name
    private String title;

    // food's description
    private String content;

    // pic' file name
    private String fileName;

    // whether this food is selected
    private boolean selected;

    // uri string with take photo from library
    private String picUriString;

    // boolean value to identify two photo capture way
    private boolean takeFromCamera;

    // food list's establish time
    private long datetime;

    public Food() {
        this.title = new String();
        this.content = new String();
        this.fileName = new String();
    }

    public Food(String title, String fileName, boolean takeFromCamera) {
        this.title = title;
        this.fileName = fileName;
        this.takeFromCamera = takeFromCamera;
    }

    public Food(String title, String fileName, String picUriString, boolean takeFromCamera) {
        this.title = title;
        this.fileName = fileName;
        this.picUriString = picUriString;
        this.takeFromCamera = takeFromCamera;
    }

    public Food(long id, float calorie, float portions, float grams, String title, String content, String fileName) {
        this.id = id;
        this.calorie = calorie;
        this.portions = portions;
        this.grams = grams;
        this.title = title;
        this.content = content;
        this.fileName = fileName;
    }

    public long getId() {return this.id;}
    public void setId(long id) {this.id = id;}

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }

    public float getCalorie() {return this.calorie;}
    public void setCalorie(float calorie) {this.calorie = calorie;}

    public float getPortions() {return this.portions;}
    public void setPortions(float portions) {this.portions = portions;}

    public float getGrams() {return this.grams;}
    public void setGrams(float grams) {this.grams = grams;}

    public String getFileName() {return fileName;}
    public void setFileName(String fileName) {this.fileName = fileName;}

    public String getPicUriString() {return picUriString;}
    public void setPicUriString(String picUriString) {this.picUriString = picUriString;}

    public boolean isSelected() {return selected;}
    public void setSelected(boolean selected) {this.selected = selected;}

    public boolean isTakeFromCamera() {return takeFromCamera;}
    public void setTakeFromCamera(boolean takeFromCamera) {this.takeFromCamera = takeFromCamera;}

    public String getLocaleDatetime() {return String.format(Locale.getDefault(), "%tF  %<tR", new Date(datetime));}
    public void setDatetime(long datetime) {
        this.datetime = datetime;
    }
}

