package com.hse.auth.accountmanager

import android.accounts.AbstractAccountAuthenticator
import android.accounts.Account
import android.accounts.AccountAuthenticatorResponse
import android.content.Context
import android.os.Bundle
import android.util.Log
import timber.log.Timber

//Класс со всей логикой авторизацией и взаимодействием
class Authenticator(val context: Context) : AbstractAccountAuthenticator(context) {
    companion object {
        private const val TAG = "Authenticator"
    }

    override fun getAuthTokenLabel(authTokenType: String?): String {
        Timber.i( "getAuthTokenLabel()")
        return "AuthTokenLabel"
    }

    override fun confirmCredentials(
        response: AccountAuthenticatorResponse?,
        account: Account?,
        options: Bundle?
    ): Bundle {
        Timber.i( "confirmCredentials()")
        return Bundle()
    }

    override fun updateCredentials(
        response: AccountAuthenticatorResponse?,
        account: Account?,
        authTokenType: String?,
        options: Bundle?
    ): Bundle {
        Timber.i( "updateCredentials()")
        return Bundle()
    }

    override fun getAuthToken(
        response: AccountAuthenticatorResponse?,
        account: Account?,
        authTokenType: String?,
        options: Bundle?
    ): Bundle {
        return Bundle()
    }

    override fun hasFeatures(
        response: AccountAuthenticatorResponse?,
        account: Account?,
        features: Array<out String>?
    ): Bundle {
        Timber.i( "hasFeatures()")
        return Bundle()
    }

    override fun editProperties(
        response: AccountAuthenticatorResponse?,
        accountType: String?
    ): Bundle {
        Timber.i( "editProperties()")
        return Bundle()
    }

    override fun addAccount(
        response: AccountAuthenticatorResponse?,
        accountType: String?,
        authTokenType: String?,
        requiredFeatures: Array<out String>?,
        options: Bundle?
    ): Bundle {
        Timber.i( "addAccount()")
        return Bundle()
    }
}