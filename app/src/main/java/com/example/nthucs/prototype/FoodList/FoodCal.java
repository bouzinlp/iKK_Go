package com.example.nthucs.prototype.FoodList;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by USER12345678 on 2016/7/19.
 */
public class FoodCal implements Parcelable {

    private String index;
    private String category;
    private String chineseName;
    private String englishName;
    private int calorie;
    private int modifiedCalorie;

    public static final Parcelable.Creator<FoodCal> CREATOR = new Creator(){
        @Override
        public FoodCal createFromParcel(Parcel source) {
            FoodCal foodCal = new FoodCal(source.readString(), source.readString(), source.readString(), source.readString(), source.readString(), source.readString());
            return foodCal;
        }

        @Override
        public FoodCal[] newArray(int size) {
            return new FoodCal[size];
        }
    };

    public FoodCal(String index, String category, String chineseName, String englishName, String calorie, String modifiedCalorie) {
        this.index = index;
        this.category = category;
        this.chineseName = chineseName;
        this.englishName = englishName;
        this.calorie = Integer.parseInt(calorie);
        this.modifiedCalorie = Integer.parseInt(modifiedCalorie);
    }

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
