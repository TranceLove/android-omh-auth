# Module plugin-box-mobileweb

Box.com Implementation of OMH Authentication API using AndroidX Browser CustomTab.

## Quirks

Some box.com's API with regards to authentication and user profile require Client Secret as part of request parameters. To preserve the original implementation's method signatures separate route to inject Client Secret to components is necessary.

## Required OAuth2 Scopes

None. By default it doesn't ask for any scopes - please remember to add them on your own. At the minimum, you need to add `root_readonly` or `root_readwrite` scope to your application.

## Required Configurations

You need to setup a Redirect URI. You can use Custom URI as most Android apps would do; however for better user experience you may consider Google's recommended [verified app links](https://developer.android.com/training/app-links/verify-android-applinks) method.

## Edit Your Resources and Manifest

At your strings.xml, override these 3 keys as necessary

- `com.openmobilehub.android.auth.box.oauth2.redirect.scheme`
- `com.openmobilehub.android.auth.box.oauth2.redirect.host`
- `com.openmobilehub.android.auth.box.oauth2.redirect.pathPrefix`

## Using This Library

Jot down your App's Client ID and Client Secret, then use `BoxMobileWebAuthClient.Builder` to create the `OmhAuthClient` instance. Assuming you put the client ID at `BuildConfig.BOX_CLIENT_ID` and `BuildConfig.BOX_CLIENT_SECRET`, this is how it looks like to create the `OmhAuthClient`:

```kotlin
val client = com.openmobilehub.android.auth.plugin.box.mobileweb.presentation.BoxMobileWebAuthClient.Builder(
    clientId = BuildConfig.BOX_CLIENT_ID,
    clientSecret = BuildConfig.BOX_CLIENT_SECRET,
).also { builder ->
    arrayListOf("root_readonly", "root_readwrite").forEach { scope ->
        builder.addScope(scope)
    }
}.build(context)
```

Following plugin-google-nongms's behaviour, the redirect activity is declared within the plugin itself, and you don't need to add it explicitly.

## No Escape Hatch available

This plugin does not provides an escape hatch to access the native Box SDK, as it uses REST API instead.