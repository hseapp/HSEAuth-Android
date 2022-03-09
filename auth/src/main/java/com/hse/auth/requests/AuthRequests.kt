package com.hse.auth.requests

import com.hse.auth.utils.AuthConstants
import okhttp3.ResponseBody
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface AuthRequests {

    private companion object {

        const val TOKEN_ROUTE = "oauth2/token"

        const val AUTH_SCHEME = "https"
        const val AUTH_BASE_URL = "auth.hse.ru"
        const val AUTH_PATH_ADFS = "adfs"
        const val AUTH_PATH_OAUTH = "oauth2"
        const val AUTH_PATH_AUTHORIZE = "authorize"
        const val AUTH_LOGIN_HINT = "login_hint"
        const val AUTH_PROMPT = "prompt"

        const val KEY_CLIENT_ID = "client_id"
        const val KEY_REDIRECT_URI = "redirect_uri"

        const val KEY_RESPONSE_TYPE = "response_type"
        const val RESPONSE_TYPE = "code"

        const val KEY_CODE = "code"

        const val KEY_GRANT_TYPE = "grant_type"
        const val AUTH_GRANT_TYPE = "authorization_code"
        const val REFRESH_GRANT_TYPE = "refresh_token"

        const val KEY_META_DATA_CLIENT_ID = "auth.hse.ru.client_id"
        const val KEY_META_DATA_REDIRECT_URI = "auth.hse.ru.redirect_uri"

        const val KEY_EMAIL = "email"
        const val KEY_UPN = "upn"

        const val KEY_AVATAR_URL = "avatar_url"
        const val KEY_FULL_NAME = "full_name"

        //Acc manager data keys
        const val KEY_ACCESS_EXPIRES_IN_MILLIS = "access_expires_in"
        const val KEY_REFRESH_EXPIRES_IN_MILLIS = "refresh_expires_in"
        const val KEY_ACCESS_TOKEN = "access_token"
        const val KEY_REFRESH_TOKEN = "refresh_token"
    }

    @FormUrlEncoded
    @POST(TOKEN_ROUTE)
    suspend fun getRefreshToken(
        @Field(KEY_CLIENT_ID) clientId: String?,
        @Field(KEY_REFRESH_TOKEN) token: String?,
        @Field(KEY_GRANT_TYPE) type: String = REFRESH_GRANT_TYPE
    ): ResponseBody

    @FormUrlEncoded
    @POST(TOKEN_ROUTE)
    suspend fun getToken(
        @Field(KEY_CODE) code: String?,
        @Field(KEY_CLIENT_ID) clientId: String?,
        @Field(KEY_REDIRECT_URI) uri: String?,
        @Field(KEY_GRANT_TYPE) type: String = AUTH_GRANT_TYPE
    ): ResponseBody
}