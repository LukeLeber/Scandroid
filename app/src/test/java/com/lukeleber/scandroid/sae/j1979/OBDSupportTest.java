package com.lukeleber.scandroid.sae.j1979;

import android.os.Parcel;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;

/**
 * Unit tests for the {@link com.lukeleber.scandroid.sae.j1979.OBDSupport} class.
 *
 */
@Config(manifest = "./src/main/AndroidManifest.xml", emulateSdk = 18)
@RunWith(RobolectricTestRunner.class)
public class OBDSupportTest
{

    /**
     * Confirms that the number of enumerated members matches the number mandated by SAE J1979.
     */
    @Test
    public void conformanceTest()
    {
        assertEquals(0xD, OBDSupport.values().length);
    }

    /**
     * Confirms that the {@link OBDSupport#toString()} method of enumerated member correspond to
     * the values mandated by SAE J1979.
     *
     */
    @Test
    public void toStringTest()
    {
        assertEquals("OBD II", OBDSupport.OBDII_CALIFORNIA_ARB.toString());
        assertEquals("OBD", OBDSupport.OBD_FEDERAL_EPA.toString());
        assertEquals("OBD and OBD II", OBDSupport.OBD_AND_OBD_II.toString());
        assertEquals("OBD I", OBDSupport.OBD_I.toString());
        assertEquals("No OBD", OBDSupport.NO_OBD.toString());
        assertEquals("EOBD", OBDSupport.EOBD.toString());
        assertEquals("EOBD and OBD II", OBDSupport.EOBD_AND_OBD_II.toString());
        assertEquals("EOBD and OBD", OBDSupport.EOBD_AND_OBD.toString());
        assertEquals("EOBD, OBD, and OBD II", OBDSupport.EOBD_OBD_AND_OBD_II.toString());
        assertEquals("JOBD", OBDSupport.JOBD.toString());
        assertEquals("JOBD and OBD II", OBDSupport.JOBD_AND_OBD_II.toString());
        assertEquals("JOBD and OBD", OBDSupport.JOBD_AND_OBD.toString());
        assertEquals("JOBD, EOBD, and OBD II", OBDSupport.JOBD_EOBD_AND_OBD_II.toString());
    }

    /**
     * Confirms that the enumerated values correspond to the values mandated by SAE J1979.  This
     * should never fail (unless an accidental modification is made?).
     */
    @Test
    public void valueTest()
    {
        assertEquals(0x1, OBDSupport.OBDII_CALIFORNIA_ARB.getValue());
        assertEquals(0x2, OBDSupport.OBD_FEDERAL_EPA.getValue());
        assertEquals(0x3, OBDSupport.OBD_AND_OBD_II.getValue());
        assertEquals(0x4, OBDSupport.OBD_I.getValue());
        assertEquals(0x5, OBDSupport.NO_OBD.getValue());
        assertEquals(0x6, OBDSupport.EOBD.getValue());
        assertEquals(0x7, OBDSupport.EOBD_AND_OBD_II.getValue());
        assertEquals(0x8, OBDSupport.EOBD_AND_OBD.getValue());
        assertEquals(0x9, OBDSupport.EOBD_OBD_AND_OBD_II.getValue());
        assertEquals(0xA, OBDSupport.JOBD.getValue());
        assertEquals(0xB, OBDSupport.JOBD_AND_OBD_II.getValue());
        assertEquals(0xC, OBDSupport.JOBD_AND_OBD.getValue());
        assertEquals(0xD, OBDSupport.JOBD_EOBD_AND_OBD_II.getValue());
    }

    /**
     * Confirms that all enumerated members can be used with parcelling without issue.
     *
     * Simply writes all enumerated members to a parcel and reads them back.
     *
     */
    @Test
    public void parcelableTest()
    {
        Parcel p = null;
        try
        {
            p = Parcel.obtain();
            for(int i = 0; i < OBDSupport.values().length; ++i)
            {
                p.writeParcelable(OBDSupport.values()[i], OBDSupport.values()[i].describeContents());
            }
            p.setDataPosition(0);
            for(int i = 0; i < OBDSupport.values().length; ++i)
            {
                assertEquals(OBDSupport.values()[i], p.readParcelable(null));
            }
        }
        finally
        {
            if(p != null)
            {
                p.recycle();
            }
        }
    }
}
