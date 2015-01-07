/*
 * This file is protected under the KILLGPL.
 * For more information visit <insert_valid_link_to_killgpl_here>
 * <p/>
 * Copyright (c) Luke A. Leber <LukeLeber@gmail.com> 2014
 */

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
