package com.yunchen.piggybank;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.yunchen.piggybank.utils.EncryptionUtils;
import com.yunchen.piggybank.utils.WebServiceUtils;

public class ResetPwdFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_reset_pwd, container, false);
        Button mResetBtn = rootView.findViewById(R.id.reset_pwd_button);
        EditText mPwdEt = rootView.findViewById(R.id.reset_new_pwd);
        EditText mConfirmPwdEt = rootView.findViewById(R.id.reset_confirm_pwd);
        String username = ((ResetPwdActivity)getActivity()).getUsername();
        mResetBtn.setOnClickListener(view -> {
            String newPwd = mPwdEt.getText().toString();
            String confirmPwd = mConfirmPwdEt.getText().toString();
            if(newPwd.equals(confirmPwd)) {
                resetPwd(username, newPwd);
            }else {
                Toast.makeText(getContext(), "Passwords are not consistent!", Toast.LENGTH_SHORT).show();
            }
        });
        return rootView;
    }

    private void resetPwd(String username, String newPwd){
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        String encryptedPwd = EncryptionUtils.getEncryptedPwd(newPwd);
        String url = WebServiceUtils.resetPwd(username,encryptedPwd);
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET,
                url, null,
                response -> {
                    Toast.makeText(getContext(), "Reset password successfully!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getActivity(),LoginActivity.class);
                    startActivity(intent);
                }, error -> {
        });
        requestQueue.add(request);
    }
}
