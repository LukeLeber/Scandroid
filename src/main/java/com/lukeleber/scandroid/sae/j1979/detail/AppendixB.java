// This file is protected under the KILLGPL.
// For more information, visit http://www.lukeleber.github.io/KILLGPL.html
//
// Copyright (c) Luke Leber <LukeLeber@gmail.com>

package com.lukeleber.scandroid.sae.j1979.detail;

import android.support.annotation.NonNull;

import com.lukeleber.scandroid.Constants;
import com.lukeleber.scandroid.sae.j1979.AuxiliaryInputStatus;
import com.lukeleber.scandroid.sae.j1979.DefaultPID;
import com.lukeleber.scandroid.sae.j1979.DriveCycleMonitorStatus;
import com.lukeleber.scandroid.sae.j1979.MonitorStatus;
import com.lukeleber.scandroid.sae.j1979.OBDSupport;
import com.lukeleber.scandroid.sae.j1979.OxygenSensor;
import com.lukeleber.scandroid.sae.j1979.PID;
import com.lukeleber.scandroid.sae.j1979.SecondaryAirStatus;
import com.lukeleber.scandroid.sae.j1979.FuelSystemStatus;
import com.lukeleber.scandroid.sae.j2012.DiagnosticTroubleCode;
import com.lukeleber.scandroid.util.Unit;
import com.lukeleber.util.SerializablePair;

import java.util.HashMap;

/**
 * Defined herein are the PIDs mandated by SAE J1979 present in Appendix B.
 *
 * Fair warning: This could get ugly...this code is not for the faint of heart.
 *
 * This internal class shall not be fully documented.
 *
 */
public class AppendixB
{

    /// PID $01 Definition: Monitor Status Since DTCs Cleared
    public final static PID<MonitorStatus> MONITOR_STATUS
            = new DefaultPID<>(
            0x01,
            "Monitor Status",
            "Retrieve the monitor status since the last time the DTCs wee cleared",
            new HashMap<Unit, PID.Unmarshaller<MonitorStatus>>()
            {
                {
                    super.put(Unit.PACKETED, new PID.Unmarshaller<MonitorStatus>()
                    {
                        @Override
                        public MonitorStatus invoke(@NonNull byte... bytes)
                        {
                            return new MonitorStatus(((bytes[0] & 0xFF) << 24) |
                                                     ((bytes[1] & 0xFF) << 16) |
                                                     ((bytes[2] & 0xFF) << 8) |
                                                     (bytes[3] & 0xFF));
                        }
                    });
                }
            }
    );

    /// A PID that requests the {@link DiagnosticTroubleCode DTC} that caused a freeze-frame
    /// Note: {@link DiagnosticTroubleCode#NO_DTC is returned if there is not a DTC set
    public final static PID<DiagnosticTroubleCode> FREEZE_FRAME_DTC
        = new DefaultPID<>(
        0x02,
        "DTCFRZF",
        "Check to see whether or not freeze frame data exists",
        new HashMap<Unit, PID.Unmarshaller<DiagnosticTroubleCode>>()
        {
            {
                super.put(Unit.INTERNAL_PLACEHOLDER,
                    new PID.Unmarshaller<DiagnosticTroubleCode>()
                    {
                        @Override
                        public DiagnosticTroubleCode invoke(@NonNull byte... bytes)
                        {
                            return new DiagnosticTroubleCode((bytes[0] << 8) | bytes[1], "");
                        }
                    }
                );
            }
        }
    );

    /// A PID that requests the {@link FuelSystemStatus statuses} of all onboard fuel systems
    public final static PID<SerializablePair<FuelSystemStatus, FuelSystemStatus>> FUEL_SYSTEM_STATUS
            = new DefaultPID<>(
            0x03,
            "FSYS_STAT",
            "Retrieves a collection of statuses for all onboard fuel systems",
            new HashMap<Unit, PID.Unmarshaller<SerializablePair<FuelSystemStatus, FuelSystemStatus>>>()
            {
                {
                    super.put(Unit.INTERNAL_PLACEHOLDER,
                    new PID.Unmarshaller<SerializablePair<FuelSystemStatus, FuelSystemStatus>>()
                    {

                        @Override
                        public SerializablePair<FuelSystemStatus, FuelSystemStatus> invoke(
                                @NonNull byte... bytes)
                        {
                            return new SerializablePair<>(FuelSystemStatus.forByte(bytes[0]),
                                                          FuelSystemStatus.forByte(bytes[1]));
                        }
                    });
                }
            }
    );

    /// A PID that requests the calculated engine load (LOAD)
    public final static PID<Float> CALCULATED_ENGINE_LOAD = new DefaultPID<>(
            0x04,
            "LOAD_PCT",
            "Retrieves the calculated engine load value (%)",
            new HashMap<Unit, PID.Unmarshaller<Float>>()
            {
                {
                    super.put(Unit.PERCENT, new PID.Unmarshaller<Float>()
                    {
                        @Override
                        public Float invoke(@NonNull byte... bytes)
                        {
                            return (bytes[0] & 0xFF) * 100.0f / 255.0f;
                        }
                    });
                }
            }
    );

    /// A PID that requests the coolant temperature (CTS)
    /// NOTE: Diesel vehicles that are unequipped with a coolant temperature sensor are permitted
    ///       to substitute the reading of the engine oil temperature instead!
    public final static PID<Integer> ENGINE_COOLANT_TEMPERATURE = new DefaultPID<>(
            0x05,
            "ECT",
            "Retrieves the engine coolant temperature (°C, °F)",
            new HashMap<Unit, PID.Unmarshaller<Integer>>()
            {
                {
                    super.put(Unit.TEMPERATURE_CELSIUS, new PID.Unmarshaller<Integer>()
                    {
                        @Override
                        public Integer invoke(@NonNull byte... bytes)
                        {
                            return (bytes[0] & 0xFF) - 40;
                        }
                    });
                    super.put(Unit.TEMPERATURE_FAHRENHEIT, new PID.Unmarshaller<Integer>()
                    {
                        @Override
                        public Integer invoke(@NonNull byte... bytes)
                        {
                            return (int) (((bytes[0] & 0xFF) - 40) * (9.0f / 5.0f) + 32);
                        }
                    });
                }
            }
    );

    /// A PID that requests the short term fuel trim value for bank 1 (STFT1)
    public final static PID<SerializablePair<Float, Float>> SHORT_TERM_FUEL_TRIM_BANK_1_3 =
            new DefaultPID<>(
                    0x06,
                    "SHRTFT1",
                    "Retrieve the short term fuel trim for bank 1 (%)",
                    new HashMap<Unit, PID.Unmarshaller<SerializablePair<Float, Float>>>()
                    {
                        {
                            super.put(Unit.PERCENT,
                                      new PID.Unmarshaller<SerializablePair<Float, Float>>()
                                      {
                                          @Override
                                          public SerializablePair<Float, Float> invoke(
                                                  @NonNull byte... bytes)
                                          {
                                              return new SerializablePair<>(
                                                      ((bytes[0] & 0xFF) - 128) * 100.0f / 128.0f,
                                                      bytes.length > 1 ?
                                                              (((bytes[1] & 0xFF) - 128) * 100.0f / 128.0f) :
                                                              null);
                                          }
                                      });
                        }
                    }
            );

    /// A PID that requests the long term fuel trim value for bank 1 (LTFT1)
    public final static PID<SerializablePair<Float, Float>> LONG_TERM_FUEL_TRIM_BANK_1_3 =
            new DefaultPID<>(
                    0x07,
                    "LONGFT1",
                    "Retrieve the long term fuel trim for bank 1 (%)",
                    new HashMap<Unit, PID.Unmarshaller<SerializablePair<Float, Float>>>()
                    {
                        {
                            super.put(Unit.PERCENT,
                                      new PID.Unmarshaller<SerializablePair<Float, Float>>()
                                      {
                                          @Override
                                          public SerializablePair<Float, Float> invoke(
                                                  @NonNull byte... bytes)
                                          {
                                              return new SerializablePair<>(
                                                      ((bytes[0] & 0xFF) - 128) * 100.0f / 128.0f,
                                                      bytes.length > 1 ?
                                                              (((bytes[1] & 0xFF) - 128) * 100.0f / 128.0f) :
                                                              null);
                                          }
                                      });
                        }
                    }
            );

    /// A PID that requests the short term fuel trim value for bank 2 (STFT2)
    public final static PID<SerializablePair<Float, Float>> SHORT_TERM_FUEL_TRIM_BANK_2_4 =
            new DefaultPID<>(
                    0x08,
                    "SHRTFT2",
                    "Retrieve the short term fuel trim for bank 2 (%)",
                    new HashMap<Unit, PID.Unmarshaller<SerializablePair<Float, Float>>>()
                    {
                        {
                            super.put(Unit.PERCENT,
                                      new PID.Unmarshaller<SerializablePair<Float, Float>>()
                                      {
                                          @Override
                                          public SerializablePair<Float, Float> invoke(
                                                  @NonNull byte... bytes)
                                          {
                                              return new SerializablePair<>(
                                                      ((bytes[0] & 0xFF) - 128) * 100.0f / 128.0f,
                                                      bytes.length > 1 ?
                                                              (((bytes[1] & 0xFF) - 128) * 100.0f / 128.0f) :
                                                              null);
                                          }
                                      });
                        }
                    }
            );

