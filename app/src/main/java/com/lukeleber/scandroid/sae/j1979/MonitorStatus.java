// This file is protected under the KILLGPL.
// For more information, visit http://www.lukeleber.github.io/KILLGPL.html
//
// Copyright (c) Luke Leber <LukeLeber@gmail.com>

package com.lukeleber.scandroid.sae.j1979;

import java.io.Serializable;

public class MonitorStatus implements Serializable
{
    private final int bits;

    public MonitorStatus(int bits)
    {
        this.bits = bits;
    }

    public boolean isMalfunctionLampOn()
    {
        return (bits & 0x80000000) != 0;
    }

    public int getDiagnosticTroubleCodeCount()
    {
        return (bits & 0x7F000000) >> 24;
    }

    public String getSupportReadinessString()
    {
        StringBuilder rv = new StringBuilder();
        for(int i = 0; i < ContinuousMonitorReadiness.values().length; ++i)
        {
            rv.append(ContinuousMonitorSupport.values()[i].name().replace("_SUP", "")).append(": ");
            if(isContinuousMonitorSupported(ContinuousMonitorSupport.values()[i]))
            {
                rv.append("[SUP").append(isContinuousMonitorReady(ContinuousMonitorReadiness.values()[i]) ? ", RDY]\n" : ", NOT_RDY]\n");
            }
            else
            {
                rv.append("[NOT_SUP]\n");
            }
        }
        for(int i = 0; i < NonContinuousMonitorReadiness.values().length; ++i)
        {
            rv.append(NonContinuousMonitorSupport.values()[i].name().replace("_SUP", "")).append(": ");
            if(isNonContinuousMonitorSupported(NonContinuousMonitorSupport.values()[i]))
            {
                rv.append("[SUP").append(isNonContinuousMonitorReady(NonContinuousMonitorReadiness.values()[i]) ? ", RDY]\n" : ", NOT_RDY]\n");
            }
            else
            {
                rv.append("[NOT_SUP]\n");
            }
        }
        return rv.substring(0, rv.length() - 1);
    }

    public boolean isContinuousMonitorSupported(ContinuousMonitorSupport monitor)
    {
        return (bits & monitor.getMask()) != 0;
    }

    public boolean isContinuousMonitorReady(ContinuousMonitorReadiness monitor)
    {
        return (bits & monitor.getMask()) != 0;
    }

    public boolean isNonContinuousMonitorSupported(NonContinuousMonitorSupport monitor)
    {
        return (bits & monitor.getMask()) != 0;
    }

    public boolean isNonContinuousMonitorReady(NonContinuousMonitorReadiness monitor)
    {
        return (bits & monitor.getMask()) != 0;
    }

    /**
     * An enumeration of all currently defined continuous monitor support tests.
     * <p/>
     * Continuous monitors may be either supported or unsupported based on the vehicle's OEM
     * equipment.  For example, diesel vehicles may not support {@link
     * MonitorStatus.ContinuousMonitorSupport#FUEL_SUP}. <br> For more
     * detailed documentation on whether or not a monitor is required to be supported, refer to each
     * individual monitor enumerated member.
     */
    public static enum ContinuousMonitorSupport
    {
        /**
         * Misfire monitoring shall be supported on both, spark ignition, and compression vehicles
         * if the vehicle utilises a misfire monitor.
         */
        MIS_SUP(0x10000),

        /**
         * Fuel system monitoring shall be supported on vehicles that utilise oxygen sensors for
         * closed loop fuel feedback control,k and utilise a fuel system monitor, typically spark
         * ignition engines.
         */
        FUEL_SUP(0x20000),

        /**
         * Comprehensive component monitoring shall be supported on spark ignition and compression
         * ignition vehicles if the vehicle utilises comprehensive component monitoring.
         */
        CCM_SUP(0x40000);

        /// The mask of the bit-position defined by SAE J1979
        private final int mask;

        /**
         * Constructs a {@link MonitorStatus.ContinuousMonitorSupport} with
         * the provided mask
         *
         * @param mask
         *         The mask of the bit-position defined by SAE J1979
         */
        private ContinuousMonitorSupport(int mask)
        {
            this.mask = mask;
        }

