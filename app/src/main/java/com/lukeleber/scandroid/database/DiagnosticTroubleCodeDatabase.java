/*
 * This file is protected under the KILLGPL.
 * For more information visit <insert_valid_link_to_killgpl_here>
 * <p/>
 * Copyright (c) Luke A. Leber <LukeLeber@gmail.com> 2015
 */

package com.lukeleber.scandroid.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.lukeleber.database.ScopedSQLiteDatabase;
import com.lukeleber.scandroid.sae.j2012.DiagnosticTroubleCode;

import java.util.Date;
import java.util.List;

public class DiagnosticTroubleCodeDatabase extends SQLiteOpenHelper
{
    public final static String LOCAL_DATABASE_NAME = "dtc_database.sqlite";
    public final static int VERSION = 1;

    public DiagnosticTroubleCodeDatabase(Context context)
    {
        super(context, LOCAL_DATABASE_NAME, null, VERSION);
    }

    @Override
    public final void onCreate(SQLiteDatabase db)
    {
        db.rawQuery("CREATE TABLE `trouble_code` (\n" +
                            "  `DTC` char(5) NOT NULL,\n" +
                            "  `MAKE` varchar(64) NOT NULL,\n" +
                            "  `DESCRIPTION` varchar(255) NOT NULL,\n" +
                            "  `SECOND_MAKE` varchar(64) NOT NULL,\n" +
                            "  `SECOND_DESCRIPTION` varchar(1024) NOT NULL,\n" +
                            "  `JURISDICTION` int(1) NOT NULL,\n" +
                            "  `TYPE` varchar(255) NOT NULL,\n" +
                            "  `LOCATION` varchar(128) NOT NULL,\n" +
                            "  `NOTES` varchar(1024) NOT NULL,\n" +
                            "  `CAUSE` varchar(512) NOT NULL,\n" +
                            "  PRIMARY KEY (`DTC`)\n" +
                            ") ENGINE=MyISAM DEFAULT CHARSET=latin1;", null);
        db.rawQuery("", null);
    }

    @Override
    public final void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {

    }


    public List<DiagnosticTroubleCode> getDTCs(int... codes)
    {
        StringBuilder query = new StringBuilder("SELECT * FROM `trouble_code` WHERE `DTC` = '");
        for (int code : codes)
        {
            query.append(code)
                 .append("' OR `DTC` = '");
        }
        String sql = query.substring(0, query.length() - 13);
        return null;
    }
}