    /// A PID that requests the long term fuel trim value for bank 2 (LTFT2)
    public final static PID<SerializablePair<Float, Float>> LONG_TERM_FUEL_TRIM_BANK_2_4 =
            new DefaultPID<>(
                    0x09,
                    "LONGFT2",
                    "Retrieve the long term fuel trim for bank 2 (%)",
                    new HashMap<Unit, PID.Unmarshaller<SerializablePair<Float, Float>>>()
                    {
                        {
                            super.put(Unit.PERCENT,
                                      new PID.Unmarshaller<SerializablePair<Float, Float>>()
                                      {
                                          @Override
                                          public SerializablePair<Float, Float> invoke(
                                                  @NonNull byte... bytes)
                                          {
                                              return new SerializablePair<>(
                                                      ((bytes[0] & 0xFF) - 128) * 100.0f / 128.0f,
                                                      bytes.length > 1 ?
                                                              (((bytes[1] & 0xFF) - 128) * 100.0f / 128.0f) :
                                                              null);
                                          }
                                      });
                        }
                    }
            );

    /// A PID that requests the fuel pressure at the fuel rail relative to atmosphere (FP)
    /// NOTE: An ECU may ONLY support ONE of the following PIDs: 0x0A, 0x22, or 0x23
    ///       0x0A uses atmospheric pressure as a reference
    ///       0x22 uses manifold vacuum as a reference
    ///       0x23 uses atmospheric pressure as a reference, but has a wider value range (this
    ///       PID is most likely to be used on diesels, which operate at a much higher
    ///       fuel pressure)
    public final static PID<Integer> FUEL_RAIL_PRESSURE_NARROW_RANGE_ATMOSPHEREIC_REFERENCE =
            new DefaultPID<>(
                    0x0A,
                    "FRP",
                    "Retrieve the fuel pressure (kPa)",
                    new HashMap<Unit, PID.Unmarshaller<Integer>>()
                    {
                        {
                            super.put(Unit.KILO_PASCALS, new PID.Unmarshaller<Integer>()
                            {
                                @Override
                                public Integer invoke(@NonNull byte... bytes)
                                {
                                    return (bytes[0] & 0xFF) * 3;
                                }
                            });
                            super.put(Unit.PSI, new PID.Unmarshaller<Integer>()
                            {
                                @Override
                                public Integer invoke(@NonNull byte... bytes)
                                {
                                    return (int)(((bytes[0] & 0xFF) * 3) * Constants.PSI_TO_KILO_PASCAL_FACTOR);
                                }
                            });
                        }
                    }
            );

    /// A PID that requests the manifold pressure by a manifold absolute pressure sensor. (MAP)
    /// NOTE: For vehicles that are equipped with both MAP and MAF sensors, this PID
    /// is required to be supported.
    public final static PID<Integer> INTAKE_MANIFOLD_ABSOLUTE_PRESSURE =
            new DefaultPID<>(
                    0x0B,
                    "MAP",
                    "Retrieve the intake manifold absolute pressure",
                    new HashMap<Unit, PID.Unmarshaller<Integer>>()
                    {
                        {
                            super.put(Unit.KILO_PASCALS, new PID.Unmarshaller<Integer>()
                            {
                                @Override
                                public Integer invoke(@NonNull byte... bytes)
                                {
                                    return bytes[0] & 0xFF;
                                }
                            });
                            super.put(Unit.PSI, new PID.Unmarshaller<Integer>()
                            {
                                @Override
                                public Integer invoke(@NonNull byte... bytes)
                                {
                                    return (int)((bytes[0] & 0xFF) * Constants.PSI_TO_KILO_PASCAL_FACTOR);
                                }
                            });
                        }
                    }
            );

    /// A PID that requests the engine speed (RPM)
    public final static PID<Float> ENGINE_SPEED =
            new DefaultPID<>(
                    0x0C,
                    "TACH",
                    "Retrieve the speed of the engine",
                    new HashMap<Unit, PID.Unmarshaller<Float>>()
                    {
                        {
                            super.put(Unit.ROTATIONS_PER_MINUTE, new PID.Unmarshaller<Float>()
                            {
                                @Override
                                public Float invoke(@NonNull byte... bytes)
                                {
                                    return (((bytes[0] & 0xFF) << 8) | (bytes[1] & 0xFF)) / 4.0f;
                                }
                            });
                        }
                    }
            );

    /// A PID that requests the vehicle speed (VSS)
    /// NOTE: SAE-J1979 is loose about how this PID is supported.  The actual reading may result
    ///       directly from a VSS, calculated by the ECU, or obtained from a different onboard
    ///       module (likely an ABS control module).
    public final static PID<Integer> VEHICLE_SPEED =
            new DefaultPID<>(
                    0x0D,
                    "VSS",
                    "Retrieve the vehicle speed",
                    new HashMap<Unit, PID.Unmarshaller<Integer>>()
                    {
                        {
                            super.put(Unit.KILOMETERS_PER_HOUR, new PID.Unmarshaller<Integer>()
                            {
                                @Override
                                public Integer invoke(@NonNull byte... bytes)
                                {
                                    return bytes[0] & 0xFF;
                                }
                            });
                            super.put(Unit.MILES_PER_HOUR, new PID.Unmarshaller<Integer>()
                            {
                                @Override
                                public Integer invoke(@NonNull byte... bytes)
                                {
                                    return (int)((bytes[0] & 0xFF) * Constants.KILOMETERS_TO_MILES);
                                }
                            });
                        }
                    }
            );

    /// A PID that requests the ignition timing advance for cylinder #1
    /// NOTE: The returned value DOES NOT include any mechanical advancement or retardation.
    public final static PID<Float> TIMING_ADVANCE =
            new DefaultPID<>(
                    0x0E,
                    "SPARK_ADV",
                    "Retrieve the timing advance relative to cylinder 1",
                    new HashMap<Unit, PID.Unmarshaller<Float>>()
                    {
                        {
                            super.put(Unit.ANGLE_DEGREES, new PID.Unmarshaller<Float>()
                            {
                                @Override
                                public Float invoke(@NonNull byte... bytes)
                                {
                                    return ((bytes[0] & 0xFF) - 128) / 2.0f;
                                }
                            });
                        }
                    }
            );

    /// A PID that requests the intake air temperature (IAT)
    /// NOTE: SAE-J1979 is loose about how this PID is supported.  The actual reading may result
    ///       directly from an IAT sensor, or may be calculated by "other sensor inputs".
    public final static PID<Integer> INTAKE_AIR_TEMPERATURE =
            new DefaultPID<>(
                    0x0F,
                    "IAT",
                    "Retrieve the intake air temperature",
                    new HashMap<Unit, PID.Unmarshaller<Integer>>()
                    {
                        {
                            super.put(Unit.TEMPERATURE_FAHRENHEIT, new PID.Unmarshaller<Integer>()
                            {
                                @Override
                                public Integer invoke(@NonNull byte... bytes)
                                {
                                    return (int) (((bytes[0] & 0xFF) - 40) * (9.0f / 5.0f) + 32);
                                }
                            });
                            super.put(Unit.TEMPERATURE_CELSIUS, new PID.Unmarshaller<Integer>()
                            {
                                @Override
                                public Integer invoke(@NonNull byte... bytes)
                                {
                                    return (bytes[0] & 0xFF) - 40;
                                }
                            });
                        }
                    }
            );

    /// A PID that requests the airflow rate by a mass airflow sensor (MAF)
    public final static PID<Float> MASS_AIRFLOW_RATE =
            new DefaultPID<>(
                    0x10,
                    "MAF",
                    "Retrieve the MAF reading",
                    new HashMap<Unit, PID.Unmarshaller<Float>>()
                    {
                        {
                            super.put(Unit.GRAMS_PER_SECOND, new PID.Unmarshaller<Float>()
                            {
                                @Override
                                public Float invoke(@NonNull byte... bytes)
                                {
                                    return (((bytes[0] & 0xFF) << 8) | (bytes[1] & 0xFF)) / 100.0f;
                                }
                            });
                            super.put(Unit.POUNDS_PER_MINUTE, new PID.Unmarshaller<Float>()
                            {
                                @Override
                                public Float invoke(@NonNull byte... bytes)
                                {
                                    return ((((bytes[0] & 0xFF) << 8) | (bytes[1] & 0xFF)) / 100.0f) * Constants.GRAMS_PER_SEC_TO_POUNDS_PER_MIN;
                                }
                            });
                        }
                    }
            );

    /// A PID that requests the absolute throttle position (TPS)
    public final static PID<Float> ABSOLUTE_THROTTLE_POSITION =
            new DefaultPID<>(
                    0x11,
                    "TPS",
                    "Retrieve the absolute throttle position",
                    new HashMap<Unit, PID.Unmarshaller<Float>>()
                    {
                        {
                            super.put(Unit.PERCENT, new PID.Unmarshaller<Float>()
                            {
                                @Override
                                public Float invoke(@NonNull byte... bytes)
                                {
                                    return ((bytes[0] & 0xFF) * 100.0f) / 255.0f;
                                }
                            });
                        }
                    }
            );

