// This file is protected under the KILLGPL.
// For more information, visit http://www.lukeleber.github.io/KILLGPL.html
//
// Copyright (c) Luke Leber <LukeLeber@gmail.com>

package com.lukeleber.scandroid.gui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.lukeleber.app.EnhancedActivity;
import com.lukeleber.scandroid.Globals;
import com.lukeleber.scandroid.R;
import com.lukeleber.scandroid.gui.dialogs.ParameterSelector;
import com.lukeleber.scandroid.sae.j1979.PID;
import com.lukeleber.scandroid.sae.j1979.ServiceFacet;
import com.lukeleber.scandroid.sae.j1979.detail.AppendixA;
import com.lukeleber.scandroid.sae.j1979.detail.AppendixB;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * The "Home Page" of the Scandroid application.  This activity is shown when the application
 * starts.  From this activity, all of the facets of Scandroid may be navigated to.
 * <p/>
 * TODO: Interface is incomplete
 */
public class Scandroid
        extends
        EnhancedActivity
        implements ParameterSelector.ParameterSelectorHost
{

    private final static String TAG = Scandroid.class.getName();

    /*package*/ Handler handler = new Handler();

    @OnClick(R.id.scanToolGeneric)
    void onScanToolGenericClicked()
    {
        if (Globals.getInterpreter(getApplicationContext()) == null ||
                Globals.getProfile(getApplicationContext()) == null)
        {
            Toast.makeText(Scandroid.this, "opening connection to vehicle...", Toast.LENGTH_SHORT)
                 .show();
            GenericScanner.startGenericScanner(this);
        }
        else
        {
            startActivity(new Intent(this, GenericScanner.class));
        }
    }

    List<ServiceFacet> mockPIDs = new ArrayList<ServiceFacet>()
    {
        {
            super.add(AppendixB.AUXILIARY_INPUT_STATUS);
            super.add(AppendixB.MONITOR_STATUS);
            super.add(AppendixB.ENGINE_COOLANT_TEMPERATURE);
            super.add(AppendixB.ABSOLUTE_LOAD_VALUE);
            super.add(AppendixB.ABSOLUTE_THROTTLE_POSITION);
            super.add(AppendixB.CALCULATED_ENGINE_LOAD);
        }

    };

    @Override
    public @NonNull List<ServiceFacet> getSupportedParameters()
    {
        return mockPIDs;
    }

    @Override
    public void onParameterSelection(@NonNull List<? extends ServiceFacet> selectedParameters)
    {
        System.out.println(selectedParameters);
    }

    @OnClick(R.id.vinDecoder)
    void onVINDecoderClicked()
    {

        new ParameterSelector().show(super.getFragmentManager(), ParameterSelector.class.getSimpleName());
//        new BridgeStatus().show(super.getFragmentManager(), BridgeStatus.class.getSimpleName());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Globals.initStringCache(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scandroid);
        ButterKnife.inject(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.scandroid, menu);
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.scandroid_menu_settings_item:
                Toast.makeText(this, "Not Implemented Yet", Toast.LENGTH_SHORT)
                     .show();
                // TODO
                break;
            case R.id.scandroid_menu_logging_item:
                Toast.makeText(this, "Not Implemented Yet", Toast.LENGTH_SHORT)
                     .show();
                // TODO
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    /**
     * A static nested class to hold extras associated with this activity
     */
    public final static class Extras
    {
        /// Should we continue in "Generic Mode" or "Expert Mode"?
        public final static String SCANNER_MODE = "mode";
    }
}
