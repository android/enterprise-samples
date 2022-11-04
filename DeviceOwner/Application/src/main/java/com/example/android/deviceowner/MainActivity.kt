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

import android.app.admin.DevicePolicyManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.android.deviceowner.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (savedInstanceState == null) {
            val manager = getSystemService(DEVICE_POLICY_SERVICE) as DevicePolicyManager
            if (manager.isDeviceOwnerApp(applicationContext.packageName)) {
                // This app is set up as the device owner. Show the main features.
                Log.d(TAG, "The app is the device owner.")
                showFragment(DeviceOwnerFragment.newInstance())
            } else {
                // This app is not set up as the device owner. Show instructions.
                Log.d(TAG, "The app is not the device owner.")
                showFragment(InstructionFragment.newInstance())
            }
        }
    }

    private fun showFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment)
            .commit()
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}
