package com.hse.auth.ui.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserAccountData(
    val email: String,
    val avatartUrl: String?,
    val fullName: String?,
    val accessToken: String,
    val refreshToken: String,
    val accessExpiresIn: Long,
    val refreshExpiresIn: Long,
    val clientId: String
): Parcelable