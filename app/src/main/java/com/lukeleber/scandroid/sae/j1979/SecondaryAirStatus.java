/*
 * This file is protected under the KILLGPL.
 * For more information visit <insert_valid_link_to_killgpl_here>
 * <p/>
 * Copyright (c) Luke A. Leber <LukeLeber@gmail.com> 2014
 */

package com.lukeleber.scandroid.sae.j1979;

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
