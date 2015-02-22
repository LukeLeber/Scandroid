package com.lukeleber.scandroid.gui.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.lukeleber.scandroid.BuildConfig;
import com.lukeleber.scandroid.R;
import com.lukeleber.scandroid.sae.j1979.ServiceFacet;

import java.util.ArrayList;
import java.util.List;

/**
 * A helpful dialog that allows users to filter viewed parameters.  This dialog can be shown
 * by any activity that implements the
 * {@link com.lukeleber.scandroid.gui.dialogs.ParameterSelector.ParameterSelectorHost}
 * interface.  Initially, all supported parameters are unselected and the user must select
 * the ones that they wish to view.
 *
 * <p>Note - Although the name of this class is "ParameterSelector", this dialog can be used
 * to filter all types of PIDs/TIDs/OBDMIDs/INFOTYPEs.</p>
 *
 */
public class ParameterSelector<T extends ServiceFacet>
        extends DialogFragment
{
    /**
     * An interface that is implemented by classes (generally
     * {@link android.app.Activity activities}) that host a
     * {@link com.lukeleber.scandroid.gui.dialogs.ParameterSelector} dialog.
     */
    public static interface ParameterSelectorHost
    {
        /**
         * Retrieves the list of parameters that should be presented to the user as view options
         *
         * @return the list of parameters that should be presented to the user as view options
         *
         */
        @NonNull <T extends ServiceFacet> List<T> getSupportedParameters();

        /**
         * Invoked when the user presses the positive dialog button, this callback contains the
         * result of the user's parameter filtering.
         *
         * @param selectedParameters the result of the user's parameter filtering
         *
         */
        <T extends ServiceFacet> void onParameterSelection(@NonNull List<T> selectedParameters);
    }

    /// @internal debugging tag
    private final static String TAG = ParameterSelector.class.getName();

    /// The {@link com.lukeleber.scandroid.gui.dialogs.ParameterSelector.ParameterSelectorHost}
    /// of this dialog.  This member is non-null only between a pair of onAttach / onDetach calls
    private ParameterSelectorHost listener;

    /// The list of parameters that the user has selected thus far.  This member is non-null only
    /// between a pair of onAttach / onDetach calls
    private List<T> selectedParameters;

    /**
     * {@inheritDoc}
     *
     * @throws java.lang.ClassCastException if the calling {@link android.app.Activity} does not
     * implement the
     * {@link com.lukeleber.scandroid.gui.dialogs.ParameterSelector.ParameterSelectorHost}
     * interface.
     */
    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        if (!ParameterSelectorHost.class.isAssignableFrom(activity.getClass()))
        {
            throw new ClassCastException(activity.getClass().getName()
                    + " must implement " +
                    ParameterSelectorHost.class.getName());
        }
        if (BuildConfig.DEBUG && this.listener != null)
        {
            Log.w(TAG, "illegal state: host != null");
        }
        if(BuildConfig.DEBUG && this.selectedParameters != null)
        {
            Log.w(TAG, "illegal state: selectedParameters != null");
        }
        this.listener = (ParameterSelectorHost) activity;
        this.selectedParameters = new ArrayList<>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onDetach()
    {
        super.onDetach();
        if (BuildConfig.DEBUG && this.listener == null)
        {
            Log.w(TAG, "illegal state: host == null");
        }
        if(BuildConfig.DEBUG && this.selectedParameters == null)
        {
            Log.w(TAG, "illegal state: selectedParameters == null");
        }
        this.listener = null;
        this.selectedParameters = null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Dialog onCreateDialog(Bundle sis)
    {
        final List<T> supportedParameters = listener.getSupportedParameters();
        String[] choices = new String[supportedParameters.size()];
        {
            int i = 0;
            for (T pid : supportedParameters)
            {
                choices[i++] = pid.getDisplayName();
            }
        }
        return new AlertDialog.Builder(getActivity())
            .setTitle(R.string.dialog_parameter_selector_title)
            .setMultiChoiceItems(choices, null,
                new DialogInterface.OnMultiChoiceClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked)
                    {
                        if (isChecked)
                        {
                            selectedParameters.add(supportedParameters.get(which));
                        }
                        else
                        {
                            selectedParameters.remove(supportedParameters.get(which));
                        }
                    }
                })
            .setNegativeButton(R.string.dialog_parameter_selector_negative_button_text, null)
            .setPositiveButton(R.string.dialog_parameter_selector_positive_button_text,
                new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        listener.onParameterSelection(selectedParameters);
                    }
                })
            .create();
    }
}
