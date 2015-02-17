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

/// TODO: Write Unit Tests

/**
 * <p>This class models the packeted PID result data type defined by SAE J1979 Appendix B:
 * Table B28.  For each continuous and non-continuous monitor, two pieces of information are
 * provided.</p>
 * <ol>
 *     <li>Enable Status for the current driving cycle: Consisting of a single bit, this
 *     field indicates whether or not a monitor is disabled such that there is no longer any
 *     possibility for the vehicle to meet the enabling criteria for this driving cycle.
 *     Examples include:
 *     <ul>
 *         <li>Engine off soak not long enough (e.g., cold start temperature conditions not
 *         satisfied)</li>
 *         <li>Maximum time limit or number of attempts/aborts exceeded</li>
 *         <li>Ambient air temperature too low or too high</li>
 *         <li>Barometric pressure too low</li>
 *     </ul>
 *     The enable status shall not be set to false due to operator controlled conditions such as
 *     engine speed, load, throttle position, etc...
 *     </li>
 *     <li>Completion status for the current driving cycle: Consisting of a single bit, this
 *     field indicates whether or not the monitor test has been completed during the current
 *     driving cycle.  Initially, the completion status of all monitors is set to "not complete"
 *     when a new driving cycle begins.</li>
 * </ol>
 */
public class DriveCycleMonitorStatus
        implements
        Parcelable,
        Serializable,
        Internationalized
{

    /**
     * An enumeration of all currently defined continuous monitor enable status tests.
     * <p/>
     * Enable status can either be enabled or disabled.  Disabled status (reported as 'NO')
     * indicates either that the monitor is disabled for the remainder of the driving cycle or
     * that the monitor is not supported (see the packeted data type of
     * {@link com.lukeleber.scandroid.sae.j1979.MonitorStatus PID $01 - MonitorStatus}).  Enabled
     * status (reported as 'YES') indicates that the monitor is still enabled for the driving
     * cycle -- that is, the vehicle still has a chance to meet the enabling criteria to run
     * the monitor.
     *
     */
    public static enum ContinuousMonitorEnableStatus
    {
        /**
         * The enable status constant for misfire monitoring.
         *
         * @see com.lukeleber.scandroid.sae.j1979.MonitorStatus.ContinuousMonitorSupport#MIS_SUP
         */
        MIS_ENA(0x10000),

        /**
         * The enable status constant for fuel system monitoring.
         *
         * @see com.lukeleber.scandroid.sae.j1979.MonitorStatus.ContinuousMonitorSupport#FUEL_SUP
         */
        FUEL_ENA(0x20000),

        /**
         * The enable status constant for comprehensive component monitoring.
         *
         * @see com.lukeleber.scandroid.sae.j1979.MonitorStatus.ContinuousMonitorSupport#CCM_SUP
         */
        CCM_ENA(0x40000);

        /// The mask of the bit-position defined by SAE J1979
        private final int mask;

        /**
         * Constructs a {@link com.lukeleber.scandroid.sae.j1979.DriveCycleMonitorStatus.ContinuousMonitorEnableStatus}
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
         * Constructs a {@link com.lukeleber.scandroid.sae.j1979.DriveCycleMonitorStatus.ContinuousMonitorCompletionStatus}
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
         * Constructs a {@link com.lukeleber.scandroid.sae.j1979.DriveCycleMonitorStatus.NonContinuousMonitorEnableStatus}
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
         * Constructs a {@link com.lukeleber.scandroid.sae.j1979.DriveCycleMonitorStatus.NonContinuousMonitorCompletionStatus}
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

    /// Required by the {@link android.os.Parcelable} interface
    public final static Parcelable.Creator<DriveCycleMonitorStatus> CREATOR
            = new Parcelable.Creator<DriveCycleMonitorStatus>()
    {

        /**
         * {@inheritDoc}
         *
         */
        @Override
        public DriveCycleMonitorStatus createFromParcel(Parcel in)
        {
            return new DriveCycleMonitorStatus(in.readInt());
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
    public void writeToParcel(Parcel out, int flags)
    {
        out.writeInt(bits);
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

    public String getEnableCompletionString()
    {
        StringBuilder rv = new StringBuilder();
        for (int i = 0; i < ContinuousMonitorCompletionStatus.values().length; ++i)
        {
            rv.append(ContinuousMonitorEnableStatus.values()[i].name().replace("_ENA", "")).append(": ");
            if (isContinuousMonitorEnabled(ContinuousMonitorEnableStatus.values()[i]))
            {
                rv.append("[ENA").append(isContinuousMonitorComplete(ContinuousMonitorCompletionStatus.values()[i]) ? ", CMPL]\n" : ", NOT_CMPL]\n");
            }
            else
            {
                rv.append("[NOT_ENA]\n");
            }
        }
        for (int i = 0; i < NonContinuousMonitorCompletionStatus.values().length; ++i)
        {
            rv.append(NonContinuousMonitorEnableStatus.values()[i].name().replace("_ENA", "")).append(": ");
            if (isNonContinuousMonitorEnabled(NonContinuousMonitorEnableStatus.values()[i]))
            {
                rv.append("[ENA").append(isNonContinuousMonitorComplete(NonContinuousMonitorCompletionStatus.values()[i]) ? ", CMPL]\n" : ", NOT_CMPL]\n");
            }
            else
            {
                rv.append("[NOT_ENA]\n");
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
}
