/*
 * This file is protected under the KILLGPL.
 * For more information visit <insert_valid_link_to_killgpl_here>
 * <p/>
 * Copyright (c) Luke A. Leber <LukeLeber@gmail.com> 2014
 */

package com.lukeleber.scandroid.interpreter;

import android.os.AsyncTask;
import android.util.Log;
import android.util.Pair;

import com.lukeleber.scandroid.BuildConfig;
import com.lukeleber.scandroid.io.CommunicationInterface;

import java.io.IOException;
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
 * @param <Result>
 *         the type of data that is returned from the asynchronous invocation of this Interpreter
 *
 * @see com.lukeleber.scandroid.interpreter.Interpreter
 * @see android.os.AsyncTask
 */
public abstract class AbstractInterpreter<T, U, Result>
        extends
        AsyncTask<Void, Pair<U, ResponseListener<U>>, Result>
        implements
        Interpreter<T>
{

    /// @internal tag for debug logging
    private final static String TAG = AbstractInterpreter.class.getName();
    private final CommunicationInterface com;
    /// The queue that internally synchronizes the use of this class with a UI thread
    private final transient BlockingQueue<Pair<Request<T, ?>, ResponseListener<U>>> pendingWrites;
    /// The {@link killgpl.scandroid.interpreter.ErrorHandler} to forward errors to
    private ErrorHandler errorHandler;

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
    protected final Result doInBackground(Void... params)
    {

        CommunicationInterface com = getCommunicationInterface();
        try
        {
            com.connect();
            onConnected();
            init();
            onInitialized();
        }
        catch (IOException ioe)
        {
            if (errorHandler != null)
            {
                errorHandler.onError(ioe);
            }
            cleanup();
            return getExceptionResult(ioe);
        }
        boolean cancelled = false, interrupted = false;
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
                    if (errorHandler != null)
                    {
                        errorHandler.onError(ioe);
                    }
                }
            }
            catch (InterruptedException ie)
            {
                interrupted = true;
                if (super.isCancelled())
                {
                    cancelled = true;
                }
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
        if (cancelled)
        {
            return getCancellationResult();
        }
        return getSuccessfulExitResult();
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
        start(null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void start(ErrorHandler errorHandler)
    {
        this.errorHandler = errorHandler;
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
    }

    /**
     * Retrieves an object of type {@link Result} that indicates an exceptional exit
     *
     * @param e
     *         the {@link Exception} that was raised during execution
     *
     * @return an object of type {@link Result} that indicates an exceptional exit
     */
    protected abstract Result getExceptionResult(Exception e);

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

    /**
     * Retrieves an object of type {@link Result} that indicates a task cancellation
     *
     * @return an object of type {@link Result} that indicates a task cancellation
     */
    protected abstract Result getCancellationResult();

    /**
     * Retrieves an object of type {@link Result} that indicates a successful exit
     *
     * @return an object of type {@link Result} that indicates a successful exit
     */
    protected abstract Result getSuccessfulExitResult();

    protected void onConnected()
    { /* no-op */ }

    protected void onInitialized()
    { /* no-op */ }

}
