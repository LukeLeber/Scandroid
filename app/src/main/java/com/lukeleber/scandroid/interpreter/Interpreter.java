// This file is protected under the KILLGPL.
// For more information, visit http://www.lukeleber.github.io/KILLGPL.html
//
// Copyright (c) Luke Leber <LukeLeber@gmail.com>
//
// "This software is provided freely under a single condition.  You can't force anyone
// that uses this software to release any source code.  It is their decision alone.
// Other than that, anything goes.
//
// PS. If you like this work, please hire me!  I'm uneducated (no CS degree)
// and as such it's borderline impossible to get into the industry.  My current
// career is absolute hell and I am trapped in it.  I leave this message here
// in the vain hope that someone, somewhere might read this and give me the
// opportunity to join their association as a programmer."
//
// Cheers,
// Luke

package com.lukeleber.scandroid.interpreter;

import com.lukeleber.scandroid.io.CommunicationInterface;

import java.io.Closeable;

/**
 * The ultimate base class for all interpreter implementations.  <code>Interpreters</code> are
 * responsible for providing all of the means required to communicate with a piece of remote
 * hardware, thus acting as a bridge between the android device and the vehicle that is being
 * accessed.
 *
 * @param <T>
 *         the type of data that is to be sent over this Interpreter
 */
public interface Interpreter<T>
        extends
        Closeable
{
    /**
     * An optional function object that can be provided to the {@link com.lukeleber.scandroid.interpreter.Interpreter#start(com.lukeleber.scandroid.interpreter.Interpreter.ErrorListener)}
     * to provide custom handling for any exceptions arising during the asynchronous execution of
     * the interpreter
     */
    public interface ErrorListener
    {
        /**
         * Invoked when the {@link com.lukeleber.scandroid.interpreter.Interpreter} encounters an
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
     * com.lukeleber.scandroid.interpreter.Interpreter} utilizes.
     *
     * @return the {@link com.lukeleber.scandroid.io.CommunicationInterface} that this {@link
     * com.lukeleber.scandroid.interpreter.Interpreter} utilizes.
     *
     * @since 1.0
     */
    CommunicationInterface getCommunicationInterface();

    /**
     * Retrieves a user-friendly name of this {@link com.lukeleber.scandroid.interpreter.Interpreter}
     *
     * @return a user-friendly name of this {@link com.lukeleber.scandroid.interpreter.Interpreter}
     *
     * @since 1.0
     */
    String getName();

    /**
     * Sends an asynchronous request over this {@link com.lukeleber.scandroid.interpreter.Interpreter}
     * and invokes a default {@link ResponseListener} on the UI thread when a reply is received, the
     * request times out, or an error occurs.
     *
     * @param request
     *         the {@link Request} to send over this {@link com.lukeleber.scandroid.interpreter.Interpreter}
     *
     * @since 1.0
     */
    <V> void sendRequest(Request<T, V> request);

    /**
     * Sends an asynchronous request over this {@link com.lukeleber.scandroid.interpreter.Interpreter}
     * and invokes the provided {@link ResponseListener} on the UI thread when a reply is received,
     * the request times out, or an error occurs.
     *
     * @param request
     *         the {@link Request} to send over this {@link com.lukeleber.scandroid.interpreter.Interpreter}
     * @param listener
     *         the {@link ResponseListener} to handle the reply from the remote hardware
     *
     * @since 1.0
     */
    <V> void sendRequest(Request<T, V> request, ResponseListener<T> listener);

    void addErrorListener(ErrorListener listener);

    void addConnectionListener(ConnectionListener listener);

    void addShutdownListener(ShutdownListener listener);

    /**
     * Asynchronously starts this {@link com.lukeleber.scandroid.interpreter.Interpreter}
     *
     */
    void start();

    /**
     * Asynchronously stops this {@link com.lukeleber.scandroid.interpreter.Interpreter}
     *
     * @return true if this {@link com.lukeleber.scandroid.interpreter.Interpreter} was successfully
     * signalled to stop, otherwise false
     */
    boolean stop();
}
