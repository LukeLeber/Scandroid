// This file is protected under the KILLGPL.
// For more information, visit http://www.lukeleber.github.io/KILLGPL.html
//
// Copyright (c) Luke Leber <LukeLeber@gmail.com>

package com.lukeleber.scandroid.gui.fragments.detail;


import android.os.Parcelable;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.View;

import com.lukeleber.scandroid.gui.fragments.util.ViewHolderBase;
import com.lukeleber.scandroid.sae.j1979.ServiceFacet;

import java.io.Serializable;

/**
 * A common base class for all classes that act to wrap the {@link com.lukeleber.scandroid.sae.j1979.ServiceFacet}
 * for use in a GUI.  Generally speaking, all concrete implementations of this class will usually be
 * found within {@link android.widget.ListView list views} and as such this class provides methods
 * to make use of the view holder pattern.
 *
 * @param <T>
 *         The data type associated with the current value of the wrapped {@link
 *         com.lukeleber.scandroid.sae.j1979.ServiceFacet}
 */
@SuppressWarnings("unused")
public abstract class ServiceFacetWrapper<T extends Serializable>
        implements Comparable<ServiceFacetWrapper<T>>,
                   Parcelable,
                   Serializable
{

    /// The {@link ServiceFacet} that is being wrapped
    private final ServiceFacet facet;

    /**
     * Constructs a {@link ServiceFacetWrapper} around the provided {@link
     * com.lukeleber.scandroid.sae.j1979.ServiceFacet}
     *
     * @param facet
     *         the {@link com.lukeleber.scandroid.sae.j1979.ServiceFacet} to wrap
     */
    public ServiceFacetWrapper(@NonNull ServiceFacet facet)
    {
        this.facet = facet;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int compareTo(@NonNull ServiceFacetWrapper<T> other)
    {
        return this.facet.compareTo(other.facet);
    }

    /**
     * Retrieves the {@link com.lukeleber.scandroid.sae.j1979.ServiceFacet} that is being wrapped
     *
     * @return the {@link com.lukeleber.scandroid.sae.j1979.ServiceFacet} that is being wrapped
     */
    public
    @NonNull
    ServiceFacet unwrap()
    {
        return facet;
    }

    /**
     * Retrieves the layout ID to use for this wrapper
     *
     * @return the layout ID to use for this wrapper
     */
    public abstract
    @LayoutRes
    int getLayoutID();

    /**
     * Creates a view holder object that is attached to a member {@link android.view.View} of a
     * {@link android.widget.ListView}.  This is an effort to prevent redundant layout inflation and
     * to provide smoother scrolling through said list.
     *
     * @param view
     *         the {@link android.view.View} to create a holder for
     *
     * @return a view holder object
     */
    public abstract ViewHolderBase createViewHolder(@NonNull View view);

    /**
     * Updates the provided view holder object with the provided value
     *
     * @param viewHolder
     *         the view holder object to update
     * @param value
     *         the value to update with
     */
    public abstract void updateViewHolder(@NonNull
                                          ViewHolderBase viewHolder,
                                          @NonNull T value);

}
