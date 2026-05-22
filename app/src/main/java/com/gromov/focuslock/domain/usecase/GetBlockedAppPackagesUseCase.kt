package com.gromov.focuslock.domain.usecase

import com.gromov.focuslock.domain.repository.AppRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetBlockedAppPackagesUseCase @Inject constructor(
    private val appRepository: AppRepository
) {
    operator fun invoke(): Flow<List<String>> = appRepository.getBlockedAppPackages()
}