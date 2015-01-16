// This file is protected under the KILLGPL.
// For more information, visit http://www.lukeleber.github.io/KILLGPL.html
//
// Copyright (c) Luke Leber <LukeLeber@gmail.com>
//
// "This software is provided freely under a single condition.  You can't force anyone
// that uses this software to release any source code.  It is their decision alone.
// Other than that, anything goes.
//
// PS. If you like this work, please hire me!  I'm uneducated (no CS degree)
// and as such it's borderline impossible to get into the industry.  My current
// career is absolute hell and I am trapped in it.  I leave this message here
// in the vain hope that someone, somewhere might read this and give me the
// opportunity to join their association as a programmer."
//
// Cheers,
// Luke

package com.lukeleber.scandroid.sae;

import android.view.View;

import com.lukeleber.scandroid.util.Unit;

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

    /**
     * Retrieves the number of bytes that the response to this PID consists of
     *
     * @param profile
     *         the Profile to reference
     *
     * @return the number of bytes that the response to this PID consists of
     */
    int getResponseLength(Profile profile);

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

    /**
     * Retrieves the layout resource ID that adapts this PID to a user interface
     *
     * @return the layout resource ID that adapts this PID to a user interface
     */
    int getLayoutID();

    Object createViewModel(View view);

    void updateViewModel(Object view, Object value);
}
