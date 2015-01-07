/*
 * This file is protected under the KILLGPL.
 * For more information visit <insert_valid_link_to_killgpl_here>
 * <p/>
 * Copyright (c) Luke A. Leber <LukeLeber@gmail.com> 2014
 */

package com.lukeleber.scandroid.sae;

import android.os.Parcel;
import android.os.Parcelable;

import com.lukeleber.scandroid.util.Unit;

import java.util.Map;

public class DefaultPID<T>
        extends
        AbstractPID<T>
{
    private final int responseLength;

    public static final Parcelable.Creator<AbstractPID> CREATOR
            = new Parcelable.Creator<AbstractPID>()
    {
        public AbstractPID createFromParcel(Parcel in)
        {
            return new DefaultPID(in.readInt(), in.readString(), in.readString(), in.readHashMap(null), in.readInt(), in.readInt());
        }

        public AbstractPID[] newArray(int size)
        {
            return new AbstractPID[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeInt(getID());
        dest.writeString(getDisplayName());
        dest.writeString(getDescription());
        dest.writeMap(getUnmarshallers());
        try
        {
            dest.writeInt(getResponseLength(null));
        }
        catch(Exception e)
        {
            dest.writeInt(0);
        }
        dest.writeInt(getLayoutID());
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    public DefaultPID(int id, String displayName, String description, Map<Unit, Unmarshaller<T>> unmarshallers, int responseLength)
    {
        super(id, displayName, description, unmarshallers);
        this.responseLength = responseLength;
    }

    public DefaultPID(int id, String displayName, String description, Map<Unit, Unmarshaller<T>> unmarshallers, int responseLength, int layoutID)
    {
        super(id, displayName, description, unmarshallers, layoutID);
        this.responseLength = responseLength;
    }

    @Override
    public int getResponseLength(Profile profile)
    {
        return responseLength;
    }
}
