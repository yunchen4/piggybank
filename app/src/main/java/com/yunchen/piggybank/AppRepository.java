package com.yunchen.piggybank;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import androidx.lifecycle.LiveData;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.yunchen.piggybank.database.AppRoomDatabase;
import com.yunchen.piggybank.database.dao.PlanDao;
import com.yunchen.piggybank.database.entity.Plan;
import com.yunchen.piggybank.database.entity.Statement;
import com.yunchen.piggybank.database.dao.StatementDao;
import com.yunchen.piggybank.utils.DateUtils;
import com.yunchen.piggybank.utils.WebServiceUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class AppRepository {

    private StatementDao mStatementDao;
    private PlanDao mPlanDao;

    private RequestQueue requestQueue;
    private SharedPreferences.Editor editor;
    private SharedPreferences preferences;
    private Context context;

    private volatile static AppRepository instance;

    private List<Statement> waitingInsertStatements;
    private List<Statement> waitingUpdateStatements;
    private List<Statement> waitingDeleteStatements;

    private List<Plan> waitingInsertPlans;
    private List<Plan> waitingUpdatePlans;
    private List<Plan> waitingDeletePlans;

    private AppRepository(Application application){
        AppRoomDatabase db = AppRoomDatabase.getDatabase(application);
        mStatementDao = db.statementDao();
        mPlanDao = db.planDao();
        context = application.getApplicationContext();
        preferences = application.getSharedPreferences("appInfo",Context.MODE_PRIVATE);
        requestQueue = Volley.newRequestQueue(context);
        editor = preferences.edit();
        waitingInsertStatements = new ArrayList<>();
        waitingUpdateStatements = new ArrayList<>();
        waitingDeleteStatements = new ArrayList<>();
        waitingInsertPlans  = new ArrayList<>();
        waitingUpdatePlans  = new ArrayList<>();
        waitingDeletePlans  = new ArrayList<>();
    }

    public static AppRepository getInstance(Application application){
        if(instance==null){
            synchronized (AppRepository.class) {
                if(instance==null) {
                    instance = new AppRepository(application);
                }
            }
        }
        return instance;
    }

    public LiveData<List<Statement>> getmAllStatements(){
        return mStatementDao.getAllStatements();
    }

    public LiveData<List<Plan>> getmAllPlans() {
        return mPlanDao.getAllPlans();
    }

    public void insertStatement(Statement statement){
        AppExecutors.getInstance().diskIO().execute(()->
                mStatementDao.insertStatement(statement));
            waitingInsertStatements.add(statement);
    }

    public void updateStatement(Statement statement){
        AppExecutors.getInstance().diskIO().execute(()->
                mStatementDao.updateStatement(statement));

            waitingUpdateStatements.add(statement);
    }

    public LiveData<List<Statement>> getTodayStatements(long now) {
        return mStatementDao.getTodayStatements(now);
    }

    public LiveData<Statement> getStatementById(int id){
        return mStatementDao.getStatementById(id);
    }

    public LiveData<List<Statement>> getStatementsByType(String type){
        return mStatementDao.getStatementByType(type);
    }

    public void deleteStatement(Statement statement){
        AppExecutors.getInstance().diskIO().execute(()->
                mStatementDao.deleteStatement(statement));

            waitingDeleteStatements.add(statement);
    }

    public LiveData<Long> getMonthlyExpenses(){
        try {
            long now = DateUtils.getNormalizedUtcDateForThisMonth();
            long next = DateUtils.getNormalizedUtcDateForNextMonth();
            return mStatementDao.getMonthlyExpenses(now,next);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public LiveData<Long> getMonthlyBudget(){
        try {
            long now = DateUtils.getNormalizedUtcDateForThisMonth();
            long next = DateUtils.getNormalizedUtcDateForNextMonth();
            return mStatementDao.getMonthlyBudget(now,next);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public LiveData<Long> getMonthlySavings(){
        try {
            long now = DateUtils.getNormalizedUtcDateForThisMonth();
            long next = DateUtils.getNormalizedUtcDateForNextMonth();
            return mStatementDao.getMonthlySavings(now,next);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public LiveData<Long> getTotalSavings(){
        return mStatementDao.getTotalSavings();
    }

    public LiveData<Long> getTotalExpenses(){
        return mStatementDao.getTotalExpenses();
    }

    public void insertPlan(Plan plan){
        AppExecutors.getInstance().diskIO().execute(()-> mPlanDao.insertPlan(plan));

            waitingInsertPlans.add(plan);
    }

    public void updatePlan(Plan plan){
        AppExecutors.getInstance().diskIO().execute(()-> mPlanDao.updatePlan(plan));

            waitingUpdatePlans.add(plan);
    }

    public void deletePlan(Plan plan){
        AppExecutors.getInstance().diskIO().execute(()-> mPlanDao.deletePlan(plan));

            waitingDeletePlans.add(plan);
    }

    public void deleteEverything(){
        AppExecutors.getInstance().diskIO().execute(()->{
            mPlanDao.deleteAllPlans();
            mStatementDao.deleteAllStatements();
        });
    }

    public LiveData<Plan> getPlanById(int id){
        return mPlanDao.getPlanById(id);
    }

    public LiveData<Plan> getTheLatestPlan(){
        return mPlanDao.getTheLatestPlanDirectly();
    }

    public void refreshPlanInProgressByStatement(int requestCode, long oldAmount, long newAmount,
                                                 long staDate){
        AppExecutors.getInstance().diskIO().execute(()->{
            Plan plan = mPlanDao.getPlanInProgressDirectly();
            if(plan!=null && staDate>=plan.getStartDate() && staDate<=plan.getEndDate()) {
                long oldRemain = plan.getGoalRemain();
                switch (requestCode) {
                    case AddStatementActivity.INSERT:
                        plan.setGoalRemain(oldRemain - newAmount);
                        break;
                    case AddStatementActivity.UPDATE:
                        plan.setGoalRemain(oldRemain + oldAmount - newAmount);
                        break;
                    case AddStatementActivity.DELETE:
                        plan.setGoalRemain(oldRemain + newAmount);
                        break;
                }
                updatePlan(plan);
            }
        });
    }

    public void refreshPlanByDate(long now){
        AppExecutors.getInstance().diskIO().execute(()->{
            Plan plan = mPlanDao.getPlanInProgressDirectly();
            if(plan!=null) {
                if(!DateUtils.isTheFirstDayOfMonth(now)){
                    editor.putBoolean("isMonthlyUpdated",false);
                    editor.commit();
                }else if(!preferences.getBoolean("isMonthlyUpdated",true)){
                    try {
                        long oldRemain = plan.getGoalRemain();
                        long lastMonth = DateUtils.getNormalizedUtcDateForLastMonth();
                        long thisMonth = DateUtils.getNormalizedUtcDateForThisMonth();
                        long expenses = mStatementDao.getMonthlyExpensesNL(lastMonth,thisMonth);
                        long budget = mStatementDao.getMonthlyBudgetNL(lastMonth,thisMonth);
                        int oldLocalStatementId = preferences.getInt("localId",-1);
                        if(budget-expenses >0){
                            Statement remain = new Statement(budget-expenses, "Saving", "Saving",
                                "Others", thisMonth, "Last month budget remains",
                                oldLocalStatementId+1);
                            refreshPlanInProgressByStatement(AddStatementActivity.INSERT,
                                    0, remain.getAmount(), thisMonth);
                            insertStatement(remain);
                            editor.putInt("localId", oldLocalStatementId + 1);
                        }else if(budget - expenses < 0){
                            plan.setGoalRemain(oldRemain - budget + expenses);
                            updatePlan(plan);
                        }
                        editor.putBoolean("isMonthlyUpdated",true);
                        editor.commit();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                if (plan.getGoalRemain() <= 0) {
                    plan.setCurrentState(Plan.COMPLETED);
                    editor.putBoolean("isInPlan", false);
                    editor.putBoolean("isJustFinished",true);
                    editor.commit();
                    updatePlan(plan);
                }else{
                    if (now > plan.getEndDate()) {
                        plan.setCurrentState(Plan.FAILED);
                        editor.putBoolean("isInPlan", false);
                        editor.putBoolean("isJustFinished",true);
                        editor.commit();
                        updatePlan(plan);
                    }
                }
            }
        });
    }

    public void recoverEverythingFromRemoteDb(String username){
        AppExecutors.getInstance().diskIO().execute(()->{
            int localId = preferences.getInt("localId",-1);
            if(!mStatementDao.hasLatestStatement(localId)) {
                String requestPlansUrl = WebServiceUtils.getAllPlansUrl(username);
                JsonArrayRequest getAllPlansRequest = new JsonArrayRequest(Request.Method.GET,
                        requestPlansUrl, null,
                        response -> AppExecutors.getInstance().diskIO().execute(()->{
                            JSONObject o;
                            for (int i = 0; i < response.length(); i++) {
                                try {
                                    o = response.getJSONObject(i);
                                    mPlanDao.insertPlan(new Plan(o));
                                }
                                catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }), error -> {
                        });
                String requestStatementsUrl = WebServiceUtils.getAllStatementsUrl(username);
                JsonArrayRequest getAllStatementsRequest = new JsonArrayRequest(Request.Method.GET,
                        requestStatementsUrl, null,
                        response -> AppExecutors.getInstance().diskIO().execute(()->{
                            JSONObject o;
                            for (int i = 0; i < response.length(); i++) {
                                try {
                                    o = response.getJSONObject(i);
                                    mStatementDao.insertStatement(new Statement(o));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }), error -> {
                        });
                requestQueue.add(getAllPlansRequest);
                requestQueue.add(getAllStatementsRequest);
            }
        });
    }

    public void uploadWaitingItems(){
        String username = preferences.getString("username",null);
        int localId = preferences.getInt("localId",-1);
        int localPlanId = preferences.getInt("localPlanId",-1);
        if(username!=null) {
            ListIterator<Statement> iterator = waitingInsertStatements.listIterator();
            while (iterator.hasNext()) {
                if (isNetWork()) {
                    String requestUrl = WebServiceUtils.getStatementInsertUrl(username, iterator.next(), localId);
                    syncWithRemote(requestUrl);
                    iterator.remove();
                } else {
                    break;
                }
            }
            iterator = waitingUpdateStatements.listIterator();
            while (iterator.hasNext()) {
                if (isNetWork()) {
                    String requestUrl = WebServiceUtils.getStatementUpdateUrl(username, iterator.next());
                    syncWithRemote(requestUrl);
                    iterator.remove();
                } else {
                    break;
                }
            }
            iterator = waitingDeleteStatements.listIterator();
            while (iterator.hasNext()) {
                if (isNetWork()) {
                    String requestUrl = WebServiceUtils.getStatementDeleteUrl(username, iterator.next());
                    syncWithRemote(requestUrl);
                    iterator.remove();
                } else {
                    break;
                }
            }
            ListIterator<Plan> planListIterator = waitingInsertPlans.listIterator();
            while (planListIterator.hasNext()) {
                if (isNetWork()) {
                    String requestUrl = WebServiceUtils.getPlanInsertUrl(username, planListIterator.next(), localPlanId);
                    syncWithRemote(requestUrl);
                    planListIterator.remove();
                } else {
                    break;
                }
            }
            planListIterator = waitingUpdatePlans.listIterator();
            while (planListIterator.hasNext()) {
                if (isNetWork()) {
                    String requestUrl = WebServiceUtils.getPlanUpdateUrl(username, planListIterator.next());
                    syncWithRemote(requestUrl);
                    planListIterator.remove();
                } else {
                    break;
                }
            }
            planListIterator = waitingDeletePlans.listIterator();
            while (planListIterator.hasNext()) {
                if (isNetWork()) {
                    String requestUrl = WebServiceUtils.getPlanDeleteUrl(username, planListIterator.next());
                    syncWithRemote(requestUrl);
                    planListIterator.remove();
                } else {
                    break;
                }
            }
        }
    }

    private void syncWithRemote(String url){
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET,
                url, null,
                response -> {
                }, error -> {
                });
        requestQueue.add(request);
    }

    private boolean isNetWork(){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if(networkInfo!=null&&networkInfo.isAvailable()){
            return true;
        }
        return false;
    }
}