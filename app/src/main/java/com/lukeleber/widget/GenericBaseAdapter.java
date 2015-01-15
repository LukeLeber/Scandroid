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

package com.lukeleber.widget;

import android.widget.BaseAdapter;

/**
 * <p>An alternative implementation of the {@link android.widget.Adapter}, {@link
 * android.widget.ListAdapter}, and {@link android.widget.SpinnerAdapter} interfaces without support
 * for {@link android.database.DataSetObserver observers}.  Additionally, this type of adapter is
 * generic, shadowing the method defined in its super-interface, which allows for increased
 * type-safety.</p> <p/> <p>The documentation has also been enhanced to enforce a stricter contract.
 * Particularly exceptional circumstances are explicitly documented such that any concrete
 * implementation is bound to follow suit.  This should give a better idea of how to handle bad
 * input.</p>
 *
 * @param <T>
 *         The type of data that is being adapted
 */
public abstract class GenericBaseAdapter<T>
        extends BaseAdapter
{
    /**
     * Retrieves the data item associated with the specified position in the data set.
     *
     * @param position
     *         Position of the item whose data we want within the adapter's data set.
     *
     * @return The data at the specified position.
     *
     * @throws IllegalArgumentException
     *         if the provided position is negative.
     * @throws IndexOutOfBoundsException
     *         if the provided position lies outside of the bounds of the data set.
     */
    public abstract T getItem(int position);
}
