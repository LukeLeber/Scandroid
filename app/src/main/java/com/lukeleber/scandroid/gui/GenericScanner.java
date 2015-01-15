/*
 * This file is protected under the KILLGPL.
 * For more information visit <insert_valid_link_to_killgpl_here>
 * <p/>
 * Copyright (c) Luke A. Leber <LukeLeber@gmail.com> 2014
 */

package com.lukeleber.scandroid.gui;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.lukeleber.app.ActivityResultListener;
import com.lukeleber.app.EnhancedActivity;
import com.lukeleber.scandroid.BuildConfig;
import com.lukeleber.scandroid.Globals;
import com.lukeleber.scandroid.R;
import com.lukeleber.scandroid.gui.fragments.DiagnosticTroubleCodeDisplay;
import com.lukeleber.scandroid.gui.fragments.FreezeFrameRecords;
import com.lukeleber.scandroid.gui.fragments.LiveDatastream;
import com.lukeleber.scandroid.gui.fragments.ResetDiagnosticInformation;
import com.lukeleber.scandroid.gui.fragments.ServiceFragment;
import com.lukeleber.scandroid.gui.fragments.UnsupportedService;
import com.lukeleber.scandroid.interpreter.ConfigurationRequest;
import com.lukeleber.scandroid.interpreter.FailureCode;
import com.lukeleber.scandroid.interpreter.Handler;
import com.lukeleber.scandroid.interpreter.Interpreter;
import com.lukeleber.scandroid.interpreter.ResponseListener;
import com.lukeleber.scandroid.interpreter.ServiceRequest;
import com.lukeleber.scandroid.interpreter.elm327.Constants;
import com.lukeleber.scandroid.interpreter.elm327.ELM327;
import com.lukeleber.scandroid.interpreter.elm327.OpCode;
import com.lukeleber.scandroid.interpreter.elm327.Protocol;
import com.lukeleber.scandroid.io.ScandroidIOException;
import com.lukeleber.scandroid.io.bluetooth.BluetoothInterface;
import com.lukeleber.scandroid.sae.Profile;
import com.lukeleber.scandroid.sae.j1979.Service;
import com.lukeleber.scandroid.sae.detail.AppendixA;

import java.io.IOException;

/**
 * A scan tool implementation that supports the bare minimum required by SAE-J1979.
 *
 */
