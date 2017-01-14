package com.example.nthucs.prototype.Utility;

/**
 * Created by user on 2017/1/12.
 */

public class FitnessActivity {

    public String activityName;
    public float activityExpenditure;
    public long activityTime,activityTimeStamp;

    public FitnessActivity(String activityName,long activityTime,long activityTimeStamp){
        this.activityName = activityName;
        this.activityTime = activityTime;
        this.activityTimeStamp = activityTimeStamp;

    }

    public String getActivityName(){return activityName;}
    public long getActivityTime(){return activityTime;}
    public long getActivityTimeStamp(){return activityTimeStamp;}
    public void setActivityExpenditure(float expenditure){this.activityExpenditure = expenditure;}
}
