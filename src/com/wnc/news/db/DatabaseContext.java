package com.wnc.news.db;

import java.io.File;

import android.content.Context;
import android.content.ContextWrapper;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;

class DatabaseContext extends ContextWrapper
{

    private static final String DEBUG_CONTEXT = "DatabaseContext";

    public DatabaseContext(Context base)
    {
        super(base);
    }

    @Override
    public File getDatabasePath(String dbfile)
    {
        if (!dbfile.endsWith(".db"))
        {
            dbfile += ".db";
        }

        File result = new File(dbfile);

        return result;
    }

    /*
     * this version is called for android devices >= api-11. thank to @damccull
     * for fixing this.
     */
    @Override
    public SQLiteDatabase openOrCreateDatabase(String name, int mode,
            SQLiteDatabase.CursorFactory factory,
            DatabaseErrorHandler errorHandler)
    {
        return openOrCreateDatabase(name, mode, factory);
    }

    /* this version is called for android devices < api-11 */
    @Override
    public SQLiteDatabase openOrCreateDatabase(String name, int mode,
            SQLiteDatabase.CursorFactory factory)
    {
        SQLiteDatabase result = SQLiteDatabase.openOrCreateDatabase(
                getDatabasePath(name), null);

        return result;
    }
}
