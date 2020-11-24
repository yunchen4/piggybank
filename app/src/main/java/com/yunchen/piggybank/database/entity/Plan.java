package com.yunchen.piggybank.database.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.yunchen.piggybank.utils.DateUtils;

import org.json.JSONException;
import org.json.JSONObject;

@Entity(tableName = "Plan")
public class Plan implements Comparable<Plan>{

    public static final int IN_PROGRESS = 0;
    public static final int COMPLETED = 1;
    public static final int FAILED = 2;

    @PrimaryKey(autoGenerate = true)
    @NonNull
    private int id;

    @NonNull
    private long startDate;

    @NonNull
    private long endDate;

    @NonNull
    private long goalAmount;

    @NonNull
    private int currentState;

    @NonNull
    private long goalRemain;

    @NonNull
    private String punishment;

    @NonNull
    private int localId;

    @Ignore
    public Plan(int id,  @NonNull long endDate, long goalAmount, long remain, String punishment, int localId ) {
        this.id = id;
        this.startDate = DateUtils.getNormalizedUtcDateForToday();
        this.endDate = endDate;
        this.goalAmount = goalAmount;
        this.goalRemain = remain;
        this.currentState = IN_PROGRESS;
        this.punishment = punishment;
        this.localId = localId;
    }

    public Plan( @NonNull long endDate, long goalAmount, long goalRemain, String punishment, int localId) {
        this.startDate = DateUtils.getNormalizedUtcDateForToday();
        this.endDate = endDate;
        this.goalAmount = goalAmount;
        this.goalRemain = goalRemain;
        this.currentState = IN_PROGRESS;
        this.punishment = punishment;
        this.localId = localId;
    }

    public Plan(JSONObject o){
        try {
            localId=o.getInt("localId");
            startDate=o.getLong("startdate");
            endDate=o.getLong("enddate");
            goalAmount=o.getLong("amount");
            goalRemain=o.getLong("remain");
            currentState=o.getInt("currentState");
            punishment=o.getString("punishment");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @NonNull
    public long getStartDate() {
        return startDate;
    }

    @NonNull
    public long getEndDate() {
        return endDate;
    }

    @NonNull
    public long getGoalAmount() {
        return goalAmount;
    }

    public void setEndDate(@NonNull long endDate) {
        this.endDate = endDate;
    }

    public void setGoalAmount(@NonNull long goalAmount) {
        this.goalAmount = goalAmount;
    }

    public long getGoalRemain() {
        return goalRemain;
    }

    public void setGoalRemain(long goalRemain) {
        this.goalRemain = goalRemain;
    }

    public int getCurrentState() {
        return currentState;
    }

    public void setCurrentState(int currentState) {
        this.currentState = currentState;
    }

    @NonNull
    public String getPunishment() {
        return punishment;
    }

    public void setPunishment(@NonNull String punishment) {
        this.punishment = punishment;
    }

    public void setStartDate(@NonNull long startDate) {
        this.startDate = startDate;
    }

    public int getLocalId() {
        return localId;
    }

    public void setLocalId(int localId) {
        this.localId = localId;
    }

    @Override
    public int compareTo(Plan plan) {
        if(this.getCurrentState()==IN_PROGRESS){
            return -1;
//        }else if(this.getEndDate()>plan.getEndDate()){
//            return -1;
        }else{
            return 1;
        }
    }
}