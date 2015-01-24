// This file is protected under the KILLGPL.
// For more information, visit http://www.lukeleber.github.io/KILLGPL.html
//
// Copyright (c) Luke Leber <LukeLeber@gmail.com>

package com.lukeleber.util;

import android.util.Pair;

import java.io.Serializable;

public class SerializablePair<T extends Serializable, U extends Serializable>
        extends Pair<T, U>
        implements Serializable
{
    /**
     * Constructor for a Pair.
     *
     * @param first
     *         the first object in the Pair
     * @param second
     *         the second object in the pair
     */
    public SerializablePair(T first, U second)
    {
        super(first, second);
    }
}
