package com.lukeleber.scandroid.sae.j1979;

import android.os.Parcel;
import android.support.annotation.NonNull;

import com.lukeleber.scandroid.util.Unit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.Serializable;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
// TODO: Retrolambda integration to finish this fight?
@Config(manifest = "./src/main/AndroidManifest.xml", emulateSdk = 18)
@RunWith(RobolectricTestRunner.class)
public class DefaultPIDTest
{

    static class TestUnmarshaller implements PID.Unmarshaller<Object>, Serializable
    {

        @Override
        public Object invoke(@NonNull byte... bytes) {
            return null;
        }
    }

    @Test
    public void parcelableTest()
    {

        DefaultPID<Object> test =
            new DefaultPID<>(
                1, /// ID
                "displayName",
                "userFriendlyDisplayName",
                "description",
                new HashMap<Unit, PID.Unmarshaller<Object>>()
                {
                    {
                        super.put(Unit.PACKETED,
                            new TestUnmarshaller());
                    }
                }
            );
        Parcel p = null;
        try
        {
            p = Parcel.obtain();
            p.writeParcelable(test, test.describeContents());
            p.setDataPosition(0);
            assertEquals(test, p.readParcelable(null));
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
