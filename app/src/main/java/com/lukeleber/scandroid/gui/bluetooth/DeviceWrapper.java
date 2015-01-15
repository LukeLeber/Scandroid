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
