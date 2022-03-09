package com.hse.auth.ui.credentials

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.auth0.android.jwt.JWT
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.hse.auth.models.MeDataEntity
import com.hse.auth.models.TokensModel
import com.hse.auth.requests.ApiRequests
import com.hse.auth.requests.AuthRequests
import com.hse.auth.ui.models.UserAccountData
import com.hse.auth.utils.AuthConstants
import com.hse.auth.utils.safeResult
import com.hse.core.enums.LoadingState
import com.hse.core.viewmodels.BaseViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.joda.time.DateTime
import timber.log.Timber
import javax.inject.Inject

class WebViewCredentialsViewModel @Inject constructor(
    private val apiRequests: ApiRequests,
    private val authRequests: AuthRequests
) :
    BaseViewModel() {
    override val loadingState: MutableLiveData<LoadingState>? = null

    private val exceptionsHandler = CoroutineExceptionHandler { _, throwable ->
        Timber.e("ExceptionHandler: ${throwable.message} in ${throwable.cause}; $throwable")
        FirebaseCrashlytics.getInstance().recordException(throwable)
        _error.postValue(throwable)
    }

    private val _tokensResultLiveData = MutableLiveData<TokensModel>()
    val tokensResultLiveData: LiveData<TokensModel>
        get() = _tokensResultLiveData

    private val _userAccountLiveData: MutableLiveData<UserAccountData> = MutableLiveData()
    val userAccountLiveData: LiveData<UserAccountData>
        get() = _userAccountLiveData

    private val _closeWithoutResult: MutableLiveData<Boolean> = MutableLiveData()
    val closeWithoutResult: LiveData<Boolean>
        get() = _closeWithoutResult

    private val _error: MutableLiveData<Throwable> = MutableLiveData()
    val error: LiveData<Throwable>
        get() = _error

    private var wasPaused = false

    companion object {
        private const val TAG = "WebViewCredentialsVM"
        private const val KEY_ERROR = "key_error"
    }

    fun onCodeLoaded(
        code: String,
        clientId: String,
        redirectUri: String
    ) {
        viewModelScope.launch(Dispatchers.IO + exceptionsHandler) {
            val tokensResult = safeResult<TokensModel> {
                authRequests.getToken(
                    code,
                    clientId = clientId,
                    uri = redirectUri
                )
            }

            if (tokensResult != null) {
                var userEmail = ""
                JWT(tokensResult.accessToken).getClaim(AuthConstants.KEY_EMAIL).asString()?.let {
                    userEmail = it
                } ?: JWT(tokensResult.idToken).getClaim(AuthConstants.KEY_EMAIL).asString()?.let {
                    userEmail = it
                } ?: JWT(tokensResult.idToken).getClaim(AuthConstants.KEY_UPN).asString()?.let {
                    userEmail = it
                }

                safeResult<MeDataEntity> { apiRequests.getMe(ApiRequests.getAuthHeader(tokensResult.accessToken)) }
                    ?.let { meEntity ->
                        val accountData = UserAccountData(
                            email = userEmail,
                            accessToken = tokensResult.accessToken,
                            refreshToken = tokensResult.refreshToken!!,
                            avatartUrl = meEntity.avatarUrl,
                            fullName = meEntity.fullName,
                            accessExpiresIn = DateTime().millis + tokensResult.accessExpiresIn * 1000,
                            refreshExpiresIn = DateTime().millis + tokensResult.refreshExpiresIn * 1000,
                            clientId = clientId
                        )

                        _userAccountLiveData.postValue(accountData)
                        _tokensResultLiveData.postValue(tokensResult)
                    }
            }
        }
    }

}