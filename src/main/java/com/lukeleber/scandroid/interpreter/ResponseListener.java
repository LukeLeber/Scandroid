// This file is protected under the KILLGPL.
// For more information, visit http://www.lukeleber.github.io/KILLGPL.html
//
// Copyright (c) Luke Leber <LukeLeber@gmail.com>

package com.lukeleber.scandroid.interpreter;

/**
 * <p>The <code>ResponseListener</code> interface is responsible for acting as a middle-man between
 * the raw data received from a piece of remote hardware and the rest of the framework, providing
 * all necessary pre-processing, filtering, etc...</p>
 *
 * <p>Sometimes a response from the remote systems must be broken into several (if not many)
 * different data packets in order to be transmitted over the bus.  It is conceivable that data
 * may not always arrive in the correct (that is, FIFO) order that would be expected due to bus
 * arbitration, latency, or even a negative "please wait" response sent by an on-board module.
 * This interface can be thought of as a sort of software buffer that ensures received data is
 * presented to the rest of the framework in the correct order and in its entirety.</p>
 *
 * <p>It is also likely that this interface may be used to detect "application layer" errors
 * that are not tested for on the lower "hardware" layers.  For example, using SAE J1979; sending
 * a service request $04 should result in receiving a response in the form of
 * {[header], 44} -- where '[header]' is the protocol message header and '44' is the service
 * identifier.  Suppose such a request is written, but instead of the expected response, a
 * negative response message is found instead.  While completely legal and compliant from the
 * hardware perspective, this negative response message indicates on the "application layer" that
 * the vehicle did not provide the requested information, or otherwise indicated that the
 * request could not be carried out.  In such a case, it might be preferable to invoke
 * the {@link #onFailure(FailureCode)} method like so:
 * <pre>
 *     boolean conditionsNotCorrect(byte[] message) {...}
 *
 *     new ResponseListener<byte[]>()
 *     {
 *         public void onSuccess(byte[] message)
 *         {
 *            if(conditionsNotCorrect(message))
 *            {
 *                onFailure(FailureCode.CONDITIONS_NOT_CORRECT;
 *            }
 *         }
 *
 *         public void onFailure(FailureCode code)
 *         {
 *             /// no-op
 *         }
 *     };
 * </pre></p>
 *
 * @param <T> The type of data that is received from the remote interpreter hardware (typically
 *           <tt>byte[]</tt> or {@link java.lang.String}).
 *
 */
public interface ResponseListener<T>
{
    /**
     * Invoked when a data packet is received from the remote interpreter hardware.
     *
     * @param message the message that was received
     *
     */
    void onSuccess(T message);

    /**
     * Invoked either when an I/O error arises with the remote interpreter, or within the
     * {@link #onSuccess(Object)} method if a the received data does not satisfy the conditions
     * representing a successful read (IE negative responses or an incomplete multi-packet
     * response).
     *
     * @param code a {@link com.lukeleber.scandroid.interpreter.FailureCode} representing what
     *             went wrong.
     *
     */
    void onFailure(FailureCode code);
}
