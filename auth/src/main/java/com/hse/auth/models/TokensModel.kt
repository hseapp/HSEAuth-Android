package com.hse.auth.models

import com.google.gson.annotations.SerializedName


data class TokensModel(
    @SerializedName("access_token") var accessToken: String,
    @SerializedName("refresh_token") var refreshToken: String,
    @SerializedName("id_token") var idToken: String
)