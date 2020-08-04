package com.hse.auth.ui.credentials

import android.accounts.Account
import android.accounts.AccountManager
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.hse.auth.R
import com.hse.auth.di.AuthComponent
import com.hse.auth.utils.AuthConstants.KEY_REFRESH_TOKEN
import com.hse.core.BaseApplication
import com.hse.core.common.*
import com.hse.core.enums.LoadingState
import com.hse.core.ui.BaseFragment
import com.hse.core.ui.widgets.BorderedEditText
import com.hse.core.ui.widgets.HseButton
import com.hse.network.RequestException
import javax.inject.Inject

class CredentialsFragment : BaseFragment<CredentialsViewModel>() {
    @Inject
    lateinit var viewModelFactory: BaseViewModelFactory

    private lateinit var email: BorderedEditText
    private lateinit var password: BorderedEditText
    private lateinit var button: HseButton
    private lateinit var loader: ProgressBar

    override fun provideViewModel(): CredentialsViewModel {
        (BaseApplication.appComponent as AuthComponent).inject(this)
        return ViewModelProviders.of(this, viewModelFactory).get(CredentialsViewModel::class.java)
    }

    override fun provideView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.credentials_fragment, container, false)
        root.findViewById<View>(R.id.back_button).onClick { activity()?.onBackPressed() }

        email = root.findViewById(R.id.email)
        password = root.findViewById(R.id.password)
        button = root.findViewById(R.id.button)
        loader = root.findViewById(R.id.progressBar)

        email.title = getString(R.string.email)
        password.title = getString(R.string.password)
        password.transformationMethod = PasswordTransformationMethod.getInstance()
        password.setOnEditorActionListener { v, actionId, event ->
            login()
            true
        }
        button.onClick { login() }

        viewModel.loadingState.observe(viewLifecycleOwner, Observer {
            setLoadingState(it)
        })
        viewModel.value.observe(viewLifecycleOwner, Observer { model ->
            if (model != null) {
                activity?.let {
                    val data = Intent().apply {
                        putExtra("access_token", model.accessToken)
                        putExtra("refresh_token", model.refreshToken)
                    }
                    it.setResult(Activity.RESULT_OK, data)
                    it.finish()
                }
            }
        })

        viewModel.userAccountLiveData.observe(viewLifecycleOwner, Observer {
            val account = Account(it.email, getString(R.string.ru_hseid_acc_type))
            val userData = Bundle().apply {
                putString(KEY_REFRESH_TOKEN, it.refreshToken)
            }
            val am = AccountManager.get(context)
            am.addAccountExplicitly(account, "", userData)
            am.setAuthToken(account, account.type, it.accessToken)
        })

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        email.requestFocus()
    }

    private fun setLoadingState(state: LoadingState) {
        when (state) {
            LoadingState.LOADING -> {
                loader.setVisible()
                button.setGone()
            }
            LoadingState.IDLE -> {
                loader.setGone()
                button.setVisible()
            }
            LoadingState.ERROR -> {
                loader.setGone()
                button.setVisible()
                setError(state.obj as Throwable)
            }
        }
    }

    private fun setError(t: Throwable) {
        when (t) {
            is RequestException -> {
                if (t.name == "LkLoginError") {
                    showToast(R.string.wrong_credentials)
                }
            }
            else -> {
                showToast(R.string.error_occurred_description)
            }
        }
    }

    private fun login() {
        password.hideKeyboard()
        viewModel.login(email.text.toString(), password.text.toString())
    }

    override fun getFragmentTag() = TAG

    class Builder : BaseFragment.Builder(CredentialsFragment::class.java)

    companion object {
        const val TAG = "CredentialsFragment"
    }
}