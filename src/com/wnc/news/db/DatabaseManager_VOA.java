package com.wnc.news.db;

import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseManager_VOA
{

    private AtomicInteger mOpenCounter = new AtomicInteger();
    Logger log = Logger.getLogger(DatabaseManager_VOA.class);

    private static DatabaseManager_VOA instance;
    private static SQLiteOpenHelper mDatabaseHelper;
    private SQLiteDatabase mDatabase;

    public static synchronized void initializeInstance(SQLiteOpenHelper helper)
    {
        if (instance == null)
        {
            instance = new DatabaseManager_VOA();
            mDatabaseHelper = helper;
        }
    }

    public static synchronized DatabaseManager_VOA getInstance()
    {
        if (instance == null)
        {
            throw new IllegalStateException(
                    DatabaseManager_VOA.class.getSimpleName()
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