/*
 * This file is protected under the KILLGPL.
 * For more information visit <insert_valid_link_to_killgpl_here>
 * <p/>
 * Copyright (c) Luke A. Leber <LukeLeber@gmail.com> 2014
 */

package com.lukeleber.scandroid.sae;

public enum Service
{
    LIVE_DATASTREAM(1, "Live Datastream",
                    "Retrieve information from the currently active data-stream"),
    FREEZE_FRAME_DATASTREAM(2, "Freeze-Frame Data",
                            "Retrieve information from freeze-frame data (if present)"),
    RETRIEVE_DTC(3, "Read Codes", "Retrieve diagnostic trouble codes (DTCs)"),
    CLEAR_DTC(4, "Reset Diagnostic Info", "Clear diagnostic trouble codes (DTCs)"),
    TEST_RESULTS_NON_CAN(5, "Test Results (non-CAN)",
                         "Retrieve test results for non-CAN protocols"),
    TEST_RESULTS_CAN_ONLY(6, "Test Results (CAN)", "Retrieve test results for CAN protocols"),
    RETRIEVE_PENDING_DTC(7, "Read Pending Codes",
                         "Retrieve diagnostic trouble codes (DTCs) that were detected during " +
                                 "the current of last drive cycle"),
    REMOTE_CONTROL(8, "Remote Control", "Control an on-board component and/or system remotely"),
    VEHICLE_INFORMATION(9, "Vehicle Information", "Retrieve information about the vehicle"),
    RETRIEVE_PERMANENT_DTC(10, "Read Permanent Codes",
                           "Retrieve permanent diagnostic trouble codes (DTCs)");

    private final int id;
    private final String brief;
    private final String description;

    private Service(int id, String brief, String description)
    {
        this.id = id;
        this.brief = brief;
        this.description = description;
    }

    public final int getID()
    {
        return id;
    }

    @Override
    public final String toString()
    {
        return brief;
    }

    public final String getDescription()
    {
        return description;
    }
}
