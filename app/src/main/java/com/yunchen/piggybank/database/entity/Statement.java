package com.yunchen.piggybank.database.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import org.json.JSONException;
import org.json.JSONObject;

@Entity(tableName = "Statement")
public class Statement implements Comparable<Statement> {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    private int id;
    @NonNull
    private String type;
    @NonNull
    private String primaryCategory;
    @NonNull
    private String secondaryCategory;
    @NonNull
    private long date;

    private String memo;
    @NonNull
    private long amount;

    @NonNull
    private int localId;

    @Ignore
    public Statement(@NonNull long amount, @NonNull String type, @NonNull String primaryCategory, @NonNull
            String secondaryCategory,long date, String memo, int localId) {
        this.type = type;
        this.primaryCategory = primaryCategory;
        this.secondaryCategory = secondaryCategory;
        this.memo = memo;
        this.date = date;
        this.amount = amount;
        this.localId = localId;
    }

    public Statement(int id, long amount, @NonNull String type, @NonNull String primaryCategory,
                     @NonNull String secondaryCategory, @NonNull long date, String memo, int localId) {
        this.id = id;
        this.type = type;
        this.primaryCategory = primaryCategory;
        this.secondaryCategory = secondaryCategory;
        this.date = date;
        this.memo = memo;
        this.amount = amount;
        this.localId = localId;
    }

    public Statement(JSONObject o){
        try {
            localId = o.getInt("localId");
            type = o.getString("type");
            primaryCategory = o.getString("primaryCategory");
            secondaryCategory = o.getString("secondaryCategory");
            date = o.getLong("date");
            if(o.getString("memo").equals("null")){
                memo = null;
            }else {
                memo = o.getString("memo");
            }
            amount = o.getLong("amount");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @NonNull
    public int getId() {
        return id;
    }

    @NonNull
    public String getType() {
        return type;
    }

    @NonNull
    public String getPrimaryCategory() {
        return primaryCategory;
    }

    @NonNull
    public String getSecondaryCategory() {
        return secondaryCategory;
    }

    public void setSecondaryCategory(@NonNull String secondaryCategory) {
        this.secondaryCategory = secondaryCategory;
    }

    @NonNull
    public long getDate() {
        return date;
    }

    public String getMemo() {
        return memo;
    }

    @NonNull
    public long getAmount() {
        return amount;
    }

    public void setId(@NonNull int id) {
        this.id = id;
    }

    public void setType(@NonNull String type) {
        this.type = type;
    }

    public void setPrimaryCategory(@NonNull String primaryCategory) {
        this.primaryCategory = primaryCategory;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public int getLocalId() {
        return localId;
    }

    public void setLocalId(int localId) {
        this.localId = localId;
    }

    @Override
    public int compareTo(Statement o) {
        if(this.getDate()>o.getDate()){
            return -1;
        }else if(this.getDate()<o.getDate()){
            return 1;
        }else{
            return o.getLocalId()-this.getLocalId();
        }
    }
}
