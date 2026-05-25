package com.gromov.focuslock.ui.appselection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gromov.focuslock.domain.model.InstalledApp
import com.gromov.focuslock.domain.usecase.GetInstalledAppsUseCase
import com.gromov.focuslock.domain.usecase.ToggleAppBlockUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppSelectionViewModel @Inject constructor(
    private val getInstalledAppsUseCase: GetInstalledAppsUseCase,
    private val toggleAppBlockUseCase: ToggleAppBlockUseCase
) : ViewModel() {
    val installedApps: StateFlow<List<InstalledApp>> = getInstalledAppsUseCase().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun toggleAppLock(packageName: String, shouldBlock: Boolean) {
        viewModelScope.launch {
            toggleAppBlockUseCase(packageName, shouldBlock)
        }
    }
}