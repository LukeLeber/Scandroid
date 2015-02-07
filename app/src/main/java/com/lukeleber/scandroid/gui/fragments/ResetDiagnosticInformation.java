// This file is protected under the KILLGPL.
// For more information, visit http://www.lukeleber.github.io/KILLGPL.html
//
// Copyright (c) Luke Leber <LukeLeber@gmail.com>

package com.lukeleber.scandroid.gui.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.lukeleber.scandroid.R;
import com.lukeleber.scandroid.interpreter.FailureCode;
import com.lukeleber.scandroid.interpreter.Handler;
import com.lukeleber.scandroid.interpreter.ServiceRequest;
import com.lukeleber.scandroid.sae.j1979.Service;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A {@link com.lukeleber.scandroid.gui.fragments.ServiceFragment} that provides graphical access
 * to service mode $04 as defined by SAE J1979 §5.4 and SAE J1979 §6.4.  This GUI also meets the
 * requirements set forth by SAE J1978 §7.7.
 */
public class ResetDiagnosticInformation
        extends ServiceFragment
{

    private final DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener()
    {
        @Override
        public void onClick(DialogInterface dialog, int which)
        {
            host.getInterpreter().sendRequest(
                new ServiceRequest<String, String>(Service.CLEAR_DTC,
                    new Handler<String>()
                    {
                        @Override
                        public void onResponse(String value)
                        {
                            Toast.makeText(getActivity(), getString(
                                R.string.fragment_reset_diagnostic_information_success),
                                Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(FailureCode code)
                        {
                            Toast.makeText(getActivity(), String.format(getString(
                                R.string.fragment_reset_diagnostic_information_failure),
                                code.toString()), Toast.LENGTH_SHORT).show();
                        }
                    }
                )
            );
        }
    };

    @OnClick(R.id.reset_diagnostic_information)
    @SuppressWarnings("unused")
    void onResetDiagnosticsClicked()
    {
        new AlertDialog.Builder(getActivity())
                .setTitle(getString(
                    R.string.fragment_reset_diagnostic_information_dialog_title))
                .setMessage(getString(
                    R.string.fragment_reset_diagnostic_information_dialog_message))
                .setPositiveButton(getString(
                    R.string.fragment_reset_diagnostic_information_dialog_positive_button_label),
                    listener)
                .setNegativeButton(getString(
                    R.string.fragment_reset_diagnostic_information_dialog_negative_button_label),
                    null)
                .create().show();
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
        View rv = inflater.inflate(R.layout.fragment_reset_diagnostic_information, container,
                false);
        ButterKnife.inject(this, rv);
        return rv;
    }
}
