package com.hse.auth.accountmanager

import android.accounts.AbstractAccountAuthenticator
import android.accounts.Account
import android.accounts.AccountAuthenticatorResponse
import android.content.Context
import android.os.Bundle
import android.util.Log

//Класс со всей логикой авторизацией и взаимодействием
class Authenticator(val context: Context) : AbstractAccountAuthenticator(context) {
    companion object {
        private const val TAG = "Authenticator"

        private const val EMPTY_TOKEN = ""
    }

    override fun getAuthTokenLabel(authTokenType: String?): String {
        Log.i(TAG, "getAuthTokenLabel()")
        return "AuthTokenLabel"
    }

    override fun confirmCredentials(
        response: AccountAuthenticatorResponse?,
        account: Account?,
        options: Bundle?
    ): Bundle {
        Log.i(TAG, "confirmCredentials()")
        return Bundle()
    }

    override fun updateCredentials(
        response: AccountAuthenticatorResponse?,
        account: Account?,
        authTokenType: String?,
        options: Bundle?
    ): Bundle {
        Log.i(TAG, "updateCredentials()")
        return Bundle()
    }

    override fun getAuthToken(
        response: AccountAuthenticatorResponse?,
        account: Account?,
        authTokenType: String?,
        options: Bundle?
    ): Bundle {
        val result = Bundle()
//        val am = AccountManager.get(context.applicationContext)
//        var authToken = am.peekAuthToken(account, authTokenType)
//        if (TextUtils.isEmpty(authToken)) {
//            val password = am.getPassword(account)
//            if (!TextUtils.isEmpty(password)) {
//                authToken = EMPTY_TOKEN
//            }
//        }
//        if (TextUtils.isEmpty(authToken).not()) {
//            result.putString(AccountManager.KEY_ACCOUNT_NAME, account?.name ?: "")
//            result.putString(AccountManager.KEY_ACCOUNT_TYPE, account?.type ?: "")
//            result.putString(AccountManager.KEY_AUTHTOKEN, authToken)
//        } else {
//            val intent = Intent(context, LoginActivity::class.java)
//            intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response)
//            //            intent.putExtra(AuthenticatorActivity.EXTRA_TOKEN_TYPE, authTokenType)
//            result.putParcelable(AccountManager.KEY_INTENT, intent)
//        }
        return result
    }

    override fun hasFeatures(
        response: AccountAuthenticatorResponse?,
        account: Account?,
        features: Array<out String>?
    ): Bundle {
        Log.i(TAG, "hasFeatures()")
        return Bundle()
    }

    override fun editProperties(
        response: AccountAuthenticatorResponse?,
        accountType: String?
    ): Bundle {
        Log.i(TAG, "editProperties()")
        return Bundle()
    }

    override fun addAccount(
        response: AccountAuthenticatorResponse?,
        accountType: String?,
        authTokenType: String?,
        requiredFeatures: Array<out String>?,
        options: Bundle?
    ): Bundle {
        Log.i(TAG, "addAccount()")
//        val intent = Intent(context, WebAuthActivity::class.java)
//        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response)
        val bundle = Bundle()
//        if (options != null) {
//            bundle.putAll(options)
//        }
//        bundle.putParcelable(AccountManager.KEY_INTENT, intent)
        return bundle
    }
}