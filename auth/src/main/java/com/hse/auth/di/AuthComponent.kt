package com.hse.auth.di

import com.hse.auth.ui.accountmanager.AccountManagerFragment
import com.hse.auth.ui.credentials.CredentialsFragment
import com.hse.auth.ui.credentials.WebViewCredentialsFragment

interface AuthComponent {
    fun inject(credentialsFragment: CredentialsFragment)
    fun inject(accountManagerFragment: AccountManagerFragment)
    fun inject(webViewCredentialsFragment: WebViewCredentialsFragment)
}