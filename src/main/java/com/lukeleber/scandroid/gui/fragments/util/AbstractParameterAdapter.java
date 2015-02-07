// This file is protected under the KILLGPL.
// For more information, visit http://www.lukeleber.github.io/KILLGPL.html
//
// Copyright (c) Luke Leber <LukeLeber@gmail.com>

package com.lukeleber.scandroid.gui.fragments.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lukeleber.scandroid.R;
import com.lukeleber.widget.GenericBaseAdapter;

/**
 * A partial implementation of a PID adapter.  Simply override {@link
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
    @SuppressWarnings("unchecked")
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ///todo...why are the units changing faster than the values...?
        /// Cache the layout its child views
        ParameterModel model = getItem(position);
        int layoutID = model.getPID().getLayoutID();
        //if(convertView == null || layoutID != (int)convertView.getTag(R.id.abstract_parameter_adapter_layout_id))
        //{
            convertView = inflater.inflate(model.getPID()
                                                .getLayoutID(), null);
            convertView.setBackgroundColor((position % 2 == 0) ? 0xFFABABAB : 0xFFBABABA);
            convertView.setTag(R.id.abstract_parameter_adapter_layout_id, layoutID);
            convertView.setTag(R.id.abstract_parameter_adapter_view_holder_id, model.getPID()
                                    .createViewHolder(convertView));
        //}
        if(model.getLastKnownValue() != null)
        {
            model.getPID()
                 .updateViewHolder((ViewHolderBase) convertView.getTag(R.id.abstract_parameter_adapter_view_holder_id), model.getLastKnownValue());
        }
        return convertView;
    }
}