    /// A PID that requests the secondary air status (AIR)
    public final static PID<SecondaryAirStatus> SECONDARY_AIR_STATUS =
            new DefaultPID<>(
                    0x12,
                    "AIR",
                    "Retrieve the secondary air system status",
                    new HashMap<Unit, PID.Unmarshaller<SecondaryAirStatus>>()
                    {
                        {
                            super.put(Unit.INTERNAL_PLACEHOLDER,
                                      new PID.Unmarshaller<SecondaryAirStatus>()
                                      {
                                          @Override
                                          public SecondaryAirStatus invoke(@NonNull byte... bytes)
                                          {
                                              return SecondaryAirStatus.forByte(bytes[0]);
                                          }
                                      });
                        }
                    }
            );

    /// A PID that requests the location of all onboard conventional oxygen sensors
    /// NOTE: SAE_J1979 specifies that if this PID is supported, then PID 1D must be unsupported
    public final static PID<OxygenSensor[]> DUAL_BANK_OXYGEN_SENSOR_LOCATIONS =
            new DefaultPID<>(
                    0x13,
                    "O2SLOC",
                    "Retrieve all equipped oxygen sensors",
                    new HashMap<Unit, PID.Unmarshaller<OxygenSensor[]>>()
                    {
                        {
                            super.put(Unit.INTERNAL_PLACEHOLDER,
                                      new PID.Unmarshaller<OxygenSensor[]>()
                                      {
                                          @Override
                                          public OxygenSensor[] invoke(@NonNull byte... bytes)
                                          {
                                              return OxygenSensor.DualBank.forByte(bytes[0]);
                                          }
                                      });
                        }
                    }
            );

    private final static PID.Unmarshaller<SerializablePair<Float, Float>> CONVENTIONAL_O2S =
        new PID.Unmarshaller<SerializablePair<Float, Float>>()
        {
            @Override
            public SerializablePair<Float, Float> invoke(@NonNull byte... bytes)
            {
                return new SerializablePair<>((bytes[0] & 0xFF) * 0.005f, 0.78125f * (bytes[0] & 0xFF) - 100.0f);
            }
        };



    /// A PID that requests the reading of O2S11 on a dual-bank system
    /// NOTE: SAE-J1979 specifies that this PID cannot be supported if PID $24 is
    public final static PID<SerializablePair<Float, Float>> DUAL_BANK_O2S11_CONVENTIONAL = new DefaultPID<>(
            0x14,
            "O2S11",
            "Retrieve O2S11 value",
            new HashMap<Unit, PID.Unmarshaller<SerializablePair<Float, Float>>>()
            {
                {
                    super.put(Unit.CONVENTIONAL_O2S, CONVENTIONAL_O2S);
                }
            }
    );

    /// A PID that requests the reading of O2S11 on a quad-bank system
    /// NOTE: SAE-J1979 specifies that this PID cannot be supported if PID $24 is
    public final static PID<SerializablePair<Float, Float>> QUAD_BANK_O2S11_CONVENTIONAL = new DefaultPID<>(
            0x14,
            "O2S11",
            "Retrieve O2S11 value",
            new HashMap<Unit, PID.Unmarshaller<SerializablePair<Float, Float>>>()
            {
                {
                    super.put(Unit.CONVENTIONAL_O2S, CONVENTIONAL_O2S);
                }
            }
    );

    /// A PID that requests the reading of O2S12 on a dual-bank system
    /// NOTE: SAE-J1979 specifies that this PID cannot be supported if PID $25 is
    public final static PID<SerializablePair<Float, Float>> DUAL_BANK_O2S12_CONVENTIONAL = new DefaultPID<>(
            0x15,
            "O2S12",
            "Retrieve O2S12 value",
            new HashMap<Unit, PID.Unmarshaller<SerializablePair<Float, Float>>>()
            {
                {
                    super.put(Unit.CONVENTIONAL_O2S, CONVENTIONAL_O2S);
                }
            }
    );
    /// A PID that requests the reading of O2S12 on a quad-bank system
    /// NOTE: SAE-J1979 specifies that this PID cannot be supported if PID $25 is
    public final static PID<SerializablePair<Float, Float>> QUAD_BANK_O2S12_CONVENTIONAL = new DefaultPID<>(
            0x15,
            "O2S12",
            "Retrieve O2S12 value",
            new HashMap<Unit, PID.Unmarshaller<SerializablePair<Float, Float>>>()
            {
                {
                    super.put(Unit.CONVENTIONAL_O2S, CONVENTIONAL_O2S);
                }
            }
    );

    /// A PID that requests the reading of O2S13 on a dual-bank system
    /// NOTE: SAE-J1979 specifies that this PID cannot be supported if PID $26 is
    public final static PID<SerializablePair<Float, Float>> DUAL_BANK_O2S13_CONVENTIONAL = new DefaultPID<>(
            0x16,
            "O2S13",
            "Retrieve O2S13 value",
            new HashMap<Unit, PID.Unmarshaller<SerializablePair<Float, Float>>>()
            {
                {
                    super.put(Unit.CONVENTIONAL_O2S, CONVENTIONAL_O2S);
                }
            }
    );

    /// A PID that requests the reading of O2S21 on a quad-bank system
    /// NOTE: SAE-J1979 specifies that this PID cannot be supported if PID $28 is
    public final static PID<SerializablePair<Float, Float>> QUAD_BANK_O2S21_CONVENTIONAL = new DefaultPID<>(
            0x16,
            "O2S21",
            "Retrieve O2S21 value",
            new HashMap<Unit, PID.Unmarshaller<SerializablePair<Float, Float>>>()
            {
                {
                    super.put(Unit.CONVENTIONAL_O2S, CONVENTIONAL_O2S);
                }
            }
    );

    /// A PID that requests the reading of O2S14 on a dual-bank system
    /// NOTE: SAE-J1979 specifies that this PID cannot be supported if PID $27 is
    public final static PID<SerializablePair<Float, Float>> DUAL_BANK_O2S14_CONVENTIONAL = new DefaultPID<>(
            0x17,
            "O2S14",
            "Retrieve O2S14 value",
            new HashMap<Unit, PID.Unmarshaller<SerializablePair<Float, Float>>>()
            {
                {
                    super.put(Unit.CONVENTIONAL_O2S, CONVENTIONAL_O2S);
                }
            }
    );

    /// A PID that requests the reading of O2S22 on a quad-bank system
    /// NOTE: SAE-J1979 specifies that this PID cannot be supported if PID $27 is
    public final static PID<SerializablePair<Float, Float>> QUAD_BANK_O2S22_CONVENTIONAL = new DefaultPID<>(
            0x17,
            "O2S22",
            "Retrieve O2S22 value",
            new HashMap<Unit, PID.Unmarshaller<SerializablePair<Float, Float>>>()
            {
                {
                    super.put(Unit.CONVENTIONAL_O2S, CONVENTIONAL_O2S);
                }
            }
    );

    /// A PID that requests the reading of O2S21 on a dual-bank system
    /// NOTE: SAE-J1979 specifies that this PID cannot be supported if PID $28 is
    public final static PID<SerializablePair<Float, Float>> DUAL_BANK_O2S21_CONVENTIONAL = new DefaultPID<>(
            0x18,
            "O2S21",
            "Retrieve O2S21 value",
            new HashMap<Unit, PID.Unmarshaller<SerializablePair<Float, Float>>>()
            {
                {
                    super.put(Unit.CONVENTIONAL_O2S, CONVENTIONAL_O2S);
                }
            }
    );

    /// A PID that requests the reading of O2S31 on a quad-bank system
    /// NOTE: SAE-J1979 specifies that this PID cannot be supported if PID $28 is
    public final static PID<SerializablePair<Float, Float>> QUAD_BANK_O2S31_CONVENTIONAL = new DefaultPID<>(
            0x18,
            "O2S31",
            "Retrieve O2S31 value",
            new HashMap<Unit, PID.Unmarshaller<SerializablePair<Float, Float>>>()
            {
                {
                    super.put(Unit.CONVENTIONAL_O2S, CONVENTIONAL_O2S);
                }
            }
    );

    /// A PID that requests the reading of O2S14 on a dual-bank system
    /// NOTE: SAE-J1979 specifies that this PID cannot be supported if PID $29 is
    public final static PID<SerializablePair<Float, Float>> DUAL_BANK_O2S22_CONVENTIONAL = new DefaultPID<>(
            0x19,
            "O2S22",
            "Retrieve O2S22 value",
            new HashMap<Unit, PID.Unmarshaller<SerializablePair<Float, Float>>>()
            {
                {
                    super.put(Unit.CONVENTIONAL_O2S, CONVENTIONAL_O2S);
                }
            }
    );

    /// A PID that requests the reading of O2S32 on a quad-bank system
    /// NOTE: SAE-J1979 specifies that this PID cannot be supported if PID $29 is
    public final static PID<SerializablePair<Float, Float>> QUAD_BANK_O2S32_CONVENTIONAL = new DefaultPID<>(
            0x19,
            "O2S32",
            "Retrieve O2S32 value",
            new HashMap<Unit, PID.Unmarshaller<SerializablePair<Float, Float>>>()
            {
                {
                    super.put(Unit.CONVENTIONAL_O2S, CONVENTIONAL_O2S);
                }
            }
    );

