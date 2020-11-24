package com.yunchen.piggybank.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.yunchen.piggybank.AppRepository;
import com.yunchen.piggybank.database.entity.Plan;

import java.util.List;

public class PlansViewModel extends AndroidViewModel {

    private AppRepository mRepository;
    private LiveData<List<Plan>> mAllPlans;

    public PlansViewModel(@NonNull Application application) {
        super(application);
        mRepository = AppRepository.getInstance(application);
        mAllPlans = mRepository.getmAllPlans();
    }

    public LiveData<List<Plan>> getmAllPlans(){
        return mAllPlans;
    }

    public LiveData<Plan> getPlanById(int id){
        return mRepository.getPlanById(id);
    }

    public void refreshPlanByDate(long now){
        mRepository.refreshPlanByDate(now);
    }

    public void updateWaitingItems(){
        mRepository.uploadWaitingItems();
    }
}
