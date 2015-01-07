/*
 * This file is protected under the KILLGPL.
 * For more information visit <insert_valid_link_to_killgpl_here>
 * <p/>
 * Copyright (c) Luke A. Leber <LukeLeber@gmail.com> 2015
 */

package com.lukeleber.scandroid.io;

import java.io.IOException;

public class ScandroidIOException extends IOException
{

    public enum ExceptionCode
    {
        BLUETOOTH_NOT_ENABLED("This operation requires bluetooth to be enabled."),
        BLUETOOTH_UNSUPPORTED("Bluetooth is unsupported on this device."),
        BLUETOOTH_DEVICE_NOT_AVAILABLE("Unable to find a suitable bluetooth interface device."),
        BLUETOOTH_NOT_PERMITTED("This operation requires bluetooth permissions."),
        UNDOCUMENTED_BLUETOOTH_ERROR("Wish I could give you a better message, but the Android API is very vague in terms of why this might happen...");

        private final String what;

        ExceptionCode(String what)
        {
            this.what = what;
        }

        public final String what()
        {
            return what;
        }

    }
    private final ExceptionCode code;

    public ScandroidIOException(IOException cause, ExceptionCode code)
    {
        super(cause);
        this.code = code;
    }

    public ScandroidIOException(ExceptionCode code)
    {
        this.code = code;
    }

    public final ExceptionCode getCode()
    {
        return code;
    }


}
