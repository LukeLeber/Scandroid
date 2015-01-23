/*
 * This file is protected under the KILLGPL.
 * For more information visit <insert_valid_link_to_killgpl_here>
 * <p/>
 * Copyright (c) Luke A. Leber <LukeLeber@gmail.com> 2014
 */

package com.lukeleber.scandroid.sae.j1979;

import android.support.annotation.NonNull;

import com.lukeleber.scandroid.util.Unit;

import java.util.Map;

public abstract class AbstractPID<T>
        extends AbstractServiceFacet
        implements PID<T>
{

    private final Unit defaultUnit;
    private final Unmarshaller<T> defaultUnmarshaller;
    private final Map<Unit, Unmarshaller<T>> unmarshallers;

    public final Map<Unit, Unmarshaller<T>> getUnmarshallers()
    {
        return unmarshallers;
    }

    @Override
    public final int compareTo(@NonNull ServiceFacet rhs)
    {
        return getID() - rhs.getID();
    }

    protected AbstractPID(int id, String displayName, String description,
                          Map<Unit, Unmarshaller<T>> unmarshallers)
    {
        super(id, displayName, description);
        this.unmarshallers = unmarshallers;
        Map.Entry<Unit, Unmarshaller<T>> defaults = unmarshallers.entrySet()
                                                                 .iterator()
                                                                 .next();
        this.defaultUnit = defaults.getKey();
        this.defaultUnmarshaller = defaults.getValue();
    }

    @Override
    public final Unit getDefaultUnit()
    {
        return defaultUnit;
    }

    @Override
    public final Unmarshaller<T> getDefaultUnmarshaller()
    {
        return defaultUnmarshaller;
    }

    @Override
    public final Unmarshaller<T> getUnmarshallerForUnit(Unit unit)
    {
        return unmarshallers.get(unit);
    }
}
