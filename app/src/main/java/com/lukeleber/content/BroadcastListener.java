// This file is protected under the KILLGPL.
// For more information, visit http://www.lukeleber.github.io/KILLGPL.html
//
// Copyright (c) Luke Leber <LukeLeber@gmail.com>

package com.lukeleber.content;

import android.content.Context;
import android.content.Intent;

/**
 * <p>An <strong>INTERFACE</strong> for {@link android.content.BroadcastReceiver broadcast
 * receivers} that allows the use of such receivers in the same way as we have registered such
 * things in swing for the last decade.  This interface is for use with the {@link
 * com.lukeleber.app.EnhancedActivity#addBroadcastReceiver(BroadcastListener,
 * android.content.IntentFilter)} method such that the following pattern is valid:</p> <p/> <pre
 * class="prettyprint"> public class MyActivity extends EnhancedActivity implements
 * BroadcastListener { public final void onReceive(Context ctx, Intent intent) { ... } <p/>
 * protected final void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState);
 * super.addBroadcastReceiver(this, new IntentFilter(...); } } </pre> <p/> <p>Furthermore, the
 * possibility of resource leaks due to programmer error is now gone.  The {@link
 * com.lukeleber.app.EnhancedActivity} class manages the registration and unregistration of the
 * underlying {@link android.content.BroadcastReceiver} objects automatically following the same
 * activity lifecycle as defined within the {@link android.app.Activity} documentation. </p>
 * <p/>
 */
public interface BroadcastListener
{
    /**
     * This method is called when the com.lukeleber.content.IBroadcastReceiver is receiving an
     * Intent broadcast.  During this time you can use the other methods on
     * com.lukeleber.content.IBroadcastReceiver to view/modify the current result values.  This
     * method is always called within the main thread of its process, unless you explicitly asked
     * for it to be scheduled on a different thread using {@link android.content.Context#registerReceiver(android.content.BroadcastReceiver,
     * android.content.IntentFilter, String, android.os.Handler)}. When it runs on the main thread
     * you should never perform long-running operations in it (there is a timeout of 10 seconds that
     * the system allows before considering the receiver to be blocked and a candidate to be
     * killed). You cannot launch a popup dialog in your implementation of onReceive(). <p/>
     * <p><b>If this BroadcastReceiver was launched through a &lt;receiver&gt; tag, then the object
     * is no longer alive after returning from this function.</b>  This means you should not perform
     * any operations that return a result to you asynchronously -- in particular, for interacting
     * with services, you should use {@link android.content.Context#startService(android.content.Intent)}
     * instead of {@link android.content.Context#bindService(android.content.Intent,
     * android.content.ServiceConnection, int)}.</p>
     * <p/>
     * <p>The Intent filters used in {@link android.content.Context#registerReceiver} and in
     * application manifests are <em>not</em> guaranteed to be exclusive. They are hints to the
     * operating system about how to find suitable recipients. It is possible for senders to force
     * delivery to specific recipients, bypassing filter resolution.  For this reason,
     * implementations should respond only to known actions, ignoring any unexpected Intents that
     * they may receive.
     *
     * @param context
     *         The Context in which the receiver is running.
     * @param intent
     *         The Intent being received.
     */
    public void onReceive(Context context, Intent intent);
}
