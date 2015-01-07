/*
 * This file is protected under the KILLGPL.
 * For more information visit <insert_valid_link_to_killgpl_here>
 * <p/>
 * Copyright (c) Luke A. Leber <LukeLeber@gmail.com> 2014
 */

package com.lukeleber.scandroid.gui;

/**
 * <p>The <code>ConfigurationMode</code> affects how many functions are available in subsequent user
 * interfaces.  {@link killgpl.scandroid.gui.ConfigurationMode#GENERIC_MODE} shall only display the
 * functions defined by SAE-J1979 and shall not support any manufacturer specific operations. {@link
 * killgpl.scandroid.gui.ConfigurationMode#EXPERT_MODE} on the other hand may contain manufacturer
 * specified information, tests, and other advanced functions.  It is worth noting that some "expert
 * mode" functions may be dangerous, as their behavior is not governed by the SAE.  For example, a
 * relearn procedure may rewrite certain areas of memory in an onboard controller.  Being
 * interrupted part-way through (IE losing connection to the vehicle) may have disastrous effects on
 * the system, possibly even bricking the module.</p> <p>Case in point: <b>ADVISE THE USER TO BE
 * CAREFUL IN EXPERT MODE!</b></p>
 */
/*package*/ enum ConfigurationMode
{
    /** A mode that only supports the base functions provided by SAE-J1979 */
    GENERIC_MODE,

    /**
     * A mode that supports all of the base functions provided by SAE-J1979 and additional functions
     * defined by the vehicle manufacturer
     */
    EXPERT_MODE
}