package com.gromov.focuslock.domain.repository

import com.gromov.focuslock.domain.model.InstalledApp
import kotlinx.coroutines.flow.Flow

interface AppRepository {
    fun getInstalledApps(): Flow<List<InstalledApp>>
    suspend fun lockApp(packageName: String)
    suspend fun unlockApp(packageName: String)
}