        /**
         * Retrieves the mask of the bit-position defined by SAE J1979
         *
         * @return the mask of the bit-position defined by SAE J1979
         */
        public int getMask()
        {
            return mask;
        }
    }

    /**
     * An enumeration of all currently defined continuous monitor readiness tests.  SAE J1979
     * dictates that all continuous monitors shall report "complete" at all times if the monitor is
     * so supported by the vehicle.
     */
    public static enum ContinuousMonitorReadiness
    {
        /**
         * Misfire monitor readiness test
         */
        MIS_RDY(0x100000),

        /**
         * Fuel system monitor readiness test
         */
        FUEL_RDY(0x200000),

        /**
         * Comprehensive component monitor readiness test
         */
        CCM_RDY(0x400000);

        /// The mask of the bit-position defined by SAE J1979
        private final int mask;

        /**
         * Constructs a {@link MonitorStatus.ContinuousMonitorReadiness} with
         * the provided mask
         *
         * @param mask
         *         The mask of the bit-position defined by SAE J1979
         */
        private ContinuousMonitorReadiness(int mask)
        {
            this.mask = mask;
        }

        /**
         * Retrieves the mask of the bit-position defined by SAE J1979
         *
         * @return the mask of the bit-position defined by SAE J1979
         */
        public int getMask()
        {
            return mask;
        }
    }

    /**
     * An enumeration of all currently defined non-continuous monitor support tests.
     * <p/>
     * Noncontinuous monitors may be either supported or unsupported based on the vehicle's OEM
     * equipment.  For example, many vehicles may not be equipped with secondary air injection, thus
     * the monitor support for {@link MonitorStatus.NonContinuousMonitorSupport#AIR_SUP}
     * will show as unsupported.
     */
    public static enum NonContinuousMonitorSupport
    {
        /**
         * Catalyst monitoring support
         */
        CAT_SUP(0x100),

        /**
         * Heated catalyst monitoring support
         */
        HCAT_SUP(0x200),

        /**
         * Evaporative system monitoring support
         */
        EVAP_SUP(0x400),

        /**
         * Secondary air system monitoring support
         */
        AIR_SUP(0x800),

        /**
         * AC system refrigerant monitoring support
         */
        ACRF_SUP(0x1000),

        /**
         * Oxygen sensor monitoring support
         */
        O2S_SUP(0x2000),

        /**
         * Oxygen sensor heater monitoring support
         */
        HTR_SUP(0x4000),

        /**
         * EGR system monitoring support
         */
        EGR_SUP(0x8000);

        /// The SAE J1979 defined bit mask
        private final int mask;

        /**
         * Constructs a {@link MonitorStatus.NonContinuousMonitorSupport}
         *
         * @param mask
         *         the SAE J1979 defined bit mask
         */
        private NonContinuousMonitorSupport(int mask)
        {
            this.mask = mask;
        }

        /**
         * Retrieves the SAE J1979 defined bit mask
         *
         * @return the SAE J1979 defined bit mask
         */
        public int getMask()
        {
            return mask;
        }
    }

    public static enum NonContinuousMonitorReadiness
    {
        /**
         * Catalyst monitoring support
         */
        CAT_RDY(0x1),

        /**
         * Heated catalyst monitoring support
         */
        HCAT_RDY(0x2),

        /**
         * Evaporative system monitoring support
         */
        EVAP_RDY(0x4),

        /**
         * Secondary air system monitoring support
         */
        AIR_RDY(0x8),

        /**
         * AC system refrigerant monitoring support
         */
        ACRF_RDY(0x10),

        /**
         * Oxygen sensor monitoring support
         */
        O2S_RDY(0x20),

        /**
         * Oxygen sensor heater monitoring support
         */
        HTR_RDY(0x40),

        /**
         * EGR system monitoring support
         */
        EGR_RDY(0x80);

        /// The SAE J1979 defined bit mask
        private final int mask;

        /**
         * Constructs a {@link MonitorStatus.NonContinuousMonitorReadiness}
         *
         * @param mask
         *         the SAE J1979 defined bit mask
         */
        private NonContinuousMonitorReadiness(int mask)
        {
            this.mask = mask;
        }

        /**
         * Retrieves the SAE J1979 defined bit mask
         *
         * @return the SAE J1979 defined bit mask
         */
        public int getMask()
        {
            return mask;
        }
    }
}
