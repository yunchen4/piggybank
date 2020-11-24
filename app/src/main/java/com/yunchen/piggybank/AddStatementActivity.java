package com.yunchen.piggybank;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.yunchen.piggybank.database.converter.AmountConverter;
import com.yunchen.piggybank.database.converter.DateConverter;
import com.yunchen.piggybank.database.entity.Statement;
import com.yunchen.piggybank.utils.DateUtils;
import com.yunchen.piggybank.viewmodel.AddStatementViewModel;


import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;


public class AddStatementActivity extends AppCompatActivity {


    public static final String EXTRA_STATEMENT_ID = "extraStatementId";
    public static final String INSTANCE_STATEMENT_ID = "instanceStatementId";
    public static final String STATEMENT_LOCAL_ID = "statementLocalId";
    public static final String ENTRANCE_PAGE = "entrancePage";
    private static final int DEFAULT_STATEMENT_ID = -1;
    public static final int INSERT = 0;
    public static final int UPDATE=1;
    public static final int DELETE=2;


    private Button mSaveButton;
    private Button mDeleteButton;
    private EditText mEditAmountView;
    private TextView mEditCategoryView;
    private EditText mEditMemoView;
    private TextView mDate;
    private AddStatementViewModel viewModel;
    private Spinner mTypeSpinner;

    private int mStatementId = DEFAULT_STATEMENT_ID;
    private int mLocalId;

