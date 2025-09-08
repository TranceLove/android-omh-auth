package com.openmobilehub.android.auth.plugin.microsoft.mobileweb.data.login

import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.openmobilehub.android.auth.core.common.mobileweb.domain.models.ApiResult
import retrofit2.http.GET
import retrofit2.http.Header

internal interface MicrosoftApiRest {
    @GET("me")
    suspend fun getUserProfile(
        @Header("Authorization") token: String
    ): ApiResult<User>
}

@Keep
@JsonIgnoreProperties(ignoreUnknown = true)
internal data class User(
    @JsonProperty("givenName")
    val givenName: String,
    @JsonProperty("surname")
    val surname: String,
    @JsonProperty("mail")
    val mail: String
)
