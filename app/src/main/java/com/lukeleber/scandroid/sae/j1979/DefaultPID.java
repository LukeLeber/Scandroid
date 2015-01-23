/*
 * This file is protected under the KILLGPL.
 * For more information visit <insert_valid_link_to_killgpl_here>
 * <p/>
 * Copyright (c) Luke A. Leber <LukeLeber@gmail.com> 2014
 */

package com.lukeleber.scandroid.sae.j1979;

import android.os.Parcel;
import android.os.Parcelable;

import com.lukeleber.scandroid.util.Unit;

import java.util.Map;

public class DefaultPID<T>
        extends
        AbstractPID<T>
{
    public static final Parcelable.Creator<DefaultPID> CREATOR
            = new Parcelable.Creator<DefaultPID>()
    {
        @SuppressWarnings("unchecked")
        public DefaultPID createFromParcel(Parcel in)
        {
            return new DefaultPID(in.readInt(), in.readString(), in.readString(), in.readHashMap(
                    null));
        }

        public DefaultPID[] newArray(int size)
        {
            return new DefaultPID[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeInt(getID());
        dest.writeString(getDisplayName());
        dest.writeString(getDescription());
        dest.writeMap(getUnmarshallers());
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    public DefaultPID(int id, String displayName, String description,
                      Map<Unit, Unmarshaller<T>> unmarshallers)
    {
        super(id, displayName, description, unmarshallers);
    }
}
