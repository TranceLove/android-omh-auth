package com.openmobilehub.android.auth.sample.di

import android.content.Context
import com.openmobilehub.android.auth.core.OmhAuthClient
import com.openmobilehub.android.auth.plugin.dropbox.DropboxAuthClient
import com.openmobilehub.android.auth.plugin.dropbox.mobileweb.presentation.DropboxMobileWebAuthClient
import com.openmobilehub.android.auth.plugin.facebook.FacebookAuthClient
import com.openmobilehub.android.auth.plugin.microsoft.MicrosoftAuthClient
import com.openmobilehub.android.auth.plugin.microsoft.mobileweb.presentation.MicrosoftMobileWebAuthClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ProvidesDropboxMobileWebAuthClient

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ProvidesMicrosoftMobileWebAuthClient

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ProvidesBoxMobileWebAuthClient

@Suppress("LongParameterList", "TooGenericExceptionThrown")
class AuthClientProvider @Inject constructor(
    private val googleAuthClient: OmhAuthClient,
    private val facebookAuthClient: FacebookAuthClient,
    private val microsoftAuthClient: MicrosoftAuthClient,
    @ProvidesMicrosoftMobileWebAuthClient private val microsoftMobileWebAuthClient: OmhAuthClient,
    private val dropboxAuthClient: DropboxAuthClient,
    @ProvidesDropboxMobileWebAuthClient private val dropboxMobileWebAuthClient: OmhAuthClient,
    @ProvidesBoxMobileWebAuthClient private val boxMobileWebAuthClient: OmhAuthClient,
) {
    suspend fun getClient(context: Context): OmhAuthClient = withContext(Dispatchers.IO) {
        when (LoginState(context).getLoggedInProvider().firstOrNull()) {
            "google" -> googleAuthClient
            "facebook" -> facebookAuthClient
            "microsoft" -> microsoftAuthClient
            "microsoft_mobileweb" -> microsoftMobileWebAuthClient
            "dropbox" -> dropboxAuthClient
            "dropbox_mobileweb" -> dropboxMobileWebAuthClient
            "box_mobileweb" -> boxMobileWebAuthClient
            else -> throw Exception("No login provider found")
        }
    }
}