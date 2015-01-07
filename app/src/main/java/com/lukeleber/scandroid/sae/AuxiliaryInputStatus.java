/*
 * This file is protected under the KILLGPL.
 * For more information visit <insert_valid_link_to_killgpl_here>
 * <p/>
 * Copyright (c) Luke A. Leber <LukeLeber@gmail.com> 2014
 */

package com.lukeleber.scandroid.sae;

public enum AuxiliaryInputStatus
{
    OFF(0x0),
    ON(0x1);

    private final int mask;

    private AuxiliaryInputStatus(int mask)
    {
        this.mask = mask;
    }

    public static AuxiliaryInputStatus forByte(int val)
    {
        return (val & ON.mask) != 0 ? ON : OFF;
    }
}
