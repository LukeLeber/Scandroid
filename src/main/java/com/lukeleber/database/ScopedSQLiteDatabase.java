// This file is protected under the KILLGPL.
// For more information, visit http://www.lukeleber.github.io/KILLGPL.html
//
// Copyright (c) Luke Leber <LukeLeber@gmail.com>

package com.lukeleber.database;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import com.lukeleber.scandroid.BuildConfig;

public final class ScopedSQLiteDatabase
        implements
        AutoCloseable
{

    private final static String TAG = ScopedSQLiteDatabase.class.getName();

    private final SQLiteDatabase wrapped;

    public ScopedSQLiteDatabase(SQLiteDatabase wrapped)
    {
        this.wrapped = wrapped;
    }

    public SQLiteDatabase unwrap()
    {
        return wrapped;
    }

    @Override
    public void close()
            throws
            Exception
    {
        try
        {
            wrapped.close();
        }
        catch (SQLiteException e)
        {
            if (BuildConfig.DEBUG)
            {
                Log.e(TAG, "Unable to close database", e);
            }
        }
    }
}
