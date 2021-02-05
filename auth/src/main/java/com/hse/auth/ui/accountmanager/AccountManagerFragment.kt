package com.hse.auth.ui.accountmanager

import android.accounts.AccountManager
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.hse.auth.R
import com.hse.auth.di.AuthComponentProvider
import com.hse.auth.ui.LoginActivity
import com.hse.auth.ui.credentials.WebViewCredentialsFragment
import com.hse.auth.utils.AuthConstants.KEY_ACCESS_TOKEN
import com.hse.auth.utils.AuthConstants.KEY_REFRESH_TOKEN
import com.hse.auth.utils.getClientId
import com.hse.auth.utils.updateAccountManagerData
import com.hse.core.common.BaseViewModelFactory
import com.hse.core.common.activity
import com.hse.core.common.onClick
import com.hse.core.enums.LoadingState
import com.hse.core.ui.BaseFragment
import kotlinx.android.synthetic.main.fragment_account_manager.*
import javax.inject.Inject

class AccountManagerFragment : BaseFragment<AccountManagerViewModel>() {

    companion object {
        private const val TAG = "AccountManagerFragment"
    }

    @Inject
    lateinit var viewModelFactory: BaseViewModelFactory

    override fun getFragmentTag(): String = TAG

    override fun provideView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_account_manager, container, false)

    override fun provideViewModel(): AccountManagerViewModel {
        (activity?.applicationContext as? AuthComponentProvider)?.provideAuthComponent()
            ?.inject(this)
        return ViewModelProvider(this, viewModelFactory).get(AccountManagerViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val am = AccountManager.get(context)
        viewModel.onViewCreated(
            am,
            getString(R.string.ru_hseid_acc_type),
            requireContext().getClientId()
        )

        val adapter = UserAccountsAdapter(
            onUserClickListener = viewModel::onAccountClicked
        )
        userAccountsRv.adapter = adapter
        userAccountsRv.layoutManager = LinearLayoutManager(requireContext())

        viewModel.userAccountsLiveData.observe(viewLifecycleOwner,
            Observer { userAccounts ->
                adapter.submitList(userAccounts)
            }
        )

        viewModel.navigateToCredentials.observe(
            viewLifecycleOwner,
            Observer {
                if (it) {
                    WebViewCredentialsFragment.Builder().go(activity())
                    viewModel.onNavigateToCredentials()
                }
            }
        )

        viewModel.loadingState.observe(viewLifecycleOwner, Observer {
            setLoadingState(it)
        })

        viewModel.userAccountLiveData.observe(
            viewLifecycleOwner,
            Observer { it.updateAccountManagerData(requireActivity()) }
        )

        viewModel.loginWithSelectedAccount.observe(viewLifecycleOwner, Observer { userData ->
            activity?.let {
                val data = Intent().apply {
                    putExtra(KEY_ACCESS_TOKEN, userData.accessToken)
                    putExtra(KEY_REFRESH_TOKEN, userData.refreshToken)
                }
                it.setResult(Activity.RESULT_OK, data)
                (it as? LoginActivity)?.bottomSheetBehavior?.state =
                    BottomSheetBehavior.STATE_HIDDEN
            }
        })

        viewModel.reloginWithSelectedAccount.observe(viewLifecycleOwner, Observer { userData ->
            WebViewCredentialsFragment.Builder().addUserAccountData(userData).go(activity())
        })

        viewModel.error.observe(viewLifecycleOwner, Observer {
            Toast.makeText(requireContext(), getString(R.string.error_happened), Toast.LENGTH_SHORT)
                .show()
            (activity() as? LoginActivity)?.bottomSheetBehavior?.state =
                BottomSheetBehavior.STATE_HIDDEN
        })

        loginWithNewAccBtn.onClick {
            viewModel.onNewAccLoginClick()
        }
    }

    private fun setLoadingState(state: LoadingState) = when (state) {
        LoadingState.LOADING -> {
            userAccountsRv.isVisible = false
            loadingPb.isVisible = true
            loginWithNewAccBtn.isVisible = false
        }
        LoadingState.DONE -> {
            userAccountsRv.isVisible = true
            loadingPb.isVisible = false
            loginWithNewAccBtn.isVisible = true
        }
        LoadingState.ERROR -> {
            userAccountsRv.isVisible = true
            Toast.makeText(requireContext(), R.string.error_happened, Toast.LENGTH_SHORT).show()
            loadingPb.isVisible = false
        }
        else -> {
            userAccountsRv.isVisible = true
            loadingPb.isVisible = false
        }
    }

    class Builder : BaseFragment.Builder(AccountManagerFragment::class.java)

}