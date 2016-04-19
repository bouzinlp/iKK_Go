package com.example.nthucs.prototype;

import java.util.Date;
import java.util.Locale;

public class Food implements java.io.Serializable {

    private long id;
    private float calorie;
    private float portions;
    private float grams;
    private String title;
    private String content;

    private boolean selected;

    public Food() {
        this.title = new String();
        this.content = new String();
    }

    public Food(long id, float calorie, float portions, float grams, String title, String content) {
        this.id = id;
        this.calorie = calorie;
        this.portions = portions;
        this.grams = grams;
        this.title = title;
        this.content = content;
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

    public boolean isSelected() {
        return selected;
    }
    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}

