// This file is protected under the KILLGPL.
// For more information, visit http://www.lukeleber.github.io/KILLGPL.html
//
// Copyright (c) Luke Leber <LukeLeber@gmail.com>

package com.lukeleber.scandroid.sae.j1979;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.lukeleber.scandroid.util.Internationalized;

import java.io.Serializable;

/// TODO: re-read SAE 1979 regarding the differences between this and MonitorStatus
/// TODO: Write Unit Tests
public class DriveCycleMonitorStatus
        implements
        Parcelable,
        Serializable,
        Internationalized
{
    /// Required by the {@link android.os.Parcelable} interface
    public final static Parcelable.Creator<DriveCycleMonitorStatus> CREATOR
            = new Parcelable.Creator<DriveCycleMonitorStatus>()
    {

        /**
         * {@inheritDoc}
         *
         */
        @Override
        public DriveCycleMonitorStatus createFromParcel(Parcel source)
        {
            return new DriveCycleMonitorStatus(source.readInt());
        }

        /**
         * {@inheritDoc}
         *
         */
        @Override
        public DriveCycleMonitorStatus[] newArray(int size)
        {
            return new DriveCycleMonitorStatus[size];
        }
    };


    private final int bits;


    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeInt(bits);
    }

    @Override
    public String toI18NString(Context context)
    {
        return "Drive cycle monitor status";
    }

    public DriveCycleMonitorStatus(int bits)
    {
        this.bits = bits;
    }

    public String getSupportReadinessString()
    {
        StringBuilder rv = new StringBuilder();
        for (int i = 0; i < ContinuousMonitorCompletionStatus.values().length; ++i)
        {
            rv.append(ContinuousMonitorEnableStatus.values()[i].name().replace("_SUP", "")).append(": ");
            if (isContinuousMonitorEnabled(ContinuousMonitorEnableStatus.values()[i]))
            {
                rv.append("[SUP").append(isContinuousMonitorComplete(ContinuousMonitorCompletionStatus.values()[i]) ? ", RDY]\n" : ", NOT_RDY]\n");
            }
            else
            {
                rv.append("[NOT_SUP]\n");
            }
        }
        for (int i = 0; i < NonContinuousMonitorCompletionStatus.values().length; ++i)
        {
            rv.append(NonContinuousMonitorEnableStatus.values()[i].name().replace("_SUP", "")).append(": ");
            if (isNonContinuousMonitorEnabled(NonContinuousMonitorEnableStatus.values()[i]))
            {
                rv.append("[SUP").append(isNonContinuousMonitorComplete(NonContinuousMonitorCompletionStatus.values()[i]) ? ", RDY]\n" : ", NOT_RDY]\n");
            }
            else
            {
                rv.append("[NOT_SUP]\n");
            }
        }
        return rv.substring(0, rv.length() - 1);
    }

    public boolean isContinuousMonitorEnabled(ContinuousMonitorEnableStatus monitor)
    {
        return (bits & monitor.getMask()) != 0;
    }

    public boolean isContinuousMonitorComplete(ContinuousMonitorCompletionStatus monitor)
    {
        return (bits & monitor.getMask()) != 0;
    }

    public boolean isNonContinuousMonitorEnabled(NonContinuousMonitorEnableStatus monitor)
    {
        return (bits & monitor.getMask()) != 0;
    }

    public boolean isNonContinuousMonitorComplete(NonContinuousMonitorCompletionStatus monitor)
    {
        return (bits & monitor.getMask()) != 0;
    }


    /**
     * An enumeration of all currently defined continuous monitor support tests.
     * <p/>
     * Continuous monitors may be either supported or unsupported based on the vehicle's OEM
     * equipment.  For example, diesel vehicles may not support {@link
     * com.lukeleber.scandroid.sae.j1979.MonitorStatus.ContinuousMonitorSupport#FUEL_SUP}. <br> For
     * more detailed documentation on whether or not a monitor is required to be supported, refer to
     * each individual monitor enumerated member.
     */
    public static enum ContinuousMonitorEnableStatus
    {
        /**
         * Misfire monitoring shall be supported on both, spark ignition, and compression vehicles
         * if the vehicle utilises a misfire monitor.
         */
        MIS_ENA(0x10000),

        /**
         * Fuel system monitoring shall be supported on vehicles that utilise oxygen sensors for
         * closed loop fuel feedback control,k and utilise a fuel system monitor, typically spark
         * ignition engines.
         */
        FUEL_ENA(0x20000),

        /**
         * Comprehensive component monitoring shall be supported on spark ignition and compression
         * ignition vehicles if the vehicle utilises comprehensive component monitoring.
         */
        CCM_ENA(0x40000);

        /// The mask of the bit-position defined by SAE J1979
        private final int mask;

        /**
         * Constructs a {@link com.lukeleber.scandroid.sae.j1979.MonitorStatus.ContinuousMonitorSupport}
         * with the provided mask
         *
         * @param mask
         *         The mask of the bit-position defined by SAE J1979
         */
        private ContinuousMonitorEnableStatus(int mask)
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
    public static enum ContinuousMonitorCompletionStatus
    {
        /**
         * Misfire monitor readiness test
         */
        MIS_CMPL(0x100000),

        /**
         * Fuel system monitor readiness test
         */
        FUEL_CMPL(0x200000),

        /**
         * Comprehensive component monitor readiness test
         */
        CCM_CMPL(0x400000);

        /// The mask of the bit-position defined by SAE J1979
        private final int mask;

        /**
         * Constructs a {@link com.lukeleber.scandroid.sae.j1979.MonitorStatus.ContinuousMonitorReadiness}
         * with the provided mask
         *
         * @param mask
         *         The mask of the bit-position defined by SAE J1979
         */
        private ContinuousMonitorCompletionStatus(int mask)
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
     * the monitor support for {@link com.lukeleber.scandroid.sae.j1979.MonitorStatus.NonContinuousMonitorSupport#AIR_SUP}
     * will show as unsupported.
     */
    public static enum NonContinuousMonitorEnableStatus
    {
        /**
         * Catalyst monitoring support
         */
        CAT_ENA(0x100),

        /**
         * Heated catalyst monitoring support
         */
        HCAT_ENA(0x200),

        /**
         * Evaporative system monitoring support
         */
        EVAP_ENA(0x400),

        /**
         * Secondary air system monitoring support
         */
        AIR_ENA(0x800),

        /**
         * AC system refrigerant monitoring support
         */
        ACRF_ENA(0x1000),

        /**
         * Oxygen sensor monitoring support
         */
        O2S_ENA(0x2000),

        /**
         * Oxygen sensor heater monitoring support
         */
        HTR_ENA(0x4000),

        /**
         * EGR system monitoring support
         */
        EGR_ENA(0x8000);

        /// The SAE J1979 defined bit mask
        private final int mask;

        /**
         * Constructs a {@link com.lukeleber.scandroid.sae.j1979.MonitorStatus.NonContinuousMonitorSupport}
         *
         * @param mask
         *         the SAE J1979 defined bit mask
         */
        private NonContinuousMonitorEnableStatus(int mask)
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

    public static enum NonContinuousMonitorCompletionStatus
    {
        /**
         * Catalyst monitoring support
         */
        CAT_CMPL(0x1),

        /**
         * Heated catalyst monitoring support
         */
        HCAT_CMPL(0x2),

        /**
         * Evaporative system monitoring support
         */
        EVAP_CMPL(0x4),

        /**
         * Secondary air system monitoring support
         */
        AIR_CMPL(0x8),

        /**
         * AC system refrigerant monitoring support
         */
        ACRF_CMPL(0x10),

        /**
         * Oxygen sensor monitoring support
         */
        O2S_CMPL(0x20),

        /**
         * Oxygen sensor heater monitoring support
         */
        HTR_CMPL(0x40),

        /**
         * EGR system monitoring support
         */
        EGR_CMPL(0x80);

        /// The SAE J1979 defined bit mask
        private final int mask;

        /**
         * Constructs a {@link com.lukeleber.scandroid.sae.j1979.MonitorStatus.NonContinuousMonitorReadiness}
         *
         * @param mask
         *         the SAE J1979 defined bit mask
         */
        private NonContinuousMonitorCompletionStatus(int mask)
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
