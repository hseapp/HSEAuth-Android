package com.hse.hseauth

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.hse.auth.AuthHelper
import com.hse.auth.utils.AuthConstants
import com.hse.auth.utils.Mode
import com.hse.core.BaseApplication
import com.hse.core.ui.BaseActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        BaseApplication.appComponent.inject(this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        loginBtn.setOnClickListener {
            AuthHelper.login(this, Mode.BASIC, 1)
        }
    }

    override fun onNewIntent(intent: Intent?) {
        AuthHelper.onNewIntent(intent, this, 1)
        super.onNewIntent(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            Toast.makeText(this, "success", Toast.LENGTH_SHORT).show()
        }
    }
}