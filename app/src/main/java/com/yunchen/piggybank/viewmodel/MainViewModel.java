package com.yunchen.piggybank.viewmodel;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.yunchen.piggybank.AppRepository;
import com.yunchen.piggybank.database.entity.Plan;

public class MainViewModel extends AndroidViewModel {

    private AppRepository mRepository;
    private SharedPreferences.Editor editor;

    public MainViewModel(@NonNull Application application) {
        super(application);
        mRepository = AppRepository.getInstance(application);
        editor = application.getSharedPreferences("appInfo",Context.MODE_PRIVATE).edit();
    }


    public void refreshPlanByDate(long now){
        mRepository.refreshPlanByDate(now);
    }

    public LiveData<Plan> getTheLatestPlan(){
        return mRepository.getTheLatestPlan();
    }

    public void updateWaitingItems(){
        mRepository.uploadWaitingItems();
    }

    public void deleteEverything(){
        mRepository.deleteEverything();
    }
}
