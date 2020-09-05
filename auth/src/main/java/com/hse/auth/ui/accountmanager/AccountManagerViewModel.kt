package com.hse.auth.ui.accountmanager

import android.accounts.AccountManager
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.auth0.android.jwt.JWT
import com.hse.auth.requests.GetMeRequest
import com.hse.auth.requests.RefreshTokenRequest
import com.hse.auth.ui.models.UserAccountData
import com.hse.auth.utils.AuthConstants
import com.hse.auth.utils.AuthConstants.KEY_REFRESH_TOKEN
import com.hse.core.enums.LoadingState
import com.hse.core.viewmodels.BaseViewModel
import com.hse.network.Network
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AccountManagerViewModel @Inject constructor(val network: Network) :
    BaseViewModel() {
    override val loadingState = MutableLiveData<LoadingState>()

    companion object {
        private const val TOKEN_EXPIRED_ERROR_MESSAGE = "TokenExpiredError"
    }

    private val exceptionsHandler = CoroutineExceptionHandler { _, throwable ->
        if (throwable.message == TOKEN_EXPIRED_ERROR_MESSAGE) {
            Log.i("LOL", "TokenExpired")
            viewModelScope.launch(Dispatchers.IO) {
                Log.i("LOL", "TryRefresh")
                val tokensResult = RefreshTokenRequest(
                    clientId = clientId,
                    refreshToken = refreshToken
                ).run(network)
                Log.i("LOL", "TokensRefreshResult $tokensResult")

                if (tokensResult?.accessToken == null) {
                    Log.i("LOL", "No new token, auth again")
                    _navigateToCredentials.postValue(true)
                } else {
                    Log.i("LOL", "Successfully refreshed")
                    var userEmail = ""
                    JWT(tokensResult.accessToken).getClaim(AuthConstants.KEY_EMAIL).asString()
                        ?.let {
                            userEmail = it
                        } ?: JWT(tokensResult.idToken).getClaim(AuthConstants.KEY_EMAIL).asString()
                        ?.let {
                            userEmail = it
                        } ?: JWT(tokensResult.idToken).getClaim(AuthConstants.KEY_UPN).asString()
                        ?.let {
                            userEmail = it
                        }

                    val accountData = UserAccountData(
                        email = userEmail,
                        accessToken = tokensResult.accessToken,
                        refreshToken = tokensResult.refreshToken,
                        avatartUrl = null
                    )
                    _userAccountLiveData.postValue(accountData)

                    reloadUserAccountData(accountManager, accountType, clientId)
                }
            }
        }
    }

    private val _userAccountsLiveData: MutableLiveData<List<UserAccountData>> = MutableLiveData()
    val userAccountsLiveData: LiveData<List<UserAccountData>>
        get() = _userAccountsLiveData

    private val _navigateToCredentials: MutableLiveData<Boolean> = MutableLiveData()
    val navigateToCredentials: LiveData<Boolean>
        get() = _navigateToCredentials

    private val _userAccountLiveData: MutableLiveData<UserAccountData> = MutableLiveData()
    val userAccountLiveData: LiveData<UserAccountData>
        get() = _userAccountLiveData

    lateinit var clientId: String
    lateinit var refreshToken: String
    lateinit var accountManager: AccountManager
    lateinit var accountType: String

    fun onViewCreated(
        accountManager: AccountManager,
        accountType: String,
        clientId: String
    ) = viewModelScope.launch(Dispatchers.IO + exceptionsHandler) {
        this@AccountManagerViewModel.clientId = clientId
        this@AccountManagerViewModel.accountManager = accountManager
        this@AccountManagerViewModel.accountType = accountType

        val accounts = accountManager.accounts.filter { it.type == accountType }
        withContext(Dispatchers.Main) {
            loadingState.value = LoadingState.LOADING
        }

        val accountsDataList = mutableListOf<UserAccountData>()
        accounts.forEach { acc ->
            val token = accountManager.blockingGetAuthToken(acc, acc.type, true)
            val refreshToken = accountManager.getUserData(acc, KEY_REFRESH_TOKEN)
            this@AccountManagerViewModel.refreshToken = refreshToken
            Log.i("LOL", "Data from acc manager\nToken\n$token\nRefreshToken\n$refreshToken")

            try {
                GetMeRequest(token).run(network)
                    ?.let { meEntity ->
                        accountsDataList.add(
                            UserAccountData(
                                acc.name,
                                meEntity.avatarUrl,
                                token,
                                refreshToken
                            )
                        )
                        Log.i("LOL", "Successful got user data with token from acc manager")
                    }
            } catch (e: Exception) {
                Log.i("LOL", "Catched ${e.message}")
            }
        }

        withContext(Dispatchers.Main) {
            _userAccountsLiveData.value = accountsDataList
            loadingState.value = LoadingState.DONE
        }
    }

    fun onNewAccLoginClick() {
        _navigateToCredentials.value = true
    }

    fun onNavigateToCredentials() {
        _navigateToCredentials.value = false
    }

    private fun reloadUserAccountData(
        accountManager: AccountManager,
        accountType: String,
        clientId: String
    ) = viewModelScope.launch(Dispatchers.IO) {
        this@AccountManagerViewModel.clientId = clientId
        this@AccountManagerViewModel.accountManager = accountManager
        this@AccountManagerViewModel.accountType = accountType

        val accounts = accountManager.accounts.filter { it.type == accountType }
        withContext(Dispatchers.Main) {
            loadingState.value = LoadingState.LOADING
        }

        val accountsDataList = mutableListOf<UserAccountData>()
        accounts.forEach { acc ->
            val token = accountManager.blockingGetAuthToken(acc, acc.type, true)
            val refreshToken = accountManager.getUserData(acc, KEY_REFRESH_TOKEN)
            this@AccountManagerViewModel.refreshToken = refreshToken

            try {
                GetMeRequest(token).run(network)
                    ?.let { meEntity ->
                        accountsDataList.add(
                            UserAccountData(
                                acc.name,
                                meEntity.avatarUrl,
                                token,
                                refreshToken
                            )
                        )
                    }
            } catch (e: Exception) {
                Log.i("LOL", "Catched ${e.message}")
            }
        }

        withContext(Dispatchers.Main) {
            _userAccountsLiveData.value = accountsDataList
            loadingState.value = LoadingState.DONE
        }
    }
}