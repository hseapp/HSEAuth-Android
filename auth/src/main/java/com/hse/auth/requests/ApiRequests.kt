package com.hse.auth.requests

import com.google.gson.annotations.SerializedName
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiRequests {

    companion object {

        private const val DUMP_ROUTE = "v2/dump"
        private const val DUMP_ME_ROUTE = "$DUMP_ROUTE/me"
        private const val AUTH_HEADER = "Authorization"
        private const val LOGIN_ROUTE = "auth/lk"

        fun getAuthHeader(token: String) = "Bearer $token"
    }

    @GET(DUMP_ME_ROUTE)
    suspend fun getMe(@Header(AUTH_HEADER) authHeader: String): ResponseBody

    @POST(LOGIN_ROUTE)
    suspend fun login(@Body params: LoginParams): ResponseBody
}

data class LoginParams(
    @SerializedName("login")
    var login: String,
    @SerializedName("password")
    var pass: String
)