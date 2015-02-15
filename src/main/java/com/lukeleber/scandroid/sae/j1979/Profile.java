// This file is protected under the KILLGPL.
// For more information, visit http://www.lukeleber.github.io/KILLGPL.html
//
// Copyright (c) Luke Leber <LukeLeber@gmail.com>

package com.lukeleber.scandroid.sae.j1979;

import android.os.Parcel;
import android.os.Parcelable;

import com.lukeleber.scandroid.interpreter.FailureCode;
import com.lukeleber.scandroid.interpreter.Handler;
import com.lukeleber.scandroid.interpreter.Interpreter;
import com.lukeleber.scandroid.interpreter.elm327.Protocol;
import com.lukeleber.scandroid.sae.j1979.detail.AppendixA;
import com.lukeleber.scandroid.sae.j1979.detail.AppendixB;
import com.lukeleber.scandroid.sae.j1979.util.CumulativePIDSupport;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/// TODO: Write Unit Tests
public class Profile
        implements
        Serializable,
        Parcelable
{

    public final static String DUAL_BANK = "dual_bank";

    public final static String QUAD_BANK = "quad_bank";

    private final static String[] EQUIPMENT_KEYS = new String[]
    {
        DUAL_BANK,
        QUAD_BANK
    };

    private final Protocol protocol;

    private final Map<String, Boolean> equipmentCache;

    private final Map<Service, PID<?>[]> pids;

    public Profile(Protocol protocol,
                   Map<Service, CumulativePIDSupport> supportedPIDs)
    {
        this.protocol = protocol;
        this.equipmentCache = new HashMap<>();
        this.pids = new TreeMap<>();
        for(Service service : Service.values())
        {
            if(supportedPIDs.get(service) != null)
            {
                pids.put(service, new PID[255]);
            }
            else
            {
                pids.put(service, null);
            }
        }
        if(supportedPIDs.get(Service.LIVE_DATASTREAM) != null)
        {
            populateService01(supportedPIDs.get(Service.LIVE_DATASTREAM));
        }
        if(supportedPIDs.get(Service.FREEZE_FRAME_DATA) != null)
        {
            populateService02(supportedPIDs.get(Service.FREEZE_FRAME_DATA));
        }
        populateEquipment();
    }

    public Protocol getProtocol()
    {
        return protocol;
    }

    private void populateEquipment()
    {
        equipmentCache.put(DUAL_BANK, isSupported(Service.LIVE_DATASTREAM,
                AppendixB.DUAL_BANK_OXYGEN_SENSOR_LOCATIONS.getID()));
        equipmentCache.put(QUAD_BANK, isSupported(Service.LIVE_DATASTREAM,
                AppendixB.QUAD_BANK_OXYGEN_SENSOR_LOCATIONS.getID()));
    }

    private void populateService01(CumulativePIDSupport support)
    {
        {
            addIfSupported(Service.LIVE_DATASTREAM, AppendixA.J1979_CHECK_PID_SUPPORT_1_TO_20,
                    support);
            addIfSupported(Service.LIVE_DATASTREAM, AppendixA.J1979_CHECK_PID_SUPPORT_21_TO_40,
                    support);
            addIfSupported(Service.LIVE_DATASTREAM, AppendixA.J1979_CHECK_PID_SUPPORT_41_TO_60,
                    support);
            addIfSupported(Service.LIVE_DATASTREAM, AppendixA.J1979_CHECK_PID_SUPPORT_61_TO_80,
                    support);
            addIfSupported(Service.LIVE_DATASTREAM, AppendixA.J1979_CHECK_PID_SUPPORT_81_TO_A0,
                    support);
            addIfSupported(Service.LIVE_DATASTREAM, AppendixA.J1979_CHECK_PID_SUPPORT_A1_TO_C0,
                    support);
            addIfSupported(Service.LIVE_DATASTREAM, AppendixA.J1979_CHECK_PID_SUPPORT_C1_TO_E0,
                    support);
            addIfSupported(Service.LIVE_DATASTREAM, AppendixA.J1979_CHECK_PID_SUPPORT_E1_TO_FF,
                    support);

            /// Appendix B
            for(PID<?> pid : SAE_J1979.SAE_J1979_STATIC_PIDS)
            {
                addIfSupported(Service.LIVE_DATASTREAM, pid, support);
            }

        }
    }

    private void populateService02(CumulativePIDSupport support)
    {
        addIfSupported(Service.FREEZE_FRAME_DATA, AppendixA.J1979_CHECK_PID_SUPPORT_1_TO_20,
                support);
        addIfSupported(Service.FREEZE_FRAME_DATA, AppendixA.J1979_CHECK_PID_SUPPORT_21_TO_40,
                support);
        addIfSupported(Service.FREEZE_FRAME_DATA, AppendixA.J1979_CHECK_PID_SUPPORT_41_TO_60,
                support);
        addIfSupported(Service.FREEZE_FRAME_DATA, AppendixA.J1979_CHECK_PID_SUPPORT_61_TO_80,
                support);
        addIfSupported(Service.FREEZE_FRAME_DATA, AppendixA.J1979_CHECK_PID_SUPPORT_81_TO_A0,
                support);
        addIfSupported(Service.FREEZE_FRAME_DATA, AppendixA.J1979_CHECK_PID_SUPPORT_A1_TO_C0,
                support);
        addIfSupported(Service.FREEZE_FRAME_DATA, AppendixA.J1979_CHECK_PID_SUPPORT_C1_TO_E0,
                support);
        addIfSupported(Service.FREEZE_FRAME_DATA, AppendixA.J1979_CHECK_PID_SUPPORT_E1_TO_FF,
                support);
        for(PID<?> pid : SAE_J1979.SAE_J1979_STATIC_PIDS)
        {
            addIfSupported(Service.FREEZE_FRAME_DATA, pid, support);
        }
    }

    private void addIfSupported(Service service,
                                PID<?> pid,
                                CumulativePIDSupport support)
    {
        if(support.isSupported(pid.getID()))
        {
            pids.get(service)[pid.getID()] = pid;
        }
    }

    public static void createProfile(final Interpreter interpreter,
                                     final Protocol protocol,
                                     final Handler<Profile> listener)
    {
        final Map<Service, CumulativePIDSupport> serviceMap = new TreeMap<>();
        final int[] services = new int[] {0, 1, 4, 5, 7, 8};
        /// TODO: Check support for service $03, $04, and $07
        /// TODO: But for now, just assume support (I guess...)
        CumulativePIDSupport.getSupportedPIDs(Service.values()[0], interpreter,
                new Handler<CumulativePIDSupport>()
                {

                    int currentService = 0;

                    @Override
                    public void onResponse(CumulativePIDSupport value)
                    {
                        serviceMap.put(
                                Service.values()[services[currentService++]],
                                value);
                        if(currentService < services.length)
                        {
                            CumulativePIDSupport.getSupportedPIDs(
                                    Service.values()[services[currentService]],
                                    interpreter,
                                    this);
                        }
                        else
                        {
                            listener.onResponse(new Profile(protocol,
                                    serviceMap));
                        }
                    }

                    @Override
                    public void onFailure(FailureCode code)
                    {
                        if(code ==
                           FailureCode.REQUEST_NOT_SUPPORTED)
                        {
                            serviceMap.put(
                                    Service.values()[services[currentService++]],
                                    null);
                            if(currentService <
                               services.length)
                            {
                                CumulativePIDSupport.getSupportedPIDs(
                                        Service.values()[services[currentService]],
                                        interpreter,
                                        this);
                            }
                            else
                            {
                                listener.onResponse(new Profile(protocol,
                                        serviceMap));
                            }
                        }
                        else
                        {
                            listener.onFailure(code);
                        }
                    }
                });

    }

    public PID<?> getID(Service service,
                        int id)
    {
        PID<?>[] pids = this.pids.get(service);
        if(pids != null)
        {
            return pids[id];
        }
        return null;
    }

    public boolean isServiceSupported(Service service)
    {
        /// FIXME: Read ELM327 Docs
        if(service == Service.RETRIEVE_DTC || service == Service.CLEAR_DTC ||
           service == Service.RETRIEVE_PENDING_DTC)
        {
            return true;
        }
        return pids.get(service) != null;
    }

    public boolean isSupported(Service service,
                               int id)
    {
        /// FIXME: Read ELM327 Docs
        if(service == Service.RETRIEVE_DTC || service == Service.CLEAR_DTC ||
           service == Service.RETRIEVE_PENDING_DTC)
        {
            return true;
        }
        return isServiceSupported(service) && pids.get(service)[id] != null;
    }

    public boolean isEquipped(String key)
    {
        Boolean rv = equipmentCache.get(key);
        return rv != null && rv;
    }


    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out,
                              int flags)
    {
        out.writeParcelable(protocol, protocol.describeContents());
        boolean[] cache = new boolean[EQUIPMENT_KEYS.length];
        {
            int i = 0;
            for(String key : EQUIPMENT_KEYS)
            {
                Boolean val = equipmentCache.get(key);
                cache[i++] = val != null && val;
            }
        }
        out.writeBooleanArray(cache);
        out.writeByte((byte)pids.size());
        for(Map.Entry<Service, PID<?>[]> entry : pids.entrySet())
        {
            out.writeParcelable(entry.getKey(), 0);
            out.writeParcelableArray(entry.getValue(), 0);
        }
    }

    private Profile(Protocol protocol, boolean[] cache, Map<Service, PID<?>[]> pids)
    {
        this.protocol = protocol;
        this.equipmentCache = new HashMap<>();
        {
            int i = 0;
            for(String key : EQUIPMENT_KEYS)
            {
                equipmentCache.put(key, cache[i++]);
            }
        }
        this.pids = pids;
    }

    public final static Parcelable.Creator<Profile> CREATOR =
            new Parcelable.Creator<Profile>()
            {

                @Override
                public Profile createFromParcel(Parcel in)
                {
                    Protocol protocol = in.readParcelable(null);
                    boolean[] cache = new boolean[EQUIPMENT_KEYS.length];
                    in.readBooleanArray(cache);
                    Map<Service, PID<?>[]> pids = new TreeMap<>();
                    int i = in.readByte() & 0xFF;
                    while(i-- > 0)
                    {
                        pids.put((Service)in.readParcelable(null), (PID<?>[])in.readParcelableArray(null));
                    }
                    return new Profile(protocol, cache, pids);
                }

                @Override
                public Profile[] newArray(int length)
                {
                    return new Profile[length];
                }
            };
}
