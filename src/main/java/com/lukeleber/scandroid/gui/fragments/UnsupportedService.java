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

package com.lukeleber.scandroid.gui.fragments;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lukeleber.scandroid.R;

/**
 * A generic "placeholder" fragment for instances when a particular service is not supported
 * by a vehicle.  This placeholder should consist of a brief, simple message stating that the
 * service is not supported.
 */
public class UnsupportedService
        extends Fragment
{

    /**
     * {@inheritDoc}
     *
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_unsupported_service, container, false);
    }


}
