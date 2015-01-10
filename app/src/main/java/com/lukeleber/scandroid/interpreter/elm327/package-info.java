/*
 * This file is protected under the KILLGPL.
 * For more information visit <insert_valid_link_to_killgpl_here>
 * <p/>
 * Copyright (c) Luke A. Leber <LukeLeber@gmail.com> 2014
 */

/**
 * <p>Implementation details for the ELM327 can be found within this package.  Refer to the
 * {@link com.lukeleber.scandroid.interpreter.elm327.OpCode} class for more detailed information as
 * to how to configure the ELM327 for use.</p>
 *
 * <p>Otherwise, using the ELM327 implementation is very simple:
 * <pre>
 *     /// arbitrary communication interface
 *     CommunicationInterface comm = ...;
 *
 *     /// single constructor
 *     ELM327 elm = new ELM327(comm);
 *
 *     /// asynchronously start the interpreter
 *     elm.start();
 *
 *     /// start talking with a vehicle
 *     elm.sendRequest(...);
 * </pre></p>
 *
 */
package com.lukeleber.scandroid.interpreter.elm327;