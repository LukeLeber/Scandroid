//  This file is protected under the KILLGPL.
//  For more information, visit http://www.lukeleber.github.io/KILLGPL.html
//
//  Copyright (c) Luke Leber <LukeLeber@gmail.com>

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
