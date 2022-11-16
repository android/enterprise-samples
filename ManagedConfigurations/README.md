
Android Managed Configuration Sample
====================================

A basic app showing how to allow a device administrator to configurate 
an application using the Android Device Administration API. 
The app exports a managed configuration that enables or disables a UI control. Device Administration applications are able to enforce a specific value for this policy, as directed by enterprise administrators.

Introduction
------------

Managed configurations, previously known as application restrictions, allow the organization's IT admin to remotely specify settings for apps. This capability is particularly useful for organization-approved apps deployed to a work profile.

For example, an organization might require that approved apps allow the IT admin to:

* Allow or block URLs for a web browser
* Configure whether an app is allowed to sync content via cellular, or just by Wi-Fi
* Configure the app's email settings

You can find additional information about Managed configurations in the [Set up managed configurations][1] article.

* Screenshots

   <img src="screenshots/main.png" height="400" alt="Screenshot"/> 


[1]: https://developer.android.com/work/managed-configurations

Application Feedback
--------------------

The sample application uses the Jetpack Enterprise's app feedback API to send a messages back to the EMM when a new configuration is received for a couple of the available options.  

Testing
-------

The application can be tested locally using the TestDPC application available on the [Play Store][2] and on [github][3].

You can follow he guide on github to understand how to setup TestDPC on a device and how to update the manage configuration of this demo application when installed in an a managed profile. We're providing here a couple of animations to show this process:

* Setup TestDPC

  <img src="screenshots/setup_testdpc.gif" height="400" alt="Setup TestDPC"/> 

* Update Managed Configurations

  <img src="screenshots/update_managed_configurations.gif" height="400" alt="Update Managed Configurations"/> 

* Receive App Feedback

  <img src="screenshots/app_feedback.gif" height="400" alt="Receive App Feedback"/> 

[2]: https://play.google.com/store/apps/details?id=com.afwsamples.testdpc
[3]: https://github.com/googlesamples/android-testdpc

Pre-requisites
--------------

- Android Studio Dolphin | 2021.3.1

Getting Started
---------------

This sample uses the Gradle build system. To build this project, use the
"gradlew build" command or use "Import Project" in Android Studio.

Support
-------

- Stack Overflow: http://stackoverflow.com/questions/tagged/android

If you've found an error in this sample, please file an issue:
https://github.com/android/enterprise-samples/issues

Patches are encouraged, and may be submitted by forking this project and
submitting a pull request through GitHub. Please see CONTRIBUTING.md for more details.
