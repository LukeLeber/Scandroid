// This file is protected under the KILLGPL.
// For more information, visit http://www.lukeleber.github.io/KILLGPL.html
//
// Copyright (c) Luke Leber <LukeLeber@gmail.com>

package com.lukeleber.scandroid.util;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * An enumeration of various units that are assigned to the result of the corresponding unmarshaller
 * functions.  Units are classified by a couple of characteristics including whether or not the unit
 * has a displayable character display, and whether the unit is metric or SAE. As such, units may be
 * sorted by the user to match the desired locale preferences.
 */
@SuppressWarnings("unused")
public enum Unit
        implements Parcelable
{
    /**
     * This is the unit for a byte array.  Little more than a tag, this constant exists only for
     * self-documentation and should never be displayed to the user.
     */
    BYTE_ARRAY("")
            {
                /**
                 * {@inheritDoc}
                 */
                @Override
                public final boolean isDisplayable()
                {
                    return false;
                }

                /**
                 * {@inheritDoc}
                 */
                @Override
                public final boolean isMetric()
                {
                    return false;
                }

                /**
                 * {@inheritDoc}
                 */
                @Override
                public final boolean isSAE()
                {
                    return false;
                }
            },

    /**
     * This is the unit for a {@link com.lukeleber.scandroid.sae.j1979.PIDSupport} structure found
     * in SAE J1979 Appendix A.  Little more than a tag, this constant exists only for
     * self-documentation and is never displayed to the user.
     */
    PID_SUPPORT_STRUCT("")
            {
                /**
                 * {@inheritDoc}
                 */
                @Override
                public final boolean isDisplayable()
                {
                    return false;
                }

                /**
                 * {@inheritDoc}
                 */
                @Override
                public final boolean isMetric()
                {
                    return false;
                }

                /**
                 * {@inheritDoc}
                 */
                @Override
                public final boolean isSAE()
                {
                    return false;
                }
            },

    /**
     * This is the unit for any enumerated structure.  Little more than a tag, this constant
     * exists only for self-documentation and is never displayed to the user.
     */
    ENUMERATED("")
            {
                /**
                 * {@inheritDoc}
                 */
                @Override
                public final boolean isDisplayable()
                {
                    return false;
                }

                /**
                 * {@inheritDoc}
                 */
                @Override
                public final boolean isMetric()
                {
                    return false;
                }

                /**
                 * {@inheritDoc}
                 */
                @Override
                public final boolean isSAE()
                {
                    return false;
                }
            },

    /**
     * This is the unit for volts.
     */
    VOLTS("V")
            {
                /**
                 * {@inheritDoc}
                 */
                @Override
                public final boolean isDisplayable()
                {
                    return true;
                }

                /**
                 * {@inheritDoc}
                 */
                @Override
                public final boolean isMetric()
                {
                    return false;
                }

                /**
                 * {@inheritDoc}
                 */
                @Override
                public final boolean isSAE()
                {
                    return false;
                }
            },

    /**
     * This is the unit for milli-volts (1/1000 of a volt).
     */
    MILLI_VOLTS("mV")
            {
                /**
                 * {@inheritDoc}
                 */
                @Override
                public final boolean isDisplayable()
                {
                    return true;
                }

                /**
                 * {@inheritDoc}
                 */
                @Override
                public final boolean isMetric()
                {
                    return false;
                }

                /**
                 * {@inheritDoc}
                 */
                @Override
                public final boolean isSAE()
                {
                    return false;
                }
            },

    /**
     * This is the unit for amperes.
     */
    AMPERES("A")
            {
                /**
                 * {@inheritDoc}
                 */
                @Override
                public final boolean isDisplayable()
                {
                    return true;
                }

                /**
                 * {@inheritDoc}
                 */
                @Override
                public final boolean isMetric()
                {
                    return false;
                }

                /**
                 * {@inheritDoc}
                 */
                @Override
                public final boolean isSAE()
                {
                    return false;
                }
            },

    /**
     * This is the unit for milli-amperes (1/1000 of an ampere).
     */
    MILLI_AMPERES("mA")
            {
                /**
                 * {@inheritDoc}
                 */
                @Override
                public final boolean isDisplayable()
                {
                    return true;
                }

                /**
                 * {@inheritDoc}
                 */
                @Override
                public final boolean isMetric()
                {
                    return false;
                }

                /**
                 * {@inheritDoc}
                 */
                @Override
                public final boolean isSAE()
                {
                    return false;
                }
            },

    /**
     * This is the unit for ohms.
     */
    OHMS("Ω")
            {
                /**
                 * {@inheritDoc}
                 */
                @Override
                public final boolean isDisplayable()
                {
                    return true;
                }

                /**
                 * {@inheritDoc}
                 */
                @Override
                public final boolean isMetric()
                {
                    return false;
                }

                /**
                 * {@inheritDoc}
                 */
                @Override
                public final boolean isSAE()
                {
                    return false;
                }
            },

    /**
     * This is the unit for mega-ohms (100,000 ohms).
     */
    MEGA_OHMS("MΩ")
            {
                /**
                 * {@inheritDoc}
                 */
                @Override
                public final boolean isDisplayable()
                {
                    return true;
                }

                /**
                 * {@inheritDoc}
                 */
                @Override
                public final boolean isMetric()
                {
                    return false;
                }

                /**
                 * {@inheritDoc}
                 */
                @Override
                public final boolean isSAE()
                {
                    return false;
                }
            },

    /**
     * This is the unit for kilo-ohms (1,000 ohms).
     */
    KILO_OHMS("kΩ")
            {
                /**
                 * {@inheritDoc}
                 */
                @Override
                public final boolean isDisplayable()
                {
                    return true;
                }

                /**
                 * {@inheritDoc}
                 */
                @Override
                public final boolean isMetric()
                {
                    return false;
                }

                /**
                 * {@inheritDoc}
                 */
                @Override
                public final boolean isSAE()
                {
                    return false;
                }
            },

    /**
     * This is the unit for milli-ohms (1/1000 of an ohm).
     */
    MILLI_OHMS("mΩ")
            {
                /**
                 * {@inheritDoc}
                 */
                @Override
                public final boolean isDisplayable()
                {
                    return true;
                }

                /**
                 * {@inheritDoc}
                 */
                @Override
                public final boolean isMetric()
                {
                    return false;
                }

                /**
                 * {@inheritDoc}
                 */
                @Override
                public final boolean isSAE()
                {
                    return false;
                }
            },

    /**
     * This is the unit for gallons per hour.
     */
    GALLONS_PER_HOUR("g/h")
            {
                /**
                 * {@inheritDoc}
                 */
                @Override
                public final boolean isDisplayable()
                {
                    return true;
                }

                /**
                 * {@inheritDoc}
                 */
                @Override
                public final boolean isMetric()
                {
                    return false;
                }

                /**
                 * {@inheritDoc}
                 */
                @Override
                public final boolean isSAE()
                {
                    return true;
                }
            },
    /**
     * This is the unit for liters per hour.
     */
    LITERS_PER_HOUR("l/h")
            {
                /**
                 * {@inheritDoc}
                 */
                @Override
                public final boolean isDisplayable()
                {
                    return true;
                }

                /**
                 * {@inheritDoc}
                 */
                @Override
                public final boolean isMetric()
                {
                    return true;
                }

                /**
                 * {@inheritDoc}
                 */
                @Override
                public final boolean isSAE()
                {
                    return false;
                }
            },
    /**
     * This is the unit for miles per gallon.
     */
    MILES_PER_GALLON("mpg")
            {
                /**
                 * {@inheritDoc}
                 */
                @Override
                public final boolean isDisplayable()
                {
                    return true;
                }

                /**
                 * {@inheritDoc}
                 */
                @Override
                public final boolean isMetric()
                {
                    return false;
                }

                /**
                 * {@inheritDoc}
                 */
                @Override
                public final boolean isSAE()
                {
                    return true;
                }
            },

    /**
     * This is the unit for kilometers per gallon.
     */
    KILOMETERS_PER_GALLON("kmpg")
            {
                /**
                 * {@inheritDoc}
                 */
                @Override
                public final boolean isDisplayable()
                {
                    return true;
                }

                /**
                 * {@inheritDoc}
                 */
                @Override
                public final boolean isMetric()
                {
                    return true;
                }

                /**
                 * {@inheritDoc}
                 */
                @Override
                public final boolean isSAE()
                {
                    return false;
                }
            },

    /**
     * This is the unit for newton-meters.
     */
    NEWTON_METERS("NM")
            {
                /**
                 * {@inheritDoc}
                 */
                @Override
                public final boolean isDisplayable()
                {
                    return true;
                }

                /**
                 * {@inheritDoc}
                 */
                @Override
                public final boolean isMetric()
                {
                    return true;
                }

                /**
                 * {@inheritDoc}
                 */
                @Override
                public final boolean isSAE()
                {
                    return false;
                }
            },
    /**
     * This is the unit for foot-pounds.
     */
    FOOT_POUNDS("lbs")
            {
                /**
                 * {@inheritDoc}
                 */
                @Override
                public final boolean isDisplayable()
                {
                    return true;
                }

                /**
                 * {@inheritDoc}
                 */
                @Override
                public final boolean isMetric()
                {
                    return false;
                }

                /**
                 * {@inheritDoc}
                 */
                @Override
                public final boolean isSAE()
                {
                    return true;
                }
            },
    /**
     * This is the unit for numerical percentage.
     */
    PERCENT("%")
            {
                /**
                 * {@inheritDoc}
                 */
                @Override
                public final boolean isDisplayable()
                {
                    return true;
                }

                /**
                 * {@inheritDoc}
                 */
                @Override
                public final boolean isMetric()
                {
                    return false;
                }

                /**
                 * {@inheritDoc}
                 */
                @Override
                public final boolean isSAE()
                {
                    return false;
                }
            },
    /**
     * This is the unit for the measure of angular rotation in degrees.
     */
    ANGLE_DEGREES("°")
            {
                /**
                 * {@inheritDoc}
                 */
                @Override
                public final boolean isDisplayable()
                {
                    return true;
                }

                /**
                 * {@inheritDoc}
                 */
                @Override
                public final boolean isMetric()
                {
                    return false;
                }

                /**
                 * {@inheritDoc}
                 */
                @Override
                public final boolean isSAE()
                {
                    return false;
                }
            },
    /**
     * This is the unit for the measure of angular rotation in radians.
     */
    ANGLE_RADIANS("rad")
            {
                /**
                 * {@inheritDoc}
                 */
                @Override
                public final boolean isDisplayable()
                {
                    return true;
                }

                /**
                 * {@inheritDoc}
                 */
                @Override
                public final boolean isMetric()
                {
                    return false;
                }

                /**
                 * {@inheritDoc}
                 */
                @Override
                public final boolean isSAE()
                {
                    return false;
                }
            },
    /**
     * This is the unit of rotations per minute.
     */
    ROTATIONS_PER_MINUTE("rpm")
            {
                /**
                 * {@inheritDoc}
                 */
                @Override
                public final boolean isDisplayable()
                {
                    return true;
                }

                /**
                 * {@inheritDoc}
                 */
                @Override
                public final boolean isMetric()
                {
                    return false;
                }

                /**
                 * {@inheritDoc}
                 */
                @Override
                public final boolean isSAE()
                {
                    return false;
                }
            },

    /**
     * This is the pressure unit of pascals.
     */
    PASCALS("PA")
            {
                /**
                 * {@inheritDoc}
                 */
                @Override
                public final boolean isDisplayable()
                {
                    return true;
                }

                /**
                 * {@inheritDoc}
                 */
                @Override
                public final boolean isMetric()
                {
                    return true;
                }

                /**
                 * {@inheritDoc}
                 */
                @Override
                public final boolean isSAE()
                {
                    return false;
                }
            },

    /**
     * This is the pressure unit of kilo-pascals (1,000 pascals).
     */
    KILO_PASCALS("kPA")
            {
                /**
                 * {@inheritDoc}
                 */
                @Override
                public final boolean isDisplayable()
                {
                    return true;
                }

                /**
                 * {@inheritDoc}
                 */
                @Override
                public final boolean isMetric()
                {
                    return true;
                }

                /**
                 * {@inheritDoc}
                 */
                @Override
                public final boolean isSAE()
                {
                    return false;
                }
            },

    /**
     * This is the pressure unit of inches of mercury
     */
    INCHES_OF_MERCURY("\"HG")
            {
                /**
                 * {@inheritDoc}
                 */
                @Override
                public final boolean isDisplayable()
                {
                    return true;
                }

                /**
                 * {@inheritDoc}
                 */
                @Override
                public final boolean isMetric()
                {
                    return false;
                }

                /**
                 * {@inheritDoc}
                 */
                @Override
                public final boolean isSAE()
                {
                    return true;
                }
            },

    /**
     * This is the time unit seconds.
     */
    SECONDS("sec")
            {
                /**
                 * {@inheritDoc}
                 */
                @Override
                public final boolean isDisplayable()
                {
                    return true;
                }

                /**
                 * {@inheritDoc}
                 */
                @Override
                public final boolean isMetric()
                {
                    return false;
                }

                /**
                 * {@inheritDoc}
                 */
                @Override
                public final boolean isSAE()
                {
                    return false;
                }
            },
    /**
     * This is the time unit minutes.
     */
    MINUTES("min")
            {
                /**
                 * {@inheritDoc}
                 */
                @Override
                public final boolean isDisplayable()
                {
                    return true;
                }

                /**
                 * {@inheritDoc}
                 */
                @Override
                public final boolean isMetric()
                {
                    return false;
                }

                /**
                 * {@inheritDoc}
                 */
                @Override
                public final boolean isSAE()
                {
                    return false;
                }
            },
    /**
     * This is the time unit hours.
     */
    HOURS("hr")
            {
                /**
                 * {@inheritDoc}
                 */
                @Override
                public final boolean isDisplayable()
                {
                    return true;
                }

                /**
                 * {@inheritDoc}
                 */
                @Override
                public final boolean isMetric()
                {
                    return false;
                }

                /**
                 * {@inheritDoc}
                 */
                @Override
                public final boolean isSAE()
                {
                    return false;
                }
            },
    /**
     * This is the time unit days.
     */
    DAYS("day")
            {
                /**
                 * {@inheritDoc}
                 */
                @Override
                public final boolean isDisplayable()
                {
                    return true;
                }

                /**
                 * {@inheritDoc}
                 */
                @Override
                public final boolean isMetric()
                {
                    return false;
                }

                /**
                 * {@inheritDoc}
                 */
                @Override
                public final boolean isSAE()
                {
                    return false;
                }
            },
    /**
     * This is the distance unit miles.
     */
    MILES("mi")
            {
                /**
                 * {@inheritDoc}
                 */
                @Override
                public final boolean isDisplayable()
                {
                    return true;
                }

                /**
                 * {@inheritDoc}
                 */
                @Override
                public final boolean isMetric()
                {
                    return false;
                }

                /**
                 * {@inheritDoc}
                 */
                @Override
                public final boolean isSAE()
                {
                    return true;
                }
            },

    /**
     * This is the distnce unit kilometers.
     */
    KILOMETERS("kM")
            {
                /**
                 * {@inheritDoc}
                 */
                @Override
                public final boolean isDisplayable()
                {
                    return true;
                }

                /**
                 * {@inheritDoc}
                 */
                @Override
                public final boolean isMetric()
                {
                    return true;
                }

                /**
                 * {@inheritDoc}
                 */
                @Override
                public final boolean isSAE()
                {
                    return false;
                }
            },

    /**
     * This is the speed unit miles per hour.
     */
    MILES_PER_HOUR("mph")
            {
                /**
                 * {@inheritDoc}
                 */
                @Override
                public final boolean isDisplayable()
                {
                    return true;
                }

                /**
                 * {@inheritDoc}
                 */
                @Override
                public final boolean isMetric()
                {
                    return false;
                }

                /**
                 * {@inheritDoc}
                 */
                @Override
                public final boolean isSAE()
                {
                    return true;
                }
            },

    /**
     * This is the speed unit kilometers per hour
     */
    KILOMETERS_PER_HOUR("kph")
            {
                /**
                 * {@inheritDoc}
                 */
                @Override
                public final boolean isDisplayable()
                {
                    return true;
                }

                /**
                 * {@inheritDoc}
                 */
                @Override
                public final boolean isMetric()
                {
                    return true;
                }

                /**
                 * {@inheritDoc}
                 */
                @Override
                public final boolean isSAE()
                {
                    return false;
                }
            },
    /**
     * This is the flow rate unit grams per second.
     */
    GRAMS_PER_SECOND("gm/s")
            {
                /**
                 * {@inheritDoc}
                 */
                @Override
                public final boolean isDisplayable()
                {
                    return true;
                }

                /**
                 * {@inheritDoc}
                 */
                @Override
                public final boolean isMetric()
                {
                    return true;
                }

                /**
                 * {@inheritDoc}
                 */
                @Override
                public final boolean isSAE()
                {
                    return false;
                }
            },

    /**
     * This is the temperature unit fahrenheit.
     */
    TEMPERATURE_FAHRENHEIT("°F")
            {
                /**
                 * {@inheritDoc}
                 */
                @Override
                public final boolean isDisplayable()
                {
                    return true;
                }

                /**
                 * {@inheritDoc}
                 */
                @Override
                public final boolean isMetric()
                {
                    return false;
                }

                /**
                 * {@inheritDoc}
                 */
                @Override
                public final boolean isSAE()
                {
                    return false;
                }
            },

    /**
     * This is the temperature unit celsius.
     */
    TEMPERATURE_CELSIUS("°C")
            {
                /**
                 * {@inheritDoc}
                 */
                @Override
                public final boolean isDisplayable()
                {
                    return true;
                }

                /**
                 * {@inheritDoc}
                 */
                @Override
                public final boolean isMetric()
                {
                    return false;
                }

                /**
                 * {@inheritDoc}
                 */
                @Override
                public final boolean isSAE()
                {
                    return false;
                }
            },

    /**
     * This is the pressure unit pounds per square inch
     */
    PSI("psi")
            {
                /**
                 * {@inheritDoc}
                 */
                @Override
                public final boolean isDisplayable()
                {
                    return true;
                }

                /**
                 * {@inheritDoc}
                 */
                @Override
                public final boolean isMetric()
                {
                    return false;
                }

                /**
                 * {@inheritDoc}
                 */
                @Override
                public final boolean isSAE()
                {
                    return true;
                }
            },

    /**
     * This is the flow rate unit pounds per minute
     */
    POUNDS_PER_MINUTE("lb/min")
            {
                /**
                 * {@inheritDoc}
                 */
                @Override
                public final boolean isDisplayable()
                {
                    return true;
                }

                /**
                 * {@inheritDoc}
                 */
                @Override
                public final boolean isMetric()
                {
                    return false;
                }

                /**
                 * {@inheritDoc}
                 */
                @Override
                public final boolean isSAE()
                {
                    return true;
                }
            },

    /**
     * This is the pressure unit inches of water
     */
    INCHES_OF_WATER("in/h2o")
            {
                /**
                 * {@inheritDoc}
                 */
                @Override
                public final boolean isDisplayable()
                {
                    return true;
                }

                /**
                 * {@inheritDoc}
                 */
                @Override
                public final boolean isMetric()
                {
                    return false;
                }

                /**
                 * {@inheritDoc}
                 */
                @Override
                public final boolean isSAE()
                {
                    return true;
                }
            },
    /**
     * This is the unit for a boolean.  Little more than a tag, this constant exists only for
     * self-documentation and should never be displayed to the user.
     */
    BOOLEAN("")
            {
                /**
                 * {@inheritDoc}
                 */
                @Override
                public final boolean isDisplayable()
                {
                    return false;
                }

                /**
                 * {@inheritDoc}
                 */
                @Override
                public final boolean isMetric()
                {
                    return false;
                }

                /**
                 * {@inheritDoc}
                 */
                @Override
                public final boolean isSAE()
                {
                    return false;
                }
            },

    /**
     * This is the unit for an integral accumulator.  Little more than a tag, this constant exists
     * only for self-documentation and should never be displayed to the user.
     */
    ACCUMULATED_NUMBER("")
            {
                /**
                 * {@inheritDoc}
                 */
                @Override
                public final boolean isDisplayable()
                {
                    return false;
                }

                /**
                 * {@inheritDoc}
                 */
                @Override
                public final boolean isMetric()
                {
                    return false;
                }

                /**
                 * {@inheritDoc}
                 */
                @Override
                public final boolean isSAE()
                {
                    return false;
                }
            },
    /**
     * This is the unit for packeted data.  Little more than a tag, this constant exists only for
     * self-documentation and should never be displayed to the user.  Packeted data is generally
     * broken apart and displayed to the user piece by piece.
     */
    PACKETED("")
            {
                /**
                 * {@inheritDoc}
                 */
                @Override
                public final boolean isDisplayable()
                {
                    return false;
                }

                /**
                 * {@inheritDoc}
                 */
                @Override
                public final boolean isMetric()
                {
                    return false;
                }

                /**
                 * {@inheritDoc}
                 */
                @Override
                public final boolean isSAE()
                {
                    return false;
                }
            },
    /**
     * This is the unit for a {@link com.lukeleber.scandroid.sae.j2012.DiagnosticTroubleCode}.
     * Little more than a tag, this constant exists only for self-documentation and should never be
     * displayed to the user.
     */
    DIAGNOSTIC_TROUBLE_CODE("")
            {
                /**
                 * {@inheritDoc}
                 */
                @Override
                public final boolean isDisplayable()
                {
                    return false;
                }

                /**
                 * {@inheritDoc}
                 */
                @Override
                public final boolean isMetric()
                {
                    return false;
                }

                /**
                 * {@inheritDoc}
                 */
                @Override
                public final boolean isSAE()
                {
                    return false;
                }
            };

    /// Required by the {@link android.os.Parcelable} interface
    public final static Parcelable.Creator<Unit> CREATOR = new Parcelable.Creator<Unit>()
    {

        /**
         * {@inheritDoc}
         */
        @Override
        public Unit createFromParcel(Parcel source)
        {
            return Unit.values()[source.readByte() & 0xFF];
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Unit[] newArray(int size)
        {
            return new Unit[size];
        }
    };

    /// The display character string of this <code>Unit</code>
    private final String display;

    /**
     * {@inheritDoc}
     */
    @Override
    public int describeContents()
    {
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeToParcel(Parcel out,
                              int flags)
    {
        out.writeByte((byte) ordinal());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String toString()
    {
        return display;
    }

    /**
     * Constructs a <code>Unit</code> with the provided display character string
     *
     * @param display the display character string of this unit
     */
    private Unit(String display)
    {
        this.display = display;
    }

    /**
     * Does this <code>Unit</code> have a displayable character string?
     *
     * @return true if a displayable character string is available, otherwise false
     *
     */
    public abstract boolean isDisplayable();

    /**
     * Is this <code>Unit</code> a metric unit?
     *
     * @return true if this is a metric unit, otherwise false
     */
    public abstract boolean isMetric();

    /**
     * Is this <code>Unit</code> a SAE unit?
     *
     * @return true if this is a SAE unit, otherwise false
     */
    public abstract boolean isSAE();

}
