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

package com.openmobilehub.android.auth.plugin.box.mobileweb.presentation

import android.content.Context
import android.content.Intent
import com.openmobilehub.android.auth.plugin.box.mobileweb.data.login.AuthRepositoryImpl
import com.openmobilehub.android.auth.plugin.box.mobileweb.data.user.UserRepositoryImpl
import com.openmobilehub.android.auth.core.OmhAuthClient
import com.openmobilehub.android.auth.core.OmhCredentials
import com.openmobilehub.android.auth.core.async.OmhTask
import com.openmobilehub.android.auth.core.models.OmhAuthException
import com.openmobilehub.android.auth.core.models.OmhUserProfile
import com.openmobilehub.android.auth.plugin.box.mobileweb.presentation.redirect.RedirectActivity
import com.openmobilehub.android.auth.plugin.common.mobileweb.domain.auth.AuthRepository
import com.openmobilehub.android.auth.plugin.common.mobileweb.domain.auth.AuthUseCase
import com.openmobilehub.android.auth.plugin.common.mobileweb.domain.models.ApiResult
import com.openmobilehub.android.auth.plugin.common.mobileweb.domain.user.ProfileUseCase
import com.openmobilehub.android.auth.plugin.common.mobileweb.presentation.MobileWebCredentials

/**
 * Box.com Sign-In (via mobile web) implementation of the OmhAuthClient abstraction
 */
class BoxMobileWebAuthClient private constructor(
    private val clientId: String,
    private val clientSecret: String,
    private val scopes: String,
    context: Context,
) : OmhAuthClient {

    private val applicationContext: Context = context.applicationContext

    override fun initialize(): OmhTask<Unit> {
        return OmhTask({
            // No initialization needed for Box Sign-In
        })
    }

    override fun getLoginIntent(): Intent {
        return Intent(
            applicationContext,
            RedirectActivity::class.java,
        )
            .putExtra(
                com.openmobilehub.android.auth.plugin.common.mobileweb.presentation.redirect.RedirectActivity.CLIENT_ID,
                clientId
            )
            .putExtra(
                RedirectActivity.CLIENT_SECRET,
                clientSecret,
            )
            .putExtra(
                com.openmobilehub.android.auth.plugin.common.mobileweb.presentation.redirect.RedirectActivity.SCOPES,
                scopes
            )
    }

    override fun getUser(): OmhTask<OmhUserProfile> {
        val userRepository = UserRepositoryImpl.getUserRepository(applicationContext)
        val profileUseCase = ProfileUseCase.createUserProfileUseCase(userRepository)

        return OmhTask({
            val profileData = profileUseCase.getProfileData()
                ?: throw OmhAuthException.UnrecoverableLoginException(
                    cause = Throwable(message = "No user profile stored")
                )

            return@OmhTask profileData
        })
    }

    class Builder(
        private val clientId: String,
        private val clientSecret: String,
    ) : OmhAuthClient.Builder {

        private var authScope: String = ""

        fun addScope(scope: String): Builder {
            authScope += " $scope"
            authScope = authScope.trimStart().trimEnd()
            return this
        }

        override fun build(context: Context): OmhAuthClient {
            return BoxMobileWebAuthClient(clientId, clientSecret, authScope, context)
        }
    }

    override fun getCredentials(): OmhCredentials {
        val authRepository: AuthRepository = AuthRepositoryImpl.getAuthRepository(
            applicationContext,
            clientId,
            clientSecret)
        val authUseCase = AuthUseCase.createAuthUseCase(authRepository)
        return MobileWebCredentials(authUseCase, clientId)
    }

    @SuppressWarnings("TooGenericExceptionCaught") // Until we find any specific errors for this.
    override fun signOut(): OmhTask<Unit> {
        val authRepository: AuthRepository = AuthRepositoryImpl.getAuthRepository(
            applicationContext,
            clientId,
            clientSecret)
        val authUseCase = AuthUseCase.createAuthUseCase(authRepository)
        return OmhTask(authUseCase::logout)
    }

    override fun revokeToken(): OmhTask<Unit> {
        val authRepository: AuthRepository = AuthRepositoryImpl.getAuthRepository(
            applicationContext,
            clientId,
            clientSecret)
        val authUseCase = AuthUseCase.createAuthUseCase(authRepository)
        return OmhTask ({
            val apiResult: ApiResult<Unit> = authUseCase.revokeToken()
            return@OmhTask apiResult.extractResult()
        })
    }

    override fun getProviderSdk(): Any {
        throw UnsupportedOperationException(
            "getProviderSdk() is not supported for Box Sign-In over Mobile Web."
        )
    }
}
