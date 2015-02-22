package com.lukeleber.scandroid.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import com.lukeleber.database.ScopedSQLiteDatabase;
import com.lukeleber.scandroid.sae.j2012.DiagnosticTroubleCode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DTCDatabase
{
    private final static String TABLE = "dtcs";

    private final static String COLUMN_DTC_INDEX = "index";

    private final static String COLUMN_DTC_CODE = "code";

    private final static String COLUMN_DTC_NAMING = "naming";

    private final static String QUERY_RETRIEVE_DTC_BY_INDEX =
            "SELECT `" + COLUMN_DTC_CODE + "`, `"+ COLUMN_DTC_NAMING + "` " +
                    "FROM `" + TABLE + "` " +
                    "WHERE `" + COLUMN_DTC_INDEX + "` = %d LIMIT 1;";

    private final static String QUERY_RETRIEVE_DTC_BY_CODE =
            "SELECT `" + COLUMN_DTC_INDEX + "`, `"+ COLUMN_DTC_NAMING + "` " +
                    "FROM `" + TABLE + "` " +
                    "WHERE `" + COLUMN_DTC_CODE + "` = '%s' LIMIT 1";

    private final static String QUERY_RETRIEVE_DTC_BY_NAMING =
            "SELECT `" + COLUMN_DTC_INDEX + "`, `"+ COLUMN_DTC_CODE + "` " +
                    "FROM `" + TABLE + "` " +
                    "WHERE `" + COLUMN_DTC_NAMING + "` = '%s' LIMIT 1";

    private Map<Integer, DiagnosticTroubleCode> cache = new HashMap<>();

    public List<DiagnosticTroubleCode> getCodeByIndex(int... indices)
    {
        List<DiagnosticTroubleCode> rv = new ArrayList<>();
        String query = null;
        for(int index : indices)
        {
            if(!cache.containsKey(index))
            {
                if(query == null)
                {
                    query = String.format(QUERY_RETRIEVE_DTC_BY_INDEX, index);
                }
                else
                {
                    query = query.replace("LIMIT 1",
                            "OR `" + COLUMN_DTC_INDEX + "` = " + index + " LIMIT 1");
                }
            }
            else
            {
                rv.add(cache.get(index));
            }
        }
        if(query != null)
        {
            try(ScopedSQLiteDatabase database = new ScopedSQLiteDatabase(SQLiteDatabase.openDatabase("path", null, 0)))
            {
                Cursor c = database.unwrap().rawQuery(query, null);
                c.moveToFirst();
            }
            catch(Exception e)
            {
                // no-op
            }
        }
        return rv;
    }

    public DiagnosticTroubleCode getCodeByIndex(int index)
    {
        if(!cache.containsKey(index))
        {
            try(ScopedSQLiteDatabase db = new ScopedSQLiteDatabase(SQLiteDatabase.openDatabase("path", null, 0)))
            {
                Cursor c = db.unwrap().rawQuery("", null);
                c.moveToFirst();
                if(!c.isAfterLast())
                {
                    return cache.put(index, new DiagnosticTroubleCode(index, c.getString(c.getColumnIndex(
                            COLUMN_DTC_NAMING))));

                }
            }
            catch(Exception e)
            {
                // todo
            }
        }
        return cache.get(index);
    }
}
