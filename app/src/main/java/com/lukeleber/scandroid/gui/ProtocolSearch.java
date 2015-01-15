package com.lukeleber.scandroid.gui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lukeleber.scandroid.Globals;
import com.lukeleber.scandroid.R;
import com.lukeleber.scandroid.interpreter.ConfigurationRequest;
import com.lukeleber.scandroid.interpreter.FailureCode;
import com.lukeleber.scandroid.interpreter.Handler;
import com.lukeleber.scandroid.interpreter.Interpreter;
import com.lukeleber.scandroid.interpreter.ResponseListener;
import com.lukeleber.scandroid.interpreter.ServiceRequest;
import com.lukeleber.scandroid.interpreter.elm327.Constants;
import com.lukeleber.scandroid.interpreter.elm327.OpCode;
import com.lukeleber.scandroid.interpreter.elm327.Protocol;
import com.lukeleber.scandroid.sae.detail.AppendixA;
import com.lukeleber.scandroid.sae.j1979.Service;
import com.lukeleber.widget.GenericBaseAdapter;

import java.util.Arrays;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ProtocolSearch
        extends Activity
{

    public final static int PROTOCOL_FOUND = 1;
    public final static int SEARCH_ABORTED = 2;
    public final static int FATAL_ERROR = 3;
    private final static Protocol[] MODEL = Arrays.copyOfRange(Protocol.values(), 1, Protocol.values().length - 2);

    public static final String PROTOCOL_RESULT = "protocol";

    @Override
    public final void onBackPressed()
    {
        super.setResult(SEARCH_ABORTED);
        super.onBackPressed();
        super.finish();
    }

    private final static class ProtocolViewModel
    {
        ProtocolViewModel(TextView proto, ImageView img)
        {
            this.proto = proto;
            this.img = img;
        }

        final TextView proto;
        final ImageView img;
    }

    private android.os.Handler handler = new android.os.Handler();

    /// A specialized response listener type to handle bus initialization
    private final class BusInitializationResponseListener
            implements ResponseListener<String>
    {
        private final Handler<?> handler;

        BusInitializationResponseListener(Handler<?> handler)
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
                    message.equals(Constants.ELM327_CAN_ERROR))
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
                handler.onResponse(null);
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


    private volatile int currentAttempt = 0;
    private volatile int retryAttempts = 0;

    @InjectView(R.id.textView2)
    TextView statusText;

    @InjectView(R.id.listView)
    ListView protocols;




    void onProtocolPassed()
    {
        views[currentAttempt + 1].img.setImageResource(R.drawable.checkbox_positive);
        Intent intent = new Intent();
        intent.putExtra(PROTOCOL_RESULT, MODEL[currentAttempt]);
        super.setResult(PROTOCOL_FOUND, intent);
        super.finish();
    }

    void onProtocolFailed()
    {
        views[currentAttempt + 1].img.setImageResource(R.drawable.checkbox_negative);
    }

    private final class TryProtocolX
            extends ConfigurationRequest<String, String>
    {
        public TryProtocolX(final Interpreter<String> interpreter, final ProtocolSearch ice_workaround)
        {

            super(new Handler<String>()
            {
                private final Handler<String> thiz;

                {
                    thiz = this;
                    ice_workaround.statusText.setText("Initializing bus (" + MODEL[currentAttempt].name() + ")");
                }

                @SuppressWarnings("unchecked")
                @Override
                public void onResponse(String value)
                {
                    /// Send a dummy request to invoke the protocol search so our custom
                    /// response listener can handle it
                    interpreter.sendRequest(new ServiceRequest(Service.LIVE_DATASTREAM,
                                    AppendixA.J1979_CHECK_PID_SUPPORT_1_TO_20, null),
                            new BusInitializationResponseListener(
                                    new Handler<Object>()
                                    {
                                        @Override
                                        public void onResponse(Object value)
                                        {
                                            ice_workaround.onProtocolPassed();
                                        }

                                        @Override
                                        public void onFailure(FailureCode code)
                                        {
                                            ice_workaround.onProtocolFailed();
                                            if (++ice_workaround.currentAttempt >= MODEL.length)
                                            {
                                                ice_workaround.currentAttempt = 0;
                                                ++ice_workaround.retryAttempts;
                                                Toast.makeText(ice_workaround, "No communication - is the key on?  Retrying..." + ice_workaround.retryAttempts, Toast.LENGTH_SHORT).show();
                                                for(ProtocolViewModel pvm : ice_workaround.views)
                                                {
                                                    pvm.img.setImageResource(R.drawable.checkbox_neutral);
                                                }
                                            }
                                            interpreter.sendRequest(ice_workaround.new TryProtocolX(interpreter, ice_workaround), ice_workaround.new BusInitializationResponseListener(thiz));
                                        }
                                    }
                            )
                    );
                }

                @Override
                public void onFailure(FailureCode code)
                {
                    ice_workaround.handler.post(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            ice_workaround.setResult(FATAL_ERROR);
                            ice_workaround.finish();
                        }
                    });
//                    onProtocolFailed();
/*                    if (++currentAttempt >= MODEL.length)
                    {
                        currentAttempt = 0;
                        ++retryAttempts;
                        Toast.makeText(ice_workaround, "No communication - is the key on?  Retrying..." + retryAttempts, Toast.LENGTH_SHORT).show();
                    }
                    interpreter.sendRequest(ice_workaround.new TryProtocolX(interpreter, ice_workaround), ice_workaround.new BusInitializationResponseListener(thiz));*/

                }
            }, OpCode.ELM327_OBD_TRY_PROTOCOL, MODEL[currentAttempt].getID());
        }

    }
    Protocol[] MODEL2 = Arrays.copyOf(Protocol.values(), Protocol.values().length - 2);

    ProtocolViewModel[] views = new ProtocolViewModel[MODEL2.length];


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_protocol_search);
        ButterKnife.inject(this);
        protocols.setAdapter(new GenericBaseAdapter<Protocol>()
        {
            private final LayoutInflater inflater;

            {
                this.inflater = getLayoutInflater();
            }

            @Override
            public int getCount()
            {
                return MODEL2.length;
            }

            @Override
            public Protocol getItem(int position)
            {
                return MODEL2[position];
            }

            @Override
            public long getItemId(int position)
            {
                return position;
            }



            @Override
            public View getView(int position, View convertView, ViewGroup parent)
            {
                if (convertView == null)
                {
                    convertView = inflater.inflate(R.layout.protocol_checked_text_view, null);
                    ProtocolViewModel pvm = new ProtocolViewModel(
                            (TextView) convertView.findViewById(
                                    R.id.protocol_checked_text_view_protocol),
                            (ImageView) convertView.findViewById(
                                    R.id.protocol_checked_text_view_image)
                    );
                    pvm.proto.setText(position == 0 ? "Available Protocols:" : getItem(position).name());
                    if(position != 0)
                        pvm.img.setImageResource(R.drawable.checkbox_neutral);
                    convertView.setTag(pvm);
                    views[position] = pvm;
                }
                return convertView;
            }
        });

        final Interpreter<String> interpreter = Globals.getInterpreter(getApplicationContext());
        interpreter.sendRequest(new TryProtocolX(interpreter, this));
    }
}
