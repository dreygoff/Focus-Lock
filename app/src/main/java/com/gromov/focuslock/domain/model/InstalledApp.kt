package com.gromov.focuslock.domain.model

import android.graphics.drawable.Drawable

data class InstalledApp(
    val appName: String,
    val packageName: String,
    val icon: Drawable,
    val isBlocked: Boolean
)