// This file is protected under the KILLGPL.
// For more information, visit http://www.lukeleber.github.io/KILLGPL.html
//
// Copyright (c) Luke Leber <LukeLeber@gmail.com>

package com.lukeleber.scandroid.sae.j1979;

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
