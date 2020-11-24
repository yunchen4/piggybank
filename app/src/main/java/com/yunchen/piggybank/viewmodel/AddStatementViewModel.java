package com.yunchen.piggybank.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.yunchen.piggybank.AppRepository;
import com.yunchen.piggybank.database.entity.Statement;

public class AddStatementViewModel extends AndroidViewModel {

    private AppRepository mRepository;

    public AddStatementViewModel(@NonNull Application application) {
        super(application);
        mRepository = AppRepository.getInstance(application);
    }

    public LiveData<Statement> getStatementById(int id){
        return mRepository.getStatementById(id);
    }

    public void updateStatement(Statement statement){
        mRepository.updateStatement(statement);
    }

    public void insertStatement(Statement statement){
        mRepository.insertStatement(statement);
    }

    public void deleteStatement(Statement statement){
        mRepository.deleteStatement(statement);
    }

    public void refreshPlanInProgress(int requestCode, long oldAmount, long newAmount, long staDate) {
        mRepository.refreshPlanInProgressByStatement(requestCode,oldAmount,newAmount,staDate);
    }
}