/*
 * This file is protected under the com.lukeleber.
 * For more information visit <insert_valid_link_to_killgpl_here>
 * <p/>
 * Copyright (c) Luke A. Leber <LukeLeber@gmail.com> 2014
 */

package com.lukeleber.scandroid.sae;

import com.lukeleber.scandroid.util.Unit;

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
 */
public interface PID<T>
        extends ServiceFacet
{

    /**
     * Retrieves the default Unit for this PID
     *
     * @return the default Unit for this PID
     */
    Unit getDefaultUnit();

    /**
     * Retrieves the default unmarshaller for this PID
     *
     * @return the default unmarshaller for this PID
     */
    Unmarshaller<T> getDefaultUnmarshaller();

    /**
     * Retrieves an unmarshaller for the provided Unit
     *
     * @param unit
     *         the Unit to retrieve an unmarshaller for
     *
     * @return an unmarshaller for the provided Unit or null if no such unmarshaller exists
     */
    Unmarshaller<T> getUnmarshallerForUnit(Unit unit);

    Map<Unit, Unmarshaller<T>> getUnmarshallers();

    /**
     * A function object that acts to unmarshall a byte array into a meaningful object.
     *
     * @param <T>
     *         the type of data that this Unmarshaller creates
     */
    interface Unmarshaller<T>
    {
        T invoke(byte... bytes);
    }
}
