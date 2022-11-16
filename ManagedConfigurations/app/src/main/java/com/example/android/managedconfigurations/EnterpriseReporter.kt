/*
 * Copyright 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.managedconfigurations

import android.content.Context
import android.util.Log
import androidx.enterprise.feedback.KeyedAppState
import androidx.enterprise.feedback.KeyedAppStatesCallback
import androidx.enterprise.feedback.KeyedAppStatesCallback.STATUS_EXCEEDED_BUFFER_ERROR
import androidx.enterprise.feedback.KeyedAppStatesCallback.STATUS_SUCCESS
import androidx.enterprise.feedback.KeyedAppStatesCallback.STATUS_TRANSACTION_TOO_LARGE_ERROR
import androidx.enterprise.feedback.KeyedAppStatesCallback.STATUS_UNKNOWN_ERROR
import androidx.enterprise.feedback.KeyedAppStatesReporter

/**
 * Sends feedback to device management apps.
 */
fun Context.enterpriseFeedback(
    key: String,
    message: String,
    data: String,
    severity: Int = KeyedAppState.SEVERITY_ERROR
) {
    val keyedAppStatesReporter = KeyedAppStatesReporter.create(this)
    val keyedAppStateMessage = KeyedAppState.builder()
        .setSeverity(severity)
        .setKey(key)
        .setMessage(message)
        .setData(data)
        .build()
    val list: MutableList<KeyedAppState> = ArrayList()
    list.add(keyedAppStateMessage)
    keyedAppStatesReporter.setStates(list, Callback())
}

internal class Callback : KeyedAppStatesCallback {
    override fun onResult(state: Int, throwable: Throwable?) {
        when (state) {
            STATUS_SUCCESS ->
                Log.i("ErrorReporter", "KeyedAppStatesCallback status: SUCCESS ")
            STATUS_UNKNOWN_ERROR ->
                Log.i("ErrorReporter", "KeyedAppStatesCallback status: UNKNOWN_ERROR ")
            STATUS_TRANSACTION_TOO_LARGE_ERROR ->
                Log.i("ErrorReporter", "KeyedAppStatesCallback status: TRANSACTION_TOO_LARGE_ERROR ")
            STATUS_EXCEEDED_BUFFER_ERROR ->
                Log.i("ErrorReporter", "KeyedAppStatesCallback status: EXCEEDED_BUFFER_ERROR ")
            else ->
                Log.i("ErrorReporter", "KeyedAppStatesCallback status: $state ")
        }
    }
}
