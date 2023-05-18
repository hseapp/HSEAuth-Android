package com.hse.auth.ui.models

import android.os.Parcelable
import com.hse.auth.ui.accountmanager.AccountManagerViewModel
import kotlinx.android.parcel.Parcelize
import org.joda.time.DateTime

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

fun UserAccountData.isAccessTokenFresh() = accessExpiresIn - DateTime().millis > AccountManagerViewModel.MINIMUM_TIME_DELTA_MILLIS
fun UserAccountData.isRefreshTokenFresh() = refreshExpiresIn - DateTime().millis > AccountManagerViewModel.MINIMUM_TIME_DELTA_MILLIS