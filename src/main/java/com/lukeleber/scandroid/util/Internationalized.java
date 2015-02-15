// This file is protected under the KILLGPL.
// For more information, visit http://www.lukeleber.github.io/KILLGPL.html
//
// Copyright (c) Luke Leber <LukeLeber@gmail.com>

package com.lukeleber.scandroid.util;

import android.content.Context;

/**
 * The {@link com.lukeleber.scandroid.util.Internationalized} interface provides an alternative
 * to the {@link #toString()} method that allows for an internationalized string to be returned
 * based upon the {@link android.content.Context} that the method is invoked with.
 *
 */
public interface Internationalized
{

    /**
     * Retrieves an internationalized {@link java.lang.String} representation of this object
     *
     * @param context the calling {@link android.content.Context}
     *
     * @return an internationalized {@link java.lang.String} representation of this object
     *
     */
    String toI18NString(Context context);
}
