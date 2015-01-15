// This file is protected under the KILLGPL.
// For more information, visit http://www.lukeleber.github.io/KILLGPL
//
// Copyright (c) Luke Leber <LukeLeber@gmail.com>
//
// "This software is provided freely under a single condition.  You can't force anyone
// that uses this software to release any source code.  It is their decision alone.
// Other than that, anything goes.
//
// PS. If you like this work, please hire me!  I'm uneducated (no CS degree)
// and as such its borderline impossible to get into the industry.  My current
// career is absolute hell and I am trapped in it.  I leave this message here
// in the vain hope that someone, somewhere might read this and give me the
// opportunity to join their association as a programmer."
//
// Cheers,
// Luke

package com.lukeleber.scandroid.sae;

import java.util.Arrays;
import java.util.List;

import static com.lukeleber.scandroid.sae.detail.AppendixB.ABSOLUTE_THROTTLE_POSITION;
import static com.lukeleber.scandroid.sae.detail.AppendixB.AUXILIARY_INPUT_STATUS;
import static com.lukeleber.scandroid.sae.detail.AppendixB.BAROMETRIC_PRESSURE;
import static com.lukeleber.scandroid.sae.detail.AppendixB.CALCULATED_ENGINE_LOAD;
import static com.lukeleber.scandroid.sae.detail.AppendixB.CATALYST_TEMPERATURE_BANK_1_SENSOR_1;
import static com.lukeleber.scandroid.sae.detail.AppendixB.CATALYST_TEMPERATURE_BANK_1_SENSOR_2;
import static com.lukeleber.scandroid.sae.detail.AppendixB.CATALYST_TEMPERATURE_BANK_2_SENSOR_1;
import static com.lukeleber.scandroid.sae.detail.AppendixB.CATALYST_TEMPERATURE_BANK_2_SENSOR_2;
import static com.lukeleber.scandroid.sae.detail.AppendixB.COMMANDED_EGR;
import static com.lukeleber.scandroid.sae.detail.AppendixB.COMMANDED_EVAPORATIVE_PURGE;
import static com.lukeleber.scandroid.sae.detail.AppendixB.DISTANCE_TRAVELLED_SINCE_DTC_RESET;
import static com.lukeleber.scandroid.sae.detail.AppendixB.DISTANCE_TRAVELLED_WHILE_MIL_IS_ACTIVATED;
import static com.lukeleber.scandroid.sae.detail.AppendixB.DUAL_BANK_O2S11_CONVENTIONAL;
import static com.lukeleber.scandroid.sae.detail.AppendixB.DUAL_BANK_O2S11_WIDE_RANGE;
import static com.lukeleber.scandroid.sae.detail.AppendixB.DUAL_BANK_O2S11_WIDE_RANGE_ALT;
import static com.lukeleber.scandroid.sae.detail.AppendixB.DUAL_BANK_O2S12_CONVENTIONAL;
import static com.lukeleber.scandroid.sae.detail.AppendixB.DUAL_BANK_O2S12_WIDE_RANGE;
import static com.lukeleber.scandroid.sae.detail.AppendixB.DUAL_BANK_O2S12_WIDE_RANGE_ALT;
import static com.lukeleber.scandroid.sae.detail.AppendixB.DUAL_BANK_O2S13_CONVENTIONAL;
import static com.lukeleber.scandroid.sae.detail.AppendixB.DUAL_BANK_O2S13_WIDE_RANGE;
import static com.lukeleber.scandroid.sae.detail.AppendixB.DUAL_BANK_O2S13_WIDE_RANGE_ALT;
import static com.lukeleber.scandroid.sae.detail.AppendixB.DUAL_BANK_O2S14_CONVENTIONAL;
import static com.lukeleber.scandroid.sae.detail.AppendixB.DUAL_BANK_O2S14_WIDE_RANGE;
import static com.lukeleber.scandroid.sae.detail.AppendixB.DUAL_BANK_O2S14_WIDE_RANGE_ALT;
import static com.lukeleber.scandroid.sae.detail.AppendixB.DUAL_BANK_O2S21_CONVENTIONAL;
import static com.lukeleber.scandroid.sae.detail.AppendixB.DUAL_BANK_O2S21_WIDE_RANGE;
import static com.lukeleber.scandroid.sae.detail.AppendixB.DUAL_BANK_O2S21_WIDE_RANGE_ALT;
import static com.lukeleber.scandroid.sae.detail.AppendixB.DUAL_BANK_O2S22_CONVENTIONAL;
import static com.lukeleber.scandroid.sae.detail.AppendixB.DUAL_BANK_O2S22_WIDE_RANGE;
import static com.lukeleber.scandroid.sae.detail.AppendixB.DUAL_BANK_O2S22_WIDE_RANGE_ALT;
import static com.lukeleber.scandroid.sae.detail.AppendixB.DUAL_BANK_O2S23_CONVENTIONAL;
import static com.lukeleber.scandroid.sae.detail.AppendixB.DUAL_BANK_O2S23_WIDE_RANGE;
import static com.lukeleber.scandroid.sae.detail.AppendixB.DUAL_BANK_O2S23_WIDE_RANGE_ALT;
import static com.lukeleber.scandroid.sae.detail.AppendixB.DUAL_BANK_O2S24_CONVENTIONAL;
import static com.lukeleber.scandroid.sae.detail.AppendixB.DUAL_BANK_O2S24_WIDE_RANGE;
import static com.lukeleber.scandroid.sae.detail.AppendixB.DUAL_BANK_O2S24_WIDE_RANGE_ALT;
import static com.lukeleber.scandroid.sae.detail.AppendixB.DUAL_BANK_OXYGEN_SENSOR_LOCATIONS;
import static com.lukeleber.scandroid.sae.detail.AppendixB.EGR_ERROR;
import static com.lukeleber.scandroid.sae.detail.AppendixB.ENGINE_COOLANT_TEMPERATURE;
import static com.lukeleber.scandroid.sae.detail.AppendixB.ENGINE_SPEED;
import static com.lukeleber.scandroid.sae.detail.AppendixB.EVAP_SYSTEM_VAPOR_PRESSURE;
import static com.lukeleber.scandroid.sae.detail.AppendixB.FREEZE_FRAME_DTC;
import static com.lukeleber.scandroid.sae.detail.AppendixB.FUEL_LEVEL_INPUT;
import static com.lukeleber.scandroid.sae.detail.AppendixB.FUEL_RAIL_PRESSURE_NARROW_RANGE_ATMOSPHEREIC_REFERENCE;
import static com.lukeleber.scandroid.sae.detail.AppendixB.FUEL_RAIL_PRESSURE_RELATIVE_TO_MANIFOLD_VACUUM;
import static com.lukeleber.scandroid.sae.detail.AppendixB.FUEL_RAIL_PRESSURE_WIDE_RANGE;
import static com.lukeleber.scandroid.sae.detail.AppendixB.FUEL_SYSTEM_STATUS;
import static com.lukeleber.scandroid.sae.detail.AppendixB.INTAKE_AIR_TEMPERATURE;
import static com.lukeleber.scandroid.sae.detail.AppendixB.INTAKE_MANIFOLD_ABSOLUTE_PRESSURE;
import static com.lukeleber.scandroid.sae.detail.AppendixB.LONG_TERM_FUEL_TRIM_BANK_1_3;
import static com.lukeleber.scandroid.sae.detail.AppendixB.LONG_TERM_FUEL_TRIM_BANK_2_4;
import static com.lukeleber.scandroid.sae.detail.AppendixB.MASS_AIRFLOW_RATE;
import static com.lukeleber.scandroid.sae.detail.AppendixB.MONITOR_STATUS;
import static com.lukeleber.scandroid.sae.detail.AppendixB.OBD_SUPPORT;
import static com.lukeleber.scandroid.sae.detail.AppendixB.QUAD_BANK_O2S11_CONVENTIONAL;
import static com.lukeleber.scandroid.sae.detail.AppendixB.QUAD_BANK_O2S11_WIDE_RANGE;
import static com.lukeleber.scandroid.sae.detail.AppendixB.QUAD_BANK_O2S11_WIDE_RANGE_ALT;
import static com.lukeleber.scandroid.sae.detail.AppendixB.QUAD_BANK_O2S12_CONVENTIONAL;
import static com.lukeleber.scandroid.sae.detail.AppendixB.QUAD_BANK_O2S12_WIDE_RANGE;
import static com.lukeleber.scandroid.sae.detail.AppendixB.QUAD_BANK_O2S12_WIDE_RANGE_ALT;
import static com.lukeleber.scandroid.sae.detail.AppendixB.QUAD_BANK_O2S21_CONVENTIONAL;
import static com.lukeleber.scandroid.sae.detail.AppendixB.QUAD_BANK_O2S21_WIDE_RANGE;
import static com.lukeleber.scandroid.sae.detail.AppendixB.QUAD_BANK_O2S21_WIDE_RANGE_ALT;
import static com.lukeleber.scandroid.sae.detail.AppendixB.QUAD_BANK_O2S22_CONVENTIONAL;
import static com.lukeleber.scandroid.sae.detail.AppendixB.QUAD_BANK_O2S22_WIDE_RANGE;
import static com.lukeleber.scandroid.sae.detail.AppendixB.QUAD_BANK_O2S22_WIDE_RANGE_ALT;
import static com.lukeleber.scandroid.sae.detail.AppendixB.QUAD_BANK_O2S31_CONVENTIONAL;
import static com.lukeleber.scandroid.sae.detail.AppendixB.QUAD_BANK_O2S31_WIDE_RANGE;
import static com.lukeleber.scandroid.sae.detail.AppendixB.QUAD_BANK_O2S31_WIDE_RANGE_ALT;
import static com.lukeleber.scandroid.sae.detail.AppendixB.QUAD_BANK_O2S32_CONVENTIONAL;
import static com.lukeleber.scandroid.sae.detail.AppendixB.QUAD_BANK_O2S32_WIDE_RANGE;
import static com.lukeleber.scandroid.sae.detail.AppendixB.QUAD_BANK_O2S32_WIDE_RANGE_ALT;
import static com.lukeleber.scandroid.sae.detail.AppendixB.QUAD_BANK_O2S41_CONVENTIONAL;
import static com.lukeleber.scandroid.sae.detail.AppendixB.QUAD_BANK_O2S41_WIDE_RANGE;
import static com.lukeleber.scandroid.sae.detail.AppendixB.QUAD_BANK_O2S41_WIDE_RANGE_ALT;
import static com.lukeleber.scandroid.sae.detail.AppendixB.QUAD_BANK_O2S42_CONVENTIONAL;
import static com.lukeleber.scandroid.sae.detail.AppendixB.QUAD_BANK_O2S42_WIDE_RANGE;
import static com.lukeleber.scandroid.sae.detail.AppendixB.QUAD_BANK_O2S42_WIDE_RANGE_ALT;
import static com.lukeleber.scandroid.sae.detail.AppendixB.QUAD_BANK_OXYGEN_SENSOR_LOCATIONS;
import static com.lukeleber.scandroid.sae.detail.AppendixB.SECONDARY_AIR_STATUS;
import static com.lukeleber.scandroid.sae.detail.AppendixB.SHORT_TERM_FUEL_TRIM_BANK_1_3;
import static com.lukeleber.scandroid.sae.detail.AppendixB.SHORT_TERM_FUEL_TRIM_BANK_2_4;
import static com.lukeleber.scandroid.sae.detail.AppendixB.TIME_SINCE_ENGINE_START;
import static com.lukeleber.scandroid.sae.detail.AppendixB.TIMING_ADVANCE;
import static com.lukeleber.scandroid.sae.detail.AppendixB.VEHICLE_SPEED;
import static com.lukeleber.scandroid.sae.detail.AppendixB.WARM_UPS_SINCE_DTC_RESET;

