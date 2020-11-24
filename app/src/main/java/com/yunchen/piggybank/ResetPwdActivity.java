package com.yunchen.piggybank;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

public class ResetPwdActivity extends AppCompatActivity {

    private String username;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pwd);
        Objects.requireNonNull(getSupportActionBar()).hide();

        if(findViewById(R.id.reset_pwd_fragment_container)!=null){
            if(savedInstanceState!=null){
                return;
            }

            VerificationFragment verificationFragment = new VerificationFragment();

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.reset_pwd_fragment_container, verificationFragment)
                    .commit();
        }

    }

    public String getUsername(){
        return username;
    }

    public void setUsername(String username){
        this.username = username;
    }
}
