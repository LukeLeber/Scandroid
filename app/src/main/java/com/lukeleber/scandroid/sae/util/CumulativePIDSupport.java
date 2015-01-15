/*
 * This file is protected under the com.lukeleber.
 * For more information visit <insert_valid_link_to_killgpl_here>
 * <p/>
 * Copyright (c) Luke A. Leber <LukeLeber@gmail.com> 2014
 */

package com.lukeleber.scandroid.sae.util;

import com.lukeleber.scandroid.interpreter.FailureCode;
import com.lukeleber.scandroid.interpreter.Handler;
import com.lukeleber.scandroid.interpreter.Interpreter;
import com.lukeleber.scandroid.interpreter.ServiceRequest;
import com.lukeleber.scandroid.sae.PID;
import com.lukeleber.scandroid.sae.PIDSupport;
import com.lukeleber.scandroid.sae.j1979.Service;
import com.lukeleber.scandroid.sae.detail.AppendixA;

import java.util.ArrayList;
import java.util.List;

/**
 * A <code>CumulitivePIDSupport</code> is a form of {@link com.lukeleber.scandroid.sae.PIDSupport}
 * that covers the entire support range; that is, from ID 0 to ID 255.  This utility class is a
 * convenience function for obtaining all supported PIDs/TIDs/OBDMIDs from the vehicle in a single
 * statement.
 */
public class CumulativePIDSupport
{
    /// All of the support checking PIDs
    @SuppressWarnings("unchecked")
    private final static PID<PIDSupport>[] PID_RANGE_REQUESTS = new PID[]
            {
                    AppendixA.J1979_CHECK_PID_SUPPORT_1_TO_20,
                    AppendixA.J1979_CHECK_PID_SUPPORT_21_TO_40,
                    AppendixA.J1979_CHECK_PID_SUPPORT_41_TO_60,
                    AppendixA.J1979_CHECK_PID_SUPPORT_61_TO_80,
                    AppendixA.J1979_CHECK_PID_SUPPORT_81_TO_A0,
                    AppendixA.J1979_CHECK_PID_SUPPORT_A1_TO_C0,
                    AppendixA.J1979_CHECK_PID_SUPPORT_C1_TO_E0,
                    AppendixA.J1979_CHECK_PID_SUPPORT_E1_TO_FF
            };

    /// The service mode that is being queried
    private final transient Service service;

    /// The handler that handles the asynchronous result of running this operation
    private final transient Handler<CumulativePIDSupport> handler;

    /// A list of PIDSupports that are either supported or partially supported.
    List<PIDSupport> pids;


    private final class SupportHandler
            implements Handler<PIDSupport>
    {

        /// The current index into {@link PID_RANGE_REQUESTS}
        private transient int rangeIndex;

        private Interpreter<?> interpreter;

        /*package*/ SupportHandler(Interpreter<?> interpreter)
        {
            this.interpreter = interpreter;
        }

        @Override
        public void onResponse(PIDSupport value)
        {
            pids.add(value);
            if (value.checkSupport(31)) /// Are additional ranges supported?
            {
                ++rangeIndex;
                interpreter.sendRequest(new ServiceRequest(service, PID_RANGE_REQUESTS[rangeIndex],
                                                           this));
            }
            else
            {
                handler.onResponse(CumulativePIDSupport.this);
                this.interpreter = null;
            }
        }

        @Override
        public void onFailure(FailureCode code)
        {
            this.interpreter = null;
            handler.onFailure(code);
        }
    }

    /// The "Helper" handler that is called upon a response to each successive range query
    private final transient Handler<PIDSupport> supportHandler;

    public CumulativePIDSupport(Service service, Interpreter<?> interpreter,
                                Handler<CumulativePIDSupport> handler)
    {
        this.pids = new ArrayList<>();
        this.service = service;
        this.handler = handler;
        this.supportHandler = new SupportHandler(interpreter);
    }

    public static <T> void getSupportedPIDs(Service service, Interpreter<T> interpreter,
                                            Handler<CumulativePIDSupport> handler)
    {
        CumulativePIDSupport tmp = new CumulativePIDSupport(service, interpreter, handler);
        interpreter.sendRequest(new ServiceRequest<T, PIDSupport>(service, PID_RANGE_REQUESTS[0],
                                                                  tmp.supportHandler));
    }

    public boolean isSupported(int pid)
    {
        int index = pid / 32;
        if (index < pids.size())
        {
            return pids.get(index)
                       .checkSupport(pid);
        }
        return false;
    }
}
