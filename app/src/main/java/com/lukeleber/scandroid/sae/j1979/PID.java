// This file is protected under the KILLGPL.
// For more information, visit http://www.lukeleber.github.io/KILLGPL.html
//
// Copyright (c) Luke Leber <LukeLeber@gmail.com>

package com.lukeleber.scandroid.sae.j1979;

import android.support.annotation.NonNull;

import com.lukeleber.scandroid.util.Unit;

import java.io.Serializable;
import java.util.Map;

/**
 * Utilized in Services $01 and $02, this type of ServiceFacet is called a "Parameter ID" or "PID"
 * for short.  PIDs describe a means by which to retrieve pieces of information from sensors and
 * other values obtained through an onboard control module.  When a response to a PID request is
 * received, the data is in the form of a byte array which must then be unmarshalled into a
 * meaningful object.  PIDs can offer various unmarshallers that can transform the received data in
 * different ways.  For example, a PID for requesting the engine coolant temperature can offer an
 * unmarshaller for both fahrenheit and celsius units.  In another example, a throttle position PID
 * can be expressed in either degrees or percent.
 *
 * @param <T>
 *         the data type that this PID should retrieve from a vehicle
 *
 * @see java.lang.Iterable
 * @see com.lukeleber.scandroid.sae.j1979.ServiceFacet
 * @see java.util.Map.Entry
 * @see com.lukeleber.scandroid.util.Unit
 */
public interface PID<T>
        extends
            Iterable<Map.Entry<Unit, PID.Unmarshaller<T>>>,
            ServiceFacet
{

    /**
     * A function object that acts to unmarshall a byte array into a meaningful object.
     *
     * @param <T>
     *         the type of data that this Unmarshaller creates
     */
    interface Unmarshaller<T>
        extends Serializable
    {
        T invoke(@NonNull byte... bytes);
    }

    /**
     * Retrieves the default Unit for this PID
     *
     * @return the default Unit for this PID
     */
    Unit getDefaultUnit();

    /**
     * Retrieves an unmarshaller for the provided Unit
     *
     * @param unit
     *         the Unit to retrieve an unmarshaller for
     *
     * @return an unmarshaller for the provided Unit or null if no such unmarshaller exists
     */
    Unmarshaller<T> getUnmarshallerForUnit(Unit unit);

    /**
     * Retrieves the number of
     * {@link com.lukeleber.scandroid.sae.j1979.PID.Unmarshaller unmarshallers} provided by this
     * <code>PID</code>
     *
     * @return the number of
     * {@link com.lukeleber.scandroid.sae.j1979.PID.Unmarshaller unmarshallers} provided by this
     * <code>PID</code>
     */
    int getUnmarshallerCount();

}
