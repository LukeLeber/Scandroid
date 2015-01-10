/*
 * This file is protected under the KILLGPL.
 * For more information visit <insert_valid_link_to_killgpl_here>
 * <p/>
 * Copyright (c) Luke A. Leber <LukeLeber@gmail.com> 2015
 */

package com.lukeleber.scandroid.gui.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lukeleber.scandroid.R;
import com.lukeleber.scandroid.sae.Service;

import butterknife.OnClick;
import butterknife.Optional;

public class DiagnosticTroubleCodeDisplay
        extends ServiceFragment
{

    @Optional
    @OnClick(R.id.fragment_diagnostic_trouble_code_display_code_scan_button)
    void onScanClicked()
    {
        /// TODO: Query $0102 for # responses and provide custom listener to receive # of lines
        ///  I'm not 100% sure of the format of the incoming multi-line response.  TODO: Try it out!
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_diagnostic_trouble_code_display, container,
                                false);
    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
    }

    @Override
    public Service getService()
    {
        return Service.RETRIEVE_DTC;
    }

}
