// This file is protected under the KILLGPL.
// For more information, visit http://www.lukeleber.github.io/KILLGPL.html
//
// Copyright (c) Luke Leber <LukeLeber@gmail.com>

package com.lukeleber.scandroid.interpreter.elm327;

import com.lukeleber.scandroid.interpreter.Option;

/**
 * All of the op-codes provided with the ELM327 documentation.
 *
 * <p><strong>The documentation contained within this file is the intellectual property of
 * <a href=http://www.elmelectronics.com>ELM Electronics</a> and is embedded herein with
 * written permission.  To obtain the latest documentation from ELM Electronics, please visit
 * their <a href=http://elmelectronics.com/obdic.html>ELM327 page</a>.</strong></p>
 *
 */
public enum OpCode
        implements Option<String>
{
    /**
     * <CR> [ repeat the last command ] Sending a single carriage return character causes the ELM327
     * to repeat the last command that it performed. This is typically used when you wish to obtain
     * updates to a value at the fastest possible rate - for example, you may send 01 0C to obtain
     * the engine rpm, then send only a carriage return character each time you wish to receive an
     * update
     */
    ELM327_REPEAT_LAST_COMMAND("\r"),

    /**
     * AL [ Allow Long messages ] The standard OBDII protocols restrict the number of data bytes in
     * a message to seven, which the ELM327 normally does as well (for both send and receive). If AL
     * is selected, the ELM327 will allow long sends (eight data bytes) and long receives (unlimited
     * in number). The default is AL off (and NL selected).
     */
    ELM327_OBD_ALLOW_LONG_MESSAGES("AL"),

    /**
     * AMC [ display Activity Monitor Count ] The Activity Monitor uses a counter to determine just
     * how active the ELM327's OBD inputs are. Every time that activity is detected, this counter is
     * reset, while if there is no activity, the count goes up (every 0.655 seconds). This count
     * then represents the time since activity was last detected, and may be useful when writing
     * your own logic based on OBD activity. The counter will not increment past FF (internal logic
     * stops it there), and stays at 00 while monitoring
     */
    ELM327_OBD_DISPLAY_ACTIVITY_MONITOR_COUNT("AMC"),

    /**
     * AMT hh [ set the Act Mon Timeout to hh ] When the Activity Monitor Count (ie time) exceeds a
     * certain threshold, the ELM327 decides that there is no OBD activity. It might then give an
     * ACT ALERT message or switch to Low Power operation, depending on how the bits of PP 0F are
     * set. The threshold setting is determined by either PP 0F bit 4, or by the AT AMT value,
     * should you provide it. The actual time to alarm will be (hh+1) x 0.65536 seconds. Note that a
     * value of 00 is accepted for AMT, but is used to block all Activity Monitor outputs.
     */
    ELM327_OBD_ACTIVITY_MONITOR_TIMEOUT("AMT%d"),

    /**
     * AR [ Automatically set the Receive address ] Responses from the vehicle will be acknowledged
     * and displayed by the ELM327, if the internally stored receive address matches the address
     * that the message is being sent to. With the auto receive mode in effect, the value used for
     * the receive address will be chosen based on the current header bytes, and will automatically
     * be updated whenever the header bytes are changed. The value that is used for the receive
     * address is determined based on such things as the contents of the first header byte, and
     * whether the message uses physical addressing, functional addressing, or if the user has set a
     * value with the SR or RA commands. Auto Receive is turned on by default, and is not used by
     * the J1939 protocol.
     */
    ELM327_OBD_AUTOMATICALLY_SET_RECEIVE_ADDRESS("AR"),

    /**
     * AT0 , AT1 and AT2 [ Adaptive Timing control ] When receiving responses from a vehicle, the
     * ELM327 has traditionally waited the time set by the AT ST hh setting for a response. To
     * ensure that the IC would work with a wide variety of vehicles, the default value was set to a
     * conservative (slow) value. Although it was adjustable, many people did not have the equipment
     * or experience to determine a better value. The Adaptive Timing feature automatically sets the
     * timeout value for you, to a value that is based on the actual response times that your
     * vehicle is responding in. As conditions such as bus loading, etc. change, the algorithm
     * learns from them, and makes appropriate adjustments. Note that it always uses your AT ST hh
     * setting as the maximum setting, and will never choose one which is longer. There are three
     * adaptive timing settings that are available for use. By default, Adaptive Timing option 1
     * (AT1) is enabled, and is the recommended setting. AT0 is used to disable Adaptive Timing (so
     * the timeout is always as set by AT ST), while AT2 is a more aggressive version of AT1 (the
     * effect is more noticeable for very slow connections – you may not see much difference with
     * faster OBD systems). The J1939 protocol does not support Adaptive Timing – it uses fixed
     * timeouts as set in the standard.
     */

    /**
     * AT0 [Adaptive Timing Off] Disables Adaptive Timing (uses the timeout specified by "AT ST"
     */
    ELM327_OBD_ADAPTIVE_OFF("AT0"),

    /**
     * AT1 [Adaptive Timing control 1] The default (recommended) adaptive timing mode.
     */
    ELM327_OBD_ADAPTIVE_TIMING_AUTO1("AT1"),

    /**
     * AT2 [Adaptive Timing control 2] A more aggressive version of AT1
     */
    ELM327_OBD_ADAPTIVE_TIMING_AUTO2("AT2"),

    /**
     * BD [ perform an OBD Buffer Dump ] All messages sent and received by the ELM327 are stored
     * temporarily in a set of twelve memory storage locations called the OBD Buffer. Occasionally,
     * it may be of use to view the contents of this buffer, perhaps to see why an initiation
     * failed, to see the header bytes in the last message, or just to learn more of the structure
     * of OBD messages. You can ask at any time for the contents of this buffer to be ‘dumped’ (ie
     * printed) – when you do, the ELM327 sends a length byte (representing the length of the
     * message in the buffer) followed by the contents of all twelve OBD buffer locations. For
     * example, here’s one ‘dump’: >AT BD 05 C1 33 F1 3E 23 C4 00 00 10 F8 00 00 The 05 is the
     * length byte - it tells us that only the first 5 bytes (ie C1 33 F1 3E and 23) are valid. The
     * remaining bytes are likely left over from a previous operation. The length byte always
     * represents the actual number of bytes received, whether they fit into the OBD buffer or not.
     * This may be useful when viewing long data streams (with AT AL), as it represents the actual
     * number of bytes received, mod 256. Note that only the first twelve bytes received are stored
     * in the buffer.
     */
    ELM327_OBD_BUFFER_DUMP("BD"),

    /**
     * BI [ Bypass the Initialization sequence ] This command should be used with caution. It allows
     * an OBD protocol to be made active without requiring any sort of initiation or handshaking to
     * occur. The initiation process is normally used to validate the protocol, and without it,
     * results may be difficult to predict. It should not be used for routine OBD use, and has only
     * been provided to allow the construction of ECU simulators and training demonstrators.
     */
    ELM327_OBD_BYPASS_INITIALIZATION("BI"),

    /**
     * BRD hh [ try Baud Rate Divisor hh ] This command is used to change the RS232 baud rate
     * divisor to the hex value provided by hh, while under computer control. It is not intended for
     * casual experimenting - if you wish to change the baud rate from a terminal program, you
     * should use PP 0C. Since some interface circuits are not able to operate at high data rates,
     * the BRD command uses a sequence of sends and receives to test the interface, with any failure
     * resulting in a fallback to the previous baud rate. This allows several baud rates to be
     * tested and a reliable one chosen for the communications. The entire process is described in
     * detail in the ‘Using Higher RS232 Baud Rates’ section, on pages 50 and 51. If successful, the
     * actual baud rate (in kbps) will be 4000 divided by the divisor (hh). The value 00 is not
     * accepted by the BRD command.
     */
    ELM327_BAUD_RATE_DIVISOR("BRD%d"),

    /**
     * BRT hh [ set Baud Rate Timeout to hh ] This command allows the timeout used for the Baud Rate
     * handshake (ie. AT BRD) to be varied. The time delay is given by hh x 5.0 msec, where hh is a
     * hexadecimal value. The default value for this setting is 0F, providing 75 msec. Note that a
     * value of 00 does not result in 0 msec - it provides the maximum time of 256 x 5.0 msec, or
     * 1.28 seconds.
     */
    ELM327_BAUD_RATE_TIMEOUT("BRT%d"),

    /**
     * CAF0 and CAF1 [ CAN Auto Formatting off or on ] These commands determine whether the ELM327
     * assists you with the formatting of the CAN data that is sent and received. With CAN Automatic
     * Formatting enabled (CAF1), the formatting (PCI) bytes will be automatically generated for you
     * when sending, and will be removed when receiving. This means that you can continue to issue
     * OBD requests (01 00, etc.) as usual, without regard to the extra bytes that CAN diagnostics
     * systems require. Also, with formatting on, any extra (unused) data bytes that are received in
     * the frame will be removed, and any messages with invalid PCI bytes will be ignored. (When
     * monitoring, however, messages with invalid PCI bytes are all shown, with a ‘<DATA ERROR’
     * message beside them). Multi-frame responses may be returned by the vehicle with ISO 15765 and
     * SAE J1939. To make these more readable, the Auto Formatting mode will extract the total data
     * length and print it on one line, then show each line of data with the segment number followed
     * by a colon (‘:’), and then the data bytes. You may also see the characters ' FC: ' on a line
     * (if you are experimenting). This identifies a Flow Control message that has been sent as part
     * of the multi-line message signalling. Flow Control messages are automatically generated by
     * the ELM327 in response to a ‘First Frame’ reply, as long as the CFC setting is on (it does
     * not matter if auto formatting is on or not). Another type of message – the RTR (or ‘Remote
     * Transfer Request’) – will be automatically hidden for you when in the CAF1 mode, since they
     * contain no data. When auto formatting is off (CAF0), you will see the characters 'RTR'
     * printed when a remote transfer request frame has been received. Turning the CAN Automatic
     * Formatting off (CAF0), will cause the ELM327 to print all of the data bytes as received. No
     * bytes will be hidden from you, and none will be inserted for you. Similarly, when sending
     * data with formatting off, you must provide all of the required data bytes exactly as they are
     * to be sent – the ELM327 will not add a PCI byte for you (but it will add some trailing
     * 'padding' bytes to ensure that the required eight data bytes are sent). This allows the
     * ELM327 to be used with protocols that have special formatting requirements. Note that turning
     * the display of headers on (with AT H1) will override some of the CAF1 formatting of the
     * received data, so that the received bytes will appear much like in the CAF0 mode (ie. as
     * received). It is only the printing of the received data that will be affected when both CAF1
     * and H1 modes are enabled, though; when sending data, the PCI byte will still be created for
     * you and padding bytes will still be added. Auto Formatting on (CAF1) is the default setting.
     */

    /**
     * CAF0 [CAN Auto Formatting off] CAN Auto Formatting is disabled
     */
    ELM327_OBD_CAN_AUTO_FORMATTING_OFF("CAF0"),

    /**
     * CAF1 [CAN Auto Formatting on] CAN Auto formatting is enabled
     */
    ELM327_OBD_CAN_AUTO_FORMATTING_ON("CAF1"),

    /**
     * CEA [ turn off the CAN Extended Address ] The CEA command is used to turn off the special
     * features that are set with the CEA hh command.
     */
    ELM327_CAN_EXTENDED_ADDRESS_OFF("CEA"),

    /**
     * CEA hh [ set the CAN Extended Address to hh ] Some (non-OBD) CAN protocols extend the
     * addressing fields by using the first of the eight data bytes as a target (receiver) address.
     * This command allows the ELM327 to interact with those protocols. Sending the CEA hh command
     * causes the ELM327 to insert the hh value as the first data byte of all CAN messages that you
     * send. It also adds one more filtering step to received messages, only passing ones that have
     * the Tester Address in the first byte position (in addition to requiring that ID bits match
     * the patterns set by AT CF and CM, or CRA). The AT CEA hh command can be sent at any time, and
     * changes are effective immediately, allowing for changes of the address ‘on-the-fly’. There is
     * a more lengthy discussion of extended addressing in the ‘Using CAN Extended Addresses’
     * section on page 61. The CEA mode of operation is off by default, and once on, can be turned
     * off at any time by sending AT CEA, with no address. Note that the CEA setting has no effect
     * when J1939 formatting is on.
     */
    ELM327_CAN_EXTENDED_ADDRESS_ON("CEA%d"),

    /**
     * CF hhh [ set the CAN ID Filter to hhh ] The CAN Filter works in conjunction with the CAN Mask
     * to determine what information is to be accepted by the receiver. As each message is received,
     * the incoming CAN ID bits are compared to the CAN Filter bits (when the mask bit is a ‘1’). If
     * all of the relevant bits match, the message will be accepted, and processed by the ELM327,
     * otherwise it will be discarded. This three nibble version of the CAN Filter command makes it
     * a little easier to set filters with 11 bit ID CAN systems. Only the rightmost 11 bits of the
     * provided nibbles are used, and the most significant bit is ignored. The data is actually
     * stored as four bytes internally however, with this command adding leading zeros for the other
     * bytes. See the CM command(s) for more details.
     */
    ELM327_CAN_ID_FILTER_11_BIT("CF%d"),

    /**
     * CF hh hh hh hh [ set the CAN ID Filter to hhhhhhhh ] This command allows all four bytes
     * (actually 29 bits) of the CAN Filter to be set at once. The 3 most significant bits will
     * always be ignored, and may be given any value. This command may be used to enter 11 bit ID
     * filters as well, since they are stored in the same locations internally (entering AT CF 00 00
     * 0h hh is exactly the same as entering the shorter AT CF hhh command).
     */
    ELM327_CAN_ID_FILTER_29_BIT("CF%d"),

    /**
     * CFC0 and CFC1 [ CAN Flow Control off or on ] The ISO 15765-4 CAN protocol expects a ‘Flow
     * Control’ message to always be sent in response to a ‘First Frame’ message, and the ELM327
     * automatically sends these without any intervention by the user. If experimenting with a
     * non-OBD system, it may be desirable to turn this automatic response off, and the AT CFC0
     * command has been provided for that purpose. As of firmware version 2.0, these commands also
     * enable or disable the sending of J1939 TP.CM_CTS messages in response to TP.CM_RTS requests.
     * During monitoring (AT MA, MR, or MT), there are never any Flow Controls sent no matter what
     * the CFC option is set to. The default setting is CFC1 - Flow Controls on.
     */

    /**
     * CFC0 [ CAN Flow Control off ] Turns off CAN Flow Control
     */
    ELM327_CAN_FLOW_CONTROL_OFF("CFC0"),

    /**
     * CFC1 [ CAN Flow Control on ] Turns on CAN Flow Control
     */
    ELM327_CAN_FLOW_CONTROL_ON("CFC1"),

    /**
     * CM hhh [ set the CAN ID Mask to hhh ] There can be a great many messages being transmitted in
     * a CAN system at any one time. In order to limit what the ELM327 views, there needs to be a
     * system of filtering out the relevant ones from all the others. This is accomplished by the
     * filter, which works in conjunction with the mask. A mask is a group of bits that show the
     * ELM327 which bits in the filter are relevant, and which ones can be ignored. A ‘must match’
     * condition is signalled by setting a mask bit to '1', while a 'don't care' is signalled by
     * setting a bit to '0'. This three digit variation of the CM command is used to provide mask
     * values for 11 bit ID systems (the most significant bit is always ignored). Note that a common
     * storage location is used internally for the 29 bit and 11 bit masks, so an 11 bit mask could
     * conceivably be assigned with the next command (CM hh hh hh hh), should you wish to do the
     * extra typing. The values are right justified, so you would need to provide five leading zeros
     * followed by the three mask bytes.
     */
    ELM327_CAN_ID_MASK_11_BIT("CM%d"),

    /**
     * CM hh hh hh hh [ set the CAN ID Mask to hhhhhhhh ] This command is used to assign mask values
     * for 29 bit ID systems. See the discussion under the CM hhh command as it is essentially
     * identical, except for the length. Note that the three most significant bits that you provide
     * in the first digit will be ignored.
     */
    ELM327_CAN_ID_MASK_29_BIT("CM%d"),

    /**
     * CP hh [ set CAN Priority bits to hh ] This command is used to assign the five most
     * significant bits of the 29 bit CAN ID that is used for sending messages (the other 24 bits
     * are set with the AT SH command). Many systems use these bits to assign a priority value to
     * messages, and to determine the protocol. Any bits provided in excess of the five required are
     * ignored, and not stored by the ELM327 (it only uses the five least significant bits of this
     * byte). The default value for these priority bits is hex 18, which can be restored at any time
     * with the AT D command.
     */
    ELM327_CAN_PRIORITY_BITS("CP%d"),

    /**
     * CRA [ reset the CAN Rx Addr ] The AT CRA command is used to restore the CAN receive filters
     * to their default values. Note that it does not have any arguments (ie no data).
     */
    ELM327_CAN_RESET_RECEIVE_ADDRESS("CRA"),

    /**
     * CRA hhh [ set the CAN Rx Addr to hhh ] Setting the CAN masks and filters can be difficult at
     * times, so if you only want to receive information from one address (ie. one CAN ID), then
     * this command may be very welcome. For example, if you only want to see information from 7E8,
     * simply send AT CRA 7E8, and the ELM327 will make the necessary adjustments to both the mask
     * and the filter for you. If you wish to allow the reception of a range of values, you can use
     * the letter X to signify a ‘don’t care’ condition. That is, AT CRA 7EX would allow all IDs
     * that start with 7E to pass (7E0, 7E1, etc.). For a more specific range of IDs, you may need
     * to assign a mask and filter. To reverse the changes made by the CRA command, simply send AT
     * CRA or AT AR.
     */
    ELM327_CAN_SET_RECEIVE_ADDRESS_11_BIT("CRA%d"),

    /**
     * CRA hhhhhhhh [set the CAN Rx Addr to hhhhhhhh] This command is identical to the previous one,
     * except that it is used with 29 bit CAN IDs. Sending either AT CRA or AT AR will also reverse
     * any changes made by this command.
     */
    ELM327_CAN_SET_RECEIVE_ADDRESS_29_BIT("CRA%d"),

    /**
     * CS [ show the CAN Status counts ] The CAN protocol requires that statistics be kept regarding
     * the number of transmit and receive errors 16 of 94 ELM327 ELM327DSJ Elm Electronics –
     * Circuits for the Hobbyist www.elmelectronics.com detected. If there should be a significant
     * number of errors (due to a hardware or software problem), the device will go off-line in
     * order to not affect other data on the bus. The AT CS command lets you see both the
     * transmitter (Tx) and the receiver (Rx) error counts, in hexadecimal. If the transmitter
     * should be off (count >FF), you will see ‘OFF’ rather than a specific count.
     */
    ELM327_SHOW_CAN_STATUS_COUNTS("CS"),

    /**
     * CSM0 and CSM1 [ CAN Silent Monitoring off or on ] The ELM327 was designed to be completely
     * silent while monitoring a CAN bus. Because of this, it is able to report exactly what it
     * sees, without colouring the information in any way. Occasionally (when bench testing, or when
     * connecting to a dedicated CAN port), it may be preferred that the ELM327 does not operate
     * silently (ie generates ACK bits, etc.), and this is what the CSM command is for. CSM1 turns
     * it on, CSM0 turns it off, and the default value is determined by PP 21. Be careful when
     * experimenting with this. If you should choose the wrong baud rate then monitor the CAN bus
     * with the silent monitoring off, you will disturb the flow of data. Always keep the silent
     * monitoring on until you are certain that you have chosen the correct baud rate.
     */

    /**
     * CSM0 [ CAN Silent Monitoring off ] Turns CAN silent monitoring off
     */
    ELM327_CAN_SILENT_MONITORING_OFF("CSM0"),

    /**
     * CSM1 [ CAN Silent Monitoring on ] Turns CAN silent monitoring on
     */
    ELM327_CAN_SILENT_MONITORING_ON("CSM1"),

    /**
     * CTM1 [ set the Timer Multiplier to 1 ] This command causes all timeouts set by AT ST to be
     * multiplied by a factor of 1. Note that this currently only affects the CAN protocols (6 to
     * C). CTM1 is the default setting.
     */
    ELM327_CAN_SET_TIMER_MULTIPLIER_1("CTM1"),

    /**
     * CTM5 [ set the Timer Multiplier to 5 ] This command causes all timeouts set by AT ST to be
     * multiplied by a factor of 5. Note that this currently only affects the CAN protocols (6 to
     * C). This command was originally added (as JTM5) to assist with the retrieving of some J1939
     * messages. We have since had several requests to allow it to affect all CAN modes, and so have
     * modified the JTM5 code and added the new CTM1/CTM5 commands. If using CTM5, we caution that
     * the Adaptive Timing code does not monitor changes in the setting, so we advise turning it off
     * (with AT AT0). By default, this multiplier is off.
     */
    ELM327_CAN_SET_TIMER_MULTIPLIER_5("CTM5"),

    /**
     * CV dddd [ Calibrate the Voltage to dd.dd volts ] The voltage reading that the ELM327 shows
     * for an AT RV request can be calibrated with this command.  The argument (‘dddd’) must always
     * be provided as 4 digits, with no decimal point (it assumes that the decimal place is between
     * the second and the third digits). To use this feature, simply use an accurate meter to read
     * the actual input voltage, then use the CV command to change the internal calibration
     * (scaling) factor. For example, if the ELM327 shows the voltage as 12.2V while you measure
     * 11.99 volts, then send AT CV 1199 and the ELM327 will recalibrate itself for that voltage (it
     * will actually read 12.0V due to digit roundoff). See page 29 for some more information on how
     * to read voltages and perform the calibration.
     */
    ELM327_VOLTAGE_READING_CALIBRATE_VOLTAGE("CV %d"),

    /**
     * CV 0000 [ restore the factory Calibration Value ] If you are experimenting with the CV dddd
     * command but do not have an accurate voltmeter as a reference, you may soon get into trouble.
     * If this happens, you can always send AT CV 0000 to restore the ELM327 to the original
     * calibration value.
     */
    ELM327_VOLTAGE_READING_RESTORE_FACTORY_CALIBRATION("CV0000"),

    /**
     * D [ set all to Defaults ] This command is used to set the options to their default (or
     * factory) settings, as when power is first applied. The last stored protocol will be retrieved
     * from memory, and will become the current setting (possibly closing other protocols that are
     * active). Any settings that the user had made for custom headers, filters, or masks will be
     * restored to their default values, and all timer settings will also be restored to their
     * defaults.
     */
    ELM327_RESET_TO_DEFAULT("D"),

    /**
     * D0 and D1 [ display of DLC off or on ] Standard CAN (ISO 15765-4) OBD requires that all
     * messages have 8 data bytes, so displaying the number of data bytes (the DLC) is not normally
     * very useful. When experimenting with other protocols, however, it may be useful to be able to
     * see what the data lengths are. The D0 and D1 commands control the display of the DLC digit
     * (the headers must also be on in order to see this digit). When displayed, the single DLC
     * digit will appear between the ID (header) bytes and the data bytes. The default setting is
     * determined by PP 29.
     */

    /**
     * D0 [ display of DLC off ] Turns displaying of DLC off
     */
    ELM327_CAN_DISPLAY_DLC_OFF("D0"),

    /**
     * D1 [ display of DLC on ] Turns displaying of DLC on
     */
    ELM327_CAN_DISPLAY_DLC_ON("D1"),

    /**
     * DM1 [ monitor for DM1s ] The SAE J1939 Protocol broadcasts trouble codes periodically, by way
     * of Diagnostic Mode 1 (DM1) messages. This command sets the ELM327 to continually monitor for
     * this type of message for you, following multi-segment transport protocols as required. Note
     * that a combination of masks and filters could be set to provide a similar output, but they
     * would not allow multiline messages to be detected. The DM1 command adds the extra logic that
     * is needed for multiline messages. This command is only available when a CAN Protocol (A, B,
     * or C) has been selected for J1939 formatting. It returns an error if attempted under any
     * other conditions.
     */
    ELM327_J1939_CAN_ENABLE_DM1("DM1"),

    /**
     * DP [ Describe the current Protocol ] The ELM327 automatically detects a vehicle’s OBD
     * protocol, but does not normally report what it is. The DP command is a convenient means of
     * asking what protocol the IC is currently set to (even if it has not yet ‘connected’ to the
     * vehicle). If a protocol is chosen and the automatic option is also selected, AT DP will show
     * the word 'AUTO' before the protocol description. Note that the description shows the actual
     * protocol names, not the numbers used by the protocol setting commands.
     */
    ELM327_DESCRIBE_CURRENT_PROTOCOL("ATDP"),

    /**
     * DPN [ Describe the Protocol by Number ] This command is similar to the DP command, but it
     * returns a number which represents the current protocol. If the automatic search function is
     * also enabled, the number will be preceded with the letter ‘A’. The number is the same one
     * that is used with the set protocol and test protocol commands.
     */
    ELM327_DESCRIBE_CURRENT_PROTOCOL_BY_NUMBER("ATDPN"),

    /**
     * E0 and E1 [ Echo off or on ] These commands control whether or not the characters received on
     * the RS232 port are echoed (retransmitted) back to the host computer. Character echo can be
     * used to confirm that the characters sent to the ELM327 were received correctly. The default
     * is E1 (or echo on).
     */

    /**
     * E0 [ Echo off ] Turns echo off
     */
    ELM327_ECHO_OFF("ATE0"),

    /**
     * E1 [ Echo on ] Turns echo on
     */
    ELM327_ECHO_ON("ATE1"),

    /**
     * FC SD [1-5 bytes] [ Flow Control Set Data to... ] The data bytes that are sent in a CAN Flow
     * Control message may be defined with this command. One to five data bytes may be specified,
     * with the remainder of the data bytes in the message being automatically set to the default
     * CAN filler byte, if required by the protocol. Data provided with this command is only used
     * when Flow Control modes 1 or 2 have been enabled.
     */
    ELM327_CAN_FLOW_CONTROL_SET_DATA("FCSD%d"),

    /**
     * FC SH hhh [ Flow Control Set Header to... ] The header (or more properly ‘CAN ID’) bytes used
     * for CAN Flow Control messages can be set using this command. Only the right-most 11 bits of
     * those provided will be used - the most significant bit is always removed. This command only
     * affects Flow Control mode 1.
     */
    ELM327_CAN_FLOW_CONTROL_SET_HEADER_11_BIT("FCSH%d"),

    /**
     * FC SH hhhhhhhh [ Flow Control Set Header to... ] This command is used to set the header (or
     * ‘CAN ID’) bits for Flow Control responses with 29 bit CAN ID systems. Since the 8 nibbles
     * define 32 bits, only the right-most 29 bits of those provided will be used - the most
     * significant three bits are always removed. This command only affects Flow Control mode 1.
     */
    ELM327_CAN_FLOW_CONTROL_SET_HEADER_29_BIT("FCSH%d"),

    /**
     * FC SM h [ Flow Control Set Mode to h ] This command sets how the ELM327 responds to First
     * Frame messages when automatic Flow Control responses are enabled. The single digit provided
     * can either be ‘0’ (the default) for fully automatic responses, ‘1’ for completely user
     * defined responses, or ‘2’ for user defined data bytes in the response. Note that FC modes 1
     * and 2 can only be enabled if you have defined the needed data and possibly ID bytes. If you
     * have not, you will get an error. More complete details and examples can be found in the
     * Altering Flow Control Messages section (page 60).
     */
    ELM327_CAN_FLOW_CONTROL_SET_MOE("FCSM%d"),

    /**
     * FE [ Forget Events ] There are certain events which may change how the ELM327 responds from
     * that time onwards. One of these is the occurrence of a fatal CAN error (ERR94), which blocks
     * subsequent searching through CAN protocols if PP 2A bit 5 is ‘1’. Normally, an event such as
     * this will affect all searches until the next power off and on, but it can be ‘forgotten’
     * using software, with the AT FE command. Another example is an ‘LV RESET’ event which will
     * prevent searches through CAN protocols if PP 2A bit 4 is ‘1’. It may also be forgotten with
     * the AT FE command.
     */
    ELM327_FORGET_EVENTS("FE"),

    /**
     * FI [ perform a Fast Initiation ] One version of the Keyword protocol uses what is known as a
     * 'fast initiation' sequence to begin communications. Usually, this sequence is performed when
     * the first message needs to be sent, and then the message is sent immediately after. Some ECUs
     * may need more time between the two however, and having a separate initiation command allows
     * you to control this time. Simply send AT FI, wait a little, then send the message. You may
     * need to experiment to get the right amount of delay. Another use for this command might be if
     * you would like to perform a fast initiation with an ISO 9141 type protocol (ie 3 - CARB
     * format). Simply follow these steps to do that: AT SP 5 AT FI AT SP 3 AT BI and you should be
     * able to then communicate with the ECU. Note that a protocol close (ie AT PC) is not required
     * in the above code, as the ELM327 automatically performs one when you switch protocols.
     * Protocol 5 must be selected to use the AT FI command, or an error will result.
     */
    ELM327_ISO_FAST_INITIATION("ATFI"),

    /**
     * H0 and H1 [ Headers off or on ] These commands control whether or not the additional (header)
     * bytes of information are shown in the responses from the vehicle. These are not normally
     * shown by the ELM327, but may be of interest (especially if you receive multiple responses and
     * wish to determine what modules they were from). Turning the headers on (with AT H1) actually
     * shows more than just the header bytes – you will see the complete message as transmitted,
     * including the check-digits and PCI bytes, and possibly the CAN data length code (DLC) if it
     * has been enabled with PP 29 or AT D1. The current version of this IC does not display the CAN
     * CRC code, nor the special J1850 IFR bytes (which some protocols use to acknowledge receipt of
     * a message).
     */

    /**
     * H0 [ Headers off ] Disables the display of header bytes
     */
    ELM327_OBD_HEADERS_OFF("H0"),

    /**
     * H1 [ Headers on ] Enables the display of header bytes
     */
    ELM327_OBD_HEADERS_ON("H1"),

    /**
     * I [ Identify yourself ] Issuing this command causes the chip to identify itself, by printing
     * the startup product ID string (currently ‘ELM327 v2.1’). Software can use this to determine
     * exactly which integrated circuit it is talking to, without having to reset the IC.
     */
    ELM327_IDENTIFY("I"),

    /**
     * IB 10 [ set the ISO Baud rate to 10400 ] This command restores the ISO 9141-2 and ISO 14230-4
     * baud rates to the default value of 10400.
     */
    ELM327_ISO_BAUD_RATE_10400("IB10"),

    /**
     * IB 48 [ set the ISO Baud rate to 4800 ] This command is used to change the baud rate used for
     * the ISO 9141-2 and ISO 14230-4 protocols (numbers 3, 4, and 5) to 4800 baud, while relaxing
     * some of the requirements for the initiation byte transfers. It may be useful for
     * experimenting with some vehicles. Normal (10,400 baud) operation may be restored at any time
     * with the IB 10 command.
     */
    ELM327_ISO_BAUD_RATE_4800("IB48"),

    /**
     * IB 96 [ set the ISO Baud rate to 9600 ] This command is used to change the baud rate used for
     * the ISO 9141-2 and ISO 14230-4 protocols (numbers 3, 4, and 5) to 9600 baud, while relaxing
     * some of the requirements for the initiation byte transfers. It may be useful for
     * experimenting with some vehicles. Normal (10,400 baud) operation may be restored at any time
     * with the IB 10 command.
     */
    ELM327_ISO_BAUD_RATE_9600("IB96"),

    /**
     * IFR0, IFR1 , and IFR2 [ IFR control ] The SAE J1850 protocol allows for an In-Frame Response
     * (IFR) byte to be sent after each message, usually to acknowledge the correct receipt of that
     * message. The ELM327 automatically generates and sends this byte for you by default, but you
     * can override this behaviour with this command. The AT IFR0 command will disable the sending
     * of all IFRs, no matter what the header bytes require. AT IFR2 is the opposite - it will cause
     * an IFR byte to always be sent, no matter what the header bytes say. The AT IFR1 command is
     * the default mode, with the sending of IFRs determined by the ‘K’ bit of the first header byte
     * (for both PWM and VPW)
     */

    /**
     * IFR0 [ IFR control mode 0 ] Completely disables in frame responses
     */
    ELM327_J1850_DISABLE_IN_FRAME_RESPONSE("IFR0"),

    /**
     * IFR1 [ IFR control mode 1 ] In frame responses determined by k-bit of header (default)
     */
    ELM327_J1850_K_BIT_IN_FRAME_RESPONSE("IFR1"),

    /**
     * IFR2 [ IFR control mode 2] In frame responses forced in all circumstances
     */
    ELM327_J1850_FORCE_IN_FRAME_RESPONSE("IFR2"),

    /**
     * IFR H and IFR S [ IFR from Header or Source ] The value sent in the J1850 In-Frame Response
     * (IFR) byte is normally the same as the value sent as the Source (or Tester) Address byte that
     * was in the header of the request. There may be occasions when it is desirable to use some
     * other value, however, and this set of commands allows for this.  If you send AT IFR S, the
     * ELM327 will use the value defined as the Source Address (usually F1, but it can be changed
     * with PP 06), even if another value was sent in the Header bytes. This is not what is normally
     * required, and caution should be used when using AT IFR S. AT IFR H restores the sending of
     * the IFR bytes to those provided in the Header, and is the default setting.
     */

    /**
     * IFRH [ IFR from Header ] Sets the IFR byte to those provided in the header (default)
     */
    ELM327_J1850_IN_FRAME_RESPONSE_FROM_HEADER("IFRH"),

    /**
     * IFRS [ IFR from Source ] Sets the IFR byte to the Source Address byte
     */
    ELM327_J1850_IN_FRAME_RESPONSE_FROM_SOURCE("IFRS"),

    /**
     * IGN [ read the IgnMon input level ] This command reads the signal level at pin 15. It assumes
     * that the logic level is related to the ignition voltage, so if the input is at a high level,
     * the response will be ‘ON’, and a low level will report ‘OFF’. This feature is most useful if
     * you wish to perform the power control functions using your own software. If you disable the
     * Low Power automatic response to a low input on this pin (by setting bit 2 of PP 0E to 0),
     * then pin 15 will function as the RTS input. A low level on the input will not turn the power
     * off, but it will interrupt any OBD activity that is in progress. All you need to do is detect
     * the ‘STOPPED’ message that is sent when the ELM327 is interrupted, and then check the level
     * at pin 15 using AT IGN. If it is found to be OFF, you can perform an orderly shutdown
     * yourself.
     */
    ELM327_OTHER_IGNITION_MONITOR_INPUT_LEVEL("IGN"),

    /**
     * IIA hh [ set the ISO Init Address to hh ] The ISO 9141-2 and ISO 14230-4 standards state that
     * when beginning a session with an ECU, the initiation sequence is to be directed to a specific
     * address ($33). If you wish to experiment by directing the slow five baud sequence to another
     * address, it is done with this command. For example, if you prefer that the initiation be
     * performed with the ECU at address $7A, then simply send: >AT IIA 7A and the ELM327 will use
     * that address when called to do so (protocols 3 or 4). The full eight bit value is used
     * exactly as provided – no changes are made to it (ie no adding of parity bits, etc.) Note that
     * setting this value does not affect any address values used in the header bytes. The ISO init
     * address is restored to $33 whenever the defaults, or the ELM327, are reset.
     */
    ELM327_ISO_SET_INIT_ADDRESS("IIA%d"),

    /**
     * JE [ enables the J1939 ELM data format ] The J1939 standard requires that PGN requests be
     * sent with the byte order reversed from the standard ‘left-to-right’ order, which many of us
     * would expect. For example, to send a request for the engine temperature (PGN 00FEEE), the
     * data bytes are actually sent in the reverse order (ie EE FE 00), and the ELM327 would
     * normally expect you to provide the data in that order for passing on to the vehicle. When
     * experimenting, this constant need for byte reversals can be quite confusing, so we have
     * defined an ELM format that reverses the bytes for you. When the J1939 ELM (JE) format is
     * enabled, and you have a J1939 protocol selected, and you provide three data bytes to the
     * ELM327, it will reverse the order for you before sending them to the ECU. To request the
     * engine temperature PGN, you would send 00 FE EE (and not EE FE 00). The ‘JE’ type of
     * automatic formatting is enabled by default.
     */
    ELM327_ENABLE_J1939_ELM_DATA_FORMAT("JE"),

    /**
     * JHF0 and JHF1 [ J1939 Header Formatting off or on ] When printing responses, the ELM327
     * normally formats the J1939 ID (ie Header) bits in such a way as to isolate the priority bits
     * and group all the PGN information, while keeping the source address byte separate. If you
     * prefer to see the ID information as four separate bytes (which a lot of the J1939 software
     * seems to do), then simply turn off the formatting with JHF0. The CAF0 command has the same
     * effect (and overrides the JHF setting), but also affects other formatting. The default
     * setting is JHF1.
     */

    /**
     * JHF0 [ J1939 Header Formatting off ] Turns off header formatting for the J1939 protocol
     */
    ELM327_J1939_HEADER_FORMATTING_OFF("JHF0"),

    /**
     * JHF1 [J1939 Header Formatting on ] Turns on header formatting for the J1939 protocol
     */
    ELM327_J1939_HEADER_FORMATTING_ON("JHF1"),

    /**
     * JS [ enables the J1939 SAE data format ] The AT JS command disables the automatic byte
     * reordering that the JE command performs for you. If you wish to send data bytes to the J1939
     * vehicle without any manipulation of the byte order (ie in the order specified by the SAE
     * documents), then select JS formatting. As an example, when sending a request for engine
     * temperature (PGN 00FEEE) with the data format set to JS, you must present the bytes to the
     * ELM327 as EE FE 00 (this is also known as little-endian byte ordering). The JS type of data
     * formatting is off by default.
     */
    ELM327_J1939_ENABLE_J1939_SAE_DATA_FORMAT("JS"),

    /**
     * JTM1 [ set the J1939 Timer Multiplier to 1 ] This used to set the AT ST time multiplier to 1,
     * for the SAE J1939 protocol. As of firmware v2.1, this command now simply calls the CTM1
     * command.
     */
    ELM327_J1939_SET_J1939_TIMER_MULTIPLIER_1("JTM1"),

    /**
     * JTM5 [ set the J1939 Timer Multiplier to 5 ] This used to set the AT ST time multiplier to 5,
     * for the SAE J1939 protocol. As of firmware v2.1, this command now simply calls the CTM5
     * command.
     */
    ELM327_J1939_SET_J1939_TIMER_MULTIPLIER_5("JTM5"),

    /**
     * KW [ display the Key Words ] When the ISO 9141-2 and ISO 14230-4 protocols are initialized,
     * two special bytes (key words) are passed to the ELM327 (the values are used internally to
     * determine whether a particular protocol variation can be supported by the ELM327). If you
     * wish to see what the value of these bytes were, simply send the AT KW command.
     */
    ELM327_ISO_DISPLAY_KEY_WORDS("KW"),

    /**
     * KW0 and KW1 [ Key Word checks off or on ] The ELM327 looks for specific bytes (called key
     * words) to be sent to it during the ISO 9141-2 and ISO14230-4 initiation sequences. If the
     * bytes are not found, the initiation is said to have failed (you might see ‘UNABLE TO CONNECT’
     * or perhaps ‘BUS INIT: ...ERROR’). This might occur if you are trying to connect to a non-OBD
     * compliant ECU, or perhaps to an older one. If you wish to experiment with non-standard
     * systems, you may have to tell the ELM327 to perform the initiation sequence, but ignore the
     * contents of the bytes that are sent and received. To do this, send: >AT KW0 After turning
     * keyword checking off, the ELM327 will still require the two key word bytes in the response,
     * but will not look at the actual values of the bytes. It will also send an acknowledgement to
     * the ECU, and will wait for the final response from it (but will not stop and report an error
     * if none is received). This may allow you to make a connection in an otherwise ‘impossible’
     * situation. Normal behaviour can be returned with AT KW1, which is the default setting.
     */

    /**
     * KW0 [ Key Word checks off ] Disables key word checks
     */
    ELM327_ISO_KEY_WORD_CHECKING_OFF("KW0"),

    /**
     * KW1 [ Key Word checks on ] Enables key word checks
     */
    ELM327_ISO_KEY_WORD_CHECKING_ON("KW1"),

    /**
     * L0 and L1 [ Linefeeds off or on ] This option controls the sending of linefeed characters
     * after each carriage return character. For AT L1, linefeeds will be generated after every
     * carriage return character, and for AT L0, they will be off. Users will generally wish to have
     * this option on if using a terminal program, but off if using a custom computer interface (as
     * the extra characters transmitted will only serve to slow the communications down). The
     * default setting is determined by the voltage at pin 7 during power on (or reset). If the
     * level is high, then linefeeds are on by default; otherwise they will be off.
     */

    /**
     * L0 [ Linefeeds off ] Disables transmission of linefeed characters
     */
    ELM327_LINEFEEDS_OFF("ATL0"),

    /**
     * L1 [ Linefeeds on ] Enables transmission of linefeed characters
     */
    ELM327_LINEFEEDS_ON("ATL1"),

    /**
     * LP [ go to the Low Power mode ] This command causes the ELM327 to shut off all but ‘essential
     * services’ in order to reduce the power consumption to a minimum. The ELM327 will respond with
     * an ‘OK’ (but no carriage return) and then, one second later, will change the state of the
     * PwrCtrl output (pin 16) and will enter the low power (standby) mode. The IC can be brought
     * back to normal operation through a character received at the RS232 input or a rising edge at
     * the IgnMon (pin 15) input, in addition to the usual methods of resetting the IC (power off
     * then on, a low on pin 1, or a brownout). See the Power Control section (page 64) for more
     * information.
     */
    ELM327_ENTER_LOW_POWER_MODE("LP"),

    /**
     * M0 and M1 [ Memory off or on ] The ELM327 has internal ‘non-volatile’ memory that is capable
     * of remembering the last protocol used, even after the power is turned off. This can be
     * convenient if the IC is often used for one particular protocol, as that will be the first one
     * attempted when next powered on. To enable this memory function, it is necessary to either use
     * an AT command to select the M1 option, or to have chosen ‘memory on’ as the default power on
     * mode (by connecting pin 5 of the ELM327 to a high logic level). When the memory function is
     * enabled, each time that the ELM327 finds a valid OBD protocol, that protocol will be
     * memorized (stored) and will become the new default. If the memory function is not enabled,
     * protocols found during a session will not be memorized, and the ELM327 will always start at
     * power up using the same (last saved) protocol. If the ELM327 is to be used in an environment
     * where the protocol is constantly changing, it would likely be best to turn the memory
     * function off, and issue an AT SP 0 command once. The SP 0 command tells the ELM327 to start
     * in an 'Automatic' protocol search mode, which is the most useful for an unknown environment.
     * ICs come from the factory set to this mode. If, however, you have only one vehicle that you
     * regularly connect to, storing that vehicle’s protocol as the default would make the most
     * sense.  The default setting for the memory function is determined by the voltage level at pin
     * 5 during power up (or system reset). If it is connected to a high level (V DD ), then the
     * memory function will be on by default. If pin 5 is connected to a low level, the memory
     * saving will be off by default.
     */

    /**
     * M0 [ Memory off ] Disables internal 'non-volatile' memory for the remainder of the session
     */
    ELM327_MEMORY_OFF("M0"),

    /**
     * M1 [ Memory on ] Enables internal 'non-volatile' memory for the remainder of the session
     */
    ELM327_MEMORY_ON("M1"),

    /**
     * MA [ Monitor All messages ] This command places the ELM327 into a bus monitoring mode, in
     * which it continually monitors for (and displays) all messages that it sees on the OBD bus. It
     * is a quiet monitor, not sending In Frame Responses for J1850 systems, Acknowledges for CAN
     * systems (unless you turn silent mode off with CSM0), or Wakeup (‘keep-alive’) messages for
     * the ISO 9141 and ISO 14230 protocols. Monitoring will continue until you stop it with
     * activity on the RS232 input, or the RTS pin. To stop monitoring, simply send any single
     * character to the ELM327, then wait for it to respond with a prompt character (‘ > ’), or a
     * low level output on the Busy pin. (Setting the RTS input to a low level will interrupt the
     * device as well.) Waiting for the prompt is necessary as the response time varies depending on
     * what the IC was doing when it was interrupted. If for instance it was in the middle of
     * printing a line, it will first complete that line and then print ‘STOPPED’, before returning
     * to the command state and sending a prompt character. If it were simply waiting for input, it
     * would return much quicker. Note that the character which stops the monitoring will always be
     * discarded, and will not affect subsequent commands. If this command is used with CAN
     * protocols, and if the CAN filter and/or mask were previously set (with CF, CM or CRA), then
     * the MA command will be affected by those settings. For example, if the receive address had
     * been set previously with CRA 4B0, then the AT MA command would only be able to ‘see’ messages
     * with an ID of 4B0. This may not be what is desired - you may want to reset the masks and
     * filters (with AT AR or AT CRA) first. All of the monitoring commands (MA, MR and MT) operate
     * by closing the current protocol (an AT PC is executed internally), before configuring the IC
     * for monitoring the data. When the next OBD command is to be transmitted, the protocol will
     * again be initialized, and you may see messages stating this. ‘SEARCHING...’ may also be seen,
     * depending on what changes were made while monitoring.
     */
    ELM327_OBD_MONITOR_ALL_MESSAGES("MA"),

    /**
     * MP hhhh [ Monitor for PGN hhhh ] The AT MA, MR and MT commands are quite useful for when you
     * wish to monitor for a specific byte in the header of a typical OBD message. For the SAE J1939
     * Protocol, however, it is often desirable to monitor for the multi-byte Parameter Group
     * Numbers (or PGNs), which can appear in either the header, or the data bytes. The MP command
     * is a special J1939 only command that is used to look for responses to a particular PGN
     * request. Note that this MP command provides no means to set the first two digits of the
     * requested PGN, and they are always assumed to be 00. For example, the DM2 PGN has an assigned
     * value of 00FECB (see SAE J1939-73). To monitor for DM2 messages, you would issue AT MP FECB,
     * eliminating the 00, since the MP hhhh command always assumes that the PGN is preceded by two
     * zeros. This command is only available when a CAN Protocol (A, B, or C) has been selected for
     * SAE J1939 formatting. It returns an error if attempted under any other conditions. Note also
     * that this version of the ELM327 only displays responses that match the criteria, not the
     * requests that are asking for the PGN information.
     */
    ELM327_J1939_MONITOR_FOR_PNG_HHHH("MP%d"),

    /**
     * MP hhhh n [ Monitor for PGN, get n messages ] This is very similar to the above command, but
     * adds the ability to set the number of messages that should be fetched before the ELM327
     * automatically stops monitoring and prints a prompt character. The value ‘n’ may be any single
     * hex digit.
     */
    ELM327_J1939_MONITOR_FOR_PNG_HHHH_N("MP%d%d"),

    /**
     * MP hhhhhh [ Monitor for PGN hhhhhh ] This command is very similar to the MP hhhh command, but
     * it extends the number of bytes provided by one, so that there is complete control over the
     * PGN definition (it does not make the assumption that the Data Page bit is 0, as the MP hhhh
     * command does). This allows for future expansion, should additional PGNs be defined with the
     * Data Page bit set. Note that only the Data Page bit is relevant in the extra byte - the other
     * bits are ignored.
     */
    ELM327_J1939_MONITOR_FOR_PNG_HHHHHH("MP%d"),

    /**
     * MP hhhhhh n [ Monitor for PGN, get n messages ] This is very similar to the previous command,
     * but it adds the ability to set the number of messages that should be fetched before the
     * ELM327 automatically stops monitoring and prints a prompt character. The value ‘n’ may be any
     * single hex digit.
     */
    ELM327_J1939_MONITOR_FOR_PNG_HHHHHH_N("MP%d%d"),

    /**
     * MR hh [ Monitor for Receiver hh ] This command is very similar to the AT MA command except
     * that it will only display messages that were sent to the hex address given by hh. These are
     * messages which are found to have the value hh in the second byte of a traditional three byte
     * OBD header, in bits 8 to 15 of a 29 bit CAN ID, or in bits 8 to 10 of an 11 bit CAN ID. Any
     * single RS232 character aborts the monitoring, as with the MA command. Note that if this
     * command is used with CAN protocols, and if the CAN filter and/or mask were previously set
     * (with CF, CM or CRA), then the MR command will over-write the previous values for these bits
     * only - the others will remain unchanged. As an example, if the receive address has been set
     * with CRA 4B0, and then you send MR 02, the 02 will replace the 4, and the CAN masks/filters
     * will only allow IDs that are equal to 2B0. This is often not what is desired - you may want
     * to reset the masks and filters (with AT AR) first. As with the AT MA command, this command
     * begins by performing an internal Protocol Close. Subsequent OBD requests may show ‘SEARCHING’
     * or ‘BUS INIT’, etc. messages when the protocol is reactivated.
     */
    ELM327_OBD_MONITOR_FOR_RECEIVER("MR%d"),

    /**
     * MT hh [ Monitor for Transmitter hh ] This command is also very similar to the AT MA command,
     * except that it will only display messages that were sent by the transmitter with the hex
     * address given by hh. These are messages which are found to have that value in the third byte
     * of a traditional three byte OBD header, or in bits 0 to 7 for CAN IDs. As with the MA and MR
     * monitoring modes, any RS232 activity (single character) aborts the monitoring. Note that if
     * this command is used with CAN protocols, and if the CAN filter and/or mask were previously
     * set (with CF, CM or CRA), then the MT command will over-write the previous values for these
     * bits only - the others will remain unchanged. As an example, if the receive address has been
     * set with CRA 4B0, and then you send MT 20, the 20 will replace the B0, and the CAN
     * masks/filters will only allow IDs that are equal to 420. This is often not what is desired -
     * you may want to reset the masks and filters (with AT AR) first.  As with the AT MA command,
     * this command begins by performing an internal Protocol Close. Subsequent OBD requests may
     * show ‘SEARCHING’ or ‘BUS INIT’, etc. messages when the protocol is reactivated.
     */
    ELM327_OBD_MONITOR_FOR_TRANSMITTER("MT%d"),

    /**
     * NL [ Normal Length messages ] Setting the NL mode on forces all sends and receives to be
     * limited to the standard seven data bytes in length, similar to the other ELM32x OBD ICs. To
     * allow longer messages, use the AL command. Beginning with v1.2, the ELM327 does not require a
     * change to AL to allow longer message lengths for the KWP protocols to be received (as
     * determined by the header length values). You can simply leave the IC set to the default
     * setting of NL, and all of the received bytes will be shown.
     */
    ELM327_OBD_NORMAL_LENGTH_MESSAGES("NL"),

    /**
     * PB xx yy [ set Protocol B parameters ] This command allows you to change the protocol B
     * (USER1) options and baud rate without having to change the associated Programmable
     * Parameters. This allows for easier testing, and program control. To use this feature, simply
     * set xx to the value for PP 2C, and yy to the value for PP 2D, and issue the command. The next
     * time that the protocol is initialized it will use these values. For example, assume that you
     * wish to try monitoring a system that uses 11 bit CAN at 33.3 kbps. If you do not want any
     * special formatting, this means a value of 11000000 or C0 hex for PP 2C, and 15 decimal or 0F
     * hex for PP 2D. Send both of these values to the ELM327 in one command: >AT PB C0 0F then
     * monitor: >AT MA if you see CAN ERRORs, and realize that you wanted an 83.3 kbps baud rate,
     * simply close the protocol, and then send the new values: >AT PC OK >AT PB C0 06 OK >AT MA
     * Values passed in this way do not affect those that are stored in the 2C and 2D Programmable
     * Parameters, and are lost if the ELM327 is reset. If you want to make your settings persist
     * over power cycles, then you may wish to store them in the Programmable Parameter for CAN
     * protocols USER1 or USER2.
     */
    ELM327_CAN_SET_PROTOCOL_B_PARAMETERS("PB%d%d"),

    /**
     * PC [ Protocol Close ] There may be occasions where it is desirable to stop (deactivate) a
     * protocol. Perhaps you are not using the automatic protocol finding, and wish to manually
     * activate and deactivate protocols. Perhaps you wish to stop the sending of idle (wakeup)
     * messages, or have another reason. The PC command is used in these cases to force a protocol
     * to close.
     */
    ELM327_OBD_PROTOCOL_CLOSE("PC"),

    /**
     * PP hh OFF [ turn Prog. Parameter hh OFF ] This command disables Programmable Parameter number
     * hh. Any value assigned using the PP hh SV command will no longer be used, and the factory
     * default setting will once again be in effect. The actual time when the new value for this
     * parameter becomes effective is determined by its type. Refer to the Programmable Parameters
     * section (page 69) for more information on the types. Note that ‘PP FF OFF’ is a special
     * command that disables all of the Programmable Parameters, as if you had entered PP OFF for
     * every possible one. It is possible to alter some of the Programmable Parameters so that it
     * may be difficult, or even impossible, to communicate with the ELM327. If this occurs, there
     * is a hardware means of resetting all of the Programmable Parameters at once. Connect a jumper
     * from circuit common to pin 28, holding it there while powering up the ELM327 circuit. Hold it
     * in position until you see the RS232 Receive LED begin to flash (which indicates that all of
     * the PPs have been turned off). At this point, remove the jumper to allow the IC to perform a
     * normal startup. Note that a reset of the PPs occurs quite quickly – if you are holding the
     * jumper on for more than a few seconds and do not see the RS232 receive light flashing, remove
     * the jumper and try again, as there may be a problem with your connection.
     */
    ELM327_PROGRAMMABLE_PARAMETER_DISABLE("PP%dOFF"),

    /**
     * PP FF OFF [ Disables all programmable parameters ]
     */
    ELM327_PROGRAMMABLE_PARAMETER_DISABLE_ALL("PPFFOFF"),

    /**
     * PP hh ON [ turn Programmable Parameter hh ON ] This command enables Programmable Parameter
     * number hh. Once enabled, any value assigned using the PP hh SV command will be used where the
     * factory default value was before. (All of the programmable parameter values are set to their
     * default values at the factory, so enabling a programmable parameter before assigning a value
     * to it will not cause problems.) The actual time when the value for this parameter becomes
     * effective is determined by its type. Refer to the Programmable Parameters section (page 69)
     * for more information on the types. Note that ‘PP FF ON’ is a special command that enables all
     * of the Programmable Parameters at the same time.
     */
    ELM327_PROGRAMMABLE_PARAMETER_ENABLE("PP%dON"),

    /**
     * PP FF ON [ Enables all programmable parameters ]
     */
    ELM327_PROGRAMMABLE_PARAMETER_ENABLE_ALL("PPFFON"),

    /**
     * PP hh SV yy [ Prog. Param. hh: Set the Value to yy ] A value is assigned to a Programmable
     * Parameter using this command. The system will not be able to use this new value until the
     * Programmable Parameter has been enabled, with PP hh ON.
     */
    ELM327_PROGRAMMABLE_PARAMETER_SET_PARAMETER("PP%dSV%d"),

    /**
     * PPS [ Programmable Parameter Summary ] The complete range of current Programmable Parameters
     * are displayed with this command (even those not yet implemented). Each is shown as a PP
     * number followed by a colon and the value that is assigned to it. This is followed by a single
     * digit – either ‘N’ or ‘F’ to show that it is ON (enabled), or OFF (disabled), respectively.
     * See the Programmable Parameters section for a more complete discussion.
     */
    ELM327_PROGRAMMABLE_PARAMETER_SUMMARY("PPS"),

    /**
     * R0 and R1 [ Responses off or on ] These commands control the ELM327’s automatic receive (and
     * display) of the messages returned by the vehicle. If responses have been turned off, the IC
     * will not wait for a reply from the vehicle after sending a request, and will return
     * immediately to wait for the next RS232 command (the ELM327 does not print anything to say
     * that the send was successful, but you will see a message if it was not). R0 may be useful to
     * send commands blindly when using the IC for a non-OBD network application, or when simulating
     * an ECU in a learning environment. It is not recommended that this option used for normal OBD
     * communications, however, as the vehicle may have difficulty if it is expecting an
     * acknowledgement and never receives one. An R0 setting will always override any ‘number of
     * responses digit’ that is provided with an OBD request. The default setting is R1, or
     * responses on.
     */

    /**
     * R0 [ Responses off ] Disables the displaying of positive responses
     */
    ELM327_OBD_RESPONSES_OFF("R0"),

    /**
     * R1 [ Responses on ] Enables the displaying of positive responses
     */
    ELM327_OBD_RESPONSES_ON("R1"),

    /**
     * RA hh [ set the Receive Address to hh ] Depending on the application, users may wish to
     * manually set the address to which the ELM327 will respond. Issuing this command will turn off
     * the AR mode, and force the IC to only accept responses addressed to hh. Use caution with this
     * setting, as depending on what you set it to, you may end up accepting (ie. acknowledging with
     * an IFR) a message that was actually meant for another module. To turn off the RA filtering,
     * simply send AT AR. This command is not very effective for use with the CAN protocols, as it
     * only monitors for one portion of the ID bits, and that is not likely enough for most CAN
     * applications - the CRA command may be a better choice. Also, this command has no effect on
     * the addresses used by the J1939 protocols, as the J1939 routines derive them from the header
     * values, as required by the SAE standard. The RA command is exactly the same as the SR
     * command, and can be used interchangeably. Note that CAN Extended Addressing does not use this
     * value - it uses the one set by the AT TA command.
     */
    ELM327_OBD_SET_RECEIVE_ADDRESS_A("RA%d"),

    /**
     * RD [ Read the Data in the user memory ] The byte value stored with the SD command is
     * retrieved with this command. There is only one memory location, so no address is required.
     */
    ELM327_READ_USER_MEMORY("RD"),

    /**
     * RTR [ send an RTR message ] This command causes a special ‘Remote Frame’ CAN message to be
     * sent. This type of message has no data bytes, and has its Remote Transmission Request (RTR)
     * bit set. The headers and filters will remain as previously set (ie the ELM327 does not make
     * any assumptions as to what format a response may have), so adjustments may need to be made to
     * the mask and filter. This command must be used with an active CAN protocol (one that has been
     * sending and receiving messages), as it can not initiate a protocol search. Note that the CAF1
     * setting normally eliminates the display of all RTRs, so if you are monitoring messages and
     * want to see the RTRs, you will have to turn off formatting, or else turn the headers on. The
     * ELM327 treats an RTR just like any other message sent, and will wait for a response from the
     * vehicle (unless AT R0 has been chosen).
     */
    ELM327_CAN_SEND_RTR_MESSAGE("RTR"),

    /**
     * RV [ Read the input Voltage ] This initiates the reading of the voltage present at pin 2, and
     * the conversion of it to a decimal voltage. By default, it is assumed that the input is
     * connected to the voltage to be measured through a 47K Ω and 10K Ω resistor divider (with the
     * 10K Ω connected from pin 2 to Vss), and that the ELM327 supply is a nominal 5V. This will
     * allow for the measurement of input voltages up to about 28V, with an uncalibrated accuracy of
     * typically about 2%
     */
    ELM327_VOLTAGE_READING_READ_INPUT_VOLTAGE("ATRV"),

    /**
     * S0 and S1 [ printing of Spaces off or on ] These commands control whether or not space
     * characters are inserted in the ECU response. The ELM327 normally reports ECU responses as a
     * series of hex characters that are separated by space characters (to improve readability), but
     * messages can be transferred much more quickly if every third byte (the space) is removed.
     * While this makes the message less readable for humans, it can provide significant
     * improvements for computer processing of the data. By default, spaces are on (S1), and space
     * characters are inserted in every response.
     */

    /**
     * S0 [ printing of Spaces off ] Printing of spaces between bytes is disabled
     */
    ELM327_OBD_SPACES_OFF("ATS0"),

    /**
     * S1 [ printing of Spaces on ] Printing of spaces between bytes is enabled
     */
    ELM327_OBD_SPACES_ON("ATS1"),

    /**
     * SD hh [ Save Data byte hh ] The ELM327 is able to save one byte of information for you in a
     * special nonvolatile memory location, which is able to retain its contents even if the power
     * is turned off. Simply provide the byte to be stored, then retrieve it later with the read
     * data (AT RD) command. This location is ideal for storing user preferences, unit ids,
     * occurrence counts, or other information.
     */
    ELM327_WRITE_USER_MEMORY("SD%d"),

    /**
     * SH xyz [ Set the Header to 00 0x yz ] Entering CAN 11 bit ID words (headers) normally
     * requires that extra leading zeros be added (eg. AT SH 00 07 DF), but this command simplifies
     * doing so. The AT SH xyz command accepts a three digit argument, takes only the right-most 11
     * bits from that, adds leading zeros, and stores the result in the header storage locations for
     * you. As an example, AT SH 7DF is a valid command, and is quite useful for working with 11 bit
     * CAN systems. It actually results in the header bytes being stored internally as 00 07 DF.
     */
    ELM327_OBD_SET_HEADER_00_0X_YZ("SH%d%d%d"),

    /**
     * SH xx yy zz [ Set the Header to xx yy zz ] This command allows the user to manually control
     * the values that are sent as the three header bytes in a message. These bytes are normally
     * assigned values for you (and are not required to be adjusted), but there may be occasions
     * when it is desirable to change them (particularly if experimenting with physical addressing).
     * If experimenting, it is not necessary but may be better to set the headers after a protocol
     * is active. That way, wakeup messages, etc. that get set on protocol activation will use the
     * default values. The header bytes are defined with hexadecimal digits - xx will be used for
     * the first or priority/type byte, yy will be used for the second or receiver/target byte, and
     * zz will be used for the third or transmitter/source byte. These remain in effect until set
     * again, or until restored to their default values with the D, WS, or Z commands. If new values
     * for header bytes are set before the vehicle protocol has been determined, and if the search
     * is not set for fully automatic (ie other than protocol 0), these new values will be used for
     * the header bytes of the first request to the vehicle. If that first request should fail to
     * obtain a response, and if the automatic search is enabled, the ELM327 will then continue to
     * search for a protocol using default values for the header bytes. Once a valid protocol is
     * found, the header bytes will revert to the values assigned with the AT SH command. This
     * command is used to assign all header bytes, whether they are for a J1850, ISO 9141, ISO
     * 14230, or a CAN system. The CAN systems will use these three bytes to fill bits 0 to 23 of
     * the ID word (for a 29 bit ID), or will use only the rightmost 11 bits for an 11 bit CAN ID
     * (and any extra bits assigned will be ignored). The additional 5 bits needed for a 29 bit
     * system are set with the AT CP command. If assigning header values for the KWP protocols (4
     * and 5), care must be taken when setting the first header byte (xx) value. The ELM327 will
     * always insert the number of data bytes for you, but how it is done depends on the values that
     * you assign to this byte. If the second digit of this first header byte is anything other than
     * 0 (zero), the ELM327 assumes that you wish to have the length value inserted in that first
     * byte when sending. In other words, providing a length value in the first header byte tells
     * the ELM327 that you wish to use a traditional 3 byte header, where the length is stored in
     * the first byte of the header.  If you provide a value of 0 for the second digit of the first
     * header byte, the ELM327 will assume that you wish that value to remain as 0, and that you
     * want to have a fourth header (length) byte inserted into the message. This is contrary to the
     * ISO 14230-4 OBD standard, but it is in use by many KWP2000 systems for (non-OBD) data
     * transfer, so may be useful when experimenting.
     */
    ELM327_OBD_SET_HEADER_XX_YY_ZZ("SH%d%d%d"),

    /**
     * SH ww xx yy zz [ Set the Header to ww xx yy zz ] This four byte version of the AT SH command
     * allows setting a complete 29 bit CAN ID in one instruction. Alternatively, AT SP (for the
     * five most significant bits) and AT SH (for the other three bytes) may be used.
     */
    ELM327_OBD_SET_HEADER_WW_XX_YY_ZZ("SH%d%d%d%d"),

    /**
     * SI [ perform a Slow Initiation ] Protocols 3 and 4 use what is sometimes called a 5 baud, or
     * slow initiation sequence in order to begin communications. Usually, the sequence is performed
     * when the first message needs to be sent, and then the message is sent immediately after. Some
     * ECUs may need more time between the two however, and having a separate initiation command
     * allows you to control this time. Simply send AT SI, wait a little, then send the message. You
     * may need to experiment a little to get the right amount of delay. Protocol 3 or 4 must be
     * selected to use the AT SI command, or an error will result.
     */
    ELM327_ISO_PERFORM_SLOW_INITIATION("SI"),

    /**
     * SP h [ Set Protocol to h ] This command is used to set the ELM327 for operation using the
     * protocol specified by 'h', and to also save it as the new default. Note that the protocol
     * will be saved no matter what the AT M0/M1 setting is. The ELM327 supports 12 different
     * protocols (two can be user-defined). They are: 0 - Automatic 1 - SAE J1850 PWM (41.6 kbaud) 2
     * - SAE J1850 VPW (10.4 kbaud) 3 - ISO 9141-2 (5 baud init, 10.4 kbaud) 4 - ISO 14230-4 KWP (5
     * baud init, 10.4 kbaud) 5 - ISO 14230-4 KWP (fast init, 10.4 kbaud) 6 - ISO 15765-4 CAN (11
     * bit ID, 500 kbaud) 7 - ISO 15765-4 CAN (29 bit ID, 500 kbaud) 8 - ISO 15765-4 CAN (11 bit ID,
     * 250 kbaud) 9 - ISO 15765-4 CAN (29 bit ID, 250 kbaud) A - SAE J1939 CAN (29 bit ID, 250*
     * kbaud) B - USER1 CAN (11* bit ID, 125* kbaud) C - USER2 CAN (11* bit ID, 50* kbaud) default
     * settings (user adjustable) The first protocol shown (0) is a convenient way of telling the
     * ELM327 that the vehicle’s protocol is not known, and that it should perform a search. It
     * causes the ELM327 to try all protocols if necessary, looking for one that can be initiated
     * correctly. When a valid protocol is found, and the memory function is enabled, that protocol
     * will then be remembered, and will become the new default setting. When saved like this, the
     * automatic mode searching will still be enabled, and the next time the ELM327 fails to connect
     * to the saved protocol, it will again search all protocols for another valid one. Note that
     * some vehicles respond to more than one protocol - if searching, you may see more than one
     * type of response. ELM327 users often use the AT SP 0 command to reset the search protocol
     * before starting (or restarting) a connection. This works well, but since it is used so often,
     * and since writes to EEPROM result in an unnecessary delay (of about 30 msec), the AT SP0
     * command sets the protocol to 0, but does not perform a write to EEPROM. Similarly, the SP A0
     * and SP 0A commands do not perform writes to EEPROM, either. Saving this value to EEPROM would
     * not provide any advantage (and would be very short-lived, as the ELM327 will soon be finding
     * the vehicle’s protocol and over-writing the ‘0’ value in EEPROM). If you really want to store
     * the value ‘0’ in the internal EEPROM, you must use the AT SP 00 command. If another protocol
     * (other than 0) is selected with this command (eg. AT SP 3), that protocol will become the
     * default, and will be the only protocol used by the ELM327. Failure to initiate a connection
     * in this situation will result in a response such as ‘BUS INIT: ...ERROR’, and no other
     * protocols will be attempted. This is a useful setting if you know that your vehicle(s) only
     * use the one protocol, but is also one that can cause a lot of problems if you do not
     * understand it.
     */
    ELM327_OBD_SET_PROTOCOL("ATSP%01x"),

    /**
     * SP 00 [ erase the Stored Protocol ] To speed up protocol initiation and detection, the SP 0
     * command sets the protocol to automatic, but does not perform a (very time-consuming) write to
     * EEPROM. Some users felt it was necessary to be able to actually write to the ELM327's EEPROM,
     * however, so we provided this command. It should not normally be used when connecting to a
     * vehicle.
     */
    ELM327_OBD_ERASE_STORED_PROTOCOL("SP00"),

    /**
     * SP Ah [ Set Protocol to Auto, h ] This variation of the SP command allows you to choose a
     * starting (default) protocol, while still retaining the ability to automatically search for a
     * valid protocol on a failure to connect. For example, if your vehicle is ISO 9141-2, but you
     * want to occasionally use the ELM327 circuit on other vehicles, you might use the AT SP A3
     * command, so that the first protocol tried will then be yours (3), but it will also
     * automatically search for other protocols. Don't forget to disable the memory function if
     * doing this, or each new protocol detected will become your new default. SP Ah will save the
     * protocol information even if the memory option is off (but SP A0 and SP 0A do not- if you
     * must write 0 to the EEPROM, use command AT SP 00). Note that the ‘A’ can come before or after
     * the h, so AT SP A3 can also be entered as AT SP 3A.
     */
    ELM327_OBD_SET_DEFAULT_PROTOCOL_AUTO("ATSPA%01x"),

    /**
     * SR hh [Set the Receive address to hh ] Depending on the application, users may wish to
     * manually set the address to which the ELM327 will respond. Issuing this command will turn off
     * the AR mode, and force the IC to only accept responses addressed to hh. Use caution with this
     * setting, as depending on what you set it to, you may accept a message that was actually meant
     * for another module, possibly sending an IFR when you should not. To turn off the SR
     * filtering, simply send AT AR. This command has limited use with CAN, as it only monitors one
     * byte of the ID bits, and that is not likely selective enough for most CAN applications (the
     * CRA command may be a better choice). Also, the command has no effect on the addresses used by
     * the J1939 protocols, as the J1939 routines set their own receive addresses based on the ID
     * bit (header) values. This SR command is exactly the same as the RA command, and can be used
     * interchangeably with it. Note that CAN Extended Addressing does not use this value - it uses
     * the one set by the AT TA command.
     */
    ELM327_OBD_SET_RECEIVE_ADDRESS_B("SR%d"),

    /**
     * SS [ use the Standard Sequence for searches ] SAE standard J1978 specifies a protocol search
     * order that scan tools should use. It follows the number order that we have assigned to the
     * ELM327 protocols. In order to provide a faster search, the ELM327 does not normally follow
     * this order, but it will if you command it to with AT SS.
     */
    ELM327_OBD_USE_STANDARD_SEARCH_SEQUENCE("SS"),

    /**
     * ST hh [ Set Timeout to hh ] After sending a request, the ELM327 waits a preset time for a
     * response before it can declare that there was ‘NO DATA’ received from the vehicle. The same
     * timer setting may also be used after a response has been received, while waiting to see if
     * any more is coming (but this depends on the AT AT setting). The AT ST command allows this
     * timer to be adjusted, in increments of 4 msec (or 20 msec if in a CAN protocol, with CTM5
     * selected). When Adaptive Timing is enabled, the AT ST time sets the maximum time that is to
     * be allowed, even if the adaptive algorithm determines that the setting should be longer. In
     * most circumstances, it is best to simply leave the AT ST time at the default setting, and let
     * the adaptive timing algorithm determine what to use for the timeout. The ST timer is set to
     * 32 by default (giving a time of approximately 200 msec), but this default setting can be
     * adjusted by changing PP 03. Note that a value of 00 does not result in a time of 0 msec – it
     * will restore the timer to the default value. Also, during protocol searches, an internally
     * set minimum time is used - you may select longer times with AT ST, but not shorter ones.
     */
    ELM327_OBD_SET_TIMEOUT("ST%d"),

    /**
     * SW hh [ Set Wakeup to hh ] Once a data connection has been established, some protocols
     * require that there be data flow every few seconds, just so that the ECU knows to maintain the
     * communications path open. If the messages do not appear, the ECU will assume that you are
     * finished, and will close the channel. The connection will need to be initialized again to
     * reestablish communications. The ELM327 will automatically generate periodic messages, as
     * required, in order to maintain a connection. Any replies to these messages are ignored by the
     * ELM327, and are not visible to the user. (Currently, only protocols 3, 4, and 5 support these
     * messages - nothing is available for CAN. If you require CAN periodic messages, you must use
     * the ELM329.) The time interval between these periodic ‘wakeup’ messages can be adjusted in 20
     * msec increments using the AT SW hh command, where hh is any hexadecimal value from 00 to FF.
     * The maximum possible time delay of just over 5 seconds results when a value of FF (decimal
     * 255) is used. The default setting (92) provides a nominal delay of 3 seconds between
     * messages. Note that the value 00 (zero) is special, as it will stop the periodic (wakeup)
     * messages. This provides a control for experimenters to stop the messages while keeping the
     * rest of the protocol functioning normally, and is not intended to be used regularly. Issuing
     * AT SW 00 will not change a prior setting for the time between wakeup messages, if the
     * protocol is re- initialized. Once periodic messages have been turned off with AT SW, they can
     * only be reestablished by closing and reinitializing the protocol.
     */
    ELM327_ISO_SET_WAKEUP("SW%d"),

    /**
     * SW00 [ Stop sending Wakeups ] Stops sending 'wakeup' messages
     */
    ELM327_ISO_STOP_SENDING_WAKEUPS("SW00"),

    /**
     * TA hh [ set the Tester Address to hh ] This command is used to change the current tester (ie.
     * scan tool) address that is used in the headers, periodic messages, filters, etc. The ELM327
     * normally uses the value that is stored in PP 06 for this, but the TA command allows you to
     * temporarily override that value. Sending AT TA will affect all protocols, including J1939.
     * This provides a convenient means to change the J1939 address from the default value of F9,
     * without affecting other settings. Although this command may appear to work ‘on the fly’, it
     * is not recommended that you try to change this address after a protocol is active, as the
     * results may be unpredictable.
     */
    ELM327_OBD_SET_TESTER_ADDRESS("TA%d"),

    /**
     * TP h [ Try Protocol h ] This command is identical to the SP command, except that the protocol
     * that you select is not immediately saved in internal EEPROM memory, so does not change the
     * default setting. Note that if the memory function is enabled (AT M1), and this new protocol
     * that you are trying is found to be valid, that protocol will then be stored in memory as the
     * new default.
     */
    ELM327_OBD_TRY_PROTOCOL("ATTP%01x"),

    /**
     * TP Ah [ Try Protocol h with Auto ] This command is very similar to the AT TP command above,
     * except that if the protocol that is tried should fail to initialize, the ELM327 will then
     * automatically sequence through the other protocols, attempting to connect to one of them.
     */
    ELM327_OBD_TRY_PROTOCOL_FALLBACK_AUTO("ATTPA%01x"),

    /**
     * V0 and V1 [ Variable data lengths off or on ] Many CAN protocols (ie ISO 15765-4) expect to
     * send eight data bytes at all times. The V0 and V1 commands may be used to override this
     * behaviour (for any CAN protocol) should you wish. Choosing V1 will cause the current CAN
     * protocol to send variable data length messages, just as bit 6 of PP 2C and PP 2E do for
     * protocols B and C. It does not matter what the protocol should be doing - V1 will override
     * that. This allows experimenting with variable data length messages on demand. If you select
     * V0 (the default setting), the forced sending of variable length CAN messages is turned off.
     * The format of the sent messages reverts to the protocol’s settings.
     */

    /**
     * V0 [ Variable data lengths off ] Disables the forced sending of variable length CAN messages
     */
    ELM327_CAN_VARIABLE_DATA_LENGTH_OFF("V0"),

    /**
     * V1 [ Variable data lengths on ] Enables the forced sending of variable length CAN messages
     */
    ELM327_CAN_VARIABLE_DATA_LENGTH_ON("V1"),

    /**
     * WM [1 to 6 bytes] [ set Wakeup Message to... ] This command allows the user to override the
     * default settings for the wakeup messages (sometimes known as the ‘periodic idle’ messages).
     * Simply provide the message that you wish to have sent (typically three header bytes and one
     * to three data bytes), and the ELM327 will add the checksum and send them as required, at the
     * rate determined by the AT SW setting. Default settings will send the bytes 68 6A F1 01 00 for
     * ISO 9141, and C1 33 F1 3E for KWP.
     */
    ELM327_ISO_SET_WAKEUP_MESSAGE("WM%l"),

    /**
     * WS [ Warm Start ] This command causes the ELM327 to perform a complete reset. It is very
     * similar to the AT Z command, but does not include the power on LED test. Users may find this
     * a convenient way to quickly ‘start over’ without having the extra delay of the AT Z command.
     * If using variable RS232 baud rates (ie AT BRD commands), it is preferred that you reset the
     * IC using this command rather than AT Z, as AT WS will not affect the chosen RS232 baud rate.
     */
    ELM327_WARM_START("WS"),

    /**
     * Z [ reset all ] This command causes the chip to perform a complete reset as if power were
     * cycled off and then on again. All settings are returned to their default values, and the chip
     * will be put into the idle state, waiting for characters on the RS232 bus. Note that any baud
     * rate that was set with the AT BRD command will be lost, and the ELM327 will return to the
     * default baud rate setting.
     */
    ELM327_RESET_ALL("ATZ"),

    /**
     * @1 [ display the device description ] This command displays the device description string.
     * The default text is ‘OBDII to RS232 Interpreter’.
     */
    ELM327_DISPLAY_DEVICE_DESCRIPTION("AT@1"),

    /**
     * @2 [ display the device identifier ] A device identifier string that was recorded with the @3
     * command is displayed with the @2 command. All 12 characters and a terminating carriage return
     * will be sent in response, if they have been defined. If no identifier has been set, the @2
     * command returns an error response (‘?’). The identifier may be useful for storing product
     * codes, production dates, serial numbers, or other such codes. See the ‘Programming Serial
     * Numbers’ section for more information.
     */
    ELM327_DISPLAY_DEVICE_IDENTIFIER("AT@2"),

    /**
     * @3 cccccccccccc [ store the device identifier ] This command is used to set the device
     * identifier code. Exactly 12 characters must be sent, and once written to memory, they can not
     * be changed (ie you may only use the @3 command one time). The characters sent must be
     * printable (ascii character values 0x21 to 0x5F inclusive). If you are developing software to
     * write device identifiers, you may be interested in the ELM328 IC, as it allows multiple
     * writes using the @3 command (but it can not send OBD messages).
     */
    ELM327_STORE_DEVICE_IDENTIFIER("AT@3");

    public static final String ELM327_NO_DATA = "NO DATA";

    /// The actual string op-code
    private final String request;

    /**
     * Constructs an OpCode with the provided string
     *
     * @param request
     *         the string op-code
     */
    private OpCode(String request)
    {
        this.request = request;
    }

    /**
     * Retrieves the string op-code
     *
     * @return the string op-code
     */
    public String getOption()
    {
        return request;
    }

    /**
     * Retrieves a serialized representation of the string op-code
     *
     * @return a serialized representation of the string op-code
     */
    public byte[] serialize()
    {
        return request.getBytes();
    }
}
