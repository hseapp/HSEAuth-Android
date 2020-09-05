package com.hse.hseauth

import com.hse.auth.di.AuthComponent
import com.hse.auth.di.AuthComponentProvider
import com.hse.core.BaseApplication

class App : BaseApplication(), AuthComponentProvider {

    override fun onCreate() {
        super.onCreate()

        appComponent = DaggerAppComponent.builder()
            .application(this)
            .build()

        (appComponent as AppComponent).inject(this)
    }

    override fun provideAuthComponent(): AuthComponent {
        return (appComponent as AppComponent).loginComponent().create()
    }
}