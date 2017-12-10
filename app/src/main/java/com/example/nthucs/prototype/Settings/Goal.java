package com.example.nthucs.prototype.Settings;

/**
 * Created by SElab on 2017/12/10.
 */

public class Goal {
    private long id, userID;
    private int goalTime;
    private int goalStep;
    private int goalBurn;
    private int goalDist;

    public Goal() {
        this.goalTime = 30;
        this.goalStep = 8000;
        this.goalBurn = 2000;
        this.goalDist = 6000;
    }

    public Goal(int t, int s, int b, int d) {
        this.goalTime = t;
        this.goalStep = s;
        this.goalBurn = b;
        this.goalDist = d;
    }

    public void setId(long id) { this.id = id; }
    public long getId() { return this.id; }

    public void setUserID(long userID) { this.userID = userID; }
    public long getUserID() { return this.userID; }

    public void setGoalTime(int time) { this.goalTime = time; }
    public int getGoalTime() { return this.goalTime; }

    public void setGoalStep(int step) { this.goalStep = step; }
    public int getGoalStep() { return this.goalStep; }

    public void setGoalBurn(int burn) { this.goalBurn = burn; }
    public int getGoalBurn() { return this.goalBurn; }

    public void setGoalDist(int dist) { this.goalDist = dist; }
    public int getGoalDist() { return this.goalDist; }
}
