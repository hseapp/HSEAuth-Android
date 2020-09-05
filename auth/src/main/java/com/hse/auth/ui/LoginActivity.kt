package com.hse.auth.ui

import android.accounts.AccountManager
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.hse.auth.R
import com.hse.auth.ui.accountmanager.AccountManagerFragment
import com.hse.auth.ui.credentials.WebViewCredentialsFragment
import com.hse.auth.utils.AuthConstants
import com.hse.auth.utils.Mode
import com.hse.core.navigation.Navigation
import com.hse.core.navigation.NavigationCallback
import com.hse.core.ui.BaseActivity
import com.hse.core.ui.BaseFragment

class LoginActivity : BaseActivity(), NavigationCallback {
    val mode = Mode.BASIC

    private fun getRootTag(): String {
        return when (mode) {
            Mode.BASIC -> WebViewCredentialsFragment.TAG
            else -> ""
        }
    }

    private fun getRootFragment(): BaseFragment<*> {
        val am = AccountManager.get(this)
        val acc = am.accounts.find { it.type == getString(R.string.ru_hseid_acc_type) }
        val code = intent.getStringExtra(AuthConstants.KEY_CODE)

        return if (acc != null && code == null) AccountManagerFragment()
        else WebViewCredentialsFragment.newInstance(code)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        navigation?.onSaveInstanceState(outState)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        navigation =
            Navigation(savedInstanceState, R.id.navHostFragment, supportFragmentManager, this)
        if (savedInstanceState == null) navigation?.switchStack(getRootTag())
    }

    override fun getRootFragment(rootTag: String) = getRootFragment()

    override fun onStackChanged(newRootTag: String) {
    }

    override fun onTopFragmentChanged(fragment: BaseFragment<*>?, rootTag: String) {
    }

    companion object {
        fun launch(
            context: Activity,
            mode: Mode = Mode.BASIC,
            requestCode: Int,
            loginCode: String? = null
        ) {
            context.startActivityForResult(Intent(context, LoginActivity::class.java).apply {
                putExtra("mode", mode)
                loginCode?.let {
                    putExtra(AuthConstants.KEY_CODE, loginCode)
                }
            }, requestCode)
        }
    }
}
