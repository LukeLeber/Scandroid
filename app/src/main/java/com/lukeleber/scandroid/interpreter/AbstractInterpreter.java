// This file is protected under the KILLGPL.
// For more information, visit http://www.lukeleber.github.io/KILLGPL.html
//
// Copyright (c) Luke Leber <LukeLeber@gmail.com>

package com.lukeleber.scandroid.interpreter;

import android.os.AsyncTask;
import android.util.Log;
import android.util.Pair;

import com.lukeleber.scandroid.BuildConfig;
import com.lukeleber.scandroid.io.CommunicationInterface;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * A reasonable skeletal implementation of much of the {@link com.lukeleber.scandroid.interpreter.Interpreter}
 * interface.
 *
 * @param <T>
 *         the type of data that is to be sent over this Interpreter
 * @param <U>
 *         the type of data that is to be received over this Interpreter
 *
 * @see com.lukeleber.scandroid.interpreter.Interpreter
 * @see android.os.AsyncTask
 */
public abstract class AbstractInterpreter<T, U>
        extends
        AsyncTask<Void, Pair<U, ResponseListener<U>>, Void>
        implements
        Interpreter<T>
{

    /// @internal tag for debug logging
    private final static String TAG = AbstractInterpreter.class.getName();
    private final CommunicationInterface com;

    /// The queue that internally synchronizes the use of this class with a UI thread
    private final transient BlockingQueue<Pair<Request<T, ?>, ResponseListener<U>>> pendingWrites;

    private final List<ErrorListener> errorListeners = new ArrayList<>();

    private final List<ConnectionListener> connectionListeners = new ArrayList<>();

    private final List<ShutdownListener> shutdownListeners = new ArrayList<>();

    @Override
    public final void addErrorListener(ErrorListener listener)
    {
        errorListeners.add(listener);
    }

    @Override
    public final void addConnectionListener(ConnectionListener listener)
    {
        connectionListeners.add(listener);
    }

    @Override
    public final void addShutdownListener(ShutdownListener listener)
    {
        shutdownListeners.add(listener);
    }

    /**
     * Constructs an {@link com.lukeleber.scandroid.interpreter.AbstractInterpreter}
     */
    protected AbstractInterpreter(CommunicationInterface com)
    {
        this.pendingWrites = new LinkedBlockingQueue<>();
        this.com = com;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    protected final Void doInBackground(Void... params)
    {

        CommunicationInterface com = getCommunicationInterface();
        try
        {
            com.connect();
            for(ConnectionListener listener : connectionListeners)
            {
                listener.onConnected();
            }
        }
        catch (IOException ioe)
        {
            for(ErrorListener listener : errorListeners)
            {
                listener.onError(ioe);
            }
            cleanup();
            return null;
        }
        boolean interrupted = false;
        do
        {
            if (super.isCancelled())
            {
                break;
            }
            Pair<Request<T, ?>, ResponseListener<U>> request;
            try
            {
                request = pendingWrites.take();
                try
                {
                    writeRequest(request.first);
                    publishProgress(new Pair<>(readReply(), request.second));
                }
                catch (IOException ioe)
                {
                    publishProgress(new Pair<>((U) null, request.second));
                    for(ErrorListener listener : errorListeners)
                    {
                        listener.onError(ioe);
                    }
                }
            }
            catch (InterruptedException ie)
            {
                interrupted = true;
                break;
            }
        }
        while (true);
        cleanup();

        if (interrupted)
        {
            Thread.currentThread()
                  .interrupt();
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SafeVarargs
    protected final void onProgressUpdate(Pair<U, ResponseListener<U>>... values)
    {
        for (Pair<U, ResponseListener<U>> p : values)
        {

            if (p.first != null && p.second != null)
            {
                p.second.onSuccess(p.first);
            }
            else if (p.second != null)
            {
                p.second.onFailure(FailureCode.IO_LINK_ERROR);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final CommunicationInterface getCommunicationInterface()
    {
        return com;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public final <V> void sendRequest(Request<T, V> request, ResponseListener<T> listener)
    {
        if (!pendingWrites.offer(new Pair(request, listener)))
        {
            if (BuildConfig.DEBUG)
            {
                Log.e(TAG, "Offer declined (something very bad has happened...)");
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void start()
    {
        execute();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean stop()
    {
        return super.cancel(true);
    }

    /**
     * Performs any initialization before entering the I/O loop.  This is an ideal spot to pass any
     * configurable options to the interpreter hardware.
     *
     * @throws java.io.IOException
     *         if any I/O error occurs during initialization
     */
    protected abstract void init()
            throws
            IOException;

    /**
     * Performs a best-attempt cleanup.  Closes the {@link com.lukeleber.scandroid.io.CommunicationInterface}
     * associated with this {@link com.lukeleber.scandroid.interpreter.Interpreter} and invokes the
     * virtual {@link java.io.Closeable#close()} method on itself.
     */
    private void cleanup()
    {
        try
        {
            getCommunicationInterface().close();
        }
        catch (Exception e)
        {
            if (BuildConfig.DEBUG)
            {
                Log.e(TAG, "Error closing communication interface", e);
            }
        }
        try
        {
            close();
        }
        catch (Exception e)
        {
            if (BuildConfig.DEBUG)
            {
                Log.e(TAG, "Error closing interpreter", e);
            }
        }
        for(ShutdownListener listener : shutdownListeners)
        {
            listener.onShutdown();
        }
    }

    /**
     * Performs the actual writing operation to the remote hardware.  The implementation details of
     * this method will be dependent on the type of remote system being interacted with.
     *
     * @param request
     *         the {@link com.lukeleber.scandroid.interpreter.Request} to write
     *
     * @throws java.io.IOException
     *         if any I/O error occurs during the write
     */
    protected abstract void writeRequest(Request<T, ?> request)
            throws
            IOException;

    /**
     * Performs the actual reading operation from the remote hardware.  The implementation details
     * of this method will be dependent on the type of remote system being interacted with.
     *
     * @return an object of type {@link U} that was read
     *
     * @throws java.io.IOException
     *         if any I/O error occurs during the read
     */
    protected abstract U readReply()
            throws
            IOException;

}
