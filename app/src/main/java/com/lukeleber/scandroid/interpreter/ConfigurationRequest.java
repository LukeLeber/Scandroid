/*
 * This file is protected under the KILLGPL.
 * For more information visit <insert_valid_link_to_killgpl_here>
 * <p/>
 * Copyright (c) Luke A. Leber <LukeLeber@gmail.com> 2014
 */

package com.lukeleber.scandroid.interpreter;

/**
 * A type of {@link com.lukeleber.scandroid.interpreter.Request} that is used to configure a piece
 * of remote hardware.
 *
 * @param <T>
 *         the type of data that represents the configuration option
 * @param <U>
 *         the type of data that is received in response to this request
 */
public class ConfigurationRequest<T, U>
        extends Request<T, U>
{

    /// The {@link killgpl.scandroid.interpreter.Option} being configured
    private final Option<T> option;

    private final Object[] args;

    /**
     * Constructs a <code>ConfigurationRequest</code> with the provided {@link
     * com.lukeleber.scandroid.interpreter.Option}.  This constructor is equivalent to
     * <code>ConfigurationRequest(option, null);</code>
     *
     * @param option
     *         the {@link Option} to configure
     * @param args
     */
    public ConfigurationRequest(Option<T> option, Object... args)
    {
        this(null, option, args);
    }

    /**
     * Constructs a <code>ConfigurationRequest</code> with the provided {@link
     * com.lukeleber.scandroid.interpreter.Option} and {@link com.lukeleber.scandroid.interpreter.Handler}
     *
     * @param handler
     *         the {@link Handler} that is invoked when a response to this request is received from
     *         the remote hardware
     * @param option
     *         the {@link Option} to configure
     * @param args
     */
    public ConfigurationRequest(Handler<U> handler, Option<T> option, Object... args)
    {
        super(handler);
        this.option = option;
        this.args = args;
    }

    /**
     * Retrieves the {@link com.lukeleber.scandroid.interpreter.Option} that is being configured
     *
     * @return the {@link com.lukeleber.scandroid.interpreter.Option} that is being configured
     */
    public final Option<T> getOption()
    {
        return option;
    }

    public final Object[] getArgs()
    {
        return args;
    }
}
