// This file is protected under the KILLGPL.
// For more information, visit http://www.lukeleber.github.io/KILLGPL.html
//
// Copyright (c) Luke Leber <LukeLeber@gmail.com>

package com.lukeleber.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.lukeleber.app.ActivityResultListener;
import com.lukeleber.app.EnhancedActivity;

import java.io.Serializable;

/**
 * A convenience class that cuts down on the boilerplate code necessary to confirm that bluetooth
 * has been enabled or not.  Simply inherit the {@link com.lukeleber.bluetooth.BluetoothEnabler.Handler}
 * interface and pass the object to the static function defined in this class.
 * <p/>
 * <pre>
 *     class Example extends Activity implements BluetoothEnabler.Handler
 *     {
 *         public final void onBluetoothEnabled()
 *         {
 *             // bluetooth has been enabled
 *         }
 *
 *         public final void onBluetoothDisabled()
 *         {
 *             // bluetooth has been disabled
 *         }
 *
 *         protected final void onCreate(Bundle unused)
 *         {
 *             BluetoothEnabler.requestBluetoothEnableIfDisabled(this, this);
 *         }
 *     }
 * </pre>
 */
public class BluetoothEnabler
{
    /// The key for the handler value
    private final static String EXTRA_HANDLER = "handler";

    /**
     * <p>A convenience function for requesting that bluetooth be enabled.</p> <p/> <p>Launches a
     * confirmation dialog in the same manner as starting an activity with {@link
     * android.bluetooth.BluetoothAdapter#ACTION_REQUEST_ENABLE}.  The provided {@link
     * com.lukeleber.bluetooth.BluetoothEnabler.Handler} shall receive the result of the
     * activity.</p>
     *
     * @param context
     *         the calling {@link android.content.Context}
     * @param handler
     *         the {@link com.lukeleber.bluetooth.BluetoothEnabler.Handler} to receive the result of
     *         this call.
     */
    public static void requestBluetoothEnableIfDisabled(Context context, Handler handler)
    {
        context.startActivity(new Intent(context, Wrapper.class).putExtra(EXTRA_HANDLER, handler));
    }

    /**
     * An interface that is implemented by any object that handles the result of a request to enable
     * the bluetooth system service.
     */
    public static interface Handler
            extends
            Serializable
    {
        /**
         * Invoked if the bluetooth service has been enabled (or was already previously enabled)
         */
        abstract void onBluetoothEnabled();

        /**
         * Invoked if the bluetooth service could not be enabled (ie the user declined)
         */
        abstract void onBluetoothDisabled();
    }

    /**
     * An activity that unions a request and handler pair.
     */
    public final static class Wrapper
            extends
            EnhancedActivity
            implements
            ActivityResultListener
    {
        /// The {@link com.lukeleber.bluetooth.BluetoothEnabler.Handler} of this request
        private transient Handler handler;

        /**
         * {@inheritDoc}
         */
        @Override
        public final void onActivityResult(int resultCode, Intent unused)
        {
            if (resultCode == Activity.RESULT_OK)
            {
                handler.onBluetoothEnabled();
            }
            else
            {
                handler.onBluetoothDisabled();
            }
            finish();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected final void onCreate(Bundle unused)
        {
            super.onCreate(unused);
            this.handler = (Handler) super.getIntent()
                                          .getSerializableExtra(EXTRA_HANDLER);
            super.startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE),
                                         super.addResultListener(this));
        }
    }
}
