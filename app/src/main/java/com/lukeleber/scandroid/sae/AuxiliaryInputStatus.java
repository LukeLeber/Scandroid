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

package com.lukeleber.scandroid.sae;

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
