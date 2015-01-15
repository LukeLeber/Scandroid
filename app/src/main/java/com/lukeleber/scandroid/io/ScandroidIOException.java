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

package com.lukeleber.scandroid.io;

import java.io.IOException;

public class ScandroidIOException
        extends IOException
{

    public enum ExceptionCode
    {
        BLUETOOTH_NOT_ENABLED("This operation requires bluetooth to be enabled."),
        BLUETOOTH_UNSUPPORTED("Bluetooth is unsupported on this device."),
        BLUETOOTH_DEVICE_NOT_AVAILABLE("Unable to find a suitable bluetooth interface device."),
        BLUETOOTH_NOT_PERMITTED("This operation requires bluetooth permissions."),
        UNDOCUMENTED_BLUETOOTH_ERROR(
                "Wish I could give you a better message, but the Android API is very vague in terms of why this might happen...");

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
