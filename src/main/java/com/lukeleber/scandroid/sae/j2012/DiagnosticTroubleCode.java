// This file is protected under the KILLGPL.
// For more information, visit http://www.lukeleber.github.io/KILLGPL.html
//
// Copyright (c) Luke Leber <LukeLeber@gmail.com>

package com.lukeleber.scandroid.sae.j2012;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.io.Serializable;

/**
 * <p>An implementation of SAE J2012 (rev 2002): Diagnostic Trouble Code Definitions</p>
 *
 * <p>The following implementation was modeled after SAE J2012.  This documentation shall give
 * an in depth look at the requirements of a Diagnostic Trouble Code (hereafter, DTC).</p>
 *
 * <p>According to SAE J2012 §4 through §5.7.4:
 * <ul>
 *   <li>A DTC consists of a three digit numeric code preceded by an alphanumeric designator.</li>
 *   <li>The alphanumeric designators are as follows:
 *     <ol>
 *         <li>P0</li>
 *         <li>P1</li>
 *         <li>P2</li>
 *         <li>P3</li>
 *         <li>C0</li>
 *         <li>C1</li>
 *         <li>C2</li>
 *         <li>C3</li>
 *         <li>B0</li>
 *         <li>B1</li>
 *         <li>B2</li>
 *         <li>B3</li>
 *         <li>U0</li>
 *         <li>U1</li>
 *         <li>U2</li>
 *         <li>U3</li>
 *     </ol>
 *   </li>
 *   <li>Codes are structured in the following format:
 *   <pre>
 *
 *       (msb)   byte 1   (lsb)     (msb)   byte 2  (lsb)
 *       b7 b6 b5 b4 b3 b2 b1 b0 | b7 b6 b5 b4 b3 b2 b1 b0
 *       |  |  |  |  |  |  |  |    |  |  |  |  |  |  |  |_____________________________
 *       |  |  |  |  |  |  |  |    |  |  |  |  |  |  |________| 4th character of code |
 *       |  |  |  |  |  |  |  |    |  |  |  |  |  |___________| (hexadecimal (0...F)) |
 *       |  |  |  |  |  |  |  |    |  |  |  |  |______________|_display_character_5___|
 *       |  |  |  |  |  |  |  |    |  |  |  |
 *       |  |  |  |  |  |  |  |    |  |  |  |__________________________________________
 *       |  |  |  |  |  |  |  |    |  |  |____________________| 3rd character of code |
 *       |  |  |  |  |  |  |  |    |  |_______________________| (hexadecimal (0...F)) |
 *       |  |  |  |  |  |  |  |    |__________________________|_display_character_4___|
 *       |  |  |  |  |  |  |  |
 *       |  |  |  |  |  |  |  |________________________________________________________
 *       |  |  |  |  |  |  |__________________________________| Area of vehicle system|
 *       |  |  |  |  |  |_____________________________________| (hexadecimal (0...F)) |
 *       |  |  |  |  |________________________________________|_display_character_3___|
 *       |  |  |  |
 *       |  |  |  |_______________________________________________________________________________________________________________________
 *       |  |  |______________________________________________| 00 = ISO/SAE controlled                                                   |
 *       |  |                                                 | 01 = manufacturer controlled                                              |
 *       |  |                                                 | 10 = if(powertrain) ISO/SAE controlled, else manufacturer controlled      |
 *       |  |                                                 | 11 = ISO/SAE controlled except powertrain where                           |
 *       |  |                                                 |                              |_ P3000 - P3399 are manufacturer controlled |
 *       |  |                                                 |                              |  P3400 - P3999 are ISO/SAE controlled      |
 *       |  |                                                 |_display_character_2_______________________________________________________|
 *       |  |
 *       |  |_________________________________________________________________________
 *       |____________________________________________________| 00 = powertrain (P)   |
 *                                                            | 01 = chassis (C)      |
 *                                                            | 10 = body (B)         |
 *                                                            | 11 = network (U)      |
 *                                                            |_display_character_1___|
 *   </pre>
 *   </li>
 * </ul></p>
 * <p> The section above has given the structure of a DTC, but no context or reasoning behind
 * what purposes DTCs are used for.  The following section attempts to give an informal
 * explanation that covers why DTCs are important, and what they are used for.</p>
 * <p>DTCs provide a uniform, concise way to express a fault in an on-board system.  They aid
 * service technicians in diagnosis and repair of said systems by providing a starting point for
 * coming up with a diagnostic procedure.  Typically, DTCs are displayed to users as plain text,
 * for example: "P0777: Pressure Control Solenoid 'B' Stuck On".  DTCs may be defined by the
 * ISO/SAE or by the vehicle manufacturer.  ISO/SAE defined DTCs are referred to as "Core DTCs"
 * as they will not vary between different vehicle makes/models.  Manufacturer defined DTCs on
 * the other hand may vary between manufacturers, and somewhat within manufacturers.  Most OEMs
 * do not offer this information to the public freely.</p>
 *
 * <p><strong>Implementation Details</strong><br\>
 *     The <code>DiagnosticTroubleCode</code> class provides functionality that is not explicitly
 *     required by the standard, but is useful for developers.  Among the key features are:
 *     <ul>
 *         <li>Perfect hashing (through the {@link #hashCode()} method) - this means that DTCs
 *         may be stored in a database with a unique id.</li>
 *         <li>DTCs are {@link java.lang.Comparable} based on this unique id.</li>
 *         <li>DTCs are {@link android.os.Parcelable}.</li>
 *         <li>DTCs are {@link java.io.Serializable}.</li>
 *         <li>DTCs are immutable and thus are completely thread safe.</li>
 *     </ul>
 *     At the time of this writing, most DTCs are expected to be stored externally (most likely
 *     in a SQLite database).  It is expected that multiple databases will eventually be used
 *     as more manufacturer-specific information is acquired or as DTC definitions are translated
 *     to other languages.</p>
 *
 * @author Luke A. Leber <LukeLeber@gmail.com>
 *
 * @since 1.0
 *
 * @see java.lang.Comparable
 *
 * @see android.os.Parcelable
 *
 * @see java.io.Serializable
 *
 */
