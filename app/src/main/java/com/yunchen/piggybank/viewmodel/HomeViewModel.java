package com.yunchen.piggybank.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.yunchen.piggybank.AppRepository;
import com.yunchen.piggybank.database.entity.Statement;

import java.util.List;

public class HomeViewModel extends AndroidViewModel {

    private AppRepository mAppRepository;


    public HomeViewModel(@NonNull Application application){
        super(application);
        mAppRepository = AppRepository.getInstance(application);
    }

    public LiveData<List<Statement>> getmTodayStatements(long now) {
        return mAppRepository.getTodayStatements(now);
    }

    public LiveData<Statement> getStatementById(int id){
        return mAppRepository.getStatementById(id);
    }

    public LiveData<Long> getMonthlyExpenses(){
        return mAppRepository.getMonthlyExpenses();
    }

    public LiveData<Long> getMonthlyBudget(){
        return mAppRepository.getMonthlyBudget();
    }

    public LiveData<Long> getMonthlySavings(){
        return mAppRepository.getMonthlySavings();
    }

    public void updateWaitingItems(){
        mAppRepository.uploadWaitingItems();
    }
}
