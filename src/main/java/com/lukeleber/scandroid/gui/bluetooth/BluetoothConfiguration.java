// This file is protected under the KILLGPL.
// For more information, visit http://www.lukeleber.github.io/KILLGPL.html
//
// Copyright (c) Luke Leber <LukeLeber@gmail.com>

package com.lukeleber.scandroid.gui.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.lukeleber.app.EnhancedActivity;
import com.lukeleber.bluetooth.BluetoothEnabler;
import com.lukeleber.content.BroadcastListener;
import com.lukeleber.scandroid.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnItemClick;

/**
 * <p>The {@link BluetoothConfiguration} Activity presents the
 * user with a means by which to choose the bluetooth device that is to act as a bridge between the
 * vehicle's onboard systems and the Scandroid API.  The default device for Scandroid is the ELM327
 * interpreter which usually has the device name "OBDII", although that is not set in stone.</p>
 */
public class BluetoothConfiguration
        extends
        EnhancedActivity
        implements
        BroadcastListener,
        BluetoothEnabler.Handler

{

    /// The "Back" {@link Button}
    @InjectView(R.id.backButton)
    transient Button backButton;
    /// The "Connect" {@link Button}
    @InjectView(R.id.connectButton)
    transient Button connectButton;
    /// The {@link ListView} that holds all of the {@link BluetoothDevice devices} in range
    @InjectView(R.id.deviceList)
    transient ListView devices;
    /// Again, possibly a hack?  I must not be using list-views correctly...
    private int pos;

    /**
     * Hack?  Enables the connect button and updates the selected item in the list view.
     */
    @OnItemClick(R.id.deviceList)
    void onItemClicked(int position)
    {
        pos = position;
        connectButton.setEnabled(true);
    }

    /**
     * Fired when the user clicks the back button
     */
    @OnClick(R.id.backButton)
    void onBackClicked()
    {
        super.onBackPressed();
    }

    /**
     * Fired when the user clicks the connect button, this starts a new activity
     */
    @OnClick(R.id.connectButton)
    void onConnectClicked()
    {
        /// Turn off device discovery
        onScanCancelled();

        /// FIXME: Research the proper way to get a UUID...sucks not having internet access!
        BluetoothDevice selectedDevice = ((DeviceWrapper) devices.getItemAtPosition(pos)).unwrap();
        /// TODO: Replace NaiveTest with user's testing GUI of choice.
//        Intent intent = new Intent(BluetoothConfiguration.this, NaiveTest.class);
//        intent.putExtra("dev", selectedDevice);
//        intent.putExtra("uuid", selectedDevice.getUuids()[0]);
//        startActivity(intent);
    }

    /**
     * Fired when the user clicks the cancel button
     */
    @OnClick(R.id.cancelScan)
    void onScanCancelled()
    {
        BluetoothManager manager = (BluetoothManager)
                BluetoothConfiguration.this.getSystemService(BLUETOOTH_SERVICE);
        if (manager != null)
        {
            manager.getAdapter()
                   .cancelDiscovery();
        }
    }

    /**
     * Fired when the user clicks the scan button
     */
    @OnClick(R.id.startScanButton)
    void onScanStarted()
    {
        BluetoothManager manager = (BluetoothManager)
                BluetoothConfiguration.this.getSystemService(BLUETOOTH_SERVICE);
        if (manager != null)
        {
            manager.getAdapter()
                   .startDiscovery();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_bluetooth_configuration);
        super.addBroadcastReceiver(this, new IntentFilter(BluetoothDevice.ACTION_FOUND));
        ButterKnife.inject(this);
        BluetoothEnabler.requestBluetoothEnableIfDisabled(this, this);
        populateDevices();
    }

    /**
     * Initially populates the device {@link android.widget.ListView}
     */
    private void populateDevices()
    {
        List<DeviceWrapper> population = new ArrayList<>();
        BluetoothManager manager = (BluetoothManager) super.getSystemService(BLUETOOTH_SERVICE);
        if (manager != null)
        {
            for (BluetoothDevice device : manager.getAdapter()
                                                 .getBondedDevices())
            {
                population.add(new DeviceWrapper(device));
            }
        }
        devices.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, population));
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        onScanCancelled();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.bluetooth_configuration, menu);
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        return id == R.id.action_settings || super.onOptionsItemSelected(item);
    }

    @Override
    public final void onBluetoothEnabled()
    {
        /** no-op */
    }

    @Override
    public final void onBluetoothDisabled()
    {
        onBackPressed();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void onReceive(Context context, Intent intent)
    {
        String action = intent.getAction();
        /// Adds a device to the devices adapter if not already present
        if (BluetoothDevice.ACTION_FOUND.equals(action))
        {
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            @SuppressWarnings("unchecked")
            ArrayAdapter<DeviceWrapper> adapter =
                    (ArrayAdapter<DeviceWrapper>) devices.getAdapter();
            for (int i = 0;
                 i < adapter.getCount();
                 ++i)
            {
                if (adapter.getItem(i)
                           .unwrap()
                           .equals(device))
                {
                    return;
                }
            }
            adapter.add(new DeviceWrapper((BluetoothDevice)
                                                  intent.getParcelableExtra(
                                                          BluetoothDevice.EXTRA_DEVICE)));
        }
    }
}
