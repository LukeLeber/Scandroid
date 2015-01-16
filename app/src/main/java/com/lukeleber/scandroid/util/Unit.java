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

package com.lukeleber.scandroid.util;

@SuppressWarnings("unused")
public enum Unit
{
    PID_SUPPORT_STRUCT("N/A"),
    INTERNAL_PLACEHOLDER("N/A"),

    MILLI_VOLTS("mV"),
    VOLTS("V"),
    AMPERES("A"),
    MILLI_AMPERES("mA"),
    OHMS("Ω"),
    MILLI_OHMS("mΩ"),
    GALLONS_PER_HOUR("g/h"),
    LITERS_PER_HOUR("l/h"),
    MILES_PER_GALLON("mpg"),
    NEWTON_METERS("NM"),
    FOOT_POUNDS("lbs"),

    PERCENT("%"),
    ANGLE_DEGREES("°"),
    ANGLE_RADIANS("°"),
    ROTATIONS_PER_MINUTE("rpm"),

    PASCALS("PA"),
    KILO_PASCALS("kPA"),
    INCHES_OF_MERCURY("\"HG"),
    SECONDS("sec"),
    MINUTES("min"),
    HOURS("hr"),
    DAYS("day"),
    MILES("mi"),
    KILOMETERS("kM"),

    MILES_PER_HOUR("mph"),
    KILOMETERS_PER_HOUR("kph"),
    GRAMS_PER_SECOND("gm/s"),

    TEMPERATURE_FAHRENHEIT("°F"),
    TEMPERATURE_CELSIUS("°C"),
    BOOLEAN(""), PSI("psi"),
    ACCUMULATED_NUMBER("");

    private final String string;

    private Unit(String string)
    {
        this.string = string;
    }

    @Override
    public final String toString()
    {
        return string;
    }

}
