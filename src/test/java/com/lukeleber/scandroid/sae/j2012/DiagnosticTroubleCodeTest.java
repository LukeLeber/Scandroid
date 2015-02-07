package com.lukeleber.scandroid.sae.j2012;


import android.os.Parcel;

import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

/**
 * Test cases for com.lukeleber.scandroid.sae.j2012.DiagnosticTroubleCode
 */
@Config(manifest = "./src/main/AndroidManifest.xml", emulateSdk = 18)
@RunWith(RobolectricTestRunner.class)
public class DiagnosticTroubleCodeTest
{
    /**
     * The default constructor should create a DTC with:
     *
     * A) An integer value of "0"
     *
     * B) A string encoding of "P0000"
     *
     * C) A string description of an empty string
     *
     */
    @Test
    public void defaultConstructorTest()
    {
        /// Fail
        DiagnosticTroubleCode test = null;
        
        try
        {
            test = new DiagnosticTroubleCode();
        }
        catch(Throwable t)
        {
            assertFalse("default constructor threw", true);
        }
        /// Test A
        assertEquals(0, test.getBits());

        /// Test B
        assertEquals("P0000", test.getCode());

        /// Test C
        assertEquals("", test.getNaming());
    }

    /**
     * The {@link com.lukeleber.scandroid.sae.j2012.DiagnosticTroubleCode#DiagnosticTroubleCode(int, String)}
     * constructor takes an integral value and string description and produces a DTC if and only
     * if:
     *
     * A) The integral value is between 0 and 65535 inclusive
     *
     * B) The description is non null
     *
     * If the above preconditions are not met, this constructor shall throw an instance of
     * {@link java.lang.RuntimeException}.
     *
     */
    @Test
    public void inflaterConstructorATest()
    {
        /// Test all valid DTCs for successful construction
        try
        {
            for (int i = 0; i < 0xFFFF; ++i)
            {
                DiagnosticTroubleCode test = new DiagnosticTroubleCode(i, "test");
                assertEquals(i, test.getBits());
                assertEquals("test", test.getNaming());
            }
        }
        catch(Throwable t)
        {
            assertFalse("unexpected throwable: " + t.getClass().getName(), true);
        }

        /// Test a negative number for failure
        try
        {
            new DiagnosticTroubleCode(-1, "test");
            assertFalse("constructor did not throw when 'code < 0'", true);
        }
        catch(IllegalArgumentException e)
        {
            /// no-op
        }
        catch(Throwable t)
        {
            assertFalse("unexpected throwable: " + t.getClass().getName(), true);
        }

        /// Test a number > 0xFFFF for failure
        try
        {
            new DiagnosticTroubleCode(0xFFFFFF, "test");
            assertFalse("constructor did not throw when 'code > 65535'", true);
        }
        catch(IllegalArgumentException e)
        {
            /// no-op
        }
        catch(Throwable t)
        {
            assertFalse("unexpected throwable: " + t.getClass().getName(), true);
        }

        /// Test a null description for failure
        try
        {
            new DiagnosticTroubleCode(0, null);
            assertFalse("constructor did not throw when 'description == null'", true);
        }
        catch(IllegalArgumentException e)
        {
            /// no-op
        }
        catch(Throwable t)
        {
            assertFalse("unexpected throwable: " + t.getClass().getName(), true);
        }
    }

