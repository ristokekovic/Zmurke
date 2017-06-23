# Zmurke

Zmurke is an Android app that simulates the popular game Hide & Seek on your Android device! It uses Google Maps and your GPS location to track you and your friends and when they get close to you, they will appear on your map and the hunt can begin! Add your friends via Bluetooth and let the game begin!

Also check out [some of my other projects](https://github.com/Risto995).

## Installation

Download:

    $ git clone https://github.com/Risto995/MyPlaces.git

Import Project by Android Studio Menu > File > Import Project...

Run MyPlaces by Android Studio Menu > Run > Run MyPlaces  
If some issues are happened, try "Sync Project with Gradle Files" or "Rebuild Project" at Android Studio Menu

## Integration with Google Maps API

This app uses built-in services from Google Maps, so it is very important that you do the following steps in order to enable API services on your computer and get permissions to use Google Maps API in your app.

Step 1 : Setup Google Play Services support in Android Studio as follows

Open build.gradle(Module: app) file under Gradle Scripts and add google play service support under dependencies as follows.

    compile 'com.google.android.gms:play-services:8.4.0'

Step 2 : Generate SHA-1 fingerprint certificate key in Android Studio by following the steps given 
              below.
1.Open Android Studio
2.Open Your Project
3.Click on Gradle (From Right Side Panel, you will see Gradle Bar)
4.Click on Refresh (Click on Refresh from Gradle Bar, you will see List Gradle scripts of your Project)
5.Click on Your Project (Your Project Name form List (root))
6.Click on Tasks
7.Click on android
8.Double Click on signingReport (You will get SHA1 and MD5 in Run Bar)

Step 2 : Generate google maps api key by using following steps:
1.Sign In into following web site by using your google account user name and password https://console.developers.google.com
2.Next from top right corner of the web site you can one drop down box from that select create new project option and create new one by providing project name. 
3.Next select Google API's option and next uder google maps apis select Google Maps Android API and enable this api by selecting enable api option.
4.Next select credentials option from left side of the web site.
5.Next under New credentials select API key option next select Android Key and next select Add Package name and fingerprint and in next window provide your application package name SHA-1 certificate fingerprint key from Android Studio after click create button. next maps api  key will be generated next we are going to use this key in side application by using following procudure.

Step 3 : Insert following code into the AndroidManifest.xml file as follows

    <uses-feature
        android:glEsVersion="0x00020000"
        android:required="true" />

    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
    <uses-permission android:name="com.google.android.providers.gsf.permission.READ_GSERVICES" />
    <!--
     The following two permissions are not required to use
     Google Maps Android API v2, but are recommended.
    -->
    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />

    <permission
        android:name="your app package name.permission.MAPS_RECEIVE"
        android:protectionLevel="signature" />

    <uses-permission android:name="your app package name.permission.MAPS_RECEIVE" />

In addition, add the following meta-data inside application tag in Android Manifest:

    <meta-data
            android:name="com.google.android.gms.version"

            android:value="@integer/google_play_services_version" />

        <meta-data
            android:name="com.google.android.maps.v2.API_KEY"
            android:value="place maps api key here" />

