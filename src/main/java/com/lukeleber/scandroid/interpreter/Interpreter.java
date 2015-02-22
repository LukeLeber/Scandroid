// This file is protected under the KILLGPL.
// For more information, visit http://www.lukeleber.github.io/KILLGPL.html
//
// Copyright (c) Luke Leber <LukeLeber@gmail.com>

package com.lukeleber.scandroid.interpreter;

import android.content.Context;
import android.support.annotation.StringRes;

import com.lukeleber.scandroid.R;
import com.lukeleber.scandroid.io.CommunicationInterface;
import com.lukeleber.scandroid.util.Internationalized;

import java.io.Closeable;

/**
 * The ultimate base class for all interpreter implementations.  <code>Interpreters</code> are
 * responsible for providing all of the means required to communicate with a piece of remote
 * hardware, thus acting as a bridge between the android device and the vehicle that is being
 * accessed.
 *
 */
public interface Interpreter
        extends
        Closeable
{
    public enum LinkStatus implements Internationalized
    {
        DISCONNECTED(R.string.disconnected),
        CONNECTED(R.string.connected),
        ERROR(R.string.error);

        private final @StringRes int id;

        LinkStatus(@StringRes
                   int id)
        {
            this.id = id;
        }

        @Override
        public final String toI18NString(Context context)
        {
            return context.getString(id);
        }

    }

    /**
     * An optional function object that can be provided to a
     * {@link com.lukeleber.scandroid.interpreter.Interpreter} to provide custom handling for any
     * exceptions arising during the asynchronous execution of the interpreter
     */
    public interface ErrorListener
    {
        /**
         * Invoked when the {@link Interpreter} encounters an
         * error of any kind
         *
         * @param error
         *         the error that was encountered
         */
        void onError(Throwable error);
    }

    public interface ConnectionListener
    {
        void onConnected();
    }

    public interface ShutdownListener
    {
        void onShutdown();
    }

    /**
     * Retrieves the {@link com.lukeleber.scandroid.io.CommunicationInterface} that this {@link
     * Interpreter} utilizes.
     *
     * @return the {@link com.lukeleber.scandroid.io.CommunicationInterface} that this {@link
     * Interpreter} utilizes.
     *
     * @since 1.0
     */
    CommunicationInterface getCommunicationInterface();

    /**
     * Retrieves a user-friendly name of this {@link Interpreter}
     *
     * @return a user-friendly name of this {@link Interpreter}
     *
     * @since 1.0
     */
    String getName();

    /**
     * Sends an asynchronous request over this {@link Interpreter}
     * and invokes a default {@link ResponseListener} on the UI thread when a reply is received, the
     * request times out, or an error occurs.
     *
     * @param request
     *         the {@link com.lukeleber.scandroid.interpreter.Request} to send over this {@link Interpreter}
     *
     * @since 1.0
     */
    <V> void sendRequest(Request<V> request);

    /**
     * Sends an asynchronous request over this {@link Interpreter}
     * and invokes the provided {@link ResponseListener} on the UI thread when a reply is received,
     * the request times out, or an error occurs.
     *
     * @param request
     *         the {@link com.lukeleber.scandroid.interpreter.Request} to send over this {@link Interpreter}
     * @param listener
     *         the {@link ResponseListener} to handle the reply from the remote hardware
     *
     * @since 1.0
     */
    <V> void sendRequest(Request<V> request, ResponseListener<?> listener);

    void addErrorListener(ErrorListener listener);

    void addConnectionListener(ConnectionListener listener);

    void addShutdownListener(ShutdownListener listener);

    /**
     * Asynchronously starts this {@link Interpreter}
     *
     */
    void start();

    /**
     * Asynchronously stops this {@link Interpreter}
     *
     * @return true if this {@link Interpreter} was successfully
     * signalled to stop, otherwise false
     */
    boolean stop();

    long getAverageLatency();

    LinkStatus getLinkStatus();
}
