/*
 * This file is protected under the KILLGPL.
 * For more information visit <insert_valid_link_to_killgpl_here>
 * <p/>
 * Copyright (c) Luke A. Leber <LukeLeber@gmail.com> 2014
 */

package com.lukeleber.scandroid.gui;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import com.lukeleber.app.EnhancedActivity;
import com.lukeleber.bluetooth.BluetoothEnabler;
import com.lukeleber.scandroid.R;
import com.lukeleber.scandroid.gui.bluetooth.BluetoothConfiguration;

import java.util.Collection;
import java.util.Iterator;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * The start of the "scan tool" GUI chain.  This GUI enables users to choose between various types
 * of I/O interfaces that may be used to communicate with a vehicle.  Each I/O interface utilizes
 * hardware resources on the device and therefore all interfaces may not be supported on all
 * devices.  Typically, this is the only configuration menu that is shown to the user when in
 * "Generic Mode", but subsequent configuration GUIs may be required for "Expert Mode".
 */
public class SelectInterface
        extends EnhancedActivity
        implements InterfaceDetectorListener,
                   BluetoothEnabler.Handler
{

    private final static String TAG = SelectInterface.class.getName();
    @InjectView(R.id.radioButton)
    RadioButton auto;
    @InjectView(R.id.radioButton2)
    RadioButton usb;
    @InjectView(R.id.radioButton3)
    RadioButton wifi;
    @InjectView(R.id.radioButton4)
    RadioButton bluetooth;
    @InjectView(R.id.backButton)
    Button backButton;
    @InjectView(R.id.connectButton)
    Button continueButton;
    private transient Iterator<InterfaceSelectionMode> modeIterator;
    private ConfigurationMode mode;

    @Override
    public void onDetectionComplete(Collection<InterfaceSelectionMode> modes)
    {
        this.modeIterator = modes.iterator();
        Iterator<InterfaceSelectionMode> iter = modes.iterator();
        if (iter.hasNext())
        {
            switch (iter.next())
            {
                case BLUETOOTH:
                    BluetoothEnabler.requestBluetoothEnableIfDisabled(this, this);
                    break;
                case WIFI:
                    // TODO: wifi interface
                    break;
                case USB:
                    // TODO: usb interface
                    break;
                default:
            }
        }
        else
        {
            onAutoConfigurationFailure();
        }
    }

    private void onAutoConfigurationFailure()
    {
        Toast.makeText(this, "No communication, is the key on?", Toast.LENGTH_SHORT)
             .show();
        // TODO: Toast?
    }

    @Override
    public void onBluetoothEnabled()
    {
/*        CommunicationInterface comm = DefaultBluetoothConfiguration.getDefault(this);
        if(comm != null)
        {
            this.modeIterator = null;
            /// TODO: replace with spi
            new ELM327(comm).execute();
        }
        else
        {
            onBluetoothDisabled();
        }*/
    }

    @Override
    public void onBluetoothDisabled()
    {
        if (modeIterator.hasNext())
        {
            switch (modeIterator.next())
            {
                case WIFI:
                    // TODO: wifi interface
                    break;
                case USB:
                    // TODO: usb interface
                    break;
                default:
            }
        }
        else
        {
            onAutoConfigurationFailure();
        }
    }

    @OnClick(R.id.backButton)
    void onBackClicked()
    {
        super.onBackPressed();
    }

    @OnClick(R.id.connectButton)
    void onContinueClicked()
    {
        if (auto.isChecked())
        {
            new InterfaceDetector(SelectInterface.this, SelectInterface.this).execute();
        }
        else if (bluetooth.isChecked())
        {
            startActivity(new Intent(SelectInterface.this, BluetoothConfiguration.class));
        }
        else if (wifi.isChecked())
        {
            // todo: implement wifi
        }
        else if (usb.isChecked())
        {
            // todo: implement usb
        }
    }

    /**
     * {@inheritDoc}
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_interface);
        this.mode = (ConfigurationMode) super.getIntent()
                                             .getSerializableExtra(Scandroid.Extras.SCANNER_MODE);
        ButterKnife.inject(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.scan_tool, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings)
        {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /*package*/ static enum InterfaceSelectionMode
    {
        AUTO,
        USB,
        WIFI,
        BLUETOOTH
    }
}
