// This file is protected under the KILLGPL.
// For more information, visit http://www.lukeleber.github.io/KILLGPL.html
//
// Copyright (c) Luke Leber <LukeLeber@gmail.com>

package com.lukeleber.scandroid.sae.j1979;

import android.os.Parcelable;

import com.lukeleber.scandroid.util.Internationalized;

import java.io.Serializable;

/**
 * The ultimate base class for all PIDs, TIDs, OBDMIDs, and INFOTYPEs defined by SAE J1979.  All
 * PID/TID/OBDMID/INFOTYPE objects are stateless objects that simply define the request format,
 * bit/scaling algorithm(s), and response formatting.
 *
 * @see java.lang.Comparable
 * @see java.io.Serializable
 * @see android.os.Parcelable
 * @see com.lukeleber.scandroid.util.Internationalized
 */
public interface ServiceFacet
        extends
        Comparable<ServiceFacet>,
        Serializable,
        Parcelable,
        Internationalized
{

    /**
     * Retrieves the unique ISO/SAE/Manufacturer defined identification number of this ServiceFacet
     *
     * @return the unique ISO/SAE/Manufacturer defined identification number of this ServiceFacet
     */
    int getID();

    /**
     * Retrieves the ISO/SAE/Manufacturer specified display name of this ServiceFacet.  It can be
     * assumed that this string may be displayed to the user in a GUI.  There exists an option to
     * retrieve a more user-friendly version of the display name through the use of
     * {@link com.lukeleber.scandroid.util.Internationalized#toI18NString(android.content.Context)}.
     *
     * @return the ISO/SAE/Manufacturer specified display name of this ServiceFacet
     */
    String getDisplayName();

    /**
     * <p>Retrieves a brief description of this ServiceFacet.  Note - descriptions are not
     * explicitly required to be displayed to users, and may not use the wording of the standard in
     * verbatim.  This method can be thought of as a non-standard extension of J1979 that can be
     * safely ignored if strict compatibility is required.</p>
     * <p/>
     * <p>That being said, I felt that the descriptions should be available so that if users want to
     * gain a deeper understanding of onboard diagnostics, the information would be available in a
     * "non-standardese" dialect.  "Standardese" is hard enough for implementers to understand
     * sometimes, let alone end-users!</p>
     *
     * @return a brief description of this ServiceFacet
     */
    String getDescription();

}
