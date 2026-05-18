package com.gromov.focuslock.data.repository

import android.content.Intent
import android.content.pm.PackageManager
import com.gromov.focuslock.domain.model.InstalledApp
import com.gromov.focuslock.domain.repository.AppRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class AppRepositoryImpl @Inject constructor(
    private val packageManager: PackageManager
) : AppRepository {

    override fun getInstalledApps(): Flow<List<InstalledApp>> = flow {
        val intent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }

        val apps = packageManager.queryIntentActivities(intent, 0).map { resolveInfo ->
            InstalledApp(
                appName = resolveInfo.loadLabel(packageManager).toString(),
                packageName = resolveInfo.activityInfo.packageName,
                icon = resolveInfo.loadIcon(packageManager),
                isBlocked = false
            )
        }

        emit(apps)
    }.flowOn(Dispatchers.IO)

    override fun blockApp() {
        TODO("Not yet implemented")
    }
}