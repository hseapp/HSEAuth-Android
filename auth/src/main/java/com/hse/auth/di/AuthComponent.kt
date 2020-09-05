package com.hse.auth.di

import com.hse.auth.ui.accountmanager.AccountManagerFragment
import com.hse.auth.ui.credentials.WebViewCredentialsFragment
import dagger.Subcomponent

@Subcomponent
interface AuthComponent {
    fun inject(accountManagerFragment: AccountManagerFragment)
    fun inject(webViewCredentialsFragment: WebViewCredentialsFragment)

    @Subcomponent.Factory
    interface Factory {
        fun create(): AuthComponent
    }
}