    /// A PID that requests the reading of O2S23 on a dual-bank system
    /// NOTE: SAE-J1979 specifies that this PID cannot be supported if PID $2A is
    public final static PID<SerializablePair<Float, Float>> DUAL_BANK_O2S23_CONVENTIONAL = new DefaultPID<>(
            0x1A,
            "O2S23",
            "Retrieve O2S23 value",
            new HashMap<Unit, PID.Unmarshaller<SerializablePair<Float, Float>>>()
            {
                {
                    super.put(Unit.CONVENTIONAL_O2S, CONVENTIONAL_O2S);
                }
            }
    );

    /// A PID that requests the reading of O2S41 on a quad-bank system
    /// NOTE: SAE-J1979 specifies that this PID cannot be supported if PID $2A is
    public final static PID<SerializablePair<Float, Float>> QUAD_BANK_O2S41_CONVENTIONAL = new DefaultPID<>(
            0x1A,
            "O2S41",
            "Retrieve O2S41 value",
            new HashMap<Unit, PID.Unmarshaller<SerializablePair<Float, Float>>>()
            {
                {
                    super.put(Unit.CONVENTIONAL_O2S, CONVENTIONAL_O2S);
                }
            }
    );

    /// A PID that requests the reading of O2S24 on a dual-bank system
    /// NOTE: SAE-J1979 specifies that this PID cannot be supported if PID $2B is
    public final static PID<SerializablePair<Float, Float>> DUAL_BANK_O2S24_CONVENTIONAL = new DefaultPID<>(
            0x1B,
            "O2S24",
            "Retrieve O2S24 value",
            new HashMap<Unit, PID.Unmarshaller<SerializablePair<Float, Float>>>()
            {
                {
                    super.put(Unit.CONVENTIONAL_O2S, CONVENTIONAL_O2S);
                }
            }
    );

    /// A PID that requests the reading of O2S42 on a quad-bank system
    /// NOTE: SAE-J1979 specifies that this PID cannot be supported if PID $2B is
    public final static PID<SerializablePair<Float, Float>> QUAD_BANK_O2S42_CONVENTIONAL = new DefaultPID<>(
            0x1B,
            "O2S42",
            "Retrieve O2S42 value",
            new HashMap<Unit, PID.Unmarshaller<SerializablePair<Float, Float>>>()
            {
                {
                    super.put(Unit.CONVENTIONAL_O2S, CONVENTIONAL_O2S);
                }
            }
    );

    /// A PID that requests the type of on-board diagnostics that the vehicle supports
    public final static PID<OBDSupport> OBD_SUPPORT = new DefaultPID<>(
            0x1C,
            "OBDSUP",
            "Retrieve the OBD standard(s) that this vehicle conforms to",
            new HashMap<Unit, PID.Unmarshaller<OBDSupport>>()
            {
                {
                    super.put(Unit.INTERNAL_PLACEHOLDER, new PID.Unmarshaller<OBDSupport>()
                    {
                        @Override
                        public OBDSupport invoke(@NonNull byte... bytes)
                        {
                            return OBDSupport.forByte(bytes[0]);
                        }
                    });
                }
            }
    );

    /// A PID that requests the location of all onboard wide range oxygen sensors
    /// NOTE: SAE_J1979 specifies that if this PID is supported, then PID 13 must be unsupported
    public final static PID<OxygenSensor[]> QUAD_BANK_OXYGEN_SENSOR_LOCATIONS =
            new DefaultPID<>(
                    0x1D,
                    "O2SLOC",
                    "Retrieve all equipped oxygen sensors",
                    new HashMap<Unit, PID.Unmarshaller<OxygenSensor[]>>()
                    {
                        {
                            super.put(Unit.INTERNAL_PLACEHOLDER,
                                      new PID.Unmarshaller<OxygenSensor[]>()
                                      {
                                          @Override
                                          public OxygenSensor[] invoke(@NonNull byte... bytes)
                                          {
                                              return OxygenSensor.QuadBank.forByte(bytes[0]);
                                          }
                                      });
                        }
                    }
            );

    /// A PID that requests the status of an onboard PTO unit
    public final static PID<AuxiliaryInputStatus> AUXILIARY_INPUT_STATUS = new DefaultPID<>(
            0x1E,
            "PTO_STAT",
            "Retrieve the Power Take Off (PTO) status",
            new HashMap<Unit, PID.Unmarshaller<AuxiliaryInputStatus>>()
            {
                {
                    super.put(Unit.BOOLEAN, new PID.Unmarshaller<AuxiliaryInputStatus>()
                    {
                        @Override
                        public AuxiliaryInputStatus invoke(@NonNull byte... bytes)
                        {
                            return AuxiliaryInputStatus.forByte(bytes[0]);
                        }
                    });
                }
            }
    );

    public final static PID<Integer> TIME_SINCE_ENGINE_START = new DefaultPID<>(
            0x1F,
            "RUNTM",
            "Retrieve the elapsed time since the engine has started",
            new HashMap<Unit, PID.Unmarshaller<Integer>>()
            {
                {
                    super.put(Unit.SECONDS, new PID.Unmarshaller<Integer>()
                    {
                        @Override
                        public Integer invoke(@NonNull byte... bytes)
                        {
                            return ((bytes[0] & 0xFF) << 8) | bytes[0];
                        }
                    });
                }
            }
    );

    public final static PID<Integer> DISTANCE_TRAVELLED_WHILE_MIL_IS_ACTIVATED = new DefaultPID<>(
            0x21,
            "MIL_DIST",
            "Retrieve the distance travelled since the MIL has been activated",
            new HashMap<Unit, PID.Unmarshaller<Integer>>()
            {
                {
                    super.put(Unit.KILOMETERS, new PID.Unmarshaller<Integer>()
                    {
                        @Override
                        public Integer invoke(@NonNull byte... bytes)
                        {
                            return ((bytes[0] & 0xFF) << 8) | bytes[1];
                        }
                    });
                    super.put(Unit.MILES, new PID.Unmarshaller<Integer>()
                    {
                        @Override
                        public Integer invoke(@NonNull byte... bytes)
                        {
                            return (int)((((bytes[0] & 0xFF) << 8) | bytes[1]) * Constants.KILOMETERS_TO_MILES);
                        }
                    });
                }
            }
    );

    /// A PID that requests the fuel pressure at the fuel rail relative to intake manifold vacuum (FP)
    /// NOTE: An ECU may ONLY support ONE of the following PIDs: 0x0A, 0x22, or 0x23
    ///       0x0A uses atmospheric pressure as a reference
    ///       0x22 uses manifold vacuum as a reference
    ///       0x23 uses atmospheric pressure as a reference, but has a wider value range (this
    ///       PID is most likely to be used on diesels, which operate at a much higher
    ///       fuel pressure)
    public final static PID<Float> FUEL_RAIL_PRESSURE_RELATIVE_TO_MANIFOLD_VACUUM =
            new DefaultPID<>(
                    0x22,
                    "FRP",
                    "Retrieves the fuel rail pressure relative to manifold vacuum",
                    new HashMap<Unit, PID.Unmarshaller<Float>>()
                    {
                        {
                            super.put(Unit.KILO_PASCALS, new PID.Unmarshaller<Float>()
                            {
                                @Override
                                public Float invoke(@NonNull byte... bytes)
                                {
                                    return (((bytes[0] & 0xFF) << 8) | bytes[1]) * 0.079F;
                                }
                            });
                            super.put(Unit.PSI, new PID.Unmarshaller<Float>()
                            {
                                @Override
                                public Float invoke(@NonNull byte... bytes)
                                {
                                    return ((((bytes[0] & 0xFF) << 8) | bytes[1]) * 0.079F) * Constants.KILO_PASCAL_TO_PSI_FACTOR;
                                }
                            });
                        }
                    }
            );

    /// A PID that requests the fuel pressure at the fuel rail relative to atmosphere
    /// NOTE: An ECU may ONLY support ONE of the following PIDs: 0x0A, 0x22, or 0x23
    ///       0x0A uses atmospheric pressure as a reference
    ///       0x22 uses manifold vacuum as a reference
    ///       0x23 uses atmospheric pressure as a reference, but has a wider value range (this
    ///       PID is most likely to be used on diesels, which operate at a much higher
    ///       fuel pressure)
    public final static PID<Integer> FUEL_RAIL_PRESSURE_WIDE_RANGE = new DefaultPID<>(
            0x23,
            "FRP",
            "Retrieves the fuel rail pressure relative to atmosphere (wide range)",
            new HashMap<Unit, PID.Unmarshaller<Integer>>()
            {
                {
                    super.put(Unit.KILO_PASCALS, new PID.Unmarshaller<Integer>()
                    {
                        @Override
                        public Integer invoke(@NonNull byte... bytes)
                        {
                            return (((bytes[0] & 0xFF) << 8) | bytes[1]) * 10;
                        }
                    });
                    super.put(Unit.PSI, new PID.Unmarshaller<Integer>()
                    {
                        @Override
                        public Integer invoke(@NonNull byte... bytes)
                        {
                            return (int) ((((bytes[0] & 0xFF) << 8) | bytes[1]) * 1.450377f);
                        }
                    });
                }
            }
    );

    private final static PID.Unmarshaller<SerializablePair<Float, Float>>
            WIDE_RANGE_O2S_UNMARSHALLER = new PID.Unmarshaller<SerializablePair<Float, Float>>()
    {
        @Override
        public SerializablePair<Float, Float> invoke(@NonNull byte... bytes)
        {
            return new SerializablePair<>(
                    (((bytes[0] << 8) | bytes[1]) * 0.0000305f),
                    (((bytes[2] << 8) | bytes[3]) * 0.000122f)
            );
        }
    };

