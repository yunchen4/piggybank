package com.yunchen.piggybank;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.yunchen.piggybank.database.converter.AmountConverter;
import com.yunchen.piggybank.database.converter.DateConverter;
import com.yunchen.piggybank.database.entity.Plan;
import com.yunchen.piggybank.utils.DateUtils;
import com.yunchen.piggybank.viewmodel.AddPlanViewModel;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Objects;

public class AddPlanActivity extends AppCompatActivity {

    public static final String EXTRA_PLAN_ID = "extraStatementId";
    public static final String INSTANCE_PLAN_ID = "instanceStatementId";
    private static final int DEFAULT_PLAN_ID = -1;
    public static final String PLAN_LOCAL_ID = "planLocalId";
    public static final String PLAN_CURRENT_STATE = "planCurrentState";

    private EditText mAmountGoal;
    private TextView mEndDate;
    private EditText mPunishment;
    private Button mSubmitButton;
    private Button mDeleteButton;
    private AddPlanViewModel viewModel;

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    private int mPlanId = DEFAULT_PLAN_ID;
    private int mLocalId;
    private long oldRemain;
    private long oldGoal;
    private int mCurrentState;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_plan);
        initViews();
        viewModel = new ViewModelProvider(this).get(AddPlanViewModel.class);
        preferences = getSharedPreferences("appInfo",MODE_PRIVATE);
        editor = preferences.edit();
        if (savedInstanceState != null && savedInstanceState.containsKey(INSTANCE_PLAN_ID)) {
            mPlanId = savedInstanceState.getInt(INSTANCE_PLAN_ID, DEFAULT_PLAN_ID);
        }

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(EXTRA_PLAN_ID)) {
            mLocalId = intent.getIntExtra(PLAN_LOCAL_ID,0);
            mSubmitButton.setText(R.string.btn_update);
            mDeleteButton.setVisibility(View.VISIBLE);
            if (mPlanId == DEFAULT_PLAN_ID) {
                mPlanId = intent.getIntExtra(EXTRA_PLAN_ID, DEFAULT_PLAN_ID);
                viewModel.getPlanById(mPlanId).observe(this, new Observer<Plan>() {
                    @Override
                    public void onChanged(Plan plan) {
                        viewModel.getPlanById(mPlanId).removeObserver(this);
                        if (plan != null) {
                            try {
                                oldRemain = plan.getGoalRemain();
                                oldGoal = plan.getGoalAmount();
                                mCurrentState = plan.getCurrentState();
                                populateUI(plan);
                                if(plan.getCurrentState()!=Plan.IN_PROGRESS) {
                                    mAmountGoal.setEnabled(false);
                                    mEndDate.setEnabled(false);
                                    mPunishment.setEnabled(false);
                                    mSubmitButton.setVisibility(View.INVISIBLE);
                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    private void initViews(){
        this.setTitle("Plan");
        mSubmitButton = findViewById(R.id.submit_plan_button);
        mEndDate = findViewById(R.id.time_goal);
        mAmountGoal = findViewById(R.id.amount_goal);
        mPunishment = findViewById(R.id.punishment);
        mSubmitButton.setOnClickListener(view -> {
            try {
                onSaveButtonClicked();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        });
        mDeleteButton = findViewById(R.id.delete_plan_button);
        mDeleteButton.setOnClickListener(view -> {
            try {
                onDeleteButtonClicked();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        });

        mEndDate.setOnTouchListener((view, motionEvent) -> {
            if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                showDatePickerDialog(mEndDate);
                return true;
            }
            return false;
        });
        mEndDate.setOnFocusChangeListener((view, b) -> {
            if(b){
                showDatePickerDialog(mEndDate);
            }
        });
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    }

    private void populateUI(Plan plan) throws ParseException {
        if(plan == null){
            return;
        }

        mAmountGoal.setText(AmountConverter.toString(plan.getGoalAmount()));
        mEndDate.setText(DateConverter.longToStr(plan.getEndDate()));
        mPunishment.setText(plan.getPunishment());
    }

    public void showDatePickerDialog(TextView mDate){
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(AddPlanActivity.this,
                (datePicker, year, month, day) -> {
                    String monthStr, dayStr;
                    monthStr = month+1<10?("0"+(month+1)) : String.valueOf(month+1);
                    dayStr = day<10?("0"+day):String.valueOf(day);
                    mDate.setText(year+"/"+monthStr+"/"+dayStr);
                }, calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        DatePicker dp = datePickerDialog.getDatePicker();
        dp.setMinDate(DateUtils.getNormalizedUtcDateForToday());
        datePickerDialog.show();
    }

    public void onSaveButtonClicked() throws ParseException {
        if(!isAllInfoFilledIn()){
            Toast.makeText(this, getResources().getText(R.string.warning_add_plan),
                    Toast.LENGTH_SHORT).show();
            return;
        }
        int oldId = preferences.getInt("localPlanId",-1);
        Plan plan = generateNewPlan();
        if(mPlanId==DEFAULT_PLAN_ID){
            plan.setLocalId(oldId+1);
            editor.putInt("localPlanId",oldId+1);
            editor.commit();
            viewModel.insertPlan(plan);
            editor.putBoolean("isInPlan",true);
            editor.commit();
        }else{
            plan.setId(mPlanId);
            plan.setLocalId(mLocalId);
            viewModel.updatePlan(plan);
        }
        backToLastPage();
    }

    public void onDeleteButtonClicked() throws ParseException {
        if(!isAllInfoFilledIn()){
            Toast.makeText(this, getResources().getText(R.string.warning_add_plan),
                    Toast.LENGTH_SHORT).show();
            return;
        }
        Plan plan = generateNewPlan();
        plan.setId(mPlanId);
        plan.setLocalId(mLocalId);
        if(mCurrentState==Plan.IN_PROGRESS){
            editor.putBoolean("isInPlan",false);
            editor.commit();
        }
        viewModel.deletePlan(plan);
        backToLastPage();
    }

    public Plan generateNewPlan() throws ParseException {
        BigDecimal amountBD = new BigDecimal(mAmountGoal.getText().toString());
        long amountGoal = AmountConverter.toLong(amountBD);
        long remain  = oldRemain-oldGoal+amountGoal;
        long endDate = DateUtils.normalizeDate(DateConverter.strToLong(mEndDate.getText().toString()));
        String punishment = mPunishment.getText().toString();
        return new Plan(endDate,amountGoal, remain,punishment, -1);
    }

    private boolean isAllInfoFilledIn(){
        if(mAmountGoal.getText().toString().length()==0 || mEndDate.getText().toString().length()== 0
                || mPunishment.getText().toString().length() == 0) {
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        backToLastPage();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id==android.R.id.home){
            backToLastPage();
        }
        return super.onOptionsItemSelected(item);
    }

    private void backToLastPage(){
        Intent intent = new Intent(AddPlanActivity.this, MainActivity.class);
        intent.putExtra("page",2);
        startActivity(intent);
    }
}
