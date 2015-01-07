/*
 * This file is protected under the KILLGPL.
 * For more information visit <insert_valid_link_to_killgpl_here>
 * <p/>
 * Copyright (c) Luke A. Leber <LukeLeber@gmail.com> 2014
 */

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
