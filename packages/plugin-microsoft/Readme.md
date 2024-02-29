# Microsoft plugin

## Set up your Microsoft application

To access Microsoft APIs, generate a unique **Client ID** and **Keystore Hash** for your app in the Microsoft Azure. Add the **Application ID** and **Keystore Hash** to your app's code and complete the required Microsoft Azure setup steps:

1.  [Go to the Microsoft Azure](https://portal.azure.com/#view/Microsoft_AAD_RegisteredApps/ApplicationsListBlade).
2.  Click on "New registration" to start creating a new Microsoft Azure application.
3.  Once created the application, go to the Authentication and add a new Android platform.
4.  Set your app package name (Use "com.openmobilehub.android.auth.sample.base.DemoApp" if you are following the starter-code).
5.  Generate and set your Signature Hash:

    ### Generating a Development Key Hash

    You'll have a unique development key hash for each Android development environment.

    #### Mac OS

    You will need the Key and Certificate Management Tool (keytool) from the Java Development Kit. To generate a development key hash, open a terminal window and run the following command:

    ```bash
    keytool -exportcert -alias androiddebugkey -keystore ~/.android/debug.keystore | openssl sha1 -binary | openssl base64
    ```

    #### Windows

    You will need the following:

    - Key and Certificate Management Tool (keytool) from the Java Development Kit
    - openssl-for-windows openssl library for Windows from the Google Code Archive

    To generate a development key hash, run the following command in a command prompt in the Java SDK folder:

    ```bash
    keytool -exportcert -alias androiddebugkey -keystore "C:\Users\USERNAME\android\debug.keystore" | "PATH_TO_OPENSSL_LIBRARY\bin\openssl" sha1 -binary | "PATH_TO_OPENSSL_LIBRARY\bin\openssl" base64
    ```

    This command will generate a 28-character key hash unique to your development environment. Copy and paste it into the field below. You will need to provide a development key hash for the development environment of each person who works on your app.

    ### Generating a Release Key Hash

    Android apps must be digitally signed with a release key before you can upload them to the store. To generate a hash of your release key, run the following command on Mac or Windows substituting your release key alias and the path to your keystore:

    ```bash
    keytool -exportcert -alias YOUR_RELEASE_KEY_ALIAS -keystore YOUR_RELEASE_KEY_PATH | openssl sha1 -binary | openssl base64
    ```

    This will generate a 28-character string that you should copy and paste into the field below. Also, see the Android documentation for signing your apps.

    ### Setting the Signature Hash

    Once you generated the debug or the release key hash, add it under **Signature Hash** input and save your changes.

## Configure the AndroidManifest.xml

1. Configure an intent filter in the Android Manifest, using your redirect URI:

```XML
  <activity
    android:name="com.microsoft.identity.client.BrowserTabActivity"
    android:exported="true">
    <intent-filter>
        <action android:name="android.intent.action.VIEW" />

        <category android:name="android.intent.category.DEFAULT" />
        <category android:name="android.intent.category.BROWSABLE" />

        <data
          android:host="<YOUR_PACKAGE_NAME>"
            android:path="@string/microsoft_path"
            android:scheme="msauth" />
    </intent-filter>
  </activity>
```

2. Add a uses-permission element to the manifest after the application element:

```XML
  <uses-permission android:name="android.permission.INTERNET"/>
  <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
```

## Add the Client ID and Keystore Hash to your app

You should not check your Client ID or Keystore Hash into your version control system, so it is recommended storing it in the `local.properties` file, which is located in the root directory of your project. For more information about the `local.properties` file, see [Gradle properties](https://developer.android.com/studio/build#properties-files) [files](https://developer.android.com/studio/build#properties-files).

Open the `local.properties` in your project level directory, and do the following:

- Replace `YOUR_MICROSOFT_CLIENT_ID` with your **CLIENT ID**: `MICROSOFT_CLIENT_ID=YOUR_MICROSOFT_CLIENT_ID`.
- Replace `YOUR_KEYSTORE_HASH` with your **Keystore Hash**: `KEYSTORE_HASH=YOUR_KEYSTORE_HASH`.

## Gradle configuration

To incorporate Microsoft plugin into your project, you have to directly include the Microsoft plugin as a dependency. In the `build.gradle.kts`, add the following implementation statement to the `dependencies{}` section:

```groovy
implementation("com.openmobilehub.android.auth:plugin-microsoft:2.0.0-beta")
```

Save the file and [sync your project with Gradle](https://developer.android.com/studio/build#sync-files).

## Provide the Microsoft OMH Auth Client

In the `SingletonModule.kt` file in the `:auth-starter-sample` module add the following code to provide the Microsoft OMH Auth Client.

```kotlin
@Provides
fun providesMicrosoftAuthClient(@ApplicationContext context: Context): MicrosoftAuthClient {
    return MicrosoftAuthClient(
        configFileResourceId = R.raw.ms_auth_config,
        context = context,
        scopes = arrayListOf("User.Read"),
    )
}
```

> We'd recommend to store the client as a singleton with your preferred dependency injection library as this will be your only gateway to the OMH Auth SDK and it doesn't change in runtime at all.