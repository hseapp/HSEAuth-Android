package com.hse.auth.di

import android.app.Application
import com.hse.auth.AuthApp
import com.hse.auth.AuthHelper
import com.hse.auth.Presets
import com.hse.core.di.AppModule
import com.hse.core.di.BaseAppComponent
import com.hse.core.di.CoreModule
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, AuthModule::class, CoreModule::class])
internal interface AuthAppComponent : BaseAppComponent {

    fun inject(app: AuthApp)
    fun inject(helper: Presets)
    //fun inject(activity: MainActivity)

    fun loginComponent(): AuthComponent.Factory

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder

        fun build(): AuthAppComponent
    }
}