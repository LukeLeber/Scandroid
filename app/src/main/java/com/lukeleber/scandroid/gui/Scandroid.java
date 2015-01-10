/*
 * This file is protected under the KILLGPL.
 * For more information visit <insert_valid_link_to_killgpl_here>
 * <p/>
 * Copyright (c) Luke A. Leber <LukeLeber@gmail.com> 2014
 */

package com.lukeleber.scandroid.gui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.lukeleber.app.EnhancedActivity;
import com.lukeleber.scandroid.Globals;
import com.lukeleber.scandroid.R;
import com.lukeleber.scandroid.database.DiagnosticTroubleCodeDatabase;

import butterknife.ButterKnife;
import butterknife.InjectView;
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
{

    private final static String TAG = Scandroid.class.getName();
    /// The button for selecting "Generic Mode"
    @InjectView(R.id.scanToolGeneric)
    Button scanToolGeneric;
    /// The button for selecting "Expert Mode"
    @InjectView(R.id.scanToolExpert)
    Button scanToolExpert;
    /// The button for starting the VIN decoder service
    @InjectView(R.id.vinDecoder)
    Button vinDecoder;

    /*package*/ Handler handler = new Handler();

    /**
     * Invoked when the "Generic OBDII Mode" button is clicked.  This method creates a new {@link
     * killgpl.scandroid.gui.SelectInterface} intent with the {@link killgpl.scandroid.gui.ConfigurationMode#GENERIC_MODE}
     * extra field.
     */
    @OnClick(R.id.scanToolGeneric)
    void onScanToolGenericClicked()
    {
        if (Globals.getInterpreter(getApplicationContext()) == null ||
                Globals.getProfile(getApplicationContext()) == null)
        {
            Toast.makeText(Scandroid.this, "opening connection to vehicle...", Toast.LENGTH_SHORT)
                 .show();
            GenericScanner.startGenericScanner(this, null);
        }
        else
        {
            startActivity(new Intent(this, GenericScanner.class));
        }
    }

    /**
     * Invoked when the "Expert Mode" button is clicked.  This method creates a new {@link
     * killgpl.scandroid.gui.SelectInterface} intent with the {@link killgpl.scandroid.gui.ConfigurationMode#EXPERT_MODE}
     * extra field.
     */
    @OnClick(R.id.scanToolExpert)
    void onScanToolExpertClicked()
    {
        Toast.makeText(this, "Not Implemented Yet", Toast.LENGTH_SHORT)
             .show();
        // TODO:
/*        Intent intent = new Intent(Scandroid.this, SelectInterface.class);
        intent.putExtra(Extras.SCANNER_MODE, ConfigurationMode.EXPERT_MODE);
        startActivity(intent);*/
    }

    @OnClick(R.id.vinDecoder)
    void onVINDecoderClicked()
    {
        Toast.makeText(this, "Not Implemented Yet", Toast.LENGTH_SHORT)
             .show();
        //TODO
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
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
