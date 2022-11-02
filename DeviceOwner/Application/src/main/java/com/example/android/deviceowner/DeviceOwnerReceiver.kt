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
package com.example.android.deviceowner

import android.app.admin.DeviceAdminReceiver
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent

/**
 * Handles events related to device owner.
 */
class DeviceOwnerReceiver : DeviceAdminReceiver() {
    /**
     * Called on the new profile when device owner provisioning has completed. Device owner
     * provisioning is the process of setting up the device so that its main profile is managed by
     * the mobile device management (MDM) application set up as the device owner.
     */
    override fun onProfileProvisioningComplete(context: Context, intent: Intent) {
        // Enable the profile
        val manager = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        val componentName = getComponentName(context)
        manager.setProfileName(componentName, context.getString(R.string.profile_name))
        // Open the main screen
        val launch = Intent(context, MainActivity::class.java)
        launch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(launch)
    }

    companion object {
        /**
         * @return A newly instantiated [android.content.ComponentName] for this
         * DeviceAdminReceiver.
         */
        @JvmStatic
        fun getComponentName(context: Context): ComponentName {
            return ComponentName(context.applicationContext, DeviceOwnerReceiver::class.java)
        }
    }
}
