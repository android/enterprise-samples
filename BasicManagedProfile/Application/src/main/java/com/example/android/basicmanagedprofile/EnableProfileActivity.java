/*
 * Copyright (C) 2014 The Android Open Source Project
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

package com.example.android.basicmanagedprofile;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

/**
 * This activity is started after the provisioning is complete in {@link BasicDeviceAdminReceiver}.
 */
public class EnableProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final PostProvisioningHelper helper = new PostProvisioningHelper(this);
        if (!helper.isDone()) {
            // Important: After the profile has been created, the MDM must enable it for corporate
            // apps to become visible in the launcher.
            helper.completeProvisioning();
        }

        // This is just a friendly shortcut to the main screen.
        setContentView(R.layout.enable_profile_activity);
        findViewById(R.id.icon).setOnClickListener((v) -> {
            // Opens up the main screen
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });
    }
}
