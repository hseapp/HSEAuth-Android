package com.hse.auth.ui.accountmanager

import android.accounts.AccountManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.hse.auth.models.MeDataEntity
import com.hse.auth.requests.GetMeRequest
import com.hse.auth.ui.models.UserAccountData
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

    private val exceptionsHandler = CoroutineExceptionHandler { _, throwable ->
        viewModelScope.launch(Dispatchers.Main) {
            loadingState.value = LoadingState.ERROR.apply { obj = throwable }
            loadingState.value = LoadingState.IDLE
        }
    }

    private val _userAccountLiveData: MutableLiveData<UserAccountData> = MutableLiveData()
    val userAccountLiveData: LiveData<UserAccountData>
        get() = _userAccountLiveData

    private val _meEntityLiveData: MutableLiveData<MeDataEntity> = MutableLiveData()
    val meEntityLiveData: LiveData<MeDataEntity>
        get() = _meEntityLiveData

    private val _navigateToCredentials: MutableLiveData<Boolean> = MutableLiveData()
    val navigateToCredentials: LiveData<Boolean>
        get() = _navigateToCredentials

    fun onViewCreated(
        accountManager: AccountManager,
        accountType: String
    ) = viewModelScope.launch {
        withContext(Dispatchers.IO + exceptionsHandler) {
            val account = accountManager.accounts.find { it.type == accountType }
            withContext(Dispatchers.Main) {
                loadingState.value = LoadingState.LOADING
            }
            account?.let { acc ->
                val token = accountManager.blockingGetAuthToken(acc, acc.type, true)
                val refreshToken = accountManager.getUserData(acc, KEY_REFRESH_TOKEN)

                GetMeRequest().run(network)?.let { meEntity ->
                    withContext(Dispatchers.Main) {
                        _meEntityLiveData.value = meEntity
                    }
                }

                withContext(Dispatchers.Main) {
                    _userAccountLiveData.value = UserAccountData(acc.name, token, refreshToken)
                    loadingState.value = LoadingState.DONE
                }
            }
        }
    }

    fun onNewAccLoginClick() {
        _navigateToCredentials.value = true
    }

    fun onNavigateToCredentials() {
        _navigateToCredentials.value = false
    }
}