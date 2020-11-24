package com.yunchen.piggybank;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.yunchen.piggybank.utils.EncryptionUtils;
import com.yunchen.piggybank.utils.WebServiceUtils;
import com.yunchen.piggybank.viewmodel.LoginViewModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private SharedPreferences preferences;
    private EditText mUsername;
    private EditText mPwd;

    private LoginViewModel viewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        preferences = getSharedPreferences("appInfo", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        Button mLoginBtn = findViewById(R.id.login_button);
        TextView mRegisterEntry = findViewById(R.id.register_entry);
        TextView mSkipEntry = findViewById(R.id.skip_entry);
        TextView mForgetPwdEntry = findViewById(R.id.forget_pwd_entry);
        mUsername = findViewById(R.id.username_edit);
        mPwd = findViewById(R.id.pwd_edit);
        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        Objects.requireNonNull(getSupportActionBar()).hide();

        mSkipEntry.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        mRegisterEntry.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        mForgetPwdEntry.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);

        mSkipEntry.setOnClickListener(view -> {
            if(preferences.getInt("localId",-1)!=-1) {
                editor.putInt("localId", -1);
                editor.putInt("localPlanId", -1);
                editor.commit();
            }
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
        });

        mForgetPwdEntry.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this,ResetPwdActivity.class);
            startActivityForResult(intent,0);
        });

        mRegisterEntry.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });


        mLoginBtn.setOnClickListener(view -> {
            String username = mUsername.getText().toString();
            String pwd = mPwd.getText().toString();
            LoginVolleyUtil loginVolleyUtil = new LoginVolleyUtil();
            loginVolleyUtil.checkPwd(username, pwd, LoginActivity.this,
                    (localId, isVerified, localPlanId) -> {
                        if(isVerified){
                            editor.putInt("localId",localId);
                            editor.putInt("localPlanId",localPlanId);
                            if(localPlanId>-1){
                                editor.putBoolean("isInPlan",true);
                            }
                            editor.putString("username",username);
                            editor.commit();
                            viewModel.syncWithRemoteDb(username);
                            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                            intent.putExtra("login",true);
                            startActivity(intent);
                        }else {
                            Toast.makeText(LoginActivity.this, "Username or password is incorrect!", Toast.LENGTH_SHORT).show();
                        }
                    });
            if(username.length()==0 || pwd.length()==0) Toast.makeText(this, "Please fill in all info!", Toast.LENGTH_SHORT).show();
        });
    }
}

class LoginVolleyUtil {
    private boolean isVerified;
    private int localId;
    private int localPlanId;
    void checkPwd(String username, String pwd, Context context, final VolleyCallback callback){
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        String requestUrl = WebServiceUtils.getPwd(username);
        JsonArrayRequest getPwdRequest = new JsonArrayRequest(Request.Method.GET, requestUrl, null,
                response -> {
                    if(response!=null){
                        try {
                            JSONObject o = response.getJSONObject(0);
                            String encryptedPwd = EncryptionUtils.getEncryptedPwd(pwd);
                            isVerified = encryptedPwd.equals(o.getString("password"));
                            localId = o.getInt("localId");
                            localPlanId=o.getInt("localPlanId");
                            callback.onSuccess(localId, isVerified,localPlanId);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, error -> {
                });
        requestQueue.add(getPwdRequest);
    }

    public interface VolleyCallback {
        void onSuccess(int localId, boolean isOK, int localPlanId);
    }
}

