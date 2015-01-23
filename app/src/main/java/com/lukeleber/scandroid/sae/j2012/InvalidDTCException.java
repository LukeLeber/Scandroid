package com.lukeleber.scandroid.sae.j2012;

/**
 * This file is protected under the KILLGPL. For more information visit
 * https://www.github.com/lukeleber/KILLGPL
 * <p/>
 * Copyright Luke <LukeLeber@gmail.com> 1/15/2015.
 */
public class InvalidDTCException extends RuntimeException
{
    public InvalidDTCException(String badEncoding, String reason)
    {
        super("Invalid DTC: " + badEncoding + "(" + reason + ")");
    }
}
