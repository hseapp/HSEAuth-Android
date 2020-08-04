package com.hse.auth.accountmanager

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log

//Сервис, который будет общаться с AccountManager в фоне
class AuthService : Service() {

    companion object {
        private const val TAG = "AuthService"
    }

    private lateinit var authenticator: Authenticator

    override fun onCreate() {
        Log.i(TAG, "onCreate()")
        authenticator = Authenticator(this)
    }

    override fun onBind(intent: Intent?): IBinder? {
        Log.i(TAG, "onBind()")
        return authenticator.iBinder
    }
}