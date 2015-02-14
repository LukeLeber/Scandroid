// This file is protected under the KILLGPL.
// For more information, visit http://www.lukeleber.github.io/KILLGPL.html
//
// Copyright (c) Luke Leber <LukeLeber@gmail.com>

package com.lukeleber.scandroid.sae.j1979;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.lukeleber.scandroid.util.Unit;

import java.util.HashMap;
import java.util.Map;

/// TODO: Write Unit Tests
/**
 * The default concrete implementation of the {@link com.lukeleber.scandroid.sae.j1979.PID}
 * interface.
 *
 * @param <T>
 *         the data type that this PID should retrieve from a vehicle
 */
public class DefaultPID<T>
        extends
        AbstractPID<T>
{
    /// Required by the {@link android.os.Parcelable} interface
    public static final Parcelable.Creator<DefaultPID> CREATOR
            = new Parcelable.Creator<DefaultPID>()
    {

        /// Reconstructs the unmarshallers map that was written to the provided parcel
        private Map<Unit, Unmarshaller<?>> readUnmarshallers(Parcel in)
        {
            Map<Unit, Unmarshaller<?>> rv = new HashMap<>();
            int count = in.readByte();
            while (count-- > 0)
            {
                rv.put(in.<Unit>readParcelable(Unit.class.getClassLoader()),
                        /// TODO:
                        /// Generally, the java.io.Serializable mechanism is discouraged in
                        /// android, however it is required to persist the anonymous
                        /// unmarshallers classes (someday to be lambdas).

                        /// If anyone has a better way, I'm all ears.
                        (Unmarshaller<?>) in.readSerializable());
            }
            return rv;
        }

        /**
         * {@inheritDoc}
         *
         */
        @SuppressWarnings("unchecked")
        @Override
        public DefaultPID createFromParcel(Parcel in)
        {
            return new DefaultPID(in.readInt(), in.readString(), in.readString(), in.readString(), readUnmarshallers(in));
        }

        /**
         * {@inheritDoc}
         *
         */
        @Override
        public DefaultPID[] newArray(int size)
        {
            return new DefaultPID[size];
        }
    };

    /// The user-friendly display name of this <code>DefaultPID</code>
    private final String userFriendlyName;

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeInt(getID());
        dest.writeString(getDisplayName());
        dest.writeString(userFriendlyName);
        dest.writeString(getDescription());
        dest.writeByte((byte) getUnmarshallers().size());
        for (Map.Entry<Unit, Unmarshaller<T>> e : this)
        {
            dest.writeParcelable(e.getKey(), 0);
            /// TODO:
            /// Generally, the java.io.Serializable mechanism is discouraged in
            /// android, however it is required to persist the anonymous
            /// unmarshallers classes (someday to be lambdas).

            /// If anyone has a better way, I'm all ears.
            dest.writeSerializable(e.getValue());
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int describeContents()
    {
        return 0;
    }

    /**
     * {@inheritDoc}
     * @param context
     */
    @Override
    public String toI18NString(Context context)
    {
        return userFriendlyName;
    }

    @Override
    public boolean equals(Object rhs)
    {
        return rhs instanceof DefaultPID && this.getID() == ((DefaultPID)rhs).getID();
    }

    /**
     * Constructs a <cde>DefaultPID</cde> with the provided ID, display name, user friendly display
     * name, description, and {@link PID.Unmarshaller unmarshallers}.
     *
     * @param id
     *         the unique ISO/SAE/Manufacturer defined identification number
     * @param displayName
     *         the ISO/SAE/Manufacturer specified display name
     * @param userFriendlyName
     *         the user-friendly display name of this <code>DefaultPID</code>
     * @param description
     *         a brief description
     * @param unmarshallers
     *         the {@link PID.Unmarshaller unmarshallers} that are available for use with this
     *         <code>DefaultPID</code>
     */
    public DefaultPID(int id, String displayName, String userFriendlyName, String description,
                      Map<Unit, Unmarshaller<T>> unmarshallers)
    {
        super(id, displayName, description, unmarshallers);
        this.userFriendlyName = userFriendlyName;
    }

    /**
     * Constructs a <cde>DefaultPID</cde> with the provided ID, display name, description, and
     * {@link PID.Unmarshaller unmarshallers}.  Note - this constructor uses the ISO/SAE defined
     * display name as the user-friendly display name.
     *
     * @param id
     *         the unique ISO/SAE/Manufacturer defined identification number
     * @param displayName
     *         the ISO/SAE/Manufacturer specified display name
     * @param description
     *         a brief description
     * @param unmarshallers
     *         the {@link PID.Unmarshaller unmarshallers} that are available for use with this
     *         <code>DefaultPID</code>
     */
    public DefaultPID(int id, String displayName, String description,
                      Map<Unit, Unmarshaller<T>> unmarshallers)
    {
        this(id, displayName, displayName, description, unmarshallers);
    }
}
