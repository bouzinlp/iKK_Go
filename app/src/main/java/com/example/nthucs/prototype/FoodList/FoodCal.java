package com.example.nthucs.prototype.FoodList;

/**
 * Created by USER12345678 on 2016/7/19.
 */
public class FoodCal implements java.io.Serializable {

    private String index;
    private String category;
    private String chineseName;
    private String englishName;
    private int calorie;
    private int modifiedCalorie;

    public FoodCal(String index, String category, String chineseName, String englishName, String calorie, String modifiedCalorie) {
        this.index = index;
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
    public String toString() {
        return "FoodCal [index="+index+" category="+category+" chinese name="+chineseName+" english name="+englishName+" calorie="+calorie+" modified calorie="+modifiedCalorie+"]";
    }
}
