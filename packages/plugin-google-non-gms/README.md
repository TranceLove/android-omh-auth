# Module plugin-google-non-gms

This plugin basically follows the same setup as Google GMS plugin. You can find the documentation [here](/packages/plugin-google-gms/README.md).

However, an additional step is required, for the custom URI callback to work without Google Play.

There is a placeholder in AndroidManifest.xml for the custom URI callback, which is used to redirect the user back to the app after the authentication process.

You need to replace the placeholder with the client ID you applied for.

The client ID is used to construct the custom URI callback, which follows the format `com.googleusercontent.apps.<client_id>:/oauth2redirect`. You will need to have code like this in your build file, in order to provide the value of `googleClientIdForRedirect` manifest placeholder:

```kotlin
val googleClientId = getValueFromProperties("GOOGLE_CLIENT_ID")
manifestPlaceholders["googleClientIdForRedirect"] = googleClientId.let {
    val bareId = it.replace(".apps.googleusercontent.com", "")
    "com.googleusercontent.apps.${bareId}"
}
```

You can take reference from the auth-sample app's `build.gradle.kts` for the implementation.

## Escape Hatch

This plugin does not provides an escape hatch to access the native Google Android SDK, as it uses REST API instead.
