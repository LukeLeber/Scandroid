/*
 * This file is protected under the KILLGPL.
 * For more information visit <insert_valid_link_to_killgpl_here>
 * <p/>
 * Copyright (c) Luke A. Leber <LukeLeber@gmail.com> 2014
 */

package com.lukeleber.scandroid.sae;

public abstract class AbstractServiceFacet
        implements ServiceFacet
{
    private final int id;
    private final String displayName;
    private final String description;

    protected AbstractServiceFacet(int id, String displayName, String description)
    {
        this.id = id;
        this.displayName = displayName;
        this.description = description;
    }

    @Override
    public final int getID()
    {
        return id;
    }

    @Override
    public final String getDisplayName()
    {
        return displayName;
    }

    @Override
    public final String getDescription()
    {
        return description;
    }
}