    /// A PID that requests the reading of O2S11 on a dual-bank system
    /// NOTE: SAE-J1979 specifies that this PID cannot be supported if PID $24 is
    public final static PID<SerializablePair<Float, Float>> DUAL_BANK_O2S11_WIDE_RANGE =
            new DefaultPID<>(
                    0x24,
                    "O2S11",
                    "Retrieve O2S11 value",
                    new HashMap<Unit, PID.Unmarshaller<SerializablePair<Float, Float>>>()
                    {
                        {
                            super.put(Unit.INTERNAL_PLACEHOLDER, WIDE_RANGE_O2S_UNMARSHALLER);
                        }
                    }
            );

    /// A PID that requests the reading of O2S11 on a quad-bank system
    /// NOTE: SAE-J1979 specifies that this PID cannot be supported if PID $14 is
    public final static PID<SerializablePair<Float, Float>> QUAD_BANK_O2S11_WIDE_RANGE =
            new DefaultPID<>(
                    0x24,
                    "O2S11",
                    "Retrieve O2S11 value",
                    new HashMap<Unit, PID.Unmarshaller<SerializablePair<Float, Float>>>()
                    {
                        {
                            super.put(Unit.INTERNAL_PLACEHOLDER, WIDE_RANGE_O2S_UNMARSHALLER);
                        }
                    }
            );

    /// A PID that requests the reading of O2S12 on a dual-bank system
    /// NOTE: SAE-J1979 specifies that this PID cannot be supported if PID $15 is
    public final static PID<SerializablePair<Float, Float>> DUAL_BANK_O2S12_WIDE_RANGE =
            new DefaultPID<>(
                    0x25,
                    "O2S12",
                    "Retrieve O2S12 value",
                    new HashMap<Unit, PID.Unmarshaller<SerializablePair<Float, Float>>>()
                    {
                        {
                            super.put(Unit.INTERNAL_PLACEHOLDER, WIDE_RANGE_O2S_UNMARSHALLER);
                        }
                    }
            );
    /// A PID that requests the reading of O2S12 on a quad-bank system
    /// NOTE: SAE-J1979 specifies that this PID cannot be supported if PID $15 is
    public final static PID<SerializablePair<Float, Float>> QUAD_BANK_O2S12_WIDE_RANGE =
            new DefaultPID<>(
                    0x25,
                    "O2S12",
                    "Retrieve O2S12 value",
                    new HashMap<Unit, PID.Unmarshaller<SerializablePair<Float, Float>>>()
                    {
                        {
                            super.put(Unit.INTERNAL_PLACEHOLDER, WIDE_RANGE_O2S_UNMARSHALLER);
                        }
                    }
            );

    /// A PID that requests the reading of O2S13 on a dual-bank system
    /// NOTE: SAE-J1979 specifies that this PID cannot be supported if PID $16 is
    public final static PID<SerializablePair<Float, Float>> DUAL_BANK_O2S13_WIDE_RANGE =
            new DefaultPID<>(
                    0x26,
                    "O2S13",
                    "Retrieve O2S13 value",
                    new HashMap<Unit, PID.Unmarshaller<SerializablePair<Float, Float>>>()
                    {
                        {
                            super.put(Unit.INTERNAL_PLACEHOLDER, WIDE_RANGE_O2S_UNMARSHALLER);
                        }
                    }
            );

    /// A PID that requests the reading of O2S21 on a quad-bank system
    /// NOTE: SAE-J1979 specifies that this PID cannot be supported if PID $18 is
    public final static PID<SerializablePair<Float, Float>> QUAD_BANK_O2S21_WIDE_RANGE =
            new DefaultPID<>(
                    0x26,
                    "O2S21",
                    "Retrieve O2S21 value",
                    new HashMap<Unit, PID.Unmarshaller<SerializablePair<Float, Float>>>()
                    {
                        {
                            super.put(Unit.INTERNAL_PLACEHOLDER, WIDE_RANGE_O2S_UNMARSHALLER);
                        }
                    }
            );

    /// A PID that requests the reading of O2S14 on a dual-bank system
    /// NOTE: SAE-J1979 specifies that this PID cannot be supported if PID $17 is
    public final static PID<SerializablePair<Float, Float>> DUAL_BANK_O2S14_WIDE_RANGE =
            new DefaultPID<>(
                    0x27,
                    "O2S14",
                    "Retrieve O2S14 value",
                    new HashMap<Unit, PID.Unmarshaller<SerializablePair<Float, Float>>>()
                    {
                        {
                            super.put(Unit.INTERNAL_PLACEHOLDER, WIDE_RANGE_O2S_UNMARSHALLER);
                        }
                    }
            );

    /// A PID that requests the reading of O2S22 on a quad-bank system
    /// NOTE: SAE-J1979 specifies that this PID cannot be supported if PID $17 is
    public final static PID<SerializablePair<Float, Float>> QUAD_BANK_O2S22_WIDE_RANGE =
            new DefaultPID<>(
                    0x27,
                    "O2S22",
                    "Retrieve O2S22 value",
                    new HashMap<Unit, PID.Unmarshaller<SerializablePair<Float, Float>>>()
                    {
                        {
                            super.put(Unit.INTERNAL_PLACEHOLDER, WIDE_RANGE_O2S_UNMARSHALLER);
                        }
                    }
            );

    /// A PID that requests the reading of O2S21 on a dual-bank system
    /// NOTE: SAE-J1979 specifies that this PID cannot be supported if PID $18 is
    public final static PID<SerializablePair<Float, Float>> DUAL_BANK_O2S21_WIDE_RANGE =
            new DefaultPID<>(
                    0x28,
                    "O2S21",
                    "Retrieve O2S21 value",
                    new HashMap<Unit, PID.Unmarshaller<SerializablePair<Float, Float>>>()
                    {
                        {
                            super.put(Unit.INTERNAL_PLACEHOLDER, WIDE_RANGE_O2S_UNMARSHALLER);
                        }
                    }
            );

    /// A PID that requests the reading of O2S31 on a quad-bank system
    /// NOTE: SAE-J1979 specifies that this PID cannot be supported if PID $18 is
    public final static PID<SerializablePair<Float, Float>> QUAD_BANK_O2S31_WIDE_RANGE =
            new DefaultPID<>(
                    0x28,
                    "O2S31",
                    "Retrieve O2S31 value",
                    new HashMap<Unit, PID.Unmarshaller<SerializablePair<Float, Float>>>()
                    {
                        {
                            super.put(Unit.INTERNAL_PLACEHOLDER, WIDE_RANGE_O2S_UNMARSHALLER);
                        }
                    }
            );

    /// A PID that requests the reading of O2S14 on a dual-bank system
    /// NOTE: SAE-J1979 specifies that this PID cannot be supported if PID $19 is
    public final static PID<SerializablePair<Float, Float>> DUAL_BANK_O2S22_WIDE_RANGE =
            new DefaultPID<>(
                    0x29,
                    "O2S22",
                    "Retrieve O2S22 value",
                    new HashMap<Unit, PID.Unmarshaller<SerializablePair<Float, Float>>>()
                    {
                        {
                            super.put(Unit.INTERNAL_PLACEHOLDER, WIDE_RANGE_O2S_UNMARSHALLER);
                        }
                    }
            );

    /// A PID that requests the reading of O2S32 on a quad-bank system
    /// NOTE: SAE-J1979 specifies that this PID cannot be supported if PID $19 is
    public final static PID<SerializablePair<Float, Float>> QUAD_BANK_O2S32_WIDE_RANGE =
            new DefaultPID<>(
                    0x29,
                    "O2S32",
                    "Retrieve O2S32 value",
                    new HashMap<Unit, PID.Unmarshaller<SerializablePair<Float, Float>>>()
                    {
                        {
                            super.put(Unit.INTERNAL_PLACEHOLDER, WIDE_RANGE_O2S_UNMARSHALLER);
                        }
                    }
            );

    /// A PID that requests the reading of O2S23 on a dual-bank system
    /// NOTE: SAE-J1979 specifies that this PID cannot be supported if PID $1A is
    public final static PID<SerializablePair<Float, Float>> DUAL_BANK_O2S23_WIDE_RANGE =
            new DefaultPID<>(
                    0x2A,
                    "O2S23",
                    "Retrieve O2S23 value",
                    new HashMap<Unit, PID.Unmarshaller<SerializablePair<Float, Float>>>()
                    {
                        {
                            super.put(Unit.INTERNAL_PLACEHOLDER, WIDE_RANGE_O2S_UNMARSHALLER);
                        }
                    }
            );

    /// A PID that requests the reading of O2S41 on a quad-bank system
    /// NOTE: SAE-J1979 specifies that this PID cannot be supported if PID $1A is
    public final static PID<SerializablePair<Float, Float>> QUAD_BANK_O2S41_WIDE_RANGE =
            new DefaultPID<>(
                    0x2A,
                    "O2S41",
                    "Retrieve O2S41 value",
                    new HashMap<Unit, PID.Unmarshaller<SerializablePair<Float, Float>>>()
                    {
                        {
                            super.put(Unit.INTERNAL_PLACEHOLDER, WIDE_RANGE_O2S_UNMARSHALLER);
                        }
                    }
            );

