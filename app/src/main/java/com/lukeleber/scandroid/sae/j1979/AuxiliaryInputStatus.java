// This file is protected under the KILLGPL.
// For more information, visit http://www.lukeleber.github.io/KILLGPL.html
//
// Copyright (c) Luke Leber <LukeLeber@gmail.com>

package com.lukeleber.scandroid.sae.j1979;

import java.io.Serializable;

public enum AuxiliaryInputStatus implements Serializable
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
