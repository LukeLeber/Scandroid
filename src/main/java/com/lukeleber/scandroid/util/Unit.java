// This file is protected under the KILLGPL.
// For more information, visit http://www.lukeleber.github.io/KILLGPL.html
//
// Copyright (c) Luke Leber <LukeLeber@gmail.com>

package com.lukeleber.scandroid.util;

import android.os.Parcel;
import android.os.Parcelable;

@SuppressWarnings("unused")
public enum Unit
    implements Parcelable
{
    BYTE_ARRAY(""),
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
    ACCUMULATED_NUMBER(""), PACKETED(""),
    DIAGNOSTIC_TROUBLE_CODE(""),
    POUNDS_PER_MINUTE("lb/min"),
    INCHES_OF_WATER("in/h2o"), CONVENTIONAL_O2S("");

    public final static Parcelable.Creator<Unit> CREATOR = new Parcelable.Creator<Unit>()
    {

        @Override
        public Unit createFromParcel(Parcel source)
        {
            return Unit.values()[source.readByte() & 0xFF];
        }

        @Override
        public Unit[] newArray(int size)
        {
            return new Unit[size];
        }
    };

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeByte((byte)ordinal());
    }

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