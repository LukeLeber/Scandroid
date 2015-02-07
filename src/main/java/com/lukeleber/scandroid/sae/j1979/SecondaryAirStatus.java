// This file is protected under the KILLGPL.
// For more information, visit http://www.lukeleber.github.io/KILLGPL.html
//
// Copyright (c) Luke Leber <LukeLeber@gmail.com>

package com.lukeleber.scandroid.sae.j1979;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import com.lukeleber.scandroid.R;
import com.lukeleber.scandroid.util.Internationalized;

import java.io.Serializable;

/**
 * <p>The status of the on-board secondary air injection system as defined by SAE J1979, Appendix B:
 * Table 9.  Currently SAE J1979 mandates that only the three (3) least significant bits of a
 * byte are used while the five (5) most significant bits are reserved by the document and must
 * be reported as zero (0).</p>
 * <p>The {@link SecondaryAirStatus#toString()} method shall return the ISO/SAE defined
 * terminology, whereas
 * {@link com.lukeleber.scandroid.util.Internationalized#toI18NString(Context)} shall return a
 * more user-friendly description.</p>
 *
 * //todo: comprehensive docs
 *
 * @see java.io.Serializable
 *
 * @see android.os.Parcelable
 *
 * @see com.lukeleber.scandroid.util.Internationalized
 *
 */
public enum SecondaryAirStatus
        implements Internationalized,
                   Serializable,
                   Parcelable
{
    /**
     * Air is currently being injected upstream of the first catalyst
     */
    UPS(0x0, R.string.secondary_air_status_ups_desc),

    /**
     * Air is currently being injected downstream of the first catalyst
     */
    DNS(0x1, R.string.secondary_air_status_dns_desc),

    /**
     * Air is currently not being injected (or is being vented to atmosphere)
     */
    OFF(0x2, R.string.secondary_air_status_off_desc);

    /// Required by the {@link android.os.Parcelable} interface
    public final static Parcelable.Creator<SecondaryAirStatus> CREATOR =
            new Parcelable.Creator<SecondaryAirStatus>()
    {
        /**
         * {@inheritDoc}
         *
         */
        @Override
        public SecondaryAirStatus createFromParcel(Parcel in)
        {
            return SecondaryAirStatus.values()[in.readByte()];
        }

        /**
         * {@inheritDoc}
         *
         */
        @Override
        public SecondaryAirStatus[] newArray(int length)
        {
            return new SecondaryAirStatus[length];
        }
    };

    /**
     * Retrieves the <code>SecondaryAirStatus</code> that corresponds to the provided value
     *
     * @param byte0 The value to mask off
     *
     * @return the <code>SecondaryAirStatus</code> that corresponds to the provided value
     *
     * @throws java.lang.IllegalArgumentException if provided value does not correspond to any
     * <code>SecondaryAirStatus</code> objects.
     *
     */
    public static @NonNull
    SecondaryAirStatus forByte(int byte0)
    {
        for (SecondaryAirStatus status : SecondaryAirStatus.values())
        {
            if ((byte0 & status.value) != 0)
            {
                return status;
            }
        }
        throw new IllegalArgumentException("No secondary air status exists for " + byte0);
    }

    /// The SAE J1979 defined value of this <code>SecondaryAirStatus</code>
    private final int value;

    /// The user-friendly description of this <code>SecondaryAirStatus</code>
    private final @StringRes
    int stringID;

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
        out.writeByte((byte) ordinal());
    }

    /**
     * {@inheritDoc}
     *
     */
    @Override
    public String toI18NString(Context context)
    {
        return context.getString(stringID);
    }

    public final int getValue()
    {
        return value;
    }

    /**
     * Constructs a <code>SecondaryAirStatus</code> with the provided value and resource ID
     *
     * @param value The SAE J1979 defined value of this <code>SecondaryAirStatus</code>
     *
     * @param stringID The string resource ID of the I18N description of this
     *                 <code>SecondaryAirStatus</code>
     *
     */
    private SecondaryAirStatus(int value, @StringRes int stringID)
    {
        this.value = value;
        this.stringID = stringID;
    }
}
