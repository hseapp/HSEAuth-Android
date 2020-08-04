package com.hse.auth.requests

import com.google.gson.Gson
import com.hse.auth.models.TokensModel
import com.hse.auth.utils.AuthConstants.GRANT_TYPE
import com.hse.auth.utils.AuthConstants.KEY_CLIENT_ID
import com.hse.auth.utils.AuthConstants.KEY_CODE
import com.hse.auth.utils.AuthConstants.KEY_GRANT_TYPE
import com.hse.auth.utils.AuthConstants.KEY_REDIRECT_URI
import com.hse.auth.utils.fromJson
import com.hse.network.Request

class TokenRequest(
    private val code: String,
    private val clientId: String,
    private val redirectUri: String
) : Request<TokensModel>("https://auth.hse.ru/adfs/oauth2/token") {

    init {
        method = Companion.Method.POST
        params.put(KEY_CODE, code)
        params.put(KEY_CLIENT_ID, clientId)
        params.put(KEY_REDIRECT_URI, redirectUri)
        params.put(KEY_GRANT_TYPE, GRANT_TYPE)
    }

    override fun parse(response: String): TokensModel {
        return Gson().fromJson(response)
    }
}