/*
 * This file is protected under the KILLGPL.
 * For more information visit <insert_valid_link_to_killgpl_here>
 * <p/>
 * Copyright (c) Luke A. Leber <LukeLeber@gmail.com> 2014
 */

package com.lukeleber.scandroid.sae.j1979;

import com.lukeleber.scandroid.util.UserFriendlyToString;

import java.io.Serializable;

/**
 * <p>An enumeration of all service modes defined by SAE-J1979.  Vehicles are not required to
 * support all services.  It is recommended to use a {@link com.lukeleber.scandroid.sae.Profile}
 * to help determine which services are supported by a vehicle through the
 * {@link com.lukeleber.scandroid.sae.Profile#isServiceSupported(Service)} method.</p>
 * <p><strong>Note that there are substantial variations in the documentation depending
 * on which protocol the vehicle supports!</strong></p>
 */
public enum Service implements Serializable,
                               UserFriendlyToString
{
    /**
     * <p><strong>The following documentation applies to vehicles supporting ISO 9141-2,
     * ISO 14230-4, and SAE J1850 protocols only.</strong></p>
     * <p>The purpose of this service is to allow access to current emission related data values
     * including analogue inputs and outputs, digital inputs and outputs, and system status
     * information.  The request for information includes a parameter identification (PID)
     * value that indicates to the on-board system the specific information requested.  PID
     * specifications, scaling information, and display formats are included in Appendix B.</p>
     * <p>The ECU(s) will respond to this message by transmitting the requested data value last
     * determined by the system.  All data values returned for sensor readings will be actual
     * readings, not default or substitute values used by the system because of a fault with
     * that sensor.</p>
     * <p>Not all PIDs are applicable or supported by all systems.  PID $00 is a bit-encoded
     * PID that indicates, for each ECU which PIDs that ECU supports.  PID $00 shall be
     * supported by all ECUs that respond to a service $01 request, because the external test
     * equipment that conforms to SAE J1978 use the presence of a response message by the
     * vehicle to this request message to determine which protocol is supported for
     * diagnostic communications.  Appendix A defines how to encode supported PIDs.</p>
     * <br\>
     * <p><strong>The following documentation applies to vehicles supporting ISO 15765-4
     * protocols only.</strong></p>
     * <p>The purpose of this service is to allow access to current emission related data values
     * including analogue inputs and outputs, digital inputs and outputs, and system status
     * information.  The request for information includes a parameter identification (PID)
     * value that indicates to the on-board system the specific information requested.  PID
     * specifications, scaling information, and display formats are included in Appendix B.</p>
     * <p>The ECU(s) will respond to this message by transmitting the requested data value last
     * determined by the system.  All data values returned for sensor readings will be actual
     * readings, not default or substitute values used by the system because of a fault with
     * that sensor.</p>
     * <p>Not all PIDs are applicable or supported by all systems.  PID $00 is a bit-encoded
     * value that indicates for each ECU which PIDs are supported.  PID $00 indicates support
     * for PIDs from $01 to $20.  PID $20 indicates support for PIDs $21 through $40, etc.  This
     * is the same concept for PIDs/OBD Monitor IDs/TIDs/InfoTypes support in services $01,
     * $02, $06, $08, $09.  PID $00 is required for those ECUs that respond to a corresponding
     * service $01 request message as specified in Appendix A.  PID $00 is optional for those
     * ECUs that do not respond to additional service $01 request messages.</p>
     * <p>The order of the PIDs in the response message is not required to match the order
     * in the request message.</p>
     */
    LIVE_DATASTREAM(1, "Live Datastream",
                    "Service $01 - Request Current Powertrain Diagnostic Data"),

    /**
     * <p><strong>The following documentation applies to vehicles supporting ISO 9141-2,
     * ISO 14230-4, and SAE J1850 protocols only.</strong></p>
     * <p>The purpose of this service is to allow access to emission-related data values
     * in a freeze frame.  This allows expansion to meet manufacturer specific requirements
     * not necessarily related to the required freeze frame and not necessarily containing
     * the same data values as the required freeze frame.  The request message includes a
     * parameter identification (PID) value that indicates to the on-board system the
     * specific information requested.  PID specifications, scaling information, and display
     * formats for the freeze frame are included in Appendix B.</p>
     * <p>The ECU(s) will respond to this message by transmitting the requested data value
     * stored by the system.  All data values returned for sensor readings will be actual stored
     * readings, not default or substitute values used by the system because of a fault with
     * that sensor.</p>
     * <p>Not all PIDs are applicable or supported by all systems.  PID $00 is a bit-encoded
     * PID that indicates, for each ECU, which PIDs that ECU supports.  Therefore, PID $00
     * shall be supported by all ECUs that respond to a service $02 request as specified
     * even if the ECU does not have a freeze frame stored at the time of the request.</p>
     * <p>Appendix A defines how to encode supported PIDs.</p>
     * <p>PID $02 indicates the DTC that caused the freeze frame data to be stored.  If freeze
     * frame data is not stored in the ECU, the system shall report $00 00 as the DTC.  Any
     * data reported when the stored DTC is $00 00 may not be valid.</p>
     * <p>The frame number byte will indicate $00 for the mandated freeze frame data.
     * Manufacturers may optionally save additional freeze frames and use this service to
     * obtain that data by specifying the freeze frame number in the request message.  If a
     * manufacturer uses these additional freeze frames, they will be stored under conditions
     * specified by the manufacturer, and contain data specified by the manufacturer.</p>
     * <br\>
     * <p><strong>The following documentation applies to vehicles supporting ISO 15765-4
     * protocols only.</strong></p>
     * <p>The purpose of this service is to allow access to emission-related data values
     * in a freeze frame.  This allows expansion to meet manufacturer specific requirements
     * not necessarily related to the required freeze frame and not necessarily containing
     * the same data values as the required freeze frame.  The request message includes a
     * parameter identification (PID) value that indicates to the on-board system the
     * specific information requested.  PID specifications, scaling information, and display
     * formats for the freeze frame are included in Appendix B.</p>
     * <p>The ECU(s) will respond to this message by transmitting the requested data value
     * stored by the system.  All data values returned for sensor readings will be actual stored
     * readings, not default or substitute values used by the system because of a fault with
     * that sensor.</p>
     * <p>Not all PIDs are applicable or supported by all systems.  PID $00 is a bit-encoded
     * PID that indicates, for each ECU, which PIDs that ECU supports.  Therefore, PID $00
     * shall be supported by all ECUs that respond to a service $02 request as specified
     * even if the ECU does not have a freeze frame stored at the time of the request.</p>
     * <p>Appendix A defines how to encode supported PIDs.</p>
     * <p>PID $02 indicates the DTC that caused the freeze frame data to be stored.  If freeze
     * frame data is not stored in the ECU, the system shall report $00 00 as the DTC.  Any
     * data reported when the stored DTC is $00 00 may not be valid.</p>
     * <p>The frame number byte will indicate $00 for the mandated freeze frame data.
     * Manufacturers may optionally save additional freeze frames and use this service to
     * obtain that data by specifying the freeze frame number in the request message.  If a
     * manufacturer uses these additional freeze frames, they will be stored under conditions
     * specified by the manufacturer, and contain data specified by the manufacturer.</p>
     * <p>Not all PIDs are applicable or supported by all systems.  PID $00 is a bit-encoded
     * value that indicates for each ECU which PIDs are supported.  PID $00 indicates support
     * for PIDs from $01 to $20.  PID $20 indicates support for PIDs $21 through $40, etc.  This
     * is the same concept for PIDs/OBD Monitor IDs/TIDs/InfoTypes support in services $01,
     * $02, $06, $08, $09.  PID $00 is required for those ECUs that respond to a corresponding
     * service $02 request message as specified in Appendix A.  PID $00 is optional for those
     * ECUs that do not respond to additional service $02 request messages.</p>
     * <p>The order of the PIDs in the response message is not required to match the order
     * in the request message.</p>
     */
    FREEZE_FRAME_DATA(2, "Freeze-Frame Data",
                    "Service $02 - Request Powertrain Freeze Frame Data"),

    /**
     * <p><strong>The following documentation applies to vehicles supporting ISO 9141-2,
     * ISO 14230-4, and SAE J1850 protocols only.</strong></p>
     * TODO: Find this documentation!!!
     * <br\>
     * <p><strong>The following documentation applies to vehicles supporting ISO 15765-4
     * protocols only.</strong></p>
     * <p>The purpose of this service is to enable to external test equipment to obtain
     * "confirmed" emission-related DTCs.</p>
     * <p>Send a Service $03 request for all emission-related DTCs.  Each ECU that has DTCs
     * will respond with one (1) message containing all emission-related DTCs.  If an ECU does
     * not have emission-related DTCs then it shall respond with a message indicating no DTCs
     * are stored by setting the parameter # of DTC to $00.</p>
     * <p>DTCs are transmitted in two (2) bytes of information for each DTC.  The first two (2)
     * bits (high order) of the first (1) byte for each DTC will be zeros to indicate whether
     * the DTC is a Powertrain, Chassis, Body, or Network DTC (refer to SAE J2012 for additional
     * interpretation of this structure).  The second two (2) bits will indicate the first digit
     * of the DTC (0 through 3).  The second (2) nibble of the first (1) byte and the entire
     * second (2) byte are the next three (3) hexadecimal characters of the actual DTC reported
     * as hexadecimal.  A Powertrain DTC transmitted as $0143 shall be displayed as P0143.</p>
     */
    RETRIEVE_DTC(3, "Read Codes",
                    "Service $03 - Request Emission-Related Diagnostic Trouble Codes"),

    /**
     * <p><strong>The following documentation applies to vehicles supporting ISO 9141-2,
     * ISO 14230-4, and SAE J1850 protocols only.</strong></p>
     * <p>The purpose of this service is to provide a means for the external test equipment to
     * command ECUs to clear all emission-related diagnostic information.  This includes:</p>
     * <ul>
     *     <li>Number of diagnostic trouble codes (can be read with Service $01, PID $01)</li>
     *     <li>Diagnostic trouble codes (can be read with Service $03)</li>
     *     <li>Trouble code for freeze frame data (can be read with Service $02, PID $02)</li>
     *     <li>Freeze frame data (can be read with Service $02)</li>
     *     <li>Oxygen sensor test data (can be read with Service $05)</li>
     *     <li>Status of system monitoring tests (can be read with Service $01, PID $01)</li>
     *     <li>On-board monitoring test results (can be read with Services $06 and $07)</li>
     *     <li>Distance travelled while MIL is activated (can be read with Service $01, PID $21)
     *     </li>
     *     <li>Number of warm-ups since DTC cleared (can be read with Service $01, PID $30)</li>
     *     <li>Distance since diagnostic trouble codes cleared (can be read with Service $01,
     *     PID $31)</li>
     *     <li>Minutes ran by the engine while MIL activated (can be read with Service $01,
     *     PID $4D)</li>
     *     <li>Time since diagnostic trouble codes cleared (can be read with Service $01,
     *     PID $4E)</li>
     * </ul>
     * <p>Other manufacturer specific "clearing/resetting" actions may also occur in
     * response to this request message.</p>
     * <p>For safety and/or technical design reasons, some ECUs may not respond to this service
     * under all conditions.  All ECUs shall respond to this service request with the ignition
     * ON and with the engine not running.  ECUs that cannot perform this operation under
     * other conditions, such as with the engine running, will ignore the request with
     * SAE-J1850 and ISO 9141-2 interfaces, or will send a negative response message with ISO
     * 14230-4 interfaces, as described in ISO 14230-4.</p>
     * <br\>
     * <p><strong>The following documentation applies to vehicles supporting ISO 15765-4
     * protocols only.</strong></p>
     * The purpose of this service is to provide a means for the external test equipment to
     * command ECUs to clear all emission-related diagnostic information.  This includes:</p>
     * <ul>
     *     <li>Number of diagnostic trouble codes (can be read with Service $01, PID $01)</li>
     *     <li>Diagnostic trouble codes (can be read with Service $03)</li>
     *     <li>Trouble code for freeze frame data (can be read with Service $02, PID $02)</li>
     *     <li>Freeze frame data (can be read with Service $02)</li>
     *     <li>Oxygen sensor test data (can be read with Service $05)</li>
     *     <li>Status of system monitoring tests (can be read with Service $01, PID $01)</li>
     *     <li>On-board monitoring test results (can be read with Services $06 and $07)</li>
     *     <li>Distance travelled while MIL is activated (can be read with Service $01, PID $21)
     *     </li>
     *     <li>Number of warm-ups since DTC cleared (can be read with Service $01, PID $30)</li>
     *     <li>Distance since diagnostic trouble codes cleared (can be read with Service $01,
     *     PID $31)</li>
     *     <li>Minutes ran by the engine while MIL activated (can be read with Service $01,
     *     PID $4D)</li>
     *     <li>Time since diagnostic trouble codes cleared (can be read with Service $01,
     *     PID $4E)</li>
     * </ul>
     * <p>Other manufacturer specific "clearing/resetting" actions may also occur in
     * response to this request message.  All ECUs shall respond to this request message
     * with ignition ON and with the engine not running.</p>
     * <p>For safety and/or technical design reasons, ECUs that cannot perform this operation
     * under other conditions, such as with the engine running shall send a negative response
     * message with response code $22 - conditionsNotCorrect.</p>
     */
    CLEAR_DTC(4, "Reset Diagnostic Info",
                    "Service $04 - Clear/Reset Emission-Related Diagnostic Information"),

    /**
     * <p><strong>The following documentation applies to vehicles supporting ISO 9141-2,
     * ISO 14230-4, and SAE J1850 protocols only.</strong></p>
     * <p>The purpose of this service is to allow access to the on-board oxygen sensor monitoring
     * test results.  The same information may be obtained by the use of service $06.</p>
     * <p>The request message for test results includes a Test ID value that indicates the
     * information requested.  Test value definitions, scaling information, and display formats
     * are included in Appendix C.</p>
     * <p>Many methods may be used to calculate test results for this service by different
     * manufacturers.  If data values are to be reported using these messages that are different
     * from those specified, ranges of test values have been assigned that can be used which have
     * standard units of measure.  The external test equipment can convert these values and display
     * them in standard units.</p>
     * <p>The ECU will respond to this message by transmitting the requested test data last
     * determined by the system.  The latest test results are to be retained, even over multiple
     * ignition OFF cycles, until replaced by more recent test results.  Test results are
     * requested by Test ID.</p>
     * <p>Not all test values are applicable or supported by all vehicles.  An optional feature of
     * this service is for the ECU to indicate which Test IDs are supported.  Test ID $00 is a
     * bit-encoded value that indicates support for Test IDs from $01 to $20.  Test ID $20
     * indicates support for Test IDs $21 through $40, etc.  This is the same concept as used
     * for PID support in services $01 and $02 as specified in Appendix A.  If Test ID $00 is
     * not supported, then the ECU does not use this feature to indicate Test ID support.</p>
     * <br\>
     * <p><strong>The following documentation applies to vehicles supporting ISO 15765-4
     * protocols only.</strong></p>
     * <p>Service $05 is not supported for CAN.  The functionality of service $05 is implemented
     * in service $06.</p>
     */
    OXYGEN_SENSOR_TEST_RESULTS(5, "Oxygen Sensor Test Results",
                    "Service $05 - Request Oxygen Sensor Monitoring Test Results"),

    /**
     * <p><strong>The following documentation applies to vehicles supporting ISO 9141-2,
     * ISO 14230-4, and SAE J1850 protocols only.</strong></p>
     * <p>The purpose of this service is to allow access to the results for on-board diagnostic
     * monitoring tests of specific components/systems that are not continuously monitored.
     * Examples are catalyst monitoring and the evaporative system monitoring.</p>
     * <p>The vehicle manufacturer is responsible for assigning Test IDs and Component IDs for
     * tests of different systems and components.  The latest test results are to be retained,
     * even over multiple ignition OFF cycles, until replaced by more recent test results.  Test
     * results are requested by Test ID.  Test results are reported only for supported
     * combinations of test limit type and component ID, and are reported as positive (unsigned)
     * values.  Only one test limit is included in a response message, but that limit could be
     * either a minimum or a maximum limit.  If both a minimum and maximum test limit are to be
     * reported, then two (2) response messages will be transmitted, in any order.  The most
     * significant bit of the "test limit type / component ID" byte will be used to indicate
     * the test limit type.</p>
     * <p>An optional feature of this service is for the ECU to indicate which Test IDs are
     * supported.  Test ID $00 is a bit-encoded value that indicates support for Test IDs from
     * $01 to $20.  Test ID $20 indicates support for Test IDs $21 through $40, etc.  This is
     * the same concept as used for PID support in services $01 and $02 as specified in Appendix
     * A.  If Test ID $00 is not supported, then the ECU does not use this feature to indicate
     * Test ID support.</p>
     * <p>This service can be used as an alternative to service $05 to report oxygen sensor
     * rest results.</p>
     * <br\>
     * <p><strong>The following documentation applies to vehicles supporting ISO 15765-4
     * protocols only.</strong></p>
     * <p>The purpose of this service is to allow access to the results for on-board diagnostic
     * monitoring tests of specific components / systems that are continuously monitored.  (e.g.,
     * mis-fire monitoring) and non-continuously monitored (e.g., catalyst system).</p>
     * <p>The request message for test values includes an On-Board Diagnostic Monitor ID (see
     * Appendix D) that indicates the information requested.  Unit and Scaling information is
     * included in Appendix E.</p>
     * <p>The vehicle manufacturer is responsible for assigning "Manufacturer Defined Test IDs"
     * for different tests of a monitored system.  The latest test values (results) are to be
     * retained, even over multiple ignition OFF cycles, until replaced by more recent test
     * values (results).  Test values (results) are always reported with the Minimum and
     * Maximum Test Limits.  The Unit and Scaling ID included in the response message defines
     * the scaling and unit to be used by the external test equipment to display the test
     * values (results), Minimum Test Limit, and Maximum Test Limit information.</p>
     * <p>If an On-Board Diagnostic Monitor has not been completed at last once since
     * Clear/reset emission-related diagnostic information or battery disconnect, then
     * the parameters Test Value (Results), Minimum Test Limit, and Maximum Test Limit shall
     * be set to zero ($00) values.</p>
     * <p>Not all On-Board Diagnostic Monitor IDs are applicable or supported by all systems.
     * On-Board Diagnostic Monitor ID $00 is a bit-encoded value that indicates for each ECU
     * which On-Board Diagnostic Monitor IDs are supported.  On-Board Diagnostic Monitor ID $00
     * indicates support for On-Board Diagnostic Monitor IDs from $01 to $20.  On-Board Diagnostic
     * Monitor ID $20 indicates support for On-Board Diagnostic Monitor IDs $21 through $40, etc.
     * This is the same concept for PIDs/TIDs/InfoTypes support in services $01, $02, $06, $08,
     * and $09.  On-board Diagnostic Monitor ID $00 is required for those ECUs that respond
     * to a corresponding service $06 request message as specified in Appendix A.  On-Board
     * Diagnostic Monitor ID $00 is optional for those ECUs that do not respond to additional
     * service $06 request messages.</p>
     */
    OTHER_TEST_RESULTS(6, "Other Test Results",
                    "Service $06 - Request On-Board Monitoring Test Results for Specific Monitored Systems"),

    /**
     * <p><strong>The following documentation applies to vehicles supporting ISO 9141-2,
     * ISO 14230-4, and SAE J1850 protocols only.</strong></p>
     * <p>The purpose of this service is to enable the external test equipment to obtain "pending"
     * diagnostic trouble codes detected during current or last completed driving cycle for
     * emission-related components / systems that are tested or continuously monitored during
     * normal driving conditions.  Service $07 is required for all DTCs and is independent of
     * Service $03.  The intended use of this data is to assist the service technician after a
     * vehicle repair, and after clearing diagnostic information, by reporting the test results
     * after a single driving cycle.  If the test failed during the driving cycle, the DTC
     * associated with that test will be reported.  Test results reported by this service do not
     * necessarily indicate a faulty component / system.  If test results indicate a failure
     * after additional driving, then the MIL will be illuminated and a DTC will be set and
     * reported with service $03, indicating a faulty component / system.  This service can
     * always be used to request the results of the latest test, independent of the setting of
     * a DTC.</p>
     * <p>Test results for these components/systems are reported in the same format as the DTCs
     * in service $03 - refer to the functional description for service $03.</p>
     * <p>If less than three (3) DTC values are reported for failed tests, the response messages
     * used to report the test results shall be filled with $00 to fill seven (7) data bytes.
     * This maintains the required fixed message length for all messages.</p>
     * <p>If there are no test failures to report, responses are permitted but not required for
     * SAE J1850 and ISO 9141-2 interfaces.  For ISO 14230-4 interfaces, the ECU will respond
     * with a report containing no codes (all DTC values shall contain $00).</p>
     * <br\>
     * <p><strong>The following documentation applies to vehicles supporting ISO 15765-4
     * protocols only.</strong></p>
     * <p>The purpose of this service is to enable the external test equipment to obtain "pending"
     * diagnostic trouble codes detected during current or last completed driving cycle for
     * emission-related components / systems that are tested or continuously monitored during
     * normal driving conditions.  Service $07 is required for all DTCs and is independent of
     * Service $03.  The intended use of this data is to assist the service technician after a
     * vehicle repair, and after clearing diagnostic information, by reporting the test results
     * after a single driving cycle.  If the test failed during the driving cycle, the DTC
     * associated with that test will be reported.  Test results reported by this service do not
     * necessarily indicate a faulty component / system.  If test results indicate a failure
     * after additional driving, then the MIL will be illuminated and a DTC will be set and
     * reported with service $03, indicating a faulty component / system.  This service can
     * always be used to request the results of the latest test, independent of the setting of
     * a DTC.</p>
     * <p>Test results for these components/systems are reported in the same format as the DTCs
     * in service $03 - refer to the functional description for service $03.</p>
     */
    RETRIEVE_PENDING_DTC(7, "Read Pending Codes",
                    "Service $07 - Request Emission-Related Diagnostic Trouble Codes Detected During Current or Last Completed Driving Cycle"),

    /**
     * <p><strong>The following documentation applies to vehicles supporting ISO 9141-2,
     * ISO 14230-4, and SAE J1850 protocols only.</strong></p>
     * <p>The purpose of this service is to enable the external test equipment to control the
     * operation of an on-board system, test, or component.</p>
     * <p>The data bytes will be specified, if necessary, for each Test ID in Appendix F, and
     * will be unique for each Test ID.  If any data bytes are unused for any test, they shall
     * be filled with $00 to maintain a fixed message length.</p>
     * <p>Possible uses for these data bytes in the request message are:</p>
     * <ul>
     *     <li>Turn on-board system/test/component ON</li>
     *     <li>Turn on-board system/test/component OFF</li>
     *     <li>Cycle on-board system/test/component for 'n' seconds</li>
     * </ul>
     * <p>Possible uses for these data bytes in the response message are:</p>
     * <ul>
     *     <li>Report system status</li>
     *     <li>Report test results</li>
     * </ul>
     * <p>An optional feature of this service is for the ECU to indicate which Test IDs are
     * supported.  Test ID $00 is a bit-encoded value that indicates support for Test IDs from
     * $01 to $20.  Test ID $20 indicates support for Test IDs $21 through $40, etc.  This is
     * the same concept as used for PID support in services $01 and $02 as specified in Appendix
     * A.  If Test ID $00 is not supported, then the ECU does not use this feature to indicate
     * Test ID support.</p>
     * <br\>
     * <p><strong>The following documentation applies to vehicles supporting ISO 15765-4
     * protocols only.</strong></p>
     * <p>The purpose of this service is to enable the external test equipment to control the
     * operation of an on-board system, test, or component.</p>
     * <p>The data bytes will be specified, if necessary, for each Test ID in Appendix F, and
     * will be unique for each Test ID.</p>
     * <p>Possible uses for these data bytes in the request message are:</p>
     * <ul>
     *     <li>Turn on-board system/test/component ON</li>
     *     <li>Turn on-board system/test/component OFF</li>
     *     <li>Cycle on-board system/test/component for 'n' seconds</li>
     * </ul>
     * <p>Possible uses for these data bytes in the response message are:</p>
     * <ul>
     *     <li>Report system status</li>
     *     <li>Report test results</li>
     * </ul>
     * <p>Not all TIDs are applicable or supported by all systems.  TID $00 is a bit-encoded
     * value that indicates for each ECU which TIDs are supported.  TID $00 indicates support
     * for TIDs from $01 to $20.  TID $20 indicates support for TIDs $21 through $40, etc.  This
     * is the same concept for PIDs/TIDs/InfoTypes support in services $01, $02, $06, $08,
     * $09.  TID $00 is required for those ECUs that respond to a corresponding service $08
     * request message as specified in Appendix A.  TID $00 is optional for those ECUs that do not
     * respond to additional service $08 request messages.</p>
     * <p>The order of the TIDs in the response message is not required to match the order in
     * the request message.</p>
     */
    REMOTE_CONTROL(8, "Remote Control",
                    "Service $08 - Request Control of On-Board System, Test, or Component"),

    /**
     * <p><strong>The following documentation applies to vehicles supporting ISO 9141-2,
     * ISO 14230-4, and SAE J1850 protocols only.</strong></p>
     * TODO: Monkey work... :(
     * <br\>
     * <p><strong>The following documentation applies to vehicles supporting ISO 15765-4
     * protocols only.</strong></p>
     */
    VEHICLE_INFORMATION(9, "Vehicle Information",
                    "Service $09 - Request Vehicle Information");

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
        return description;
    }

    @Override
    public final String toUserFriendlyString()
    {
        return brief;
    }
}
