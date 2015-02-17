//  This file is protected under the KILLGPL.
//  For more information, visit http://www.lukeleber.github.io/KILLGPL.html
//
//  Copyright (c) Luke Leber <LukeLeber@gmail.com>

package com.lukeleber.scandroid.gui.dialogs;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.lukeleber.scandroid.BuildConfig;
import com.lukeleber.scandroid.R;
import com.lukeleber.scandroid.gui.InterpreterHost;
import com.lukeleber.scandroid.interpreter.FailureCode;
import com.lukeleber.scandroid.interpreter.Handler;
import com.lukeleber.scandroid.interpreter.Interpreter;
import com.lukeleber.scandroid.interpreter.elm327.ELM327;
import com.lukeleber.scandroid.interpreter.elm327.OpCode;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * TODO: Filter all ELM327 dependent fields...
 * An informative dialog that can be shown by any {@link android.app.Activity} that implements
 * the {@link com.lukeleber.scandroid.gui.InterpreterHost} interface.  Various information about
 * the interpreter bridge is displayed to the user including hardware types and versions,
 * link status, average latency, and system voltage.  Specialized versions of this class can
 * be derived for use with implementation specific types of hardware.
 *
 * <p>For example, an ELM327 equipped with a bluetooth interface might present the device name,
 * UUID, and signal strength in addition to the information provided by this base class.</p>
 *
 */
public class BridgeStatus
        extends DialogFragment
{

    /// Text field for displaying the name of the interpreter hardware
    @InjectView(R.id.dialog_bridge_status_hardware)
    TextView hardware;

    /// TODO: Think about it...
    @InjectView(R.id.dialog_bridge_status_hardware_version)
    TextView hardwareVersion;

    /// TODO: Think about it...
    @InjectView(R.id.dialog_bridge_status_system_voltage)
    TextView systemVoltage;

    /// Text field for displaying the current link status of the interpreter bridge
    @InjectView(R.id.dialog_bridge_status_link_status)
    TextView linkStatus;

    /// Text field for displaying the average latency between a request and a response
    @InjectView(R.id.dialog_bridge_status_average_latency)
    TextView averageLatency;

    /// @internal debugging tag
    private final static String TAG = BridgeStatus.class.getName();

    /// The {@link com.lukeleber.scandroid.gui.InterpreterHost} of this dialog
    /// This member is non-null only between a pair of onAttach / onDetach calls
    private InterpreterHost host;

    /**
     * Retrieves a reference to the {@link com.lukeleber.scandroid.gui.InterpreterHost} of this
     * dialog
     *
     * @return a reference to the {@link com.lukeleber.scandroid.gui.InterpreterHost} of this
     * dialog
     */
    @SuppressWarnings("unused")
    protected final InterpreterHost getHost()
    {
        return host;
    }

    /**
     * This method takes all appropriate actions in order to update the bridge status data (which
     * could involve asynchronous operations in some cases).  Derived classes should call through
     * to the implementation of this method found in their superclass.
     */
    protected void update()
    {
        Interpreter interpreter = host.getInterpreter();
        hardware.setText(interpreter.getName());
        averageLatency.setText(String.valueOf(interpreter.getAverageLatency()) + " ms");
        linkStatus.setText(interpreter.getLinkStatus()
                                      .toI18NString(super.getActivity()));
        systemVoltage.setText(getString(R.string.waiting_for_response));

        interpreter.sendRequest(new ELM327.ConfigurationRequest(new Handler<String>()
        {
            @Override
            public void onResponse(String value)
            {
                systemVoltage.setText(value);
            }

            @Override
            public void onFailure(FailureCode code)
            {
                systemVoltage.setText(BridgeStatus.this.getString(R.string.not_available));
            }
        }, OpCode.ELM327_VOLTAGE_READING_READ_INPUT_VOLTAGE
                ));
    }

    /**
     * Invoked when the user presses the 'refresh' button, this method simply calls the
     * protected method {@link BridgeStatus#update()}.
     */
    @SuppressWarnings("unused")
    @OnClick(R.id.dialog_bridge_status_refresh_button)
    void onRefreshClicked()
    {
        update();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        if (!InterpreterHost.class.isAssignableFrom(activity.getClass()))
        {
            throw new ClassCastException(activity.getClass().getName()
                    + " must implement " +
                    InterpreterHost.class.getName());
        }
        /// Debugging check to ensure this fragment is not attached to multiple activities
        /// Things could get wonky really fast if we're juggling multiple interpreters...
        if(BuildConfig.DEBUG && this.host != null)
        {
            Log.w(TAG, "illegal state: host != null");
        }
        this.host = (InterpreterHost) activity;
    }

    /**
     * {@inheritDoc}
     *
     */
    @Override
    public void onDetach()
    {
        super.onDetach();
        /// I don't see how this could happen short of some joker setting internal fields with
        /// reflection.  The joke's on him (or her?). -- therefore Log.wtf seems appropriate.
        if(BuildConfig.DEBUG && this.host == null)
        {
            Log.wtf(TAG, "illegal state: host == null");
        }
        this.host = null;
    }

    /**
     * {@inheritDoc}
     *
     */
    @Override
    public Dialog onCreateDialog(Bundle sis)
    {
        final Activity activity = getActivity();
        @SuppressLint("InflateParams")
        View view = activity.getLayoutInflater().inflate(R.layout.dialog_bridge_status, null);
        ButterKnife.inject(this, view);
        return new AlertDialog.Builder(activity)
                .setView(view)
                .setTitle(R.string.dialog_bridge_status_title)
                .setNegativeButton(R.string.dialog_bridge_status_dismiss_button_text, null)
                .create();
    }
}
