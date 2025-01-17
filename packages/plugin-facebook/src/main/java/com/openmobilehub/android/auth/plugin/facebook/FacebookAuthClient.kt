package com.openmobilehub.android.auth.plugin.facebook

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.facebook.AccessToken
import com.facebook.FacebookSdk
import com.facebook.GraphRequest
import com.facebook.HttpMethod
import com.facebook.Profile
import com.facebook.login.LoginManager
import com.openmobilehub.android.auth.core.OmhAuthClient
import com.openmobilehub.android.auth.core.async.OmhTask
import com.openmobilehub.android.auth.core.models.OmhUserProfile
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class FacebookAuthClient(val scopes: ArrayList<String>, val context: Context) :
    OmhAuthClient {

    override fun initialize(): OmhTask<Unit> {
        return OmhTask(
            {
                // No initialization needed for Facebook Sign-In
            },
        )
    }

    override fun getLoginIntent(): Intent {
        return Intent(
            context, FacebookLoginActivity::class.java
        ).putStringArrayListExtra("scopes", scopes)
    }

    override fun getUser(): OmhTask<OmhUserProfile> {
        return OmhTask(::getUserRequest)
    }

    override fun getCredentials(): FacebookCredentials {
        return FacebookCredentials()
    }

    override fun signOut(): OmhTask<Unit> {
        return OmhTask(LoginManager.getInstance()::logOut)
    }

    override fun revokeToken(): OmhTask<Unit> {
        return OmhTask({
            revokeTokenRequest()
            LoginManager.getInstance().logOut()
        })
    }

    override fun getProviderSdk() = FacebookSdk

    internal suspend fun getUserRequest(): OmhUserProfile = suspendCoroutine { continuation ->
        val request = GraphRequest.newMeRequest(
            AccessToken.getCurrentAccessToken(),
        ) { jsonObject, response ->
            if (jsonObject == null) {
                continuation.resumeWithException(response?.error?.exception!!)
            } else {
                val userProfile = OmhUserProfile(
                    jsonObject.getString("first_name"),
                    jsonObject.getString("last_name"),
                    jsonObject.optString("email", ""),
                    jsonObject.getJSONObject("picture").getJSONObject("data").getString("url")
                )

                continuation.resume(userProfile)
            }
        }

        val params = Bundle().apply {
            putString("fields", "first_name,last_name,email,picture")
        }

        request.parameters = params

        request.executeAsync()
    }

    internal suspend fun revokeTokenRequest() = suspendCoroutine { continuation ->
        val request = GraphRequest().apply {
            accessToken = AccessToken.getCurrentAccessToken()
            graphPath = "/%s/permissions".format(Profile.getCurrentProfile()?.id)
            httpMethod = HttpMethod.DELETE
            callback = GraphRequest.Callback { response ->
                if (response.error != null) {
                    continuation.resumeWithException(response.error!!.exception!!)
                } else {
                    continuation.resume(Unit)
                }
            }
        }

        request.executeAsync()
    }
}
