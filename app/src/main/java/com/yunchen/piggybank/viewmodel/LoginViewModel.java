package com.yunchen.piggybank.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.yunchen.piggybank.AppRepository;

public class LoginViewModel extends AndroidViewModel {

    private AppRepository mRepository;

    public LoginViewModel(@NonNull Application application) {
        super(application);
        mRepository = AppRepository.getInstance(application);
    }

    public void syncWithRemoteDb(String username){
        mRepository.recoverEverythingFromRemoteDb(username);
    }
}
