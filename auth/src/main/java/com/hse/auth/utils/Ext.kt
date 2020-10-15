package com.hse.auth.utils

import android.content.Context
import android.content.pm.PackageManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

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