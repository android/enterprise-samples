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

import android.app.Activity
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager.ResolveInfoFlags
import android.content.pm.ResolveInfo
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.SimpleAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import com.example.android.deviceowner.DeviceOwnerReceiver.Companion.getComponentName
import com.example.android.deviceowner.databinding.FragmentDeviceOwnerBinding
import java.util.ArrayList
import java.util.HashMap

/**
 * Demonstrates the usage of the most common device management APIs for the device owner case.
 * In addition to various features available for profile owners, device owners can perform extra
 * actions, such as configuring global settings and enforcing a preferred Activity for a specific
 * IntentFilter.
 */
class DeviceOwnerFragment : Fragment() {
    private var devicePolicyManager: DevicePolicyManager? = null

    // Adapter for the spinner to show list of available launchers
    private lateinit var adapter: LauncherAdapter

    private var _binding: FragmentDeviceOwnerBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    /**
     * Handles events on the Switches.
     */
    private val mOnCheckedChangeListener =
        CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            when (buttonView.id) {
                R.id.switch_auto_time -> {
                    setBooleanGlobalSetting(Settings.Global.AUTO_TIME, isChecked)
                    retrieveCurrentSettings(requireActivity())
                }
                R.id.switch_auto_time_zone -> {
                    setBooleanGlobalSetting(Settings.Global.AUTO_TIME_ZONE, isChecked)
                    retrieveCurrentSettings(requireActivity())
                }
            }
        }

    /**
     * Handles click events on the Button.
     */
    private val mOnClickListener = View.OnClickListener { v ->
        when (v.id) {
            R.id.button_set_preferred_launcher -> {
                if (loadPersistentPreferredLauncher(activity) == null) {
                    setPreferredLauncher()
                } else {
                    clearPreferredLauncher()
                }
                retrieveCurrentSettings(requireActivity())
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDeviceOwnerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Bind event handlers
        binding.switchAutoTime.setOnCheckedChangeListener(mOnCheckedChangeListener)
        binding.switchAutoTimeZone.setOnCheckedChangeListener(mOnCheckedChangeListener)
        binding.buttonSetPreferredLauncher.setOnClickListener(mOnClickListener)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        devicePolicyManager =
            context.getSystemService(Activity.DEVICE_POLICY_SERVICE) as DevicePolicyManager
    }

    override fun onDetach() {
        devicePolicyManager = null
        super.onDetach()
    }

    override fun onResume() {
        super.onResume()
        retrieveCurrentSettings(requireActivity())
    }

    /**
     * Retrieves the current global settings and changes the UI accordingly.
     *
     * @param activity The activity
     */
    private fun retrieveCurrentSettings(activity: Activity) {
        // Global settings
        setCheckedSafely(
            binding.switchAutoTime,
            getBooleanGlobalSetting(activity.contentResolver, Settings.Global.AUTO_TIME)
        )
        setCheckedSafely(
            binding.switchAutoTimeZone,
            getBooleanGlobalSetting(
                activity.contentResolver,
                Settings.Global.AUTO_TIME_ZONE
            )
        )

        // Launcher
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)
        val list = if (Build.VERSION.SDK_INT > 32) {
            activity.packageManager.queryIntentActivities(intent, ResolveInfoFlags.of(0L))
        } else {
            @Suppress("DEPRECATION")
            activity.packageManager.queryIntentActivities(intent, 0)
        }
        adapter = LauncherAdapter(activity, list)
        binding.availableLaunchers.adapter = adapter
        val packageName = loadPersistentPreferredLauncher(activity)
        if (packageName == null) { // No preferred launcher is set
            binding.availableLaunchers.isEnabled = true
            binding.buttonSetPreferredLauncher.setText(R.string.set_as_preferred)
        } else {
            var position = -1
            for (i in list.indices) {
                if (list[i].activityInfo.packageName == packageName) {
                    position = i
                    break
                }
            }
            if (position != -1) {
                binding.availableLaunchers.setSelection(position)
                binding.availableLaunchers.isEnabled = false
                binding.buttonSetPreferredLauncher.setText(R.string.clear_preferred)
            }
        }
    }

    /**
     * Sets the boolean value of the specified global setting.
     *
     * @param setting The setting to be set
     * @param value   The value to be set
     */
    private fun setBooleanGlobalSetting(setting: String, value: Boolean) {
        devicePolicyManager?.setGlobalSetting(
            // The ComponentName of the device owner
            getComponentName(requireActivity()),
            // The settings to be set
            setting,
            // The value we write here is a string representation for SQLite
            if (value) "1" else "0"
        )
    }

    /**
     * A utility method to set the checked state of the button without invoking its listener.
     *
     * @param button  The button
     * @param checked The value to be set
     */
    private fun setCheckedSafely(button: CompoundButton, checked: Boolean) {
        button.setOnCheckedChangeListener(null)
        button.isChecked = checked
        button.setOnCheckedChangeListener(mOnCheckedChangeListener)
    }

    /**
     * Sets the selected launcher as preferred.
     */
    private fun setPreferredLauncher() {
        val activity = activity ?: return
        val filter = IntentFilter(Intent.ACTION_MAIN)
        filter.addCategory(Intent.CATEGORY_HOME)
        filter.addCategory(Intent.CATEGORY_DEFAULT)
        val componentName = adapter.getComponentName(
            binding.availableLaunchers.selectedItemPosition
        )
        devicePolicyManager!!.addPersistentPreferredActivity(
            getComponentName(activity),
            filter,
            componentName
        )
        savePersistentPreferredLauncher(activity, componentName.packageName)
    }

    /**
     * Clears the launcher currently set as preferred.
     */
    private fun clearPreferredLauncher() {
        val activity = activity ?: return
        devicePolicyManager!!.clearPackagePersistentPreferredActivities(
            getComponentName(activity),
            loadPersistentPreferredLauncher(activity)
        )
        savePersistentPreferredLauncher(activity, null)
    }

    /**
     * Shows list of [ResolveInfo] in a [Spinner].
     */
    private class LauncherAdapter(context: Context?, list: List<ResolveInfo>) : SimpleAdapter(
        context,
        createData(list),
        android.R.layout.simple_list_item_1,
        arrayOf(
            KEY_PACKAGE_NAME
        ),
        intArrayOf(android.R.id.text1)
    ) {
        fun getComponentName(position: Int): ComponentName {
            @Suppress("UNCHECKED_CAST")
            val map = getItem(position) as HashMap<String, String>
            return ComponentName(map[KEY_PACKAGE_NAME]!!, map[KEY_ACTIVITY_NAME]!!)
        }

        companion object {
            private const val KEY_PACKAGE_NAME = "package_name"
            private const val KEY_ACTIVITY_NAME = "activity_name"
            private fun createData(list: List<ResolveInfo>): List<HashMap<String, String>> {
                val data: MutableList<HashMap<String, String>> = ArrayList()
                for (info in list) {
                    val map = HashMap<String, String>()
                    map[KEY_PACKAGE_NAME] = info.activityInfo.packageName
                    map[KEY_ACTIVITY_NAME] = info.activityInfo.name
                    data.add(map)
                }
                return data
            }
        }
    }

    companion object {
        // Keys for SharedPreferences
        private const val PREFS_DEVICE_OWNER = "DeviceOwnerFragment"
        private const val PREF_LAUNCHER = "launcher"

        /**
         * @return A newly instantiated [DeviceOwnerFragment].
         */
        fun newInstance(): DeviceOwnerFragment {
            return DeviceOwnerFragment()
        }

        /**
         * Retrieves the current boolean value of the specified global setting.
         *
         * @param resolver The ContentResolver
         * @param setting  The setting to be retrieved
         * @return The current boolean value
         */
        private fun getBooleanGlobalSetting(resolver: ContentResolver, setting: String): Boolean {
            return 0 != Settings.Global.getInt(resolver, setting, 0)
        }

        /**
         * Loads the package name from SharedPreferences.
         *
         * @param activity The activity
         * @return The package name of the launcher currently set as preferred, or null if there is no
         * preferred launcher.
         */
        private fun loadPersistentPreferredLauncher(activity: Activity?): String? {
            return activity
                ?.getSharedPreferences(PREFS_DEVICE_OWNER, Context.MODE_PRIVATE)
                ?.getString(PREF_LAUNCHER, null)
        }

        /**
         * Saves the package name into SharedPreferences.
         *
         * @param activity    The activity
         * @param packageName The package name to be saved. Pass null to remove the preferred launcher.
         */
        private fun savePersistentPreferredLauncher(activity: Activity, packageName: String?) {
            val editor = activity.getSharedPreferences(
                PREFS_DEVICE_OWNER,
                Context.MODE_PRIVATE
            ).edit()
            if (packageName == null) {
                editor.remove(PREF_LAUNCHER)
            } else {
                editor.putString(PREF_LAUNCHER, packageName)
            }
            editor.apply()
        }
    }
}
