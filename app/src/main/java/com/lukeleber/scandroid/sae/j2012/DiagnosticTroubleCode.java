/*
 * This file is protected under the KILLGPL.
 * For more information visit <insert_valid_link_to_killgpl_here>
 * <p/>
 * Copyright (c) Luke A. Leber <LukeLeber@gmail.com> 2014
 */

package com.lukeleber.scandroid.sae.j2012;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Defined by SAE J2012: Diagnostic Trouble Code Definitions
 * <p/>
 * <p>The DiagnosticTroubleCode class offers a standardized representation of fault codes that exist
 * within OBD systems.  Diagnostic Trouble Codes (DTCs) are generally represented as five printable
 * characters.  The first character represents the system category that the fault lies under.  This
 * character can be one of 'P', 'C', 'B', or 'U' for "Powertrain", "Chassis", "Body", or "Network",
 * respectively.  The second character represents the code jurisdiction.  DTCs can be either
 * controlled by the ISO/SAE or by the manufacturer.  The second character may be one of '0', '1',
 * '2', or '3'.  The jurisdiction scheme varies between system categories.  The next three
 * characters make up the code number.  The code number is further broken down into two fields.  The
 * first character represents the subsystem category. The remaining two characters allow for 256
 * combinations, thus allowing 256 codes per subsystem. </p>
 * <p/>
 * <p>For example, the code "P01234": P - Powertrain 0 - ISO/SAE Controlled 0 - Fuel and Air
 * Metering and Auxilliary Emission Controls 1 2 - "A" Camshaft Position - Timing Over-Retarded
 * </p>
 */
