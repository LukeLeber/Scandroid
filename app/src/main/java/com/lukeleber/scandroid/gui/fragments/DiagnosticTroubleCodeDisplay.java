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

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.lukeleber.scandroid.R;
import com.lukeleber.scandroid.interpreter.FailureCode;
import com.lukeleber.scandroid.interpreter.Handler;
import com.lukeleber.scandroid.interpreter.ResponseListener;
import com.lukeleber.scandroid.interpreter.ServiceRequest;
import com.lukeleber.scandroid.interpreter.elm327.OpCode;
import com.lukeleber.scandroid.sae.MonitorStatus;
import com.lukeleber.scandroid.sae.j1979.Service;
import com.lukeleber.scandroid.sae.detail.AppendixB;
import com.lukeleber.scandroid.sae.j2012.DiagnosticTroubleCode;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.Optional;

public class DiagnosticTroubleCodeDisplay
        extends ServiceFragment
{

    @Optional
    @InjectView(R.id.fragment_diagnostic_trouble_code_display_code_listview)
    ListView codeView;

    private final List<DiagnosticTroubleCode> model = new ArrayList<>();

    @Optional
    @InjectView(R.id.fragment_diagnostic_trouble_code_display_code_scan_button)
    Button codeScanButton;

    void onCodesRead(String[] responses)
    {
        model.clear();
        for(String response : responses)
        {
            int i = 2;
            String dtc = response.substring(i, i + 4);;
            do
            {
                model.add(new DiagnosticTroubleCode(Integer.parseInt(dtc, 16), "Cylinder 3 Misfire Detected"));
                i += 4;
                dtc = response.substring(i, i + 4);
            }
            while(i < 14 && !"0000".equals(dtc));
        }
        codeView.invalidateViews();
    }

    void onCodesReadCAN(String[] responses)
    {

    }

    @Optional
    @OnClick(R.id.fragment_diagnostic_trouble_code_display_code_scan_button)
    @SuppressWarnings("unchecked")
    void onScanClicked()
    {
        host.getInterpreter().sendRequest(new ServiceRequest(Service.LIVE_DATASTREAM,
                AppendixB.MONITOR_STATUS, new Handler<MonitorStatus>()
        {
            @Override
            public void onResponse(final MonitorStatus value)
            {
                host.getInterpreter().sendRequest(new ServiceRequest(Service.RETRIEVE_DTC), new ResponseListener<String>()
                {
                    @Override
                    public void onSuccess(String message)
                    {
                        if(!message.equals(OpCode.ELM327_NO_DATA + (char)13))
                        {
                            switch(host.getProfile().getProtocol())
                            {
                                case SAE_J1850_PWM:
                                case SAE_J1850_VPW:
                                case ISO_14230_4_KWP_5_BAUD_INIT:
                                case ISO_14230_4_KWP_FAST_INIT:
                                    onCodesRead(message.split("\n"));
                                    break;
                                case ISO_15765_4_CAN_11_BIT_500_KBAUD:
                                case ISO_15765_4_CAN_29_BIT_500_KBAUD:
                                case ISO_15765_4_CAN_11_BIT_250_KBAUD:
                                case ISO_15765_4_CAN_29_BIT_250_KBAUD:
                                case SAE_J1939_CAN:
                                case USER_1_CAN_11_BIT_125_KBAUD:
                                case USER_2_CAN_11_BIT_125_KBAUD:
                                    onCodesReadCAN(message.split("\n"));
                                    break;
                            }
                        }
                        else
                        {
                            model.add(new DiagnosticTroubleCode(0, "No codes present"));
                            codeView.invalidateViews();
                        }

                    }

                    @Override
                    public void onFailure(FailureCode code)
                    {

                    }
                });
                codeScanButton.setText("# DTC: " + value.getDiagnosticTroubleCodeCount());

            }

            @Override
            public void onFailure(final FailureCode code)
            {
                codeScanButton.setText("# DTC: Unknown");
            }
        }
        ));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View rv = inflater.inflate(R.layout.fragment_diagnostic_trouble_code_display, container,
                                false);
        ButterKnife.inject(this, rv);
        codeView.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, model));
        return rv;
    }

}