    private long oldAmount;
    private long staDate;
    private String type;
    private String primaryCategory;
    private String secondaryCategory;

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    private List<String> primaryCategories;
    private List<List<String>> secondaryCategories;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_statement);
        initViews();
        preferences=getApplication().getSharedPreferences("appInfo",MODE_PRIVATE);
        editor = preferences.edit();
        primaryCategories = new ArrayList<>();
        secondaryCategories  = new ArrayList<>();

        viewModel = new ViewModelProvider(this).get(AddStatementViewModel.class);
        if (savedInstanceState != null && savedInstanceState.containsKey(INSTANCE_STATEMENT_ID)) {
            mStatementId = savedInstanceState.getInt(INSTANCE_STATEMENT_ID, DEFAULT_STATEMENT_ID);
        }
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(EXTRA_STATEMENT_ID)) {
            mLocalId = intent.getIntExtra(STATEMENT_LOCAL_ID,0);
            mSaveButton.setText(R.string.btn_update);
            mDeleteButton.setVisibility(View.VISIBLE);
            if (mStatementId == DEFAULT_STATEMENT_ID) {
                mStatementId = intent.getIntExtra(EXTRA_STATEMENT_ID, DEFAULT_STATEMENT_ID);
                viewModel.getStatementById(mStatementId).observe(this, new Observer<Statement>() {
                    @Override
                    public void onChanged(Statement statement) {
                        viewModel.getStatementById(mStatementId).removeObserver(this);
                        if (statement != null) {
                            try {
                                type = statement.getType();
                                primaryCategory = statement.getPrimaryCategory();
                                secondaryCategory = statement.getSecondaryCategory();
                                populateUI(statement);
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
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(INSTANCE_STATEMENT_ID, mStatementId);
        super.onSaveInstanceState(outState);
    }

    private void initViews(){
        this.setTitle("Statement");
        mEditAmountView = findViewById(R.id.amount);
        mEditAmountView.setOnFocusChangeListener((view, b) -> {
            InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(mEditAmountView.getWindowToken(),0);
        });
        mTypeSpinner = findViewById(R.id.type_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.types_array, R.layout.spinner_add_type);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mTypeSpinner.setAdapter(adapter);
        mTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                type = (String) adapterView.getItemAtPosition(i);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        mEditCategoryView = findViewById(R.id.category);
        mEditCategoryView.setOnTouchListener((view, motionEvent) -> {
            if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                showCategoryPickerView();
                return true;
            }
            return false;
        });
        mEditCategoryView.setOnFocusChangeListener((view, b) -> {
            if(b){
                showCategoryPickerView();
            }
        });
        mEditMemoView = findViewById(R.id.memo);
        mSaveButton = findViewById(R.id.save_button);
        mSaveButton.setOnClickListener(view -> {
            try {
                onSaveButtonClicked();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        });
        mDeleteButton = findViewById(R.id.delete_button);
        mDeleteButton.setOnClickListener(view -> {
            try {
                onDeleteButtonClicked();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        });
        mDate = findViewById(R.id.date);
        mDate.setOnTouchListener((view, motionEvent) -> {
            if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                showDatePickerDialog();
                return true;
            }
            return false;
        });
        mDate.setOnFocusChangeListener((view, b) -> {
            if(b){
                showDatePickerDialog();
            }
        });
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    }

    private void populateUI(Statement statement) throws ParseException {
        if (statement == null) {
            return;
        }

        oldAmount = statement.getAmount();
        mEditAmountView.setText(AmountConverter.toString(oldAmount));
        SpinnerAdapter adapter = mTypeSpinner.getAdapter();
        int count = adapter.getCount();
        for(int i =0;i<count;i++){
            if(statement.getType().equals(adapter.getItem(i).toString())){
                mTypeSpinner.setSelection(i,true);
                break;
            }
        }
        mEditCategoryView.setText(statement.getPrimaryCategory()+"->"+statement.getSecondaryCategory());
        mDate.setText(DateConverter.longToStr(statement.getDate()));
        mEditMemoView.setText(statement.getMemo());
    }

    public void onSaveButtonClicked() throws ParseException {
        if(!isAllInfoFilledIn()){
            Toast.makeText(this, getResources().getText(R.string.warning_add_statement),
                    Toast.LENGTH_SHORT).show();
            return;
        }
        int oldId = preferences.getInt("localId",-1);
        Statement statement = generateNewStatement();
        if(mStatementId==DEFAULT_STATEMENT_ID){
            statement.setLocalId(oldId+1);
            editor.putInt("localId",oldId+1);
            editor.commit();
            viewModel.insertStatement(statement);
            refreshPlanInProgress(INSERT,statement.getAmount());
        }else{
            statement.setId(mStatementId);
            statement.setLocalId(mLocalId);
            viewModel.updateStatement(statement);
            refreshPlanInProgress(UPDATE,statement.getAmount());
        }
        backToLastPage();
    }

    public void onDeleteButtonClicked() throws ParseException {
        if(!isAllInfoFilledIn()){
            Toast.makeText(this, getResources().getText(R.string.warning_add_statement),
                    Toast.LENGTH_SHORT).show();
            return;
        }
        Statement statement = generateNewStatement();
        statement.setId(mStatementId);
        statement.setLocalId(mLocalId);
        viewModel.deleteStatement(statement);
        refreshPlanInProgress(DELETE,statement.getAmount());
        backToLastPage();
    }

    public void showDatePickerDialog(){
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(AddStatementActivity.this,
                (datePicker, year, month, day) -> {
                    String monthStr, dayStr;
                    monthStr = month+1<10?("0"+(month+1)) : String.valueOf(month+1);
                    dayStr = day<10?("0"+day):String.valueOf(day);
                    AddStatementActivity.this.mDate.setText(year+"/"+monthStr+"/"+dayStr);
                }, calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DATE));
        datePickerDialog.show();
    }

    private Statement generateNewStatement() throws ParseException {
        BigDecimal amountBD = new BigDecimal(mEditAmountView.getText().toString());
        long amount = AmountConverter.toLong(amountBD);
        staDate = DateUtils.normalizeDate(DateConverter.strToLong(mDate.getText().toString()));
        String memo = mEditMemoView.getText().toString();
        return new Statement(amount, type, primaryCategory,secondaryCategory, staDate, memo,-1);
    }

    private void showCategoryPickerView(){
        primaryCategories.clear();
        secondaryCategories.clear();
        OptionsPickerView categoryPicker = new OptionsPickerBuilder(AddStatementActivity.this,
                (options1, options2, options3, v) -> {
                    AddStatementActivity.this.primaryCategory = primaryCategories.get(options1);
                    AddStatementActivity.this.secondaryCategory = secondaryCategories.get(options1).get(options2);
                    String category = AddStatementActivity.this.primaryCategory
                            +" -> "+AddStatementActivity.this.secondaryCategory;
                    AddStatementActivity.this.mEditCategoryView.setText(category);
                }).setCancelText("Cancel").setSubmitText("Confirm")
                .setCancelColor(Color.GRAY)
                .setSubmitColor(getResources().getColor(R.color.colorAccent))
                .setTextXOffset(0,0,0)
                .setTypeface(Typeface.DEFAULT)
                .setTextColorCenter(getResources().getColor(R.color.colorAccent))
                .setTextColorOut(Color.GRAY)
                .setContentTextSize(20)
                .isRestoreItem(true)
                .build();
        setCategoryData(type);
        categoryPicker.setPicker(primaryCategories,secondaryCategories);
        if(primaryCategories.contains(primaryCategory)) {
            categoryPicker.setSelectOptions(primaryCategories.indexOf(primaryCategory),
                    secondaryCategories.get(primaryCategories.indexOf(primaryCategory))
                            .indexOf(secondaryCategory));
        }
        categoryPicker.show();
    }

    private void setCategoryData(String type){
        if(type.equals("Budget")||type.equals("Saving")){
            primaryCategories.add(type);
            String[] items = this.getResources().getStringArray(R.array.incomes_array);
            List<String> secondaryCategories_01 = new ArrayList<>(Arrays.asList(items));
            secondaryCategories.add(secondaryCategories_01);
        }else if(type.equals("Expense")){
            String[] primary = this.getResources().getStringArray(R.array.expenses_primary_array);
            for(String item:primary){
                primaryCategories.add(item);
                String[] items = null;
                switch (item){
                    case "Food":
                        items = this.getResources().getStringArray(R.array.food_array);
                        break;
                    case "Transportation":
                        items = this.getResources().getStringArray(R.array.transportation_array);
                        break;
                    case "Amusement":
                        items = this.getResources().getStringArray(R.array.amusement_array);
                        break;
                    case "Shopping":
                        items = this.getResources().getStringArray(R.array.shopping_array);
                        break;
                    case "Household":
                        items = this.getResources().getStringArray(R.array.household_array);
                        break;
                    case "Communication":
                        items = this.getResources().getStringArray(R.array.communication_array);
                        break;
                    case "Study":
                        items = this.getResources().getStringArray(R.array.study_array);
                        break;
                    case "Relations":
                        items = this.getResources().getStringArray(R.array.relations_array);
                        break;
                    case "Medical":
                        items = this.getResources().getStringArray(R.array.medical_array);
                        break;
                    case "Others":
                        items = this.getResources().getStringArray(R.array.others_array);
                        break;
                }
                List<String> secondaryCategory_i = new ArrayList<>(Arrays.asList(items));
                secondaryCategories.add(secondaryCategory_i);
            }
        }
    }


    private void refreshPlanInProgress(int requestCode, long newAmount){
        if(preferences.getBoolean("isInPlan",false) && type.equals("Saving")){
            viewModel.refreshPlanInProgress(requestCode,oldAmount,newAmount,staDate);
        }
    }

    private boolean isAllInfoFilledIn(){
        if(mEditAmountView.getText().toString().length()==0 || mEditCategoryView.getText().toString().length()== 0
                || mDate.getText().toString().length() == 0) {
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
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void backToLastPage(){
        Intent intent = new Intent(AddStatementActivity.this, MainActivity.class);
        intent.putExtra("page",getIntent().getIntExtra(ENTRANCE_PAGE,0));
        startActivity(intent);
    }
}