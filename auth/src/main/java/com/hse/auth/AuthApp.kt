package com.hse.auth

import com.hse.auth.di.AuthAppComponent
import com.hse.auth.di.AuthComponent
import com.hse.auth.di.AuthComponentProvider
import com.hse.auth.di.DaggerAuthAppComponent
import com.hse.core.BaseApplication

class AuthApp : BaseApplication(), AuthComponentProvider {

    override fun onCreate() {
        super.onCreate()

        appComponent = DaggerAuthAppComponent.builder()
            .application(this)
            .build()

        (appComponent as AuthAppComponent).inject(this)
    }

    override fun provideAuthComponent(): AuthComponent {
        return (appComponent as AuthAppComponent).loginComponent().create()
    }
}