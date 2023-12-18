package com.psh.project.cryptorate.di

import android.content.Context
import androidx.room.Room
import com.google.gson.GsonBuilder
import com.psh.project.cryptorate.data.local.CurrencyDao
import com.psh.project.cryptorate.data.local.CurrencyDatabase
import com.psh.project.cryptorate.data.prefs.SharedPreferencesManager
import com.psh.project.cryptorate.data.remote.CryptoService
import com.psh.project.cryptorate.utils.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
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

    @Provides
    fun providesCurrencyDao(database: CurrencyDatabase): CurrencyDao {
        return database.currencyDao()
    }

    @Singleton
    @Provides
    fun providesDatabase(@ApplicationContext context: Context): CurrencyDatabase {
        return Room.databaseBuilder(
            context,
            CurrencyDatabase::class.java,
            "currency_database"
        ).build()
    }

    @Provides
    @Singleton
    fun provideSharedPreferencesManager(@ApplicationContext context: Context): SharedPreferencesManager {
        return SharedPreferencesManager.getInstance(context)
    }
}