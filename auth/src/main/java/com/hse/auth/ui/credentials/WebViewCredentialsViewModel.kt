package com.hse.auth.ui.credentials

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.auth0.android.jwt.JWT
import com.hse.auth.models.TokensModel
import com.hse.auth.requests.GetMeRequest
import com.hse.auth.requests.TokenRequest
import com.hse.auth.ui.models.UserAccountData
import com.hse.auth.utils.AuthConstants
import com.hse.core.enums.LoadingState
import com.hse.core.viewmodels.BaseViewModel
import com.hse.network.Network
import kotlinx.coroutines.launch
import org.joda.time.DateTime
import javax.inject.Inject

class WebViewCredentialsViewModel @Inject constructor(private val network: Network) :
    BaseViewModel() {
    override val loadingState: MutableLiveData<LoadingState>? = null

    private val _tokensResultLiveData = MutableLiveData<TokensModel>()
    val tokensResultLiveData: LiveData<TokensModel>
        get() = _tokensResultLiveData

    private val _userAccountLiveData: MutableLiveData<UserAccountData> = MutableLiveData()
    val userAccountLiveData: LiveData<UserAccountData>
        get() = _userAccountLiveData

    private val _closeWithoutResult: MutableLiveData<Boolean> = MutableLiveData()
    val closeWithoutResult: LiveData<Boolean>
        get() = _closeWithoutResult

    private var wasPaused = false

    fun onCodeLoaded(
        code: String,
        clientId: String,
        redirectUri: String
    ) {
        viewModelScope.launch {
            val tokensResult = TokenRequest(
                code,
                clientId = clientId,
                redirectUri = redirectUri
            ).run(network)

            if (tokensResult != null) {
                var userEmail = ""
                JWT(tokensResult.accessToken).getClaim(AuthConstants.KEY_EMAIL).asString()?.let {
                    userEmail = it
                } ?: JWT(tokensResult.idToken).getClaim(AuthConstants.KEY_EMAIL).asString()?.let {
                    userEmail = it
                } ?: JWT(tokensResult.idToken).getClaim(AuthConstants.KEY_UPN).asString()?.let {
                    userEmail = it
                }

                GetMeRequest(tokensResult.accessToken).run(network)
                    ?.let { meEntity ->

                        val accountData = UserAccountData(
                            email = userEmail,
                            accessToken = tokensResult.accessToken,
                            refreshToken = tokensResult.refreshToken!!,
                            avatartUrl = meEntity.avatarUrl,
                            fullName = meEntity.fullName,
                            accessExpiresIn = DateTime().millis + tokensResult.accessExpiresIn * 1000,
                            refreshExpiresIn = DateTime().millis + tokensResult.refreshExpiresIn * 1000
                        )

                        _userAccountLiveData.postValue(accountData)
                        _tokensResultLiveData.postValue(tokensResult)
                    }
            }
        }
    }

    fun onPause() {
        wasPaused = true
    }

    fun onResume() {
        if (wasPaused){
            _closeWithoutResult.value = true
        }
    }
}