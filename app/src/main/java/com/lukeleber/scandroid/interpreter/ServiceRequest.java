/*
 * This file is protected under the KILLGPL.
 * For more information visit <insert_valid_link_to_killgpl_here>
 * <p/>
 * Copyright (c) Luke A. Leber <LukeLeber@gmail.com> 2014
 */

package com.lukeleber.scandroid.interpreter;

import com.lukeleber.scandroid.sae.PID;
import com.lukeleber.scandroid.sae.Service;
import com.lukeleber.scandroid.util.Unit;

/**
 * <p>A type of {@link killgpl.scandroid.interpreter.Request} that is used to request a piece of
 * information from a remote system.  <code>ServiceRequests</code> consist of a {@link
 * killgpl.scandroid.sae.PID} and a {@link killgpl.scandroid.sae.Service} on which the PID
 * should be requested under.  This is typically represented textually as "$XX$YY" where "XX" is the
 * service (from 00 to FF) and "YY" is the PID (from 00 to FF).  For example, $01$04 translates to
 * "Get me the calculated engine load from the live datastream". </p> <p/> <p>Not all vehicles
 * support all services and not all services support all PIDs.  However this problem can be lessened
 * through the use of a {@link killgpl.scandroid.sae.Profile} that automatically detects the
 * constructs that a vehicle supports.</p>
 *
 * @param <T>
 *         the type of data that represents the configuration option
 * @param <U>
 *         the type of data that is received in response to this request
 */
public class ServiceRequest<T, U>
        extends Request<T, U>
{

    /// The {@link killgpl.scandroid.j1979.Service} that this request is to be sent for
    private final Service service;

    /// The {@link killgpl.scandroid.j1979.PID} that is being requested
    private final PID<U> pid;

    /// The {@link killgpl.scandroid.util.Unit} whose unmarshaller should be called
    private final Unit preferredUnit;

    /**
     * Constructs a ServiceRequest with the provided {@link killgpl.scandroid.sae.Service}, {@link
     * killgpl.scandroid.sae.PID}, {@link killgpl.scandroid.interpreter.Handler}, and {@link
     * killgpl.scandroid.util.Unit}.  This constructor is equivalent to
     * <code>ServiceRequest(service, pid, handler, pid.getDefaultUnit());</code>
     *
     * @param service
     *         the {@link killgpl.scandroid.sae.Service} that this request is to be sent for
     * @param pid
     *         the {@link killgpl.scandroid.sae.PID} that is being requested
     * @param handler
     *         the {@link killgpl.scandroid.interpreter.Handler} that is invoked when a response to
     *         this request is received from the remote hardware
     */
    public ServiceRequest(Service service, PID<U> pid, Handler<U> handler)
    {
        this(service, pid, handler, pid.getDefaultUnit());
    }

    /**
     * Constructs a ServiceRequest with the provided {@link killgpl.scandroid.sae.Service}, {@link
     * killgpl.scandroid.sae.PID}, {@link killgpl.scandroid.interpreter.Handler}, and {@link
     * killgpl.scandroid.util.Unit}.
     *
     * @param service
     *         the {@link killgpl.scandroid.sae.Service} that this request is to be sent for
     * @param pid
     *         the {@link killgpl.scandroid.sae.PID} that is being requested
     * @param handler
     *         the {@link killgpl.scandroid.interpreter.Handler} that is invoked when a response to
     *         this request is received from the remote hardware
     * @param preferredUnit
     *         the {@link killgpl.scandroid.util.Unit} whose unmarshaller should be called
     */
    public ServiceRequest(Service service, PID<U> pid, Handler<U> handler, Unit preferredUnit)
    {
        super(handler);
        this.service = service;
        this.pid = pid;
        this.preferredUnit = preferredUnit;
    }

    /**
     * Retrieves the {@link killgpl.scandroid.sae.Service} that this request is to be sent for
     *
     * @return the {@link killgpl.scandroid.sae.Service} that this request is to be sent for
     */
    public final Service getService()
    {
        return service;
    }

    /**
     * Retrieves the {@link killgpl.scandroid.sae.PID} that is being requested
     *
     * @return the {@link killgpl.scandroid.sae.PID} that is being requested
     */
    public final PID<U> getPID()
    {
        return pid;
    }

    /**
     * Retrieves the {@link killgpl.scandroid.sae.PID.Unmarshaller} that should be used
     *
     * @return the {@link killgpl.scandroid.sae.PID.Unmarshaller} that should be used
     */
    public final PID.Unmarshaller<U> getUnmarshaller()
    {
        return pid.getUnmarshallerForUnit(preferredUnit);
    }
}
