/*
 * This file is protected under the KILLGPL.
 * For more information visit <insert_valid_link_to_killgpl_here>
 * <p/>
 * Copyright (c) Luke A. Leber <LukeLeber@gmail.com> 2014
 */

package com.lukeleber.scandroid.gui;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;

import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.IOException;

import butterknife.ButterKnife;
import butterknife.InjectView;
import com.lukeleber.scandroid.BuildConfig;
import com.lukeleber.scandroid.R;
import com.lukeleber.scandroid.Globals;
import com.lukeleber.scandroid.gui.fragments.FreezeFrameRecords;
import com.lukeleber.scandroid.gui.fragments.LiveDatastream;
import com.lukeleber.scandroid.gui.fragments.ResetDiagnosticInformation;
import com.lukeleber.scandroid.interpreter.ConfigurationRequest;
import com.lukeleber.scandroid.interpreter.FailureCode;
import com.lukeleber.scandroid.interpreter.Handler;
import com.lukeleber.scandroid.interpreter.Interpreter;
import com.lukeleber.scandroid.interpreter.ServiceRequest;
import com.lukeleber.scandroid.interpreter.elm327.ELM327;
import com.lukeleber.scandroid.interpreter.elm327.OpCode;
import com.lukeleber.scandroid.io.ScandroidIOException;
import com.lukeleber.scandroid.io.bluetooth.BluetoothInterface;
import com.lukeleber.scandroid.sae.PIDSupport;
import com.lukeleber.scandroid.sae.Profile;
import com.lukeleber.scandroid.sae.Service;
import com.lukeleber.scandroid.sae.detail.AppendixA;

