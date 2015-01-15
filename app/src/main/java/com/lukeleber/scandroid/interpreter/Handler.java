/*
 * This file is protected under the KILLGPL.
 * For more information visit <insert_valid_link_to_killgpl_here>
 * <p/>
 * Copyright (c) Luke A. Leber <LukeLeber@gmail.com> 2014
 */

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
