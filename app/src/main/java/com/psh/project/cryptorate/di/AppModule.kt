package com.psh.project.cryptorate.di

import com.google.gson.GsonBuilder
import com.psh.project.cryptorate.data.CryptoService
import com.psh.project.cryptorate.data.utils.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class AppModule {

    @Singleton
    @Provides
    fun providesRetrofit(): Retrofit {
        val gson = GsonBuilder().create()
        return Retrofit.Builder().addConverterFactory(GsonConverterFactory.create(gson))
            .baseUrl(Constants.BASE_URL).build()
    }

    @Singleton
    @Provides
    fun providesCryptoService(retrofit: Retrofit): CryptoService {
        return retrofit.create(CryptoService::class.java)
    }
}