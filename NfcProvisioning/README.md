
Android NfcProvisioning Sample
===================================

This sample demonstrates how to use NFC to provision a new device with a device owner. Device owner
is a specialized type of device administrator that can control device security and configuration.
This sample itself is not a device owner, but it is a programming app that sends NFC message to an
unprovisioned peer device and tells it to set up the specified device owner app.

This can read either the older name=value form (if found in nfcprovisioning.txt) or
as JSON, identical in form to that needed for QR provisioning (if found in nfcprovisioning.json).
(If both are present, the JSON is used.)

Introduction
------------

NFC Provisioning app is nothing but an ordinary Android Beam app that just sends out provisioning
values to the peer device. This sample uses the values below.

* `EXTRA_PROVISIONING_DEVICE_ADMIN_PACKAGE_NAME` - The package name of the mobile device management
  application that will be set as the profile owner or device owner.
* `EXTRA_PROVISIONING_LOCALE` - The Locale that the device will be set to.
* `EXTRA_PROVISIONING_TIME_ZONE` - The time zone AlarmManager that the device will be set to.
* `EXTRA_PROVISIONING_WIFI_SSID` - The ssid of the wifi network that should be used during nfc
  device owner provisioning for downloading the mobile device management application.
* `EXTRA_PROVISIONING_WIFI_PASSWORD` - The password of the wifi network in
  EXTRA_PROVISIONING_WIFI_SSID.
* `EXTRA_PROVISIONING_WIFI_SECURITY_TYPE` - NONE, WPA, WEP or EAP.
* `EXTRA_PROVISIONING_WIFI_HIDDEN` - true (or not coded)

The file nfcprovisioning.txt (if used) is simple name=value pairs.
See nfcprovisioning.txt in this app for a sample.
Any names that do not begin with `andriod.app.extra` are collected into
`EXTRA_PROVISIONING_ADMIN_EXTRAS_BUNDLE`.

The file nfcprovisioning.json (see the sample) is in JSON format. Since JSON can represent it,
`EXTRA_PROVISIONING_ADMIN_EXTRAS_BUNDLE` should be coded directly, as
seen in the sample. Spaces are OK in this file, TABs are not.

Functional summary:

Store values in an instance of Properties. Get a byte array representation of the Properties using
ByteArrayOutputStream. Create an NdefRecord with the MIME type of
[DevicePolicyManager.MIME_TYPE_PROVISIONING_NFC][1]. Use [NfcAdapter#setNdefPushMessage][2] to set
the NdefMessage as the message to be sent.

[1]: https://developer.android.com/reference/android/app/admin/DevicePolicyManager.html#MIME_TYPE_PROVISIONING_NFC
[2]: http://developer.android.com/reference/android/nfc/NfcAdapter.html#setNdefPushMessage(android.nfc.NdefMessage,%20android.app.Activity,%20android.app.Activity...)

Use and tips
------------
This app runs on a support android device. It simply sends information to the
target, which is the device you wish to provision.

Install this app on any device that has NFC hardware. It doesn't need any
special permissions, since all it does is reformat text and send it via
NFC.  You need to download nfcprovisioning.txt or nfcprovisioning.json
to /scdard/ on the device. (Theoretically you could enter it all from
the screen, but that would be impractical in reality.)

You will need to Factory Reset the device being provisioned to download
a DPC app. Note that Factory Reset can take a long time, and it may
take several minutes of "doing nothing" for it to finish. (Read up
on Factory Reset... it's permanent.)

Once the app is running, bring the devices together (back to back is
easy). Slide them around until this app reduces window size, and then
tap the screen of this device. It may request that you separate
and recontact the devices. It will make a "happy" chirp when things
actually work. Note that protective cases may make it somewhat harder
to make the connection, so it might take a bit more moving of the
devices until connection is made.

Note that some devices being provisioned don't turn on NFC immediately
once the device is rebooted.
One required selecting the language before NFC was enabled.

Pre-requisites
--------------

- Android SDK 28
- Android Build Tools v28.0.3
- Android Support Repository

Screenshots
-------------

<img src="screenshots/1-main.png" height="400" alt="Screenshot"/> 

Getting Started
---------------

This sample uses the Gradle build system. To build this project, use the
"gradlew build" command or use "Import Project" in Android Studio.

Support
-------

- Stack Overflow: http://stackoverflow.com/questions/tagged/android

If you've found an error in this sample, please file an issue:
https://github.com/android/enterprise

Patches are encouraged, and may be submitted by forking this project and
submitting a pull request through GitHub. Please see CONTRIBUTING.md for more details.
