// This file is protected under the KILLGPL.
// For more information, visit http://www.lukeleber.github.io/KILLGPL.html
//
// Copyright (c) Luke Leber <LukeLeber@gmail.com>

package com.lukeleber.scandroid.interpreter;

import com.lukeleber.scandroid.sae.j1979.PID;

/**
 * A common base class for all requests that may be sent over an interpreter.
 *
 * @param <T>
 *         the type of data that represents the serializable content of this request
 * @param <U>
 *         the type of data that represents the response to this request
 */
@SuppressWarnings("unused")
public abstract class Request<T>
{
    /// The {@link killgpl.scandroid.interpreter.Handler} for this request
    private final Handler<T> handler;

    /**
     * Constructs a Request with the provided {@link Handler}
     *
     * @param handler
     *         the {@link Handler} that is invoked when either a response to this request is
     *         received from the remote hardware, or an error occurs.
     *
     */
    public Request(Handler<T> handler)
    {
        this.handler = handler;
    }

    /**
     * Retrieves the {@link Handler} that is invoked when a
     * response to this request is received from the remote hardware
     *
     * @return the {@link Handler} that is invoked when a
     * response to this request is received from the remote hardware
     *
     */
    public final Handler<T> getHandler()
    {
        return handler;
    }

    public abstract PID.Unmarshaller<T> getUnmarshaller();
}
