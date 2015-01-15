/*
 * This file is protected under the KILLGPL.
 * For more information visit <insert_valid_link_to_killgpl_here>
 * <p/>
 * Copyright (c) Luke A. Leber <LukeLeber@gmail.com> 2015
 */

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
