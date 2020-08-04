package com.hse.auth.ui.credentials

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.hse.auth.models.TokensModel
import com.hse.auth.requests.GetMeRequest
import com.hse.auth.requests.LoginRequest
import com.hse.auth.ui.models.UserAccountData
import com.hse.core.enums.LoadingState
import com.hse.core.viewmodels.BaseViewModel
import com.hse.log.i
import com.hse.network.Network
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CredentialsViewModel @Inject constructor(private val network: Network) : BaseViewModel() {
    override val loadingState = MutableLiveData<LoadingState>()
    val value = MutableLiveData<TokensModel?>()

    private val exceptionsHandler = CoroutineExceptionHandler { _, throwable ->
        viewModelScope.launch(Dispatchers.Main) {
            loadingState.value = LoadingState.ERROR.apply { obj = throwable }
            loadingState.value = LoadingState.IDLE
        }
    }

    private val _userAccountLiveData: MutableLiveData<UserAccountData> = MutableLiveData()
    val userAccountLiveData: LiveData<UserAccountData>
        get() = _userAccountLiveData

    fun login(email: String, password: String) {
        if (loadingState.value == LoadingState.LOADING) return
        if (email.isEmpty() || password.isEmpty()) return
        loadingState.value = LoadingState.LOADING

        viewModelScope.launch(Dispatchers.IO + exceptionsHandler) {
            val tokensResult = LoginRequest(email, password).run(network)
            i(tokensResult.toString())

            val meResult = GetMeRequest().run(network)

            if (tokensResult != null && meResult?.user?.email != null) {
                val accountData = UserAccountData(
                    email = meResult.user!!.email!!,
                    accessToken = tokensResult.accessToken,
                    refreshToken = tokensResult.refreshToken
                )

                withContext(Dispatchers.Main) {
                    value.value = tokensResult
                    _userAccountLiveData.value = accountData
                    loadingState.value = LoadingState.IDLE
                }
            }
        }
    }
}
