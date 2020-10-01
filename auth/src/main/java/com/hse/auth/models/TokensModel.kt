package com.hse.auth.models

import com.google.gson.annotations.SerializedName


data class TokensModel(
    @SerializedName("access_token") var accessToken: String,
    @SerializedName("expires_in") var accessExpiresIn: Long,
    @SerializedName("refresh_token") var refreshToken: String?,
    @SerializedName("refresh_token_expires_in") var refreshExpiresIn: Long,
    @SerializedName("id_token") var idToken: String
)