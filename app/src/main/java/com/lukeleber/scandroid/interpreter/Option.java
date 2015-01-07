/*
 * This file is protected under the KILLGPL.
 * For more information visit <insert_valid_link_to_killgpl_here>
 * <p/>
 * Copyright (c) Luke A. Leber <LukeLeber@gmail.com> 2014
 */

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