public class GenericScanner<T, U>
        extends Activity
        implements InterpreterHost<T, U>
{

    private final static ActionBar.TabListener waste_of_space_and_clocks = new ActionBar.TabListener()
    {
        @Override
        public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft)
        {

        }

        @Override
        public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft)
        {

        }

        @Override
        public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft)
        {

        }
    };

    private final static String TAG = GenericScanner.class.getName();

    /**
     * Attempts to start a "Generic" OBDII scan tool
     *
     * @param context the parent {@link android.app.Activity}
     *
     * @param errorHandler the {@link killgpl.scandroid.interpreter.Interpreter.ErrorHandler} to
     *                     receive any fatal errors on
     *
     */
    public static void startGenericScanner(final Scandroid context,
                                           Interpreter.ErrorHandler errorHandler)
    {
        try
        {

            Interpreter<String> interpreter = new ELM327(BluetoothInterface.getDefault(context))
            {
                /// Invoked when a successful connection to the ELM327 is made
                @Override
                protected final void onConnected()
                {
                    context.handler.post(
                        new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                Toast.makeText(
                                        context, "connected to interpreter...",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    );
                }

                /// Performs any initialization procedures for this session
                @Override
                protected final void init()
                {
                    /// General configuration options
                    /// Tell the ELM327 that we don't want it to echo back everything we send it
                    /// This helps to mitigate I/O traffic
                    super.sendRequest(
                            new ConfigurationRequest<String, String>(OpCode.ELM327_ECHO_OFF));

                    /// Tell the ELM327 to eat any whitespace between data bytes
                    /// This helps to mitigate I/O traffic and eases response parsing
                    super.sendRequest(
                            new ConfigurationRequest<String, String>(
                                    OpCode.ELM327_OBD_SPACES_OFF));

                    /// Tell the ELM327 that the communication protocol is unknown
                    /// The ELM327 will then try each protocol until a match is found
                    ///super.sendRequest(
                    ///        new ConfigurationRequest<String, String>(
                    ///                OpCode.ELM327_OBD_SET_PROTOCOL, Protocol.AUTOMATIC.getID()));
                }

                /// Runs any post-initialization procedures
                @Override
                protected final void onInitialized()
                {
                    /// Quick check to ensure the onboard conditions are correct
                    /// All (?) OBDII compliant vehicles should support $1$0...
                    /// So if this request fails, assume the key is off (or there is a
                    /// problem in the vehicle's diagnostic system(s))
                    super.sendRequest(
                        new ServiceRequest<String, PIDSupport>(
                            Service.LIVE_DATASTREAM,
                            AppendixA.J1979_CHECK_PID_SUPPORT_1_TO_20,
                            new killgpl.scandroid.interpreter.Handler<PIDSupport>()
                    {
                        @Override
                        public void onResponse(PIDSupport value) {  }

                        @Override
                        public void onFailure(FailureCode code)
                        {
                            cancel(true);
                            Toast.makeText(context,
                                    "No communication -- is the key on?",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }));
                }
            };

            /// Since we have to store shared non-parcelables in global scope...
            /// run cleanup on an old one (if it exists)
            Interpreter<?> old = Globals.setInterpreter(context.getApplicationContext(),
                    interpreter);
            if (old != null)
            {
                try
                {
                    old.close();
                }
                catch (IOException ioe)
                {
                    if(BuildConfig.DEBUG)
                    {
                        Log.e(TAG, ioe.getMessage(), ioe);
                    }
                }
            }
            /// Start the new interpreter...
            interpreter.start(errorHandler);

            /// and attempt to make a profile
            Profile.createProfile(interpreter, new Handler<Profile>()
            {
                /// Upon success, launch the scanner activity
                @Override
                public void onResponse(Profile value)
                {
                    Globals.setProfile(context.getApplicationContext(), value);
                    Toast.makeText(context, "scandroid is ready to use", Toast.LENGTH_SHORT).show();
                    context.startActivity(new Intent(context, GenericScanner.class));
                }

                /// If something breaks...
                @Override
                public void onFailure(FailureCode code)
                {
                    switch(code)
                    {
                        case IO_ERROR:
                        case IO_LINK_ERROR:
                        case REQUEST_NOT_SUPPORTED:
                            Toast.makeText(context, "failed to create a profile for vehicle.", Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
            });
        }
        catch(ScandroidIOException sioe)
        {
            Toast.makeText(context, sioe.getCode().what(), Toast.LENGTH_SHORT).show();
        }
    }

    @InjectView(R.id.genericScannerProgressBar)
    ProgressBar progressBar;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide fragments for each of the
     * sections. We use a {@link FragmentPagerAdapter} derivative, which will keep every loaded
     * fragment in memory. If this becomes too memory intensive, it may be best to switch to a
     * {@link android.support.v13.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link android.support.v4.view.ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;



    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generic_scanner);
        ButterKnife.inject(this);
        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener()
        {
            @Override
            public void onPageSelected(int position)
            {
                actionBar.setSelectedNavigationItem(position);
            }
        });
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++)
        {
            actionBar.addTab(
                    actionBar.newTab()
                             .setText(mSectionsPagerAdapter.getPageTitle(i))
                             .setTabListener(waste_of_space_and_clocks)
                            );
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.generic_scanner, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        return id == R.id.action_settings || super.onOptionsItemSelected(item);
    }

    @Override
    public Interpreter<T> getInterpreter()
    {
        return Globals.getInterpreter(getApplicationContext());
    }

    @Override
    public Profile getProfile()
    {
        return Globals.getProfile(getApplicationContext());
    }

    public class SectionsPagerAdapter
           extends FragmentStatePagerAdapter
    {

        public SectionsPagerAdapter(FragmentManager fm)
        {
            super(fm);
        }

        @Override
        public Fragment getItem(int position)
        {
            if(position == 0)
            {
                return new LiveDatastream();
            }
            else if(position == 3)
            {
                return new ResetDiagnosticInformation();
            }
            else
            {
                return new FreezeFrameRecords();
            }
        }

        @Override
        public int getCount()
        {
            return Service.values().length;
        }

        @Override
        public CharSequence getPageTitle(int position)
        {
            return Service.values()[position].toString();
        }
    }
}
