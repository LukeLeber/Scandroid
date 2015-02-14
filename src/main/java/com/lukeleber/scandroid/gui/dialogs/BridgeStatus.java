package com.lukeleber.scandroid.gui.dialogs;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.lukeleber.scandroid.BuildConfig;
import com.lukeleber.scandroid.R;
import com.lukeleber.scandroid.gui.InterpreterHost;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * An informative dialog that can be shown by any {@link android.app.Activity} that implements
 * the {@link com.lukeleber.scandroid.gui.InterpreterHost} interface.  Various information about
 * the interpreter bridge is displayed to the user including hardware types and versions,
 * link status, average latency, and system voltage.  Specialized versions of this class can
 * be derived for use with implementation specific types of hardware.
 *
 * <p>For example, an ELM327 equipped with a bluetooth interface might present the device name,
 * UUID, and signal strength in addition to the information provided by the base class.</p>
 *
 */
public class BridgeStatus
        extends DialogFragment
{

    /// @internal debugging tag
    private final static String TAG = BridgeStatus.class.getName();

    /// The {@link com.lukeleber.scandroid.gui.InterpreterHost} of this dialog
    /// This member is non-null only between a pair of onAttach / onDetach calls
    private InterpreterHost host;

    /**
     * Retrieves a reference to the {@link com.lukeleber.scandroid.gui.InterpreterHost} of this
     * dialog
     *
     * @return a reference to the {@link com.lukeleber.scandroid.gui.InterpreterHost} of this
     * dialog
     */
    @SuppressWarnings("unused")
    protected final InterpreterHost getHost()
    {
        return host;
    }

    /**
     * This method takes all appropriate actions in order to update the bridge status data (which
     * could involve asynchronous operations in some cases).  Derived classes should call through
     * to the implementation of this method found in their superclass.
     */
    protected void update()
    {

    }

    /**
     * Invoked when the user presses the 'refresh' button, this method simply calls the
     * protected method {@link BridgeStatus#update()}.
     */
    @SuppressWarnings("unused")
    @OnClick(R.id.dialog_bridge_status_refresh_button)
    void onRefreshClicked()
    {
        update();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        if (!InterpreterHost.class.isAssignableFrom(activity.getClass()))
        {
            throw new ClassCastException(activity.getClass().getName()
                    + " must implement " +
                    InterpreterHost.class.getName());
        }
        if(BuildConfig.DEBUG && this.host != null)
        {
            Log.w(TAG, "illegal state: host != null");
        }
        this.host = (InterpreterHost) activity;
    }

    /**
     * {@inheritDoc}
     *
     */
    @Override
    public void onDetach()
    {
        super.onDetach();
        if(BuildConfig.DEBUG && this.host == null)
        {
            Log.w(TAG, "illegal state: host == null");
        }
        this.host = null;
    }

    /**
     * {@inheritDoc}
     *
     */
    @Override
    public Dialog onCreateDialog(Bundle sis)
    {
        final Activity activity = getActivity();
        @SuppressLint("InflateParams")
        View view = activity.getLayoutInflater().inflate(R.layout.dialog_bridge_status, null);
        ButterKnife.inject(this, view);
        return new AlertDialog.Builder(activity)
                .setView(view)
                .setTitle(R.string.dialog_bridge_status_title)
                .setNegativeButton(R.string.dialog_bridge_status_dismiss_button_text, null)
                .create();
    }
}
