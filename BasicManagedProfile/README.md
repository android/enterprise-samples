
Android BasicManagedProfile Sample
===================================

This sample demonstrates basic functionalities of Managed Profile API
introduced in Android 5.0 Lollipop. You can set up this app as a
profile owner, and use this app to enable/disable apps in the newly
created managed profile. You can also set restrictions to some apps,
enable/disable Intent forwarding between profiles, and wipe out all
the data associated with the profile.

Introduction
------------

As of Android 5.0, DevicePolicyManager introduces new features to
support managed profile.

To set up this app as a profile owner, you need to encrypt your device
(you are prompted to do if you haven't). This doesn't wipe out the
device, but be aware that you can set up only one managed profile at a
time.

[isProfileOwnerApp][1] can be used to determine if a particular
package is registered as the profile owner for the current user. You
can initiate the provisioning flow of a managed profile with Intent of
[ACTION_PROVISION_MANAGED_PROFILE][2].

You have to implement a class extending [DeviceAdminReceiver][3] to
receive the result of the provisioning flow. Use
[setProfileEnabled][4] to enable the newly created profile, and your
app is now set up as a profile owner.

[1]: http://developer.android.com/reference/android/app/admin/DevicePolicyManager.html#isProfileOwnerApp(java.lang.String)
[2]: http://developer.android.com/reference/android/app/admin/DevicePolicyManager.html#ACTION_PROVISION_MANAGED_PROFILE
[3]: http://developer.android.com/reference/android/app/admin/DeviceAdminReceiver.html
[4]: http://developer.android.com/reference/android/app/admin/DevicePolicyManager.html#setProfileEnabled(android.content.ComponentName)

Pre-requisites
--------------

- Android SDK 28
- Android Build Tools v28.0.3
- Android Support Repository

Screenshots
-------------

<img src="screenshots/not_set_up.png" height="400" alt="Screenshot"/> <img src="screenshots/set_up.png" height="400" alt="Screenshot"/> <img src="screenshots/main.png" height="400" alt="Screenshot"/> 

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
