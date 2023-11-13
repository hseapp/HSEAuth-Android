package com.hse.hseauth

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.hse.auth.AuthHelper
import com.hse.auth.utils.AuthConstants

class MainActivity : AppCompatActivity() {

    private val TAG = "authHelperDebug"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        AuthHelper.init(this)
        AuthHelper.login(this, 1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null) {
            Toast.makeText(this, "success", Toast.LENGTH_SHORT).show()

            val accessToken = data.getStringExtra(AuthConstants.KEY_ACCESS_TOKEN) ?: ""
            val refreshToken = data.getStringExtra(AuthConstants.KEY_REFRESH_TOKEN) ?: ""

            Log.e(TAG, refreshToken)
            Log.e(TAG, AuthHelper.accessTokenIsFresh(accessToken).toString())

            AuthHelper.refreshToken(refreshToken, onResult = { model ->
                Log.e(TAG, model.toString())
            }, onError = { e ->
                Log.e(TAG, null, e)
            })

            AuthHelper.getMe(accessToken, onResult = { model ->
                Log.e(TAG, model.toString())
            }, onError = { e ->
                Log.e(TAG, null, e)
            })
        }
    }
}