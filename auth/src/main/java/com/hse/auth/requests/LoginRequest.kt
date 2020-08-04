package com.hse.auth.requests

import com.google.gson.Gson
import com.hse.auth.models.TokensModel
import com.hse.auth.utils.fromJson
import com.hse.network.Request

class LoginRequest(login: String, password: String) :
    Request<TokensModel>("https://api.hseapp.ru/auth/lk") {
    init {
        method = Companion.Method.POST
        params.toJson = true
        params.put("login", login)
        params.put("password", password)
    }

    override fun parse(response: String): TokensModel {
        return Gson().fromJson(response)
    }
}