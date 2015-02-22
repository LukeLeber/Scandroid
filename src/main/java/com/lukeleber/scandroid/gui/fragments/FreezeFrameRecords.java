// This file is protected under the KILLGPL.
// For more information, visit http://www.lukeleber.github.io/KILLGPL.html
//
// Copyright (c) Luke Leber <LukeLeber@gmail.com>

package com.lukeleber.scandroid.gui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.lukeleber.scandroid.R;
import com.lukeleber.scandroid.gui.fragments.detail.SAEJ1979AppendixWrapper;
import com.lukeleber.scandroid.gui.fragments.util.AbstractParameterAdapter;
import com.lukeleber.scandroid.gui.fragments.util.ParameterModel;
import com.lukeleber.scandroid.interpreter.FailureCode;
import com.lukeleber.scandroid.interpreter.Handler;
import com.lukeleber.scandroid.interpreter.Interpreter;
import com.lukeleber.scandroid.interpreter.ServiceRequest;
import com.lukeleber.scandroid.sae.j1979.PID;
import com.lukeleber.scandroid.sae.j1979.Profile;
import com.lukeleber.scandroid.sae.j1979.Service;
import com.lukeleber.scandroid.sae.j1979.detail.AppendixB;
import com.lukeleber.scandroid.sae.j2012.DiagnosticTroubleCode;
import com.lukeleber.scandroid.util.Unit;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * <p>A generic implementation of SAE-J1979 Diagnostic Service Mode $02.  This service mode queries
 * the vehicle a single time upon creation (and subsequently when the user clicks the refresh
 * button) for frame #0 that is required by SAE-J1979.  Unlike the live datastream, this
 * datastream does not auto-refresh (as freeze frame records are not likely to be updated as
 * often as the live datastream is).  Manufacturers are permitted to provide additional frames
 * under manufacturer defined conditions and data.
 *
 */
public class FreezeFrameRecords
        extends ServiceFragment
{

    /// The list of PIDs that service $02 supports
    private List<ParameterModel> viewedParameters = new ArrayList<>();

    /// The text view that displays a status text
    /// The value should be either positive (record stored), negative (no record stored),
    /// or error (could not retrieve record).
    @InjectView(R.id.fragment_freeze_frame_records_caption)
    TextView caption;

    @InjectView(R.id.fragment_freeze_frame_records_refresh_button)
    Button refreshButton;

    /// The list view that displays the freeze-frame data received from the vehicle
    @InjectView(R.id.fragment_freeze_frame_records_listview)
    ListView listView;

    /**
     * Invoked when the user clicks on the "Refresh" button, this method refreshes freeze-frame
     * information from the vehicle.
     */
    @OnClick(R.id.fragment_freeze_frame_records_refresh_button)
    @SuppressWarnings("unused")
    void onRefreshClicked()
    {
        refresh();
    }

    @Override
    public void onCreate(Bundle sis)
    {
        super.onCreate(sis);
        Profile profile = host.getProfile();
        if (profile.isServiceSupported(Service.FREEZE_FRAME_DATA))
        {
            for (int i = 1;
                 i < 0xFF;
                 ++i)
            {
                if (profile.isSupported(Service.FREEZE_FRAME_DATA, i))
                {
                    PID<?> pid = profile.getID(Service.FREEZE_FRAME_DATA, i);
                    viewedParameters.add(new ParameterModel<>(SAEJ1979AppendixWrapper.getWrapper(pid, profile)));
                }
            }
        }
        refresh();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        super.onCreateView(inflater,
                container, savedInstanceState);
        View rv = inflater.inflate(R.layout.fragment_freeze_frame_records, container, false);
        ButterKnife.inject(this, rv);
        listView.setAdapter(
                new AbstractParameterAdapter(getActivity())
                {
                    @Override
                    public int getCount()
                    {
                        return viewedParameters.size();
                    }

                    @Override
                    public ParameterModel getItem(int position)
                    {
                        return viewedParameters.get(position);
                    }
                }
                           );
        return rv;
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        this.viewedParameters = null;
        this.listView = null;
    }

    /**
     * Queries the vehicle for updated freeze-frame data.
     */
    private void refresh()
    {
        /// Wrapped in an anonymous object to maintain state variable "remaining"
        /// Basically just an optimization to wait until all responses get here
        /// before invalidating the list view.
        new Runnable()
        {
            final View view = getView();

            /// The number of responses that are still on their way
            int remaining = viewedParameters.size();

            @SuppressWarnings("unchecked")
            @Override
            public void run()
            {
                final Interpreter interpreter = host.getInterpreter();
                interpreter.sendRequest(new ServiceRequest(Service.FREEZE_FRAME_DATA,
                        AppendixB.FREEZE_FRAME_DTC, new Handler<DiagnosticTroubleCode>()
                {

                    @Override
                    public void onResponse(DiagnosticTroubleCode value)
                    {
                        if(value.getBits() == 0)
                        {
                            caption.setText(getString(R.string.fragment_freeze_frame_records_no_records));
                            refreshButton.setVisibility(View.INVISIBLE);
                            listView.setVisibility(View.INVISIBLE);
                        }
                        else
                        {
                            caption.setText(getString(R.string.fragment_freeze_frame_records_frame_0_caption));
                            refreshButton.setVisibility(View.VISIBLE);
                            listView.setVisibility(View.VISIBLE);
                            for(final ParameterModel model : viewedParameters)
                            {
                                final Unit unit = model.getPID()
                                                       .getDisplayUnit();
                                host.getInterpreter()
                                    .sendRequest(
                                            new ServiceRequest(Service.FREEZE_FRAME_DATA,
                                                    model.getPID()
                                                         .unwrap(),
                                                    new Handler<Serializable>()
                                                    {

                                                        @Override
                                                        public void onResponse(Serializable value)
                                                        {
                                                            model.update(value, unit);
                                                            if(--remaining == 0)
                                                            {
                                                                listView.invalidateViews();
                                                            }
                                                        }

                                                        @Override
                                                        public void onFailure(FailureCode code)
                                                        {
                                                            if(--remaining == 0)
                                                            {
                                                                listView.invalidateViews();
                                                            }
                                                        }
                                                    },
                                                    unit
                                            )
                                    );
                            }
                        }
                        if(view != null)
                        {
                            view.invalidate();
                        }
                    }

                    @Override
                    public void onFailure(FailureCode code)
                    {
                        caption.setText(getString(R.string.fragment_freeze_frame_records_error));
                        refreshButton.setVisibility(View.INVISIBLE);
                        listView.setVisibility(View.INVISIBLE);
                        if(view != null)
                        {
                            view.invalidate();
                        }
                    }
                }));
            }
        }.run();
    }
}
