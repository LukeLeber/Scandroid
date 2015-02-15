// This file is protected under the KILLGPL.
// For more information, visit http://www.lukeleber.github.io/KILLGPL.html
//
// Copyright (c) Luke Leber <LukeLeber@gmail.com>

package com.lukeleber.scandroid.gui.fragments.detail;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Parcel;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.lukeleber.scandroid.R;
import com.lukeleber.scandroid.gui.fragments.util.ViewHolderBase;
import com.lukeleber.scandroid.sae.j1979.PID;
import com.lukeleber.scandroid.util.Unit;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;

public class PIDWrapper<T extends Serializable> extends ServiceFacetWrapper<T>
{

    protected final static class DisplayUnitSelector<T extends Serializable> implements View.OnClickListener
    {
        private final PIDWrapper<T> wrapper;
        private final String[] units;
        DisplayUnitSelector(PIDWrapper<T> wrapper)
        {
            this.wrapper = wrapper;
            List<String> units = new ArrayList<>();
            for(Map.Entry<Unit, PID.Unmarshaller<T>> entry : wrapper.unwrap())
            {
                units.add(entry.getKey().name());
            }
            this.units = units.toArray(new String[units.size()]);
        }

        @Override
        public void onClick(View v)
        {
            new AlertDialog.Builder(v.getContext())
                    .setTitle(R.string.select_display_unit)
                    .setItems(units, new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            wrapper.setDisplayUnit(Unit.valueOf(units[which]));
                        }
                    })
                    .create()
                    .show();
        }
    }

    protected final static class DefaultViewHolder
            extends ViewHolderBase
    {
        final TextView displayName;
        final TextView value;

        DefaultViewHolder(View view)
        {
            this.displayName = ButterKnife.findById(view, R.id.default_pid_layout_pid_name);
            this.value = ButterKnife.findById(view, R.id.default_pid_layout_pid_value);
        }
    }

    private Unit displayUnit = unwrap().getDefaultUnit();

    public Unit getDisplayUnit()
    {
        return displayUnit;
    }

    public void setDisplayUnit(Unit displayUnit)
    {
        if(unwrap().getUnmarshallerForUnit(displayUnit) == null)
        {
            throw new IllegalArgumentException("No unmarshaller exists for " + displayUnit);
        }
        this.displayUnit = displayUnit;
    }

    public PIDWrapper(PID<T> facet)
    {
        super(facet);
    }

    @SuppressWarnings("unchecked")
    @Override
    public final @NonNull PID<T> unwrap()
    {
        return (PID<T>)super.unwrap();
    }

    @Override
    public int getLayoutID()
    {
        return R.layout.default_pid_layout;
    }

    @Override
    public ViewHolderBase createViewHolder(@NonNull View view)
    {
        DefaultViewHolder rv = new DefaultViewHolder(view);
        rv.displayName.setText(unwrap().getDisplayName());
        return rv;
    }

    protected final void addUnitSelector(@NonNull View view)
    {
        if(unwrap().getUnmarshallerCount() > 1)
        {
            view.setOnClickListener(new DisplayUnitSelector<>(this));
        }
    }

    @Override
    public void updateViewHolder(@NonNull ViewHolderBase viewHolder, @NonNull T value)
    {
        ((DefaultViewHolder)viewHolder).value.setText(value.toString() + " " +  displayUnit.toString());
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {

    }
}