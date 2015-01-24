// This file is protected under the KILLGPL.
// For more information, visit http://www.lukeleber.github.io/KILLGPL.html
//
// Copyright (c) Luke Leber <LukeLeber@gmail.com>

package com.lukeleber.scandroid.sae.j2012;

public class InvalidDTCException extends RuntimeException
{
    public InvalidDTCException(String badEncoding, String reason)
    {
        super("Invalid DTC: " + badEncoding + "(" + reason + ")");
    }
}
