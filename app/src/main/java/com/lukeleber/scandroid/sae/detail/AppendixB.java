/*
 * This file is protected under the com.lukeleber.
 * For more information visit <insert_valid_link_to_com.lukeleber._here>
 * <p/>
 * Copyright (c) Luke A. Leber <LukeLeber@gmail.com> 2014
 */

package com.lukeleber.scandroid.sae.detail;

import android.view.View;
import android.widget.TextView;

import com.lukeleber.scandroid.R;
import com.lukeleber.scandroid.sae.AuxiliaryInputStatus;
import com.lukeleber.scandroid.sae.DefaultPID;
import com.lukeleber.scandroid.sae.FuelSystemStatus;
import com.lukeleber.scandroid.sae.MonitorStatus;
import com.lukeleber.scandroid.sae.OBDSupport;
import com.lukeleber.scandroid.sae.OxygenSensor;
import com.lukeleber.scandroid.sae.PID;
import com.lukeleber.scandroid.sae.Profile;
import com.lukeleber.scandroid.sae.SecondaryAirStatus;
import com.lukeleber.scandroid.sae.j2012.DiagnosticTroubleCode;
import com.lukeleber.scandroid.util.Unit;
import com.lukeleber.util.SerializablePair;

import java.util.Collection;
import java.util.HashMap;

public class AppendixB
{
    /**
     * As defined in SAE-J1979 Appendix B:
     * <p/>
     * The following PIDs are usable in Services 1 and 2 to request either live or freeze-frame
     * information from the onboard module(s).
     * <p/>
     * Prior to using any of the following PIDs in a request, it is desirable to first check whether
     * or not the desired PID is supported by utilizing one of the PIDs defined by Appendix A
     * (above).
     */

