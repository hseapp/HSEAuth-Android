package com.hse.auth.accountmanager

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import timber.log.Timber

//Сервис, который будет общаться с AccountManager в фоне
class AuthService : Service() {

    companion object {
        private const val TAG = "AuthService"
    }

    private lateinit var authenticator: Authenticator

    override fun onCreate() {
        Timber.i( "onCreate()")
        authenticator = Authenticator(this)
    }

    override fun onBind(intent: Intent?): IBinder? {
        Timber.i( "onBind()")
        return authenticator.iBinder
    }
}