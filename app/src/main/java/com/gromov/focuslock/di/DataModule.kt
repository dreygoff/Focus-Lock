package com.gromov.focuslock.di

import android.content.Context
import android.content.pm.PackageManager
import androidx.room.Room
import com.gromov.focuslock.data.local.AppDatabase
import com.gromov.focuslock.data.local.dao.LockedAppDao
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
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase = Room.databaseBuilder(
        context = context,
        klass = AppDatabase::class.java,
        name = "focus_lock_db",
    ).build()

    @Provides
    @Singleton
    fun provideLockedAppDao(
        database: AppDatabase
    ): LockedAppDao = database.lockedAppDao()

    @Provides
    @Singleton
    fun provideAppRepository(
        packageManager: PackageManager,
        lockedAppDao: LockedAppDao
    ): AppRepository = AppRepositoryImpl(packageManager, lockedAppDao)
}