// This file is protected under the KILLGPL.
// For more information, visit http://www.lukeleber.github.io/KILLGPL.html
//
// Copyright (c) Luke Leber <LukeLeber@gmail.com>

package com.lukeleber.scandroid.gui.fragments.detail;

import android.app.AlertDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.lukeleber.scandroid.BuildConfig;
import com.lukeleber.scandroid.Globals;
import com.lukeleber.scandroid.R;
import com.lukeleber.scandroid.gui.fragments.util.ViewHolderBase;
import com.lukeleber.scandroid.sae.j1979.AuxiliaryInputStatus;
import com.lukeleber.scandroid.sae.j1979.MonitorStatus;
import com.lukeleber.scandroid.sae.j1979.OBDSupport;
import com.lukeleber.scandroid.sae.j1979.OxygenSensor;
import com.lukeleber.scandroid.sae.j1979.PID;
import com.lukeleber.scandroid.sae.j1979.PIDSupport;
import com.lukeleber.scandroid.sae.j1979.Profile;
import com.lukeleber.scandroid.sae.j1979.SecondaryAirStatus;
import com.lukeleber.scandroid.sae.j1979.FuelSystemStatus;
import com.lukeleber.scandroid.sae.j1979.detail.AppendixA;
import com.lukeleber.scandroid.sae.j1979.detail.AppendixB;
import com.lukeleber.scandroid.sae.j2012.DiagnosticTroubleCode;
import com.lukeleber.scandroid.util.Unit;
import com.lukeleber.util.SerializablePair;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

/**
 * This internal detail class provides GUI friendly wrappers for the PIDs/TIDs/OBDMIDs/INFOTYPEs
 * found in all SAE J1979 Appendices.
 *
 * <strong>Here be dragons.</strong>
 *
 */
public final class SAEJ1979AppendixWrapper
{

    /// @internal debugging tag
    private final static String TAG = SAEJ1979AppendixWrapper.class.getName();

