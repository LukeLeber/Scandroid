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

package com.lukeleber.scandroid.gui;

/**
 * <p>The <code>ConfigurationMode</code> affects how many functions are available in subsequent user
 * interfaces.  {@link com.lukeleber.scandroid.gui.ConfigurationMode#GENERIC_MODE} shall only display the
 * functions defined by SAE-J1979 and shall not support any manufacturer specific operations. {@link
 * com.lukeleber.scandroid.gui.ConfigurationMode#EXPERT_MODE} on the other hand may contain manufacturer
 * specified information, tests, and other advanced functions.  It is worth noting that some "expert
 * mode" functions may be dangerous, as their behavior is not governed by the SAE.  For example, a
 * relearn procedure may rewrite certain areas of memory in an onboard controller.  Being
 * interrupted part-way through (IE losing connection to the vehicle) may have disastrous effects on
 * the system, possibly even bricking the module.</p> <p>Case in point: <b>ADVISE THE USER TO BE
 * CAREFUL IN EXPERT MODE!</b></p>
 */
/*package*/ enum ConfigurationMode
{
    /**
     * A mode that only supports the base functions provided by SAE-J1979
     */
    GENERIC_MODE,

    /**
     * A mode that supports all of the base functions provided by SAE-J1979 and additional functions
     * defined by the vehicle manufacturer
     */
    EXPERT_MODE
}