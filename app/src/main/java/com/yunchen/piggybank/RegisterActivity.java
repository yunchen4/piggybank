package com.yunchen.piggybank;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.yunchen.piggybank.utils.EncryptionUtils;
import com.yunchen.piggybank.utils.WebServiceUtils;

import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    private EditText mUsername;
    private EditText mPwd;
    private EditText mEmail;
    private RequestQueue requestQueue;
    private SharedPreferences.Editor editor;
    private SharedPreferences appInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mUsername = findViewById(R.id.register_username_edit);
        mPwd = findViewById(R.id.register_pwd_edit);
        mEmail  = findViewById(R.id.register_email_edit);
        EditText mConfirmPwd = findViewById(R.id.register_confirm_pwd_edit);
        Button mRegisterBtn = findViewById(R.id.register_button);
        appInfo = getSharedPreferences("appInfo",MODE_PRIVATE);
        editor = appInfo.edit();
        requestQueue = Volley.newRequestQueue(this);
        Objects.requireNonNull(getSupportActionBar()).hide();


        mRegisterBtn.setOnClickListener(view -> {
            String username = mUsername.getText().toString();
            RegisterVolleyUtil volleyUtil = new RegisterVolleyUtil();
            String pwd = mPwd.getText().toString();
            String confirmPwd = mConfirmPwd.getText().toString();
            String email = mEmail.getText().toString();
            volleyUtil.checkUsername(username, getApplicationContext(), isUserExist -> {
                if(!isUserExist && pwd.length()>=6 && username.length()!=0){
                    String encryptedPwd = EncryptionUtils.getEncryptedPwd(pwd);
                    register(username, encryptedPwd, email);
                    editor.putString("username", username);
                    if ((appInfo.getInt("localId", -1) == -1) && (appInfo.getInt("localPlanId", -1) == -1)) {
                        editor.putInt("localId", -1);
                        editor.putInt("localPlanId", -1);
                    }
                    editor.commit();
                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                    startActivity(intent);
                }else{
                    Toast.makeText(RegisterActivity.this, "Username has already existed!", Toast.LENGTH_SHORT).show();
                }
            });
            if(email.length()==0 || username.length()==0){
                Toast.makeText(this, "Please fill in all info!", Toast.LENGTH_SHORT).show();
            }
            if(pwd.length()<6){
                Toast.makeText(RegisterActivity.this, "Password has to be no less than 6 characters!", Toast.LENGTH_SHORT).show();
            }
            if(!confirmPwd.equals(pwd)){
                Toast.makeText(this, "Passwords are not consistent!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void register(String username, String pwd, String email){
        String requestUrl = WebServiceUtils.register(username,pwd,email);
        StringRequest request = new StringRequest(Request.Method.GET, requestUrl, response -> {
        }, error -> {
        });
        requestQueue.add(request);
    }

}

class RegisterVolleyUtil {
    private boolean isUserExist;
    void checkUsername(String username, Context context, final VolleyCallback callback){
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        String requestUrl = WebServiceUtils.isUserExist(username);
        JsonArrayRequest checkUserRequest = new JsonArrayRequest(Request.Method.GET, requestUrl, null,
                response -> {
                    isUserExist = response.length() != 0;
                    callback.onSuccess(isUserExist);
                }, error -> {
                });
        requestQueue.add(checkUserRequest);
    }
    public interface VolleyCallback {
        void onSuccess(boolean isUserExist);
    }
}