    /// The base class for PIDs $00, $20, $40, $60, $80, $A0, $C0, and $E0
    private abstract static class SupportWrapper
            extends PIDWrapper<PIDSupport>
    {
        /// The start of the range
        private final int min;

        /// The end of the range
        private final int max;

        /// The content of the expansion dialog
        private String expansionMessageText;

        private final class ViewHolder extends ViewHolderBase
        {

            final Context context;

            final TextView rangeMin;
            final TextView rangeMax;
            final ListView supportList;

            ViewHolder(View view)
            {
                this.context = view.getContext();

                this.rangeMin = ButterKnife.findById(view, R.id.supported_pids_range_min);
                this.rangeMax = ButterKnife.findById(view, R.id.supported_pids_range_max);
                this.supportList = ButterKnife.findById(view, R.id.supported_pids_support_list);

                this.rangeMin.setText(String.format("%02x", min));
                this.rangeMax.setText(String.format("%02x", max));
            }
        }

        @Override
        public final int getLayoutID()
        {
            return R.layout.pid_support;
        }

        @SuppressWarnings("unchecked")
        @Override
        public final ViewHolder createViewHolder(@NonNull View view)
        {
            final Context context = view.getContext();
            view.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    if(expansionMessageText != null)
                    {
                        new AlertDialog.Builder(context)
                                .setMessage(String.format(context.getString(
                                        R.string.pid_support_expansion), expansionMessageText))
                                .setTitle(String.format(context.getString(
                                        R.string.pid_support_expansion_title), min, max))
                                .create()
                                .show();
                    }
                }
            });
            view.setOnLongClickListener(new View.OnLongClickListener()
            {
                @Override
                public boolean onLongClick(View view)
                {
                    new AlertDialog.Builder(context)
                            .setMessage(String.format(context.getString(R.string.pid_support_desc), min, max))
                            .setTitle(String.format(context.getString(R.string.pid_support_desc_title), min, max))
                            .create()
                            .show();
                    return true;
                }
            });
            return new ViewHolder(view);
        }

        @Override
        public final <U extends ViewHolderBase> void updateViewHolder(@NonNull U viewHolder,
                                                                      @NonNull PIDSupport value)
        {
            ViewHolder vh = (ViewHolder)viewHolder;
            if(expansionMessageText == null)
            {
                int bits = value.getBits();
                List<String> pids = new ArrayList<>(0x20);
                StringBuilder sb = new StringBuilder();
                for(int i = 0; i < 0x20; ++i)
                {
                    String sup = String.format("$%02x: %s",
                            i,
                            (bits & (1 << i)) != 0 ?
                                    Globals.getString(Globals.I18N_STRING.SUPPORTED_CAPS) :
                                    Globals.getString(Globals.I18N_STRING.UNSUPPORTED_CAPS));

                    sb.append(sup).append(System.lineSeparator());
                    pids.add(sup);
                }
                expansionMessageText = sb.toString();
                vh.supportList.setAdapter(
                        new ArrayAdapter<>(
                                vh.context,
                                android.R.layout.simple_list_item_1,
                                pids)
                );
            }
            /// SAE J1979 mandates that the list of supported PIDs/TIDs/OBDMIDs/INFOTYPEs
            /// must be cached by the external test equipment.  As such, repeated querying for
            /// the information is in violation of SAE J1979; so a debug warning is issued.
            else if(BuildConfig.DEBUG)
            {
                Log.w(TAG, "Something is updating a PID found in Appendix A, " +
                        "this shouldn't happen.  A convenience stack trace follows...",
                        new Throwable());
            }
        }

        public SupportWrapper(PID<PIDSupport> facet, int min, int max)
        {
            super(facet);
            this.min = min;
            this.max = max;
        }
    }

    /// GUI Wrapper for $00
    private final static class PidSupport_1_20 extends SupportWrapper
    {
        PidSupport_1_20()
        {
            super(AppendixA.J1979_CHECK_PID_SUPPORT_1_TO_20, 1, 20);
        }
    }

    /// GUI Wrapper for $20
    private final static class PidSupport_21_40 extends SupportWrapper
    {
        PidSupport_21_40()
        {
            super(AppendixA.J1979_CHECK_PID_SUPPORT_21_TO_40, 21, 40);
        }
    }

    ///GUI Wrapper for $40
    private final static class PidSupport_41_60 extends SupportWrapper
    {
        PidSupport_41_60()
        {
            super(AppendixA.J1979_CHECK_PID_SUPPORT_41_TO_60, 41, 60);
        }
    }

    /// GUI Wrapper for $60
    private final static class PidSupport_61_80 extends SupportWrapper
    {
        PidSupport_61_80()
        {
            super(AppendixA.J1979_CHECK_PID_SUPPORT_61_TO_80, 0x61, 0x80);
        }
    }

    /// GUI Wrapper for $80
    private final static class PidSupport_81_A0 extends SupportWrapper
    {
        PidSupport_81_A0()
        {
            super(AppendixA.J1979_CHECK_PID_SUPPORT_81_TO_A0, 0x81, 0xA0);
        }
    }

    /// GUI Wrapper for $A0
    private final static class PidSupport_A1_C0 extends SupportWrapper
    {
        PidSupport_A1_C0()
        {
            super(AppendixA.J1979_CHECK_PID_SUPPORT_A1_TO_C0, 0xA1, 0xC0);
        }
    }

    /// GUI Wrapper for $C0
    private final static class PidSupport_C1_E0 extends SupportWrapper
    {
        PidSupport_C1_E0()
        {
            super(AppendixA.J1979_CHECK_PID_SUPPORT_C1_TO_E0, 0xC1, 0xE0);
        }
    }

    /// GUI Wrapper for $E0
    private final static class PidSupport_E1_FF extends SupportWrapper
    {
        PidSupport_E1_FF()
        {
            super(AppendixA.J1979_CHECK_PID_SUPPORT_E1_TO_FF, 0xE1, 0xFF);
        }
    }

    /// <-- SAE J1979 Appendix B


    /// GUI wrapper class for PID $01
    private final static class MonitorStatusWrapper extends PIDWrapper<MonitorStatus>
    {
        MonitorStatusWrapper()
        {
            super(AppendixB.MONITOR_STATUS);
        }

        final class ViewHolder extends ViewHolderBase
        {
            final TextView milStatus;
            final TextView dtcCount;
            String monitorStatusText;

            ViewHolder(@NonNull final View view)
            {
                milStatus = ButterKnife.findById(view, R.id.monitor_status_mil_status);
                dtcCount = ButterKnife.findById(view, R.id.monitor_status_dtc_count);
                monitorStatusText = Globals.getString(Globals.I18N_STRING.TOUCH_TO_EXPAND);

                view.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        new AlertDialog.Builder(view.getContext())
                                .setMessage(
                                        String.format(
                                                view.getContext().getString(
                                                        R.string.pid_monitor_status_expansion),
                                                milStatus.getText(),
                                                dtcCount.getText(),
                                                monitorStatusText
                                                     )
                                           )
                                .setTitle(view.getContext().getString(
                                        R.string.pid_monitor_status_expansion_title))
                                .create()
                                .show();
                    }
                });
                view.setOnLongClickListener(new View.OnLongClickListener()
                {
                    @Override
                    public boolean onLongClick(View v)
                    {
                        new AlertDialog.Builder(view.getContext())
                                .setMessage(R.string.pid_monitor_status_desc)
                                .setTitle(view.getContext().getString(
                                        R.string.pid_monitor_status_desc_title))
                                .create().show();
                        return true;
                    }
                });
            }
        }

        @Override
        public final ViewHolderBase createViewHolder(@NonNull final View view)
        {
            return new ViewHolder(view);
        }

        @Override
        public final <U extends ViewHolderBase> void updateViewHolder(@NonNull U viewHolder,
                                                                      @NonNull MonitorStatus value)
        {
            ViewHolder vh = (ViewHolder) viewHolder;
            vh.milStatus.setText(value.isMalfunctionLampOn() ?
                    Globals.getString(Globals.I18N_STRING.ON_CAPS) :
                    Globals.getString(Globals.I18N_STRING.OFF_CAPS));
            vh.dtcCount.setText(String.valueOf(value.getDiagnosticTroubleCodeCount()));
            vh.monitorStatusText = value.getSupportReadinessString();
        }

        @Override
        public int getLayoutID()
        {
            return R.layout.monitor_status;
        }
    }

    /// GUI Wrapper for PID $02
    private final static class FreezeFrameDTCWrapper
            extends PIDWrapper<DiagnosticTroubleCode>
    {

        FreezeFrameDTCWrapper()
        {
            super(AppendixB.FREEZE_FRAME_DTC);
        }
        final class ViewHolder extends ViewHolderBase
        {
            final TextView dtcEncoding;
            final TextView dtcDescription;

            ViewHolder(@NonNull final View view)
            {
                view.setOnLongClickListener(new View.OnLongClickListener()
                {
                    @Override
                    public boolean onLongClick(View v)
                    {
                        new AlertDialog.Builder(view.getContext())
                                .setMessage(R.string.pid_dtc_that_caused_freeze_frame_desc)
                                .setTitle(v.getContext().getString(
                                        R.string.pid_dtc_that_caused_freeze_frame_desc_title))
                                .create().show();
                        return true;
                    }
                });

                this.dtcEncoding = ButterKnife.findById(view, R.id.diagnostic_trouble_code_encoding);
                this.dtcDescription = ButterKnife.findById(view, R.id.diagnostic_trouble_code_description);
            }
        }

        @Override
        public int getLayoutID()
        {
            return R.layout.diagnostic_trouble_code;
        }

        @Override
        public ViewHolderBase createViewHolder(@NonNull final View view)
        {
            return new ViewHolder(view);
        }

        @Override
        public final <U extends ViewHolderBase> void updateViewHolder(@NonNull U viewHolder,
                                                                      @NonNull DiagnosticTroubleCode value)
        {
            ViewHolder vh = (ViewHolder)viewHolder;
            if(value.getBits() == 0)
            {
                vh.dtcEncoding.setText(Globals.getString(
                        Globals.I18N_STRING.NOT_AVAILABLE));
                vh.dtcDescription.setText("");
            }
            else
            {
                vh.dtcEncoding.setText(value.getCode());
                vh.dtcDescription.setText(value.getNaming());
            }
        }
    }

    /// GUI Wrapper for PID $03
    private final static class FuelSystemStatusWrapper extends PIDWrapper<SerializablePair<FuelSystemStatus, FuelSystemStatus>>
    {
        FuelSystemStatusWrapper()
        {
            super(AppendixB.FUEL_SYSTEM_STATUS);
        }

        private boolean initialized;

        final class ViewHolder extends ViewHolderBase
        {

            final TextView fSys1;
            final TextView fSys2;

            final TextView fSys2Label;

            ViewHolder(@NonNull final View view)
            {
                this.fSys1 = ButterKnife.findById(view, R.id.fuel_system_1_status);
                this.fSys2 = ButterKnife.findById(view, R.id.fuel_system_2_status);
                this.fSys2Label = ButterKnife.findById(view, R.id.fuel_system_2_label);
                view.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        String NA = Globals.getString(Globals.I18N_STRING.NOT_AVAILABLE);
                        new AlertDialog.Builder(v.getContext())
                                .setMessage(
                                        String.format(
                                                v.getContext().getString(
                                                        R.string.pid_fuel_system_status_expansion),
                                                NA.equals(fSys1.getText()) ? NA :
                                                        FuelSystemStatus.valueOf(fSys1.getText().toString())
                                                                        .toI18NString(view.getContext()),
                                                NA.equals(fSys2.getText()) ? NA :
                                                        FuelSystemStatus.valueOf(fSys2.getText().toString())
                                                                        .toI18NString(view.getContext())
                                                     ))
                                .setTitle(v.getContext().getString(
                                        R.string.pid_fuel_system_status_expansion_title))
                                .create()
                                .show();
                    }
                });

                view.setOnLongClickListener(new View.OnLongClickListener()
                {
                    @Override
                    public boolean onLongClick(View v)
                    {
                        new AlertDialog.Builder(v.getContext())
                                .setMessage(R.string.pid_fuel_system_status_desc)
                                .setTitle(v.getContext().getString(
                                        R.string.pid_fuel_system_status_desc_title))
                                .create().show();
                        return true;
                    }
                });
            }
        }

        @Override
        public int getLayoutID()
        {
            return R.layout.fuel_system_status;
        }

        @Override
        public ViewHolderBase createViewHolder(@NonNull final View view)
        {
            return new ViewHolder(view);
        }

        @Override
        public final <U extends ViewHolderBase> void updateViewHolder(@NonNull U viewHolder,
                                                                      @NonNull SerializablePair<FuelSystemStatus, FuelSystemStatus> value)
        {
            ViewHolder vh = (ViewHolder)viewHolder;
            if(!initialized)
            {
                initialized = true;
                if(value.second == null)
                {
                    vh.fSys2Label.setVisibility(View.GONE);
                    vh.fSys2.setVisibility(View.GONE);
                }
            }
            vh.fSys1.setText(value.first.toString());
            if(value.second != null)
            {
                vh.fSys2.setText(value.second.toString());
            }
        }
    }

    /// GUI Wrapper for PID $04
    private final static class CalculatedEngineLoadWrapper
            extends PIDWrapper<Float>
    {
        CalculatedEngineLoadWrapper()
        {
            super(AppendixB.CALCULATED_ENGINE_LOAD);
        }

        @Override
        public ViewHolderBase createViewHolder(@NonNull final View view)
        {
            final Context context = view.getContext();
            view.setOnLongClickListener(new View.OnLongClickListener()
            {
                @Override
                public boolean onLongClick(View view)
                {
                    new AlertDialog.Builder(context)
                            .setTitle(context.getString(R.string.pid_calculated_engine_load_desc_title))
                            .setMessage(context.getString(R.string.pid_calculated_engine_load_desc))
                            .create()
                            .show();
                    return true;
                }
            });
            return super.createViewHolder(view);
        }
    }

    /// GUI Wrapper for PID $05
    private final static class EngineCoolantTemperatureWrapper extends PIDWrapper<Integer>
    {
        public EngineCoolantTemperatureWrapper()
        {
            super(AppendixB.ENGINE_COOLANT_TEMPERATURE);
        }

        @Override
        public ViewHolderBase createViewHolder(@NonNull final View view)
        {
            view.setOnLongClickListener(
                new View.OnLongClickListener()
                {
                    @Override
                    public boolean onLongClick(View view)
                    {
                        final Context context = view.getContext();
                        new AlertDialog.Builder(context)
                                .setTitle(context.getString(R.string.pid_engine_coolant_temp_desc_title))
                                .setMessage(context.getString(R.string.pid_engine_coolant_temp_desc))
                                .create()
                                .show();
                        return true;
                    }
                }
            );
            super.addUnitSelector(view);
            return super.createViewHolder(view);
        }
    }

    /// View holder implementation common to PIDs 6-9
    private final static class FuelTrimViewHolder extends ViewHolderBase
    {

        final Context context;
        final TextView fuelTrim1;
        final TextView fuelTrim2;


        FuelTrimViewHolder(View view, final boolean shortTerm, final int label1, final int label2)
        {
            this.context = view.getContext();
            view.setOnLongClickListener(new View.OnLongClickListener() {
                /// Example:
                ///  ____________________________________
                /// |      Bank 1 & 3 STFT Overview      |
                /// |____________________________________|
                /// | This PID retrieves the short term  |
                /// | fuel trim of fuel injector banks   |
                /// | 1 and (if equipped) 3...           |
                /// |____________________________________|
                @Override
                public boolean onLongClick(View view) {
                    new AlertDialog.Builder(context)
                            .setTitle(String.format(
                                    context.getString(R.string.pid_fuel_trim_desc_title),
                                    label1,
                                    label2,
                                    context.getString(shortTerm ?
                                            R.string.short_term_fuel_trim_acronym :
                                            R.string.long_term_fuel_trim_acronym)))
                            .setMessage(String.format(
                                    context.getString(R.string.pid_fuel_trim_desc),
                                    context.getString(shortTerm ?
                                            R.string.short_term :
                                            R.string.long_term),
                                    label1, label2, label2))
                            .create()
                            .show();
                    return true;
                }
            });
            ((TextView)ButterKnife.findById(view, R.id.fuel_trim_label_1))
                    .setText(shortTerm ?
                            String.format(context.getString(
                                    R.string.short_term_fuel_trim_bank_label), label1) :
                            String.format(context.getString(
                                    R.string.long_term_fuel_trim_bank_label, label1), label1));
            ((TextView)ButterKnife.findById(view, R.id.fuel_trim_label_2)).setText(shortTerm ?
                    String.format(context.getString(
                            R.string.short_term_fuel_trim_bank_label), label2) :
                    String.format(context.getString(
                            R.string.long_term_fuel_trim_bank_label, label2), label2));

            this.fuelTrim1 = ButterKnife.findById(view, R.id.fuel_trim_value_1);
            this.fuelTrim2 = ButterKnife.findById(view, R.id.fuel_trim_value_2);
        }
    }

    /// GUI Wrapper for PID $06
    private final static class ShortTermFuelTrimBank_1_3_Wrapper
            extends PIDWrapper<SerializablePair<Float, Float>>
    {

        ShortTermFuelTrimBank_1_3_Wrapper()
        {
            super(AppendixB.SHORT_TERM_FUEL_TRIM_BANK_1_3);
        }

        @Override
        public final ViewHolderBase createViewHolder(@NonNull final View view)
        {
            return new FuelTrimViewHolder(view, true, 1, 3);
        }

        @Override
        public final <U extends ViewHolderBase> void updateViewHolder(@NonNull U viewHolder,
                                                                      @NonNull SerializablePair<Float, Float> value)
        {
            FuelTrimViewHolder vh = (FuelTrimViewHolder) viewHolder;
            vh.fuelTrim1.setText(String.format("%1$3.1f", value.first));
            vh.fuelTrim2.setText(value.second != null ?
                    String.format("%1$3.1f", value.second) :
                    vh.context.getString(R.string.not_available));
        }

        @Override
        public Unit getDisplayUnit()
        {
            return Unit.PERCENT;
        }

        @Override
        public void setDisplayUnit(Unit displayUnit)
        {
            throw new UnsupportedOperationException();
        }

        @Override
        public int getLayoutID()
        {
            return R.layout.fuel_trim;
        }
    }

    /// GUI Wrapper for PID $07
    private final static class LongTermFuelTrimBank_1_3_Wrapper
            extends PIDWrapper<SerializablePair<Float, Float>>
    {
        LongTermFuelTrimBank_1_3_Wrapper()
        {
            super(AppendixB.LONG_TERM_FUEL_TRIM_BANK_1_3);
        }

        @Override
        public final ViewHolderBase createViewHolder(@NonNull final View view)
        {
            return new FuelTrimViewHolder(view, false, 1, 3);
        }

        @Override
        public final <U extends ViewHolderBase> void updateViewHolder(@NonNull U viewHolder,
                                                                      @NonNull SerializablePair<Float, Float> value)
        {
            FuelTrimViewHolder vh = (FuelTrimViewHolder) viewHolder;
            vh.fuelTrim1.setText(String.format("%1$3.1f", value.first));
            vh.fuelTrim2.setText(value.second != null ?
                    String.format("%1$3.1f", value.second) :
                    vh.context.getString(R.string.not_available));
        }

        @Override
        public Unit getDisplayUnit()
        {
            return Unit.PERCENT;
        }

        @Override
        public void setDisplayUnit(Unit displayUnit)
        {
            throw new UnsupportedOperationException();
        }

        @Override
        public int getLayoutID()
        {
            return R.layout.fuel_trim;
        }
    }

    /// GUI Wrapper for PID $08
    private final static class ShortTermFuelTrimBank_2_4_Wrapper
            extends PIDWrapper<SerializablePair<Float, Float>>
    {

        ShortTermFuelTrimBank_2_4_Wrapper()
        {
            super(AppendixB.SHORT_TERM_FUEL_TRIM_BANK_2_4);
        }

        @Override
        public final ViewHolderBase createViewHolder(@NonNull final View view)
        {
            return new FuelTrimViewHolder(view, true, 2, 4);
        }

        @Override
        public final <U extends ViewHolderBase> void updateViewHolder(@NonNull U viewHolder,
                                                                      @NonNull SerializablePair<Float, Float> value)
        {
            FuelTrimViewHolder vh = (FuelTrimViewHolder) viewHolder;
            vh.fuelTrim1.setText(String.format("%1$3.1f", value.first));
            vh.fuelTrim2.setText(value.second != null ?
                    String.format("%1$3.1f", value.second) :
                    vh.context.getString(R.string.not_available));
        }

        @Override
        public Unit getDisplayUnit()
        {
            return Unit.PERCENT;
        }

        @Override
        public void setDisplayUnit(Unit displayUnit)
        {
            throw new UnsupportedOperationException();
        }

        @Override
        public int getLayoutID()
        {
            return R.layout.fuel_trim;
        }
    }

    /// GUI Wrapper for PID $09
    private final static class LongTermFuelTrimBank_2_4_Wrapper
            extends PIDWrapper<SerializablePair<Float, Float>>
    {
        LongTermFuelTrimBank_2_4_Wrapper()
        {
            super(AppendixB.LONG_TERM_FUEL_TRIM_BANK_2_4);
        }

        @Override
        public final ViewHolderBase createViewHolder(@NonNull final View view)
        {
            return new FuelTrimViewHolder(view, false, 2, 4);
        }

        @Override
        public final <U extends ViewHolderBase> void updateViewHolder(@NonNull U viewHolder,
                                                                      @NonNull SerializablePair<Float, Float> value)
        {
            FuelTrimViewHolder vh = (FuelTrimViewHolder) viewHolder;
            vh.fuelTrim1.setText(String.format("%1$3.1f", value.first));
            vh.fuelTrim2.setText(value.second != null ?
                    String.format("%1$3.1f", value.second) :
                    vh.context.getString(R.string.not_available));
        }

        @Override
        public Unit getDisplayUnit()
        {
            return Unit.PERCENT;
        }

        @Override
        public void setDisplayUnit(Unit displayUnit)
        {
            throw new UnsupportedOperationException();
        }

        @Override
        public int getLayoutID()
        {
            return R.layout.fuel_trim;
        }
    }

    /// GUI Wrapper for PID $0A
    private final static class NarrowRangeFuelRailPressureRefAtmosphereWrapper
        extends PIDWrapper<Integer>
    {
        NarrowRangeFuelRailPressureRefAtmosphereWrapper()
        {
            super(AppendixB.FUEL_RAIL_PRESSURE_NARROW_RANGE_ATMOSPHEREIC_REFERENCE);
        }

        @Override
        public final ViewHolderBase createViewHolder(@NonNull final View view)
        {
            view.setOnLongClickListener(new View.OnLongClickListener()
            {
                @Override
                public boolean onLongClick(View view)
                {
                    final Context context = view.getContext();
                    new AlertDialog.Builder(context)
                            .setTitle(context.getString(R.string.pid_narrow_range_fuel_rail_pressure_ref_atmosphere_desc_title))
                            .setMessage(context.getString(R.string.pid_narrow_range_fuel_rail_pressure_ref_atmosphere_desc))
                            .create()
                            .show();
                    return true;
                }
            });
            super.addUnitSelector(view);
            return super.createViewHolder(view);
        }
    }

    /// GUI Wrapper for PID $0B
    private final static class IntakeManifoldAbsolutePressureWrapper
            extends PIDWrapper<Integer>
    {
        IntakeManifoldAbsolutePressureWrapper()
        {
            super(AppendixB.INTAKE_MANIFOLD_ABSOLUTE_PRESSURE);
        }

        @Override
        public final ViewHolderBase createViewHolder(@NonNull final View view)
        {
            view.setOnLongClickListener(new View.OnLongClickListener()
            {
                @Override
                public boolean onLongClick(View view)
                {
                    final Context context = view.getContext();
                    new AlertDialog.Builder(context)
                            .setTitle(context.getString(R.string.pid_manifold_absolute_pressure_desc_title))
                            .setMessage(context.getString(R.string.pid_manifold_absolute_pressure_desc))
                            .create()
                            .show();
                    return true;
                }
            });
            super.addUnitSelector(view);
            return super.createViewHolder(view);
        }
    }

    /// GUI Wrapper for PID $0C
    private final static class EngineSpeedWrapper
            extends PIDWrapper<Float>
    {
        EngineSpeedWrapper()
        {
            super(AppendixB.ENGINE_SPEED);
        }
    }

    /// GUI Wrapper for PID $0D
    private final static class VehicleSpeedWrapper
            extends PIDWrapper<Integer>
    {
        VehicleSpeedWrapper()
        {
            super(AppendixB.VEHICLE_SPEED);
        }

        @Override
        public final ViewHolderBase createViewHolder(@NonNull final View view)
        {
            view.setOnLongClickListener(new View.OnLongClickListener()
            {
                @Override
                public boolean onLongClick(View view)
                {
                    final Context context = view.getContext();
                    new AlertDialog.Builder(context)
                            .setTitle(context.getString(R.string.pid_engine_speed_desc_title))
                            .setMessage(context.getString(R.string.pid_engine_speed_desc))
                            .create()
                            .show();
                    return true;
                }
            });
            super.addUnitSelector(view);
            return super.createViewHolder(view);
        }
    }

    /// GUI Wrapper for PID $0E
    private final static class TimingAdvanceWrapper
            extends PIDWrapper<Float>
    {
        TimingAdvanceWrapper()
        {
            super(AppendixB.TIMING_ADVANCE);
        }
    }

    /// GUI Wrapper for PID $0F
    private final static class IntakeAirTemperatureWrapper
            extends PIDWrapper<Integer>
    {
        IntakeAirTemperatureWrapper()
        {
            super(AppendixB.INTAKE_AIR_TEMPERATURE);
        }

        @Override
        public final ViewHolderBase createViewHolder(@NonNull final View view)
        {
            super.addUnitSelector(view);
            return super.createViewHolder(view);
        }
    }

    /// GUI Wrapper for PID $10
    private final static class MassAirflowRateWrapper
            extends PIDWrapper<Float>
    {
        MassAirflowRateWrapper()
        {
            super(AppendixB.MASS_AIRFLOW_RATE);
        }

        @Override
        public final ViewHolderBase createViewHolder(@NonNull final View view)
        {
            super.addUnitSelector(view);
            return super.createViewHolder(view);
        }
    }

    /// GUI Wrapper for PID $11
    private final static class AbsoluteThrottlePositionWrapper
            extends PIDWrapper<Float>
    {
        AbsoluteThrottlePositionWrapper()
        {
            super(AppendixB.ABSOLUTE_THROTTLE_POSITION);
        }
    }

    /// GUI Wrapper for PID $12
    private final static class SecondaryAirStatusWrapper extends PIDWrapper<SecondaryAirStatus>
    {
        SecondaryAirStatusWrapper()
        {
            super(AppendixB.SECONDARY_AIR_STATUS);
        }
    }

    private final static class OxygenSensorLocationViewHolder extends ViewHolderBase
    {
        final TextView value;

        OxygenSensorLocationViewHolder(View view)
        {
            this.value = ButterKnife.findById(view, R.id.oxygen_sensor_location_value);
        }
    }

    /// GUI Wrapper for PID $13
    private final static class OxygenSensorLocationDualBankWrapper extends PIDWrapper<OxygenSensor[]>
    {
        OxygenSensorLocationDualBankWrapper()
        {
            super(AppendixB.DUAL_BANK_OXYGEN_SENSOR_LOCATIONS);
        }

        @Override
        public final ViewHolderBase createViewHolder(@NonNull final View view)
        {
            return new OxygenSensorLocationViewHolder(view);
        }

        @Override
        public final <U extends ViewHolderBase> void updateViewHolder(@NonNull U viewHolder,
                                                                      @NonNull OxygenSensor[] value)
        {
            OxygenSensorLocationViewHolder vh = (OxygenSensorLocationViewHolder) viewHolder;
            StringBuilder sb = new StringBuilder("[");
            for(OxygenSensor sensor : value)
            {
                sb.append(sensor.toString()).append(", ");
            }
            vh.value.setText(sb.substring(0, sb.length() - 2) + "]");
        }

        @Override
        public int getLayoutID()
        {
            return R.layout.oxygen_sensor_location;
        }
    }

    /// Base class for PIDs $14 - $1B
    private abstract static class ConventionalOxygenSensorPIDWrapper extends PIDWrapper<SerializablePair<Float, Float>>
    {

        ConventionalOxygenSensorPIDWrapper(PID<SerializablePair<Float, Float>> pid)
        {
            super(pid);
        }

        // TODO: Create dual layout...

        @Override
        public final ViewHolderBase createViewHolder(@NonNull final View view)
        {
            return super.createViewHolder(view);
        }
    }

    /// GUI Wrapper for PID $14 (dual bank)
    private final static class DualBankO2S11ConventionalWrapper extends ConventionalOxygenSensorPIDWrapper
    {
        DualBankO2S11ConventionalWrapper()
        {
            super(AppendixB.DUAL_BANK_O2S11_CONVENTIONAL);
        }
    }

    /// GUI Wrapper for PID $14 (quad bank)
    private final static class QuadBankO2S11ConventionalWrapper extends ConventionalOxygenSensorPIDWrapper
    {
        QuadBankO2S11ConventionalWrapper()
        {
            super(AppendixB.QUAD_BANK_O2S11_CONVENTIONAL);
        }
    }

    /// GUI Wrapper for PID $15 (dual bank)
    private final static class DualBankO2S12ConventionalWrapper extends ConventionalOxygenSensorPIDWrapper
    {
        DualBankO2S12ConventionalWrapper()
        {
            super(AppendixB.DUAL_BANK_O2S12_CONVENTIONAL);
        }
    }

    /// GUI Wrapper for PID $15 (quad bank)
    private final static class QuadBankO2S12ConventionalWrapper extends ConventionalOxygenSensorPIDWrapper
    {
        QuadBankO2S12ConventionalWrapper()
        {
            super(AppendixB.QUAD_BANK_O2S12_CONVENTIONAL);
        }
    }

    /// GUI Wrapper for PID $16 (dual bank)
    private final static class DualBankO2S13ConventionalWrapper extends ConventionalOxygenSensorPIDWrapper
    {
        DualBankO2S13ConventionalWrapper()
        {
            super(AppendixB.DUAL_BANK_O2S13_CONVENTIONAL);
        }
    }

    /// GUI Wrapper for PID $16 (quad bank)
    private final static class QuadBankO2S21ConventionalWrapper extends ConventionalOxygenSensorPIDWrapper
    {
        QuadBankO2S21ConventionalWrapper()
        {
            super(AppendixB.QUAD_BANK_O2S21_CONVENTIONAL);
        }
    }

    /// GUI Wrapper for PID $17 (dual bank)
    private final static class DualBankO2S14ConventionalWrapper extends ConventionalOxygenSensorPIDWrapper
    {
        DualBankO2S14ConventionalWrapper()
        {
            super(AppendixB.DUAL_BANK_O2S14_CONVENTIONAL);
        }
    }

    /// GUI Wrapper for PID $17 (quad bank)
    private final static class QuadBankO2S22ConventionalWrapper extends ConventionalOxygenSensorPIDWrapper
    {
        QuadBankO2S22ConventionalWrapper()
        {
            super(AppendixB.QUAD_BANK_O2S22_CONVENTIONAL);
        }
    }

    /// GUI Wrapper for PID $18 (dual bank)
    private final static class DualBankO2S21ConventionalWrapper extends ConventionalOxygenSensorPIDWrapper
    {
        DualBankO2S21ConventionalWrapper()
        {
            super(AppendixB.DUAL_BANK_O2S21_CONVENTIONAL);
        }
    }

    /// GUI Wrapper for PID $18 (quad bank)
    private final static class QuadBankO2S31ConventionalWrapper extends ConventionalOxygenSensorPIDWrapper
    {
        QuadBankO2S31ConventionalWrapper()
        {
            super(AppendixB.QUAD_BANK_O2S31_CONVENTIONAL);
        }
    }

    /// GUI Wrapper for PID $19 (dual bank)
    private final static class DualBankO2S22ConventionalWrapper
            extends ConventionalOxygenSensorPIDWrapper
    {
        DualBankO2S22ConventionalWrapper()
        {
            super(AppendixB.DUAL_BANK_O2S22_CONVENTIONAL);
        }
    }

    /// GUI Wrapper for PID $19 (quad bank)
    private final static class QuadBankO2S32ConventionalWrapper
            extends ConventionalOxygenSensorPIDWrapper
    {
        QuadBankO2S32ConventionalWrapper()
        {
            super(AppendixB.QUAD_BANK_O2S32_CONVENTIONAL);
        }
    }

    /// GUI Wrapper for PID $1A (dual bank)
    private final static class DualBankO2S23ConventionalWrapper extends ConventionalOxygenSensorPIDWrapper
    {
        DualBankO2S23ConventionalWrapper()
        {
            super(AppendixB.DUAL_BANK_O2S23_CONVENTIONAL);
        }
    }

    /// GUI Wrapper for PID $1A (quad bank)
    private final static class QuadBankO2S41ConventionalWrapper extends ConventionalOxygenSensorPIDWrapper
    {
        QuadBankO2S41ConventionalWrapper()
        {
            super(AppendixB.QUAD_BANK_O2S41_CONVENTIONAL);
        }
    }

    /// GUI Wrapper for PID $1B (dual bank)
    private final static class DualBankO2S24ConventionalWrapper extends ConventionalOxygenSensorPIDWrapper
    {
        DualBankO2S24ConventionalWrapper()
        {
            super(AppendixB.DUAL_BANK_O2S24_CONVENTIONAL);
        }
    }

    /// GUI Wrapper for PID $1B (quad bank)
    private final static class QuadBankO2S42ConventionalWrapper extends ConventionalOxygenSensorPIDWrapper
    {
        QuadBankO2S42ConventionalWrapper()
        {
            super(AppendixB.QUAD_BANK_O2S42_CONVENTIONAL);
        }
    }

    /// GUI Wrapper for PID $1C
    private final static class OBDSupportWrapper extends PIDWrapper<OBDSupport>
    {
        OBDSupportWrapper()
        {
            super(AppendixB.OBD_SUPPORT);
        }
    }

    /// GUI Wrapper for PID $1D
    private final static class OxygenSensorLocationQuadBankWrapper extends PIDWrapper<OxygenSensor[]>
    {
        OxygenSensorLocationQuadBankWrapper()
        {
            super(AppendixB.QUAD_BANK_OXYGEN_SENSOR_LOCATIONS);
        }

        @Override
        public final ViewHolderBase createViewHolder(@NonNull final View view)
        {
            return new OxygenSensorLocationViewHolder(view);
        }

        @Override
        public final <U extends ViewHolderBase> void updateViewHolder(@NonNull U viewHolder,
                                                                      @NonNull OxygenSensor[] value)
        {
            OxygenSensorLocationViewHolder vh = (OxygenSensorLocationViewHolder) viewHolder;
            StringBuilder sb = new StringBuilder("[");
            for(OxygenSensor sensor : value)
            {
                sb.append(sensor.toString()).append(", ");
            }
            vh.value.setText(sb.substring(0, sb.length() - 2) + "]");
        }

        @Override
        public int getLayoutID()
        {
            return R.layout.oxygen_sensor_location;
        }
    }

    /// GUI Wrapper for PID $1E
    private final static class AuxiliaryInputStatusWrapper extends PIDWrapper<AuxiliaryInputStatus>
    {
        AuxiliaryInputStatusWrapper()
        {
            super(AppendixB.AUXILIARY_INPUT_STATUS);
        }
    }

    /// GUI Wrapper for PID $1F
    private final static class TimeSinceEngineStartWrapper extends PIDWrapper<Integer>
    {
        TimeSinceEngineStartWrapper()
        {
            super(AppendixB.TIME_SINCE_ENGINE_START);
        }
    }

    /// GUI Wrapper for PID $21
    private final static class DistanceTravelledWithActiveMILWrapper extends PIDWrapper<Integer>
    {
        DistanceTravelledWithActiveMILWrapper()
        {
            super(AppendixB.DISTANCE_TRAVELLED_WHILE_MIL_IS_ACTIVATED);
        }

        @Override
        public ViewHolderBase createViewHolder(@NonNull View view)
        {
            super.addUnitSelector(view);
            return super.createViewHolder(view);
        }
    }

    /// GUI Wrapper for PID $22
    private final static class FuelRailPressureRefManifoldVacWrapper extends PIDWrapper<Float>
    {
        FuelRailPressureRefManifoldVacWrapper()
        {
            super(AppendixB.FUEL_RAIL_PRESSURE_RELATIVE_TO_MANIFOLD_VACUUM);
        }

        @Override
        public ViewHolderBase createViewHolder(@NonNull View view)
        {
            super.addUnitSelector(view);
            return super.createViewHolder(view);
        }
    }

    /// GUI Wrapper for PID $23
    private final static class WideRangeVoltageFuelRailPressureRefAtmosphere extends PIDWrapper<Integer>
    {
        WideRangeVoltageFuelRailPressureRefAtmosphere()
        {
            super(AppendixB.FUEL_RAIL_PRESSURE_WIDE_RANGE);
        }

        @Override
        public ViewHolderBase createViewHolder(@NonNull View view)
        {
            super.addUnitSelector(view);
            return super.createViewHolder(view);
        }
    }

    /// Base class for PIDs $24 - $2B
    private abstract static class WideRangeVoltageOxygenSensorPIDWrapper extends PIDWrapper<SerializablePair<Float, Float>>
    {

        private final static class ViewHolder extends ViewHolderBase
        {
            final TextView lambdaValue;
            final TextView voltageValue;

            ViewHolder(View view)
            {
                this.lambdaValue = ButterKnife.findById(view, R.id.wide_range_oxygen_sensor_lambda_value);
                this.voltageValue = ButterKnife.findById(view, R.id.wide_range_oxygen_sensor_voltage_value);
            }
        }

        WideRangeVoltageOxygenSensorPIDWrapper(PID<SerializablePair<Float, Float>> pid)
        {
            super(pid);
        }

        @Override
        public final ViewHolderBase createViewHolder(@NonNull final View view)
        {
            ViewHolder rv = new ViewHolder(view);
            ((TextView)ButterKnife.findById(view, R.id.wide_range_oxygen_sensor_label))
                    .setText(super.unwrap().getDisplayName());
            super.addUnitSelector(view);
            return rv;
        }

        @Override
        public final <U extends ViewHolderBase> void updateViewHolder(@NonNull U viewHolder, @NonNull SerializablePair<Float, Float> value)
        {
            ViewHolder vh = (ViewHolder)viewHolder;
            vh.lambdaValue.setText(String.valueOf(value.first));
            vh.voltageValue.setText(String.valueOf(value.second));
        }

        @Override
        public final int getLayoutID()
        {
            return R.layout.wide_range_oxygen_sensor;
        }
    }

    /// GUI Wrapper for PID $24 (dual bank)
    private final static class DualBankO2S11WideRangeVoltageWrapper extends WideRangeVoltageOxygenSensorPIDWrapper
    {
        DualBankO2S11WideRangeVoltageWrapper()
        {
            super(AppendixB.DUAL_BANK_O2S11_WIDE_RANGE);
        }
    }

    /// GUI Wrapper for PID $24 (quad bank)
    private final static class QuadBankO2S11WideRangeVoltageWrapper extends WideRangeVoltageOxygenSensorPIDWrapper
    {
        QuadBankO2S11WideRangeVoltageWrapper()
        {
            super(AppendixB.QUAD_BANK_O2S11_WIDE_RANGE);
        }
    }

    /// GUI Wrapper for PID $25 (dual bank)
    private final static class DualBankO2S12WideRangeVoltageWrapper extends WideRangeVoltageOxygenSensorPIDWrapper
    {
        DualBankO2S12WideRangeVoltageWrapper()
        {
            super(AppendixB.DUAL_BANK_O2S12_WIDE_RANGE);
        }
    }

    /// GUI Wrapper for PID $25 (quad bank)
    private final static class QuadBankO2S12WideRangeVoltageWrapper extends WideRangeVoltageOxygenSensorPIDWrapper
    {
        QuadBankO2S12WideRangeVoltageWrapper()
        {
            super(AppendixB.QUAD_BANK_O2S12_WIDE_RANGE);
        }
    }

    /// GUI Wrapper for PID $26 (dual bank)
    private final static class DualBankO2S13WideRangeVoltageWrapper extends WideRangeVoltageOxygenSensorPIDWrapper
    {
        DualBankO2S13WideRangeVoltageWrapper()
        {
            super(AppendixB.DUAL_BANK_O2S13_WIDE_RANGE);
        }
    }

    /// GUI Wrapper for PID $26 (quad bank)
    private final static class QuadBankO2S21WideRangeVoltageWrapper extends WideRangeVoltageOxygenSensorPIDWrapper
    {
        QuadBankO2S21WideRangeVoltageWrapper()
        {
            super(AppendixB.QUAD_BANK_O2S21_WIDE_RANGE);
        }
    }

    /// GUI Wrapper for PID $27 (dual bank)
    private final static class DualBankO2S14WideRangeVoltageWrapper extends WideRangeVoltageOxygenSensorPIDWrapper
    {
        DualBankO2S14WideRangeVoltageWrapper()
        {
            super(AppendixB.DUAL_BANK_O2S14_WIDE_RANGE);
        }
    }

    /// GUI Wrapper for PID $27 (quad bank)
    private final static class QuadBankO2S22WideRangeVoltageWrapper extends WideRangeVoltageOxygenSensorPIDWrapper
    {
        QuadBankO2S22WideRangeVoltageWrapper()
        {
            super(AppendixB.QUAD_BANK_O2S22_WIDE_RANGE);
        }
    }

    /// GUI Wrapper for PID $28 (dual bank)
    private final static class DualBankO2S21WideRangeVoltageWrapper extends WideRangeVoltageOxygenSensorPIDWrapper
    {
        DualBankO2S21WideRangeVoltageWrapper()
        {
            super(AppendixB.DUAL_BANK_O2S21_WIDE_RANGE);
        }
    }

    /// GUI Wrapper for PID $28 (quad bank)
    private final static class QuadBankO2S31WideRangeVoltageWrapper extends WideRangeVoltageOxygenSensorPIDWrapper
    {
        QuadBankO2S31WideRangeVoltageWrapper()
        {
            super(AppendixB.QUAD_BANK_O2S31_WIDE_RANGE);
        }
    }

    /// GUI Wrapper for PID $29 (dual bank)
    private final static class DualBankO2S22WideRangeVoltageWrapper
            extends WideRangeVoltageOxygenSensorPIDWrapper
    {
        DualBankO2S22WideRangeVoltageWrapper()
        {
            super(AppendixB.DUAL_BANK_O2S22_WIDE_RANGE);
        }
    }

    /// GUI Wrapper for PID $29 (quad bank)
    private final static class QuadBankO2S32WideRangeVoltageWrapper
            extends WideRangeVoltageOxygenSensorPIDWrapper
    {
        QuadBankO2S32WideRangeVoltageWrapper()
        {
            super(AppendixB.QUAD_BANK_O2S32_WIDE_RANGE);
        }
    }

    /// GUI Wrapper for PID $2A (dual bank)
    private final static class DualBankO2S23WideRangeVoltageWrapper extends WideRangeVoltageOxygenSensorPIDWrapper
    {
        DualBankO2S23WideRangeVoltageWrapper()
        {
            super(AppendixB.DUAL_BANK_O2S23_WIDE_RANGE);
        }
    }

    /// GUI Wrapper for PID $2A (quad bank)
    private final static class QuadBankO2S41WideRangeVoltageWrapper extends WideRangeVoltageOxygenSensorPIDWrapper
    {
        QuadBankO2S41WideRangeVoltageWrapper()
        {
            super(AppendixB.QUAD_BANK_O2S41_WIDE_RANGE);
        }
    }

    /// GUI Wrapper for PID $2B (dual bank)
    private final static class DualBankO2S24WideRangeVoltageWrapper extends WideRangeVoltageOxygenSensorPIDWrapper
    {
        DualBankO2S24WideRangeVoltageWrapper()
        {
            super(AppendixB.DUAL_BANK_O2S24_WIDE_RANGE);
        }
    }

    /// GUI Wrapper for PID $2B (quad bank)
    private final static class QuadBankO2S42WideRangeVoltageWrapper extends WideRangeVoltageOxygenSensorPIDWrapper
    {
        QuadBankO2S42WideRangeVoltageWrapper()
        {
            super(AppendixB.QUAD_BANK_O2S42_WIDE_RANGE);
        }
    }

    /// GUI Wrapper for PID $2C
    private final static class CommandedEGRWrapper extends PIDWrapper<Float>
    {
        CommandedEGRWrapper()
        {
            super(AppendixB.COMMANDED_EGR);
        }
    }

    /// GUI Wrapper for PID $2D
    private final static class EGRErrorWrapper extends PIDWrapper<Float>
    {
        EGRErrorWrapper()
        {
            super(AppendixB.EGR_ERROR);
        }
    }

    /// GUI Wrapper for PID $2E
    private final static class CommandedEvaporativePurgeWrapper extends PIDWrapper<Float>
    {
        CommandedEvaporativePurgeWrapper()
        {
            super(AppendixB.COMMANDED_EVAPORATIVE_PURGE);
        }
    }

    /// GUI Wrapper for PID $2F
    private final static class FuelLevelInputWrapper extends PIDWrapper<Float>
    {
        FuelLevelInputWrapper()
        {
            super(AppendixB.FUEL_LEVEL_INPUT);
        }
    }

    /// GUI Wrapper for PID $30
    private final static class WarmUpsSinceDTCResetWrapper extends PIDWrapper<Integer>
    {
        WarmUpsSinceDTCResetWrapper()
        {
            super(AppendixB.WARM_UPS_SINCE_DTC_RESET);
        }
    }

    /// GUI Wrapper for PID $31
    private final static class DistanceTravelledSinceDTCResetWrapper extends PIDWrapper<Integer>
    {
        DistanceTravelledSinceDTCResetWrapper()
        {
            super(AppendixB.DISTANCE_TRAVELLED_SINCE_DTC_RESET);
        }

        @Override
        public ViewHolderBase createViewHolder(@NonNull View view)
        {
            super.addUnitSelector(view);
            return super.createViewHolder(view);
        }
    }

    /// GUI Wrapper for PID $32
    private final static class EvaporativeSystemVaporPressureWrapper extends PIDWrapper<Float>
    {
        EvaporativeSystemVaporPressureWrapper()
        {
            super(AppendixB.EVAP_SYSTEM_VAPOR_PRESSURE);
        }

        @Override
        public ViewHolderBase createViewHolder(@NonNull View view)
        {
            super.addUnitSelector(view);
            return super.createViewHolder(view);
        }
    }

    /// GUI Wrapper for PID $33
    private final static class BarometricPressureWrapper extends PIDWrapper<Float>
    {
        BarometricPressureWrapper()
        {
            super(AppendixB.BAROMETRIC_PRESSURE);
        }

        @Override
        public ViewHolderBase createViewHolder(@NonNull View view)
        {
            super.addUnitSelector(view);
            return super.createViewHolder(view);
        }
    }

    /// Base class for PIDs $34 - $3B
    private abstract static class WideRangeCurrentOxygenSensorPIDWrapper extends PIDWrapper<SerializablePair<Float, Float>>
    {

        private final static class ViewHolder extends ViewHolderBase
        {
            final TextView lambdaValue;
            final TextView currentValue;

            ViewHolder(View view)
            {
                this.lambdaValue = ButterKnife.findById(view, R.id.wide_range_oxygen_sensor_lambda_value);
                this.currentValue = ButterKnife.findById(view, R.id.wide_range_oxygen_sensor_voltage_value);
            }
        }

        WideRangeCurrentOxygenSensorPIDWrapper(PID<SerializablePair<Float, Float>> pid)
        {
            super(pid);
        }

        @Override
        public final ViewHolderBase createViewHolder(@NonNull final View view)
        {
            ViewHolder rv = new ViewHolder(view);
            ((TextView)ButterKnife.findById(view, R.id.wide_range_oxygen_sensor_label))
                    .setText(super.unwrap().getDisplayName());
            super.addUnitSelector(view);
            return rv;
        }

        @Override
        public final <U extends ViewHolderBase> void updateViewHolder(@NonNull U viewHolder, @NonNull SerializablePair<Float, Float> value)
        {
            ViewHolder vh = (ViewHolder)viewHolder;
            vh.lambdaValue.setText(String.valueOf(value.first));
            vh.currentValue.setText(String.valueOf(value.second));
        }

        @Override
        public final int getLayoutID()
        {
            return R.layout.wide_range_oxygen_sensor;
        }
    }

    /// GUI Wrapper for PID $34 (dual bank)
    private final static class DualBankO2S11WideRangeCurrentWrapper extends WideRangeCurrentOxygenSensorPIDWrapper
    {
        DualBankO2S11WideRangeCurrentWrapper()
        {
            super(AppendixB.DUAL_BANK_O2S11_WIDE_RANGE_ALT);
        }
    }

    /// GUI Wrapper for PID $34 (quad bank)
    private final static class QuadBankO2S11WideRangeCurrentWrapper extends WideRangeCurrentOxygenSensorPIDWrapper
    {
        QuadBankO2S11WideRangeCurrentWrapper()
        {
            super(AppendixB.QUAD_BANK_O2S11_WIDE_RANGE_ALT);
        }
    }

    /// GUI Wrapper for PID $35 (dual bank)
    private final static class DualBankO2S12WideRangeCurrentWrapper extends WideRangeCurrentOxygenSensorPIDWrapper
    {
        DualBankO2S12WideRangeCurrentWrapper()
        {
            super(AppendixB.DUAL_BANK_O2S12_WIDE_RANGE_ALT);
        }
    }

    /// GUI Wrapper for PID $35 (quad bank)
    private final static class QuadBankO2S12WideRangeCurrentWrapper extends WideRangeCurrentOxygenSensorPIDWrapper
    {
        QuadBankO2S12WideRangeCurrentWrapper()
        {
            super(AppendixB.QUAD_BANK_O2S12_WIDE_RANGE_ALT);
        }
    }

    /// GUI Wrapper for PID $36 (dual bank)
    private final static class DualBankO2S13WideRangeCurrentWrapper extends WideRangeCurrentOxygenSensorPIDWrapper
    {
        DualBankO2S13WideRangeCurrentWrapper()
        {
            super(AppendixB.DUAL_BANK_O2S13_WIDE_RANGE_ALT);
        }
    }

    /// GUI Wrapper for PID $36 (quad bank)
    private final static class QuadBankO2S21WideRangeCurrentWrapper extends WideRangeCurrentOxygenSensorPIDWrapper
    {
        QuadBankO2S21WideRangeCurrentWrapper()
        {
            super(AppendixB.QUAD_BANK_O2S21_WIDE_RANGE_ALT);
        }
    }

    /// GUI Wrapper for PID $37 (dual bank)
    private final static class DualBankO2S14WideRangeCurrentWrapper extends WideRangeCurrentOxygenSensorPIDWrapper
    {
        DualBankO2S14WideRangeCurrentWrapper()
        {
            super(AppendixB.DUAL_BANK_O2S14_WIDE_RANGE_ALT);
        }
    }

    /// GUI Wrapper for PID $37 (quad bank)
    private final static class QuadBankO2S22WideRangeCurrentWrapper extends WideRangeCurrentOxygenSensorPIDWrapper
    {
        QuadBankO2S22WideRangeCurrentWrapper()
        {
            super(AppendixB.QUAD_BANK_O2S22_WIDE_RANGE_ALT);
        }
    }

    /// GUI Wrapper for PID $38 (dual bank)
    private final static class DualBankO2S21WideRangeCurrentWrapper extends WideRangeCurrentOxygenSensorPIDWrapper
    {
        DualBankO2S21WideRangeCurrentWrapper()
        {
            super(AppendixB.DUAL_BANK_O2S21_WIDE_RANGE_ALT);
        }
    }

    /// GUI Wrapper for PID $38 (quad bank)
    private final static class QuadBankO2S31WideRangeCurrentWrapper extends WideRangeCurrentOxygenSensorPIDWrapper
    {
        QuadBankO2S31WideRangeCurrentWrapper()
        {
            super(AppendixB.QUAD_BANK_O2S31_WIDE_RANGE_ALT);
        }
    }

    /// GUI Wrapper for PID $39 (dual bank)
    private final static class DualBankO2S22WideRangeCurrentWrapper
            extends WideRangeCurrentOxygenSensorPIDWrapper
    {
        DualBankO2S22WideRangeCurrentWrapper()
        {
            super(AppendixB.DUAL_BANK_O2S22_WIDE_RANGE_ALT);
        }
    }

    /// GUI Wrapper for PID $39 (quad bank)
    private final static class QuadBankO2S32WideRangeCurrentWrapper
            extends WideRangeCurrentOxygenSensorPIDWrapper
    {
        QuadBankO2S32WideRangeCurrentWrapper()
        {
            super(AppendixB.QUAD_BANK_O2S32_WIDE_RANGE_ALT);
        }
    }

    /// GUI Wrapper for PID $3A (dual bank)
    private final static class DualBankO2S23WideRangeCurrentWrapper extends WideRangeCurrentOxygenSensorPIDWrapper
    {
        DualBankO2S23WideRangeCurrentWrapper()
        {
            super(AppendixB.DUAL_BANK_O2S23_WIDE_RANGE_ALT);
        }
    }

    /// GUI Wrapper for PID $3A (quad bank)
    private final static class QuadBankO2S41WideRangeCurrentWrapper extends WideRangeCurrentOxygenSensorPIDWrapper
    {
        QuadBankO2S41WideRangeCurrentWrapper()
        {
            super(AppendixB.QUAD_BANK_O2S41_WIDE_RANGE_ALT);
        }
    }

    /// GUI Wrapper for PID $3B (dual bank)
    private final static class DualBankO2S24WideRangeCurrentWrapper extends WideRangeCurrentOxygenSensorPIDWrapper
    {
        DualBankO2S24WideRangeCurrentWrapper()
        {
            super(AppendixB.DUAL_BANK_O2S24_WIDE_RANGE_ALT);
        }
    }

    /// GUI Wrapper for PID $3B (quad bank)
    private final static class QuadBankO2S42WideRangeCurrentWrapper extends WideRangeCurrentOxygenSensorPIDWrapper
    {
        QuadBankO2S42WideRangeCurrentWrapper()
        {
            super(AppendixB.QUAD_BANK_O2S42_WIDE_RANGE_ALT);
        }
    }

    /// GUI Wrapper for PID $3C
    private final static class CatalystTemperatureBank1Sensor1Wrapper extends PIDWrapper<Float>
    {
        CatalystTemperatureBank1Sensor1Wrapper()
        {
            super(AppendixB.CATALYST_TEMPERATURE_BANK_1_SENSOR_1);
        }

        @Override
        public ViewHolderBase createViewHolder(@NonNull View view)
        {
            super.addUnitSelector(view);
            return super.createViewHolder(view);
        }
    }

    /// GUI Wrapper for PID $3D
    private final static class CatalystTemperatureBank1Sensor2Wrapper extends PIDWrapper<Float>
    {
        CatalystTemperatureBank1Sensor2Wrapper()
        {
            super(AppendixB.CATALYST_TEMPERATURE_BANK_1_SENSOR_2);
        }

        @Override
        public ViewHolderBase createViewHolder(@NonNull View view)
        {
            super.addUnitSelector(view);
            return super.createViewHolder(view);
        }
    }

    /// GUI Wrapper for PID $3E
    private final static class CatalystTemperatureBank2Sensor1Wrapper extends PIDWrapper<Float>
    {
        CatalystTemperatureBank2Sensor1Wrapper()
        {
            super(AppendixB.CATALYST_TEMPERATURE_BANK_2_SENSOR_1);
        }

        @Override
        public ViewHolderBase createViewHolder(@NonNull View view)
        {
            super.addUnitSelector(view);
            return super.createViewHolder(view);
        }
    }

    /// GUI Wrapper for PID $3F
    private final static class CatalystTemperatureBank2Sensor2Wrapper extends PIDWrapper<Float>
    {
        CatalystTemperatureBank2Sensor2Wrapper()
        {
            super(AppendixB.CATALYST_TEMPERATURE_BANK_2_SENSOR_2);
        }

        @Override
        public ViewHolderBase createViewHolder(@NonNull View view)
        {
            super.addUnitSelector(view);
            return super.createViewHolder(view);
        }
    }

    /// GUI Wrapper for PID $41



    private final static List<Class<? extends PIDWrapper<?>>> DUAL_BANK_CONVENTIONAL_OXYGEN_SENSOR_PID_WRAPPERS
            = new ArrayList<Class<? extends PIDWrapper<?>>>()
    {
        {
            super.add(DualBankO2S11ConventionalWrapper.class);
            super.add(DualBankO2S12ConventionalWrapper.class);
            super.add(DualBankO2S13ConventionalWrapper.class);
            super.add(DualBankO2S14ConventionalWrapper.class);
            super.add(DualBankO2S21ConventionalWrapper.class);
            super.add(DualBankO2S22ConventionalWrapper.class);
            super.add(DualBankO2S23ConventionalWrapper.class);
            super.add(DualBankO2S24ConventionalWrapper.class);
        }
    };

    private final static List<Class<? extends PIDWrapper<?>>> QUAD_BANK_CONVENTIONAL_OXYGEN_SENSOR_PID_WRAPPERS
            = new ArrayList<Class<? extends PIDWrapper<?>>>()
    {
        {
            super.add(QuadBankO2S11ConventionalWrapper.class);
            super.add(QuadBankO2S12ConventionalWrapper.class);
            super.add(QuadBankO2S21ConventionalWrapper.class);
            super.add(QuadBankO2S22ConventionalWrapper.class);
            super.add(QuadBankO2S31ConventionalWrapper.class);
            super.add(QuadBankO2S32ConventionalWrapper.class);
            super.add(QuadBankO2S41ConventionalWrapper.class);
            super.add(QuadBankO2S42ConventionalWrapper.class);
        }
    };

    private final static List<Class<? extends PIDWrapper<?>>> DUAL_BANK_WIDE_RANGE_VOLTAGE_OXYGEN_SENSOR_PID_WRAPPERS
            = new ArrayList<Class<? extends PIDWrapper<?>>>()
    {
        {
            super.add(DualBankO2S11WideRangeVoltageWrapper.class);
            super.add(DualBankO2S12WideRangeVoltageWrapper.class);
            super.add(DualBankO2S13WideRangeVoltageWrapper.class);
            super.add(DualBankO2S14WideRangeVoltageWrapper.class);
            super.add(DualBankO2S21WideRangeVoltageWrapper.class);
            super.add(DualBankO2S22WideRangeVoltageWrapper.class);
            super.add(DualBankO2S23WideRangeVoltageWrapper.class);
            super.add(DualBankO2S24WideRangeVoltageWrapper.class);
        }
    };

    private final static List<Class<? extends PIDWrapper<?>>> QUAD_BANK_WIDE_RANGE_VOLTAGE_OXYGEN_SENSOR_PID_WRAPPERS
            = new ArrayList<Class<? extends PIDWrapper<?>>>()
    {
        {
            super.add(QuadBankO2S11WideRangeVoltageWrapper.class);
            super.add(QuadBankO2S12WideRangeVoltageWrapper.class);
            super.add(QuadBankO2S21WideRangeVoltageWrapper.class);
            super.add(QuadBankO2S22WideRangeVoltageWrapper.class);
            super.add(QuadBankO2S31WideRangeVoltageWrapper.class);
            super.add(QuadBankO2S32WideRangeVoltageWrapper.class);
            super.add(QuadBankO2S41WideRangeVoltageWrapper.class);
            super.add(QuadBankO2S42WideRangeVoltageWrapper.class);
        }
    };

    private final static List<Class<? extends PIDWrapper<?>>> DUAL_BANK_WIDE_RANGE_CURRENT_OXYGEN_SENSOR_PID_WRAPPERS
            = new ArrayList<Class<? extends PIDWrapper<?>>>()
    {
        {
            super.add(DualBankO2S11WideRangeCurrentWrapper.class);
            super.add(DualBankO2S12WideRangeCurrentWrapper.class);
            super.add(DualBankO2S13WideRangeCurrentWrapper.class);
            super.add(DualBankO2S14WideRangeCurrentWrapper.class);
            super.add(DualBankO2S21WideRangeCurrentWrapper.class);
            super.add(DualBankO2S22WideRangeCurrentWrapper.class);
            super.add(DualBankO2S23WideRangeCurrentWrapper.class);
            super.add(DualBankO2S24WideRangeCurrentWrapper.class);
        }
    };

    private final static List<Class<? extends PIDWrapper<?>>> QUAD_BANK_WIDE_RANGE_CURRENT_OXYGEN_SENSOR_PID_WRAPPERS
            = new ArrayList<Class<? extends PIDWrapper<?>>>()
    {
        {
            super.add(QuadBankO2S11WideRangeCurrentWrapper.class);
            super.add(QuadBankO2S12WideRangeCurrentWrapper.class);
            super.add(QuadBankO2S21WideRangeCurrentWrapper.class);
            super.add(QuadBankO2S22WideRangeCurrentWrapper.class);
            super.add(QuadBankO2S31WideRangeCurrentWrapper.class);
            super.add(QuadBankO2S32WideRangeCurrentWrapper.class);
            super.add(QuadBankO2S41WideRangeCurrentWrapper.class);
            super.add(QuadBankO2S42WideRangeCurrentWrapper.class);
        }
    };

    private final static List<Class<? extends PIDWrapper<?>>> PID_WRAPPERS = new ArrayList<Class<? extends PIDWrapper<?>>>()
    {
        {
            super.add(PidSupport_1_20.class);
            super.add(MonitorStatusWrapper.class);
            super.add(FreezeFrameDTCWrapper.class);
            super.add(FuelSystemStatusWrapper.class);
            super.add(CalculatedEngineLoadWrapper.class);
            super.add(EngineCoolantTemperatureWrapper.class);
            super.add(ShortTermFuelTrimBank_1_3_Wrapper.class);
            super.add(LongTermFuelTrimBank_1_3_Wrapper.class);
            super.add(ShortTermFuelTrimBank_2_4_Wrapper.class);
            super.add(LongTermFuelTrimBank_2_4_Wrapper.class);
            super.add(NarrowRangeFuelRailPressureRefAtmosphereWrapper.class);
            super.add(IntakeManifoldAbsolutePressureWrapper.class);
            super.add(EngineSpeedWrapper.class);
            super.add(VehicleSpeedWrapper.class);
            super.add(TimingAdvanceWrapper.class);
            super.add(IntakeAirTemperatureWrapper.class);
            super.add(MassAirflowRateWrapper.class);
            super.add(AbsoluteThrottlePositionWrapper.class);
            super.add(SecondaryAirStatusWrapper.class);
            super.add(OxygenSensorLocationDualBankWrapper.class);
            super.add(null); // $14
            super.add(null); // $15
            super.add(null); // $16
            super.add(null); // $17
            super.add(null); // $18
            super.add(null); // $19
            super.add(null); // $1A
            super.add(null); // $1B
            super.add(OBDSupportWrapper.class);
            super.add(OxygenSensorLocationQuadBankWrapper.class);
            super.add(AuxiliaryInputStatusWrapper.class);
            super.add(TimeSinceEngineStartWrapper.class);
            super.add(PidSupport_21_40.class);
            super.add(DistanceTravelledWithActiveMILWrapper.class);
            super.add(FuelRailPressureRefManifoldVacWrapper.class);
            super.add(WideRangeVoltageFuelRailPressureRefAtmosphere.class);
            super.add(null); // $24
            super.add(null); // $25
            super.add(null); // $26
            super.add(null); // $27
            super.add(null); // $28
            super.add(null); // $29
            super.add(null); // $2A
            super.add(null); // $2B
            super.add(CommandedEGRWrapper.class);
            super.add(EGRErrorWrapper.class);
            super.add(CommandedEvaporativePurgeWrapper.class);
            super.add(FuelLevelInputWrapper.class);
            super.add(WarmUpsSinceDTCResetWrapper.class);
            super.add(DistanceTravelledSinceDTCResetWrapper.class);
            super.add(EvaporativeSystemVaporPressureWrapper.class);
            super.add(BarometricPressureWrapper.class);
            super.add(null); // $34
            super.add(null); // $35
            super.add(null); // $36
            super.add(null); // $37
            super.add(null); // $38
            super.add(null); // $39
            super.add(null); // $3A
            super.add(null); // $3B
            super.add(CatalystTemperatureBank1Sensor1Wrapper.class);
            super.add(CatalystTemperatureBank1Sensor2Wrapper.class);
            super.add(CatalystTemperatureBank2Sensor1Wrapper.class);
            super.add(CatalystTemperatureBank2Sensor2Wrapper.class);
            super.add(PidSupport_41_60.class);

            super.add(PidSupport_61_80.class);
            super.add(PidSupport_81_A0.class);
            super.add(PidSupport_A1_C0.class);
            super.add(PidSupport_C1_E0.class);
            super.add(PidSupport_E1_FF.class);
        }
    };

    @SuppressWarnings("unchecked")
    public static <T extends PIDWrapper<?>> T getWrapper(@NonNull PID<?> pid, Profile profile)
    {
        try
        {
            /// Conventional Oxygen Sensor
            if(pid.getID() > 0x13 && pid.getID() < 0x1C)
            {
                if(profile.isEquipped(Profile.DUAL_BANK))
                {
                    return (T)DUAL_BANK_CONVENTIONAL_OXYGEN_SENSOR_PID_WRAPPERS.get(pid.getID() - 0x14).newInstance();
                }
                else if(profile.isEquipped(Profile.QUAD_BANK))
                {
                    return (T)QUAD_BANK_CONVENTIONAL_OXYGEN_SENSOR_PID_WRAPPERS.get(pid.getID() - 0x14).newInstance();
                }
                else
                {
                    throw new IllegalStateException();
                }
            }
            /// Wide Range Oxygen Sensor (lambda + voltage)
            else if(pid.getID() > 0x23 && pid.getID() < 0x2C)
            {
                if(profile.isEquipped(Profile.DUAL_BANK))
                {
                    return (T) DUAL_BANK_WIDE_RANGE_VOLTAGE_OXYGEN_SENSOR_PID_WRAPPERS.get(pid.getID() - 0x24).newInstance();
                }
                else if(profile.isEquipped(Profile.QUAD_BANK))
                {
                    return (T) QUAD_BANK_WIDE_RANGE_VOLTAGE_OXYGEN_SENSOR_PID_WRAPPERS.get(pid.getID() - 0x24).newInstance();
                }
                else
                {
                    throw new IllegalStateException();
                }
            }
            /// Wide Range Oxygen Sensor (lambda + current)
            else if(pid.getID() > 0x33 && pid.getID() < 0x3C)
            {
                if(profile.isEquipped(Profile.DUAL_BANK))
                {
                    return (T)DUAL_BANK_WIDE_RANGE_CURRENT_OXYGEN_SENSOR_PID_WRAPPERS.get(pid.getID() - 0x34).newInstance();
                }
                else if(profile.isEquipped(Profile.QUAD_BANK))
                {
                    return (T)QUAD_BANK_WIDE_RANGE_CURRENT_OXYGEN_SENSOR_PID_WRAPPERS.get(pid.getID() - 0x34).newInstance();
                }
                else
                {
                    throw new IllegalStateException();
                }
            }
            else
            {
                return (T) PID_WRAPPERS.get(pid.getID()).newInstance();
            }
        }
        catch(ClassCastException cce)
        {
            if(BuildConfig.DEBUG)
            {
                Log.wtf(TAG, "bad cast", cce);
            }
        }
        catch (IllegalAccessException | InstantiationException e)
        {
            if(BuildConfig.DEBUG)
            {
                Log.wtf(TAG, "No wrapper exists for : " + pid + " (This means you should make one.)", e);
            }
        }
        return null;
    }

    /// SAE J1979 Appendix B -->
}
