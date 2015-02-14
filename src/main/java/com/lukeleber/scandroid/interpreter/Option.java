// This file is protected under the KILLGPL.
// For more information, visit http://www.lukeleber.github.io/KILLGPL.html
//
// Copyright (c) Luke Leber <LukeLeber@gmail.com>

package com.lukeleber.scandroid.interpreter;

/**
 * A simple wrapper class for an arbitrary type that supports custom serialization.
 *
 * @param <T>
 *         the underlying data type
 */
public interface Option<T>
{
    /**
     * Retrieves the underlying representation of the option
     *
     * @return the underlying representation of the option
     */
    T getOption();

    /**
     * Retrieves a serialized representation of the option
     *
     * @return a serialized representation of the option
     */
    byte[] serialize();
}
