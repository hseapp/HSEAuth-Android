package com.hse.auth.utils

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.ResponseBody
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

suspend inline fun <reified T> safeResult(task: (() -> ResponseBody)): T? {
    return try {
        val raw = task.invoke().string()
        Gson().fromJson<T>(raw)
    } catch (e: Exception) {
        Timber.tag("AuthSafeRunError").d(e)
        null
    }
}
