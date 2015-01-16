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
