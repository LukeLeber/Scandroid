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

import butterknife.ButterKnife;
import butterknife.OnClick;
import com.lukeleber.scandroid.R;
import com.lukeleber.scandroid.sae.Service;

public class ResetDiagnosticInformation
        extends ServiceFragment
{

    @OnClick(R.id.reset_diagnostic_information)
    void onResetDiagnosticsClicked()
    {

    }

    public ResetDiagnosticInformation()
    {
        // Required empty public constructor
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
        View rv =  inflater.inflate(R.layout.fragment_reset_diagnostic_information, container, false);
        ButterKnife.inject(this, rv);
        return rv;
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
        return Service.CLEAR_DTC;
    }

}
