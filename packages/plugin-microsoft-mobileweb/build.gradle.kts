plugins {
    `android-base-lib`
}

android {
    namespace = "com.openmobilehub.android.auth.plugin.microsoft.mobileweb"

    viewBinding {
        enable = true
    }

    defaultConfig {
        buildConfigField(
            type = "String",
            name = "MICROSOFT_AUTH_URL",
            value = getPropertyOrFail("microsoftAuthUrl")
        )
        buildConfigField(
            type = "String",
            name = "MSGRAPH_URL",
            value = getPropertyOrFail("msGraphUrl")
        )
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        buildConfig = true
    }

    sourceSets {
        getByName("test").java.srcDir("../../testShared/src/test/java")
    }
}

val useLocalProjects = project.rootProject.extra["useLocalProjects"] as Boolean

dependencies {
    if (useLocalProjects) {
        api(project(":packages:core"))
        implementation(project(":packages:plugin-common-mobileweb"))
    } else {
        api(Libs.omhAuthCore)
        implementation(Libs.omhCommonMobileWeb)
    }

    // KTX
    implementation(Libs.coreKtx)
    implementation(Libs.lifecycleKtx)
    implementation(Libs.viewModelKtx)
    implementation(Libs.activityKtx)


    // Retrofit setup
    implementation(Libs.retrofit)
    implementation(Libs.retrofitJacksonConverter)
    implementation(Libs.okHttp)
    implementation(Libs.okHttpLoggingInterceptor)

    // Coroutines
    implementation(Libs.coroutinesCore)
    implementation(Libs.coroutinesAndroid)

    // Custom tabs
    implementation(Libs.customTabs)

    implementation(Libs.androidAppCompat)
    implementation(Libs.material)

    // Test dependencies
    testImplementation(Libs.junit)
    testImplementation(Libs.androidJunit)
    testImplementation(Libs.androidXTestRunner)
    testImplementation(Libs.robolectric)
    testImplementation(Libs.mockk)
    testImplementation(Libs.coroutineTesting)
}