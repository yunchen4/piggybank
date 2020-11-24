package com.yunchen.piggybank.utils;

import com.yunchen.piggybank.database.entity.Plan;
import com.yunchen.piggybank.database.entity.Statement;

public class WebServiceUtils {

    private static final String BASE_URL = "https://studev.groept.be/api/a19sd612";
    private static final String STATEMENT = "statement/";
    private static final String PLAN = "plan/";
    private static final String QUERY = "/query";
    private static final String INSERT = "/insert";
    private static final String UPDATE = "/update";
    private static final String DELETE = "/delete";
    private static final String SLASH = "/";
    private static final StringBuilder requestUrl = new StringBuilder();


    public static String getStatementInsertUrl(String username, Statement statement, int localId){
        requestUrl.delete(0,requestUrl.length());
        requestUrl.append(BASE_URL+INSERT+"_"+STATEMENT);
        requestUrl.append(username);
        requestUrl.append(SLASH);
        requestUrl.append(localId);
        requestUrl.append(SLASH);
        requestUrl.append(statement.getAmount());
        requestUrl.append(SLASH);
        requestUrl.append(statement.getType());
        requestUrl.append(SLASH);
        requestUrl.append(statement.getPrimaryCategory());
        requestUrl.append(SLASH);
        requestUrl.append(statement.getSecondaryCategory());
        requestUrl.append(SLASH);
        requestUrl.append(statement.getDate());
        requestUrl.append(SLASH);
        if(statement.getMemo().length()!=0) {
            requestUrl.append(statement.getMemo());
        }else{
            requestUrl.append("null");
        }
        requestUrl.append(SLASH);
        requestUrl.append(localId);
        requestUrl.append(SLASH);
        requestUrl.append(username);
        return requestUrl.toString();
    }

    public static String getPlanInsertUrl(String username, Plan plan, int localPlanId){
        requestUrl.delete(0, requestUrl.length());
        requestUrl.append(BASE_URL+INSERT+"_"+PLAN);
        requestUrl.append(username);
        requestUrl.append(SLASH);
        requestUrl.append(localPlanId);
        requestUrl.append(SLASH);
        requestUrl.append(plan.getGoalAmount());
        requestUrl.append(SLASH);
        requestUrl.append(plan.getStartDate());
        requestUrl.append(SLASH);
        requestUrl.append(plan.getEndDate());
        requestUrl.append(SLASH);
        requestUrl.append(plan.getPunishment());
        requestUrl.append(SLASH);
        requestUrl.append(plan.getGoalRemain());
        requestUrl.append(SLASH);
        requestUrl.append(plan.getCurrentState());
        requestUrl.append(SLASH);
        requestUrl.append(localPlanId);
        requestUrl.append(SLASH);
        requestUrl.append(username);
        return requestUrl.toString();
    }

    public static String getStatementUpdateUrl(String username, Statement statement){
        requestUrl.delete(0, requestUrl.length());
        requestUrl.append(BASE_URL+UPDATE+"_"+STATEMENT);
        requestUrl.append(statement.getAmount());
        requestUrl.append(SLASH);
        requestUrl.append(statement.getType());
        requestUrl.append(SLASH);
        requestUrl.append(statement.getPrimaryCategory());
        requestUrl.append(SLASH);
        requestUrl.append(statement.getSecondaryCategory());
        requestUrl.append(SLASH);
        requestUrl.append(statement.getDate());
        requestUrl.append(SLASH);
        if(statement.getMemo().length()!=0) {
            requestUrl.append(statement.getMemo());
        }else{
            requestUrl.append("null");
        }
        requestUrl.append(SLASH);
        requestUrl.append(username);
        requestUrl.append(SLASH);
        requestUrl.append(statement.getLocalId());
        return requestUrl.toString();
    }

    public static String getPlanUpdateUrl(String username, Plan plan){
        requestUrl.delete(0, requestUrl.length());
        requestUrl.append(BASE_URL+UPDATE+"_"+PLAN);
        requestUrl.append(plan.getGoalAmount());
        requestUrl.append(SLASH);
        requestUrl.append(plan.getEndDate());
        requestUrl.append(SLASH);
        requestUrl.append(plan.getPunishment());
        requestUrl.append(SLASH);
        requestUrl.append(plan.getCurrentState());
        requestUrl.append(SLASH);
        requestUrl.append(plan.getGoalRemain());
        requestUrl.append(SLASH);
        requestUrl.append(username);
        requestUrl.append(SLASH);
        requestUrl.append(plan.getLocalId());
        return requestUrl.toString();
    }

    public static String getStatementDeleteUrl(String username, Statement statement){
        requestUrl.delete(0,requestUrl.length());
        requestUrl.append(BASE_URL+DELETE+"_"+STATEMENT);
        requestUrl.append(username);
        requestUrl.append(SLASH);
        requestUrl.append(statement.getLocalId());
        return requestUrl.toString();
    }

    public static String getPlanDeleteUrl(String username, Plan plan){
        requestUrl.delete(0,requestUrl.length());
        requestUrl.append(BASE_URL+DELETE+"_"+PLAN);
        requestUrl.append(username);
        requestUrl.append(SLASH);
        requestUrl.append(plan.getLocalId());
        return requestUrl.toString();
    }


    public static String getAllStatementsUrl(String username){
        requestUrl.delete(0,requestUrl.length());
        requestUrl.append(BASE_URL+"/query_all_statements/"+username);
        return requestUrl.toString();
    }

    public static String getAllPlansUrl(String username){
        requestUrl.delete(0,requestUrl.length());
        requestUrl.append(BASE_URL+"/query_all_plans/"+username);
        return requestUrl.toString();
    }

    public static String getPwd(String username){
        requestUrl.delete(0,requestUrl.length());
        requestUrl.append(BASE_URL+"/login/"+username);
        return requestUrl.toString();
    }

    public static String register(String username, String pwd, String email){
        requestUrl.delete(0,requestUrl.length());
        requestUrl.append(BASE_URL+"/register/");
        requestUrl.append(username);
        requestUrl.append(SLASH);
        requestUrl.append(pwd);
        requestUrl.append(SLASH);
        requestUrl.append(email);
        return requestUrl.toString();
    }

    public static String isUserExist(String username){
        requestUrl.delete(0,requestUrl.length());
        requestUrl.append(BASE_URL+"/user_check/"+username);
        return requestUrl.toString();
    }

    public static String resetPwd(String username, String newPwd){
        requestUrl.delete(0,requestUrl.length());
        requestUrl.append(BASE_URL+"/reset_pwd/"+newPwd+"/"+username);
        return requestUrl.toString();
    }

}
