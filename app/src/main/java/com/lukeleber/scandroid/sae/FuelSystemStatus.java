/*
 * This file is protected under the KILLGPL.
 * For more information visit <insert_valid_link_to_killgpl_here>
 * <p/>
 * Copyright (c) Luke A. Leber <LukeLeber@gmail.com> 2014
 */

package com.lukeleber.scandroid.sae;

import java.io.Serializable;

/**
 * An enumeration of all possible modes that a fuel system might be in.  Each mode is comprised of a
 * loop status (that is, open or closed loop) as well as a condition that describes the
 * aforementioned loop status.  For more detailed documentation, refer to each individual enumerated
 * member.
 */
public enum FuelSystemStatus implements Serializable
{
    /**
     * Open Loop: conditions have not yet been met to enter closed loop
     */
    OL(0x1),

    /**
     * Closed Loop: oxygen sensor(s) are being used as feedback for fuel metering
     */
    CL(0x2),

    /**
     * Open Loop: driving conditions have forced open loop status (eg power enrichment or
     * deceleration enleanment)
     */
    OL_DRIVE(0x4),

    /**
     * Open Loop: a system fault has prevented entry to closed loop
     */
    OL_FAULT(0x8),

    /**
     * Closed Loop: a fault exists with at least one oxygen sensor
     */
    CL_FAULT(0x10);
    private final int mask;

    private FuelSystemStatus(int mask)
    {
        this.mask = mask;
    }

    public static FuelSystemStatus forByte(int byte0)
    {
        for (FuelSystemStatus status : FuelSystemStatus.values())
        {
            if ((status.getMask() & byte0) != 0)
            {
                return status;
            }
        }
        return null;
    }

    public int getMask()
    {
        return mask;
    }
}
