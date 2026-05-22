package com.gromov.focuslock.domain.usecase

import com.gromov.focuslock.domain.repository.AppRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class IsAppBlockedUseCase @Inject constructor(
    private val appRepository: AppRepository
) {
    suspend operator fun invoke(packageName: String): Boolean {
        val blockedPackages: List<String> = appRepository.getBlockedAppPackages().first()

        return packageName in blockedPackages
    }
}