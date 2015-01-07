/*
 * This file is protected under the KILLGPL.
 * For more information visit <insert_valid_link_to_killgpl_here>
 * <p/>
 * Copyright (c) Luke A. Leber <LukeLeber@gmail.com> 2014
 */

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
