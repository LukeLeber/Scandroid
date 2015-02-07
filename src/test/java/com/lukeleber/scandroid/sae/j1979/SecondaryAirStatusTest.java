package com.lukeleber.scandroid.sae.j1979;

import android.os.Parcel;

import com.lukeleber.scandroid.R;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import static org.junit.Assert.assertEquals;
/**
 * Unit tests for the {@link com.lukeleber.scandroid.sae.j1979.SecondaryAirStatus} class.
 *
 */
@Config(manifest = "./src/main/AndroidManifest.xml", emulateSdk = 18)
@RunWith(RobolectricTestRunner.class)
public class SecondaryAirStatusTest
{
    @Test
    public void nameTest()
    {
        assertEquals("UPS", SecondaryAirStatus.UPS.name());
        assertEquals("DNS", SecondaryAirStatus.DNS.name());
        assertEquals("OFF", SecondaryAirStatus.OFF.name());
    }

    @Test
    public void valueTest()
    {
        assertEquals(0x0, SecondaryAirStatus.UPS.getValue());
        assertEquals(0x1, SecondaryAirStatus.DNS.getValue());
        assertEquals(0x2, SecondaryAirStatus.OFF.getValue());
    }

    @Test
    public void toI18NStringTest()
    {
        assertEquals(
                Robolectric.getShadowApplication().getApplicationContext()
                        .getString(R.string.secondary_air_status_ups_desc),
                SecondaryAirStatus.UPS.toI18NString(
                        Robolectric.getShadowApplication().getApplicationContext()));
        assertEquals(
                Robolectric.getShadowApplication().getApplicationContext()
                        .getString(R.string.secondary_air_status_dns_desc),
                SecondaryAirStatus.DNS.toI18NString(
                        Robolectric.getShadowApplication().getApplicationContext()));
        assertEquals(
                Robolectric.getShadowApplication().getApplicationContext()
                        .getString(R.string.secondary_air_status_off_desc),
                SecondaryAirStatus.OFF.toI18NString(
                        Robolectric.getShadowApplication().getApplicationContext()));
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
            for(int i = 0; i < SecondaryAirStatus.values().length; ++i)
            {
                p.writeParcelable(SecondaryAirStatus.values()[i], SecondaryAirStatus.values()[i].describeContents());
            }
            p.setDataPosition(0);
            for(int i = 0; i < SecondaryAirStatus.values().length; ++i)
            {
                assertEquals(SecondaryAirStatus.values()[i], p.readParcelable(null));
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
