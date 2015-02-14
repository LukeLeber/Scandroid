// This file is protected under the KILLGPL.
// For more information, visit http://www.lukeleber.github.io/KILLGPL.html
//
// Copyright (c) Luke Leber <LukeLeber@gmail.com>

package com.lukeleber.scandroid.gui.fragments.util;

import android.os.Parcel;
import android.os.Parcelable;

import com.lukeleber.scandroid.gui.fragments.detail.PIDWrapper;
import com.lukeleber.scandroid.util.Unit;

import java.io.Serializable;

/**
 * @Internal A class that acts to bind a PID to an unspecified point in time.  Generally used in
 * view adapters, this model yields the following information: <ul> <li>All information provided
 * through the PID interface</li> <li>The last known value of the PID as reported by the
 * vehicle</li> <li>The unit of the last known value</li> <li>The time (unix timestamp) that this
 * model was last updated</li> </ul>
 */
public final class ParameterModel<T extends Serializable>
        implements
        Parcelable,
        Serializable
{

    /// The PID that is represented by this model
    private final PIDWrapper<?> pid;

    /// The last known value of the represented PID
    private T lastKnownValue;

    /// The unit of the last known value of the represented PID
    private Unit unit;

    private Unit pendingUnit;

    /// The time (unix timestamp) that this model was last updated
    private long timestamp;

    /**
     * Retrieves the PID that is represented by this model
     *
     * @return the PID that is represented by this model
     */
    public PIDWrapper<?> getPID()
    {
        return pid;
    }

    /**
     * Retrieves the last known value of the represented PID
     *
     * @return the last known value of the represented PID
     */
    public T getLastKnownValue()
    {
        return lastKnownValue;
    }

    /**
     * Retrieves the time (unix timestamp) that this model was last updated
     *
     * @return the time (unix timestamp) that this model was last updated
     */
    public long getTimestamp()
    {
        return timestamp;
    }

    /**
     * Updates the data contained within this model (including the timestamp)
     *
     * @param newValue
     *         the new value to set
     * @param newUnit
     *         the new unit to set
     *
     * @throws NullPointerException
     *         if the provided serializable and/or unit are null
     */
    public void update(T newValue, Unit newUnit)
    {
        if (newValue == null)
        {
            throw new NullPointerException("newValue == null");
        }
        this.lastKnownValue = newValue;
        this.timestamp = System.currentTimeMillis();
    }

    public ParameterModel(PIDWrapper<?> pid)
    {
        if (pid == null)
        {
            throw new NullPointerException("pid == null");
        }
        this.pid = pid;
        this.lastKnownValue = null;
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {

    }
}
