// This file is protected under the KILLGPL.
// For more information, visit http://www.lukeleber.github.io/KILLGPL.html
//
// Copyright (c) Luke Leber <LukeLeber@gmail.com>
//
// "This software is provided freely under a single condition.  You can't force anyone
// that uses this software to release any source code.  It is their decision alone.
// Other than that, anything goes.
//
// PS. If you like this work, please hire me!  I'm uneducated (no CS degree)
// and as such it's borderline impossible to get into the industry.  My current
// career is absolute hell and I am trapped in it.  I leave this message here
// in the vain hope that someone, somewhere might read this and give me the
// opportunity to join their association as a programmer."
//
// Cheers,
// Luke

/**
 * Artifacts modeled after the information contained in SAE-J1979 ("E/E Diagnostic Test Modes")
 * can be found within this package.  Two types of terminology are provided by the artifacts of
 * this package:
 * <ol>
 *     <li>Standard terminology (per SAE-J1979)</li>
 *     <li>"User Friendly" terminology (no abbreviations and/or acronyms)</li>
 * </ol>
 *
 * <p>Standard terminology is provided by the {@link java.lang.Object#toString()} method, whereas
 * the user-friendly terminology is provided by the
 * {@link com.lukeleber.scandroid.util.UserFriendlyToString#toUserFriendlyString()} method.</p>
 */
package com.lukeleber.scandroid.sae.j1979;