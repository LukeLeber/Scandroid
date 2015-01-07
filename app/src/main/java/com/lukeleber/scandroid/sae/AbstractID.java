/*
 * This file is protected under the KILLGPL.
 * For more information visit <insert_valid_link_to_killgpl_here>
 * <p/>
 * Copyright (c) Luke A. Leber <LukeLeber@gmail.com> 2014
 */

package com.lukeleber.scandroid.sae;

public abstract class AbstractID
        implements ServiceFacet
{
    private final int id;
    private final String displayName;
    private final String description;

    protected AbstractID(int id, String displayName, String description)
    {
        this.id = id;
        this.displayName = displayName;
        this.description = description;
    }

    @Override
    public int getID()
    {
        return id;
    }

    @Override
    public String getDisplayName()
    {
        return displayName;
    }

    @Override
    public String getDescription()
    {
        return description;
    }
}
