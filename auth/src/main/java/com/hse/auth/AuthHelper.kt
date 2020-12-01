package com.hse.auth

import android.app.Activity
import com.hse.auth.ui.LoginActivity
import com.hse.auth.utils.getClientId
import com.hse.core.BaseApplication

object AuthHelper {

    fun login(activity: Activity, requestCode: Int) {
        LoginActivity.launch(activity, requestCode)
    }

    fun getClientId() = BaseApplication.appContext.getClientId()
}