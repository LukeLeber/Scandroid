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
 * Unit tests for the {@link com.lukeleber.scandroid.sae.j1979.Service} class.
 *
 */
@Config(manifest = "./src/main/AndroidManifest.xml", emulateSdk = 18)
@RunWith(RobolectricTestRunner.class)
public class ServiceTest
{

    /**
     * Confirms that the enumerated values correspond to the values mandated by SAE J1979.  This
     * should never fail (unless an accidental modification is made?).
     */
    @Test
    public void valueTest()
    {
        assertEquals(0x1, Service.LIVE_DATASTREAM.getID());
        assertEquals(0x2, Service.FREEZE_FRAME_DATA.getID());
        assertEquals(0x3, Service.RETRIEVE_DTC.getID());
        assertEquals(0x4, Service.CLEAR_DTC.getID());
        assertEquals(0x5, Service.OXYGEN_SENSOR_TEST_RESULTS.getID());
        assertEquals(0x6, Service.OTHER_TEST_RESULTS.getID());
        assertEquals(0x7, Service.RETRIEVE_PENDING_DTC.getID());
        assertEquals(0x8, Service.REMOTE_CONTROL.getID());
        assertEquals(0x9, Service.VEHICLE_INFORMATION.getID());
    }

    /**
     * (Reasonably) Confirms that the user friendly descriptions are pulled from the proper
     * I18N string resource file and not hard-coded in a single language.
     *
     */
    @Test
    public void userFriendlyToStringTest()
    {
        assertEquals(
                Robolectric.getShadowApplication()
                        .getString(R.string.service_01_desc),
                Service.LIVE_DATASTREAM.toI18NString(
                        Robolectric.getShadowApplication().getApplicationContext()));
        assertEquals(
                Robolectric.getShadowApplication()
                        .getString(R.string.service_02_desc),
                Service.FREEZE_FRAME_DATA.toI18NString(
                        Robolectric.getShadowApplication().getApplicationContext()));
        assertEquals(
                Robolectric.getShadowApplication()
                        .getString(R.string.service_03_desc),
                Service.RETRIEVE_DTC.toI18NString(
                        Robolectric.getShadowApplication().getApplicationContext()));
        assertEquals(
                Robolectric.getShadowApplication()
                        .getString(R.string.service_04_desc),
                Service.CLEAR_DTC.toI18NString(
                        Robolectric.getShadowApplication().getApplicationContext()));
        assertEquals(
                Robolectric.getShadowApplication()
                        .getString(R.string.service_05_desc),
                Service.OXYGEN_SENSOR_TEST_RESULTS.toI18NString(
                        Robolectric.getShadowApplication().getApplicationContext()));
        assertEquals(
                Robolectric.getShadowApplication()
                        .getString(R.string.service_06_desc),
                Service.OTHER_TEST_RESULTS.toI18NString(
                        Robolectric.getShadowApplication().getApplicationContext()));
        assertEquals(
                Robolectric.getShadowApplication()
                        .getString(R.string.service_07_desc),
                Service.RETRIEVE_PENDING_DTC.toI18NString(
                        Robolectric.getShadowApplication().getApplicationContext()));
        assertEquals(
                Robolectric.getShadowApplication()
                        .getString(R.string.service_08_desc),
                Service.REMOTE_CONTROL.toI18NString(
                        Robolectric.getShadowApplication().getApplicationContext()));
        assertEquals(
                Robolectric.getShadowApplication()
                        .getString(R.string.service_09_desc),
                Service.VEHICLE_INFORMATION.toI18NString(
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
            for(int i = 0; i < Service.values().length; ++i)
            {
                p.writeParcelable(Service.values()[i], Service.values()[i].describeContents());
            }
            p.setDataPosition(0);
            for(int i = 0; i < Service.values().length; ++i)
            {
                assertEquals(Service.values()[i], p.readParcelable(null));
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
