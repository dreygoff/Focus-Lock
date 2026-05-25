package com.gromov.focuslock.domain.usecase

import com.gromov.focuslock.domain.repository.AppRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ToggleAppBlockUseCase @Inject constructor(
    private val appRepository: AppRepository
) {
    suspend operator fun invoke(packageName: String, shouldBlock: Boolean) {
        withContext(Dispatchers.IO) {
            if (shouldBlock) {
                appRepository.blockApp(packageName)
            } else {
                appRepository.unblockApp(packageName)
            }
        }
    }
}