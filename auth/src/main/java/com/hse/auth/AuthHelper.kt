package com.hse.auth

import android.app.Activity
import android.content.Intent
import com.hse.auth.ui.LoginActivity
import com.hse.auth.ui.LoginBottomSheet
import com.hse.auth.utils.AuthConstants
import com.hse.auth.utils.Mode
import com.hse.auth.utils.getRedirectUri

object AuthHelper {

    fun login(activity: Activity, mode: Mode, requestCode: Int) {
        when (mode) {
            Mode.MODAL -> LoginBottomSheet(activity, requestCode).show()
            else -> LoginActivity.launch(activity, mode, requestCode)
        }
    }

    fun onNewIntent(intent: Intent?, activity: Activity, requestCode: Int) {
        val manifestUri = activity.getRedirectUri()
        intent?.data?.toString()?.let { intentUri ->

            if (intentUri.substring(0, intentUri.indexOf('?')) == manifestUri) {
                intent.data?.getQueryParameter(AuthConstants.KEY_CODE)?.let { code ->
                    LoginActivity.launch(activity, requestCode = requestCode, loginCode = code)
                }
            }
        }
    }
}