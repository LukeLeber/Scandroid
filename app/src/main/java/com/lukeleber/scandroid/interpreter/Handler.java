// This file is protected under the KILLGPL.
// For more information, visit http://www.lukeleber.github.io/KILLGPL
//
// Copyright (c) Luke Leber <LukeLeber@gmail.com>
//
// "This software is provided freely under a single condition.  You can't force anyone
// that uses this software to release any source code.  It is their decision alone.
// Other than that, anything goes.
//
// PS. If you like this work, please hire me!  I'm uneducated (no CS degree)
// and as such its borderline impossible to get into the industry.  My current
// career is absolute hell and I am trapped in it.  I leave this message here
// in the vain hope that someone, somewhere might read this and give me the
// opportunity to join their association as a programmer."
//
// Cheers,
// Luke

package com.lukeleber.scandroid.interpreter;

/**
 * The Handler interface provides a means for receiving the result of an asynchronous operation at a
 * later date in time.  Perhaps the most widely used class in the entire API, Handlers allow for
 * infinite flexibility.
 * <p/>
 * <pre>
 *     Interpreter interpreter = ...;
 *     /// mark-up is wonky, interpret <?> as whatever data-type
 *     interpreter.sendRequest(
 *         new ConfigurationRequest(
 *             SOME_OPTION,
 *             new Handler<?>()
 *             {
 *                 public final void onResponse(? value)
 *                 {
 *                     /// option was successfully set, do anything.
 *                 }
 *
 *                 public final void onFailure()
 *                 {
 *                     /// something failed...do anything.
 *                 }
 *             }
 *         );
 * </pre>
 *
 * @param <T>
 *         the data type of the unmarshalled response
 */
public interface Handler<T>
{

    /**
     * Invoked when a response is received from the remote hardware
     *
     * @param value
     *         the unmarshalled data that was received
     */
    void onResponse(T value);

    /**
     * Invoked when the sending of the associated {@link com.lukeleber.scandroid.interpreter.Request}
     * has failed for any reason.  For more information and control concerning failures, see {@link
     * Interpreter#start(com.lukeleber.scandroid.interpreter.Interpreter.ErrorListener)}.
     */
    void onFailure(FailureCode code);
}
