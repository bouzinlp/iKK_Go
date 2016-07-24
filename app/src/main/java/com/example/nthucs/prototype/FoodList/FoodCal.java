package com.example.nthucs.prototype.FoodList;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by USER12345678 on 2016/7/19.
 */
public class FoodCal implements Parcelable {

    private long id;

    // food's index string
    private String index;

    // food's category
    private String category;

    // food in chinese
    private String chineseName;

    // food in english
    private String englishName;

    // calorie in the food, assume portion = 1.0
    private int calorie;

    // modified calorie
    private int modifiedCalorie;

    public static final Parcelable.Creator<FoodCal> CREATOR = new Creator(){
        @Override
        public FoodCal createFromParcel(Parcel source) {
            FoodCal foodCal = new FoodCal();
            foodCal.setIdx(source.readString());
            foodCal.setCategory(source.readString());
            foodCal.setChineseName(source.readString());
            foodCal.setEnglishName(source.readString());
            foodCal.setCalorie(source.readInt());
            foodCal.setModifiedCalorie(source.readInt());
            return foodCal;
        }

        @Override
        public FoodCal[] newArray(int size) {
            return new FoodCal[size];
        }
    };

    public FoodCal() {
    }

    public long getId() {return this.id;}
    public void setId(long id) {this.id = id;}

    public String getIdx() {return index;}
    public void setIdx(String index) {this.index = index;}

    public String getCategory() {return category;}
    public void setCategory(String category) {this.category = category;}

    public String getChineseName() {return chineseName;}
    public void setChineseName(String chineseName) {this.chineseName = chineseName;}

    public String getEnglishName() {return englishName;}
    public void setEnglishName(String englishName) {this.englishName = englishName;}

    public int getCalorie() {return calorie;}
    public void setCalorie(int calorie) {this.calorie = calorie;}

    public int getModifiedCalorie() {return modifiedCalorie;}
    public void setModifiedCalorie(int modifiedCalorie) {this.modifiedCalorie = modifiedCalorie;}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(index);
        out.writeString(category);
        out.writeString(chineseName);
        out.writeString(englishName);
        out.writeInt(calorie);
        out.writeInt(modifiedCalorie);
    }

    @Override
    public String toString() {
        return "FoodCal [index="+index+" category="+category+" chinese name="+chineseName+" english name="+englishName+" calorie="+calorie+" modified calorie="+modifiedCalorie+"]";
    }
}
