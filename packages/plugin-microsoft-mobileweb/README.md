# Module plugin-microsoft-mobileweb

Microsoft Implementation of OMH Authentication API using Chrome webview.

## Set up your Azure application

Setup the Azure application the same as mentioned in original Microsoft plugin. However, you will be adding a Single-page application instead of Android as the platform.

Next, you need to setup a Redirect URI. Microsoft doesn't support custom URI schemes, so you must use HTTPS as the URI scheme. For better user experience, you may also use Google's recommended [verified app links](https://developer.android.com/training/app-links/verify-android-applinks) method.

## Edit Your Resources and Manifest

At your strings.xml, override these 3 keys as necessary

- `com.openmobilehub.android.auth.microsoft.oauth2.redirect.scheme`
- `com.openmobilehub.android.auth.microsoft.oauth2.redirect.host`
- `com.openmobilehub.android.auth.microsoft.oauth2.redirect.pathPrefix`

These are required for preparing the callback URL at the login intent. Just split your expected callback URI as necessary.

Following plugin-google-nongms's behaviour, the redirect activity is declared within the plugin itself, and you don't need to add it explicitly.

## No Escape Hatch available

This plugin does not provides an escape hatch to access the native Microsoft Graph SDK, as it uses REST API instead.