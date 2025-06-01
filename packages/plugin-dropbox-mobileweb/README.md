# Module plugin-dropbox-mobileweb

Dropbox Implementation of OMH Authentication API using Chrome webview.

## Set up your Dropbox application

Setup the Dropbox application the same as mentioned in original Dropbox plugin.

Next, you need to setup a Redirect URI. You can use Custom URI as most Android apps would do; however for better user experience you may consider Google's recommended [verified app links](https://developer.android.com/training/app-links/verify-android-applinks) method.

## Edit Your Resources and Manifest

At your strings.xml, override these 3 keys as necessary

- `com.openmobilehub.android.auth.dropbox.oauth2.redirect.scheme`
- `com.openmobilehub.android.auth.dropbox.oauth2.redirect.host`
- `com.openmobilehub.android.auth.dropbox.oauth2.redirect.pathPrefix`

These are required for preparing the callback URL at the login intent. Just split your expected callback URI as necessary.

Following plugin-google-nongms's behaviour, the redirect activity is declared within the plugin itself, and you don't need to add it explicitly.

## No Escape Hatch available

This plugin does not provides an escape hatch to access the native Dropbox SDK, as it uses REST API instead.