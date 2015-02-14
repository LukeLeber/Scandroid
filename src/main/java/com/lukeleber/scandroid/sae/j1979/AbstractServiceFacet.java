// This file is protected under the KILLGPL.
// For more information, visit http://www.lukeleber.github.io/KILLGPL.html
//
// Copyright (c) Luke Leber <LukeLeber@gmail.com>

package com.lukeleber.scandroid.sae.j1979;

/**
 * A partial, skeletal implementation of the
 * {@link com.lukeleber.scandroid.sae.j1979.ServiceFacet} interface that provides access to the
 * basic immutable members.
 *
 * @see com.lukeleber.scandroid.sae.j1979.ServiceFacet
 *
 * @see java.io.Serializable
 *
 * @see android.os.Parcelable
 *
 * @see com.lukeleber.scandroid.util.Internationalized
 *
 */
public abstract class AbstractServiceFacet
        implements ServiceFacet
{
    /// The unique ISO/SAE/Manufacturer defined identification number of this AbstractServiceFacet
    private final int id;

    /// The ISO/SAE/Manufacturer specified display name of this AbstractServiceFacet
    private final String displayName;

    /// A brief description of this AbstractServiceFacet
    private final String description;

    /**
     * {@inheritDoc}
     *
     */
    @Override
    public final int getID()
    {
        return id;
    }

    /**
     * {@inheritDoc}
     *
     */
    @Override
    public final String getDisplayName()
    {
        return displayName;
    }

    /**
     * {@inheritDoc}
     *
     */
    @Override
    public final String getDescription()
    {
        return description;
    }

    /**
     * Constructs an <code>AbstractServiceFacet</code>
     * @param id the unique ISO/SAE/Manufacturer defined identification number
     *
     * @param displayName the ISO/SAE/Manufacturer specified display name
     *
     * @param description a brief description of this <code>AbstractServiceFacet</code>
     *
     */
    protected AbstractServiceFacet(int id, String displayName, String description)
    {
        this.id = id;
        this.displayName = displayName;
        this.description = description;
    }
}

