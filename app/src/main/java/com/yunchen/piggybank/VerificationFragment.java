package com.yunchen.piggybank;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.yunchen.piggybank.utils.WebServiceUtils;

import org.json.JSONException;
import org.json.JSONObject;

public class VerificationFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_verification,container,false);
        ImageButton mConfirmBtn = rootView.findViewById(R.id.verification_button);
        EditText mUsernameEt = rootView.findViewById(R.id.verification_username_edit);
        EditText mEmailEt = rootView.findViewById(R.id.verification_email_edit);
        mConfirmBtn.setOnClickListener(view -> verifyInfo(mUsernameEt.getText().toString(), mEmailEt.getText().toString()));
        return rootView;
    }

    private void verifyInfo(String username,String email){
        String url = WebServiceUtils.getPwd(username);
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET,
                url, null,
                response -> {
                    try {
                        JSONObject o = response.getJSONObject(0);
                        boolean isVerified = o.getString("email").equals(email);
                        if(isVerified){
                            ((ResetPwdActivity)getActivity()).setUsername(username);
                            Fragment resetPwdFragment = new ResetPwdFragment();
                            getActivity().getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.reset_pwd_fragment_container, resetPwdFragment)
                                    .addToBackStack(null)
                                    .commit();
                        }else{
                            Toast.makeText(getContext(), "Info wrong!", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> {
        });
        requestQueue.add(request);
        if(username.length()==0||email.length()==0){
            Toast.makeText(getContext(), "Please fill in all info!", Toast.LENGTH_SHORT).show();
        }
    }
}
