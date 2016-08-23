package com.example.nthucs.prototype.SportList;

/**
 * Created by user on 2016/8/23.
 */
public class SportCal implements java.io.Serializable {

    private long id;

    private String sportName;

    private float consumeUnit, consumeHalfHouWith40, consumeHalfHouWith50, consumeHalfHouWith60, consumeHalfHouWith70;

    public SportCal() {
    }

    public long getId() {return this.id;}
    public void setId(long id) {this.id = id;}

    public String getSportName() {return this.sportName;}
    public void setSportName(String sportName) {this.sportName = sportName;}

    public float getConsumeHalfHouWith40() {return this.consumeHalfHouWith40;}
    public void setConsumeHalfHouWith40(float consumeHalfHouWith40) {this.consumeHalfHouWith40 = consumeHalfHouWith40;}

    public float getConsumeHalfHouWith50() {return this.consumeHalfHouWith50;}
    public void setConsumeHalfHouWith50(float consumeHalfHouWith50) {this.consumeHalfHouWith50 = consumeHalfHouWith50;}

    public float getConsumeHalfHouWith60() {return this.consumeHalfHouWith60;}
    public void setConsumeHalfHouWith60(float consumeHalfHouWith60) {this.consumeHalfHouWith60 = consumeHalfHouWith60;}

    public float getConsumeHalfHouWith70() {return this.consumeHalfHouWith70;}
    public void setConsumeHalfHouWith70(float consumeHalfHouWith70) {this.consumeHalfHouWith70 = consumeHalfHouWith70;}
}
