package com.yunchen.piggybank;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.yunchen.piggybank.database.converter.AmountConverter;
import com.yunchen.piggybank.database.entity.Statement;
import com.yunchen.piggybank.utils.DateUtils;
import com.yunchen.piggybank.viewmodel.HomeViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Collections;

import static android.view.View.GONE;


public class HomeFragment extends Fragment implements StatementListAdapter.ItemClickListener{

    private static final String ARGS_PAGE = "args_page";

    private HomeViewModel mHomeViewModel;
    private StatementListAdapter adapter;
    private TextView mTotalExpenseForThisMonth;
    private TextView mTotalBudgetForThisMonth;
    private TextView mTotalSavingsForThisMonth;
    private TextView mMonth;
    private RecyclerView mTodayStatementRecyclerView;
    private TextView mNoStatementsTv;

    private View rootView;

    public static HomeFragment newInstance(int page){
        Bundle args = new Bundle();
        args.putInt(ARGS_PAGE,page);
        HomeFragment fragment = new HomeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(rootView==null) {
            rootView = inflater.inflate(R.layout.fragment_home, container, false);
            mTotalExpenseForThisMonth = rootView.findViewById(R.id.home_monthly_expenses);
            mTotalBudgetForThisMonth = rootView.findViewById(R.id.home_monthly_budget);
            mTotalSavingsForThisMonth = rootView.findViewById(R.id.home_monthly_saving);
            mMonth = rootView.findViewById(R.id.home_card_month);
            FloatingActionButton mFab = rootView.findViewById(R.id.fab);
            mFab.setOnClickListener(view -> {
                Intent addStatementIntent = new Intent(getActivity(), AddStatementActivity.class);
                startActivity(addStatementIntent);
            });
            adapter = new StatementListAdapter(getContext(), this);
            mTodayStatementRecyclerView = rootView.findViewById(R.id.recycler_view_today_statements);
            mTodayStatementRecyclerView.setAdapter(adapter);
            mTodayStatementRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            mNoStatementsTv = rootView.findViewById(R.id.tv_no_today_statements);
        }
        return rootView;
    }

    private void setUpViewModel(){
        mHomeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        mHomeViewModel.getMonthlyExpenses().observe(this, aLong ->
                mTotalExpenseForThisMonth.setText(AmountConverter.toString(aLong)));
        mHomeViewModel.getMonthlyBudget().observe(this, aLong->{
            mTotalBudgetForThisMonth.setText(AmountConverter.toString(aLong));
        });
        mHomeViewModel.getMonthlySavings().observe(this, aLong->{
            mTotalSavingsForThisMonth.setText(AmountConverter.toString(aLong));
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUpViewModel();
    }

    @Override
    public void onItemClickListener(int itemId) {
        mHomeViewModel.getStatementById(itemId).observe(this, new Observer<Statement>() {
            @Override
            public void onChanged(Statement statement) {
                mHomeViewModel.getStatementById(itemId).removeObserver(this);
                if(statement!=null) {
                    Intent intent = new Intent(getActivity(), AddStatementActivity.class);
                    intent.putExtra(AddStatementActivity.EXTRA_STATEMENT_ID, itemId);
                    intent.putExtra(AddStatementActivity.STATEMENT_LOCAL_ID, statement.getLocalId());
                    intent.putExtra(AddStatementActivity.ENTRANCE_PAGE,0);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        long now = DateUtils.getNormalizedUtcDateForToday();
        mHomeViewModel.updateWaitingItems();
        mHomeViewModel.getmTodayStatements(now).observe(getViewLifecycleOwner(),
                statements -> {
                    Collections.sort(statements);
                    adapter.setmStatements(statements);
                    if(statements.size()==0){
                        mNoStatementsTv.setVisibility(View.VISIBLE);
                        mTodayStatementRecyclerView.setVisibility(GONE);
                    }else{
                        mNoStatementsTv.setVisibility(GONE);
                        mTodayStatementRecyclerView.setVisibility(View.VISIBLE);
                    }
                });
        mMonth.setText(DateUtils.getMonthName());

    }
}
