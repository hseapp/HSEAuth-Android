package com.hse.auth.utils

import android.content.Context
import android.content.pm.PackageManager

fun Context.getClientId(): String =
    this.packageManager.getApplicationInfo(
        this.applicationContext.packageName,
        PackageManager.GET_META_DATA
    )
        .let {
            it.metaData.getString(AuthConstants.KEY_META_DATA_CLIENT_ID)
                ?: throw Exception("Client id doesn't set in manifest")
        }

fun Context.getRedirectUri(): String =
    this.packageManager.getApplicationInfo(
        this.applicationContext.packageName,
        PackageManager.GET_META_DATA
    )
        .let {
            it.metaData.getString(AuthConstants.KEY_META_DATA_REDIRECT_URI)
                ?: throw Exception("Redirect uri doesn't set in manifest")
        }