    /**
     * The {@link com.lukeleber.scandroid.sae.j2012.DiagnosticTroubleCode#DiagnosticTroubleCode(String, String)}
     * constructor takes a string encoding and description and produces a DTC if and only if:
     *
     * A) The provided encoding follows the rules defined by SAE J2012 (refer to 
     * {@link com.lukeleber.scandroid.sae.j2012.DiagnosticTroubleCode} for more information
     * 
     * B) The provided description is not null
     *
     * If the above preconditions are not met, this constructor shall throw an instance of
     * {@link java.lang.RuntimeException}.
     *
     */
    @Test
    public void inflaterConstructorBTest()
    {
        String[] msb =
            new String[]
            {
                "P0", "P1", "P2", "P3",
                "C0", "C1", "C2", "C3",
                "B0", "B1", "B2", "B3",
                "U0", "U1", "U2", "U3"
            };
        String[] hex =
                new String[]
                        {
                                "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F"
                        };

        /// test all valid DTCs for successful construction
        String currentDTC = null;
        try
        {
            for (String s : msb)
            {
                for(int i = 0; i < 0xF; ++i)
                {
                    for(int j = 0; j < 0xF; ++j)
                    {
                        for(int k = 0; k < 0xF; ++k)
                        {
                            currentDTC = s+hex[i]+hex[j]+hex[k];
                            DiagnosticTroubleCode test = new DiagnosticTroubleCode(currentDTC,
                                    "test");
                            assertEquals(currentDTC, test.getCode());
                            assertEquals("test", test.getNaming());
                        }
                    }
                }
            }
        }
        catch(Throwable t)
        {
            assertFalse("Encoding '"+currentDTC+"' has failed: " + t.getMessage(), true);
        }

        /// test an encoding length < 5
        try
        {
            String bad = "P012";
            new DiagnosticTroubleCode(bad, "test");
            assertFalse("encoding '"+bad+"' should have failed, but did not", true);
        }
        catch(IllegalArgumentException e)
        {
            /// no-op
        }
        catch(Throwable t)
        {
            assertFalse("unexpected throwable: " + t.getClass().getName(), true);
        }

        /// test an encoding length > 5
        try
        {
            String bad = "P01234";
            new DiagnosticTroubleCode(bad, "test");
            assertFalse("encoding '"+bad+"' should have failed, but did not", true);
        }
        catch(IllegalArgumentException e)
        {
            /// no-op
        }
        catch(Throwable t)
        {
            assertFalse("unexpected throwable: " + t.getClass().getName(), true);
        }

        /// test an invalid category code for failure
        try
        {
            String bad = "A0000";
            new DiagnosticTroubleCode(bad, "test");
            assertFalse("encoding '"+bad+"' should have failed, but did not", true);
        }
        catch(IllegalArgumentException e)
        {
            /// no-op
        }
        catch(Throwable t)
        {
            assertFalse("unexpected throwable: " + t.getClass().getName(), true);
        }

        /// test an invalid sub-category code for failure
        try
        {
            String bad = "P8000";
            new DiagnosticTroubleCode(bad, "test");
            assertFalse("encoding '"+bad+"' should have failed, but did not", true);
        }
        catch(IllegalArgumentException e)
        {
            /// no-op
        }
        catch(Throwable t)
        {
            assertFalse("unexpected throwable: " + t.getClass().getName(), true);
        }
    }

    /**
     * Test for {@link com.lukeleber.scandroid.sae.j2012.DiagnosticTroubleCode#equals(Object)}.
     * The equals method returns true if and only if two DTCs have the same <i>bits</i> field.
     * The <i>encoding</i> and <i>naming</i> fields are completely ignored.
     *
     */
    @Test
    public void equalsTest()
    {
        /// 0 : P0000 : ""
        DiagnosticTroubleCode a = new DiagnosticTroubleCode();

        /// 1 : P0001 : "test"
        DiagnosticTroubleCode b = new DiagnosticTroubleCode("P0001", "test");

        /// 65535 : U3FFF : "test"
        DiagnosticTroubleCode c = new DiagnosticTroubleCode("U3FFF", "test");

        /// 0 : P0000 : "test"
        DiagnosticTroubleCode d = new DiagnosticTroubleCode(0, "test");

        assertEquals(a, a);
        assertEquals(b, b);
        assertEquals(c, c);
        assertEquals(d, d);

        assertNotEquals(a, b);
        assertNotEquals(b, a);

        assertNotEquals(b, c);
        assertNotEquals(c, b);

        assertNotEquals(c, d);
        assertNotEquals(d, c);

        assertEquals(a, d);
        assertEquals(d, a);
    }

    @Test
    public void hashCodeTest()
    {
        for(int i = 0; i < 0xFFFF; ++i)
        {
            assertEquals(i, new DiagnosticTroubleCode(i, "").hashCode());
        }
    }

    @Test
    public void compareToTest()
    {
        DiagnosticTroubleCode current = new DiagnosticTroubleCode();
        for(int i = 0; i < 0xFFFE; ++i)
        {
            DiagnosticTroubleCode next = new DiagnosticTroubleCode(i + 1, "");
            assertEquals(1, next.compareTo(current));
            current = next;
        }
    }

    @Test
    public void parcelableTest()
    {
        DiagnosticTroubleCode test0 = new DiagnosticTroubleCode(),
                              test1 = new DiagnosticTroubleCode(1, "");
        Parcel p = null;
        try
        {
            p = Parcel.obtain();
            p.writeParcelable(test0, test0.describeContents());
            p.writeParcelable(test1, test1.describeContents());
            p.setDataPosition(0);
            DiagnosticTroubleCode test2 = p.readParcelable(null),
                                  test3 = p.readParcelable(null);

            assertEquals(test0, test2);
            assertEquals(test1, test3);
            assertNotEquals(test0, test3);
            assertNotEquals(test1, test2);
        }
        finally
        {
            if(p != null)
            {
                p.recycle();
            }
        }
    }

