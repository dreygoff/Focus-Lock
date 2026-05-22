package com.gromov.focuslock.data.repository

import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.graphics.drawable.toBitmap
import com.gromov.focuslock.data.local.dao.BlockedAppDao
import com.gromov.focuslock.data.local.entity.BlockedAppEntity
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
    private val blockedAppDao: BlockedAppDao
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
                    isBlocked = false
                )
            }.distinctBy { it.packageName }
            emit(apps)
        }.flowOn(Dispatchers.IO)

        val blockedAppsFlow: Flow<List<String>> = getBlockedAppPackages()
        return allAppsFlow.combine(blockedAppsFlow) { installedApps, lockedPackages ->
            val lockedSet = lockedPackages.toSet()
            installedApps.map { app ->
                app.copy(
                    isBlocked = app.packageName in lockedSet
                )
            }
        }
    }

    override fun getBlockedAppPackages(): Flow<List<String>> = blockedAppDao.getBlockedApps()

    override suspend fun blockApp(packageName: String) {
        // Wrapping the string into an Entity object and passing it to the DAO
        blockedAppDao.blockApp(BlockedAppEntity(packageName))
    }

    override suspend fun unblockApp(packageName: String) {
        blockedAppDao.unblockApp(packageName)
    }
}