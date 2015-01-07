/*
 * This file is protected under the KILLGPL.
 * For more information visit <insert_valid_link_to_killgpl_here>
 * <p/>
 * Copyright (c) Luke A. Leber <LukeLeber@gmail.com> 2015
 */

package com.lukeleber.scandroid.gui.fragments.util;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

import com.lukeleber.scandroid.sae.PID;
import com.lukeleber.scandroid.util.Unit;

/**
 * @Internal
 *
 * A class that acts to bind a PID to an unspecified point in time.  Generally used in view
 * adapters, this model yields the following information:
 * <ul>
 *     <li>All information provided through the PID interface</li>
 *     <li>The last known value of the PID as reported by the vehicle</li>
 *     <li>The unit of the last known value</li>
 *     <li>The time (unix timestamp) that this model was last updated</li>
 * </ul>
 */
public final class ParameterModel
        implements
            Comparable<ParameterModel>,
            Parcelable,
            Serializable
{

    /// Required by the {@link android.os.Parcelable} mechanism
    /// So why isn't there a <i>compile time</i> error if it is omitted?
    /// Yet another design deficiency in the cookie cutter machine called android...
    public static final Creator<ParameterModel> CREATOR
            = new Creator<ParameterModel>()
    {
        @Override
        public ParameterModel createFromParcel(Parcel in)
        {
            ParameterModel rv = new ParameterModel((PID<?>)in.readParcelable(null));
            rv.lastKnownValue = in.readSerializable();
            rv.unit = (Unit) in.readSerializable();
            return rv;
        }

        @Override
        public ParameterModel[] newArray(int size)
        {
            return new ParameterModel[size];
        }
    };

    /// The PID that is represented by this model
    private final PID<?> pid;

    /// The last known value of the represented PID
    private Serializable lastKnownValue;

    /// The unit of the last known value of the represented PID
    private Unit unit;

    /// The time (unix timestamp) that this model was last updated
    private long timestamp;

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
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeParcelable(pid, flags);
        dest.writeValue(lastKnownValue);
        dest.writeSerializable(unit);

    }

    /**
     * {@inheritDoc}
     *
     */
    @Override
    public int compareTo(ParameterModel rhs)
    {
        return pid.compareTo(rhs.pid);
    }

    /**
     * Retrieves the PID that is represented by this model
     *
     * @return the PID that is represented by this model
     *
     */
    public PID<?> getPID()
    {
        return pid;
    }

    /**
     * Retrieves the last known value of the represented PID
     *
     * @return the last known value of the represented PID
     *
     */
    public Serializable getLastKnownValue()
    {
        return lastKnownValue;
    }

    /**
     * Retrieves the unit of the last known value of the represented PID
     *
     * @return the unit of the last known value of the represented PID
     *
     */
    public Unit getUnit()
    {
        return unit;
    }

    /**
     * Retrieves the time (unix timestamp) that this model was last updated
     *
     * @return the time (unix timestamp) that this model was last updated
     *
     */
    public long getTimestamp()
    {
        return timestamp;
    }

    /**
     * Updates the data contained within this model (including the timestamp)
     *
     * @param newValue the new value to set
     *
     * @param newUnit the new unit to set
     *
     * @throws NullPointerException if the provided serializable and/or unit are null
     *
     */
    public void update(Serializable newValue, Unit newUnit)
    {
        if(newValue == null)
        {
            throw new NullPointerException("newValue == null");
        }
        if(newUnit == null)
        {
            throw new NullPointerException("newUnit == null");
        }
        this.lastKnownValue = newValue;
        this.unit = newUnit;
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * Constructs a ParameterModel with the provided {@link killgpl.scandroid.sae.PID}
     *
     * @param pid the {@link killgpl.scandroid.sae.PID} that this model represents
     *
     * @throws NullPointerException if the provided {@link killgpl.scandroid.sae.PID} is null
     *
     */
    public ParameterModel(PID<?> pid)
    {
        if(pid == null)
        {
            throw new NullPointerException("pid == null");
        }
        this.pid = pid;
        this.lastKnownValue = null;
        this.unit = pid.getDefaultUnit();
    }
}
