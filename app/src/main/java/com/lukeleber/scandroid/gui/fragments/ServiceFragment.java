/*
 * This file is protected under the KILLGPL.
 * For more information visit <insert_valid_link_to_killgpl_here>
 * <p/>
 * Copyright (c) Luke A. Leber <LukeLeber@gmail.com> 2015
 */

package com.lukeleber.scandroid.gui.fragments;

import android.app.Fragment;

import com.lukeleber.scandroid.sae.Service;

public abstract class ServiceFragment extends Fragment
{
    public abstract Service getService();
}
