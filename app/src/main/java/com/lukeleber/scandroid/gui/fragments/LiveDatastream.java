/*
 * This file is protected under the KILLGPL.
 * For more information visit <insert_valid_link_to_killgpl_here>
 * <p/>
 * Copyright (c) Luke A. Leber <LukeLeber@gmail.com> 2014
 */

package com.lukeleber.scandroid.gui.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import com.lukeleber.scandroid.R;
import com.lukeleber.scandroid.gui.InterpreterHost;
import com.lukeleber.scandroid.gui.fragments.util.AbstractParameterAdapter;
import com.lukeleber.scandroid.gui.fragments.util.ParameterModel;
import com.lukeleber.scandroid.sae.PID;
import com.lukeleber.scandroid.sae.Profile;
import com.lukeleber.scandroid.sae.Service;

public class LiveDatastream
        extends
        ServiceFragment
{
    private final static String SUPPORTED_PIDS_KEY = "supported_pids";
    private final static String VIEWED_PIDS_KEY = "viewed_pids";

    private List<PID<?>> supportedPIDs;

    private List<ParameterModel> viewedParameters;

    @InjectView(R.id.fragment_live_datastream_listview)
    ListView datastreamView;

    private InterpreterHost<?, ?> host;

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        if (!InterpreterHost.class.isAssignableFrom(activity.getClass()))
        {
            throw new ClassCastException(activity.toString()
                    + " must implement " + InterpreterHost.class.getName());
        }
        this.host = (InterpreterHost) activity;
    }

    @Override
    public void onCreate(Bundle sis)
    {
        super.onCreate(sis);
        if (sis == null)
        {
            supportedPIDs = new ArrayList<>();
            viewedParameters = new ArrayList<>();
            Profile profile = host.getProfile();
            for (int i = 0; i < 0xFF; ++i)
            {
                if (profile.isSupported(Service.LIVE_DATASTREAM, i))
                {
                    PID<?> pid = profile.getID(Service.LIVE_DATASTREAM, i);
                    /// TODO: remove conditional when all $01 PIDs are implemented!
                    if (pid != null)
                    {
                        supportedPIDs.add(pid);
                        viewedParameters.add(new ParameterModel(pid));
                    }
                }
            }
        }
        else
        {
            supportedPIDs = sis.getParcelableArrayList(SUPPORTED_PIDS_KEY);
            viewedParameters = sis.getParcelableArrayList(VIEWED_PIDS_KEY);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        outState.putParcelableArrayList(SUPPORTED_PIDS_KEY, (ArrayList<PID<?>>) supportedPIDs);
        outState.putParcelableArrayList(VIEWED_PIDS_KEY, (ArrayList<ParameterModel>) viewedParameters);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View rv = inflater.inflate(R.layout.fragment_live_datastream, container, false);
        ButterKnife.inject(this, rv);
        datastreamView.setAdapter(
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
                        if (position < 0)
                        {
                            throw new IllegalArgumentException("position < 0");
                        }
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
        this.supportedPIDs = null;
        this.viewedParameters = null;
        this.viewedParameters = null;
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        this.host = null;
    }

    @Override
    public Service getService()
    {
        return Service.LIVE_DATASTREAM;
    }
}
