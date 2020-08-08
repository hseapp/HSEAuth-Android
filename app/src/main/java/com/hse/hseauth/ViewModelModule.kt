package com.hse.hseauth

import androidx.lifecycle.ViewModel
import com.hse.core.common.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(MainActivityViewModel::class)
    internal abstract fun mainActivityViewModel(viewModel: MainActivityViewModel): ViewModel
}