    /// A PID that requests the reading of O2S24 on a dual-bank system
    /// NOTE: SAE-J1979 specifies that this PID cannot be supported if PID $1B is
    public final static PID<SerializablePair<Float, Float>> DUAL_BANK_O2S24_WIDE_RANGE =
            new DefaultPID<>(
                    0x2B,
                    "O2S24",
                    "Retrieve O2S24 value",
                    new HashMap<Unit, PID.Unmarshaller<SerializablePair<Float, Float>>>()
                    {
                        {
                            super.put(Unit.INTERNAL_PLACEHOLDER, WIDE_RANGE_O2S_UNMARSHALLER);
                        }
                    }
            );

    /// A PID that requests the reading of O2S42 on a quad-bank system
    /// NOTE: SAE-J1979 specifies that this PID cannot be supported if PID $1B is
    public final static PID<SerializablePair<Float, Float>> QUAD_BANK_O2S42_WIDE_RANGE =
            new DefaultPID<>(
                    0x2B,
                    "O2S42",
                    "Retrieve O2S42 value",
                    new HashMap<Unit, PID.Unmarshaller<SerializablePair<Float, Float>>>()
                    {
                        {
                            super.put(Unit.INTERNAL_PLACEHOLDER, WIDE_RANGE_O2S_UNMARSHALLER);
                        }
                    }
            );

    /// A PID that requests the commanded EGR as a percent.  EGR shall be normalized to the
    /// maximum EGR commanded output control parameter.  EGR systems use a variety of methods
    /// to control the amount of EGR delivered to the engine.
    /// 1) If an on/off solenoid is used -- EGR_PCT shall display 0% when the EGR is commanded
    /// off, 100% when the EGR system is commanded on.
    /// 2) If a vacuum solenoid is duty cycled, the EGR duty cycle from 0 to 100% shall be
    /// displayed
    /// 3) If a linear or stepper motor valve is used, the fully closed position shall be
    /// displayed as 0%, the fully open position shall be displayed as 100%.  Intermediate
    /// positions shall be displayed as a percent of the full-open position.  For example,
    /// a stepper-motor EGR valve that moves from 0 to 128 counts shall display 0% at 0
    /// counts, 100% at 128 counts, and 50% at 64 counts.
    /// 4) Any other actuation method shall be normalised to display 0% when no EGR is
    /// commanded and 100% at the maximum commanded EGR position.
    public final static PID<Float> COMMANDED_EGR = new DefaultPID<>(
            0x2C,
            "EGR_PCT",
            "Retrieve the commanded EGR percentage",
            new HashMap<Unit, PID.Unmarshaller<Float>>()
            {
                {
                    super.put(Unit.PERCENT, new PID.Unmarshaller<Float>()
                    {
                        @Override
                        public Float invoke(@NonNull byte... bytes)
                        {
                            return (bytes[0] & 0xFF) * 100.0f / 255.0f;
                        }
                    });
                }
            }
    );
    /// todo: docs.
    public final static PID<Float> EGR_ERROR = new DefaultPID<>(
            0x2D,
            "EGR_ERR",
            "Retrieve the deviation from expected EGR flow",
            new HashMap<Unit, PID.Unmarshaller<Float>>()
            {
                {
                    super.put(Unit.PERCENT, new PID.Unmarshaller<Float>()
                    {
                        @Override
                        public Float invoke(@NonNull byte... bytes)
                        {
                            return bytes[0] * 100.0f / 128.0f;
                        }
                    });
                }
            }
    );

    public final static PID<Float> COMMANDED_EVAPORATIVE_PURGE = new DefaultPID<>(
            0x2E,
            "EVAP_PCT",
            "Retrieve the commanded evaporative purge control valve displayed as a percent",
            new HashMap<Unit, PID.Unmarshaller<Float>>()
            {
                {
                    super.put(Unit.PERCENT, new PID.Unmarshaller<Float>()
                    {
                        @Override
                        public Float invoke(@NonNull byte... bytes)
                        {
                            return (bytes[0] & 0xFF) * 100.0f / 255.0f;
                        }
                    });
                }
            }
    );

    public final static PID<Float> FUEL_LEVEL_INPUT = new DefaultPID<>(
            0x2F,
            "FLI",
            "Retrieve the nominal fuel tank liquid fill capacity as a percent of maximum",
            new HashMap<Unit, PID.Unmarshaller<Float>>()
            {
                {
                    super.put(Unit.PERCENT, new PID.Unmarshaller<Float>()
                    {
                        @Override
                        public Float invoke(@NonNull byte... bytes)
                        {
                            return (bytes[0] & 0xFF) * 100.0f / 255.0f;
                        }
                    });
                }
            }
    );

    public final static PID<Integer> WARM_UPS_SINCE_DTC_RESET = new DefaultPID<>(
            0x30,
            "WARM_UPS",
            "Retrieve the number of OBD warm-up cycles since the last DTC reset",
            new HashMap<Unit, PID.Unmarshaller<Integer>>()
            {
                {
                    super.put(Unit.ACCUMULATED_NUMBER, new PID.Unmarshaller<Integer>()
                    {
                        @Override
                        public Integer invoke(@NonNull byte... bytes)
                        {
                            return bytes[0] & 0xFF;
                        }
                    });
                }
            }
    );

    public final static PID<Integer> DISTANCE_TRAVELLED_SINCE_DTC_RESET = new DefaultPID<>(
            0x31,
            "CLR_DIST",
            "Retrieve the distance travelled since the last DTC reset",
            new HashMap<Unit, PID.Unmarshaller<Integer>>()
            {
                {
                    super.put(Unit.KILOMETERS, new PID.Unmarshaller<Integer>()
                    {
                        @Override
                        public Integer invoke(@NonNull byte... bytes)
                        {
                            return ((bytes[0] & 0xFF) << 8) | bytes[1];
                        }
                    });
                    super.put(Unit.MILES, new PID.Unmarshaller<Integer>()
                    {
                        @Override
                        public Integer invoke(@NonNull byte... bytes)
                        {
                            return (int)((((bytes[0] & 0xFF) << 8) | bytes[1]) * Constants.KILOMETERS_TO_MILES);
                        }
                    });
                }
            }
    );

    public final static PID<Float> EVAP_SYSTEM_VAPOR_PRESSURE = new DefaultPID<>(
            0x32,
            "EVAP_VP",
            "Retrieve the evaporative system vapor pressure",
            new HashMap<Unit, PID.Unmarshaller<Float>>()
            {
                {
                    super.put(Unit.PASCALS, new PID.Unmarshaller<Float>()
                    {
                        @Override
                        public Float invoke(@NonNull byte... bytes)
                        {
                            return ((bytes[0] << 8) | bytes[1]) * 0.25f;
                        }
                    });
                    super.put(Unit.INCHES_OF_WATER, new PID.Unmarshaller<Float>()
                    {
                        @Override
                        public Float invoke(@NonNull byte... bytes)
                        {
                            return (((bytes[0] << 8) | bytes[1]) * 0.25f) * Constants.PASCALS_TO_INCHES_H2O;
                        }
                    });
                }
            }
    );

    public final static PID<Float> BAROMETRIC_PRESSURE = new DefaultPID<>(
            0x33,
            "BARO",
            "Retrieve the barometric pressure utilized by the control module",
            new HashMap<Unit, PID.Unmarshaller<Float>>()
            {
                {
                    super.put(Unit.KILO_PASCALS, new PID.Unmarshaller<Float>()
                    {
                        @Override
                        public Float invoke(@NonNull byte... bytes)
                        {
                            return (float) (bytes[0] & 0xFF);
                        }
                    });
                    super.put(Unit.INCHES_OF_MERCURY, new PID.Unmarshaller<Float>()
                    {
                        @Override
                        public Float invoke(@NonNull byte... bytes)
                        {
                            return ((bytes[0] & 0xFF) * Constants.KILO_PASCAL_TO_INCHES_MERCURY);
                        }
                    });
                }
            }
    );

    /// TODO: Override toString? needs to fit in UI display..
    private final static PID.Unmarshaller<SerializablePair<Float, Float>>
            WIDE_RANGE_O2S_UNMARSHALLER_ALT
            = new PID.Unmarshaller<SerializablePair<Float, Float>>()
    {
        @Override
        public SerializablePair<Float, Float> invoke(@NonNull byte... bytes)
        {
            return new SerializablePair<>((((bytes[0] & 0xFF) << 8) | bytes[1]) * 0.0000305f,
                                          (((short) bytes[2] << 8) | bytes[3]) * 0.00390825f);
        }
    };

    public final static PID<SerializablePair<Float, Float>> DUAL_BANK_O2S11_WIDE_RANGE_ALT =
            new DefaultPID<>(
                    0x34,
                    "O2S11",
                    "",
                    new HashMap<Unit, PID.Unmarshaller<SerializablePair<Float, Float>>>()
                    {
                        {
                            super.put(Unit.INTERNAL_PLACEHOLDER, WIDE_RANGE_O2S_UNMARSHALLER_ALT);
                        }
                    }
            );

    public final static PID<SerializablePair<Float, Float>> DUAL_BANK_O2S12_WIDE_RANGE_ALT =
            new DefaultPID<>(
                    0x35,
                    "O2S12",
                    "",
                    new HashMap<Unit, PID.Unmarshaller<SerializablePair<Float, Float>>>()
                    {
                        {
                            super.put(Unit.INTERNAL_PLACEHOLDER, WIDE_RANGE_O2S_UNMARSHALLER_ALT);
                        }
                    }
            );