    @Test
    public void toStringTest()
    {
        for(int i = 0; i < 0xFFFF; ++i)
        {
            DiagnosticTroubleCode test = new DiagnosticTroubleCode(i, "");
            assertEquals(test.getCode(), test.toString());
        }
    }

    @Test
    public void isPowertrainDTCTest()
    {
        for(int i = 0; i < 0x3FFF; ++i)
        {
            assertTrue(new DiagnosticTroubleCode(i, "").isPowertrainDTC());
        }

        for(int i = 0x4000; i < 0xFFFF; ++i)
        {
            assertFalse(new DiagnosticTroubleCode(i, "").isPowertrainDTC());
        }
    }

    @Test
    public void isChassisDTCTest()
    {
        for(int i = 0; i < 0x3FFF; ++i)
        {
            assertFalse(new DiagnosticTroubleCode(i, "").isChassisDTC());
        }
        for(int i = 0x4000; i < 0x7FFF; ++i)
        {
            assertTrue(new DiagnosticTroubleCode(i, "").isChassisDTC());
        }
        for(int i = 0x8000; i < 0xFFFF; ++i)
        {
            assertFalse(new DiagnosticTroubleCode(i, "").isChassisDTC());
        }
    }

    @Test
    public void isBodyDTCTest()
    {
        for(int i = 0; i < 0x7FFF; ++i)
        {
            assertFalse(new DiagnosticTroubleCode(i, "").isBodyDTC());
        }
        for(int i = 0x8000; i < 0xBFFF; ++i)
        {
            assertTrue(new DiagnosticTroubleCode(i, "").isBodyDTC());
        }
        for(int i = 0xC000; i < 0xFFFF; ++i)
        {
            assertFalse(new DiagnosticTroubleCode(i, "").isBodyDTC());
        }
    }

    @Test
    public void isNetworkDTCTest()
    {
        for(int i = 0; i < 0xBFFF; ++i)
        {
            assertFalse(new DiagnosticTroubleCode(i, "").isNetworkDTC());
        }
        for(int i = 0xC000; i < 0xFFFF; ++i)
        {
            assertTrue(new DiagnosticTroubleCode(i, "").isNetworkDTC());
        }
    }

    @Test
    public void jurisdictionClassificationTest()
    {
        DiagnosticTroubleCode test0 = new DiagnosticTroubleCode("P0000", ""),
                              test1 = new DiagnosticTroubleCode("P1000", ""),
                              test2 = new DiagnosticTroubleCode("P2000", ""),
                              test3 = new DiagnosticTroubleCode("P3000", ""),
                              test4 = new DiagnosticTroubleCode("C0000", ""),
                              test5 = new DiagnosticTroubleCode("C1000", ""),
                              test6 = new DiagnosticTroubleCode("C2000", ""),
                              test7 = new DiagnosticTroubleCode("C3000", ""),
                              test8 = new DiagnosticTroubleCode("B0000", ""),
                              test9 = new DiagnosticTroubleCode("B1000", ""),
                              test10 = new DiagnosticTroubleCode("B2000", ""),
                              test11 = new DiagnosticTroubleCode("B3000", ""),
                              test12 = new DiagnosticTroubleCode("U0000", ""),
                              test13 = new DiagnosticTroubleCode("U1000", ""),
                              test14 = new DiagnosticTroubleCode("U2000", ""),
                              test15 = new DiagnosticTroubleCode("U3000", "");
        assertTrue(test0.isCoreDTC());
        assertTrue(test1.isNonUniformDTC());
        assertTrue(test2.isCoreDTC());
        assertTrue(test3.isNonUniformDTC());
        assertTrue(test4.isCoreDTC());
        assertTrue(test5.isNonUniformDTC());
        assertTrue(test6.isNonUniformDTC());
        assertTrue(test7.isReservedDTC());
        assertTrue(test8.isCoreDTC());
        assertTrue(test9.isNonUniformDTC());
        assertTrue(test10.isNonUniformDTC());
        assertTrue(test11.isReservedDTC());
        assertTrue(test12.isCoreDTC());
        assertTrue(test13.isNonUniformDTC());
        assertTrue(test14.isNonUniformDTC());
        assertTrue(test15.isReservedDTC());
    }
}
