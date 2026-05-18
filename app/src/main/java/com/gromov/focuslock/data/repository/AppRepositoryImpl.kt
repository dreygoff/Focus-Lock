package com.gromov.focuslock.data.repository

import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.graphics.drawable.toBitmap
import com.gromov.focuslock.data.local.dao.LockedAppDao
import com.gromov.focuslock.data.local.entity.LockedAppEntity
import com.gromov.focuslock.domain.model.InstalledApp
import com.gromov.focuslock.domain.repository.AppRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class AppRepositoryImpl @Inject constructor(
    private val packageManager: PackageManager,
    private val lockedAppDao: LockedAppDao
) : AppRepository {

    override fun getInstalledApps(): Flow<List<InstalledApp>> {
        val allAppsFlow: Flow<List<InstalledApp>> = flow {
            val intent = Intent(Intent.ACTION_MAIN).apply {
                addCategory(Intent.CATEGORY_LAUNCHER)
            }

            val apps = packageManager.queryIntentActivities(intent, 0).map { resolveInfo ->
                val drawableIcon = resolveInfo.loadIcon(packageManager)
                InstalledApp(
                    appName = resolveInfo.loadLabel(packageManager).toString(),
                    packageName = resolveInfo.activityInfo.packageName,
                    icon = drawableIcon.toBitmap(),
                    isLocked = false
                )
            }.distinctBy { it.packageName }
            emit(apps)
        }.flowOn(Dispatchers.IO)

        val lockedAppsFlow: Flow<List<String>> = lockedAppDao.getLockedApps()
        return allAppsFlow.combine(lockedAppsFlow) { installedApps, lockedPackages ->
            val lockedSet = lockedPackages.toSet()
            installedApps.map { app ->
                app.copy(
                    isLocked = app.packageName in lockedSet
                )
            }
        }
    }

    override suspend fun lockApp(packageName: String) {
        // Оборачиваем строку в объект таблицы и отдаем в DAO
        lockedAppDao.lockApp(LockedAppEntity(packageName))
    }

    override suspend fun unlockApp(packageName: String) {
        lockedAppDao.unlockApp(packageName)
    }
}