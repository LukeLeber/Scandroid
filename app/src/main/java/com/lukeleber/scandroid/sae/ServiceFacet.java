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

package com.lukeleber.scandroid.sae;

import android.os.Parcelable;

import java.io.Serializable;

/**
 * The ultimate base class for all PIDs, TIDs, OBDMIDs, and INFOTYPEs.
 */
public interface ServiceFacet
        extends
        Comparable<ServiceFacet>,
        Serializable,
        Parcelable
{

    /**
     * Retrieves the unique identification number of this ServiceFacet
     *
     * @return
     */
    int getID();

    /**
     * Retrieves the user-friendly display name of this ServiceFacet.  It can be assumed that this
     * string may be displayed to the user in a GUI.
     *
     * @return the user-friendly display name of this ServiceFacet
     */
    String getDisplayName();

    /**
     * Retrieves a brief description of this ServiceFacet.
     *
     * @return a brief description of this ServiceFacet.
     */
    String getDescription();


}
