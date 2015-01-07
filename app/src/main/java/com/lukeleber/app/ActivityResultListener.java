/*
 * This file is protected under the KILLGPL.
 * For more information visit <insert_valid_link_to_killgpl_here>
 * <p/>
 * Copyright (c) Luke A. Leber <LukeLeber@gmail.com> 2014
 */

package com.lukeleber.app;

import android.content.Intent;

/**
 * A callback function object for handling the result of a call to {@link
 * android.app.Activity#startActivityForResult}.  Borrowing from patterns found in Swing, this
 * allows for a concise, scalable solution to a very common problem.
 */
public interface ActivityResultListener
{
    /**
     * Fired when an {@link android.app.Activity} returns a result to its caller
     *
     * @param resultCode
     *         The integer result code returned by the child activity through its setResult().
     * @param data
     *         An Intent, which can return result data to the caller (various data can be attached
     *         to Intent "extras").
     */
    void onActivityResult(int resultCode, Intent data);
}
