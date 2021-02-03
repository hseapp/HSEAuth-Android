package com.hse.auth.ui

import android.accounts.AccountManager
import android.animation.ValueAnimator
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.FrameLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.hse.auth.R
import com.hse.auth.ui.accountmanager.AccountManagerFragment
import com.hse.auth.ui.credentials.WebViewCredentialsFragment
import com.hse.auth.utils.AuthConstants
import com.hse.core.common.color
import com.hse.core.navigation.Navigation
import com.hse.core.navigation.NavigationCallback
import com.hse.core.ui.BaseActivity
import com.hse.core.ui.BaseFragment
import kotlinx.android.synthetic.main.activity_login.*
import net.danlew.android.joda.JodaTimeAndroid

class LoginActivity : BaseActivity(), NavigationCallback {

    lateinit var bottomSheetBehavior: BottomSheetBehavior<FrameLayout>
    private var canFinish = false

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        navigation?.onSaveInstanceState(outState)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        JodaTimeAndroid.init(this)
        setContentView(R.layout.activity_login)

        window?.statusBarColor = color(R.color.auth_black_33pc)

        navigation = Navigation(savedInstanceState, R.id.navHostFragment, supportFragmentManager, this)
        if (savedInstanceState == null) navigation?.switchStack(getRootTag())

        initBottomSheetBehavior()
    }

    override fun getRootFragment(rootTag: String) = getRootFragment()

    override fun onStackChanged(newRootTag: String) = Unit

    override fun onTopFragmentChanged(fragment: BaseFragment<*>?, rootTag: String) = Unit

    override fun onBackPressed() {
        finish()
    }

    private fun getRootTag(): String = "LoginActivity"

    private fun getRootFragment(): BaseFragment<*> {
        val am = AccountManager.get(this)
        val acc = am.accounts.find { it.type == getString(R.string.ru_hseid_acc_type) }
        val code = intent.getStringExtra(AuthConstants.KEY_CODE)

        return if (acc != null && code == null) AccountManagerFragment()
        else WebViewCredentialsFragment.newInstance(code)
    }

    override fun onStart() {
        super.onStart()
        animateBackground(0f, 1f)

        Handler().postDelayed({
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }, 100)
    }

    override fun finish() {
        if (!canFinish) {
            animateBackground(1f, 0f)
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            Handler().postDelayed({
                canFinish = true
                finish()
            }, 150)
            return
        }

        super.finish()
        overridePendingTransition(0, 0)
    }

    private fun animateBackground(from: Float = 0f, to: Float = 1f) {
       val background = container?.background ?: return
       ValueAnimator.ofFloat(from, to).run {
           duration = 150
           addUpdateListener {
               val value = it.animatedValue as Float
               background.alpha = (255 * value).toInt()
           }
           start()
       }
    }

    private fun initBottomSheetBehavior() {
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        // Expanded by default
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        bottomSheetBehavior.skipCollapsed = true
        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    finish()
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float)  = Unit
        }
        )
        bottomSheet.setOnClickListener {
            finish()
        }
    }

//    private fun finish

    companion object {
        fun launch(
            context: Activity,
            requestCode: Int,
            loginCode: String? = null
        ) {
            context.startActivityForResult(Intent(context, LoginActivity::class.java).apply {
                loginCode?.let {
                    putExtra(AuthConstants.KEY_CODE, loginCode)
                }
            }, requestCode)
        }
    }
}
