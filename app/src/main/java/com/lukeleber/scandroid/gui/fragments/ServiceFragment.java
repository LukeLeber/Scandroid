/*
 * This file is protected under the KILLGPL.
 * For more information visit <insert_valid_link_to_killgpl_here>
 * <p/>
 * Copyright (c) Luke A. Leber <LukeLeber@gmail.com> 2015
 */

package com.lukeleber.scandroid.gui.fragments;

import android.app.Activity;
import android.app.Fragment;

import com.lukeleber.scandroid.gui.InterpreterHost;

public abstract class ServiceFragment
        extends Fragment
{
    protected InterpreterHost host;

    /**
     * {@inheritDoc}
     *
     */
    @Override
    public final void onAttach(Activity activity)
    {
        super.onAttach(activity);
        if (!InterpreterHost.class.isAssignableFrom(activity.getClass()))
        {
            throw new ClassCastException(activity.toString()
                    + " must implement " +
                    InterpreterHost.class.getName());
        }
        this.host = (InterpreterHost) activity;
    }

    @Override
    public final void onDetach()
    {
        super.onDetach();
        this.host = null;
    }
}
