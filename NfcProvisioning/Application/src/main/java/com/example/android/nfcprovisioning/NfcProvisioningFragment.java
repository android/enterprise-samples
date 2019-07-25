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

package com.example.android.nfcprovisioning;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

/**
 * Provides UI and logic for NFC provisioning.
 * <p>
 * This fragment creates an intent, which sends parameters to a second device via an Nfc bump. If
 * the second device is factory reset, this will start provisioning the second device to set it up
 * as an owned device.
 * </p>
 */
public class NfcProvisioningFragment extends Fragment implements
        NfcAdapter.CreateNdefMessageCallback,
        TextWatcherWrapper.OnTextChangedListener,
        LoaderManager.LoaderCallbacks<Map<String, String>> {

    private static final int LOADER_PROVISIONING_VALUES = 1;

    // View references
    private EditText mEditPackageName;
    private EditText mEditClassName;
    private EditText mEditLocale;
    private EditText mEditTimezone;
    private EditText mEditWifiSsid;
    private EditText mEditWifiSecurityType;
    private EditText mEditWifiPassword;

    // Values to be set via NFC bump
    private Map<String, String> mProvisioningValues;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_nfc_provisioning, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        // Retrieve view references
        mEditPackageName = (EditText) view.findViewById(R.id.package_name);
        mEditClassName = (EditText) view.findViewById(R.id.class_name);
        mEditLocale = (EditText) view.findViewById(R.id.locale);
        mEditTimezone = (EditText) view.findViewById(R.id.timezone);
        mEditWifiSsid = (EditText) view.findViewById(R.id.wifi_ssid);
        mEditWifiSecurityType = (EditText) view.findViewById(R.id.wifi_security_type);
        mEditWifiPassword = (EditText) view.findViewById(R.id.wifi_password);
        // Bind event handlers
        mEditPackageName.addTextChangedListener(new TextWatcherWrapper(R.id.package_name, this));
        mEditClassName.addTextChangedListener(new TextWatcherWrapper(R.id.class_name, this));
        mEditLocale.addTextChangedListener(new TextWatcherWrapper(R.id.locale, this));
        mEditTimezone.addTextChangedListener(new TextWatcherWrapper(R.id.timezone, this));
        mEditWifiSsid.addTextChangedListener(new TextWatcherWrapper(R.id.wifi_ssid, this));
        mEditWifiSecurityType.addTextChangedListener(
                new TextWatcherWrapper(R.id.wifi_security_type, this));
        mEditWifiPassword.addTextChangedListener(new TextWatcherWrapper(R.id.wifi_password, this));
        // Prior to API 23, the class name is not needed
        mEditClassName.setVisibility(Build.VERSION.SDK_INT >= 23 ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onStart() {
        super.onStart();
        Activity activity = getActivity();
        NfcAdapter adapter = NfcAdapter.getDefaultAdapter(activity);
        if (adapter != null) {
            adapter.setNdefPushMessageCallback(this, activity);
        }
        getLoaderManager().initLoader(LOADER_PROVISIONING_VALUES, null, this);
    }

    @Override
    public NdefMessage createNdefMessage(NfcEvent event) {
        if (mProvisioningValues == null) {
            return null;
        }
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        Properties properties = new Properties();
        // Store all the values into the Properties object
        for (Map.Entry<String, String> e : mProvisioningValues.entrySet()) {
            if (!TextUtils.isEmpty(e.getValue())) {
                String value;
                if (e.getKey().equals(DevicePolicyManager.EXTRA_PROVISIONING_WIFI_SSID)) {
                    // Make sure to surround SSID with double quotes
                    value = e.getValue();
                    if (!value.startsWith("\"") || !value.endsWith("\"")) {
                        value = "\"" + value + "\"";
                    }
                } else //noinspection deprecation
                    if (e.getKey().equals(
                            DevicePolicyManager.EXTRA_PROVISIONING_DEVICE_ADMIN_PACKAGE_NAME)
                            && Build.VERSION.SDK_INT >= 23) {
                        continue;
                    } else {
                        value = e.getValue();
                    }
                properties.put(e.getKey(), value);
            }
        }
        // Make sure to put local time in the properties. This is necessary on some devices to
        // reliably download the device owner APK from an HTTPS connection.
        if (!properties.contains(DevicePolicyManager.EXTRA_PROVISIONING_LOCAL_TIME)) {
            properties.put(DevicePolicyManager.EXTRA_PROVISIONING_LOCAL_TIME,
                    String.valueOf(System.currentTimeMillis()));
        }
        try {
            properties.store(stream, getString(R.string.nfc_comment));
            NdefRecord record = NdefRecord.createMime(
                    DevicePolicyManager.MIME_TYPE_PROVISIONING_NFC, stream.toByteArray());
            return new NdefMessage(new NdefRecord[]{record});
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onTextChanged(int id, String s) {
        if (mProvisioningValues == null) {
            return;
        }
        switch (id) {
            case R.id.package_name:
                //noinspection deprecation
                mProvisioningValues.put(
                        DevicePolicyManager.EXTRA_PROVISIONING_DEVICE_ADMIN_PACKAGE_NAME, s);
                break;
            case R.id.class_name:
                if (Build.VERSION.SDK_INT >= 23) {
                    if (TextUtils.isEmpty(s)) {
                        mProvisioningValues.remove(
                                DevicePolicyManager.EXTRA_PROVISIONING_DEVICE_ADMIN_COMPONENT_NAME);
                    } else {
                        // On API 23 and above, we can use
                        // EXTRA_PROVISIONING_DEVICE_ADMIN_COMPONENT_NAME to specify the receiver
                        // in the device owner app. If the provisioning values contain this key,
                        // EXTRA_PROVISIONING_DEVICE_ADMIN_PACKAGE_NAME is not read.
                        String packageName = mEditPackageName.getText().toString();
                        ComponentName name = new ComponentName(packageName, s);
                        mProvisioningValues.put(
                                DevicePolicyManager.EXTRA_PROVISIONING_DEVICE_ADMIN_COMPONENT_NAME,
                                name.flattenToShortString());
                    }
                }
                break;
            case R.id.locale:
                mProvisioningValues.put(DevicePolicyManager.EXTRA_PROVISIONING_LOCALE, s);
                break;
            case R.id.timezone:
                mProvisioningValues.put(DevicePolicyManager.EXTRA_PROVISIONING_TIME_ZONE, s);
                break;
            case R.id.wifi_ssid:
                mProvisioningValues.put(DevicePolicyManager.EXTRA_PROVISIONING_WIFI_SSID, s);
                break;
            case R.id.wifi_security_type:
                mProvisioningValues.put(
                        DevicePolicyManager.EXTRA_PROVISIONING_WIFI_SECURITY_TYPE, s);
                break;
            case R.id.wifi_password:
                mProvisioningValues.put(DevicePolicyManager.EXTRA_PROVISIONING_WIFI_PASSWORD, s);
                break;
        }
    }

    @Override
    public Loader<Map<String, String>> onCreateLoader(int id, Bundle args) {
        if (id == LOADER_PROVISIONING_VALUES) {
            return new ProvisioningValuesLoader(getActivity());
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Map<String, String>> loader, Map<String, String> values) {
        if (loader.getId() == LOADER_PROVISIONING_VALUES) {
            mProvisioningValues = values;
            //noinspection deprecation
            mEditPackageName.setText(values.get(
                    DevicePolicyManager.EXTRA_PROVISIONING_DEVICE_ADMIN_PACKAGE_NAME));
            if (Build.VERSION.SDK_INT >= 23) {
                ComponentName name = ComponentName.unflattenFromString(values.get(
                        DevicePolicyManager.EXTRA_PROVISIONING_DEVICE_ADMIN_COMPONENT_NAME));
                mEditClassName.setText(name.getClassName());
            }
            mEditLocale.setText(values.get(DevicePolicyManager.EXTRA_PROVISIONING_LOCALE));
            mEditTimezone.setText(values.get(DevicePolicyManager.EXTRA_PROVISIONING_TIME_ZONE));
            mEditWifiSsid.setText(values.get(DevicePolicyManager.EXTRA_PROVISIONING_WIFI_SSID));
            mEditWifiSecurityType.setText(values.get(
                    DevicePolicyManager.EXTRA_PROVISIONING_WIFI_SECURITY_TYPE));
            mEditWifiPassword.setText(values.get(
                    DevicePolicyManager.EXTRA_PROVISIONING_WIFI_PASSWORD));
        }
    }

    @Override
    public void onLoaderReset(Loader<Map<String, String>> loader) {
        // Do nothing
    }

}
