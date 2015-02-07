
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
 * Unit tests for the {@link com.lukeleber.scandroid.sae.j1979.FuelSystemStatus} class.
 *
 */
@Config(manifest = "./src/main/AndroidManifest.xml", emulateSdk = 18)
@RunWith(RobolectricTestRunner.class)
public class FuelSystemStatusTest
{
    /**
     * Confirms that the enumerated names correspond to the values mandated by SAE J1979.  This
     * should never fail (unless an accidental refactoring happens?).
     *
     */
    @Test
    public void nameTest()
    {
        assertEquals("OL", FuelSystemStatus.OL.name());
        assertEquals("CL", FuelSystemStatus.CL.name());
        assertEquals("OL_DRIVE", FuelSystemStatus.OL_DRIVE.name());
        assertEquals("OL_FAULT", FuelSystemStatus.OL_FAULT.name());
        assertEquals("CL_FAULT", FuelSystemStatus.CL_FAULT.name());
    }

    /**
     * Confirms that the enumerated values correspond to the values mandated by SAE J1979.  This
     * should never fail (unless an accidental modification is made?).
     */
    @Test
    public void valueTest()
    {
        assertEquals(0x1, FuelSystemStatus.OL.getMask());
        assertEquals(0x2, FuelSystemStatus.CL.getMask());
        assertEquals(0x4, FuelSystemStatus.OL_DRIVE.getMask());
        assertEquals(0x8, FuelSystemStatus.OL_FAULT.getMask());
        assertEquals(0x10, FuelSystemStatus.CL_FAULT.getMask());
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
                        .getString(R.string.fuel_system_status_enum_open_loop_desc),
                FuelSystemStatus.OL.toI18NString(
                        Robolectric.getShadowApplication().getApplicationContext()));
        assertEquals(
                Robolectric.getShadowApplication()
                        .getString(R.string.fuel_system_status_enum_closed_loop_desc),
                FuelSystemStatus.CL.toI18NString(
                        Robolectric.getShadowApplication().getApplicationContext()));
        assertEquals(
                Robolectric.getShadowApplication()
                        .getString(R.string.fuel_system_status_enum_open_loop_drive_desc),
                FuelSystemStatus.OL_DRIVE.toI18NString(
                        Robolectric.getShadowApplication().getApplicationContext()));
        assertEquals(
                Robolectric.getShadowApplication()
                        .getString(R.string.fuel_system_status_enum_open_loop_fault_desc),
                FuelSystemStatus.OL_FAULT.toI18NString(
                        Robolectric.getShadowApplication().getApplicationContext()));
        assertEquals(
                Robolectric.getShadowApplication()
                        .getString(R.string.fuel_system_status_enum_closed_loop_fault_desc),
                FuelSystemStatus.CL_FAULT.toI18NString(
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
            for(int i = 0; i < FuelSystemStatus.values().length; ++i)
            {
                p.writeParcelable(FuelSystemStatus.values()[i], FuelSystemStatus.values()[i].describeContents());
            }
            p.setDataPosition(0);
            for(int i = 0; i < FuelSystemStatus.values().length; ++i)
            {
                assertEquals(FuelSystemStatus.values()[i], p.readParcelable(null));
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
