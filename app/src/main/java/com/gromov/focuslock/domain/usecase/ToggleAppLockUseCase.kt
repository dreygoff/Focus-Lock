package com.gromov.focuslock.domain.usecase

import com.gromov.focuslock.domain.repository.AppRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ToggleAppLockUseCase @Inject constructor(
    private val repository: AppRepository
) {
    suspend operator fun invoke(packageName: String, shouldBlock: Boolean) {
        withContext(Dispatchers.IO) {
            if (shouldBlock) {
                repository.lockApp(packageName)
            } else {
                repository.unlockApp(packageName)
            }
        }
    }
}