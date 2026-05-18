package com.gromov.focuslock.di

import android.content.Context
import android.content.pm.PackageManager
import com.gromov.focuslock.data.repository.AppRepositoryImpl
import com.gromov.focuslock.domain.repository.AppRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun providePackageManager(
        @ApplicationContext context: Context
    ): PackageManager = context.packageManager


    @Provides
    @Singleton
    fun provideAppRepository(
        packageManager: PackageManager
    ): AppRepository = AppRepositoryImpl(packageManager)
}