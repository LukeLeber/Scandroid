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

package com.lukeleber.scandroid.gui;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.util.Log;

import com.lukeleber.scandroid.BuildConfig;
import com.lukeleber.scandroid.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * An interface that is to be implemented by classes that handle the completion of I/O interface
 * detections.
 */
/*package*/ interface InterfaceDetectorListener
{
    /**
     * Invoked when the detection process completes normally
     *
     * @param modes
     *         all available I/O interfaces that were found
     */
    void onDetectionComplete(Collection<SelectInterface.InterfaceSelectionMode> modes);
}

/**
 * A package-private helper base class to detect which I/O interfaces are available for use.
 *
 * @internal Maintainer - please extend this class when introducing a new type of I/O interface.
 */
/*package*/ abstract class Detector
        implements Callable<Boolean>
{
    /// The calling context
    protected final Context context;

    /**
     * Constructs a {@link com.lukeleber.scandroid.gui.Detector}
     *
     * @param context
     *         the calling context
     */
    protected Detector(Context context)
    {
        this.context = context;
    }

    /**
     * Retrieves the {@link killgpl.scandroid.gui.SelectInterface.InterfaceSelectionMode} that this
     * {@link com.lukeleber.scandroid.gui.Detector} detects
     *
     * @return the {@link killgpl.scandroid.gui.SelectInterface.InterfaceSelectionMode} that this
     * {@link com.lukeleber.scandroid.gui.Detector} detects
     */
    /*package*/
    abstract SelectInterface.InterfaceSelectionMode getMode();
}

/*package*/ class USBDetector
        extends Detector
{
    /**
     * Constructs a {@link killgpl.scandroid.gui.USBDetector}
     *
     * @param context
     *         the calling context
     */
    /*package*/ USBDetector(Context context)
    {
        super(context);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    /*package*/ SelectInterface.InterfaceSelectionMode getMode()
    {
        return SelectInterface.InterfaceSelectionMode.USB;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final Boolean call()
            throws
            Exception
    {
        UsbManager manager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        for (Map.Entry<String, UsbDevice> e : manager.getDeviceList()
                                                     .entrySet())
        {
            if (e.getKey()
                 .equals(context.getString(R.string.usb_device_name)))
            {
                return true;
            }
        }
        return false;
    }
}

/*package*/ class WifiDetector
        extends Detector
{
    /**
     * Constructs a {@link killgpl.scandroid.gui.WifiDetector}
     *
     * @param context
     *         the calling context
     */
    /*package*/ WifiDetector(Context context)
    {
        super(context);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    /*package*/ SelectInterface.InterfaceSelectionMode getMode()
    {
        return SelectInterface.InterfaceSelectionMode.WIFI;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final Boolean call()
    {
        WifiManager manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (manager != null)
        {
            for (ScanResult result : manager.getScanResults())
            {
                if (result.SSID.equals(context.getString(R.string.wifi_device_ssid)))
                {
                    return true;
                }
            }
        }
        return false;
    }
}

/*package*/ class BluetoothDetector
        extends Detector
{
    /**
     * Constructs a {@link killgpl.scandroid.gui.BluetoothDetector}
     *
     * @param context
     *         the calling context
     */
    /*package*/ BluetoothDetector(Context context)
    {
        super(context);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    /*package*/ SelectInterface.InterfaceSelectionMode getMode()
    {
        return SelectInterface.InterfaceSelectionMode.BLUETOOTH;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final Boolean call()
    {
        BluetoothManager manager = (BluetoothManager) context.getSystemService(
                Context.BLUETOOTH_SERVICE);
        if (manager != null)
        {
            for (BluetoothDevice device : manager.getAdapter()
                                                 .getBondedDevices())
            {
                if (device.getName()
                          .equals(context.getString(R.string.bluetooth_device_name)))
                {
                    return true;
                }
            }
        }
        return false;
    }
}

/// <-- @internal Maintainer - please add new derived Detector types below this line


/// @internal Maintainer - please add new derived Detector types above this line -->

/**
 * An asynchronous task that runs the detection routine for all registered types of I/O interfaces
 * and forwards the result to a {@link killgpl.scandroid.gui.InterfaceDetectorListener}.
 */
/*package*/ class InterfaceDetector
        extends
        AsyncTask<Void, Void, Collection<SelectInterface.InterfaceSelectionMode>>
{

    private final static String TAG = InterfaceDetector.class.getName();

    /// The calling {@link android.content.Context}
    private final Context context;

    /// The {@link killgpl.activity_scandroid.gui.InterfaceDetectorListener} to forward results to
    private final InterfaceDetectorListener listener;

    /**
     * Constructs an {@link killgpl.scandroid.gui.InterfaceDetector} with the provided calling
     * {@link android.content.Context} and {@link killgpl.scandroid.gui.InterfaceDetectorListener}.
     *
     * @param context
     *         the calling {@link android.content.Context}
     * @param listener
     *         the {@link killgpl.scandroid.gui.InterfaceDetectorListener} to forward results to
     */
    /*package*/ InterfaceDetector(Context context, InterfaceDetectorListener listener)
    {
        this.context = context;
        this.listener = listener;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Collection<SelectInterface.InterfaceSelectionMode> doInBackground(Void... params)
    {
        Detector[] detectors = new Detector[]
                {
                        new USBDetector(context),       // Since 1.0
                        new WifiDetector(context),      // Since 1.0
                        new BluetoothDetector(context)  // Since 1.0
/// <--
///     @internal Maintainer, please add new detector objects below this line

///     @internal Maintainer, please add new detector objects above this line
/// -->
                };
        List<SelectInterface.InterfaceSelectionMode> interfaces = new ArrayList<>();
        for (Detector detector : detectors)
        {
            try
            {
                if (detector.call())
                {
                    interfaces.add(detector.getMode());
                }
            }
            catch (Exception e)
            {
                if (BuildConfig.DEBUG)
                {
                    Log.e(TAG, "Error executing interface detector", e);
                }
            }
        }
        return interfaces;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected final void onPostExecute(Collection<SelectInterface.InterfaceSelectionMode> modes)
    {
        listener.onDetectionComplete(modes);
    }
}