    /// A PID that requests the current {@link MonitorStatus}
    public final static PID<MonitorStatus> MONITOR_STATUS
            = new DefaultPID<MonitorStatus>(
            0x01,
            "Monitor Status",
            "Retrieve the monitor status since the last time the DTCs wee cleared",
            new HashMap<Unit, PID.Unmarshaller<MonitorStatus>>()
            {
                {
                    super.put(Unit.INTERNAL_PLACEHOLDER, new PID.Unmarshaller<MonitorStatus>()
                    {
                        @Override
                        public MonitorStatus invoke(byte... bytes)
                        {
                            return new MonitorStatus(((bytes[0] & 0xFF) << 24) |
                                                             ((bytes[1] & 0xFF) << 16) |
                                                             ((bytes[2] & 0xFF) << 8) |
                                                             (bytes[3] & 0xFF));
                        }
                    });
                }
            },
            4
    )
    {

        final class ModelView
        {
            TextView milStatus;
            TextView dtcCount;
            TextView monitorStatus;
        }

        @Override
        public int getLayoutID()
        {
            return R.layout.monitor_status_listview_item;
        }

        @Override
        public Object createViewModel(View view)
        {
            ModelView dmv = new ModelView();
            dmv.milStatus = (TextView) view.findViewById(R.id.monitor_status_mil_status);
            dmv.dtcCount = (TextView) view.findViewById(R.id.monitor_status_dtc_count);
            dmv.monitorStatus = (TextView)view.findViewById(R.id.monitor_status_support_readiness);
            return dmv;
        }

        @Override
        public void updateViewModel(Object view, Object value)
        {
            MonitorStatus ms = (MonitorStatus)value;
            ModelView mv = (ModelView)view;
            mv.milStatus.setText(ms.isMalfunctionLampOn() ? "ON" : "OFF");
            mv.dtcCount.setText(ms.getDiagnosticTroubleCodeCount());
            mv.monitorStatus.setText(ms.getSupportReadinessString());
        }
    };
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
                                  public DiagnosticTroubleCode invoke(byte... bytes)
                                  {
                                      return new DiagnosticTroubleCode(bytes[0], bytes[1]);
                                  }
                              });
                }
            },
            2
    );

    /// A PID that requests the {@link FuelSystemStatus statuses} of all onboard fuel systems
    public final static PID<SerializablePair<FuelSystemStatus, FuelSystemStatus>> FUEL_SYSTEM_STATUS
            = new DefaultPID<SerializablePair<FuelSystemStatus, FuelSystemStatus>>(
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
                                          byte... bytes)
                                  {
                                      return new SerializablePair<>(FuelSystemStatus.forByte(
                                              bytes[0]), FuelSystemStatus.forByte(bytes[1]));
                                  }
                              });
                }
            },
            2
    )
    {
        final class ModelView
        {
            TextView fSys1;
            TextView fSys2;
        }

        @Override
        public int getLayoutID()
        {
            return R.layout.fuel_system_status_listview_item;
        }

        @Override
        public Object createViewModel(View view)
        {
            ModelView dmv = new ModelView();
            dmv.fSys1 = (TextView) view.findViewById(R.id.fuel_system_status_1);
            dmv.fSys2 = (TextView) view.findViewById(R.id.fuel_system_status_2);
            return dmv;
        }

        @Override
        public void updateViewModel(Object view, Object value)
        {
            @SuppressWarnings("unchecked")
            SerializablePair<FuelSystemStatus, FuelSystemStatus> ms =
                    (SerializablePair<FuelSystemStatus, FuelSystemStatus>)value;
            ModelView mv = (ModelView)view;
            mv.fSys1.setText(ms.first.toString());
            mv.fSys2.setText(ms.second.toString());
        }
    };
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
                        public Float invoke(byte... bytes)
                        {
                            return (bytes[0] & 0xFF) * 100.0f / 255.0f;
                        }
                    });
                }
            },
            1
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
                        public Integer invoke(byte... bytes)
                        {
                            return (bytes[0] & 0xFF) - 40;
                        }
                    });
                    super.put(Unit.TEMPERATURE_FAHRENHEIT, new PID.Unmarshaller<Integer>()
                    {
                        @Override
                        public Integer invoke(byte... bytes)
                        {
                            return (int) (((bytes[0] & 0xFF) - 40) * (9.0f / 5.0f) + 32);
                        }
                    });
                }
            },
            1
    );

    /// A PID that requests the short term fuel trim value for bank 1 (STFT1)
    public final static PID<SerializablePair<Float, Float>> SHORT_TERM_FUEL_TRIM_BANK_1_3 =
            new DefaultPID<SerializablePair<Float, Float>>(
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
                                                  byte... bytes)
                                          {
                                              return new SerializablePair<>(
                                                      ((bytes[0] & 0xFF) - 128) * 100.0f / 128.0f,
                                                      bytes.length > 1 ?
                                                              (((bytes[1] & 0xFF) - 128) * 100.0f / 128.0f) :
                                                              null);
                                          }
                                      });
                        }
                    }, 0
            )
            {
                @Override
                public int getResponseLength(Profile profile)
                {
                    return profile.isEquipped(Profile.BANK_3) ? 2 : 1;
                }
            };

    /// A PID that requests the long term fuel trim value for bank 1 (LTFT1)
    public final static PID<SerializablePair<Float, Float>> LONG_TERM_FUEL_TRIM_BANK_1_3 =
            new DefaultPID<SerializablePair<Float, Float>>(
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
                                                  byte... bytes)
                                          {
                                              return new SerializablePair<>(
                                                      ((bytes[0] & 0xFF) - 128) * 100.0f / 128.0f,
                                                      bytes.length > 1 ?
                                                              (((bytes[1] & 0xFF) - 128) * 100.0f / 128.0f) :
                                                              null);
                                          }
                                      });
                        }
                    }, 0
            )
            {
                @Override
                public int getResponseLength(Profile profile)
                {
                    return profile.isEquipped(Profile.BANK_3) ? 2 : 1;
                }
            };

    /// A PID that requests the short term fuel trim value for bank 2 (STFT2)
    public final static PID<SerializablePair<Float, Float>> SHORT_TERM_FUEL_TRIM_BANK_2_4 =
            new DefaultPID<SerializablePair<Float, Float>>(
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
                                                  byte... bytes)
                                          {
                                              return new SerializablePair<>(
                                                      ((bytes[0] & 0xFF) - 128) * 100.0f / 128.0f,
                                                      bytes.length > 1 ?
                                                              (((bytes[1] & 0xFF) - 128) * 100.0f / 128.0f) :
                                                              null);
                                          }
                                      });
                        }
                    }, 0
            )
            {
                @Override
                public int getResponseLength(Profile profile)
                {
                    return profile.isEquipped(Profile.BANK_4) ? 2 : 1;
                }
            };

    /// A PID that requests the long term fuel trim value for bank 2 (LTFT2)
    public final static PID<SerializablePair<Float, Float>> LONG_TERM_FUEL_TRIM_BANK_2_4 =
            new DefaultPID<SerializablePair<Float, Float>>(
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
                                                  byte... bytes)
                                          {
                                              return new SerializablePair<>(
                                                      ((bytes[0] & 0xFF) - 128) * 100.0f / 128.0f,
                                                      bytes.length > 1 ?
                                                              (((bytes[1] & 0xFF) - 128) * 100.0f / 128.0f) :
                                                              null);
                                          }
                                      });
                        }
                    }, 0
            )
            {
                @Override
                public int getResponseLength(Profile profile)
                {
                    return profile.isEquipped(Profile.BANK_4) ? 2 : 1;
                }
            };

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
                                public Integer invoke(byte... bytes)
                                {
                                    return (bytes[0] & 0xFF) * 3;
                                }
                            });
                        }
                    },
                    1
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
                                public Integer invoke(byte... bytes)
                                {
                                    return bytes[0] & 0xFF;
                                }
                            });
                        }
                    },
                    1
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
                                public Float invoke(byte... bytes)
                                {
                                    return (((bytes[0] & 0xFF) << 8) | (bytes[1] & 0xFF)) / 4.0f;
                                }
                            });
                        }
                    },
                    2
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
                                public Integer invoke(byte... bytes)
                                {
                                    return bytes[0] & 0xFF;
                                }
                            });
                            /// TODO: MPH Unmarshaller
                        }
                    },
                    1
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
                                public Float invoke(byte... bytes)
                                {
                                    return ((bytes[0] & 0xFF) - 128) / 2.0f;
                                }
                            });
                            /// TODO: Radian Unmarshaller
                        }
                    },
                    1
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
                                public Integer invoke(byte... bytes)
                                {
                                    return (int) (((bytes[0] & 0xFF) - 40) * (9.0f / 5.0f) + 32);
                                }
                            });
                            super.put(Unit.TEMPERATURE_CELSIUS, new PID.Unmarshaller<Integer>()
                            {
                                @Override
                                public Integer invoke(byte... bytes)
                                {
                                    return (bytes[0] & 0xFF) - 40;
                                }
                            });
                        }
                    },
                    1
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
                                public Float invoke(byte... bytes)
                                {
                                    return (((bytes[0] & 0xFF) << 8) | (bytes[1] & 0xFF)) / 100.0f;
                                }
                            });
                        }
                    },
                    2
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
                                public Float invoke(byte... bytes)
                                {
                                    return ((bytes[0] & 0xFF) * 100.0f) / 255.0f;
                                }
                            });
                            // TODO: Voltage Conversion? assume 0-5 volts and interpolate?
                        }
                    },
                    1
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
                                          public SecondaryAirStatus invoke(byte... bytes)
                                          {
                                              return SecondaryAirStatus.forByte(bytes[0]);
                                          }
                                      });
                        }
                    },
                    1
            );

    /// A PID that requests the location of all onboard conventional oxygen sensors
    /// NOTE: SAE_J1979 specifies that if this PID is supported, then PID 1D must be unsupported
    public final static PID<Collection<OxygenSensor>> DUAL_BANK_OXYGEN_SENSOR_LOCATIONS =
            new DefaultPID<>(
                    0x13,
                    "O2SLOC",
                    "Retrieve all equipped oxygen sensors",
                    new HashMap<Unit, PID.Unmarshaller<Collection<OxygenSensor>>>()
                    {
                        {
                            super.put(Unit.INTERNAL_PLACEHOLDER,
                                      new PID.Unmarshaller<Collection<OxygenSensor>>()
                                      {
                                          @Override
                                          public Collection<OxygenSensor> invoke(byte... bytes)
                                          {
                                              return OxygenSensor.DualBank.forByte(bytes[0]);
                                          }
                                      });
                        }
                    },
                    1
            );

    /// A voltage unmarshaller for conventional oxygen sensors.
    private final static PID.Unmarshaller<Float> O2S_VOLTAGE = new PID.Unmarshaller<Float>()
    {
        @Override
        public Float invoke(byte... bytes)
        {
            return (bytes[0] & 0xFF) * 0.005f;
        }
    };

    /// A fuel trim % unmarshaller for conventional oxygen sensors
    private final static PID.Unmarshaller<Float> O2S_FUEL_TRIM = new PID.Unmarshaller<Float>()
    {
        @Override
        public Float invoke(byte... bytes)
        {
            return 0.78125f * (bytes[0] & 0xFF) - 100.0f;
        }
    };

    /// A PID that requests the reading of O2S11 on a dual-bank system
    /// NOTE: SAE-J1979 specifies that this PID cannot be supported if PID $24 is
    public final static PID<Float> DUAL_BANK_O2S11_CONVENTIONAL = new DefaultPID<>(
            0x14,
            "O2S11",
            "Retrieve O2S11 value",
            new HashMap<Unit, PID.Unmarshaller<Float>>()
            {
                {
                    super.put(Unit.VOLTS, O2S_VOLTAGE);
                    super.put(Unit.PERCENT, O2S_FUEL_TRIM);
                }
            },
            1
    );

    /// A PID that requests the reading of O2S11 on a quad-bank system
    /// NOTE: SAE-J1979 specifies that this PID cannot be supported if PID $24 is
    public final static PID<Float> QUAD_BANK_O2S11_CONVENTIONAL = new DefaultPID<>(
            0x14,
            "O2S11",
            "Retrieve O2S11 value",
            new HashMap<Unit, PID.Unmarshaller<Float>>()
            {
                {
                    super.put(Unit.VOLTS, O2S_VOLTAGE);
                    super.put(Unit.PERCENT, O2S_FUEL_TRIM);
                }
            },
            1
    );

    /// A PID that requests the reading of O2S12 on a dual-bank system
    /// NOTE: SAE-J1979 specifies that this PID cannot be supported if PID $25 is
    public final static PID<Float> DUAL_BANK_O2S12_CONVENTIONAL = new DefaultPID<>(
            0x15,
            "O2S12",
            "Retrieve O2S12 value",
            new HashMap<Unit, PID.Unmarshaller<Float>>()
            {
                {
                    super.put(Unit.VOLTS, O2S_VOLTAGE);
                    super.put(Unit.PERCENT, O2S_FUEL_TRIM);
                }
            },
            1
    );
    /// A PID that requests the reading of O2S12 on a quad-bank system
    /// NOTE: SAE-J1979 specifies that this PID cannot be supported if PID $25 is
    public final static PID<Float> QUAD_BANK_O2S12_CONVENTIONAL = new DefaultPID<>(
            0x15,
            "O2S12",
            "Retrieve O2S12 value",
            new HashMap<Unit, PID.Unmarshaller<Float>>()
            {
                {
                    super.put(Unit.VOLTS, O2S_VOLTAGE);
                    super.put(Unit.PERCENT, O2S_FUEL_TRIM);
                }
            },
            1
    );

    /// A PID that requests the reading of O2S13 on a dual-bank system
    /// NOTE: SAE-J1979 specifies that this PID cannot be supported if PID $26 is
    public final static PID<Float> DUAL_BANK_O2S13_CONVENTIONAL = new DefaultPID<>(
            0x16,
            "O2S13",
            "Retrieve O2S13 value",
            new HashMap<Unit, PID.Unmarshaller<Float>>()
            {
                {
                    super.put(Unit.VOLTS, O2S_VOLTAGE);
                    super.put(Unit.PERCENT, O2S_FUEL_TRIM);
                }
            },
            1
    );

    /// A PID that requests the reading of O2S21 on a quad-bank system
    /// NOTE: SAE-J1979 specifies that this PID cannot be supported if PID $28 is
    public final static PID<Float> QUAD_BANK_O2S21_CONVENTIONAL = new DefaultPID<>(
            0x16,
            "O2S21",
            "Retrieve O2S21 value",
            new HashMap<Unit, PID.Unmarshaller<Float>>()
            {
                {
                    super.put(Unit.VOLTS, O2S_VOLTAGE);
                    super.put(Unit.PERCENT, O2S_FUEL_TRIM);
                }
            },
            1
    );

    /// A PID that requests the reading of O2S14 on a dual-bank system
    /// NOTE: SAE-J1979 specifies that this PID cannot be supported if PID $27 is
    public final static PID<Float> DUAL_BANK_O2S14_CONVENTIONAL = new DefaultPID<>(
            0x17,
            "O2S14",
            "Retrieve O2S14 value",
            new HashMap<Unit, PID.Unmarshaller<Float>>()
            {
                {
                    super.put(Unit.VOLTS, O2S_VOLTAGE);
                    super.put(Unit.PERCENT, O2S_FUEL_TRIM);
                }
            },
            1
    );

    /// A PID that requests the reading of O2S22 on a quad-bank system
    /// NOTE: SAE-J1979 specifies that this PID cannot be supported if PID $27 is
    public final static PID<Float> QUAD_BANK_O2S22_CONVENTIONAL = new DefaultPID<>(
            0x17,
            "O2S22",
            "Retrieve O2S22 value",
            new HashMap<Unit, PID.Unmarshaller<Float>>()
            {
                {
                    super.put(Unit.VOLTS, O2S_VOLTAGE);
                    super.put(Unit.PERCENT, O2S_FUEL_TRIM);
                }
            },
            1
    );

    /// A PID that requests the reading of O2S21 on a dual-bank system
    /// NOTE: SAE-J1979 specifies that this PID cannot be supported if PID $28 is
    public final static PID<Float> DUAL_BANK_O2S21_CONVENTIONAL = new DefaultPID<>(
            0x18,
            "O2S21",
            "Retrieve O2S21 value",
            new HashMap<Unit, PID.Unmarshaller<Float>>()
            {
                {
                    super.put(Unit.VOLTS, O2S_VOLTAGE);
                    super.put(Unit.PERCENT, O2S_FUEL_TRIM);
                }
            },
            1
    );

    /// A PID that requests the reading of O2S31 on a quad-bank system
    /// NOTE: SAE-J1979 specifies that this PID cannot be supported if PID $28 is
    public final static PID<Float> QUAD_BANK_O2S31_CONVENTIONAL = new DefaultPID<>(
            0x18,
            "O2S31",
            "Retrieve O2S31 value",
            new HashMap<Unit, PID.Unmarshaller<Float>>()
            {
                {
                    super.put(Unit.VOLTS, O2S_VOLTAGE);
                    super.put(Unit.PERCENT, O2S_FUEL_TRIM);
                }
            },
            1
    );

    /// A PID that requests the reading of O2S14 on a dual-bank system
    /// NOTE: SAE-J1979 specifies that this PID cannot be supported if PID $29 is
    public final static PID<Float> DUAL_BANK_O2S22_CONVENTIONAL = new DefaultPID<>(
            0x19,
            "O2S22",
            "Retrieve O2S22 value",
            new HashMap<Unit, PID.Unmarshaller<Float>>()
            {
                {
                    super.put(Unit.VOLTS, O2S_VOLTAGE);
                    super.put(Unit.PERCENT, O2S_FUEL_TRIM);
                }
            },
            1
    );

    /// A PID that requests the reading of O2S32 on a quad-bank system
    /// NOTE: SAE-J1979 specifies that this PID cannot be supported if PID $29 is
    public final static PID<Float> QUAD_BANK_O2S32_CONVENTIONAL = new DefaultPID<>(
            0x19,
            "O2S32",
            "Retrieve O2S32 value",
            new HashMap<Unit, PID.Unmarshaller<Float>>()
            {
                {
                    super.put(Unit.VOLTS, O2S_VOLTAGE);
                    super.put(Unit.PERCENT, O2S_FUEL_TRIM);
                }
            },
            1
    );

    /// A PID that requests the reading of O2S23 on a dual-bank system
    /// NOTE: SAE-J1979 specifies that this PID cannot be supported if PID $2A is
    public final static PID<Float> DUAL_BANK_O2S23_CONVENTIONAL = new DefaultPID<>(
            0x1A,
            "O2S23",
            "Retrieve O2S23 value",
            new HashMap<Unit, PID.Unmarshaller<Float>>()
            {
                {
                    super.put(Unit.VOLTS, O2S_VOLTAGE);
                    super.put(Unit.PERCENT, O2S_FUEL_TRIM);
                }
            },
            1
    );

    /// A PID that requests the reading of O2S41 on a quad-bank system
    /// NOTE: SAE-J1979 specifies that this PID cannot be supported if PID $2A is
    public final static PID<Float> QUAD_BANK_O2S41_CONVENTIONAL = new DefaultPID<>(
            0x1A,
            "O2S41",
            "Retrieve O2S41 value",
            new HashMap<Unit, PID.Unmarshaller<Float>>()
            {
                {
                    super.put(Unit.VOLTS, O2S_VOLTAGE);
                    super.put(Unit.PERCENT, O2S_FUEL_TRIM);
                }
            },
            1
    );

    /// A PID that requests the reading of O2S24 on a dual-bank system
    /// NOTE: SAE-J1979 specifies that this PID cannot be supported if PID $2B is
    public final static PID<Float> DUAL_BANK_O2S24_CONVENTIONAL = new DefaultPID<>(
            0x1B,
            "O2S24",
            "Retrieve O2S24 value",
            new HashMap<Unit, PID.Unmarshaller<Float>>()
            {
                {
                    super.put(Unit.VOLTS, O2S_VOLTAGE);
                    super.put(Unit.PERCENT, O2S_FUEL_TRIM);
                }
            },
            1
    );

    /// A PID that requests the reading of O2S42 on a quad-bank system
    /// NOTE: SAE-J1979 specifies that this PID cannot be supported if PID $2B is
    public final static PID<Float> QUAD_BANK_O2S42_CONVENTIONAL = new DefaultPID<>(
            0x1B,
            "O2S42",
            "Retrieve O2S42 value",
            new HashMap<Unit, PID.Unmarshaller<Float>>()
            {
                {
                    super.put(Unit.VOLTS, O2S_VOLTAGE);
                    super.put(Unit.PERCENT, O2S_FUEL_TRIM);
                }
            },
            1
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
                        public OBDSupport invoke(byte... bytes)
                        {
                            return OBDSupport.forByte(bytes[0]);
                        }
                    });
                }
            },
            1
    );

    /// A PID that requests the location of all onboard wide range oxygen sensors
    /// NOTE: SAE_J1979 specifies that if this PID is supported, then PID 13 must be unsupported
    public final static PID<Collection<OxygenSensor>> QUAD_BANK_OXYGEN_SENSOR_LOCATIONS =
            new DefaultPID<>(
                    0x1D,
                    "O2SLOC",
                    "Retrieve all equipped oxygen sensors",
                    new HashMap<Unit, PID.Unmarshaller<Collection<OxygenSensor>>>()
                    {
                        {
                            super.put(Unit.INTERNAL_PLACEHOLDER,
                                      new PID.Unmarshaller<Collection<OxygenSensor>>()
                                      {
                                          @Override
                                          public Collection<OxygenSensor> invoke(byte... bytes)
                                          {
                                              return OxygenSensor.QuadBank.forByte(bytes[0]);
                                          }
                                      });
                        }
                    },
                    1
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
                        public AuxiliaryInputStatus invoke(byte... bytes)
                        {
                            return AuxiliaryInputStatus.forByte(bytes[0]);
                        }
                    });
                }
            },
            1
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
                        public Integer invoke(byte... bytes)
                        {
                            return ((bytes[0] & 0xFF) << 8) | bytes[0];
                        }
                    });
                }
            },
            2
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
                        public Integer invoke(byte... bytes)
                        {
                            return ((bytes[0] & 0xFF) << 8) | bytes[1];
                        }
                    });
                    // TODO: Miles Conversion
                }
            },
            2
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
                                public Float invoke(byte... bytes)
                                {
                                    return (((bytes[0] & 0xFF) << 8) | bytes[1]) * 0.079F;
                                }
                            });
                        }
                    },
                    2
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
                        public Integer invoke(byte... bytes)
                        {
                            return (((bytes[0] & 0xFF) << 8) | bytes[1]) * 10;
                        }
                    });
                    super.put(Unit.PSI, new PID.Unmarshaller<Integer>()
                    {
                        @Override
                        public Integer invoke(byte... bytes)
                        {
                            return (int) ((((bytes[0] & 0xFF) << 8) | bytes[1]) * 1.450377f);
                        }
                    });
                }
            },
            2
    );

    private final static PID.Unmarshaller<SerializablePair<Float, Float>>
            WIDE_RANGE_O2S_UNMARSHALLER = new PID.Unmarshaller<SerializablePair<Float, Float>>()
    {
        @Override
        public SerializablePair<Float, Float> invoke(byte... bytes)
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
                    },
                    1
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
                    },
                    1
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
                    },
                    1
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
                    },
                    1
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
                    },
                    1
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
                    },
                    1
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
                    },
                    1
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
                    },
                    1
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
                    },
                    1
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
                    },
                    1
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
                    },
                    1
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
                    },
                    1
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
                    },
                    1
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
                    },
                    1
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
                    },
                    1
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
                    },
                    1
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
                        public Float invoke(byte... bytes)
                        {
                            return (bytes[0] & 0xFF) * 100.0f / 255.0f;
                        }
                    });
                }
            },
            1
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
                        public Float invoke(byte... bytes)
                        {
                            return bytes[0] * 100.0f / 128.0f;
                        }
                    });
                }
            },
            1
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
                        public Float invoke(byte... bytes)
                        {
                            return (bytes[0] & 0xFF) * 100.0f / 255.0f;
                        }
                    });
                }
            },
            1
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
                        public Float invoke(byte... bytes)
                        {
                            return (bytes[0] & 0xFF) * 100.0f / 255.0f;
                        }
                    });
                }
            },
            1
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
                        public Integer invoke(byte... bytes)
                        {
                            return bytes[0] & 0xFF;
                        }
                    });
                }
            },
            1
    );

    public final static PID<Integer> DISTANCE_TRAVELLED_SINCE_DTC_RESET = new DefaultPID<>(
            0x31,
            "CLR_DIST",
            "Retrieve the distance travelled since the last DTC reset",
            new HashMap<Unit, PID.Unmarshaller<Integer>>()
            {
                {
                    super.put(Unit.ACCUMULATED_NUMBER, new PID.Unmarshaller<Integer>()
                    {
                        @Override
                        public Integer invoke(byte... bytes)
                        {
                            return ((bytes[0] & 0xFF) << 8) | bytes[1];
                        }
                    });
                }
            },
            2
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
                        public Float invoke(byte... bytes)
                        {
                            return ((bytes[0] << 8) | bytes[1]) * 0.25f;
                        }
                    });
                    // TODO: in H2O
                }
            },
            2
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
                        public Float invoke(byte... bytes)
                        {
                            return (float) (bytes[0] & 0xFF);
                        }
                    });
                    super.put(Unit.INCHES_OF_MERCURY, new PID.Unmarshaller<Float>()
                    {
                        @Override
                        public Float invoke(byte... bytes)
                        {
                            // TODO: Implement conversion..
                            return null;
                        }
                    });
                }
            },
            1
    );

    /// TODO: Override toString? needs to fit in UI display..
    private final static PID.Unmarshaller<SerializablePair<Float, Float>>
            WIDE_RANGE_O2S_UNMARSHALLER_ALT
            = new PID.Unmarshaller<SerializablePair<Float, Float>>()
    {
        @Override
        public SerializablePair<Float, Float> invoke(byte... bytes)
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
                    },
                    4
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
                    },
                    4
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
                    },
                    4
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
                    },
                    4
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
                    },
                    4
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
                    },
                    4
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
                    },
                    4
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
                    },
                    4
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
                    },
                    4
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
                    },
                    4
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
                    },
                    4
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
                    },
                    4
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
                    },
                    4
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
                    },
                    4
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
                    },
                    4
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
                    },
                    4
            );

    private final static PID.Unmarshaller<Float> CATALYST_TEMPERATURE_CELSIUS_UNMARSHALLER =
            new PID.Unmarshaller<Float>()
            {
                @Override
                public Float invoke(byte... bytes)
                {
                    return ((((bytes[0] & 0xFF) << 8) | bytes[1]) * 0.1f) - 40.0f;
                }
            };

    private final static PID.Unmarshaller<Float> CATALYST_TEMPERATURE_FAHRENHEIT_UNMARSHALLER =
            new PID.Unmarshaller<Float>()
            {
                @Override
                public Float invoke(byte... bytes)
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
            },
            2
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
            },
            2
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
            },
            2
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
            },
            2
    );

}
