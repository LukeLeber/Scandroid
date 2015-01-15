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

/**
 * <p>The Scandroid API cannot communicate directly with vehicles, so it becomes necessary to
 * utilize a middle-man component ("Interpreter") that translates the digital output from Scandroid
 * to the analog input required by onboard micro-controllers and vice-versa.  The Interpreter
 * package provides a means by which to communicate with such a middle-man.</p>
 *
 * <p>The default {@link com.lukeleber.scandroid.interpreter.Interpreter} for the Scandroid application
 * is the (awesome) ELM327 product.  The ELM327 communicates via strings such that:
 * <pre>
 *     (--> indicates a data conversion [IE from a byte array to a string])
 *     (==> indicates a data movement [IE from a phone to the ELM327 circuit board])
 *
 *     <tt>Request</tt>
 *     Android Device --> String ==> ELM327 --> analog signal ==> vehicle
 *
 *     <tt>Response</tt>
 *     vehicle ==> ELM327 --> String ==> Android Device --> User Defined Data Type
 * </pre></p>
 *
 */
package com.lukeleber.scandroid.interpreter;