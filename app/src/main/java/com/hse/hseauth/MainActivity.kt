package com.hse.hseauth

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.hse.auth.AuthHelper
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
}