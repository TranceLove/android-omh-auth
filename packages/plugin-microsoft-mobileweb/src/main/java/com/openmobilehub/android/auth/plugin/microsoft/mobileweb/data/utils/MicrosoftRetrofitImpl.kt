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

package com.openmobilehub.android.auth.plugin.microsoft.mobileweb.data.utils

import com.openmobilehub.android.auth.plugin.common.mobileweb.data.utils.retrofit.ApiResultCallAdapterFactory
import com.openmobilehub.android.auth.plugin.microsoft.mobileweb.data.login.MicrosoftAuthRest
import com.openmobilehub.android.auth.plugin.microsoft.mobileweb.BuildConfig
import com.openmobilehub.android.auth.plugin.microsoft.mobileweb.data.login.MicrosoftApiRest
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory

internal class MicrosoftRetrofitImpl private constructor(private val origin: String) {

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(
            HttpLoggingInterceptor().apply {
                if (BuildConfig.DEBUG) setLevel(HttpLoggingInterceptor.Level.BODY)
            },
        )
        .addInterceptor(Interceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("Origin", origin)
                .build()
            chain.proceed(request)
        })
        .build()

    private val retrofitClientForAuth = Retrofit.Builder()
        .client(okHttpClient)
        .baseUrl(BuildConfig.MICROSOFT_AUTH_URL)
        .addConverterFactory(JacksonConverterFactory.create())
        .addCallAdapterFactory(ApiResultCallAdapterFactory())
        .build()

    private val retrofitClientForApi = Retrofit.Builder()
        .client(okHttpClient)
        .baseUrl(BuildConfig.MSGRAPH_URL)
        .addConverterFactory(JacksonConverterFactory.create())
        .addCallAdapterFactory(ApiResultCallAdapterFactory())
        .build()

    val authREST: MicrosoftAuthRest = retrofitClientForAuth.create(MicrosoftAuthRest::class.java)

    val apiREST: MicrosoftApiRest = retrofitClientForApi.create(MicrosoftApiRest::class.java)

    companion object {

        lateinit var instance: MicrosoftRetrofitImpl

        @JvmStatic
        fun initialize(origin: String) {
            instance = MicrosoftRetrofitImpl(origin)
        }
    }
}
