package com.example.nthucs.prototype.FoodList;

import android.os.CountDownTimer;
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

    // food's protein
    private int protein;

    // food's fat
    private int fat;

    // food's carbohydrates
    private int carbohydrates;

    // food's dietary fiber
    private int dietaryFiber;

    // food's sodium
    private int sodium;

    // food's calcium
    private int calcium;

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
            foodCal.setProtein(source.readInt());
            foodCal.setFat(source.readInt());
            foodCal.setCarbohydrates(source.readInt());
            foodCal.setDietaryFiber(source.readInt());
            foodCal.setSodium(source.readInt());
            foodCal.setCalcium(source.readInt());
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

    public int getProtein(){return this.protein;}
    public void setProtein(int protein) {this.protein = protein;}

    public int getFat(){return this.fat;}
    public void setFat(int fat){this.fat = fat;}

    public int getCarbohydrates(){return this.carbohydrates;}
    public void setCarbohydrates(int carbohydrates){this.carbohydrates = carbohydrates;}

    public int getDietaryFiber(){return this.dietaryFiber;}
    public void setDietaryFiber(int dietaryFiber){this.dietaryFiber = dietaryFiber;}

    public int getSodium(){return this.sodium;}
    public void setSodium(int sodium){this.sodium = sodium;}

    public int getCalcium(){return this.calcium;}
    public void setCalcium(int calcium){this.calcium = calcium;}

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
        out.writeInt(protein);
        out.writeInt(fat);
        out.writeInt(carbohydrates);
        out.writeInt(dietaryFiber);
        out.writeInt(sodium);
        out.writeInt(calcium);
        out.writeInt(calorie);
        out.writeInt(modifiedCalorie);
    }

    @Override
    public String toString() {
        return "FoodCal [index="+index+" category="+category+" chinese name="+chineseName+" english name="+englishName+" protein="+protein+" fat="+fat+" carbohydrates="+carbohydrates+" dietaryFiber="+dietaryFiber+" sodium="+sodium+" calcium="+calcium+" calorie="+calorie+" modified calorie="+modifiedCalorie+"]";
    }
}
