// This file is protected under the KILLGPL.
// For more information, visit http://www.lukeleber.github.io/KILLGPL.html
//
// Copyright (c) Luke Leber <LukeLeber@gmail.com>

package com.lukeleber.scandroid.sae.j1979;

import com.lukeleber.scandroid.interpreter.FailureCode;
import com.lukeleber.scandroid.interpreter.Handler;
import com.lukeleber.scandroid.interpreter.Interpreter;
import com.lukeleber.scandroid.interpreter.elm327.Protocol;
import com.lukeleber.scandroid.sae.j1979.detail.AppendixA;
import com.lukeleber.scandroid.sae.j1979.detail.AppendixB;
import com.lukeleber.scandroid.sae.j1979.util.CumulativePIDSupport;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/// TODO: Write Unit Tests
public class Profile
{
    public final static String BANK_1 = "bank_1";
    public final static String BANK_2 = "bank_2";
    public final static String BANK_3 = "bank_3";
    public final static String BANK_4 = "bank_4";

    public final static String DUAL_BANK = "dual_bank";
    public final static String QUAD_BANK = "quad_bank";

    private final Protocol protocol;

    private final Map<String, Boolean> equipmentCache = new HashMap<>();

    private final Map<Service, CumulativePIDSupport> supportedPIDs;

    private final Map<Service, PID<?>[]> pids = new TreeMap<>();

    public Profile(Protocol protocol, Map<Service, CumulativePIDSupport> supportedPIDs)
    {
        this.protocol = protocol;
        for (Service service : Service.values())
        {
            if (supportedPIDs.get(service) != null)
            {
                pids.put(service, new PID[255]);
            }
            else
            {
                pids.put(service, null);
            }
        }
        this.supportedPIDs = supportedPIDs;
        populateService01();
        populateService02();
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

    private void populateService01()
    {
        {
            addIfSupported(Service.LIVE_DATASTREAM, AppendixA.J1979_CHECK_PID_SUPPORT_1_TO_20);
            addIfSupported(Service.LIVE_DATASTREAM, AppendixA.J1979_CHECK_PID_SUPPORT_21_TO_40);
            addIfSupported(Service.LIVE_DATASTREAM, AppendixA.J1979_CHECK_PID_SUPPORT_41_TO_60);
            addIfSupported(Service.LIVE_DATASTREAM, AppendixA.J1979_CHECK_PID_SUPPORT_61_TO_80);
            addIfSupported(Service.LIVE_DATASTREAM, AppendixA.J1979_CHECK_PID_SUPPORT_81_TO_A0);
            addIfSupported(Service.LIVE_DATASTREAM, AppendixA.J1979_CHECK_PID_SUPPORT_A1_TO_C0);
            addIfSupported(Service.LIVE_DATASTREAM, AppendixA.J1979_CHECK_PID_SUPPORT_C1_TO_E0);
            addIfSupported(Service.LIVE_DATASTREAM, AppendixA.J1979_CHECK_PID_SUPPORT_E1_TO_FF);

            /// Appendix B
            for (PID<?> pid : SAE_J1979.SAE_J1979_STATIC_PIDS)
            {
                addIfSupported(Service.LIVE_DATASTREAM, pid);
            }

        }
    }

    private void populateService02()
    {
        addIfSupported(Service.FREEZE_FRAME_DATA, AppendixA.J1979_CHECK_PID_SUPPORT_1_TO_20);
        addIfSupported(Service.FREEZE_FRAME_DATA, AppendixA.J1979_CHECK_PID_SUPPORT_21_TO_40);
        addIfSupported(Service.FREEZE_FRAME_DATA, AppendixA.J1979_CHECK_PID_SUPPORT_41_TO_60);
        addIfSupported(Service.FREEZE_FRAME_DATA, AppendixA.J1979_CHECK_PID_SUPPORT_61_TO_80);
        addIfSupported(Service.FREEZE_FRAME_DATA, AppendixA.J1979_CHECK_PID_SUPPORT_81_TO_A0);
        addIfSupported(Service.FREEZE_FRAME_DATA, AppendixA.J1979_CHECK_PID_SUPPORT_A1_TO_C0);
        addIfSupported(Service.FREEZE_FRAME_DATA, AppendixA.J1979_CHECK_PID_SUPPORT_C1_TO_E0);
        addIfSupported(Service.FREEZE_FRAME_DATA, AppendixA.J1979_CHECK_PID_SUPPORT_E1_TO_FF);
        for (PID<?> pid : SAE_J1979.SAE_J1979_STATIC_PIDS)
        {
            addIfSupported(Service.FREEZE_FRAME_DATA, pid);
        }
    }

    private void addIfSupported(Service service, PID<?> pid)
    {
        if (this.isSupported(service, pid.getID()))
        {
            pids.get(service)[pid.getID()] = pid;
        }
    }

    @SuppressWarnings("unchecked")
    public static void createProfile(final Interpreter<?> interpreter, final Protocol protocol,
                                     final Handler<Profile> listener)
    {
        final Map<Service, CumulativePIDSupport> serviceMap = new TreeMap<>();
        final int[] services = new int[] { 0, 1, 4, 5, 7, 8 };
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
                        if (currentService < services.length)
                        {
                            CumulativePIDSupport.getSupportedPIDs(
                                    Service.values()[services[currentService]],
                                    interpreter, this);
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
                        if (code == FailureCode.REQUEST_NOT_SUPPORTED)
                        {
                            serviceMap.put(
                                    Service.values()[services[currentService++]],
                                    null);
                            if (currentService < services.length)
                            {
                                CumulativePIDSupport.getSupportedPIDs(
                                        Service.values()[services[currentService]],
                                        interpreter, this);
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

    public PID<?> getID(Service service, int id)
    {
        assert pids.containsKey(service) : "Invalid Service?  This should not happen!";
        PID<?>[] pids = this.pids.get(service);
        if (pids != null)
        {
            return pids[id];
        }
        return null;
    }

    public boolean isServiceSupported(Service service)
    {
        assert supportedPIDs.containsKey(service) : "Invalid Service?  This should not happen!";
        /// FIXME: Read ELM327 Docs
        if (service == Service.RETRIEVE_DTC || service == Service.CLEAR_DTC || service == Service.RETRIEVE_PENDING_DTC)
        {
            return true;
        }
        return supportedPIDs.get(service) != null;
    }

    public boolean isSupported(Service service, int id)
    {
        assert supportedPIDs.containsKey(service) : "Invalid Service?  This should not happen!";
        /// FIXME: Read ELM327 Docs
        if (service == Service.RETRIEVE_DTC || service == Service.CLEAR_DTC || service == Service.RETRIEVE_PENDING_DTC)
        {
            return true;
        }
        CumulativePIDSupport support = supportedPIDs.get(service);
        if (support != null)
        {
            return support.isSupported(id);
        }
        return false;
    }

    public boolean isEquipped(String key)
    {
        Boolean rv = equipmentCache.get(key);
        if (rv != null)
        {
            return rv.booleanValue();
        }
        return false;
    }


}
