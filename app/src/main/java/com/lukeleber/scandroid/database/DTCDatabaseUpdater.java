// This file is protected under the KILLGPL.
// For more information, visit http://www.lukeleber.github.io/KILLGPL.html
//
// Copyright (c) Luke Leber <LukeLeber@gmail.com>
//
// "This software is provided freely under a single condition.  You can't force anyone
// that uses this software to release any source code.  It is their decision alone.
// Other than that, anything goes.
//
// PS. If you like this work, please hire me!  I'm uneducated (no CS degree)
// and as such it's borderline impossible to get into the industry.  My current
// career is absolute hell and I am trapped in it.  I leave this message here
// in the vain hope that someone, somewhere might read this and give me the
// opportunity to join their association as a programmer."
//
// Cheers,
// Luke

package com.lukeleber.scandroid.database;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Toast;

import com.lukeleber.app.EnhancedActivity;
import com.lukeleber.content.BroadcastListener;
import com.lukeleber.scandroid.BuildConfig;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
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

    /// The ID of the current download request
    private long id;

    private void onDownloadFailed()
    {
        /// We had better be initializing still...
        if(BuildConfig.DEBUG && !initializing) throw new AssertionError("initializing != true");
        super.shortToast("Failed to fetch DTC database - try again later.");
        finish();
    }

    private void onDownloadFinished(String file)
    {
        /// We had better be initializing still...
        if(BuildConfig.DEBUG && !initializing) throw new AssertionError("initializing != true");
        super.shortToast("DTC database obtained...copying to internal storage...");
        File localFile = new File(URI.create(file));
        try(InputStream is = new FileInputStream(localFile);
            OutputStream os = new FileOutputStream(super.getDatabasePath(LOCAL_DTC_DATABASE)))
        {
            byte[] buffer = new byte[1024];
            int length;
            while((length = is.read(buffer)) > 0)
            {
                os.write(buffer, 0, length);
            }
            super.shortToast("DTC database successfully installed!");
        }
        catch(IOException ioe)
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
        if(BuildConfig.DEBUG && initializing) throw new AssertionError("initializing != false");
        initializing = true;
//        if(!getDatabasePath(LOCAL_DTC_DATABASE).exists())
        {
            final DownloadManager dm = (DownloadManager)super.getSystemService(DOWNLOAD_SERVICE);
            super.addBroadcastReceiver(
                new BroadcastListener()
                {
                    @Override
                    public void onReceive(final Context context, final Intent intent)
                    {
                        System.out.println("onReceive");
                        if(intent.getAction() == DownloadManager.ACTION_DOWNLOAD_COMPLETE)
                        {
                            Cursor c = dm.query(new DownloadManager.Query().setFilterById(id));
                            if (c.moveToFirst())
                            {
                                switch(c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS)))
                                {
                                    case DownloadManager.STATUS_SUCCESSFUL:
                                        onDownloadFinished(c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI)));
                                        break;
                                    case DownloadManager.STATUS_FAILED:
                                        onDownloadFailed();
                                        break;
                                }
                            }
                            c.close();
                        }
                        initializing = false;
                    }
                }, new IntentFilter(
                        DownloadManager.ACTION_DOWNLOAD_COMPLETE));
            this.id = dm.enqueue(
                    new DownloadManager.Request(
                        Uri.parse(REMOTE_FILE))
                            .setDestinationInExternalPublicDir(
                                Environment.getExternalStorageDirectory() +
                                "/Android/data/" +
                                getPackageName() +
                                "/files/",
                                LOCAL_DTC_DATABASE));
            System.out.println("Enqueued");
        }
    }
}
