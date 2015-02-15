// This file is protected under the KILLGPL.
// For more information, visit http://www.lukeleber.github.io/KILLGPL.html
//
// Copyright (c) Luke Leber <LukeLeber@gmail.com>

package com.lukeleber.scandroid;

import android.content.Context;

import com.lukeleber.scandroid.interpreter.Interpreter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>This class provides a means by which to persist
 * {@link com.lukeleber.scandroid.interpreter.Interpreter} and
 * {@link com.lukeleber.scandroid.sae.j1979.Profile} instances beyond the bounds of the normal
 * {@link android.app.Activity} lifecycle.  Interpreters maintain state that cannot be persisted
 * through the {@link java.io.Serializable} or {@link android.os.Parcelable} interfaces, and
 * as such (at this time, as far as I know) cannot be passed between activities.</p>
 * <p>Artifacts are persisted by in-memory static storage for the life of the process.  Multiple
 * artifacts may be stored on a per-context basis.  This class is internally thread-safe.
 *
 */
public class Globals
{

    /**
     * Uninstantiable
     */
    private Globals()
    {

    }

    /// A mapping of all contexts that are operating an interpreter
    private final static Map<Context, Interpreter> interpreters
            = Collections.synchronizedMap(new HashMap<Context, Interpreter>());


    /**
     * Retrieves the {@link com.lukeleber.scandroid.interpreter.Interpreter} for the provided
     * {@link android.content.Context}
     *
     * @param key the calling {@link android.content.Context}
     *
     * @return the {@link com.lukeleber.scandroid.interpreter.Interpreter} for the provided
     * {@link android.content.Context} or null if there is no
     * {@link com.lukeleber.scandroid.interpreter.Interpreter} bound to the provided
     * {@link android.content.Context}
     *
     * @throws java.lang.NullPointerException if the provided {@link android.content.Context} is
     * null
     *
     */
    public static Interpreter getInterpreter(Context key)
    {
        if(key == null)
        {
            throw new NullPointerException();
        }
        return interpreters.get(key);
    }

    /**
     * Sets the {@link com.lukeleber.scandroid.interpreter.Interpreter} for the provided
     * {@link android.content.Context}
     *
     * @param key the calling {@link android.content.Context}
     *
     * @param interpreter the {@link com.lukeleber.scandroid.interpreter.Interpreter} to set
     *
     * @return the previously set {@link com.lukeleber.scandroid.interpreter.Interpreter} or null
     * if no {@link com.lukeleber.scandroid.interpreter.Interpreter} was previously set
     *
     * @throws java.lang.NullPointerException if the provided {@link android.content.Context} is
     * null
     *
     */
    public static Interpreter setInterpreter(Context key, Interpreter interpreter)
    {
        if(key == null)
        {
            throw new NullPointerException();
        }
        return interpreters.put(key, interpreter);
    }
}
