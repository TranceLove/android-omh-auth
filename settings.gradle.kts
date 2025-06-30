pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        mavenLocal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_PROJECT)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "OMH Auth"
include(":packages:core")
include(":apps:auth-sample")
include(":packages:plugin-google-gms")
include(":packages:plugin-google-non-gms")
include(":packages:plugin-facebook")
include(":packages:plugin-common-mobileweb")
include(":packages:plugin-microsoft")
include(":packages:plugin-microsoft-mobileweb")
include(":packages:plugin-dropbox")
include(":packages:plugin-dropbox-mobileweb")
include(":packages:plugin-box-mobileweb")