public final class DiagnosticTroubleCode
        implements
        Comparable<DiagnosticTroubleCode>,
        Parcelable,
        Serializable
{

    /// A serialization proxy for working with non-transient final fields
    /// This class is used with the readResolve/writeReplace mechanism
    private final static class Proxy
    {
        private final int code;
        private final String description;

        Proxy(DiagnosticTroubleCode dtc)
        {
            this.code = dtc.bits;
            this.description = dtc.naming;
        }

        @SuppressWarnings("unused")
        private Object readResolve()
        {
            return new DiagnosticTroubleCode(code, description);
        }
    }

    /// The minimum value that a <code>DiagnosticTroubleCode</code> encoding will flatten into
    public final static int MIN_VALUE = 0;

    /// The maximum value that a <code>DiagnosticTroubleCode</code> encoding will flatten into
    public final static int MAX_VALUE = 0xFFFF;

    /// The mask for accessing the system information
    public final static int SYSTEM_MASK = 0xC000;

    /// The mask for accessing the jurisdiction information
    public final static int JURISDICTION_MASK = 0x3000;

    /// The value of a powertrain DTC
    public final static int POWERTRAIN_DTC = 0x0000;

    /// The value of a chassis DTC
    public final static int CHASSIS_DTC = 0x4000;

    /// The value of a body DTC
    public final static int BODY_DTC = 0x8000;

    /// The value of a network DTC
    public final static int NETWORK_DTC = 0xC000;

    /// The value of a reserved DTC
    public final static int RESERVED_DTC = 0x3000;

    /// Required by the parcelable interface
    public final static Parcelable.Creator<DiagnosticTroubleCode> CREATOR =
        new Parcelable.Creator<DiagnosticTroubleCode>()
        {
            @Override
            public final DiagnosticTroubleCode createFromParcel(Parcel in)
            {
                return new DiagnosticTroubleCode(in.readInt(), in.readString());
            }

            @Override
            public final DiagnosticTroubleCode[] newArray(int size)
            {
                return new DiagnosticTroubleCode[size];
            }
        };

    /// Lookup table for the most significant nibble of a DTC
    private final static String[] MOST_SIG_NIBBLE_LOOKUP_TABLE =
        new String[]
        {
            "P0", "P1", "P2", "P3",
            "C0", "C1", "C2", "C3",
            "B0", "B1", "B2", "B3",
            "U0", "U1", "U2", "U3"
        };


    /// The 16-bits that make up this DTC - note that the upper 16 bits are ignored
    /// and can be assumed to always be zero
    private final int bits;

    /// The string-encoded representation of this DTC (IE. "P0123")
    /// Transient because the string can be trivially remade via the bits field
    private final transient String code;

    /// A user-friendly naming of this DTC
    /// The SAE maintains the list of official naming that may be found in SAE J2012 Appendix A.
    /// Manufacturers are free to manage their own naming for certain ranges.
    /// Typically this string will be pulled from a database, and as such there is no possible
    /// way to check this string for validity at the application level.
    private final String naming;

    /**
     * {@inheritDoc}
     *
     * <p>Note: This method does not check the naming of the operands, but rather only the
     * integral value returned by {@link #getBits()} to determine equality.  Thus two
     * <code>DiagnosticTroubleCodes</code> may have different namings and still be considered
     * equal.  This decision was made with the idea that the namings are standard (that is,
     * controlled by the ISO/SAE/Manufacturer) and as such the namings should be relatively
     * consistent among implementations -- consistent enough to ignore any differentiations.
     * This method is effectively equivalent to the following:</p>
     * <p><pre>
     *     boolean equals = this.getBits() == other.getBits();
     * </pre></p>
     */
    @Override
    public final boolean equals(Object another)
    {
        return another instanceof DiagnosticTroubleCode && bits == ((DiagnosticTroubleCode) another).bits;
    }

    /**
     * {@inheritDoc}
     *
     * <p>Note: This method returns {@link #getBits()} and is guaranteed to be unique in all
     * circumstances.</p>
     *
     */
    @Override
    public final int hashCode()
    {
        return bits;
    }

    /**
     * {@inheritDoc}
     *
     * <p>Note: This method compares the values returned by the {@link #getBits()} method such
     * that (effectively):
     *
     * <table>
     *     <tr>
     *         <td>P0000</td><td>P3FFF</td><td>C0000</td><td>C3FFF</td><td>B0000</td>
     *         <td>B3FFF</td><td>U0000</td><td>U3FFF</td>
     *     </tr>
     *     <tr>
     *         <td>0</td><td>16383</td><td>16384</td><td>32767</td><td>32768</td>
     *         <td>49151</td><td>49152</td><td>65535</td>
     *     </tr>
     * </table>
     */
    @Override
    public final int compareTo(@NonNull DiagnosticTroubleCode another)
    {
        return bits - another.bits;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int describeContents()
    {
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void writeToParcel(Parcel out, int flags)
    {
        out.writeInt(bits); out.writeString(naming);
    }

    /**
     * {@inheritDoc}
     *
     * <p>Note: This method returns the 5 character long encoding of this
     * <code>DiagnosticTroubleCode</code> as specified in the class documentation.  Essentially
     * this method behaves exactly like {@link #getCode()}.</p>
     */
    @Override
    public final String toString()
    {
        return code;
    }

    /// Internally invoked by the JVM serialization mechanism
    @SuppressWarnings("unused")
    private Object writeReplace()
    {
        return new Proxy(this);
    }

    /**
     * Retrieves the raw 16 integral bits value of this <code>DiagnosticTroubleCode</code>.  Note
     * that although 32 bits are returned, that the most significant 16 bits shall be all set
     * to zero (0).
     *
     * @return the raw 16 integral bits value of this <code>DiagnosticTroubleCode</code>
     *
     */
    public final int getBits()
    {
        return bits;
    }

    /**
     * Retrieves the 5 character long encoding of this <code>DiagnosticTroubleCode</code> as
     * specified in the class documentation
     *
     * @return the 5 character long encoding of this <code>DiagnosticTroubleCode</code> as
     * specified in the class documentation
     *
     * @see android.support.annotation.NonNull
     */
    public final @NonNull String getCode()
    {
        return code;
    }

    /**
     * Retrieves the user friendly naming of this <code>DiagnosticTroubleCode</code>
     *
     * @return the user friendly naming of this <code>DiagnosticTroubleCode</code>
     *
     * @see android.support.annotation.NonNull
     */
    public final @NonNull String getNaming()
    {
        return naming;
    }

    /**
     * Is this DTC a powertrain DTC?
     *
     * @return true if this is a powertrain DTC, otherwise false
     */
    public final boolean isPowertrainDTC()
    {
        return (bits & SYSTEM_MASK) == POWERTRAIN_DTC;
    }

    /**
     * Is this DTC a body DTC?
     *
     * @return true if this is a body DTC, otherwise false
     */
    public final boolean isBodyDTC()
    {
        return (bits & SYSTEM_MASK) == BODY_DTC;
    }

    /**
     * Is this DTC a chassis DTC?
     *
     * @return true if this is a chassis DTC, otherwise false
     */
    public final boolean isChassisDTC()
    {
        return (bits & SYSTEM_MASK) == CHASSIS_DTC;
    }

    /**
     * Is this DTC a network DTC?
     *
     * @return true if this is a network DTC, otherwise false
     */
    public final boolean isNetworkDTC()
    {
        return (bits & SYSTEM_MASK) == NETWORK_DTC;
    }

    /**
     * Is this DTC a reserved DTC?
     *
     * @return true if this is a reserved DTC, otherwise false
     */
    public final boolean isReservedDTC()
    {
        return !isPowertrainDTC() && (bits & JURISDICTION_MASK) == RESERVED_DTC;
    }

    /**
     * Is this DTC a core DTC?
     *
     * @return true if this is a core DTC, otherwise false
     */
    public final boolean isCoreDTC()
    {
        int val = bits & JURISDICTION_MASK;
        return val == 0x0000 || (val == 0x2000 && isPowertrainDTC());
    }

    /**
     * Is this DTC a non-uniform DTC?
     *
     * @return true if this is a non-uniform DTC, otherwise false
     */
    public final boolean isNonUniformDTC()
    {
        return !isCoreDTC() && !isReservedDTC();
    }

    /**
     * Default constructor
     * <p>Produces a <bits>DiagnosticTroubleCode</bits> with:
     * <ul>
     *     <li>a bit pattern of all zeros</li>
     *     <li>an encoding of "P0000"</li>
     *     <li>a naming consisting of an empty string</li>
     * </ul>
     *
     * @since 1.0
     */
    public DiagnosticTroubleCode()
    {
        this.bits = 0;
        this.code = "P0000";
        this.naming = "";
    }

    /**
     * Constructs a <code>DiagnosticTroubleCode</code> with the provided bit pattern and
     * naming.
     *
     * @param bits an integer containing the 16 least significant bits that forms the encoding.
     *             Note that the most significant 16 bits <strong>must</strong> be zero.
     *
     * @param naming the string that describes what this <code>DiagnosticTroubleCode</code>
     *               means.  This string should be either ISO/SAE/Manufacturer defined and
     *               <strong>should not be arbitrary</strong>.  There is no reasonable way
     *               to enforce this precondition, so please do not abuse this.
     *
     * @throws java.lang.IllegalArgumentException if:
     * <ul>
     *     <li><i>bits</i> < {@link #MIN_VALUE} || <i>bits</i> > {@link #MAX_VALUE}</li>
     *     <li><i>naming</i> == null</li>
     * </ul>
     *
     * @since 1.0
     *
     */
    public DiagnosticTroubleCode(int bits, String naming)
    {
        if(bits < MIN_VALUE || bits > MAX_VALUE)
        {
            throw new IllegalArgumentException("bad precondition: 0 <= bits <= 65535");
        }
        if(naming == null)
        {
            throw new IllegalArgumentException("bad precondition: naming != null");
        }
        this.bits = bits;
        this.code = MOST_SIG_NIBBLE_LOOKUP_TABLE[(bits >> 12) & 0xF] +
                Integer.toHexString((bits >> 8) & 0xF) +
                Integer.toHexString((bits >> 4) & 0xF) +
                Integer.toHexString(bits & 0xF);
        this.naming = naming;
    }

    /**
     * Constructs a <code>DiagnosticTroubleCode</code> with the provided string <i>encoding</i>
     * and <i>naming</i>.  Note - this constructor is slower than
     * {@link #DiagnosticTroubleCode(int, String)} due to having to perform a reverse lookup
     * and should be used only when necessary.
     *
     * @param encoding the string that contains the five characters that form the textual
     *                 representation of the number of this <code>DiagnosticTroubleCode</code>.
     *                 For more information about the rules regarding the encoding, refer to the
     *                 {@link DiagnosticTroubleCode class documentation}.
     *
     * @param naming the string that describes what this <code>DiagnosticTroubleCode</code>
     *               means.  This string should be either ISO/SAE/Manufacturer defined and
     *               <strong>should not be arbitrary</strong>.  There is no reasonable way
     *               to enforce this precondition, so please do not abuse this.
     *
     * @throws java.lang.IllegalArgumentException if:
     * <ul>
     *     <li><i>encoding.length()</i> != 5</li>
     *     <li><i>encoding</i>[0] != {'p', 'P', 'c', 'C', 'b', 'B', 'u', 'U'}</li>
     *     <li><i>encoding</i>[1] != {'0', '1', '2', '3'}</li>
     *     <li><i>naming</i> == null</li>
     * </ul>
     *
     * @since 1.0
     *
     */
    public DiagnosticTroubleCode(String encoding, String naming)
    {
        if(encoding.length() != 5)
        {
            throw new IllegalArgumentException("bad precondition: encoding.length() != 5");
        }
        if(naming == null)
        {
            throw new IllegalArgumentException("bad precondition: naming != null");
        }
        int code = -1;
        String value = encoding.substring(0, 2);
        for(int i = 0; i < 0x10; ++i)
        {
            if(MOST_SIG_NIBBLE_LOOKUP_TABLE[i].equals(value))
            {
                code = i;
                break;
            }
        }
        if(code == -1)
        {
            throw new IllegalArgumentException("bad precondition: MOST_SIG_NIBBLE_LOOKUP_TABLE " +
                    "contains encoding[0, 1]");
        }
        code <<= 12;
        code |= (Integer.parseInt(encoding.substring(2, 3), 16) << 8);
        code |= (Integer.parseInt(encoding.substring(3, 4), 16) << 4);
        code |= (Integer.parseInt(encoding.substring(4, 5), 16));
        this.bits = code;
        this.code = encoding;
        this.naming = naming;
    }
}
