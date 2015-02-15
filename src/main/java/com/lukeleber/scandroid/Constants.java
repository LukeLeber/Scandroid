// This file is protected under the KILLGPL.
// For more information, visit http://www.lukeleber.github.io/KILLGPL.html
//
// Copyright (c) Luke Leber <LukeLeber@gmail.com>

package com.lukeleber.scandroid;

/**
 * An uninstantiable class that contains static members and utility methods.
 *
 * TODO: Provide convenience lambdas when dalvik supports the construct
 *
 */
public final class Constants
{

    /**
     * Uninstantiable
     */
    private Constants()
    {

    }

    /**
     * The conversion multiplier constant to change kilo-pascals to pounds per square inch
     */
    public final static float KILO_PASCAL_TO_PSI_FACTOR = 6.89475729f;

    /**
     * The conversion multiplier constant to change pounds per square inch to kilo-pascals
     */
    public final static float PSI_TO_KILO_PASCAL_FACTOR = 1 / KILO_PASCAL_TO_PSI_FACTOR;

    /**
     * The conversion multiplier constant to change kilometers to miles
     */
    public final static float KILOMETERS_TO_MILES = 0.621f;

    /**
     * The conversion multiplier constant to change grams per second to pounds per minute
     */
    public final static float GRAMS_PER_SEC_TO_POUNDS_PER_MIN = 0.132277357f;

    /**
     * The conversion multiplier constant to change pascals to inches of water
     */
    public static final float PASCALS_TO_INCHES_H2O = 0.004014631330f;

    /**
     * The conversion multiplier constant to change kilo-pascals to inches of mercury
     */
    public static final float KILO_PASCAL_TO_INCHES_MERCURY = 0.295299875f;
}
