// This file is protected under the KILLGPL.
// For more information, visit http://www.lukeleber.github.io/KILLGPL.html
//
// Copyright (c) Luke Leber <LukeLeber@gmail.com>

package com.lukeleber.scandroid.sae.j1979;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
/// TODO: Write Unit Tests
public enum OxygenSensor implements Serializable
{
    /// Bank 1 Sensor 1
    O2S11,

    /// Bank 1 Sensor 2
    O2S12,

    /// Bank 1 Sensor 3 (only available on dual-bank systems)
    O2S13,

    /// Bank 1 Sensor 4 (only available on dual-bank systems)
    O2S14,

    /// Bank 2 Sensor 1
    O2S21,

    /// Bank 2 Sensor 2
    O2S22,

    /// Bank 2 Sensor 3 (only available on dual-bank systems)
    O2S23,

    /// Bank 2 Sensor 4 (only available on dual-bank systems)
    O2S24,

    /// Bank 3 Sensor 1 (only available on quad-bank systems)
    O2S31,

    /// Bank 3 Sensor 2 (only available on quad-bank systems)
    O2S32,

    /// Bank 4 Sensor 1 (only available on quad-bank systems)
    O2S41,

    /// Bank 4 Sensor 2 (only available on quad-bank systems)
    O2S42;

    public enum DualBank
    {
        O2S11(0x1),
        O2S12(0x2),
        O2S13(0x4),
        O2S14(0x8),
        O2S21(0x10),
        O2S22(0x20),
        O2S23(0x40),
        O2S24(0x80);

        private final int mask;

        public boolean exists(int val)
        {
            return (val & mask) != 0;
        }

        public static OxygenSensor[] forByte(int val)
        {
            List<OxygenSensor> sensors = new ArrayList<>();
            for (DualBank sensor : DualBank.values())
            {
                if (sensor.exists(val))
                {
                    sensors.add(OxygenSensor.valueOf(sensor.name()));
                }
            }
            OxygenSensor[] rv = new OxygenSensor[sensors.size()];
            return sensors.toArray(rv);
        }

        DualBank(int mask)
        {
            this.mask = mask;
        }
    }

    public enum QuadBank
    {
        O2S11(0x1),
        O2S12(0x2),
        O2S21(0x4),
        O2S22(0x8),
        O2S31(0x10),
        O2S32(0x20),
        O2S41(0x40),
        O2S42(0x80);

        private final int mask;

        public boolean exists(int val)
        {
            return (val & mask) != 0;
        }

        public static OxygenSensor[] forByte(int val)
        {
            List<OxygenSensor> sensors = new ArrayList<>();
            for (QuadBank sensor : QuadBank.values())
            {
                if (sensor.exists(val))
                {
                    sensors.add(OxygenSensor.valueOf(sensor.name()));
                }
            }
            OxygenSensor[] rv = new OxygenSensor[sensors.size()];
            return sensors.toArray(rv);
        }

        QuadBank(int mask)
        {
            this.mask = mask;
        }
    }
}
