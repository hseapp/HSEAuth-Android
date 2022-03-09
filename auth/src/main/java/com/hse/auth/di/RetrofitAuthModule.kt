package com.hse.auth.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.hse.auth.requests.ApiRequests
import com.hse.auth.requests.AuthRequests
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
class RetrofitAuthModule {

    private object Const {

        const val AUTH_URL = "https://auth.hse.ru/adfs/"
        const val API_URL = "https://api.hseapp.ru/"

        const val GSON = "AUTH_GSON"
        const val CONVERTER = "AUTH_CONVERTER"
        const val OKHTTP = "AUTH_OKHTTP"
        const val RETROFIT = "AUTH_RETROFIT"
    }

    @Provides
    @Singleton
    @Named(Const.GSON)
    fun provideGson(): Gson = GsonBuilder().apply {

    }.create()

    @Provides
    @Singleton
    @Named(Const.CONVERTER)
    fun provideConverterFactory(@Named(Const.GSON) gson: Gson): Converter.Factory {
        return GsonConverterFactory.create(gson)
    }

    @Provides
    @Singleton
    @Named(Const.OKHTTP)
    fun provideOkHttpAuth(): OkHttpClient = OkHttpClient()
        .newBuilder()
        .build()

    @Provides
    @Singleton
    @Named(Const.RETROFIT)
    fun provideRetrofitBuilderAuth(
        @Named(Const.OKHTTP) client: OkHttpClient,
        @Named(Const.CONVERTER) converterFactory: Converter.Factory
    ): Retrofit.Builder = Retrofit.Builder()
        .client(client)
        .addConverterFactory(converterFactory)

    @Provides
    @Singleton
    fun provideAuthRequests(@Named(Const.RETROFIT) builder: Retrofit.Builder): AuthRequests = builder
        .baseUrl(Const.AUTH_URL)
        .build()
        .create(AuthRequests::class.java)

    @Provides
    @Singleton
    fun provideApiRequests(@Named(Const.RETROFIT) builder: Retrofit.Builder): ApiRequests = builder
        .baseUrl(Const.API_URL)
        .build()
        .create(ApiRequests::class.java)
}