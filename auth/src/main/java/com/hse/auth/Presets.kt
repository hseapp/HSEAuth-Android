package com.hse.auth

import android.content.Context
import com.hse.auth.di.AuthAppComponent
import com.hse.auth.requests.ApiRequests
import com.hse.auth.requests.AuthRequests
import com.hse.auth.utils.getClientId
import com.hse.core.BaseApplication
import javax.inject.Inject

internal class Presets(context: Context) {
    @Inject
    lateinit var authApiRequests: AuthRequests
    @Inject
    lateinit var apiRequests: ApiRequests

    lateinit var clientID: String
    init {
        clientID = context.getClientId()
        (BaseApplication.appComponent as AuthAppComponent).inject(this)
    }
}
