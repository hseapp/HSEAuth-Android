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
import com.hse.auth.utils.AuthConstants.KEY_ACCESS_EXPIRES_IN_MILLIS
import com.hse.auth.utils.AuthConstants.KEY_REFRESH_EXPIRES_IN_MILLIS
import com.hse.auth.utils.AuthConstants.KEY_REFRESH_TOKEN
import com.hse.core.enums.LoadingState
import com.hse.core.viewmodels.BaseViewModel
import com.hse.network.Network
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.joda.time.DateTime
import javax.inject.Inject

class AccountManagerViewModel @Inject constructor(val network: Network) :
    BaseViewModel() {
    override val loadingState = MutableLiveData<LoadingState>()

    companion object {
        private const val TAG = "AccountManagerVM"
        private const val TOKEN_EXPIRED_ERROR_MESSAGE = "TokenExpiredError"

        //Минимальное значение времени, которое токен ещё должен быть жив после проверки
        //То есть чтобы он не был протухшим сразу после проверки, а был жив хотя бы 10 секунд
        //для выполнения последующиъ операций
        private const val MINIMUM_TIME_DELTA_MILLIS = 30000L
    }

    private val exceptionsHandler = CoroutineExceptionHandler { _, throwable ->
        Log.e(TAG, "${throwable.message}")
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
            val accessExpiresIn =
                accountManager.getUserData(acc, KEY_ACCESS_EXPIRES_IN_MILLIS).toLong()
            val refreshExpiresIn =
                accountManager.getUserData(acc, KEY_REFRESH_EXPIRES_IN_MILLIS).toLong()

            this@AccountManagerViewModel.refreshToken = refreshToken
            Log.i(TAG, "Data from acc manager\nToken\n$token\nRefreshToken\n$refreshToken")

            if (accessExpiresIn - DateTime().millis > MINIMUM_TIME_DELTA_MILLIS) {
                try {
                    GetMeRequest(token).run(network)
                        ?.let { meEntity ->
                            accountsDataList.add(
                                UserAccountData(
                                    acc.name,
                                    meEntity.avatarUrl,
                                    token,
                                    refreshToken,
                                    accessExpiresIn,
                                    refreshExpiresIn
                                )
                            )
                            Log.i(TAG, "Successful got user data with token from acc manager")
                        }
                } catch (e: Exception) {
                    Log.i(TAG, "Catched ${e.message}")
                }
            } else {
                accountsDataList.add(
                    UserAccountData(
                        acc.name,
                        null,
                        token,
                        refreshToken,
                        accessExpiresIn,
                        refreshExpiresIn
                    )
                )
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

    fun onAccountClicked(userAccountData: UserAccountData) {
        //Токен не протух
        if (userAccountData.accessExpiresIn - DateTime().millis > MINIMUM_TIME_DELTA_MILLIS) {
            _loginWithSelectedAccount.value = userAccountData
        } else {//протух, пробуем зарефрешить
            refreshAccessToken(userAccountData)
        }
    }

    private fun refreshAccessToken(userAccountData: UserAccountData) =
        viewModelScope.launch(Dispatchers.IO + exceptionsHandler) {

            //Рефреш не протух
            if (userAccountData.refreshExpiresIn - DateTime().millis > MINIMUM_TIME_DELTA_MILLIS) {
                val tokensResult = RefreshTokenRequest(
                    clientId = clientId,
                    refreshToken = refreshToken
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

                    val accountData = UserAccountData(
                        email = userEmail,
                        accessToken = tokensResult.accessToken,
                        refreshToken = tokensResult.refreshToken ?: refreshToken,
                        avatartUrl = null,
                        accessExpiresIn = tokensResult.accessExpiresIn,
                        refreshExpiresIn = tokensResult.refreshExpiresIn
                    )
                    _userAccountLiveData.postValue(accountData)
                    _loginWithSelectedAccount.postValue(accountData)
                }
            } else {//рефреш протух, полный перелогин//TODO: hint for requset
                _reloginWithSelectedAccount.postValue(userAccountData)
            }
        }
}