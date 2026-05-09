/*
 * Copyright 2023 Open Mobile Hub
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.openmobilehub.android.auth.plugin.dropbox.mobileweb.data.login

import android.content.Context
import android.content.SharedPreferences
import com.openmobilehub.android.auth.core.utils.EncryptedSharedPreferences.getEncryptedSharedPrefs
import com.openmobilehub.android.auth.core.common.mobileweb.data.login.datasource.AuthDataSource
import com.openmobilehub.android.auth.core.common.mobileweb.domain.auth.AuthRepository
import com.openmobilehub.android.auth.core.common.mobileweb.domain.models.ApiResult
import com.openmobilehub.android.auth.core.common.mobileweb.domain.models.OAuthTokens
import com.openmobilehub.android.auth.plugin.dropbox.mobileweb.data.login.datasource.DropboxAuthDataSource
import com.openmobilehub.android.auth.plugin.dropbox.mobileweb.data.login.models.AuthTokenResponse
import com.openmobilehub.android.auth.plugin.dropbox.mobileweb.data.utils.DropboxRetrofitImpl
import com.openmobilehub.android.auth.plugin.dropbox.mobileweb.utils.Constants
import com.openmobilehub.android.auth.plugin.dropbox.mobileweb.utils.Constants.PROVIDER_DROPBOX
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal class AuthRepositoryImpl(
    private val authDataSource: AuthDataSource<AuthTokenResponse>,
    private val ioDispatcher: CoroutineDispatcher,
    private val context: Context,
) : AuthRepository {

    override suspend fun requestTokens(
        clientId: String,
        authCode: String,
        redirectUri: String,
        codeVerifier: String,
    ): ApiResult<OAuthTokens> = withContext(ioDispatcher) {
        val result: ApiResult<AuthTokenResponse> = authDataSource.requestToken(
            clientId = clientId,
            authCode = authCode,
            redirectUri = redirectUri,
            codeVerifier = codeVerifier,
        )

        result.map { data: AuthTokenResponse ->
            // HACK: store user's Dropbox account ID
            authDataSource.storeToken(
                tokenType = Constants.ID_KEY,
                token = data.accountId
            )
            authDataSource.storeToken(
                tokenType = AuthDataSource.ACCESS_TOKEN,
                token = data.accessToken,
            )
            authDataSource.storeToken(
                tokenType = AuthDataSource.REFRESH_TOKEN,
                token = checkNotNull(data.refreshToken),
            )
            OAuthTokens(
                accessToken = data.accessToken,
                refreshToken = checkNotNull(data.refreshToken),
                idToken = data.idToken,
            )
        }
    }

    override fun buildLoginUrl(
        scopes: String,
        clientId: String,
        codeChallenge: String,
        redirectUri: String,
    ): String {
        return authDataSource.buildLoginUrl(
            scopes = scopes,
            clientId = clientId,
            codeChallenge = codeChallenge,
            redirectUri = redirectUri,
        ).toString().also {
            System.err.println("Login URL: $it")
        }
    }

    override fun getAccessToken(): String? {
        return authDataSource.getToken(AuthDataSource.ACCESS_TOKEN)
    }

    override suspend fun refreshAccessToken(
        clientId: String,
    ): ApiResult<String> = withContext(ioDispatcher) {
        authDataSource.refreshAccessToken(clientId).map { data: AuthTokenResponse ->
            authDataSource.storeToken(AuthDataSource.ACCESS_TOKEN, data.accessToken)
            data.accessToken
        }
    }

    override suspend fun revokeToken(): ApiResult<Unit> = withContext(ioDispatcher) {
        val accessToken = authDataSource.getToken(AuthDataSource.ACCESS_TOKEN)
        if (accessToken == null) {
            val noTokenException = IllegalStateException("No token stored")
            return@withContext ApiResult.Error.RuntimeError(noTokenException)
        }

        return@withContext authDataSource.revokeToken(accessToken)
    }

    override fun clearData() {
        authDataSource.clearData()
    }

    override fun formatRedirectUriFrom(packageName: String, clientId: String?): String {
        return authDataSource.formatRedirectUriFrom(packageName)
    }

    companion object {

        private var authRepository: AuthRepository? = null

        fun getAuthRepository(
            context: Context,
            ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
        ): AuthRepository {
            if (authRepository == null) {
                val authService: DropboxAuthRest = DropboxRetrofitImpl.authREST
                val apiService: DropboxApiRest = DropboxRetrofitImpl.apiREST
                val sharedPreferences: SharedPreferences = getEncryptedSharedPrefs(context, PROVIDER_DROPBOX)
                val authDataSource: AuthDataSource<AuthTokenResponse> = DropboxAuthDataSource(
                    context = context,
                    authService = authService,
                    apiService = apiService,
                    sharedPreferences = sharedPreferences,
                )
                authRepository = AuthRepositoryImpl(authDataSource, ioDispatcher, context)
            }

            return authRepository!!
        }
    }
}