@SuppressWarnings("unused")
public class SAE_J1979
{


    public final static List<PID<?>> SAE_J1979_STATIC_PIDS
            = Arrays.asList(
            MONITOR_STATUS,
            FREEZE_FRAME_DTC,
            FUEL_SYSTEM_STATUS,
            CALCULATED_ENGINE_LOAD,
            ENGINE_COOLANT_TEMPERATURE,
            SHORT_TERM_FUEL_TRIM_BANK_1_3,
            LONG_TERM_FUEL_TRIM_BANK_1_3,
            SHORT_TERM_FUEL_TRIM_BANK_2_4,
            LONG_TERM_FUEL_TRIM_BANK_2_4,
            FUEL_RAIL_PRESSURE_NARROW_RANGE_ATMOSPHEREIC_REFERENCE,
            INTAKE_MANIFOLD_ABSOLUTE_PRESSURE,
            ENGINE_SPEED,
            VEHICLE_SPEED,
            TIMING_ADVANCE,
            INTAKE_AIR_TEMPERATURE,
            MASS_AIRFLOW_RATE,
            ABSOLUTE_THROTTLE_POSITION,
            SECONDARY_AIR_STATUS,
            DUAL_BANK_OXYGEN_SENSOR_LOCATIONS,
            DUAL_BANK_O2S11_CONVENTIONAL,
            QUAD_BANK_O2S11_CONVENTIONAL,
            DUAL_BANK_O2S12_CONVENTIONAL,
            QUAD_BANK_O2S12_CONVENTIONAL,
            DUAL_BANK_O2S13_CONVENTIONAL,
            QUAD_BANK_O2S21_CONVENTIONAL,
            DUAL_BANK_O2S14_CONVENTIONAL,
            QUAD_BANK_O2S22_CONVENTIONAL,
            DUAL_BANK_O2S21_CONVENTIONAL,
            QUAD_BANK_O2S31_CONVENTIONAL,
            DUAL_BANK_O2S22_CONVENTIONAL,
            QUAD_BANK_O2S32_CONVENTIONAL,
            DUAL_BANK_O2S23_CONVENTIONAL,
            QUAD_BANK_O2S41_CONVENTIONAL,
            DUAL_BANK_O2S24_CONVENTIONAL,
            QUAD_BANK_O2S42_CONVENTIONAL,
            OBD_SUPPORT,
            QUAD_BANK_OXYGEN_SENSOR_LOCATIONS,
            AUXILIARY_INPUT_STATUS,
            TIME_SINCE_ENGINE_START,
            DISTANCE_TRAVELLED_WHILE_MIL_IS_ACTIVATED,
            FUEL_RAIL_PRESSURE_RELATIVE_TO_MANIFOLD_VACUUM,
            FUEL_RAIL_PRESSURE_WIDE_RANGE,
            DUAL_BANK_O2S11_WIDE_RANGE,
            QUAD_BANK_O2S11_WIDE_RANGE,
            DUAL_BANK_O2S12_WIDE_RANGE,
            QUAD_BANK_O2S12_WIDE_RANGE,
            DUAL_BANK_O2S13_WIDE_RANGE,
            QUAD_BANK_O2S21_WIDE_RANGE,
            DUAL_BANK_O2S14_WIDE_RANGE,
            QUAD_BANK_O2S22_WIDE_RANGE,
            DUAL_BANK_O2S21_WIDE_RANGE,
            QUAD_BANK_O2S31_WIDE_RANGE,
            DUAL_BANK_O2S22_WIDE_RANGE,
            QUAD_BANK_O2S32_WIDE_RANGE,
            DUAL_BANK_O2S23_WIDE_RANGE,
            QUAD_BANK_O2S41_WIDE_RANGE,
            DUAL_BANK_O2S24_WIDE_RANGE,
            QUAD_BANK_O2S42_WIDE_RANGE,
            COMMANDED_EGR,
            EGR_ERROR,
            COMMANDED_EVAPORATIVE_PURGE,
            FUEL_LEVEL_INPUT,
            WARM_UPS_SINCE_DTC_RESET,
            DISTANCE_TRAVELLED_SINCE_DTC_RESET,
            EVAP_SYSTEM_VAPOR_PRESSURE,
            BAROMETRIC_PRESSURE,
            DUAL_BANK_O2S11_WIDE_RANGE_ALT,
            DUAL_BANK_O2S12_WIDE_RANGE_ALT,
            DUAL_BANK_O2S13_WIDE_RANGE_ALT,
            DUAL_BANK_O2S14_WIDE_RANGE_ALT,
            DUAL_BANK_O2S21_WIDE_RANGE_ALT,
            DUAL_BANK_O2S22_WIDE_RANGE_ALT,
            DUAL_BANK_O2S23_WIDE_RANGE_ALT,
            DUAL_BANK_O2S24_WIDE_RANGE_ALT,
            QUAD_BANK_O2S11_WIDE_RANGE_ALT,
            QUAD_BANK_O2S12_WIDE_RANGE_ALT,
            QUAD_BANK_O2S21_WIDE_RANGE_ALT,
            QUAD_BANK_O2S22_WIDE_RANGE_ALT,
            QUAD_BANK_O2S31_WIDE_RANGE_ALT,
            QUAD_BANK_O2S32_WIDE_RANGE_ALT,
            QUAD_BANK_O2S41_WIDE_RANGE_ALT,
            QUAD_BANK_O2S42_WIDE_RANGE_ALT,
            CATALYST_TEMPERATURE_BANK_1_SENSOR_1,
            CATALYST_TEMPERATURE_BANK_1_SENSOR_2,
            CATALYST_TEMPERATURE_BANK_2_SENSOR_1,
            CATALYST_TEMPERATURE_BANK_2_SENSOR_2
                           );

}