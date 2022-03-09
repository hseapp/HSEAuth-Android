package com.hse.auth.di

import androidx.lifecycle.ViewModel
import com.hse.auth.ui.accountmanager.AccountManagerViewModel
import com.hse.auth.ui.credentials.WebViewCredentialsViewModel
import com.hse.core.common.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module(subcomponents = [AuthComponent::class], includes = [RetrofitAuthModule::class])
abstract class AuthModule {

    @Binds
    @IntoMap
    @ViewModelKey(AccountManagerViewModel::class)
    internal abstract fun accountManagerViewModel(viewModel: AccountManagerViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(WebViewCredentialsViewModel::class)
    internal abstract fun webViewCredentialsViewModel(viewModel: WebViewCredentialsViewModel): ViewModel
}