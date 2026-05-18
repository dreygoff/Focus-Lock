package com.gromov.focuslock.domain.model

import android.graphics.Bitmap

data class InstalledApp(
    val appName: String,
    val packageName: String,
    val icon: Bitmap,
    val isLocked: Boolean
)