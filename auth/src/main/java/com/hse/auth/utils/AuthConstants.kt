package com.hse.auth.utils

object AuthConstants {
    internal const val AUTH_SCHEME = "https"
    internal const val AUTH_BASE_URL = "auth.hse.ru"
    internal const val AUTH_PATH_ADFS = "adfs"
    internal const val AUTH_PATH_OAUTH = "oauth2"
    internal const val AUTH_PATH_AUTHORIZE = "authorize"
    internal const val AUTH_LOGIN_HINT = "login_hint"
    internal const val AUTH_PROMPT = "prompt"

    internal const val KEY_CLIENT_ID = "client_id"
    internal const val KEY_REDIRECT_URI = "redirect_uri"

    internal const val KEY_RESPONSE_TYPE = "response_type"
    internal const val RESPONSE_TYPE = "code"

    internal const val KEY_CODE = "code"

    internal const val KEY_GRANT_TYPE = "grant_type"
    internal const val AUTH_GRANT_TYPE = "authorization_code"
    internal const val REFRESH_GRANT_TYPE = "refresh_token"

    internal const val KEY_META_DATA_CLIENT_ID = "auth.hse.ru.client_id"
    internal const val KEY_META_DATA_REDIRECT_URI = "auth.hse.ru.redirect_uri"

    internal const val KEY_EMAIL = "email"
    internal const val KEY_UPN = "upn"

    const val KEY_ACCESS_EXPIRES_IN_MILLIS = "access_expires_in"
    const val KEY_REFRESH_EXPIRES_IN_MILLIS = "refresh_expires_in"
    const val KEY_ACCESS_TOKEN = "access_token"
    const val KEY_REFRESH_TOKEN = "refresh_token"

}