package com.yunchen.piggybank;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.yunchen.piggybank.database.entity.Plan;
import com.yunchen.piggybank.utils.DateUtils;
import com.yunchen.piggybank.viewmodel.PlansViewModel;

import java.util.Collections;

import static android.view.View.GONE;

public class PlansFragment extends Fragment implements PlanListAdapter.ItemClickListener{

    private static final String ARGS_PAGE = "args_page";

    private static final int NEW_PLAN_REQUEST_CODE = 0;
    public static final int UPDATE_PLAN_REQUEST_CODE = 1;

    private PlansViewModel mPlansViewModel;
    private PlanListAdapter adapter;
    private SharedPreferences preference;
    private Button mNewPlanButton;
    private RecyclerView mRecyclerView;
    private TextView mNoPlansTv;

    static PlansFragment newInstance(int page){
        Bundle args = new Bundle();
        args.putInt(ARGS_PAGE,page);
        PlansFragment fragment = new PlansFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_plans, container, false);
        mNoPlansTv = rootView.findViewById(R.id.tv_no_plans);
        mRecyclerView = rootView.findViewById(R.id.recyclerview_plans);
        adapter = new PlanListAdapter(getContext(), this);
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mNewPlanButton = rootView.findViewById(R.id.new_plan_button);
        mNewPlanButton.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), AddPlanActivity.class);
            startActivity(intent);
        });
        preference = requireActivity().getSharedPreferences("appInfo", Context.MODE_PRIVATE);
        if(preference.getBoolean("isInPlan",true)){
            mNewPlanButton.setVisibility(View.GONE);
        }else{
            mNewPlanButton.setVisibility(View.VISIBLE);
        }
        setUpViewModel();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mPlansViewModel.refreshPlanByDate(DateUtils.getNormalizedUtcDateForToday());
        mPlansViewModel.updateWaitingItems();
        if(preference.getBoolean("isInPlan",true)){
            mNewPlanButton.setVisibility(View.GONE);
        }else{
            mNewPlanButton.setVisibility(View.VISIBLE);
        }
    }

    private void setUpViewModel(){
        mPlansViewModel = new ViewModelProvider(this).get(PlansViewModel.class);
        mPlansViewModel.getmAllPlans().observe(getViewLifecycleOwner(), plans -> {
            Collections.sort(plans);
            adapter.setmPlans(plans);
            if(plans.size()==0){
                mRecyclerView.setVisibility(GONE);
                mNoPlansTv.setVisibility(View.VISIBLE);
            }else{
                mNoPlansTv.setVisibility(GONE);
                mRecyclerView.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onItemClickListener(int itemId) {
        mPlansViewModel.getPlanById(itemId).observe(this, new Observer<Plan>() {
            @Override
            public void onChanged(Plan plan) {
                mPlansViewModel.getPlanById(itemId).removeObserver(this);
                if(plan!=null) {
                        Intent intent = new Intent(getContext(), AddPlanActivity.class);
                        intent.putExtra(AddPlanActivity.EXTRA_PLAN_ID, itemId);
                        intent.putExtra(AddPlanActivity.PLAN_LOCAL_ID, plan.getLocalId());
                        intent.putExtra(AddPlanActivity.PLAN_CURRENT_STATE, plan.getCurrentState());
                        startActivity(intent);
                }
            }
        });
    }
}