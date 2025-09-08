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

package com.openmobilehub.android.auth.plugin.microsoft.mobileweb.data.user

import android.content.Context
import android.content.SharedPreferences
import com.openmobilehub.android.auth.plugin.microsoft.mobileweb.data.user.datasource.MicrosoftUserDataSource
import com.openmobilehub.android.auth.core.models.OmhUserProfile
import com.openmobilehub.android.auth.core.common.mobileweb.data.user.datasource.UserDataSource
import com.openmobilehub.android.auth.core.common.mobileweb.domain.user.UserRepository
import com.openmobilehub.android.auth.core.utils.EncryptedSharedPreferences.getEncryptedSharedPrefs
import com.openmobilehub.android.auth.plugin.microsoft.mobileweb.utils.Constants.PROVIDER_MICROSOFT
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal class UserRepositoryImpl(
    private val userDataSource: UserDataSource,
    private val ioDispatcher: CoroutineDispatcher,
) : UserRepository {

    override suspend fun handleIdToken(idToken: String, clientId: String) {
        withContext(ioDispatcher) {
            userDataSource.handleIdToken(idToken, clientId)
        }
    }

    override fun getProfileData(): OmhUserProfile? {
        return userDataSource.getProfileData()
    }

    companion object {

        private var userRepository: UserRepository? = null

        fun getUserRepository(
            context: Context,
            ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
        ): UserRepository {
            if (userRepository == null) {
                val sharedPreferences: SharedPreferences = getEncryptedSharedPrefs(context, PROVIDER_MICROSOFT)
                val userDataSource: UserDataSource = MicrosoftUserDataSource(sharedPreferences)
                userRepository = UserRepositoryImpl(userDataSource, ioDispatcher)
            }
            return userRepository!!
        }
    }
}
