package com.hse.auth.requests

import com.google.gson.Gson
import com.hse.auth.models.TokensModel
import com.hse.auth.utils.AuthConstants
import com.hse.auth.utils.fromJson
import com.hse.network.Request

class RefreshTokenRequest(
    clientId: String,
    refreshToken: String
) : Request<TokensModel>("https://auth.hse.ru/adfs/oauth2/token") {

    init {
        method = Companion.Method.POST
        params.put(AuthConstants.KEY_CLIENT_ID, clientId)
        params.put(AuthConstants.KEY_GRANT_TYPE, AuthConstants.REFRESH_GRANT_TYPE)
        params.put(AuthConstants.KEY_REFRESH_TOKEN, refreshToken)
    }

    override fun parse(response: String): TokensModel {
        return Gson().fromJson(response)
    }
}