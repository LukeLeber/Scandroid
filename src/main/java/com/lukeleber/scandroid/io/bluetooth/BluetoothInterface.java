// This file is protected under the KILLGPL.
// For more information, visit http://www.lukeleber.github.io/KILLGPL.html
//
// Copyright (c) Luke Leber <LukeLeber@gmail.com>

package com.lukeleber.scandroid.io.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.ParcelUuid;
import android.util.Log;

import com.lukeleber.scandroid.BuildConfig;
import com.lukeleber.scandroid.R;
import com.lukeleber.scandroid.io.CommunicationInterface;
import com.lukeleber.scandroid.io.ScandroidIOException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * An {@link com.lukeleber.scandroid.io.CommunicationInterface} that is implemented via Bluetooth.
 * <p/>
 * The interpreter hardware used to test this interface is the ELM327 IC paired with a bluetooth
 * adapter and the standard OBDII connector.  This hardware can be a (semi) easy DIY build or can be
 * ordered for a very reasonable price (~ $8.00 at the time of this documentation) pre-assembled
 * over the internet.  This interface supports all of the service modes and various parameter IDs
 * defined in the SAE-J1979 standard.  Whether or not a vehicle supports all services or PIDs is
 * dependent on the vehicle manufacturer.
 */
public class BluetoothInterface
        implements CommunicationInterface

{

    /// @internal tag for debug logging
    private final static String TAG = BluetoothInterface.class.getName();

    /// The {@link android.bluetooth.BluetoothSocket} to the remote device;
    private final BluetoothSocket socket;

    /// The {@link java.io.InputStream} from the remote device
    private final InputStream inputStream;

    /// The {@link java.io.OutputStream} to the remote device
    private final OutputStream outputStream;

    /**
     * Constructs a {@link BluetoothInterface} from the
     * provided {@link android.bluetooth.BluetoothDevice remote device} and {@link
     * android.os.ParcelUuid UUID}
     *
     * @param remoteDevice
     *         the remote {@link android.bluetooth.BluetoothDevice} to connect with
     * @param uuid
     *         the {@link android.os.ParcelUuid UUID} of the remote {@link
     *         android.bluetooth.BluetoothDevice} to connect with
     */
    public BluetoothInterface(BluetoothDevice remoteDevice, ParcelUuid uuid)
            throws
            IOException
    {
        this.socket = remoteDevice.createInsecureRfcommSocketToServiceRecord(uuid.getUuid());
        this.inputStream = socket.getInputStream();
        this.outputStream = socket.getOutputStream();
    }

    /**
     * <p>Attempts to retrieve a "default" BluetoothInterface.  More formally, this method searches
     * for a paired device with the name {@link com.lukeleber.scandroid.R.string#bluetooth_device_name}
     * and uses the first {@link android.os.ParcelUuid} available on said device (if such a device
     * exists in the first place).  Traditionally, the resource string is set to "OBDII", which by
     * the way is the default name for the ELM327 bluetooth device.</p> <p/> <p><b>It is the
     * caller's responsibility to ensure bluetooth is enabled.</b></p>
     *
     * @param context
     *         the current context
     *
     * @return the "default" BluetoothInterface
     *
     * @throws com.lukeleber.scandroid.io.ScandroidIOException
     *         if bluetooth is not enabled on this device, no remote device was found that matched
     *         the selection criteria, or if any other type of I/O error occurs
     */
    public static BluetoothInterface getDefault(Context context)
            throws
            ScandroidIOException
    {
        if (context.getPackageManager()
                   .checkPermission(android.Manifest.permission.BLUETOOTH,
                                    context.getPackageName()) == PackageManager.PERMISSION_GRANTED)
        {
            BluetoothManager manager
                    = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
            if (manager != null)
            {
                if (manager.getAdapter()
                           .isEnabled())
                {
                    String targetName = context.getString(R.string.bluetooth_device_name);
                    for (BluetoothDevice candidate : manager.getAdapter()
                                                            .getBondedDevices())
                    {
                        if (targetName.equals(candidate.getName()))
                        {
                            try
                            {
                                return new BluetoothInterface(candidate, candidate.getUuids()[0]);
                            }
                            catch (IOException ioe)
                            {
                                throw new ScandroidIOException(ioe,
                                                               ScandroidIOException.ExceptionCode.UNDOCUMENTED_BLUETOOTH_ERROR);
                            }
                        }
                    }
                    throw new ScandroidIOException(
                            ScandroidIOException.ExceptionCode.BLUETOOTH_DEVICE_NOT_AVAILABLE);
                }
                throw new ScandroidIOException(
                        ScandroidIOException.ExceptionCode.BLUETOOTH_NOT_ENABLED);
            }
            throw new ScandroidIOException(
                    ScandroidIOException.ExceptionCode.BLUETOOTH_UNSUPPORTED);
        }
        throw new ScandroidIOException(ScandroidIOException.ExceptionCode.BLUETOOTH_NOT_PERMITTED);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final OutputStream getOutputStream()
    {
        return outputStream;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final InputStream getInputStream()
    {
        return inputStream;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void connect()
            throws
            IOException
    {
        socket.connect();

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close()
            throws
            IOException
    {
        try
        {
            socket.close();
        }
        catch (IOException ioe)
        {
            if (BuildConfig.DEBUG)
            {
                Log.e(TAG, "Error closing socket!", ioe);
            }
            throw ioe;
        }
    }
}
