package com.openmobilehub.android.auth.plugin.dropbox.mobileweb.data.login


import com.openmobilehub.android.auth.core.common.mobileweb.domain.models.ApiResult
import retrofit2.http.Header
import retrofit2.http.POST

/**
 * Reference: https://www.dropbox.com/developers/documentation/http/documentation
 * https://developers.dropbox.com/oidc-guide
 */
interface DropboxApiRest {
    @POST("auth/token/revoke")
    suspend fun revokeToken(
        @Header("Authorization") accessToken: String
    ): ApiResult<Unit>
}
