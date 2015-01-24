// This file is protected under the KILLGPL.
// For more information, visit http://www.lukeleber.github.io/KILLGPL.html
//
// Copyright (c) Luke Leber <LukeLeber@gmail.com>

package com.lukeleber.scandroid.interpreter;

public interface ResponseListener<T>
{
    void onSuccess(T message);

    void onFailure(FailureCode code);
}
