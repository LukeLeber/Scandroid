// This file is protected under the KILLGPL.
// For more information, visit http://www.lukeleber.github.io/KILLGPL.html
//
// Copyright (c) Luke Leber <LukeLeber@gmail.com>

package com.lukeleber.scandroid.sae.j1979;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.io.Serializable;

/**
 * An enumeration of all on-board diagnostic specification compliance combinations as defined
 * by SAE J1979.
 *
 */
public enum OBDSupport
        implements
            Parcelable,
            Serializable
{
    OBDII_CALIFORNIA_ARB(0x1, "OBD II"),
    OBD_FEDERAL_EPA(0x2, "OBD"),
    OBD_AND_OBD_II(0x3, "OBD and OBD II"),
    OBD_I(0x4, "OBD I"),
    NO_OBD(0x5, "No OBD"),
    EOBD(0x6, "EOBD"),
    EOBD_AND_OBD_II(0x7, "EOBD and OBD II"),
    EOBD_AND_OBD(0x8, "EOBD and OBD"),
    EOBD_OBD_AND_OBD_II(0x9, "EOBD, OBD, and OBD II"),
    JOBD(0xA, "JOBD"),
    JOBD_AND_OBD_II(0xB, "JOBD and OBD II"),
    JOBD_AND_OBD(0xC, "JOBD and OBD"),
    JOBD_EOBD_AND_OBD_II(0xD, "JOBD, EOBD, and OBD II");

    /// Required by the {@link android.os.Parcelable} interface
    public final static Parcelable.Creator<OBDSupport> CREATOR
            = new Parcelable.Creator<OBDSupport>()
    {

        /**
         * {@inheritDoc}
         *
         */
        @Override
        public OBDSupport createFromParcel(Parcel parcel)
        {
            return OBDSupport.values()[parcel.readByte() & 0xFF];
        }

        /**
         * {@inheritDoc}
         *
         */
        @Override
        public OBDSupport[] newArray(int length)
        {
            return new OBDSupport[length];
        }
    };

    /**
     * Attempts to retrieve the <code>OBDSupport</code> whose value corresponds with the provided
     * byte.
     *
     * @param value the value of the <code>OBDSupport</code> to retrieve
     *
     * @return the <code>OBDSupport</code> whose value corresponds with the provided byte.
     *
     * @throws java.lang.IllegalArgumentException if no <code>OBDSupport</code> corresponds to
     * the provided byte.
     *
     */
    public static @NonNull OBDSupport forByte(int value)
    {
        for(OBDSupport sup : values())
        {
            if(sup.getValue() == value)
            {
                return sup;
            }
        }
        throw new IllegalArgumentException("No OBDSupport object was found for byte " + value);
    }

    /// The SAE defined value of this <code>OBDSupport</code>
    private final int value;

    /// The SAE defined String representation of this <code>OBDSupport</code>
    private final String saeDefinedName;

    /**
     * {@inheritDoc}
     *
     */
    @Override
    public final @NonNull String toString()
    {
        return saeDefinedName;
    }

    /**
     * {@inheritDoc}
     *
     */
    @Override
    public int describeContents()
    {
        return 0;
    }

    /**
     * {@inheritDoc}
     *
     */
    @Override
    public void writeToParcel(Parcel out, int flags)
    {
        out.writeByte((byte)ordinal());
    }

    /**
     * Retrieves the SAE defined value of this <code>OBDSupport</code>
     *
     * @return the SAE defined value of this <code>OBDSupport</code>
     *
     */
    public final int getValue()
    {
        return value;
    }

    /**
     * Constructs an <code>OBDSupport</code> with the provided SAE defined value and string
     * representation.
     *
     * @param val the SAE defined value of this <code>OBDSupport</code>
     *
     * @param saeDefinedName the SAE defined String representation of this <code>OBDSupport</code>
     *
     */
    OBDSupport(int val, @NonNull String saeDefinedName)
    {
        this.value = val;
        this.saeDefinedName = saeDefinedName;
    }
}
