// This file is protected under the KILLGPL.
// For more information, visit http://www.lukeleber.github.io/KILLGPL.html
//
// Copyright (c) Luke Leber <LukeLeber@gmail.com>

package com.lukeleber.scandroid.sae.j1979.detail;

import android.support.annotation.NonNull;

import com.lukeleber.scandroid.sae.j1979.DefaultPID;
import com.lukeleber.scandroid.sae.j1979.PID;
import com.lukeleber.scandroid.sae.j1979.PIDSupport;
import com.lukeleber.scandroid.util.Unit;

import java.nio.ByteBuffer;
import java.util.HashMap;

/**
 * Appendix A should be a "static appendix" - meaning that the facets should not change between
 * vehicles.
 */
public class AppendixA
{

    /**
     * As defined in SAE-J1979 Appendix A:
     * <p/>
     * The following PIDs are usable in Services 1, 2, 5, 6, 7, and 9 to retrieve the collective
     * operations (PID, OBDMID, TID, INFOTYPE) that are supported by each respective Service.
     * <p/>
     * Each PID returns 4 bytes from the onboard module that are to be interpreted as a set of bits
     * that each indicate whether a service is supported or not.  For example:
     * <p/>
     * Android -> {0x1, 0x0} -> ECU ... ... ECU -> 0x123ABC0 -> Android
     * <p/>
     * In words, we request PID 0x0 of Service 0x1 and the ECU responds with 0x123ABC0
     * <p/>
     * 0x123ABC0 == 0b00001110001000111010101111000000 |||||||| Unsupported PID 0_||||||||
     * Unsupported PID 1__||||||| Unsupported PID 2___|||||| Unsupported PID 3____||||| Supported
     * PID 4_______|||| Supported PID 5________||| Supported PID 6_________|| Unsupported PID
     * 7________| ...
     */

    /// A helper <code>ValueFunction</code> that is reused for all <code>PID</code>s in Appendix A
    private final static PID.Unmarshaller<PIDSupport> APPENDIX_A_BITSET_VALUE_FUNCTION
            = new PID.Unmarshaller<PIDSupport>()
    {
        @Override
        public PIDSupport invoke(@NonNull byte... bytes)
        {
            int ctr = 0;
            while (bytes.length > 4 && bytes[ctr] == 0)
            {
                ++ctr;
            }
            return new PIDSupport(ByteBuffer.wrap(bytes, ctr, bytes.length - ctr)
                                            .getInt());
        }
    };

    /// A PID that requests support status for PIDs 0x1 to 0x20
    public final static PID<PIDSupport> J1979_CHECK_PID_SUPPORT_1_TO_20 = new DefaultPID<>(
            0x00,
            "N/A",
            "Check for supported PIDs in the range [0x1 - 0x20]",
            new HashMap<Unit, PID.Unmarshaller<PIDSupport>>()
            {
                {
                    super.put(Unit.PID_SUPPORT_STRUCT, APPENDIX_A_BITSET_VALUE_FUNCTION);
                }
            }
    );

    /// A PID that requests support status for PIDs 0x21 to 0x40
    public final static PID<PIDSupport> J1979_CHECK_PID_SUPPORT_21_TO_40 = new DefaultPID<>(
            0x20,
            "N/A",
            "Check for supported PIDs in the range [0x21 - 0x40]",
            new HashMap<Unit, PID.Unmarshaller<PIDSupport>>()
            {
                {
                    super.put(Unit.PID_SUPPORT_STRUCT, APPENDIX_A_BITSET_VALUE_FUNCTION);
                }
            }
    );

    /// A PID that requests support status for PIDs 0x41 to 0x60
    public final static PID<PIDSupport> J1979_CHECK_PID_SUPPORT_41_TO_60 = new DefaultPID<>(
            0x40,
            "N/A",
            "Check for supported PIDs in the range [0x41 - 0x60]",
            new HashMap<Unit, PID.Unmarshaller<PIDSupport>>()
            {
                {
                    super.put(Unit.PID_SUPPORT_STRUCT, APPENDIX_A_BITSET_VALUE_FUNCTION);
                }
            }
    );

    /// A PID that requests support status for PIDs 0x61 to 0x80
    public final static PID<PIDSupport> J1979_CHECK_PID_SUPPORT_61_TO_80 = new DefaultPID<>(
            0x60,
            "N/A",
            "Check for supported PIDs in the range [0x61 - 0x80]",
            new HashMap<Unit, PID.Unmarshaller<PIDSupport>>()
            {
                {
                    super.put(Unit.PID_SUPPORT_STRUCT, APPENDIX_A_BITSET_VALUE_FUNCTION);
                }
            }
    );

    /// A PID that requests support status for PIDs 0x81 to 0xA0
    public final static PID<PIDSupport> J1979_CHECK_PID_SUPPORT_81_TO_A0 = new DefaultPID<>(
            0x80,
            "N/A",
            "Check for supported PIDs in the range [0x81 - 0xA0]",
            new HashMap<Unit, PID.Unmarshaller<PIDSupport>>()
            {
                {
                    super.put(Unit.PID_SUPPORT_STRUCT, APPENDIX_A_BITSET_VALUE_FUNCTION);
                }
            }
    );

    /// A PID that requests support status for PIDs 0xA1 to 0xC0
    public final static PID<PIDSupport> J1979_CHECK_PID_SUPPORT_A1_TO_C0 = new DefaultPID<>(
            0xA0,
            "N/A",
            "Check for supported PIDs in the range [0xA1 - 0xC0]",
            new HashMap<Unit, PID.Unmarshaller<PIDSupport>>()
            {
                {
                    super.put(Unit.PID_SUPPORT_STRUCT, APPENDIX_A_BITSET_VALUE_FUNCTION);
                }
            }
    );

    /// A PID that requests support status for PIDs 0xC1 to 0xE0
    public final static PID<PIDSupport> J1979_CHECK_PID_SUPPORT_C1_TO_E0 = new DefaultPID<>(
            0xC0,
            "N/A",
            "Check for supported PIDs in the range [0xC1 - 0xE0]",
            new HashMap<Unit, PID.Unmarshaller<PIDSupport>>()
            {
                {
                    super.put(Unit.PID_SUPPORT_STRUCT, APPENDIX_A_BITSET_VALUE_FUNCTION);
                }
            }
    );

    /// A PID that requests support status for PIDs 0xE1 to 0xFF
    public final static PID<PIDSupport> J1979_CHECK_PID_SUPPORT_E1_TO_FF = new DefaultPID<>(
            0xE0,
            "N/A",
            "Check for supported PIDs in the range [0xE1 - 0xFF]",
            new HashMap<Unit, PID.Unmarshaller<PIDSupport>>()
            {
                {
                    super.put(Unit.PID_SUPPORT_STRUCT, APPENDIX_A_BITSET_VALUE_FUNCTION);
                }
            }
    );

}
