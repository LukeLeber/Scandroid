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

import android.view.View;
import android.widget.TextView;

import com.lukeleber.scandroid.R;
import com.lukeleber.scandroid.util.Unit;

import java.util.Map;

public abstract class AbstractPID<T>
        extends AbstractServiceFacet
        implements PID<T>
{

    private final Unit defaultUnit;
    private final Unmarshaller<T> defaultUnmarshaller;
    private final Map<Unit, Unmarshaller<T>> unmarshallers;
    private final int layoutID;

    protected final Map<Unit, Unmarshaller<T>> getUnmarshallers()
    {
        return unmarshallers;
    }

    @Override
    public final int compareTo(ServiceFacet rhs)
    {
        return getID() - rhs.getID();
    }

    private final static class DefaultModelView
    {
        TextView name;
        TextView value;
    }

    protected AbstractPID(int id, String displayName, String description,
                          Map<Unit, Unmarshaller<T>> unmarshallers)
    {
        super(id, displayName, description);
        this.unmarshallers = unmarshallers;
        Map.Entry<Unit, Unmarshaller<T>> defaults = unmarshallers.entrySet()
                                                                 .iterator()
                                                                 .next();
        this.defaultUnit = defaults.getKey();
        this.defaultUnmarshaller = defaults.getValue();
        this.layoutID = R.layout.default_pid_layout;
    }

    protected AbstractPID(int id, String displayName, String description,
                          Map<Unit, Unmarshaller<T>> unmarshallers, int layoutID)
    {
        super(id, displayName, description);
        this.unmarshallers = unmarshallers;
        Map.Entry<Unit, Unmarshaller<T>> defaults = unmarshallers.entrySet()
                                                                 .iterator()
                                                                 .next();
        this.defaultUnit = defaults.getKey();
        this.defaultUnmarshaller = defaults.getValue();
        this.layoutID = layoutID;
    }

    @Override
    public final Unit getDefaultUnit()
    {
        return defaultUnit;
    }

    @Override
    public final Unmarshaller<T> getDefaultUnmarshaller()
    {
        return defaultUnmarshaller;
    }

    @Override
    public final Unmarshaller<T> getUnmarshallerForUnit(Unit unit)
    {
        return unmarshallers.get(unit);
    }

    @Override
    public int getLayoutID()
    {
        return layoutID;
    }

    @Override
    public Object createViewModel(View view)
    {
        DefaultModelView dmv = new DefaultModelView();
        dmv.name = (TextView) view.findViewById(R.id.default_pid_layout_pid_name);
        dmv.name.setText(this.getDisplayName());
        dmv.value = (TextView) view.findViewById(R.id.default_pid_layout_pid_value);
        return dmv;
    }

    @Override
    public void updateViewModel(Object view, Object value)
    {
        try
        {
            DefaultModelView dmv = (DefaultModelView) view;
            dmv.name.setText(this.getDisplayName());
            dmv.value.setText(value != null ? value.toString() : "N/A");
        }
        catch(ClassCastException cce)
        {
            System.out.println("Updating model view for " + getDisplayName());
        }
    }
}