public class GenericScanner
        extends EnhancedActivity
        implements InterpreterHost
{

    /// @internal tag for debug logging
    private final static String TAG = GenericScanner.class.getName();

    /// Each service exists in its own self contained UI fragment class
    @SuppressWarnings("unchecked")
    public final static Class<? extends ServiceFragment>[] services = new Class[]
    {
        LiveDatastream.class,
        FreezeFrameRecords.class,
        DiagnosticTroubleCodeDisplay.class,
        ResetDiagnosticInformation.class
    };

    private final static class InitListener implements ResponseListener<String>
    {
        private final Handler<?> handler;

        InitListener(Handler<?> handler)
        {
            this.handler = handler;
        }

        @Override
        public void onSuccess(String message)
        {
            message = message.replace(Constants.ELM327_SEARCHING_FOR_PROTOCOL + (char)13, "");
            if(!message.equals(Constants.ELM327_UNABLE_TO_CONNECT + (char)13))
            {
                handler.onResponse(null);
            }
            else
            {
                onFailure(FailureCode.CONDITIONS_NOT_CORRECT);
            }
        }

        @Override
        public void onFailure(FailureCode code)
        {
            handler.onFailure(code);
        }
    }

    /**
     * Attempts to start a "Generic" OBDII scan tool
     *
     * @param context
     *         the parent {@link android.app.Activity}
     */
    public static void startGenericScanner(final Scandroid context)
    {
        try
        {
            /// By default, we use a bluetooth-enabled ELM327 using default settings
            final ELM327 interpreter
                    = new ELM327(BluetoothInterface.getDefault(context));
            /// Add a connection listener
            interpreter.addConnectionListener(new Interpreter.ConnectionListener()
            {
                @Override
                public void onConnected()
                {
                    /// General configuration

                    /// First, reset the interface so we have a clean slate
                    interpreter.sendRequest(new ConfigurationRequest<String, String>(
                            OpCode.ELM327_RESET_ALL));

                    /// Tell the ELM327 that we don't want it to echo back everything we send it
                    /// This helps to mitigate I/O traffic
                    interpreter.sendRequest(
                            new ConfigurationRequest<String, String>(OpCode.ELM327_ECHO_OFF));

                    /// Tell the ELM327 to eat any whitespace between data bytes
                    /// This helps to mitigate I/O traffic and eases response parsing
                    interpreter.sendRequest(
                            new ConfigurationRequest<String, String>(
                                    OpCode.ELM327_OBD_SPACES_OFF));


                    context.startActivity(new Intent(context,
                            GenericScanner.class));
               }
            });

            /// Add an error listener
            interpreter.addErrorListener(new Interpreter.ErrorListener()
            {
                @Override
                public void onError(final Throwable error)
                {
                    context.handler.post(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                             Toast.makeText(context, "I/O Error - Verify that the interpreter " +
                                     "is securely plugged into the diagnostic connector and " +
                                     "that the android device is within range.",
                                     Toast.LENGTH_LONG).show();
                            interpreter.stop();
                        }
                    });
                }
            });

            {
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
                        if (BuildConfig.DEBUG)
                        {
                            Log.e(TAG, ioe.getMessage(), ioe);
                        }
                    }
                }
            }

            /// Start the new interpreter...
            interpreter.start();
        }
        /// Catch any I/O errors that are not associated with the interpreter itself
        /// These error conditions are usually configuration issues with the android device
        /// itself or its proximity relative to the interpreter.  To resolve, correctly
        /// configure the device and/or move closer to the vehicle.
        catch (ScandroidIOException sioe)
        {
            Toast.makeText(context, sioe.getCode().what(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed()
    {
        getInterpreter().stop();
        Globals.setInterpreter(getApplicationContext(), null);
        super.onBackPressed();
        super.finish();
    }

    /**
     * {@inheritDoc}
     *
     */
    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generic_scanner);
        super.startActivityForResult(new Intent(this, ProtocolSearch.class), super.addResultListener(new ActivityResultListener()
        {
            @Override
            public void onActivityResult(int resultCode, final Intent data)
            {
                final Interpreter<String> interpreter = Globals.getInterpreter(getApplicationContext());
                switch(resultCode)
                {
                    case ProtocolSearch.PROTOCOL_FOUND:
                        Profile.createProfile(interpreter,
                                (Protocol)data.getSerializableExtra(ProtocolSearch.PROTOCOL_RESULT),
                                new Handler<Profile>()
                                {
                                    /// Upon success, launch the scanner activity
                                    @Override
                                    public void onResponse(Profile value)
                                    {
                                        Globals.setProfile(
                                                getApplicationContext(),
                                                value);

                                        /// Everything checks out
                                        /// Start the scan tool
                                        ((ViewPager) findViewById(R.id.pager)).setAdapter(
                                                new FragmentStatePagerAdapter(getFragmentManager())
                                                {
                                                    @Override
                                                    public Fragment getItem(int position)
                                                    {
                                                        Service service = Service.values()[position];
                                                        if(!getProfile().isServiceSupported(service) || position >= services.length)
                                                        {
                                                            /// TODO: Remove second part of conditional upon completing all service fragments
                                                            return new UnsupportedService();
                                                        }
                                                        else
                                                        {
                                                            try
                                                            {
                                                                return services[position].newInstance();
                                                            }
                                                            catch (InstantiationException | IllegalAccessException e)
                                                            {
                                                                throw new IllegalStateException(services[position].getName() +
                                                                        " does not have a public default constructor.");
                                                            }
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
                                                });
                                    }

                                    @Override
                                    public void onFailure(FailureCode code)
                                    {
                                        /// Error creating profile
                                    }
                                }
                                             );
                        break;
                    case ProtocolSearch.SEARCH_ABORTED:
                    case ProtocolSearch.FATAL_ERROR:
                        System.out.println("Search Aborted");
                        interpreter.stop();
                        Globals.setInterpreter(getApplicationContext(),  null);
                        finish();
                        break;

                }
            }
        }));
    }


    /**
     * {@inheritDoc}
     *
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.generic_scanner, menu);
        return true;
    }

    /**
     * {@inheritDoc}
     *
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        return item.getItemId() == R.id.action_settings || super.onOptionsItemSelected(item);
    }

    /**
     * {@inheritDoc}
     *
     */
    @Override
    public Interpreter<String> getInterpreter()
    {
        return Globals.getInterpreter(getApplicationContext());
    }

    /**
     * {@inheritDoc}
     *
     */
    @Override
    public Profile getProfile()
    {
        return Globals.getProfile(getApplicationContext());
    }
}