    public final static PID<SerializablePair<Float, Float>> DUAL_BANK_O2S13_WIDE_RANGE_ALT =
            new DefaultPID<>(
                    0x36,
                    "O2S13",
                    "",
                    new HashMap<Unit, PID.Unmarshaller<SerializablePair<Float, Float>>>()
                    {
                        {
                            super.put(Unit.INTERNAL_PLACEHOLDER, WIDE_RANGE_O2S_UNMARSHALLER_ALT);
                        }
                    }
            );

    public final static PID<SerializablePair<Float, Float>> DUAL_BANK_O2S14_WIDE_RANGE_ALT =
            new DefaultPID<>(
                    0x37,
                    "O2S14",
                    "",
                    new HashMap<Unit, PID.Unmarshaller<SerializablePair<Float, Float>>>()
                    {
                        {
                            super.put(Unit.INTERNAL_PLACEHOLDER, WIDE_RANGE_O2S_UNMARSHALLER_ALT);
                        }
                    }
            );

    public final static PID<SerializablePair<Float, Float>> DUAL_BANK_O2S21_WIDE_RANGE_ALT =
            new DefaultPID<>(
                    0x38,
                    "O2S21",
                    "",
                    new HashMap<Unit, PID.Unmarshaller<SerializablePair<Float, Float>>>()
                    {
                        {
                            super.put(Unit.INTERNAL_PLACEHOLDER, WIDE_RANGE_O2S_UNMARSHALLER_ALT);
                        }
                    }
            );

    public final static PID<SerializablePair<Float, Float>> DUAL_BANK_O2S22_WIDE_RANGE_ALT =
            new DefaultPID<>(
                    0x39,
                    "O2S22",
                    "",
                    new HashMap<Unit, PID.Unmarshaller<SerializablePair<Float, Float>>>()
                    {
                        {
                            super.put(Unit.INTERNAL_PLACEHOLDER, WIDE_RANGE_O2S_UNMARSHALLER_ALT);
                        }
                    }
            );

    public final static PID<SerializablePair<Float, Float>> DUAL_BANK_O2S23_WIDE_RANGE_ALT =
            new DefaultPID<>(
                    0x3A,
                    "O2S23",
                    "",
                    new HashMap<Unit, PID.Unmarshaller<SerializablePair<Float, Float>>>()
                    {
                        {
                            super.put(Unit.INTERNAL_PLACEHOLDER, WIDE_RANGE_O2S_UNMARSHALLER_ALT);
                        }
                    }
            );

    public final static PID<SerializablePair<Float, Float>> DUAL_BANK_O2S24_WIDE_RANGE_ALT =
            new DefaultPID<>(
                    0x3B,
                    "O2S24",
                    "",
                    new HashMap<Unit, PID.Unmarshaller<SerializablePair<Float, Float>>>()
                    {
                        {
                            super.put(Unit.INTERNAL_PLACEHOLDER, WIDE_RANGE_O2S_UNMARSHALLER_ALT);
                        }
                    }
            );


    public final static PID<SerializablePair<Float, Float>> QUAD_BANK_O2S11_WIDE_RANGE_ALT =
            new DefaultPID<>(
                    0x34,
                    "O2S11",
                    "",
                    new HashMap<Unit, PID.Unmarshaller<SerializablePair<Float, Float>>>()
                    {
                        {
                            super.put(Unit.INTERNAL_PLACEHOLDER, WIDE_RANGE_O2S_UNMARSHALLER_ALT);
                        }
                    }
            );

    public final static PID<SerializablePair<Float, Float>> QUAD_BANK_O2S12_WIDE_RANGE_ALT =
            new DefaultPID<>(
                    0x35,
                    "O2S12",
                    "",
                    new HashMap<Unit, PID.Unmarshaller<SerializablePair<Float, Float>>>()
                    {
                        {
                            super.put(Unit.INTERNAL_PLACEHOLDER, WIDE_RANGE_O2S_UNMARSHALLER_ALT);
                        }
                    }
            );

    public final static PID<SerializablePair<Float, Float>> QUAD_BANK_O2S21_WIDE_RANGE_ALT =
            new DefaultPID<>(
                    0x36,
                    "O2S21",
                    "",
                    new HashMap<Unit, PID.Unmarshaller<SerializablePair<Float, Float>>>()
                    {
                        {
                            super.put(Unit.INTERNAL_PLACEHOLDER, WIDE_RANGE_O2S_UNMARSHALLER_ALT);
                        }
                    }
            );

    public final static PID<SerializablePair<Float, Float>> QUAD_BANK_O2S22_WIDE_RANGE_ALT =
            new DefaultPID<>(
                    0x37,
                    "O2S22",
                    "",
                    new HashMap<Unit, PID.Unmarshaller<SerializablePair<Float, Float>>>()
                    {
                        {
                            super.put(Unit.INTERNAL_PLACEHOLDER, WIDE_RANGE_O2S_UNMARSHALLER_ALT);
                        }
                    }
            );

    public final static PID<SerializablePair<Float, Float>> QUAD_BANK_O2S31_WIDE_RANGE_ALT =
            new DefaultPID<>(
                    0x38,
                    "O2S31",
                    "",
                    new HashMap<Unit, PID.Unmarshaller<SerializablePair<Float, Float>>>()
                    {
                        {
                            super.put(Unit.INTERNAL_PLACEHOLDER, WIDE_RANGE_O2S_UNMARSHALLER_ALT);
                        }
                    }
            );

    public final static PID<SerializablePair<Float, Float>> QUAD_BANK_O2S32_WIDE_RANGE_ALT =
            new DefaultPID<>(
                    0x39,
                    "O2S32",
                    "",
                    new HashMap<Unit, PID.Unmarshaller<SerializablePair<Float, Float>>>()
                    {
                        {
                            super.put(Unit.INTERNAL_PLACEHOLDER, WIDE_RANGE_O2S_UNMARSHALLER_ALT);
                        }
                    }
            );

    public final static PID<SerializablePair<Float, Float>> QUAD_BANK_O2S41_WIDE_RANGE_ALT =
            new DefaultPID<>(
                    0x3A,
                    "O2S41",
                    "",
                    new HashMap<Unit, PID.Unmarshaller<SerializablePair<Float, Float>>>()
                    {
                        {
                            super.put(Unit.INTERNAL_PLACEHOLDER, WIDE_RANGE_O2S_UNMARSHALLER_ALT);
                        }
                    }
            );

    public final static PID<SerializablePair<Float, Float>> QUAD_BANK_O2S42_WIDE_RANGE_ALT =
            new DefaultPID<>(
                    0x3B,
                    "O2S42",
                    "",
                    new HashMap<Unit, PID.Unmarshaller<SerializablePair<Float, Float>>>()
                    {
                        {
                            super.put(Unit.INTERNAL_PLACEHOLDER, WIDE_RANGE_O2S_UNMARSHALLER_ALT);
                        }
                    }
            );

    private final static PID.Unmarshaller<Float> CATALYST_TEMPERATURE_CELSIUS_UNMARSHALLER =
            new PID.Unmarshaller<Float>()
            {
                @Override
                public Float invoke(@NonNull byte... bytes)
                {
                    return ((((bytes[0] & 0xFF) << 8) | bytes[1]) * 0.1f) - 40.0f;
                }
            };

    private final static PID.Unmarshaller<Float> CATALYST_TEMPERATURE_FAHRENHEIT_UNMARSHALLER =
            new PID.Unmarshaller<Float>()
            {
                @Override
                public Float invoke(@NonNull byte... bytes)
                {
                    return (((((bytes[0] & 0xFF) << 8) | bytes[1]) * 0.1f) - 40.0f) * 9.0f / 5.0f + 32.0f;
                }
            };

    public final static PID<Float> CATALYST_TEMPERATURE_BANK_1_SENSOR_1 = new DefaultPID<>(
            0x3C,
            "CATEMP11",
            "",
            new HashMap<Unit, PID.Unmarshaller<Float>>()
            {
                {
                    super.put(Unit.TEMPERATURE_CELSIUS, CATALYST_TEMPERATURE_CELSIUS_UNMARSHALLER);
                    super.put(Unit.TEMPERATURE_FAHRENHEIT,
                              CATALYST_TEMPERATURE_FAHRENHEIT_UNMARSHALLER);
                }
            }
    );

    public final static PID<Float> CATALYST_TEMPERATURE_BANK_1_SENSOR_2 = new DefaultPID<>(
            0x3D,
            "CATEMP12",
            "",
            new HashMap<Unit, PID.Unmarshaller<Float>>()
            {
                {
                    super.put(Unit.TEMPERATURE_CELSIUS, CATALYST_TEMPERATURE_CELSIUS_UNMARSHALLER);
                    super.put(Unit.TEMPERATURE_FAHRENHEIT,
                              CATALYST_TEMPERATURE_FAHRENHEIT_UNMARSHALLER);
                }
            }
    );

