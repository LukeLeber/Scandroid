/*
 * This file is protected under the KILLGPL.
 * For more information visit <insert_valid_link_to_killgpl_here>
 * <p/>
 * Copyright (c) Luke A. Leber <LukeLeber@gmail.com> 2014
 */

package com.lukeleber.scandroid.io;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * // todo: docs...left off here.  phwew.
 */
public interface CommunicationInterface
        extends Closeable
{
    OutputStream getOutputStream()
            throws
            IOException;

    InputStream getInputStream()
            throws
            IOException;

    void connect()
            throws
            IOException;
}
