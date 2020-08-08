package com.hse.auth.di

interface AuthComponentProvider {
    fun provideAuthComponent(): AuthComponent
}