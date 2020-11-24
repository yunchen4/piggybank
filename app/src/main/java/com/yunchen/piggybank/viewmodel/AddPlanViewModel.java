package com.yunchen.piggybank.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.yunchen.piggybank.AppRepository;
import com.yunchen.piggybank.database.entity.Plan;

public class AddPlanViewModel extends AndroidViewModel {

    private AppRepository mRepository;

    public AddPlanViewModel(@NonNull Application application) {
        super(application);
        mRepository = AppRepository.getInstance(application);
    }


    public LiveData<Plan> getPlanById(int id){
        return mRepository.getPlanById(id);
    }

    public void insertPlan(Plan plan){
        mRepository.insertPlan(plan);
    }

    public void updatePlan(Plan plan){
        mRepository.updatePlan(plan);
    }

    public void deletePlan(Plan plan){
        mRepository.deletePlan(plan);
    }

}
