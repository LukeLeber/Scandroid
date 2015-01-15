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

package com.lukeleber.scandroid.sae;

import java.io.Serializable;

public enum SecondaryAirStatus implements Serializable
{
    AIR_STAT_UPS(0x0, "upstream of the first catalytic converter"),
    AIR_STAT_DNS(0x1, "downstream of the first catalytic converter inlet"),
    AIR_STAT_OFF(0x2, "atmosphere / off");

    private final int mask;
    private final String description;

    private SecondaryAirStatus(int mask, String description)
    {
        this.mask = mask;
        this.description = description;
    }

    public static SecondaryAirStatus forByte(int byte_)
    {
        for (SecondaryAirStatus status : SecondaryAirStatus.values())
        {
            if ((byte_ & status.mask) != 0)
            {
                return status;
            }
        }
        return null;
    }

    public final int getMask()
    {
        return mask;
    }

    public final String getDescription()
    {
        return description;
    }
}
