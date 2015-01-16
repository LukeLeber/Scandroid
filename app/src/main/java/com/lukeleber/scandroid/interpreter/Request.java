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

/**
 * A common base class for all requests that may be sent over an interpreter
 *
 * @param <T>
 *         the type of data that represents the serializable content of this request
 * @param <U>
 *         the type of data that represents the response to this request
 */
@SuppressWarnings("unused")
public abstract class Request<T, U>
{
    /// The {@link killgpl.scandroid.interpreter.Handler} for this request
    private final Handler<U> handler;

    /**
     * Constructs a Request with the provided {@link com.lukeleber.scandroid.interpreter.Handler}
     *
     * @param handler
     *         the {@link com.lukeleber.scandroid.interpreter.Handler} that is invoked when a
     *         response to this request is received from the remote hardware
     */
    public Request(Handler<U> handler)
    {
        this.handler = handler;
    }

    /**
     * Retrieves the {@link com.lukeleber.scandroid.interpreter.Handler} that is invoked when a
     * response to this request is received from the remote hardware
     *
     * @return the {@link com.lukeleber.scandroid.interpreter.Handler} that is invoked when a
     * response to this request is received from the remote hardware
     */
    public final Handler<U> getHandler()
    {
        return handler;
    }
}
