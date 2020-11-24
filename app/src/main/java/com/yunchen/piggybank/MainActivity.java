package com.yunchen.piggybank;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Toast;

import com.yunchen.piggybank.database.entity.Plan;
import com.yunchen.piggybank.utils.DateUtils;
import com.yunchen.piggybank.viewmodel.MainViewModel;
import com.google.android.material.tabs.TabLayout;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private ViewPager viewPager;
    private SharedPreferences appInfo;
    private SharedPreferences.Editor editor;
    private MainViewModel viewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        appInfo = getSharedPreferences("appInfo", MODE_PRIVATE);
        boolean isFirst = appInfo.getBoolean("isFirst", true);
        editor = appInfo.edit();
        setContentView(R.layout.activity_main);
        Objects.requireNonNull(getSupportActionBar()).setElevation(0f);
        initTab();
        if(isFirst){
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            editor.putBoolean("isFirst", false);
            editor.putBoolean("isInPlan",false);
            editor.putBoolean("isMonthlyUpdated",true);
            editor.putBoolean("isJustFinished",false);
            editor.commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        int page = getIntent().getIntExtra("page",0);
        viewPager.setCurrentItem(page);
        if(appInfo.getBoolean("isInPlan",false)){
            viewModel.refreshPlanByDate(DateUtils.getNormalizedUtcDateForToday());
        }
        viewModel.updateWaitingItems();
        boolean isJustFinished = appInfo.getBoolean("isJustFinished",false);
        if(isJustFinished){
            viewModel.getTheLatestPlan().observe(this, plan -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                if(plan.getCurrentState()==Plan.COMPLETED) {
                    builder.setMessage(R.string.dialog_message_completed)
                            .setTitle(R.string.dialog_title_completed)
                            .setPositiveButton(R.string.dialog_button_completed,
                                    (dialogInterface, i) -> {

                            });
                }else if(plan.getCurrentState()==Plan.FAILED){
                    String message = "Sorry, you failed your plan...\nDo not forget your punishment:"+plan.getPunishment();
                    builder.setMessage(message)
                            .setTitle(R.string.dialog_title_failed)
                            .setNegativeButton(R.string.dialog_button_failed,
                                    (dialogInterface, i) -> {

                            });
                }
                AlertDialog dialog = builder.create();
                dialog.show();
            });
            editor.putBoolean("isJustFinished",false);
            editor.commit();
        }
    }

    public void initTab() {
        TabLayout mTab = findViewById(R.id.tab);
        mTab.setTabMode(TabLayout.MODE_FIXED);
        viewPager = findViewById(R.id.view_pager);
        FragmentPagerAdapter adapter = new FragmentPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        mTab.setupWithViewPager(viewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        if(appInfo.getString("username",null)!=null){
            inflater.inflate(R.menu.main_menu_logout,menu);
        }else{
            inflater.inflate(R.menu.main_menu_login,menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_login){
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            return true;
        }else if(id == R.id.action_register){
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
            return true;
        }else if(id == R.id.action_logout){
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setMessage(R.string.dialog_message_logout)
                    .setTitle(R.string.dialog_title_logout)
                    .setPositiveButton(R.string.dialog_btn_logout_confirm, (dialogInterface, i) -> {
                        editor.putString("username",null);
                        editor.putInt("localId",-1);
                        editor.putInt("localPlanId",-1);
                        editor.putBoolean("isInPlan",false);
                        editor.putBoolean("isMonthlyUpdated",false);
                        editor.putBoolean("isJustFinished",false);
                        editor.commit();
                        viewModel.deleteEverything();
                        recreate();
                        Toast.makeText(MainActivity.this, "You log out successfully!", Toast.LENGTH_SHORT).show();
                    }).setNegativeButton(R.string.dialog_btn_logout_cancel,
                    (dialogInterface, i) -> {

                    });
            AlertDialog dialog = builder.create();
            dialog.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

class FragmentPagerAdapter extends androidx.fragment.app.FragmentPagerAdapter {
    private final int COUNT = 3;
    private String[] titles = new String[]{"Home","Statements","Plans"};

    FragmentPagerAdapter(FragmentManager fm){
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 1:
                return StatementsFragment.newInstance(position);
            case 2:
                return PlansFragment.newInstance(position);
            default:
                return HomeFragment.newInstance(position);
        }
    }

    @Override
    public int getCount() {
        return COUNT;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        super.destroyItem(container, position, object);
    }
}