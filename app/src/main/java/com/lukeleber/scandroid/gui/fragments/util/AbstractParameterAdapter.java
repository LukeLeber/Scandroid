// This file is protected under the KILLGPL.
// For more information, visit http://www.lukeleber.github.io/KILLGPL.html
//
// Copyright (c) Luke Leber <LukeLeber@gmail.com>
//
// "This software is provided freely under a single condition.  You can't force anyone
// that uses this software to release any source code.  It is their decision alone.
// Other than that, anything goes.
//
// PS. If you like this work, please hire me!  I'm uneducated (no CS degree)
// and as such it's borderline impossible to get into the industry.  My current
// career is absolute hell and I am trapped in it.  I leave this message here
// in the vain hope that someone, somewhere might read this and give me the
// opportunity to join their association as a programmer."
//
// Cheers,
// Luke

package com.lukeleber.scandroid.gui.fragments.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lukeleber.widget.GenericBaseAdapter;

/**
 * @internal A partial implementation of a PID adapter.  Simply override {@link
 * android.widget.BaseAdapter#getItem(int)} and {@link android.widget.BaseAdapter#getCount()} to
 * reflect the specifics of the data-set that is being adapted and apply this adapter to a view.
 */
public abstract class AbstractParameterAdapter
        extends GenericBaseAdapter<ParameterModel>
{
    /// The {@link LayoutInflater} to use for this adapter
    private final LayoutInflater inflater;

    /**
     * Constructs a <code>ParameterAdapter</code> with the provided context
     *
     * @param context
     *         the {@link android.content.Context} that this adapter is associated with
     */
    public AbstractParameterAdapter(Context context)
    {
        this.inflater = LayoutInflater.from(context);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getItemId(int position)
    {
        return position;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        /// Cache the layout its child views
        ParameterModel model = getItem(position);

        convertView = inflater.inflate(model.getPID()
                                            .getLayoutID(), null);
        convertView.setTag(model.getPID()
                                .createViewModel(convertView));
        model.getPID()
             .updateViewModel(convertView.getTag(), model.getLastKnownValue());
        return convertView;
    }
}
