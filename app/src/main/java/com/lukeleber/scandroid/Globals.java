/*
 * This file is protected under the KILLGPL.
 * For more information visit <insert_valid_link_to_killgpl_here>
 * <p/>
 * Copyright (c) Luke A. Leber <LukeLeber@gmail.com> 2014
 */

package com.lukeleber.scandroid;

import android.content.Context;

import com.lukeleber.scandroid.interpreter.Interpreter;
import com.lukeleber.scandroid.sae.Profile;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Seems like a hack.  I really hate the android way of doing things...
 * <p/>
 * Seriously, who designed the application lifecycle API anyway?
 */
public class Globals
{

    /// <-- Interpreters based on context (outlive the application lifecycle)
    private final static Map<Context, Interpreter<?>> interpreters
            = Collections.synchronizedMap(new HashMap<Context, Interpreter<?>>());

    public static <T> Interpreter<T> getInterpreter(Context key)
    {
        return (Interpreter<T>) interpreters.get(key);
    }

    public static <T> Interpreter<T> setInterpreter(Context key, Interpreter<T> interpreter)
    {
        return (Interpreter<T>) interpreters.put(key, interpreter);
    }
    /// Interpreters based on context (outlive the application lifecycle) -->

    /// <-- Profiles based on context (outlive the application lifecycle)
    private final static Map<Context, Profile> profiles
            = Collections.synchronizedMap(new HashMap<Context, Profile>());

    public static Profile getProfile(Context key)
    {
        return profiles.get(key);
    }

    public static Profile setProfile(Context key, Profile profile)
    {
        return profiles.put(key, profile);
    }
    /// Profiles based on context (outlive the application lifecycle) -->
}
