package com.hse.auth

import android.app.Activity
import android.content.Context
import com.auth0.android.jwt.JWT
import com.hse.auth.models.MeDataEntity
import com.hse.auth.models.TokensModel
import com.hse.auth.requests.ApiRequests
import com.hse.auth.ui.LoginActivity
import com.hse.auth.ui.models.isAccessTokenFresh
import com.hse.auth.utils.AuthConstants
import com.hse.auth.utils.getClientId
import com.hse.auth.utils.safeResult
import com.hse.core.BaseApplication
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import timber.log.Timber


object AuthHelper {

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob() + CoroutineExceptionHandler { _, _ -> })
    private lateinit var presets: Presets

    private fun checkIsInit() =
        if (this::presets.isInitialized.not()) throw (Exception("Класс AuthHelper не был проинициализирован методом init.")) else Unit

    fun init(context: Context) {
        presets = Presets(context)
    }

    fun login(activity: Activity, requestCode: Int) {
        LoginActivity.launch(activity, requestCode)
    }

    /**
     * Обновляет access_token и в случае успеха возвращает [TokensModel].
     *
     * В случае какой-либо ошибки сработает коллбэк [onError].
     */
    @JvmStatic
    fun refreshToken(refreshToken: String, onResult: (TokensModel) -> Unit, onError: (Exception) -> Unit) {
        checkIsInit()
        scope.launch {
            val tokensResult = safeResult<TokensModel>(onCatch = {
                onError.invoke(it)
                return@launch
            }) {
                presets.authApiRequests.getRefreshToken(presets.clientID, refreshToken)
            }

            if (tokensResult != null) {
                Timber.e("Successfully refreshed")
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
                onResult.invoke(tokensResult)
            } else {
                onError.invoke(NullAuthResponseException())
            }
        }
    }

    /**
     * Возвращает аватар и ФИО юзера по access_token.
     *
     * В случае какой-либо ошибки сработает коллбэк [onError].
     */
    fun getMe(accessToken: String, onResult: (MeDataEntity) -> Unit, onError: (Exception) -> Unit) {
        checkIsInit()
        scope.launch {
            safeResult<MeDataEntity>(onCatch = {
                onError.invoke(it)
                return@launch
            }) { presets.apiRequests.getMe(ApiRequests.getAuthHeader(accessToken)) }
                ?.let { meEntity ->
                    onResult.invoke(meEntity)
                } ?: run {
                onError.invoke(NullAuthResponseException())
            }
        }
    }

    class NullAuthResponseException : Exception("Не удалось получить ответ на запрос")

    /**
     * Проверяет свежий ли access_token
     */
    @JvmStatic
    fun accessTokenIsFresh(accessToken: String) = isAccessTokenFresh(accessToken)

    @JvmStatic
    fun getClientId() = BaseApplication.appContext.getClientId()
}