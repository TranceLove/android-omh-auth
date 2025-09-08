plugins {
    `android-base-lib`
}

android {
    namespace = "com.openmobilehub.android.auth.plugin.box.mobileweb"

    viewBinding {
        enable = true
    }

    buildFeatures {
        buildConfig = true
    }    

    defaultConfig {
        buildConfigField(
            type = "String",
            name = "BOX_AUTH_URL",
            value = getPropertyOrFail("boxAuthUrl")
        )
        buildConfigField(
            type = "String",
            name = "BOX_API_URL",
            value = getPropertyOrFail("boxApiUrl")
        )
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    sourceSets {
        getByName("test").java.srcDir("../../testShared/src/test/java")
    }
}

val useLocalProjects = project.rootProject.extra["useLocalProjects"] as Boolean

dependencies {
    if (useLocalProjects) {
        api(project(":packages:core"))
        implementation(project(":packages:core-common-mobileweb"))
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
    testImplementation(Libs.androidSecurity)
}