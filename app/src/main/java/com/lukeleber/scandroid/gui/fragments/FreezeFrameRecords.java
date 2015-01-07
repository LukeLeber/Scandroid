/*
 * This file is protected under the KILLGPL.
 * For more information visit <insert_valid_link_to_killgpl_here>
 * <p/>
 * Copyright (c) Luke A. Leber <LukeLeber@gmail.com> 2015
 */

package com.lukeleber.scandroid.gui.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import com.lukeleber.scandroid.R;
import com.lukeleber.scandroid.gui.InterpreterHost;
import com.lukeleber.scandroid.gui.fragments.util.AbstractParameterAdapter;
import com.lukeleber.scandroid.gui.fragments.util.ParameterModel;
import com.lukeleber.scandroid.interpreter.FailureCode;
import com.lukeleber.scandroid.interpreter.Handler;
import com.lukeleber.scandroid.interpreter.ServiceRequest;
import com.lukeleber.scandroid.sae.PID;
import com.lukeleber.scandroid.sae.Profile;
import com.lukeleber.scandroid.sae.Service;

public class FreezeFrameRecords
        extends ServiceFragment
{

    @InjectView(R.id.fragment_freeze_frame_records_listview)
    ListView listView;

    InterpreterHost<?, ?> host;

    public static FreezeFrameRecords newInstance()
    {
        return new FreezeFrameRecords();
    }

    public FreezeFrameRecords()
    {

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
        View rv = inflater.inflate(R.layout.fragment_freeze_frame_records, container, false);
        ButterKnife.inject(this, rv);
        Profile profile = host.getProfile();
        for (int i = 1; i < 0xFF; ++i)
        {
            if (profile.isSupported(Service.FREEZE_FRAME_DATASTREAM, i))
            {
                PID<?> pid = profile.getID(Service.FREEZE_FRAME_DATASTREAM, i);
                if (pid != null)
                {
                    viewedParameters.add(new ParameterModel(pid));
                }
                else
                {
                    System.out.println("PID: " + i + " not implemented yet!");
                }
            }
        }
        Toast.makeText(getActivity(), "Populating freeze-frame record...", Toast.LENGTH_SHORT).show();
        new Runnable()
        {
            final int requested = viewedParameters.size();
            int received = 0;

            @SuppressWarnings("unchecked")
            @Override
            public void run()
            {
                for (final ParameterModel model : viewedParameters)
                {
                    host.getInterpreter().sendRequest(new ServiceRequest(Service.FREEZE_FRAME_DATASTREAM, model.getPID(), new Handler<Serializable>()
                    {
                        @Override
                        public void onResponse(Serializable value)
                        {
                            model.update(value, model.getUnit());
                            ++received;
                            if (requested == received)
                            {
                                listView.invalidateViews();
                            }
                        }

                        @Override
                        public void onFailure(FailureCode code)
                        {
                            model.update("N/A", model.getUnit());
                            ++received;
                            if (requested == received)
                            {
                                received = 0;
                                listView.invalidateViews();
                            }
                        }
                    }));
                }
            }
        }.run();
        listView.setAdapter(new FreezeFrameAdapter(getActivity()));
        return rv;
    }

    @Override
    public Service getService()
    {
        return Service.FREEZE_FRAME_DATASTREAM;
    }

    private final class FreezeFrameAdapter
            extends AbstractParameterAdapter
    {

        FreezeFrameAdapter(Context context)
        {
            super(context);
        }

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

    private final List<ParameterModel> viewedParameters = new ArrayList<>();

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
    public void onDetach()
    {
        super.onDetach();
    }
}
