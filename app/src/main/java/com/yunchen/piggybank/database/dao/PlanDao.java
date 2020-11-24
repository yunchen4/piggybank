package com.yunchen.piggybank.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.yunchen.piggybank.database.entity.Plan;

import java.util.List;

@Dao
public interface PlanDao {

    @Insert
    void insertPlan(Plan plan);

    @Delete
    void deletePlan(Plan plan);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updatePlan(Plan plan);

    @Query("DELETE FROM `Plan`")
    void deleteAllPlans();

    @Query("SELECT * FROM `Plan` ORDER BY endDate")
    LiveData<List<Plan>> getAllPlans();

    @Query("SELECT * FROM `Plan` WHERE id=:id")
    LiveData<Plan> getPlanById(int id);

    @Query("SELECT * FROM `Plan` WHERE currentState = 0")
    LiveData<Plan> getPlanInProgress();

    @Query("SELECT * FROM `Plan` WHERE currentState = 0")
    Plan getPlanInProgressDirectly();

    @Query("SELECT * FROM `Plan` WHERE localId = (SELECT MAX(localId) FROM `Plan`)")
    LiveData<Plan> getTheLatestPlanDirectly();
}