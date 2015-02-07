// This file is protected under the KILLGPL.
// For more information, visit http://www.lukeleber.github.io/KILLGPL.html
//
// Copyright (c) Luke Leber <LukeLeber@gmail.com>

package com.lukeleber.scandroid.interpreter.elm327;

import android.util.Log;

import com.lukeleber.scandroid.BuildConfig;
import com.lukeleber.scandroid.interpreter.AbstractInterpreter;
import com.lukeleber.scandroid.interpreter.ConfigurationRequest;
import com.lukeleber.scandroid.interpreter.FailureCode;
import com.lukeleber.scandroid.interpreter.Handler;
import com.lukeleber.scandroid.interpreter.Request;
import com.lukeleber.scandroid.interpreter.ResponseListener;
import com.lukeleber.scandroid.interpreter.ServiceRequest;
import com.lukeleber.scandroid.io.CommunicationInterface;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

/**
 * <p>An {@link com.lukeleber.scandroid.interpreter.Interpreter} that utilizes the ELM327 integrated
 * circuitry to communicate with a vehicle.</p>
 * <p/>
 * Official documentation on the ELM327 can be found at <a href=http://www.elmelectronics.com>ELM
 * Electronics</a>.
 */
public class ELM327
        extends AbstractInterpreter<String, String>
{

    /// @internal tag for debug logging
    private final static String TAG = ELM327.class.getName();

    /**
     * Constructs an {@link ELM327} with the provided
     * {@link com.lukeleber.scandroid.io.CommunicationInterface}
     *
     * @param com
     *         the {@link com.lukeleber.scandroid.io.CommunicationInterface} to communicate with the
     *         ELM327 circuitry through.
     */
    public ELM327(CommunicationInterface com)
    {
        super(com);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void init()
            throws
            IOException
    {
        /// no-op
        /// In the future some custom scan tools might require tweaking the communication
        /// protocol, baud-rates, etc...to access some non-OBDII functionality in the remote
        /// systems
    }

    /**
     * {inheritDoc}
     */
    @Override
    protected void writeRequest(Request<String, ?> request)
            throws
            IOException
    {
        byte[] toSend;
        if (request instanceof ServiceRequest)
        {
            @SuppressWarnings("unchecked")
            ServiceRequest<String, ?> sr = (ServiceRequest<String, ?>) request;
            String s = null;
            if(sr.getPID() != null)
            {
                s = String.format("%02x%02x", sr.getService()
                                                .getID(), sr.getPID()
                                                            .getID());
            }
            else
            {
                s = String.format("%02x", sr.getService().getID());
            }
            toSend = new byte[s.length() + 1];
            System.arraycopy(s.getBytes(), 0, toSend, 0, s.length());

        }
        else if (request instanceof ConfigurationRequest)
        {
            @SuppressWarnings("unchecked")
            ConfigurationRequest<String, ?> cr = (ConfigurationRequest<String, ?>) request;
            String s = String.format(cr.getOption()
                                       .getOption(), cr.getArgs());
            toSend = new byte[s.length() + 1];
            System.arraycopy(s.getBytes(), 0, toSend, 0, s.length());
        }
        else
        {
            throw new UnsupportedOperationException(
                    "Request types of " + request.getClass()
                                                 .getSimpleName() +
                            " are not supported by the " + getName() + " interpreter"
            );
        }
        toSend[toSend.length - 1] = 0x0D;
        getCommunicationInterface().getOutputStream()
                                   .write(toSend);
    }

    /**
     * {@inheritDoc}
     * <p/>
     * Always returns "ELM327"
     */
    @Override
    public String getName()
    {
        return "ELM327";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final <V> void sendRequest(Request<String, V> request)
    {
        super.sendRequest(request, new DefaultResponseListener<>(request));
    }

    /**
     * {inheritDoc}
     */
    @Override
    public String readReply()
            throws
            IOException
    {
        InputStream inputStream = getCommunicationInterface().getInputStream();
        byte[] response = new byte[256];
        int i = 0;
        while (true)
        {
            /// I found no documentation about any function returning more than 32 bytes
            /// However this number is subject to change if I missed something...
            if (BuildConfig.DEBUG)
            {
                if (i >= response.length)
                {
                    Log.e(TAG, "Buffer overflow in ELM327.readReply");
                }
            }
            if ((response[i++] = (byte) inputStream.read()) == 0x3E) /// Carriage return
            {
                break;
            }
            else if (response[i] == 0x20) /// Ignore whitespace
            {
                --i;
            }
        }
        return new String(Arrays.copyOf(response, i - 2)); /// Chop off "\n>"
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void close()
    {
        /// no-op
        /// In the future some non-OBDII functions may require logging out of a remote system
        /// For example, newer vehicles can be started remotely via smart-phones and such
        /// systems generally expect a proper log-out procedure to be followed...hairy stuff!
    }

    /**
     * The {@link com.lukeleber.scandroid.interpreter.ResponseListener} that handles incoming
     * responses from the ELM327 board.  The ELM327 responds differently to different types of
     * {@link com.lukeleber.scandroid.interpreter.Request Requests}: <ul> <li>{@link
     * com.lukeleber.scandroid.interpreter.ConfigurationRequest} - An implementation defined value
     * is sent from the ELM327, generally the string 'OK' upon success.  This value is passed to the
     * provided {@link com.lukeleber.scandroid.interpreter.Handler} verbatim</li> <li>{@link
     * com.lukeleber.scandroid.interpreter.ServiceRequest} - The first two characters sent from the
     * ELM327 is "40" plus the service ID; thus a request using service ID 1 ({@link
     * com.lukeleber.scandroid.sae.j1979.Service#LIVE_DATASTREAM}) shall produce "41".  The following two
     * characters are the PID/TID/OBDMID/INFOTYPE ID of the request; so PID #1 ({@link
     * com.lukeleber.scandroid.sae.j1979.detail.AppendixB#MONITOR_STATUS}) shall produce "01".  The
     * following characters sequence is the actual data response from the ELM327.  So to sum
     * everything up, a service request $02$04 would produce the following response: "4204??" where
     * "??" is a single hex-byte representing the calculated engine load.  This {@link
     * com.lukeleber.scandroid.interpreter.ResponseListener} trims off the first four characters of
     * each response, invoking the requested {@link com.lukeleber.scandroid.sae.j1979.PID.Unmarshaller}</li>
     * on only the actual data. </ul>
     *
     * @param <T>
     *         the type of data that shall be unmarshalled from the response
     */
    private final static class DefaultResponseListener<T>
            implements ResponseListener<String>
    {
        /// The {@link com.lukeleber.scandroid.interpreter.Request Requests} that was sent
        private final Request<String, T> request;

        /**
         * Constructs a DefaultResponseListener for the provided {@link
         * com.lukeleber.scandroid.interpreter.Request}
         *
         * @param request
         *         the {@link com.lukeleber.scandroid.interpreter.Request} that is being sent
         */
        DefaultResponseListener(Request<String, T> request)
        {
            this.request = request;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void onSuccess(String resp)
        {
            resp = resp.toUpperCase();
            Handler<T> handler = request.getHandler();
            if (resp.substring(0, resp.length() - 1)
                    .equals(OpCode.ELM327_NO_DATA))
            {
                onFailure(FailureCode.REQUEST_NOT_SUPPORTED);
            }
            else if (request instanceof ServiceRequest)
            {
                ServiceRequest<String, T> serviceRequest = (ServiceRequest<String, T>) request;
                if (!resp.startsWith(String.valueOf(40 + serviceRequest.getService()
                                                                       .getID())
                                           .toUpperCase()))
                {
                    throw new IllegalStateException(
                            "Invalid message header received" + " expected " + String.valueOf(
                                    40 + serviceRequest.getService()
                                                       .getID())
                                                                                     .toUpperCase() + " but received " + resp);
                }
                if (resp.startsWith("44")) // Special case for "clear DTCs"
                {
                    /// TODO custom handler?
                    handler.onResponse((T)"DTCs Cleared");
                    return;
                }
                resp = resp.substring(2);
                if (!resp.startsWith(String.format("%02x", serviceRequest.getPID()
                                                                         .getID())
                                           .toUpperCase()))
                {
                    throw new IllegalStateException(
                            "Invalid message header received: " + resp.substring(0,
                                                                                 2) + " but expected " + String.format(
                                    "%02x", serviceRequest.getPID()
                                                          .getID()));
                }
                resp = resp.substring(2);
                byte[] parsed = new byte[resp.length() / 2];
                for (int i = 0;
                     i < resp.length() - 2;
                     i += 2)
                {
                    parsed[i / 2] = (byte) (Integer.decode("0x" + resp.substring(i, i + 2)) & 0xFF);
                }
                if (handler != null)
                {
                    handler.onResponse(serviceRequest.getUnmarshaller()
                                                     .invoke(parsed));
                }
            }
            else
            {
                if (handler != null)
                {
                    resp = resp.replace(""+(char)13, "");
                    if(resp.equals("?"))
                    {
                        ((ConfigurationRequest<String, String>) request).getHandler().onFailure(FailureCode.CONFIGURATION_COMMAND_NOT_RECOGNIZED);
                    }
                    else
                    {
                        ((ConfigurationRequest<String, String>) request).getHandler()
                                                                        .onResponse(resp);
                    }
                }
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void onFailure(FailureCode code)
        {
            Handler<T> handler = request.getHandler();
            if (handler != null)
            {
                handler.onFailure(code);
            }

        }
    }
}
