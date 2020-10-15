package com.hse.auth.utils

import android.accounts.Account
import android.accounts.AccountManager
import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.hse.auth.R
import com.hse.auth.ui.models.UserAccountData
import com.hse.auth.utils.AuthConstants.KEY_ACCESS_EXPIRES_IN_MILLIS
import com.hse.auth.utils.AuthConstants.KEY_AVATAR_URL
import com.hse.auth.utils.AuthConstants.KEY_CLIENT_ID
import com.hse.auth.utils.AuthConstants.KEY_FULL_NAME
import com.hse.auth.utils.AuthConstants.KEY_REFRESH_EXPIRES_IN_MILLIS
import com.hse.auth.utils.AuthConstants.KEY_REFRESH_TOKEN

fun UserAccountData.updateAccountManagerData(activity: Activity) {

    val account = Account(email, activity.getString(R.string.ru_hseid_acc_type))
    val userData = Bundle().apply {
        putString(KEY_REFRESH_TOKEN, refreshToken)
        putString(KEY_ACCESS_EXPIRES_IN_MILLIS, accessExpiresIn.toString())
        putString(KEY_REFRESH_EXPIRES_IN_MILLIS, refreshExpiresIn.toString())
        putString(KEY_AVATAR_URL, avatartUrl.toString())
        putString(KEY_FULL_NAME, fullName.toString())
        putString(KEY_CLIENT_ID, clientId)
    }
    val am = AccountManager.get(activity)
    if (am.accounts.find { acc -> acc.name == account.name } != null) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
            am.removeAccount(
                account,
                activity,
                { _ ->
                    am.addAccountExplicitly(account, "", userData)
                    am.setAuthToken(account, account.type, accessToken)
                },
                Handler(Looper.getMainLooper())
            )
        } else {
            am.removeAccount(
                account,
                { _ ->
                    am.addAccountExplicitly(account, "", userData)
                    am.setAuthToken(account, account.type, accessToken)
                },
                Handler(Looper.getMainLooper())
            )
        }
    } else {
        am.addAccountExplicitly(account, "", userData)
        am.setAuthToken(account, account.type, accessToken)
    }
}