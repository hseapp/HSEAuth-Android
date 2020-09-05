package com.hse.hseauth

import android.app.Application
import com.hse.auth.di.AuthComponent
import com.hse.auth.di.AuthModule
import com.hse.core.di.AppModule
import com.hse.core.di.BaseAppComponent
import com.hse.core.di.CoreModule
import com.hse.network.NetworkModule
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, AuthModule::class, CoreModule::class, NetworkModule::class])
interface AppComponent : BaseAppComponent {

    fun inject(app: App)
    fun inject(activity: MainActivity)

    fun loginComponent(): AuthComponent.Factory

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder

        fun build(): AppComponent
    }
}