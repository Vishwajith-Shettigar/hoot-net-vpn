package com.example.hoot_net.di

import android.content.Context
import androidx.room.Room
import com.example.hoot_net.data.local.AppDatabase
import com.example.hoot_net.data.remote.ApiClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

  companion object{
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
      return Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        "app_database"
      ).build()
    }

    @Provides
    @Singleton
    fun provideApiClient(): ApiClient {
      return ApiClient()
    }

    @Provides
    @Singleton
    fun provideVPNConfigDao(database: AppDatabase) = database.vpnConfigDao()
  }
}
