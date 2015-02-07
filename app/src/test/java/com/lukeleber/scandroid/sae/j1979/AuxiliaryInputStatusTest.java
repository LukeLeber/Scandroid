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
 * Unit tests for the {@link com.lukeleber.scandroid.sae.j1979.AuxiliaryInputStatus} class.
 *
 */
@Config(manifest = "./src/main/AndroidManifest.xml", emulateSdk = 18)
@RunWith(RobolectricTestRunner.class)
public class AuxiliaryInputStatusTest
{
    /**
     * Confirms that the enumerated names correspond to the values mandated by SAE J1979.  This
     * should never fail (unless an accidental refactoring happens?).
     *
     */
    @Test
    public void nameTest()
    {
        assertEquals("OFF", AuxiliaryInputStatus.OFF.name());
        assertEquals("ON", AuxiliaryInputStatus.ON.name());
    }

    /**
     * Confirms that the enumerated values correspond to the values mandated by SAE J1979.  This
     * should never fail (unless an accidental modification is made?).
     */
    @Test
    public void valueTest()
    {
        assertEquals(0x0, AuxiliaryInputStatus.OFF.getMask());
        assertEquals(0x1, AuxiliaryInputStatus.ON.getMask());
    }

    /**
     * (Reasonably) Confirms that the user friendly descriptions are pulled from the proper
     * I18N string resource file and not hard-coded in a single language.
     *
     */
    @Test
    public void userFriendlyToStringTest() {
        assertEquals(
                Robolectric.getShadowApplication()
                        .getString(R.string.auxiliary_input_status_off_desc),
                AuxiliaryInputStatus.OFF.toI18NString(
                        Robolectric.getShadowApplication().getApplicationContext()));
        assertEquals(
                Robolectric.getShadowApplication()
                        .getString(R.string.auxiliary_input_status_on_desc),
                AuxiliaryInputStatus.ON.toI18NString(
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
            for(int i = 0; i < AuxiliaryInputStatus.values().length; ++i)
            {
                p.writeParcelable(AuxiliaryInputStatus.values()[i], AuxiliaryInputStatus.values()[i].describeContents());
            }
            p.setDataPosition(0);
            for(int i = 0; i < AuxiliaryInputStatus.values().length; ++i)
            {
                assertEquals(AuxiliaryInputStatus.values()[i], p.readParcelable(null));
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
