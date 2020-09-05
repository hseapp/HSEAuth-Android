package com.hse.auth.ui.models

data class UserAccountData(
    val email: String,
    val avatartUrl: String?,
    val accessToken: String,
    val refreshToken: String
)