package com.openmobilehub.android.auth.plugin.microsoft.utils

import android.content.Context
import com.google.gson.Gson
import com.microsoft.identity.client.PublicClientApplicationConfiguration
import com.microsoft.identity.client.PublicClientApplicationConfigurationFactory
import java.io.InputStream

/**
 * Singleton to load configuration for initializing MS Graph SDK.
 *
 * Originally MS Graph SDK only allows load configuration by specifying a raw JSON resource bundled
 * into the app. However this loses flexibility that configuration JSON that may not be bundled
 * but available from any other InputStreams, hence this object.
 *
 * Relies heavily on reflection to invoke private static methods inside
 * [PublicClientApplicationConfigurationFactory] so may break in the future, should MS Graph SDK
 * changes its API or the feature request at
 * https://github.com/AzureAD/microsoft-authentication-library-for-android/issues/2201 is
 * eventually solved.
 */
object PublicClientApplicationConfigurationLoader {

    private val initializeConfigurationInternal = PublicClientApplicationConfigurationFactory::class.java
        .getDeclaredMethod(
            "initializeConfigurationInternal",
            Context::class.java,
            PublicClientApplicationConfiguration::class.java
        )

    private val getGsonForLoadingConfiguration = PublicClientApplicationConfigurationFactory::class.java
        .getDeclaredMethod("getGsonForLoadingConfiguration")

    /**
     * Public facing method, load [PublicClientApplicationConfiguration] using given [InputStream].
     *
     * @param context [Context]
     * @param config [InputStream]
     */
    @JvmStatic
    fun createSingleAccountPublicClientApplication(
        context: Context,
        config: InputStream
    ): PublicClientApplicationConfiguration {
        initializeConfigurationInternal.isAccessible = true
        return initializeConfigurationInternal.invoke(
            null,
            context,
            loadPublicClientApplicationConfigurationFrom(config)
        ) as PublicClientApplicationConfiguration
    }

    @JvmStatic
    private fun loadPublicClientApplicationConfigurationFrom(
        source: InputStream
    ): PublicClientApplicationConfiguration {
        getGsonForLoadingConfiguration.isAccessible = true
        val gson = getGsonForLoadingConfiguration.invoke(null) as Gson
        return gson.fromJson(source.reader(), PublicClientApplicationConfiguration::class.java)
    }
}
