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

package com.openmobilehub.android.auth.plugin.microsoft.mobileweb.presentation

import android.content.Context
import android.content.Intent
import androidx.core.content.edit

import com.openmobilehub.android.auth.plugin.microsoft.mobileweb.data.login.AuthRepositoryImpl
import com.openmobilehub.android.auth.plugin.microsoft.mobileweb.data.user.UserRepositoryImpl
import com.openmobilehub.android.auth.core.OmhAuthClient
import com.openmobilehub.android.auth.core.OmhCredentials
import com.openmobilehub.android.auth.core.async.IOmhTask
import com.openmobilehub.android.auth.core.async.OmhTask
import com.openmobilehub.android.auth.core.models.OmhAuthException
import com.openmobilehub.android.auth.core.models.OmhUserProfile
import com.openmobilehub.android.auth.core.utils.EncryptedSharedPreferences.getEncryptedSharedPrefs
import com.openmobilehub.android.auth.plugin.common.mobileweb.domain.auth.AuthRepository
import com.openmobilehub.android.auth.plugin.common.mobileweb.domain.models.ApiResult
import com.openmobilehub.android.auth.plugin.common.mobileweb.domain.auth.AuthUseCase
import com.openmobilehub.android.auth.plugin.common.mobileweb.domain.user.ProfileUseCase
import com.openmobilehub.android.auth.plugin.common.mobileweb.presentation.MobileWebCredentials
import com.openmobilehub.android.auth.plugin.common.mobileweb.presentation.redirect.RedirectActivity.Companion.CLIENT_ID
import com.openmobilehub.android.auth.plugin.common.mobileweb.presentation.redirect.RedirectActivity.Companion.SCOPES
import com.openmobilehub.android.auth.plugin.microsoft.mobileweb.data.utils.MicrosoftRetrofitImpl
import com.openmobilehub.android.auth.plugin.microsoft.mobileweb.presentation.redirect.RedirectActivity
import com.openmobilehub.android.auth.plugin.microsoft.mobileweb.utils.Constants
import com.openmobilehub.android.auth.plugin.microsoft.mobileweb.utils.Constants.PROVIDER_MICROSOFT

/**
 * Non GMS implementation of the OmhAuthClient abstraction. Required a clientId and defined scopes as
 * no extra scopes can be accessed in the future.
 */
class MicrosoftMobileWebAuthClient private constructor(
    private val clientId: String,
    private val scopes: String,
    context: Context,
) : OmhAuthClient {

    private val applicationContext: Context = context.applicationContext

    override fun initialize(): IOmhTask<Unit> {
        return OmhTask({})
    }

    override fun getLoginIntent(): Intent {
        return Intent(
            applicationContext,
            RedirectActivity::class.java
        )
            .putExtra(CLIENT_ID, clientId)
            .putExtra(SCOPES, scopes)
    }

    override fun getUser(): OmhTask<OmhUserProfile>{
        val authRepository = AuthRepositoryImpl.getAuthRepository(applicationContext)
        val userRepository = UserRepositoryImpl.getUserRepository(applicationContext)
        val profileUseCase = ProfileUseCase.createUserProfileUseCase(userRepository)

        return OmhTask({
            val profileData = profileUseCase.getProfileData()
                ?: throw OmhAuthException.UnrecoverableLoginException(
                    cause = Throwable(message = "No user profile stored")
                )
            if (profileData.name == null || profileData.surname == null) {
                // If name or surname is not available, we need to fetch it from Microsoft Graph API
                // so we need to get the access token from AuthRepository.
                val sharedPreferences = getEncryptedSharedPrefs(
                    context = applicationContext,
                    name = PROVIDER_MICROSOFT
                )
                authRepository.getAccessToken()?.let { accessToken ->
                    val userResult = MicrosoftRetrofitImpl.instance.apiREST.getUserProfile(accessToken)
                    userResult.extractResult().let { user ->
                        sharedPreferences.edit {
                            putString(Constants.NAME_KEY, user.givenName)
                            putString(Constants.SURNAME_KEY, user.surname)
                        }
                        return@OmhTask OmhUserProfile(
                            idToken = profileData.idToken,
                            name = user.givenName,
                            surname = user.surname,
                            email = profileData.email,
                            profileImage = profileData.profileImage
                        )
                    }
                } ?: return@OmhTask profileData
            } else {
                return@OmhTask profileData
            }
        })
    }

    class Builder(
        private var clientId: String,
    ) : OmhAuthClient.Builder {

        private var authScope: String = "offline_access profile"

        fun addScope(scope: String): Builder {
            authScope += " $scope"
            authScope = authScope.trimStart().trimEnd()
            return this
        }

        override fun build(context: Context): OmhAuthClient {
            return MicrosoftMobileWebAuthClient(clientId, authScope, context)
        }
    }

    override fun getCredentials(): OmhCredentials {
        val authRepository: AuthRepository = AuthRepositoryImpl.getAuthRepository(applicationContext)
        val authUseCase = AuthUseCase.createAuthUseCase(authRepository)
        return MobileWebCredentials(authUseCase, clientId)
    }

    @SuppressWarnings("TooGenericExceptionCaught") // Until we find any specific errors for this.
    override fun signOut(): OmhTask<Unit> {
        val authRepository = AuthRepositoryImpl.getAuthRepository(applicationContext)
        val authUseCase = AuthUseCase.createAuthUseCase(authRepository)
        return OmhTask(authUseCase::logout)
    }

    override fun revokeToken(): OmhTask<Unit> {
        val authRepository = AuthRepositoryImpl.getAuthRepository(applicationContext)
        val authUseCase = AuthUseCase.createAuthUseCase(authRepository)
        return OmhTask({
            val apiResult: ApiResult<Unit> = authUseCase.revokeToken()
            return@OmhTask apiResult.extractResult()
        })
    }

    override fun getProviderSdk(): Any {
        throw UnsupportedOperationException(
            "getProviderSdk() is not supported for Microsoft Sign-In over Mobile Web."
        )
    }
}