public final class DiagnosticTroubleCode
        implements
        Comparable<DiagnosticTroubleCode>,
        Parcelable,
        Serializable
{

    /// Workaround class for working with final fields
    private final static class SerializationProxy
    {
        private final int code;
        private final String description;

        SerializationProxy(DiagnosticTroubleCode dtc)
        {
            this.code = dtc.code; this.description = dtc.description;
        }

        @SuppressWarnings("unused")
        private Object readResolve()
        {
            return new DiagnosticTroubleCode(code, description);
        }
    }

    /// Lookup table for the most significant nibble of a DTC
    private final static String[] MOST_SIG_NIBBLE_LOOKUP_TABLE =
            new String[]
                    {
                            "P0",
                            "P1",
                            "P2",
                            "P3",
                            "C0",
                            "C1",
                            "C2",
                            "C3",
                            "B0",
                            "B1",
                            "B2",
                            "B3",
                            "U0",
                            "U1",
                            "U2",
                            "U3"
                    };

    /// The mask for accessing the system information
    public final static int SYSTEM_MASK = 0xC0000000;

    /// The mask for accessing the jurisdiction information
    public final static int JURISDICTION_MASK = 0x30000000;

    /// The value of a powertrain DTC
    public final static int POWERTRAIN_DTC = 0x00000000;

    /// The value of a chassis DTC
    public final static int CHASSIS_DTC = 0x40000000;

    /// The value of a body DTC
    public final static int BODY_DTC = 0x80000000;

    /// The value of a network DTC
    public final static int NETWORK_DTC = 0xC0000000;

    /// The value of a reserved DTC
    public final static int RESERVED = 0x3;

    /// Required by the parcelable interface
    @SuppressWarnings("unused")
    private final static Creator<DiagnosticTroubleCode> CREATOR =
            new Creator<DiagnosticTroubleCode>()
            {
                @Override
                public final DiagnosticTroubleCode createFromParcel(Parcel source)
                {
                    return new DiagnosticTroubleCode(source.readString(), source.readString());
                }

                @Override
                public final DiagnosticTroubleCode[] newArray(int size)
                {
                    return new DiagnosticTroubleCode[size];
                }
            };

    /// The encoded 16-bits that make up this DTC
    private final int code;

    /// The string-encoded representation of this DTC (IE. "P0123")
    private final transient String encoding;

    /// A user-friendly description of this DTC (IE "Intake Runner 'A' Stuck Open");
    private final String description;

    public DiagnosticTroubleCode(int code, String description)
    {
        this.code = code;
        this.encoding = MOST_SIG_NIBBLE_LOOKUP_TABLE[(code >> 12) & 0xF] + Integer.toHexString((code >> 8) & 0xF) + Integer.toHexString((code >> 4) & 0xF) + Integer.toHexString(code & 0xF);
        this.description = description;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int compareTo(DiagnosticTroubleCode another)
    {
        return code - another.code;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int describeContents()
    {
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void writeToParcel(Parcel dest, int flags)
    {
        dest.writeInt(code); dest.writeString(description);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String toString()
    {
        return encoding + ": " + description;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean equals(Object another)
    {
        return another instanceof DiagnosticTroubleCode && code == ((DiagnosticTroubleCode) another).code;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int hashCode()
    {
        return this.code;
    }

    /// Invoked through the deserialization process by the JVM.
    @SuppressWarnings("unused")
    private Object writeReplace()
    {
        return new SerializationProxy(this);
    }

    public DiagnosticTroubleCode(String encoding, String description)
    {
        int code = 0;
        String value = encoding.substring(0, 2);
        for(int i = 0; i < MOST_SIG_NIBBLE_LOOKUP_TABLE.length; ++i)
        {
            if(MOST_SIG_NIBBLE_LOOKUP_TABLE[i] == value)
            {
                code = i;
            }
        }
        code <<= 24;
        code |= (Integer.parseInt(encoding.substring(2, 2), 16) << 8);
        code |= (Integer.parseInt(encoding.substring(3, 3), 16) << 4);
        code |= (Integer.parseInt(encoding.substring(4, 4), 16));
        this.code = code;
        this.encoding = encoding;
        this.description = description;
    }

    /**
     * Retrieves the raw integral code value of this DTC.
     *
     * @return the raw integral code value of this DTC
     */
    public final int getCode()
    {
        return code;
    }

    public final String getEncoding()
    {
        return encoding;
    }

    public final String getDescription()
    {
        return description;
    }

    /**
     * Is this DTC a powertrain DTC?
     *
     * @return true if this is a powertrain DTC, otherwise false
     */
    public final boolean isPowertrainDTC()
    {
        return (code & SYSTEM_MASK) == POWERTRAIN_DTC;
    }

    /**
     * Is this DTC a body DTC?
     *
     * @return true if this is a body DTC, otherwise false
     */
    public final boolean isBodyDTC()
    {
        return (code & SYSTEM_MASK) == BODY_DTC;
    }

    /**
     * Is this DTC a chassis DTC?
     *
     * @return true if this is a chassis DTC, otherwise false
     */
    public final boolean isChassisDTC()
    {
        return (code & SYSTEM_MASK) == CHASSIS_DTC;
    }

    /**
     * Is this DTC a network DTC?
     *
     * @return true if this is a network DTC, otherwise false
     */
    public final boolean isNetworkDTC()
    {
        return (code & SYSTEM_MASK) == NETWORK_DTC;
    }

    /**
     * Is this DTC a core DTC?
     *
     * @return true if this is a core DTC, otherwise false
     */
    public final boolean isCoreDTC()
    {
        int jurisdiction = code & JURISDICTION_MASK;
        return jurisdiction == 0 || jurisdiction == 2 && isPowertrainDTC();
    }

    /**
     * Is this DTC a non-uniform DTC?
     *
     * @return true if this is a non-uniform DTC, otherwise false
     */
    public final boolean isNonUniformDTC()
    {
        return !isCoreDTC();
    }

    /**
     * Is this DTC a reserved DTC?
     *
     * @return true if this is a reserved DTC, otherwise false
     */
    public final boolean isReservedDTC()
    {
        return (code & JURISDICTION_MASK) == RESERVED;
    }
}