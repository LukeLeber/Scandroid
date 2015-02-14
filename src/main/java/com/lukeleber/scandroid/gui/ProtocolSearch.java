// This file is protected under the KILLGPL.
// For more information, visit http://www.lukeleber.github.io/KILLGPL.html
//
// Copyright (c) Luke Leber <LukeLeber@gmail.com>

package com.lukeleber.scandroid.gui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lukeleber.scandroid.BuildConfig;
import com.lukeleber.scandroid.Globals;
import com.lukeleber.scandroid.R;
import com.lukeleber.scandroid.gui.fragments.util.ViewHolderBase;
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
import com.lukeleber.scandroid.sae.j1979.PIDSupport;
import com.lukeleber.scandroid.sae.j1979.detail.AppendixA;
import com.lukeleber.scandroid.sae.j1979.Service;
import com.lukeleber.widget.GenericBaseAdapter;

import java.util.Arrays;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * As mandated by SAE J1978 §7.1, this class provides the following functionality:
 * <ul>
 *     <li>Provides an "Automatic hands-off determination of the communication protocol"</li>
 *     <li>Informs the user that bus initialization is occurring</li>
 *     <li>Prevent user intervention while each individual protocol test is occurring</li>
 *     <li>Upon failure, advise the user of the following:
 *         <ol>
 *             <li>That communication with the vehicle could not be established</li>
 *             <li>To confirm that the ignition key is in the "ON" position</li>
 *             <li>To check the emissions label or vehicle service information to confirm
 *             that the vehicle is OBD equipped and compliant</li>
 *             <li>To confirm that the external test equipment is connected to the vehicle
 *             correctly</li>
 *         </ol>
 *     </li>
 *     <li>Notifies the user of the number of failed initialization attempts</li>
 *     <li>Retries the initialization process until succeeding or the user cancels</li>
 * </ul>
 *
 * TODO: This class has two issues: a GUI listview problem and what I feel is an ICE.
 *
 */
