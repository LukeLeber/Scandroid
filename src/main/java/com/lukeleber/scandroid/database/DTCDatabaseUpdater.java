// This file is protected under the KILLGPL.
// For more information, visit http://www.lukeleber.github.io/KILLGPL.html
//
// Copyright (c) Luke Leber <LukeLeber@gmail.com>

package com.lukeleber.scandroid.database;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;

import com.lukeleber.app.EnhancedActivity;
import com.lukeleber.content.BroadcastListener;
import com.lukeleber.database.ScopedSQLiteDatabase;
import com.lukeleber.scandroid.BuildConfig;
import com.lukeleber.scandroid.sae.j2012.DiagnosticTroubleCode;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DTCDatabaseUpdater
        extends EnhancedActivity
{

    /// The remote database (stored in my google+ drive)
//    private final static String REMOTE_FILE = "https://docs.google.com/uc?export=download&id=0B_rzRXBGnSlFRURVeU8wY2lRM1k";
    private final static String REMOTE_FILE = "http://localhost/dtc_database.sqlite";

    /// The name of the local copy of the DTC database
    private final static String LOCAL_DTC_DATABASE = "dtc_database.sqlite";

    /// Are we currently initializing?
    private volatile boolean initializing = false;

    private void onDownloadFailed()
    {
        /// We had better be initializing still...
        if (BuildConfig.DEBUG && !initializing)
        {
            throw new AssertionError("initializing != true");
        }
        super.shortToast("Failed to fetch DTC database - try again later.");
        finish();
    }

    private void onDownloadFinished(String file)
    {
        /// We had better be initializing still...
        if (BuildConfig.DEBUG && !initializing)
        {
            throw new AssertionError("initializing != true");
        }
        super.shortToast("DTC database obtained...copying to internal storage...");
        File localFile = new File(URI.create(file));
        try (InputStream is = new FileInputStream(localFile);
             OutputStream os = new FileOutputStream(super.getDatabasePath(LOCAL_DTC_DATABASE)))
        {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0)
            {
                os.write(buffer, 0, length);
            }
            super.shortToast("DTC database successfully installed!");
        }
        catch (IOException ioe)
        {
            /// Should never fail...
        }
        localFile.delete();
        finish();
    }

    @Override
    public void onCreate(Bundle sis)
    {
        super.onCreate(sis);
        if (BuildConfig.DEBUG && initializing)
        {
            throw new AssertionError("initializing != false");
        }
        initializing = true;
        new Thread(new Runnable(){

            @Override
            public void run()
            {
                URL url = null;
                try
                {
                    url = new URL(REMOTE_FILE);
                }
                catch(MalformedURLException e)
                {
                    e.printStackTrace();
                }
                try
                {
                    if(url != null)
                    {
                        HttpURLConnection c = (HttpURLConnection) url.openConnection();
                    com.lukeleber.io.StreamCopy.copyInputStream(c.getInputStream(),
                            new FileOutputStream(getDatabasePath(LOCAL_DTC_DATABASE)));
                    }
                }
                catch(Exception ee)
                {
                    ee.printStackTrace();
                }
                System.out.println("C!");

            }
        }).start();
    }
}
