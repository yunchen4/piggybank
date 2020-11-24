package com.yunchen.piggybank.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.yunchen.piggybank.AppRepository;
import com.yunchen.piggybank.database.entity.Statement;

import java.util.List;

public class StatementsViewModel extends AndroidViewModel {

    private LiveData<List<Statement>> mAllStatements;
    private AppRepository mAppRepository;

    public StatementsViewModel(@NonNull Application application) {
        super(application);
        mAppRepository = AppRepository.getInstance(application);
        mAllStatements = mAppRepository.getmAllStatements();
    }


    public LiveData<List<Statement>> getmAllStatements() {
        return mAllStatements;
    }

    public LiveData<Statement> getStatementById(int id){
        return mAppRepository.getStatementById(id);
    }

    public LiveData<List<Statement>> getStatementsByType(String type){
        if(type.equals("Choose Type")){
            return mAllStatements;
        }else{
            return mAppRepository.getStatementsByType(type);
        }
    }

    public LiveData<Long> getTotalExpenses(){
        return mAppRepository.getTotalExpenses();
    }

    public LiveData<Long> getTotalSavings(){
        return mAppRepository.getTotalSavings();
    }


    public void updateWaitingItems(){
        mAppRepository.uploadWaitingItems();
    }
}
