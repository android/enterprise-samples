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
package com.example.android.managedconfigurations

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.RestrictionEntry
import android.content.RestrictionsManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.android.managedconfigurations.databinding.FragmentManagedConfigurationsBinding
import com.example.android.common.logger.Log
import java.lang.StringBuilder

/**
 * Pressing the button on this fragment pops up a simple Toast message. The button is enabled or
 * disabled according to the restrictions set by device/profile owner. You can use the
 * AppRestrictionEnforcer sample as a profile owner for this.
 */
class ManagedConfigurationsFragment : Fragment(), View.OnClickListener {
    // Message to show when the button is clicked (String restriction)
    private var message: String? = null

    // Observes restriction changes
    private var broadcastReceiver: BroadcastReceiver? = null

    private var _binding: FragmentManagedConfigurationsBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentManagedConfigurationsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.sayHello.setOnClickListener(this)
        if (BUNDLE_SUPPORTED) {
            binding.yourItems.visibility = View.VISIBLE
        } else {
            binding.yourItems.visibility = View.GONE
        }
    }

    override fun onResume() {
        super.onResume()
        resolveRestrictions()
    }

    override fun onStart() {
        super.onStart()
        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                resolveRestrictions()
            }
        }
        requireActivity().registerReceiver(
            broadcastReceiver,
            IntentFilter(Intent.ACTION_APPLICATION_RESTRICTIONS_CHANGED)
        )
    }

    override fun onStop() {
        super.onStop()
        if (broadcastReceiver != null) {
            requireActivity().unregisterReceiver(broadcastReceiver)
            broadcastReceiver = null
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun resolveRestrictions() {
        val manager =
            requireActivity().getSystemService(Context.RESTRICTIONS_SERVICE) as RestrictionsManager
        val restrictions = manager.applicationRestrictions
        val entries = manager.getManifestRestrictions(
            requireActivity().applicationContext.packageName
        )

        for (entry in entries) {
            val key = entry.key
            Log.d(TAG, "key: $key")
            when (key) {
                KEY_CAN_SAY_HELLO -> {
                    updateCanSayHello(entry, restrictions)
                }
                KEY_MESSAGE -> {
                    updateMessage(entry, restrictions)
                }
                KEY_NUMBER -> {
                    updateNumber(entry, restrictions)
                }
                KEY_RANK -> {
                    updateRank(entry, restrictions)
                }
                KEY_APPROVALS -> {
                    updateApprovals(entry, restrictions)
                }
                KEY_ITEMS -> {
                    updateItems(restrictions)
                }
            }
        }
    }

    private fun updateCanSayHello(entry: RestrictionEntry, restrictions: Bundle?) {
        val canSayHello =
            if (restrictions == null || !restrictions.containsKey(KEY_CAN_SAY_HELLO)) {
                entry.selectedState
            } else {
                restrictions.getBoolean(KEY_CAN_SAY_HELLO)
            }
        activity?.enterpriseFeedback(KEY_CAN_SAY_HELLO, "Value is $canSayHello", "$canSayHello")
        binding.sayHello.setText(
            if (canSayHello) {
                R.string.explanation_can_say_hello_true
            } else {
                R.string.explanation_can_say_hello_false
            }
        )
        binding.sayHello.isEnabled = canSayHello
    }

    private fun updateMessage(entry: RestrictionEntry, restrictions: Bundle?) {
        message =
            if (restrictions == null || !restrictions.containsKey(KEY_MESSAGE)) {
                entry.selectedString
            } else {
                restrictions.getString(KEY_MESSAGE)
            }
        activity?.enterpriseFeedback(KEY_MESSAGE, "Value is $message", "$message")
    }

    private fun updateNumber(entry: RestrictionEntry, restrictions: Bundle?) {
        val number = if (restrictions == null || !restrictions.containsKey(KEY_NUMBER)) {
            entry.intValue
        } else {
            restrictions.getInt(KEY_NUMBER)
        }
        binding.yourNumber.text = getString(R.string.your_number, number)
    }

    private fun updateRank(entry: RestrictionEntry, restrictions: Bundle?) {
        val rank = if (restrictions == null || !restrictions.containsKey(KEY_RANK)) {
            entry.selectedString
        } else {
            restrictions.getString(KEY_RANK)
        }
        binding.yourRank.text = getString(R.string.your_rank, rank)
    }

    private fun updateApprovals(entry: RestrictionEntry, restrictions: Bundle?) {
        val approvals = if (restrictions == null || !restrictions.containsKey(KEY_APPROVALS)) {
            entry.allSelectedStrings
        } else {
            restrictions.getStringArray(KEY_APPROVALS)
        }
        val text: String = if (approvals.isNullOrEmpty()) {
            getString(R.string.none)
        } else {
            approvals.joinToString(", ")
        }
        binding.approvalsYouHave.text = getString(R.string.approvals_you_have, text)
    }

    private fun updateItems(restrictions: Bundle?) {
        if (!BUNDLE_SUPPORTED) {
            return
        }
        val builder = StringBuilder()
        if (restrictions != null) {
            val parcelables = restrictions.getParcelableArray(KEY_ITEMS)
            if (!parcelables.isNullOrEmpty()) {
                val items = arrayOfNulls<Bundle>(parcelables.size)
                for (i in parcelables.indices) {
                    items[i] = parcelables[i] as Bundle
                }
                var first = true
                for (item in items) {
                    if (!item!!.containsKey(KEY_ITEM_KEY) || !item.containsKey(KEY_ITEM_VALUE)) {
                        continue
                    }
                    if (first) {
                        first = false
                    } else {
                        builder.append(", ")
                    }
                    builder.append(item.getString(KEY_ITEM_KEY))
                    builder.append(":")
                    builder.append(item.getString(KEY_ITEM_VALUE))
                }
            } else {
                builder.append(getString(R.string.none))
            }
        } else {
            builder.append(getString(R.string.none))
        }
        binding.yourItems.text = getString(R.string.your_items, builder)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.say_hello -> {
                Toast.makeText(
                    activity,
                    getString(R.string.message, message),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    companion object {
        // Tag for the logger
        private const val TAG = "ManagedConfigurations"
        private const val KEY_CAN_SAY_HELLO = "can_say_hello"
        private const val KEY_MESSAGE = "message"
        private const val KEY_NUMBER = "number"
        private const val KEY_RANK = "rank"
        private const val KEY_APPROVALS = "approvals"
        private const val KEY_ITEMS = "items"
        private const val KEY_ITEM_KEY = "key"
        private const val KEY_ITEM_VALUE = "value"
        private val BUNDLE_SUPPORTED = Build.VERSION.SDK_INT >= 23
    }
}
