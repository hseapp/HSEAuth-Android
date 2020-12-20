package com.hse.auth.ui.credentials

import android.accounts.AccountManager
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.hse.auth.R
import com.hse.auth.di.AuthComponentProvider
import com.hse.auth.ui.LoginActivity
import com.hse.auth.ui.models.UserAccountData
import com.hse.auth.utils.AuthConstants
import com.hse.auth.utils.AuthConstants.AUTH_BASE_URL
import com.hse.auth.utils.AuthConstants.AUTH_LOGIN_HINT
import com.hse.auth.utils.AuthConstants.AUTH_PATH_ADFS
import com.hse.auth.utils.AuthConstants.AUTH_PATH_AUTHORIZE
import com.hse.auth.utils.AuthConstants.AUTH_PATH_OAUTH
import com.hse.auth.utils.AuthConstants.AUTH_PROMPT
import com.hse.auth.utils.AuthConstants.AUTH_SCHEME
import com.hse.auth.utils.AuthConstants.KEY_ACCESS_TOKEN
import com.hse.auth.utils.AuthConstants.KEY_CLIENT_ID
import com.hse.auth.utils.AuthConstants.KEY_REDIRECT_URI
import com.hse.auth.utils.AuthConstants.KEY_REFRESH_TOKEN
import com.hse.auth.utils.AuthConstants.KEY_RESPONSE_TYPE
import com.hse.auth.utils.AuthConstants.RESPONSE_TYPE
import com.hse.auth.utils.getClientId
import com.hse.auth.utils.getRedirectUri
import com.hse.auth.utils.updateAccountManagerData
import com.hse.core.common.BaseViewModelFactory
import com.hse.core.common.setGone
import com.hse.core.common.setVisible
import com.hse.core.ui.BaseFragment
import kotlinx.android.synthetic.main.fragment_web_auth.*
import javax.inject.Inject


class WebViewCredentialsFragment :
    BaseFragment<WebViewCredentialsViewModel>() {

    companion object {
        const val TAG = "WebViewCredentialsFragment"

        const val KEY_USER_ACCOUNT_DATA = "user_account_data_key"
        const val PROMPT = "login"

        fun newInstance(code: String?): WebViewCredentialsFragment =
            WebViewCredentialsFragment().apply {
                arguments = Bundle().apply {
                    code?.let { putString(AuthConstants.KEY_CODE, code) }
                }
            }
    }

    @Inject
    lateinit var viewModelFactory: BaseViewModelFactory
    private var redirectUrl: String? = null

    override fun getFragmentTag(): String = TAG

    override fun provideView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_web_auth, container, false)

    override fun provideViewModel(): WebViewCredentialsViewModel {
        (activity?.applicationContext as? AuthComponentProvider)?.provideAuthComponent()
            ?.inject(this)

        return ViewModelProvider(
            this,
            viewModelFactory
        ).get(WebViewCredentialsViewModel::class.java)
    }

    private inner class WebClient : WebViewClient() {
        override fun shouldOverrideUrlLoading(
            view: WebView?,
            request: WebResourceRequest?
        ): Boolean {
            return false
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            progress_bar?.setGone()
            web_view?.setVisible()
            url ?: return
            val sbstr = url.indexOf('?')
            if (sbstr > 0 && sbstr < url.length) {
                if (url.substring(0, sbstr) == redirectUrl) {
                    val uri = Uri.parse(url)
                    uri?.getQueryParameter(AuthConstants.KEY_CODE)?.let { code ->
                        web_view?.setGone()
                        progress_bar?.setVisible()
                        viewModel.onCodeLoaded(
                            code,
                            requireContext().getClientId(),
                            requireContext().getRedirectUri()
                        )
                    }
                }
            }
        }

    }


    private fun initWbView() {
        CookieManager.getInstance().setAcceptCookie(true)
        web_view?.let {
            it.settings?.javaScriptEnabled = true
            it.webViewClient = WebClient()
            it.webChromeClient = WebChromeClient()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initWbView()
        redirectUrl = context?.getRedirectUri()

        arguments?.getString(AuthConstants.KEY_CODE)?.let { code ->
            viewModel.onCodeLoaded(
                code,
                requireContext().getClientId(),
                requireContext().getRedirectUri()
            )
        } ?: run {
            val isFirstAccount =
                AccountManager.get(requireContext())
                    .accounts
                    .find { it.type == getString(R.string.ru_hseid_acc_type) } == null

            val uriBuilder = Uri.Builder()
                .scheme(AUTH_SCHEME)
                .authority(AUTH_BASE_URL)
                .appendPath(AUTH_PATH_ADFS)
                .appendPath(AUTH_PATH_OAUTH)
                .appendPath(AUTH_PATH_AUTHORIZE)
                .appendQueryParameter(KEY_CLIENT_ID, context?.getClientId())
                .appendQueryParameter(KEY_RESPONSE_TYPE, RESPONSE_TYPE)
                .appendQueryParameter(KEY_REDIRECT_URI, context?.getRedirectUri())

            arguments?.getParcelable<UserAccountData>(KEY_USER_ACCOUNT_DATA)?.let {
                uriBuilder.appendQueryParameter(AUTH_LOGIN_HINT, it.email)
            }

            if (isFirstAccount.not()) {
                uriBuilder.appendQueryParameter(AUTH_PROMPT, PROMPT)
            }

            val uri = uriBuilder.build()
            web_view?.loadUrl(uri.toString())
        }

        viewModel.tokensResultLiveData.observe(viewLifecycleOwner, Observer { model ->
            (activity as? LoginActivity)?.let {
                val data = Intent().apply {
                    putExtra(KEY_ACCESS_TOKEN, model.accessToken)
                    putExtra(KEY_REFRESH_TOKEN, model.refreshToken)
                }
                it.setResult(Activity.RESULT_OK, data)
                it.bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            }
        })

        viewModel.userAccountLiveData.observe(
            viewLifecycleOwner,
            Observer { it.updateAccountManagerData(requireActivity()) })

        viewModel.closeWithoutResult.observe(viewLifecycleOwner, Observer {
            if (it) {
                (activity as? LoginActivity)?.apply {
                    setResult(Activity.RESULT_CANCELED)
                    finish()
                }
            }
        })

        viewModel.error.observe(viewLifecycleOwner, Observer {
            Toast.makeText(requireContext(), R.string.error_happened, Toast.LENGTH_SHORT).show()
            activity?.finish()
        })
    }

    class Builder : BaseFragment.Builder(WebViewCredentialsFragment::class.java) {
        fun addUserAccountData(userAccountData: UserAccountData): Builder {
            arguments.putParcelable(KEY_USER_ACCOUNT_DATA, userAccountData)
            return this
        }
    }
}