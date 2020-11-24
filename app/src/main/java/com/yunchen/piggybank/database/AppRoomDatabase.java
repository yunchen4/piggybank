package com.yunchen.piggybank.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.yunchen.piggybank.database.converter.DateConverter;
import com.yunchen.piggybank.database.dao.PlanDao;
import com.yunchen.piggybank.database.dao.StatementDao;
import com.yunchen.piggybank.database.entity.Plan;
import com.yunchen.piggybank.database.entity.Statement;

@Database(entities = {Statement.class, Plan.class}, version = 1, exportSchema = false)
@TypeConverters(DateConverter.class)
public abstract class AppRoomDatabase extends RoomDatabase {

    public abstract StatementDao statementDao();
    public abstract PlanDao planDao();

    private static volatile AppRoomDatabase INSTANCE;

    public static AppRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppRoomDatabase.class, "app_database")
                            .addCallback(sRoomDatabaseCallback).build();
                }
            }
        }
        return INSTANCE;
    }

    private static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);
        }
    };
}
