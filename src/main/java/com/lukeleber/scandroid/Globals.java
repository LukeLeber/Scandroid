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
 *
 * Seriously, who designed the application lifecycle API anyway?
 */
public class Globals
{

    private final static Map<Context, Interpreter> interpreters
            = Collections.synchronizedMap(new HashMap<Context, Interpreter>());

    public static Interpreter getInterpreter(Context key)
    {
        return interpreters.get(key);
    }

    public static Interpreter setInterpreter(Context key, Interpreter interpreter)
    {
        return interpreters.put(key, interpreter);
    }

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

}
