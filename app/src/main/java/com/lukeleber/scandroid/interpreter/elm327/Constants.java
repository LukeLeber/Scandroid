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

package com.lukeleber.scandroid.interpreter.elm327;

@SuppressWarnings("unused")
public class Constants
{
    public final static String ELM327_BAD_COMMAND = "?";
    public final static String ELM327_ACT_ALERT = "ACT ALERT";
    public final static String ELM327_ACT_ALERT_ALT = "!ACT ALERT";
    public final static String ELM327_BUFFER_FULL = "BUFFER FULL";
    public final static String ELM327_BUS_BUSY = "BUS BUSY";
    public final static String ELM327_BUS_ERROR = "BUS ERROR";
    public final static String ELM327_CAN_ERROR = "CAN ERROR";
    public final static String ELM327_DATA_ERROR = "DATA ERROR";
    public final static String ELM327_RECEIVED_DATA_ERROR = "<DATA ERROR";

    public final static String ELM327_GENERIC_ERROR = "ERR";
    public final static String ELM327_FEEDBACK_ERROR = "FB ERROR";
    public final static String ELM327_LOW_POWER_ALERT = "LP ALERT";
    public final static String ELM327_LOW_POWER_ALERT_ALT = "!LP ALERT";
    public final static String ELM327_LOW_VOLTAGE_RESET = "LV RESET";
    public final static String ELM327_NO_DATA = "NO DATA";
    public final static String ELM327_RECEIVE_ERROR = "<RX ERROR";
    public final static String ELM327_STOPPED = "STOPPED";
    public final static String ELM327_UNABLE_TO_CONNECT = "UNABLE TO CONNECT";
    public final static String ELM327_SEARCHING_FOR_PROTOCOL = "SEARCHING...";

    public static final String ELM327_BUT_INIT_ERROR = "BUS INIT: ERROR";
    public static final String ELM327_BUT_INIT_ERROR_ALT = "BUS INIT: ...ERROR";
}
