package com.yunchen.piggybank.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.yunchen.piggybank.database.entity.Statement;

import java.util.List;

@Dao
public interface StatementDao {

    @Insert
    void insertStatement(Statement statement);

    @Delete
    void deleteStatement(Statement statement);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateStatement(Statement statement);

    @Query("SELECT * FROM Statement WHERE date=:today")
    LiveData<List<Statement>> getTodayStatements(long today);

    @Query("SELECT * FROM Statement ORDER BY date")
    LiveData<List<Statement>> getAllStatements();

    @Query("DELETE FROM Statement")
    void deleteAllStatements();

    @Query("SELECT * FROM Statement WHERE id=:id")
    LiveData<Statement> getStatementById(int id);

    @Query("SELECT * FROM Statement WHERE type=:type")
    LiveData<List<Statement>> getStatementByType(String type);

    @Query("SELECT SUM(amount) FROM Statement WHERE date>=:now AND date<:nextMonth AND type='Expense'")
    LiveData<Long> getMonthlyExpenses(long now, long nextMonth);

    @Query("SELECT SUM(amount) FROM Statement WHERE date>=:now AND date<:nextMonth AND type='Expense'")
    long getMonthlyExpensesNL(long now, long nextMonth);

    @Query("SELECT SUM(amount) FROM Statement WHERE date>=:now AND date<:nextMonth AND type='Budget'")
    LiveData<Long> getMonthlyBudget(long now, long nextMonth);

    @Query("SELECT SUM(amount) FROM Statement WHERE date>=:now AND date<:nextMonth AND type='Budget'")
    long getMonthlyBudgetNL(long now, long nextMonth);

    @Query("SELECT SUM(amount) FROM Statement WHERE date>=:now AND date<:nextMonth AND type='Saving'")
    LiveData<Long> getMonthlySavings(long now, long nextMonth);

    @Query("SELECT EXISTS(SELECT * FROM Statement WHERE id=:latestId)")
    boolean hasLatestStatement(int latestId);

    @Query("SELECT SUM(amount) FROM Statement WHERE type='Expense'")
    LiveData<Long> getTotalExpenses();

    @Query("SELECT SUM(amount) FROM Statement WHERE type='Saving'")
    LiveData<Long> getTotalSavings();
}
