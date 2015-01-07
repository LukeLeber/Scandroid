/*
 * This file is protected under the KILLGPL.
 * For more information visit <insert_valid_link_to_killgpl_here>
 * <p/>
 * Copyright (c) Luke A. Leber <LukeLeber@gmail.com> 2014
 */

package com.lukeleber.app;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.widget.Toast;

import com.lukeleber.content.BroadcastListener;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>An {@link android.app.Activity} with more built-in conveniences and features than the stock
 * SDK class that is designed to provide more of a "swing-like" feel.  Among the enhancements are:
 * <ul> <li>A scalable, flexible listener-based activity result handling mechanism <p> <pre
 * class="prettyprint"> public class MyEnhancedActivity extends EnhancedActivity implements
 * ActivityResultListener { <p/> private final static Class<?> clazz = ...; <p/> /// The handle to
 * use in 'startActivityForResult' private int handle; <p/> /// The overridden
 * ActivityResultListener interface public final void onActivityResult(int resultCode, Intent data)
 * { //... } <p/> public final void onCreate(Bundle sis) { this.handle =
 * super.addResultListener(this); super.startActivityForResult(new Intent(this, clazz), handle); } }
 * </pre> </p></li> <li>A safe, flexible, and scalable listener-based broadcast receiver mechanism
 * <p> <pre class="prettyprint"> public class MyActivity extends EnhancedActivity implements
 * BroadcastListener { /// The overridden BroadcastListener interface public final void
 * onReceive(Context ctx, Intent intent) { ... } <p/> protected final void onCreate(Bundle sis) {
 * super.onCreate(sis); super.addBroadcastReceiver(this, new IntentFilter(...); } } </pre> </p></li>
 * <li>Short-hand {@link android.widget.Toast} convenience functions TODO: Example </li> </ul> </p>
 */
public abstract class EnhancedActivity
        extends Activity
{
    private final static String TAG = EnhancedActivity.class.getName();

    /// The list of {@link ActivityResultListener listeners} currently registered
    private final List<ActivityResultListener> resultListeners;

    /// The list of {@link BroadcastListener broadcast listeners} currently registered
    private final List<BroadcastReceiver> broadcastReceivers;

    /**
     * Protected default constructor
     */
    protected EnhancedActivity()
    {
        this.resultListeners = new ArrayList<>();
        this.broadcastReceivers = new ArrayList<>();
    }

    /**
     * Adds the provided {@link ActivityResultListener} to this <code>EnhancedActivity</code>
     *
     * @param listener
     *         the listener to add
     *
     * @return the request code that was generated for this listener
     */
    public int addResultListener(ActivityResultListener listener)
    {
        int code = resultListeners.size();
        resultListeners.add(listener);
        return code;
    }

    /**
     * Adds the provided {@link com.lukeleber.content.BroadcastListener} to this
     * <code>EnhancedActivity</code>.  Each listener shall be implicitly unregistered when this
     * object is {@link android.app.Activity#onDestroy() destroyed}.
     *
     * @param listener
     *         the {@link com.lukeleber.content.BroadcastListener} to add
     * @param filter
     *         the {@link android.content.IntentFilter} to use
     */
    public void addBroadcastReceiver(final BroadcastListener listener, IntentFilter filter)
    {
        BroadcastReceiver wrapped = new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context context, Intent intent)
            {
                listener.onReceive(context, intent);
            }
        };
        broadcastReceivers.add(wrapped);
        super.registerReceiver(wrapped, filter);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        for (BroadcastReceiver receiver : broadcastReceivers)
        {
            super.unregisterReceiver(receiver);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected final void onActivityResult(int code, int result, Intent intent)
    {
        ActivityResultListener listener = resultListeners.get(code);
        if (listener != null)
        {
            listener.onActivityResult(result, intent);
        }
        else
        {
            Log.wtf(TAG, "unknown result code");
        }
    }

    /**
     * Shows a short toast with the text found with the provided string resource ID
     *
     * @param resourceID
     *         the resource ID (IE R.string.XXX) to show in the toast
     */
    protected final void shortToast(int resourceID)
    {
        Toast.makeText(this, super.getString(resourceID), Toast.LENGTH_SHORT).show();
    }

    /**
     * Shows a long toast with the text found with the provided string resource ID
     *
     * @param resourceID
     *         the resource ID (IE R.string.XXX) to show in the toast
     */
    @SuppressWarnings("unused")
    protected final void longToast(int resourceID)
    {
        Toast.makeText(this, super.getString(resourceID), Toast.LENGTH_LONG).show();
    }

    @SuppressWarnings("unused")
    protected final void shortToast(String text)
    {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    protected final void longToast(String text)
    {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
    }
}
