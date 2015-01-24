// This file is protected under the KILLGPL.
// For more information, visit http://www.lukeleber.github.io/KILLGPL.html
//
// Copyright (c) Luke Leber <LukeLeber@gmail.com>

package com.lukeleber.scandroid;

import android.content.Context;

import com.lukeleber.scandroid.interpreter.Interpreter;
import com.lukeleber.scandroid.sae.j1979.Profile;

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

    public enum I18N_STRING
    {
        YES_CAPS,
        NO_CAPS,
        ON_CAPS,
        OFF_CAPS,
        NOT_AVAILABLE,
        TOUCH_TO_EXPAND,
        SUPPORTED_CAPS,
        UNSUPPORTED_CAPS
    }

    private final static String[] STRING_CACHE = new String[I18N_STRING.values().length];
    private static boolean stringCacheInitialized = false;

    public static void initStringCache(Context context)
    {
        if(!stringCacheInitialized)
        {
            stringCacheInitialized = true;
            STRING_CACHE[I18N_STRING.YES_CAPS.ordinal()] = context.getString(R.string.yes_caps);
            STRING_CACHE[I18N_STRING.NO_CAPS.ordinal()] = context.getString(R.string.no_caps);
            STRING_CACHE[I18N_STRING.ON_CAPS.ordinal()] = context.getString(R.string.on_caps);
            STRING_CACHE[I18N_STRING.OFF_CAPS.ordinal()] = context.getString(R.string.off_caps);
            STRING_CACHE[I18N_STRING.NOT_AVAILABLE.ordinal()] = context.getString(R.string.not_available);
            STRING_CACHE[I18N_STRING.TOUCH_TO_EXPAND.ordinal()] = context.getString(R.string.touch_to_expand);
            STRING_CACHE[I18N_STRING.SUPPORTED_CAPS.ordinal()] = context.getString(R.string.supported_caps);
            STRING_CACHE[I18N_STRING.UNSUPPORTED_CAPS.ordinal()] = context.getString(R.string.unsupported_caps);
        }
    }

    public static String getString(I18N_STRING which)
    {
        return STRING_CACHE[which.ordinal()];
    }

    /// <-- Interpreters based on context (outlive the application lifecycle)
    private final static Map<Context, Interpreter<?>> interpreters
            = Collections.synchronizedMap(new HashMap<Context, Interpreter<?>>());

    @SuppressWarnings("unchecked")
    public static <T> Interpreter<T> getInterpreter(Context key)
    {
        return (Interpreter<T>) interpreters.get(key);
    }

    @SuppressWarnings("unchecked")
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
