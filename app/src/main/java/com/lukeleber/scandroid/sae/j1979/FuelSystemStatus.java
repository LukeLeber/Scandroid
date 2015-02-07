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
 * <p>An enumeration of all possible modes that a fuel system might be in.  Each mode is
 * comprised of a loop status (that is, open or closed loop) as well as a condition that
 * describes the aforementioned loop status.  For more detailed documentation, refer to each
 * individual enumerated member.</p>
 * <p>Loop status, in a less abstract sense, is a means by which the on-board control module(s)
 * meter the air-fuel mixture entering the engine.  When a vehicle is cranking and for a time
 * after it starts, the fuel system is said to be in "Open Loop".  During Open Loop, the control
 * module calculates a target air-fuel ratio based upon various sensor inputs such as mass air
 * (MAF), throttle position (TP), manifold absolute pressure (MAP), engine coolant temperature
 * (ECT), intake air temperature (IAT), vehicle speed (VSS), and various others based upon
 * the each vehicle's specific equipments.  This calculated approach to fuel metering, however,
 * is not ideal for optimizing fuel economy or reducing emissions; and therefore is only used
 * until the vehicle is able to enter "Closed Loop".</p>
 * <p>Closed Loop is a method of fuel metering that relies upon pre-catalyst oxygen sensors
 * (O2S).  Closed loop may only be entered if the enabling criteria are met.  Generally, three
 * independent criteria must be met to allow for the transition into Closed Loop status.
 * <ol>
 *     <li>Time - a certain manufacturer defined threshold must elapse</li>
 *     <li>Temperature - the vehicle (CTS / Equivalent) operating temperature must meet a
 *     manufacturer defined threshold</li>
 *     <li>"Cross Count" - the oxygen sensor(s) must be active enough to meet a manufacturer
 *     defined threshold</li>
 * </ol>
 * Only after (at least) these three criteria are met can a fuel system enter Closed Loop.</p>
 *
 * <p>There exist driving conditions that can cause a fuel system to temporarily drop back into
 * Open Loop during Closed Loop operation such as:
 * <ul>
 *     <li>Acceleration enrichment (near full throttle or maximum engine load)</li>
 *     <li>Deceleration enleanment (possibly during engine braking when engine load is low)</li>
 * </ul>
 * A fuel system may also fall back into Open Loop if a system fault, such as an O2S circuit
 * failure or a faulty O2S is encountered by the control module during Closed Loop operation.
 * That is not to say, however, that such a system fault will necessarily force a fuel system to
 * re-enter Open Loop operation.  The criteria for re-entering Open Loop during Closed Loop
 * operation are ultimately manufacturer defined.
 * </p>
 *
 * <p>The following "User Friendly" strings are available for internationalization:
 * <ul>
 *     <li>{@link R.string#fuel_system_status_enum_open_loop_desc}</li>
 *     <li>{@link R.string#fuel_system_status_enum_closed_loop_desc}</li>
 *     <li>{@link R.string#fuel_system_status_enum_open_loop_drive_desc}</li>
 *     <li>{@link R.string#fuel_system_status_enum_open_loop_fault_desc}</li>
 *     <li>{@link R.string#fuel_system_status_enum_closed_loop_fault_desc}</li>
 * </ul>
 *
 * These strings should briefly describe the loop status abbreviation for each enumerated member.
 *
 * </p>
 * @see com.lukeleber.scandroid.util.Internationalized
 *
 * @see android.os.Parcelable
 *
 * @see java.io.Serializable
 *
 */
public enum FuelSystemStatus
        implements Internationalized,
                   Parcelable,
                   Serializable
{
    /**
     * Open Loop: conditions have not yet been met to enter closed loop
     */
    OL(0x1, R.string.fuel_system_status_enum_open_loop_desc),

    /**
     * Closed Loop: oxygen sensor(s) are being used as feedback for fuel metering
     */
    CL(0x2, R.string.fuel_system_status_enum_closed_loop_desc),

    /**
     * Open Loop: driving conditions have forced open loop status (eg power enrichment or
     * deceleration enleanment)
     */
    OL_DRIVE(0x4, R.string.fuel_system_status_enum_open_loop_drive_desc),

    /**
     * Open Loop: a system fault has prevented entry to closed loop
     */
    OL_FAULT(0x8, R.string.fuel_system_status_enum_open_loop_fault_desc),

    /**
     * Closed Loop: a fault exists with at least one oxygen sensor
     */
    CL_FAULT(0x10, R.string.fuel_system_status_enum_closed_loop_fault_desc);

    /// Required by the {@link android.os.Parcelable} interface
    public final static Parcelable.Creator<FuelSystemStatus> CREATOR
            = new Creator<FuelSystemStatus>()
    {
        /**
         * {@inheritDoc}
         *
         */
        @Override
        public FuelSystemStatus createFromParcel(Parcel in)
        {
            return FuelSystemStatus.values()[in.readByte() & 0xFF];
        }

        /**
         * {@inheritDoc}
         *
         */
        @Override
        public FuelSystemStatus[] newArray(int size)
        {
            return new FuelSystemStatus[size];
        }
    };

    /**
     * Retrieves the first status whose mask matches the provided byte
     *
     * @param byte0
     *         the byte to mask against
     *
     * @return the first status whose mask matches the provided byte
     *
     * @throws IllegalArgumentException if no status exists for the provided value
     *
     */
    public static FuelSystemStatus forByte(int byte0)
    {
        for (FuelSystemStatus status : FuelSystemStatus.values())
        {
            if ((status.getMask() & byte0) != 0)
            {
                return status;
            }
        }
        throw new IllegalArgumentException("No " + FuelSystemStatus.class.getSimpleName() +
                                                "exists for the provided byte (" + byte0 + ")");
    }

    /// The bit-mask of this status
    private final int mask;

    /// The resource ID of te user-friendly description of this status
    @StringRes
    private int stringID;

    /**
     * {@inheritDoc}
     */
    @Override
    public int describeContents()
    {
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeToParcel(Parcel out, int flags)
    {
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
     */
    @Override
    public final String toI18NString(Context context)
    {
        return context.getString(stringID);
    }

    /**
     * Constructs a FuelSystemStatus with the provided value and description
     *
     * @param mask
     *         the bit-mask of this status
     * @param stringID
     *         the string resource ID of the user-friendly description of this status
     */
    private FuelSystemStatus(int mask, @StringRes int stringID)
    {
        this.mask = mask;
        this.stringID = stringID;
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
}
