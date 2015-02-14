// This file is protected under the KILLGPL.
// For more information, visit http://www.lukeleber.github.io/KILLGPL.html
//
// Copyright (c) Luke Leber <LukeLeber@gmail.com>

package com.lukeleber.scandroid.sae.j1979;

import android.support.annotation.NonNull;

import com.lukeleber.scandroid.util.Unit;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

/**
 * The default skeletal implementation of the {@link com.lukeleber.scandroid.sae.j1979.PID}
 * interface.
 *
 * @param <T>
 *         the data type that this PID should retrieve from a vehicle
 */
public abstract class AbstractPID<T>
        extends AbstractServiceFacet
        implements PID<T>
{

    /// The default unit for this <code>AbstractPID</code>
    private final Unit defaultUnit;

    /// The unmarshallers that are available for use with this <code>AbstractPID</code>
    private final Map<Unit, Unmarshaller<T>> unmarshallers;

    /**
     * {@inheritDoc}
     *
     */
    @Override
    public Iterator<Map.Entry<Unit, Unmarshaller<T>>> iterator()
    {
        return unmarshallers.entrySet().iterator();
    }

    /**
     * {@inheritDoc}
     *
     */
    @Override
    public final int compareTo(@NonNull ServiceFacet rhs)
    {
        return getID() - rhs.getID();
    }

    /**
     * {@inheritDoc}
     *
     */
    @Override
    public final Unit getDefaultUnit()
    {
        return defaultUnit;
    }

    /**
     * {@inheritDoc}
     *
     */
    @Override
    public final int getUnmarshallerCount()
    {
        return unmarshallers.size();
    }

    /**
     * {@inheritDoc}
     *
     */
    @Override
    public final Unmarshaller<T> getUnmarshallerForUnit(Unit unit)
    {
        return unmarshallers.get(unit);
    }

    /**
     * Constructs an <code>AbstractPID</code> with the provided ID, display name, description,
     * and unmarshallers.
     *
     * @param id the unique ISO/SAE/Manufacturer defined identification number
     *
     * @param displayName the ISO/SAE/Manufacturer specified display name
     *
     * @param description a brief description
     *
     * @param unmarshallers the {@link PID.Unmarshaller unmarshallers} that are available for
     *                      use with this <code>AbstractPID</code>
     *
     */
    protected AbstractPID(int id, String displayName, String description,
                          Map<Unit, Unmarshaller<T>> unmarshallers)
    {
        super(id, displayName, description);
        this.unmarshallers = Collections.unmodifiableMap(unmarshallers);

        /// No unmarshallers?  We'll just assume that the input is the desired output.
        /// So indicate that we want to return the input byte array without modification.
        this.defaultUnit =
                unmarshallers.size() > 0 ? unmarshallers.keySet().iterator().next() :
                        Unit.BYTE_ARRAY;
    }

    /**
     * Retrieves an unmodifiable mapping of the supported
     * {@link com.lukeleber.scandroid.util.Unit units} to the corresponding
     * {@link PID.Unmarshaller unmarshallers} that this <code>AbstractPID</code> supports
     *
     * @return an unmodifiable mapping of the supported
     * {@link com.lukeleber.scandroid.util.Unit units} to the corresponding
     * {@link PID.Unmarshaller unmarshallers} that this <code>AbstractPID</code> supports
     *
     */
    protected final Map<Unit, Unmarshaller<T>> getUnmarshallers()
    {
        return unmarshallers;
    }

}
