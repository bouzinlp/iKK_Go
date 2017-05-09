package com.example.nthucs.prototype.Settings;

import java.util.Date;
import java.util.Locale;

/**
 * Created by Debbie Chiou on 2017/1/21.
 */

public class Health implements java.io.Serializable {
    private long id;
    private long userID;
    // health's establish time
    private long datetime;

    // health's last modify time
    private long lastModify;

    // health's temperature, drunk water, and blood pressure
    private float temperature;
    private int drunkWater;
    private float systolicBloodPressure;
    private float diastolicBloodPressure;
    private float pulse;
    private float activityFactor;

    public Health() {
        this.datetime = 0;
        this.lastModify = 0;
        this.temperature = 0;
        this.drunkWater = 0;
        this.systolicBloodPressure = 0;
        this.diastolicBloodPressure = 0;
        this.pulse = 0;
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

    public float getTemperature() {return this.temperature;}
    public void setTemperature(float temperature) {this.temperature = temperature;}

    public int getDrunkWater() {return this.drunkWater;}
    public void setDrunkWater(long drunkWater) {this.drunkWater = (int)drunkWater;}

    public float getSystolicBloodPressure() {return this.systolicBloodPressure;}
    public void setSystolicBloodPressure(float systolicBloodPressure) {this.systolicBloodPressure = systolicBloodPressure;}

    public float getDiastolicBloodPressure() {return this.diastolicBloodPressure;}
    public void setDiastolicBloodPressure(float diastolicBloodPressure) {this.diastolicBloodPressure = diastolicBloodPressure;}

    public float getPulse() {return this.pulse;}
    public void setPulse(float pulse) {this.pulse = pulse;}

    public float getActivityFactor() {return  this.activityFactor;}
    public void setActivityFactor(float activityFactor) {this.activityFactor = activityFactor;}
}
