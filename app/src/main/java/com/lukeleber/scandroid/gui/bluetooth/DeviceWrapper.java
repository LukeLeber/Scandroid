/*
 * This file is protected under the KILLGPL.
 * For more information visit <insert_valid_link_to_killgpl_here>
 * <p/>
 * Copyright (c) Luke A. Leber <LukeLeber@gmail.com> 2014
 */

package com.lukeleber.scandroid.gui.bluetooth;

import android.bluetooth.BluetoothDevice;

/**
 * A "pretty" wrapper around a {@link android.bluetooth.BluetoothDevice} that overrides {@link
 * Object#toString()} in order to provide a more user-friendly representation of said device. <p>The
 * string has the form of "DEVICE_NAME (DEVICE_ADDRESS)", so for example: "OBDII
 * (01:23:45:67:89::00)".</p>
 */
public class DeviceWrapper
{
    /// The {@link BluetoothDevice} that is being wrapped
    private final BluetoothDevice device;

    /**
     * Constructs a {@link com.lukeleber.scandroid.gui.bluetooth.DeviceWrapper} with the provided
     * {@link android.bluetooth.BluetoothDevice}
     *
     * @param device
     *         the {@link android.bluetooth.BluetoothDevice} to wrap
     */
    public DeviceWrapper(BluetoothDevice device)
    {
        this.device = device;
    }

    /**
     * Unwraps and retrieves the underlying {@link android.bluetooth.BluetoothDevice}
     *
     * @return the underlying {@link android.bluetooth.BluetoothDevice}
     */
    public BluetoothDevice unwrap()
    {
        return device;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String toString()
    {
        return device.getName() + " (" + device.getAddress() + ")";
    }
}
