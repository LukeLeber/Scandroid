// This file is protected under the KILLGPL.
// For more information, visit http://www.lukeleber.github.io/KILLGPL.html
//
// Copyright (c) Luke Leber <LukeLeber@gmail.com>

package com.lukeleber.scandroid.interpreter.elm327;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * All protocol modes supported by the ELM327
 * <p/>
 * TODO: Docs (low level, I know...but necessary to access manufacturer defined functionality)
 */
@SuppressWarnings("unused")
public enum Protocol implements Parcelable
{
    /**
     * Automatically select the correct protocol
     */
    AUTOMATIC(0, -1),

    SAE_J1850_PWM(1, 41600),
    SAE_J1850_VPW(2, 10400),
    ISO_9141_2(3, -1), //FIXME: Find Baudrate
    ISO_14230_4_KWP_5_BAUD_INIT(4, -1), // FIXME: Find Baudrate
    ISO_14230_4_KWP_FAST_INIT(5, -1), // FIXME: Find Baudrate
    ISO_15765_4_CAN_11_BIT_500_KBAUD(6, 500000),
    ISO_15765_4_CAN_29_BIT_500_KBAUD(7, 500000),
    ISO_15765_4_CAN_11_BIT_250_KBAUD(8, 250000),
    ISO_15765_4_CAN_29_BIT_250_KBAUD(9, 250000),
    SAE_J1939_CAN(10, 250000),
    USER_1_CAN_11_BIT_125_KBAUD(11, 125000),
    USER_2_CAN_11_BIT_125_KBAUD(12, 50000);

    public final static Parcelable.Creator<Protocol> CREATOR =
            new Parcelable.Creator<Protocol>()
            {

                @Override
                public Protocol createFromParcel(Parcel in)
                {
                    return Protocol.values()[in.readByte() & 0xFF];
                }

                @Override
                public Protocol[] newArray(int length)
                {
                    return new Protocol[length];
                }
            };

    private final int id;
    private final int baudrate;

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out,
                              int flags)
    {
        out.writeByte((byte)ordinal());
    }

    private Protocol(int id, int baudrate)
    {
        this.id = id;
        this.baudrate = baudrate;
    }

    public final int getID()
    {
        return id;
    }

    public final int getBaudrate()
    {
        return baudrate;
    }
}