package com.hse.auth.ui.accountmanager

import android.accounts.AccountManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.auth0.android.jwt.JWT
import com.google.firebase.analytics.FirebaseAnalytics
import com.hse.auth.requests.GetMeRequest
import com.hse.auth.requests.RefreshTokenRequest
import com.hse.auth.ui.models.UserAccountData
import com.hse.auth.utils.AuthConstants
import com.hse.auth.utils.AuthConstants.KEY_ACCESS_EXPIRES_IN_MILLIS
import com.hse.auth.utils.AuthConstants.KEY_AVATAR_URL
import com.hse.auth.utils.AuthConstants.KEY_CLIENT_ID
import com.hse.auth.utils.AuthConstants.KEY_FULL_NAME
import com.hse.auth.utils.AuthConstants.KEY_REFRESH_EXPIRES_IN_MILLIS
import com.hse.auth.utils.AuthConstants.KEY_REFRESH_TOKEN
import com.hse.core.enums.LoadingState
import com.hse.core.viewmodels.BaseViewModel
import com.hse.network.Network
import kotlinx.coroutines.*
import org.joda.time.DateTime
import javax.inject.Inject

class AccountManagerViewModel @Inject constructor(val network: Network, val context: Context) :
    BaseViewModel() {
    override val loadingState = MutableLiveData<LoadingState>()

    companion object {
        private const val TAG = "AccountManagerVM"
        private const val KEY_ERROR = "key_error"

        //Минимальное значение времени, которое токен ещё должен быть жив после проверки
        //То есть чтобы он не был протухшим сразу после проверки, а был жив хотя бы 10 секунд
        //для выполнения последующиъ операций
        private const val MINIMUM_TIME_DELTA_MILLIS = 30000L
    }

    private val exceptionsHandler = CoroutineExceptionHandler { _, throwable ->
        val data = Bundle().apply {
            putString(KEY_ERROR, throwable.message ?: "empty")
        }
        FirebaseAnalytics.getInstance(context).logEvent("AuthHandledException", data)
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

    private val _loginWithSelectedAccount: MutableLiveData<UserAccountData> = MutableLiveData()
    val loginWithSelectedAccount: LiveData<UserAccountData>
        get() = _loginWithSelectedAccount

    private val _reloginWithSelectedAccount: MutableLiveData<UserAccountData> = MutableLiveData()
    val reloginWithSelectedAccount: LiveData<UserAccountData>
        get() = _reloginWithSelectedAccount

    private val _error: MutableLiveData<Throwable> = MutableLiveData()
    val error: LiveData<Throwable>
        get() = _error

    lateinit var clientId: String
    lateinit var refreshToken: String
    lateinit var accountManager: AccountManager
    lateinit var accountType: String

    fun onViewCreated(
        accountManager: AccountManager,
        accountType: String,
        clientId: String
    ) = CoroutineScope(SupervisorJob() + viewModelScope.coroutineContext).launch() {
        this@AccountManagerViewModel.clientId = clientId
        this@AccountManagerViewModel.accountManager = accountManager
        this@AccountManagerViewModel.accountType = accountType

        val accounts = accountManager.accounts.filter { it.type == accountType }
        loadingState.value = LoadingState.LOADING
        val accountsDataList = mutableListOf<UserAccountData>()
        Log.i(TAG, "For start")
        accounts.forEach { acc ->
            viewModelScope.launch(Dispatchers.IO + exceptionsHandler) {
                val token = accountManager.blockingGetAuthToken(acc, acc.type, true)
                val refreshToken = accountManager.getUserData(acc, KEY_REFRESH_TOKEN)
                val accessExpiresIn =
                    accountManager.getUserData(acc, KEY_ACCESS_EXPIRES_IN_MILLIS).toLong()
                val refreshExpiresIn =
                    accountManager.getUserData(acc, KEY_REFRESH_EXPIRES_IN_MILLIS).toLong()
                val fullName = accountManager.getUserData(acc, KEY_FULL_NAME)
                val avatarUrl = accountManager.getUserData(acc, KEY_AVATAR_URL)
                val clientId = accountManager.getUserData(acc, KEY_CLIENT_ID)

                this@AccountManagerViewModel.refreshToken = refreshToken
                Log.i(TAG, "Data from acc manager")

                //Токен не протух
                if (accessExpiresIn - DateTime().millis > MINIMUM_TIME_DELTA_MILLIS) {
                    Log.i(TAG, "Try for get user for token")
                    GetMeRequest(token).run(network)
                        ?.let { meEntity ->
                            accountsDataList.add(
                                UserAccountData(
                                    acc.name,
                                    meEntity.avatarUrl,
                                    meEntity.fullName,
                                    token,
                                    refreshToken,
                                    accessExpiresIn,
                                    refreshExpiresIn,
                                    clientId
                                )
                            )
                            Log.i(TAG, "Successful got user data with token from acc manager")
                        }
                } else {// Протух
                    accountsDataList.add(
                        UserAccountData(
                            acc.name,
                            avatarUrl,
                            fullName,
                            token,
                            refreshToken,
                            accessExpiresIn,
                            refreshExpiresIn,
                            clientId
                        )
                    )
                }
                Unit
            }.join()
        }
        Log.i(TAG, "For end")
        _userAccountsLiveData.postValue(accountsDataList)
        loadingState.postValue(LoadingState.DONE)
    }

    fun onNewAccLoginClick() {
        _navigateToCredentials.value = true
    }

    fun onNavigateToCredentials() {
        _navigateToCredentials.value = false
    }

    fun onAccountClicked(userAccountData: UserAccountData) {
        Log.i(TAG, "OnAccountClicked")
        //Токен не протух
        if (userAccountData.accessExpiresIn - DateTime().millis > MINIMUM_TIME_DELTA_MILLIS) {
            Log.i(TAG, "Access token is fresh")
            _loginWithSelectedAccount.value = userAccountData
        } else {//протух, пробуем зарефрешить
            Log.i(TAG, "Access token is out of date")
            refreshAccessToken(userAccountData)
        }
    }

    private fun refreshAccessToken(userAccountData: UserAccountData) =
        viewModelScope.launch(Dispatchers.IO + exceptionsHandler) {

            Log.i(TAG, "Try refresh token")
            //Рефреш не протух
            if (userAccountData.refreshExpiresIn - DateTime().millis > MINIMUM_TIME_DELTA_MILLIS) {
                Log.i(TAG, "Refresh is fresh")
                val tokensResult = RefreshTokenRequest(
                    clientId = clientId,
                    refreshToken = userAccountData.refreshToken
                ).run(network)

                if (tokensResult != null) {
                    Log.i(TAG, "Successfully refreshed")
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

                    GetMeRequest(tokensResult.accessToken).run(network)
                        ?.let { meEntity ->

                            val accountData = UserAccountData(
                                email = userEmail,
                                accessToken = tokensResult.accessToken,
                                refreshToken = tokensResult.refreshToken
                                    ?: userAccountData.refreshToken,
                                avatartUrl = meEntity.avatarUrl,
                                fullName = meEntity.fullName,
                                accessExpiresIn = DateTime().millis + tokensResult.accessExpiresIn * 1000,
                                refreshExpiresIn = if (tokensResult.refreshToken != null) DateTime().millis + tokensResult.refreshExpiresIn * 1000 else userAccountData.refreshExpiresIn,
                                clientId = userAccountData.clientId
                            )
                            _userAccountLiveData.postValue(accountData)
                            _loginWithSelectedAccount.postValue(accountData)
                        }
                }
            } else {//рефреш протух, полный перелогин
                Log.i(TAG, "refresh is out of date")
                _reloginWithSelectedAccount.postValue(userAccountData)
            }
        }
}