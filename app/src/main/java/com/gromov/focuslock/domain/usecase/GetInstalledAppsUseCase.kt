package com.gromov.focuslock.domain.usecase

import com.gromov.focuslock.domain.model.InstalledApp
import com.gromov.focuslock.domain.repository.AppRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetInstalledAppsUseCase @Inject constructor(
    private val appRepository: AppRepository
) {
    operator fun invoke(): Flow<List<InstalledApp>> {
        return appRepository.getInstalledApps().map { list ->
            list.sortedBy { it.appName }
        }
    }
}