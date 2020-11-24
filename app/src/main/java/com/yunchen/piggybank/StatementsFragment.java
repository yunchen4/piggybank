package com.yunchen.piggybank;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
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
import com.yunchen.piggybank.viewmodel.StatementsViewModel;

import java.util.Collections;

import static android.view.View.GONE;

public class StatementsFragment extends Fragment implements StatementListAdapter.ItemClickListener{

    private static final String ARGS_PAGE = "args_page";

    private StatementsViewModel mStatementsViewModel;
    private StatementListAdapter adapter;
    private TextView mTotalExpenses;
    private TextView mTotalSavings;
    private RecyclerView mRecyclerView;
    private TextView mNoAllStatementsTv;

    static StatementsFragment newInstance(int page){
        Bundle args = new Bundle();
        args.putInt(ARGS_PAGE,page);
        StatementsFragment fragment = new StatementsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_statements, container, false);
        mNoAllStatementsTv = rootView.findViewById(R.id.tv_no_all_statements);
        mRecyclerView = rootView.findViewById(R.id.recyclerview_statements);
        adapter = new StatementListAdapter(getContext(), this);
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        Spinner mTypeSpinner = rootView.findViewById(R.id.sort_type);
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.all_types_array, R.layout.spinner_sort_type);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mTypeSpinner.setAdapter(spinnerAdapter);
        mTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String type = (String) adapterView.getItemAtPosition(i);
                mStatementsViewModel.getStatementsByType(type).observe(getViewLifecycleOwner(),
                        statements -> {
                    Collections.sort(statements);
                    adapter.setmStatements(statements);
                });
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        mTotalExpenses = rootView.findViewById(R.id.total_expenses);
        mTotalSavings = rootView.findViewById(R.id.total_savings);
        setUpViewModel();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mStatementsViewModel.updateWaitingItems();
    }

    private void setUpViewModel(){
        mStatementsViewModel = new ViewModelProvider(this).get(StatementsViewModel.class);
        mStatementsViewModel.getmAllStatements().observe(getViewLifecycleOwner(),
                statements -> {
            Collections.sort(statements);
            adapter.setmStatements(statements);
            if(statements.size()==0){
                mRecyclerView.setVisibility(GONE);
                mNoAllStatementsTv.setVisibility(View.VISIBLE);
            }else{
                mNoAllStatementsTv.setVisibility(GONE);
                mRecyclerView.setVisibility(View.VISIBLE);
            }
        });
        mStatementsViewModel.getTotalExpenses().observe(getViewLifecycleOwner(),
                aLong -> mTotalExpenses.setText(AmountConverter.toString(aLong)));
        mStatementsViewModel.getTotalSavings().observe(getViewLifecycleOwner(),
                aLong -> mTotalSavings.setText(AmountConverter.toString(aLong)));
    }

    @Override
    public void onItemClickListener(int itemId) {
        mStatementsViewModel.getStatementById(itemId).observe(this, new Observer<Statement>() {
            @Override
            public void onChanged(Statement statement) {
                mStatementsViewModel.getStatementById(itemId).removeObserver(this);
                if(statement!=null) {
                    Intent intent = new Intent(getActivity(), AddStatementActivity.class);
                    intent.putExtra(AddStatementActivity.EXTRA_STATEMENT_ID, itemId);
                    intent.putExtra(AddStatementActivity.STATEMENT_LOCAL_ID, statement.getLocalId());
                    intent.putExtra(AddStatementActivity.ENTRANCE_PAGE,1);
                    startActivity(intent);
                }
            }
        });
    }
}
