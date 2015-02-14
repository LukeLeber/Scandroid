// This file is protected under the KILLGPL.
// For more information, visit http://www.lukeleber.github.io/KILLGPL.html
//
// Copyright (c) Luke Leber <LukeLeber@gmail.com>

package com.lukeleber.scandroid.sae.j1979;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.StringRes;

import com.lukeleber.scandroid.R;
import com.lukeleber.scandroid.util.Internationalized;

import java.io.Serializable;

/**
 * <p>The status of the on-board auxiliary input system as defined by SAE J1979, Appendix B:
 * Table 15.</p>
 */
public enum AuxiliaryInputStatus
        implements Internationalized,
        Parcelable,
        Serializable
{

    /**
     * The auxiliary input is off
     */
    OFF(0x0, R.string.auxiliary_input_status_off_desc),

    /**
     * The auxiliary input is on
     */
    ON(0x1, R.string.auxiliary_input_status_on_desc);

    /// Required by the Parcelable interface
    public final static Parcelable.Creator<AuxiliaryInputStatus> CREATOR
            = new Parcelable.Creator<AuxiliaryInputStatus>()
    {

        /**
         * {@inheritDoc}
         *
         */
        @Override
        public AuxiliaryInputStatus createFromParcel(Parcel in)
        {
            return AuxiliaryInputStatus.values()[in.readByte() & 0xFF];
        }

        /**
         * {@inheritDoc}
         *
         */
        @Override
        public AuxiliaryInputStatus[] newArray(int length)
        {
            return new AuxiliaryInputStatus[length];
        }
    };

    /**
     * Retrieves the <code>AuxiliaryInputStatus</code> whose value corresponds to masking
     *
     * @param byte0 The value to mask
     * @return the <code>AuxiliaryInputStatus</code> whose value corresponds to the masking
     */
    public static AuxiliaryInputStatus forByte(int byte0)
    {
        return (byte0 & ON.mask) != 0 ? ON : OFF;
    }

    /// The bit-mask of this status
    private final int mask;

    /// The resource ID of te user-friendly description of this status
    @StringRes
    private final int stringID;

    /**
     * {@inheritDoc}
     */
    @Override
    public final int describeContents() {
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void writeToParcel(Parcel out, int flags) {
        out.writeByte((byte) ordinal());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String toString()
    {
        return name();
    }

    /**
     * {@inheritDoc}
     *
     */
    @Override
    public final String toI18NString(Context context)
    {
        return context.getString(stringID);
    }

    /**
     * Retrieves the bit-mask of this status
     *
     * @return the bit-mask of this status
     */
    public final int getMask()
    {
        return mask;
    }

    /**
     * Constructs a <code>AuxiliaryInputStatus</code> with the provided mask and description
     *
     * @param mask    The SAE J1979 defined mask of this <code>AuxiliaryInputStatus</code>
     * @param stringID the string resource ID of the user-friendly description of this status
     */
    private AuxiliaryInputStatus(int mask, @StringRes int stringID)
    {
        this.mask = mask;
        this.stringID = stringID;
    }
}
