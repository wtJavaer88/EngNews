package com.wnc.news.db;

import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseManager_ZB8
{

    private AtomicInteger mOpenCounter = new AtomicInteger();
    Logger log = Logger.getLogger(DatabaseManager_ZB8.class);

    private static DatabaseManager_ZB8 instance;
    private static SQLiteOpenHelper mDatabaseHelper;
    private SQLiteDatabase mDatabase;

    public static synchronized void initializeInstance(SQLiteOpenHelper helper)
    {
        if (instance == null)
        {
            instance = new DatabaseManager_ZB8();
            mDatabaseHelper = helper;
        }
    }

    public static synchronized DatabaseManager_ZB8 getInstance()
    {
        if (instance == null)
        {
            throw new IllegalStateException(
                    DatabaseManager_ZB8.class.getSimpleName()
                            + " is not initialized, call initializeInstance(..) method first.");
        }

        return instance;
    }

    public synchronized SQLiteDatabase openDatabase()
    {
        if (mOpenCounter.incrementAndGet() == 1)
        {
            // Opening new database
            mDatabase = mDatabaseHelper.getWritableDatabase();
        }
        return mDatabase;
    }

    public synchronized void closeDatabase()
    {
        if (mOpenCounter.decrementAndGet() == 0)
        {
            // Closing database
            mDatabase.close();
            log.info("数据库已经关闭");
        }
    }
}