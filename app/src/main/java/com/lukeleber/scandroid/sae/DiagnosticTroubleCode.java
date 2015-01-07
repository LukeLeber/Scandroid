/*
 * This file is protected under the KILLGPL.
 * For more information visit <insert_valid_link_to_killgpl_here>
 * <p/>
 * Copyright (c) Luke A. Leber <LukeLeber@gmail.com> 2014
 */

package com.lukeleber.scandroid.sae;

import java.io.Serializable;

public final class DiagnosticTroubleCode implements Serializable
{
    private final int code;
    private final String description;

    public DiagnosticTroubleCode(byte msb, byte lsb)
    {
        this.code = ((msb & 0xFF) << 8) | lsb;
        this.description = Category.values()[code & 0xF000].name() + Integer.toHexString(code & 0xFFF);
    }

    public final int getCode()
    {
        return code;
    }

    @Override
    public final String toString()
    {
        return description;
    }

    private static enum Category
    {
        P0,
        P1,
        P2,
        P3,
        C0,
        C1,
        C2,
        C3,
        B0,
        B1,
        B2,
        B3,
        U0,
        U1,
        U2,
        U3
    }
}