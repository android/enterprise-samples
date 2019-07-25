/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.nfcprovisioning.tests;

import android.test.ActivityInstrumentationTestCase2;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.example.android.nfcprovisioning.MainActivity;
import com.example.android.nfcprovisioning.NfcProvisioningFragment;
import com.example.android.nfcprovisioning.R;

/**
 * Tests for NfcProvisioning sample.
 */
public class SampleTests extends ActivityInstrumentationTestCase2<MainActivity> {

    private MainActivity mTestActivity;
    private NfcProvisioningFragment mTestFragment;

    public SampleTests() {
        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        // Starts the activity under test using the default Intent with:
        // action = {@link Intent#ACTION_MAIN}
        // flags = {@link Intent#FLAG_ACTIVITY_NEW_TASK}
        // All other fields are null or empty.
        mTestActivity = getActivity();
        mTestFragment = (NfcProvisioningFragment)
                mTestActivity.getSupportFragmentManager().getFragments().get(1);
    }

    /**
     * Test if the test fixture has been set up correctly.
     */
    public void testPreconditions() {
        //Try to add a message to add context to your assertions. These messages will be shown if
        //a tests fails and make it easy to understand why a test failed
        assertNotNull("mTestActivity is null", mTestActivity);
        assertNotNull("mTestFragment is null", mTestFragment);
    }

    public void testEditTexts() {
        View view = mTestFragment.getView();
        assertNotNull(view);
        // Check that we have all the EditTexts on the Fragment
        EditText locale = (EditText) view.findViewById(R.id.locale);
        assertNotNull(locale);
        EditText timezone = (EditText) view.findViewById(R.id.timezone);
        assertNotNull(timezone);
        EditText wifiSsid = (EditText) view.findViewById(R.id.wifi_ssid);
        assertNotNull(wifiSsid);
        EditText wifiPassword = (EditText) view.findViewById(R.id.wifi_password);
        assertNotNull(wifiPassword);
        // These EditTexts should be filled with some default values
        assertFalse(TextUtils.isEmpty(locale.getText().toString()));
        assertFalse(TextUtils.isEmpty(timezone.getText().toString()));
    }

}
