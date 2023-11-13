package com.hse.auth.utils

import android.accounts.Account
import android.accounts.AccountManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hse.auth.ui.accountmanager.AccountManagerViewModel
import okhttp3.ResponseBody
import org.joda.time.DateTime
import timber.log.Timber

inline fun <reified T> Gson.fromJson(json: String): T =
    this.fromJson<T>(json, object : TypeToken<T>() {}.type)

fun String?.toIntSafe(): Int {
    if (this == null) return 0
    return try {
        this.toInt()
    } catch (e: NumberFormatException) {
        0
    }
}

suspend inline fun <reified T> safeResult(onCatch: (Exception) -> Unit = {}, task: (() -> ResponseBody)): T? {
    return try {
        val raw = task.invoke().string()
        Gson().fromJson<T>(raw)
    } catch (e: Exception) {
        onCatch.invoke(e)
        Timber.tag("AuthSafeRunError").d(e)
        null
    }
}

fun AccountManager.isAccessTokenExpired(acc: Account) = (getUserData(acc, AuthConstants.KEY_ACCESS_EXPIRES_IN_MILLIS).toLong() - DateTime().millis > AccountManagerViewModel.MINIMUM_TIME_DELTA_MILLIS).not()
fun AccountManager.isRefreshTokenExpired(acc: Account) = (getUserData(acc, AuthConstants.KEY_REFRESH_EXPIRES_IN_MILLIS).toLong() - DateTime().millis > AccountManagerViewModel.MINIMUM_TIME_DELTA_MILLIS).not()