public class ProtocolSearch
        extends Activity
{

    /// A specialized response listener type to handle bus initialization
    /// The ELM327 can respond in a variety of ways while initializing the bus,
    /// so this might take extensive testing on a variety of vehicles to perfect...
    private final static class BusInitializationResponseListener
            implements ResponseListener<String>
    {
        private final Handler<String> handler;

        BusInitializationResponseListener(Handler<String> handler)
        {
            this.handler = handler;
        }

        @Override
        public void onSuccess(String message)
        {
            /// The ELM327 seems to respond with "SEARCHING...\nerror_string\n"
            /// where "error_string" could be one of various values when an invalid protocol
            /// is used, so we will modify this response by:

            /// 1) Removing all occurrences of "SEARCHING...\n"
            message = message.replace(Constants.ELM327_SEARCHING_FOR_PROTOCOL + (char) 13, "");

            /// 2) Removing the trailing newline (\n) if one exists
            if (message.charAt(message.length() - 1) == (char) 13)
            {
                message = message.substring(0, message.length() - 1);
            }

            /// So we're now left with "error_string" which seems to be one of the following...
            if (message.equals(Constants.ELM327_UNABLE_TO_CONNECT) ||
                    message.equals(Constants.ELM327_NO_DATA) ||
                    message.equals(Constants.ELM327_BUT_INIT_ERROR) ||
                    message.equals(Constants.ELM327_BUT_INIT_ERROR_ALT) ||
                    message.equals(Constants.ELM327_CAN_ERROR) ||
                    message.equals(Constants.ELM327_BUS_ERROR))
            {
                /// Protocol failed - this could be because:
                /// 1) Invalid protocol
                /// 2) Key is not on
                /// 3) Electrical system fault (in the vehicle, or the ELM327)
                onFailure(FailureCode.INVALID_PROTOCOL);
            }
            else
            {
                /// Found a valid protocol (We don't care what the vehicle had to say
                /// so long as the ELM327 didn't report an error.  The simple fact
                /// that the vehicle said SOMETHING is enough to validate the protocol.)
                /// The string is only provided to the handler for debugging purposes.
                handler.onResponse(message);
            }
        }

        @Override
        public void onFailure(FailureCode code)
        {
            /// This could happen for various reasons and needs to be handled...
            /// 1) The ELM327 could become unplugged
            /// 2) The android device could drop out of range of the vehicle
            /// 3) An "internal" error occurs (that should not escape QA testing!)
            handler.onFailure(code);
        }
    }

    /// A "view holder" object for use with the list view
    private final static class ViewHolder
            extends ViewHolderBase
    {
        /// The name of the protocol being tested
        final TextView proto;

        /// The icon representing failure, neutrality, or success for a protocol test
        final ImageView img;

        /**
         * Constructs a <code>ViewHolder</code> with the provided parent {@link android.view.View}
         *
         * @param view the parent {@link android.view.View}
         */
        ViewHolder(View view)
        {
            this.proto = ButterKnife.findById(view,
                    R.id.protocol_search_listview_item_protocol_textview);
            this.img = ButterKnife.findById(view,
                    R.id.protocol_search_listview_item_imageview);
        }
    }

    private final static String TAG = ProtocolSearch.class.getName();

    /// A result code for a successful bus initialization
    public final static int PROTOCOL_FOUND = 1;

    /// A result code for a user abort
    public final static int SEARCH_ABORTED = 2;

    /// A result code for an unknown, fatal (catastrophic?) error
    public final static int FATAL_ERROR = 3;

    /// A slice of the protocols supported by the ELM327 (excluding AUTO and USER_*_CAN_*)
    private final static Protocol[] AVAILABLE_PROTOCOLS =
            Arrays.copyOfRange(Protocol.values(), 1, Protocol.values().length - 2);

    /// The key for retrieving the result of this activity
    public final static String PROTOCOL_RESULT_KEY = "protocol_search_result";

    /// A special type of request that complies with the bus initialization test method as
    /// mandated by SAE J1978 §7.1.c.
    /// Basically, this class recursively operates as follows:
    /// 1) Set ELM327 to use the current protocol to test
    /// 2) Confirm that the ELM327 has successfully done so
    /// 2a) If failure, fail with a fatal error (can't communicate with interpreter)
    /// 2b) If success, then goto step 3
    /// 3) Send service request $01$00 to the ELM327
    /// 4) Was the bus initialized successfully?
    /// 4a) If failure, increment the protocol and goto step 1
    /// 4b) If success, set the activity result and finish()
    /// TODO: Figure out why the enclosing ProtocolSearch instance can't be used!  ICE methinks...
    private final class ProtocolTest
            extends ELM327.ConfigurationRequest
    {
        public ProtocolTest(final Interpreter interpreter,
                            final ProtocolSearch ice_workaround)
        {

            super(new Handler<String>()
            {
                private final Handler<String> thiz;

                {
                    thiz = this;
                    ice_workaround.statusText.setText(String.format(
                            ice_workaround.getString(
                                    R.string.activity_protocol_search_initializing_bus_label),
                            AVAILABLE_PROTOCOLS[currentProtocol].name()));
                }

                /// The ELM327 successfully set the new protocol, so we're all set to test it.
                /// Send service request $01$00 to the vehicle and see what happens...
                @Override
                public void onResponse(String value)
                {
                    interpreter.sendRequest(new ServiceRequest<PIDSupport>(
                        Service.LIVE_DATASTREAM,
                        AppendixA.J1979_CHECK_PID_SUPPORT_1_TO_20, null),
                        new BusInitializationResponseListener(
                            new Handler<String>()
                            {
                                /// The protocol test passed, return it to the calling activity.
                                @Override
                                public void onResponse(String value)
                                {
                                    if(BuildConfig.DEBUG)
                                    {
                                        Log.i(TAG, "Protocol " +
                                            AVAILABLE_PROTOCOLS[ice_workaround.currentProtocol] +
                                            " passed the test with response: " + value);
                                    }
                                    ice_workaround.onProtocolPassed();
                                }

                                /// The protocol test failed, try the next one...
                                @Override
                                public void onFailure(FailureCode code)
                                {
                                    /// INVALID_PROTOCOL should be the only recoverable
                                    /// error at this point...so jump to the outer-handler
                                    /// for fatal conditions.
                                    if(code != FailureCode.INVALID_PROTOCOL)
                                    {
                                        thiz.onFailure(code);
                                    }
                                    else
                                    {
                                        if (BuildConfig.DEBUG)
                                        {
                                            Log.i(TAG, "Protocol " +
                                                    AVAILABLE_PROTOCOLS[
                                                            ice_workaround.currentProtocol] +
                                                    " failed the test with error code: " + code);
                                        }
                                        ice_workaround.onProtocolFailed();
                                        if (++ice_workaround.currentProtocol >=
                                                AVAILABLE_PROTOCOLS.length)
                                        {
                                            /// All protocols failed.  Drat.
                                            ice_workaround.currentProtocol = 0;
                                            ++ice_workaround.retryAttempts;
                                            ice_workaround.warnUserOfFailure();
                                            for (ViewHolder pvm : ice_workaround.views)
                                            {
                                                pvm.img.setImageResource(
                                                        R.drawable.checkbox_neutral);
                                            }
                                        }
                                        interpreter.sendRequest(
                                                ice_workaround.new ProtocolTest(
                                                        interpreter, ice_workaround),
                                                new BusInitializationResponseListener(thiz)
                                                               );
                                    }
                                }
                            }
                        )
                    );
                }

                /// The ELM327 failed to set the new protocol
                /// This indicates that the ELM327 is either unplugged, out of range,
                /// or damaged...any case is unrecoverable from the software perspective.
                /// So tell the user the bad news and life goes on...
                @Override
                public void onFailure(final FailureCode code)
                {
                    if(BuildConfig.DEBUG)
                    {
                        Log.i(TAG, "Protocol search failed with a fatal error: " + code);
                    }
                    ice_workaround.handler.post(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            ice_workaround.onFatalError(code);
                        }
                    });
                }
            }, OpCode.ELM327_OBD_TRY_PROTOCOL, AVAILABLE_PROTOCOLS[currentProtocol].getID());
        }
    }

    /// The {@link android.os.Handler} to push asynchronous event results back into the GUI loop
    private android.os.Handler handler = new android.os.Handler();

    /// The index of the current protocol that is being tested
    /// Subject to read/write on thread A and read on thread B
    /// So volatility should ensure accuracy.
    private volatile int currentProtocol = 0;

    /// The number of times that the search process has been attempted
    /// Subject to read/write on thread A and read on thread B
    /// So volatility should ensure accuracy.
    private volatile int retryAttempts = 0;

    /// The {@link android.widget.TextView} that displays the current test
    @InjectView(R.id.protocol_search_current_protocol)
    TextView statusText;

    /// The {@link android.widget.ListView that displays the overall test results
    @InjectView(R.id.protocol_search_listview)
    ListView protocols;

    ///TODO: The listview won't properly display an icon at index 0, so I put a dummy at index 0...
    ViewHolder[] views = new ViewHolder[AVAILABLE_PROTOCOLS.length + 1];

    /**
     * {@inheritDoc}
     *
     * <p>This override sets the result of this activity to {@link ProtocolSearch#SEARCH_ABORTED}
     * and invokes its super method.</p>
     */
    @Override
    public final void onBackPressed()
    {
        super.setResult(SEARCH_ABORTED);
        super.onBackPressed();
    }

    /**
     * {@inheritDoc}
     *
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_protocol_search);
        ButterKnife.inject(this);
        protocols.setAdapter(
            new GenericBaseAdapter<Protocol>()
            {
                private final LayoutInflater inflater;

                {
                    this.inflater = getLayoutInflater();
                }

                @Override
                public int getCount()
                {
                    return AVAILABLE_PROTOCOLS.length + 1;
                }

                @Override
                public Protocol getItem(int position)
                {
                    return AVAILABLE_PROTOCOLS[position - 1];
                }

                @Override
                public long getItemId(int position)
                {
                    return position;
                }

                @SuppressLint("InflateParams")
                @Override
                public View getView(int position, View convertView, ViewGroup parent)
                {
                    if (convertView == null)
                    {
                        convertView = inflater.inflate(R.layout.protocol_checked_text_view, null);
                        ViewHolder pvm = new ViewHolder(convertView);
                        if(position == 0)
                        {
                            pvm.proto.setText(getString(
                                    R.string.activity_protocol_search_available_protocols_label));
                        }
                        else
                        {
                            pvm.proto.setText(getItem(position).name());
                            pvm.img.setImageResource(R.drawable.checkbox_neutral);
                        }
                        convertView.setTag(pvm);
                        views[position] = pvm;
                    }
                    return convertView;
                }
            }
        );
        Interpreter interpreter = Globals.getInterpreter(getApplicationContext());
        interpreter.sendRequest(new ProtocolTest(interpreter, this));
    }

    /**
     * Invoked when the current protocol test succeeds
     *
     */
    private void onProtocolPassed()
    {
        views[currentProtocol + 1].img.setImageResource(R.drawable.checkbox_positive);
        Intent intent = new Intent();
        intent.putExtra(PROTOCOL_RESULT_KEY, AVAILABLE_PROTOCOLS[currentProtocol]);
        super.setResult(PROTOCOL_FOUND, intent);
        super.finish();
    }

    /**
     * Invoked when the current protocol test fails
     *
     */
    private void onProtocolFailed()
    {
        views[currentProtocol + 1].img.setImageResource(R.drawable.checkbox_negative);
    }

    /**
     * Invoked when all protocol tests fail
     *
     */
    private void warnUserOfFailure()
    {
        Toast.makeText(this,
                String.format(getString(R.string.activity_protocol_search_failure_message),
                        retryAttempts), Toast.LENGTH_LONG).show();
    }

    private void onFatalError(FailureCode code)
    {
        Toast.makeText(this,
                String.format(getString(R.string.activity_protocol_search_fatal_error_message),
                        code), Toast.LENGTH_LONG).show();
        setResult(FATAL_ERROR);
        finish();
    }
}
