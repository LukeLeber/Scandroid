// This file is protected under the KILLGPL.
// For more information, visit http://www.lukeleber.github.io/KILLGPL
//
// Copyright (c) Luke Leber <LukeLeber@gmail.com>
//
// "This software is provided freely under a single condition.  You can't force anyone
// that uses this software to release any source code.  It is their decision alone.
// Other than that, anything goes.
//
// PS. If you like this work, please hire me!  I'm uneducated (no CS degree)
// and as such its borderline impossible to get into the industry.  My current
// career is absolute hell and I am trapped in it.  I leave this message here
// in the vain hope that someone, somewhere might read this and give me the
// opportunity to join their association as a programmer."
//
// Cheers,
// Luke

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
