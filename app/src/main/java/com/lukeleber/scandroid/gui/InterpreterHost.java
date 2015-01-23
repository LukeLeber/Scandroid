/*
 * This file is protected under the KILLGPL.
 * For more information visit <insert_valid_link_to_killgpl_here>
 * <p/>
 * Copyright (c) Luke A. Leber <LukeLeber@gmail.com> 2014
 */

package com.lukeleber.scandroid.gui;

import com.lukeleber.scandroid.interpreter.Interpreter;
import com.lukeleber.scandroid.sae.j1979.Profile;

public interface InterpreterHost
{
    Interpreter<String> getInterpreter();

    Profile getProfile();
}
