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
import android.widget.ListView;

import com.lukeleber.scandroid.R;
import com.lukeleber.scandroid.gui.fragments.util.AbstractParameterAdapter;
import com.lukeleber.scandroid.gui.fragments.util.ParameterModel;
import com.lukeleber.scandroid.interpreter.FailureCode;
import com.lukeleber.scandroid.interpreter.Handler;
import com.lukeleber.scandroid.interpreter.ServiceRequest;
import com.lukeleber.scandroid.sae.PID;
import com.lukeleber.scandroid.sae.Profile;
import com.lukeleber.scandroid.sae.j1979.Service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * <p>A generic implementation of SAE-J1979 Diagnostic Service Mode $01.  This service mode
 * queries the live datastream of the vehicle on a user-defined basis.  Note that early
 * implementations of SAE-J1979 were subsets of UDS systems that did not support handling
 * requests as rapidly as modern implementations, thus in such cases the user-defined refresh
 * rate may be ignored.  Of course, all refresh rates can only occur as fast as the data bus
 * will allow anyway.  Just be aware that the refresh rate is provided on a best-effort basis
 * and should not be relied on to work in a real-time context.</p>
 *
 * <p>Conceptually, this fragment operates like so:
 * <pre>
 * while(fragment is visible)
 * {
 *  wait until next refresh interval (or proceed immediately if behind schedule)
 *  for(each viewed PID)
 *  {
 *      ask vehicle for an update
 *  }
 * }</pre></p>
 */
public class LiveDatastream
        extends
        ServiceFragment
{
    /// Key for the "supportedPIDs" bundle value
    private final static String SUPPORTED_PIDS_KEY = "supported_pids";

    /// Key for the "viewedParameters" bundle value
    private final static String VIEWED_PARAMETERS_KEY = "viewed_parameters";

    /// Key for the "refreshRate" bundle value
    private final static String REFRESH_RATE_KEY = "refresh_rate";

    /**
     * A helper class to refresh the live datastream at a user-defined interval.
     *
     * Note - the refresh rate is not guaranteed and is provided on a best-attempt basis.
     *
     */
    private final class Refresher implements Runnable
    {
        /// The default refresh rate
        public final static long DEFAULT_REFRESH_RATE = 250;

        /// The executor service to schedule updates on
        private ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

        /// The target refresh rate
        private volatile long refreshRate;

        /// The unix timestamp of the last successful refresh
        private long lastRefresh;

        /// The number of requests that have yet to be responded to
        private int remaining;

        /**
         * Constructs a Refresher with the provided target refresh rate
         *
         * @param refreshRate the target rate at which datastream updates are to occur
         *
         */
        public Refresher(long refreshRate)
        {
            this.refreshRate = refreshRate;
        }

        /**
         * Constructs a Refresher with the
         * {@link com.lukeleber.scandroid.gui.fragments.LiveDatastream.Refresher#DEFAULT_REFRESH_RATE}.
         *
         */
        public Refresher()
        {
            this(DEFAULT_REFRESH_RATE);
        }

        /**
         * Retrieves the target refresh rate
         *
         * @return the target refresh rate
         *
         */
        long getRefreshRate()
        {
            return refreshRate;
        }

        /**
         * Sets the target refresh rate
         *
         * @param refreshRate the target refresh rate
         *
         */
        void setRefreshRate(long refreshRate)
        {
            this.refreshRate = refreshRate;
        }

        /**
         * Invoked on the GUI thread when all expected responses have been received, this method
         * calculates the time required to meet the next scheduled update and re-submits this
         * object to its executor service.
         *
         */
        void scheduleRefresh()
        {
            if(datastreamView != null)
            {
                datastreamView.invalidateViews();
                remaining = viewedParameters.size();
                System.out.println("Remaining: " + remaining);
                long nextRefresh = refreshRate - (System.currentTimeMillis() - lastRefresh);
                if(nextRefresh < 0)
                {
                    nextRefresh = 0;
                }
                executor.schedule(this, nextRefresh, TimeUnit.MILLISECONDS);
                lastRefresh = System.currentTimeMillis();
            }
        }

        /**
         * Starts this Refresher
         *
         */
        public void start()
        {
            remaining = viewedParameters.size();
            executor.submit(this);
        }

        /**
         * Stops this Refresher
         *
         */
        public void stop()
        {
            executor.shutdownNow();
        }

        /**
         * {@inheritDoc}
         *
         */
        @SuppressWarnings("unchecked")
        @Override
        public void run()
        {
            for(final ParameterModel model : viewedParameters)
            {
                host.getInterpreter().sendRequest(
                    new ServiceRequest(Service.LIVE_DATASTREAM, model.getPID(),
                        new Handler<Serializable>()
                        {
                            @Override
                            public void onResponse(Serializable value)
                            {
                                model.update(value, model.getUnit());
                                if(--remaining == 0)
                                {
                                    scheduleRefresh();
                                }
                            }

                            @Override
                            public void onFailure(FailureCode code)
                            {
                                if(--remaining == 0)
                                {
                                    scheduleRefresh();
                                }
                            }
                        }
                    )
                );
            }
        }
    }

    /// The refresher to use for this LiveDatastream
    private Refresher refresher;

    /// The list of PIDs supported by this vehicle
    private List<PID<?>> supportedPIDs;

    /// The list of currently viewed PIDs
    private List<ParameterModel> viewedParameters;

    /// The Listview that displays the live data received from the vehicle
    @InjectView(R.id.fragment_live_datastream_listview)
    ListView datastreamView;

    /**
     * {@inheritDoc}
     *
     */
    @Override
    public void onCreate(Bundle sis)
    {
        super.onCreate(sis);
        if (sis == null)
        {
            supportedPIDs = new ArrayList<>();
            viewedParameters = new ArrayList<>();
            Profile profile = host.getProfile();
            for (int i = 0;
                 i < 0xFF;
                 ++i)
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
            this.refresher = new Refresher();
        }
        else
        {
            supportedPIDs = sis.getParcelableArrayList(SUPPORTED_PIDS_KEY);
            viewedParameters = sis.getParcelableArrayList(VIEWED_PARAMETERS_KEY);
            refresher = new Refresher(sis.getLong(REFRESH_RATE_KEY));
        }
    }

    /**
     * {@inheritDoc}
     *
     */
    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        outState.putParcelableArrayList(SUPPORTED_PIDS_KEY, (ArrayList<PID<?>>) supportedPIDs);
        outState.putParcelableArrayList(VIEWED_PARAMETERS_KEY,
                                        (ArrayList<ParameterModel>) viewedParameters);
        outState.putLong(REFRESH_RATE_KEY, refresher.getRefreshRate());
    }

    /**
     * {@inheritDoc}
     *
     */
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
                    return viewedParameters.get(position);
                }
            }
        );
        refresher.start();
        return rv;
    }

    /**
     * {@inheritDoc}
     *
     */
    @Override
    public void onDestroy()
    {
        super.onDestroy();
        this.refresher.stop();
        this.refresher = null;
        this.supportedPIDs = null;
        this.viewedParameters = null;
        this.datastreamView = null;
    }

    /**
     * Sets the target refresh rate
     *
     * @param refreshRate the target refresh rate
     *
     */
    public void setRefreshRate(long refreshRate)
    {
        if(refresher != null)
        {
            refresher.setRefreshRate(refreshRate);
        }
    }
}