    public final static PID<Float> CATALYST_TEMPERATURE_BANK_2_SENSOR_1 = new DefaultPID<>(
            0x3E,
            "CATEMP21",
            "",
            new HashMap<Unit, PID.Unmarshaller<Float>>()
            {
                {
                    super.put(Unit.TEMPERATURE_CELSIUS, CATALYST_TEMPERATURE_CELSIUS_UNMARSHALLER);
                    super.put(Unit.TEMPERATURE_FAHRENHEIT,
                              CATALYST_TEMPERATURE_FAHRENHEIT_UNMARSHALLER);
                }
            }
    );

    public final static PID<Float> CATALYST_TEMPERATURE_BANK_2_SENSOR_2 = new DefaultPID<>(
            0x3F,
            "CATEMP22",
            "",
            new HashMap<Unit, PID.Unmarshaller<Float>>()
            {
                {
                    super.put(Unit.TEMPERATURE_CELSIUS, CATALYST_TEMPERATURE_CELSIUS_UNMARSHALLER);
                    super.put(Unit.TEMPERATURE_FAHRENHEIT,
                              CATALYST_TEMPERATURE_FAHRENHEIT_UNMARSHALLER);
                }
            }
    );

    public final static PID<DriveCycleMonitorStatus> MONITOR_STATUS_THIS_DRIVING_CYCLE = new DefaultPID<>(
            0x41,
            "N/A",
            "",
            new HashMap<Unit, PID.Unmarshaller<DriveCycleMonitorStatus>>()
            {
                {
                    super.put(Unit.PACKETED, new PID.Unmarshaller<DriveCycleMonitorStatus>()
                    {
                        @Override
                        public DriveCycleMonitorStatus invoke(@NonNull byte... bytes)
                        {
                            return new DriveCycleMonitorStatus(((bytes[0] & 0xFF) << 24) |
                                ((bytes[1] & 0xFF) << 16) |
                                ((bytes[2] & 0xFF) << 8) |
                                (bytes[3] & 0xFF));
                        }
                    });
                }
            }
    );

    public final static PID<Float> CONTROL_MODULE_VOLTAGE = new DefaultPID<>(
            0x42,
            "VPWR",
            "",
            new HashMap<Unit, PID.Unmarshaller<Float>>()
            {
                {
                    super.put(Unit.VOLTS, new PID.Unmarshaller<Float>()
                    {
                        @Override
                        public Float invoke(@NonNull byte... bytes)
                        {
                            return (((bytes[0] & 0xFF) << 8) |
                                    (bytes[1] & 0xFF)) * 0.001f;
                        }
                    });
                }
            }
    );

    public final static PID<Float> ABSOLUTE_LOAD_VALUE = new DefaultPID<>(
            0x43,
            "LOAD_ABS",
            "",
            new HashMap<Unit, PID.Unmarshaller<Float>>()
            {
                {
                    super.put(Unit.PERCENT, new PID.Unmarshaller<Float>()
                    {
                        @Override
                        public Float invoke(@NonNull byte... bytes)
                        {
                            return (((bytes[0] & 0xFF) << 8) |
                                    (bytes[1] & 0xFF)) * 100.0f / 255.0f;
                        }
                    });
                }
            }
    );

    public final static PID<Float> COMMANDED_EQUIVALENCE_RATIO = new DefaultPID<>(
            0x44,
            "EQ_RAT",
            "",
            new HashMap<Unit, PID.Unmarshaller<Float>>()
            {
                {
                    super.put(Unit.PERCENT, new PID.Unmarshaller<Float>()
                    {
                        @Override
                        public Float invoke(@NonNull byte... bytes)
                        {
                            return (((bytes[0] & 0xFF) << 8) |
                                    (bytes[1] & 0xFF)) * 0.0000305f;
                        }
                    });
                }
            }
    );

    public final static PID<Float> RELATIVE_THROTTLE_POSITION = new DefaultPID<>(
            0x45,
            "TP_R",
            "",
            new HashMap<Unit, PID.Unmarshaller<Float>>()
            {
                {
                    super.put(Unit.PERCENT, new PID.Unmarshaller<Float>()
                    {
                        @Override
                        public Float invoke(@NonNull byte... bytes)
                        {
                            return (bytes[0] & 0xFF) * 100.0f / 255.0f;
                        }
                    });
                }
            }
    );

    public final static PID<Integer> AMBIENT_AIR_TEMPERATURE = new DefaultPID<>(
            0x46,
            "AAT",
            "",
            new HashMap<Unit, PID.Unmarshaller<Integer>>()
            {
                {
                    super.put(Unit.TEMPERATURE_CELSIUS, new PID.Unmarshaller<Integer>()
                    {
                        @Override
                        public Integer invoke(@NonNull byte... bytes)
                        {
                            return (bytes[0] & 0xFF) - 40;
                        }
                    });
                    super.put(Unit.TEMPERATURE_FAHRENHEIT, new PID.Unmarshaller<Integer>()
                    {
                        @Override
                        public Integer invoke(@NonNull byte... bytes)
                        {
                            return (int)(((bytes[0] & 0xFF) - 40.0f) * 9.0f / 5.0f + 32.0f);
                        }
                    });
                }
            }
    );

    public final static PID<Float> ABSOLUTE_THROTTLE_POSITION_B = new DefaultPID<>(
            0x47,
            "TP_B",
            "",
            new HashMap<Unit, PID.Unmarshaller<Float>>()
            {
                {
                    super.put(Unit.PERCENT, new PID.Unmarshaller<Float>()
                    {
                        @Override
                        public Float invoke(@NonNull byte... bytes)
                        {
                            return (bytes[0] & 0xFF) * 100.0f / 255.0f;
                        }
                    });
                }
            }
    );

    public final static PID<Float> ABSOLUTE_THROTTLE_POSITION_C = new DefaultPID<>(
            0x48,
            "TP_C",
            "",
            new HashMap<Unit, PID.Unmarshaller<Float>>()
            {
                {
                    super.put(Unit.PERCENT, new PID.Unmarshaller<Float>()
                    {
                        @Override
                        public Float invoke(@NonNull byte... bytes)
                        {
                            return (bytes[0] & 0xFF) * 100.0f / 255.0f;
                        }
                    });
                }
            }
    );

    public final static PID<Float> ABSOLUTE_THROTTLE_POSITION_D = new DefaultPID<>(
            0x49,
            "TP_D",
            "",
            new HashMap<Unit, PID.Unmarshaller<Float>>()
            {
                {
                    super.put(Unit.PERCENT, new PID.Unmarshaller<Float>()
                    {
                        @Override
                        public Float invoke(@NonNull byte... bytes)
                        {
                            return (bytes[0] & 0xFF) * 100.0f / 255.0f;
                        }
                    });
                }
            }
    );

    public final static PID<Float> ABSOLUTE_THROTTLE_POSITION_E = new DefaultPID<>(
            0x4A,
            "TP_E",
            "",
            new HashMap<Unit, PID.Unmarshaller<Float>>()
            {
                {
                    super.put(Unit.PERCENT, new PID.Unmarshaller<Float>()
                    {
                        @Override
                        public Float invoke(@NonNull byte... bytes)
                        {
                            return (bytes[0] & 0xFF) * 100.0f / 255.0f;
                        }
                    });
                }
            }
    );

    public final static PID<Float> ABSOLUTE_THROTTLE_POSITION_F = new DefaultPID<>(
            0x4B,
            "TP_F",
            "",
            new HashMap<Unit, PID.Unmarshaller<Float>>()
            {
                {
                    super.put(Unit.PERCENT, new PID.Unmarshaller<Float>()
                    {
                        @Override
                        public Float invoke(@NonNull byte... bytes)
                        {
                            return (bytes[0] & 0xFF) * 100.0f / 255.0f;
                        }
                    });
                }
            }
    );

    public final static PID<Float> COMMANDED_THROTTLE_ACTUATOR_CONTROL = new DefaultPID<>(
            0x4C,
            "TAC_PCT",
            "",
            new HashMap<Unit, PID.Unmarshaller<Float>>()
            {
                {
                    super.put(Unit.PERCENT, new PID.Unmarshaller<Float>()
                    {
                        @Override
                        public Float invoke(@NonNull byte... bytes)
                        {
                            return (bytes[0] & 0xFF) * 100.0f / 255.0f;
                        }
                    });
                }
            }
    );

    public final static PID<Integer> MINUTES_RAN_BY_ENGINE_WITH_MIL_ACTIVATED = new DefaultPID<>(
            0x4D,
            "MIL_TIME",
            "",
            new HashMap<Unit, PID.Unmarshaller<Integer>>()
            {
                {
                    super.put(Unit.MINUTES, new PID.Unmarshaller<Integer>()
                    {
                        @Override
                        public Integer invoke(@NonNull byte... bytes)
                        {
                            return ((bytes[0] & 0xFF) << 8) | bytes[1];
                        }
                    });
                }
            }
    );

    public final static PID<Integer> TIME_SINCE_DTC_CLEARED = new DefaultPID<>(
            0x4E,
            "CLR_TIME",
            "",
            new HashMap<Unit, PID.Unmarshaller<Integer>>()
            {
                {
                    super.put(Unit.MINUTES, new PID.Unmarshaller<Integer>()
                    {
                        @Override
                        public Integer invoke(@NonNull byte... bytes)
                        {
                            return ((bytes[0] & 0xFF) << 8) | bytes[1];
                        }
                    });
                }
            }
    );

    /// PIDs $4F - $FF are reserved by SAE J1979
}
