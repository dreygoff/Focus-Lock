package com.gromov.focuslock.service

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.util.Log
import android.view.Gravity
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.gromov.focuslock.domain.usecase.GetBlockedAppPackagesUseCase
import com.gromov.focuslock.ui.lock.FocusLockScreen
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class AppFocusService : AccessibilityService() {

    @Inject
    lateinit var getBlockedAppPackagesUseCase: GetBlockedAppPackagesUseCase

    private val serviceScope = CoroutineScope(Dispatchers.IO)
    private lateinit var windowManager: WindowManager
    private var lastBlockedPackage: String? = null
    private var overlayLifecycleOwner: ServiceLifecycleOwner? = null
    private var blockView: ComposeView? = null

    @Volatile
    private var blockedPackagesCache = setOf<String>()

    private val logTag = "AppFocusService"

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        observeBlockedApps()
    }

    private fun observeBlockedApps() {
        serviceScope.launch {
            getBlockedAppPackagesUseCase()
                .catch { e ->
                    Log.e(logTag, "Failed to collect blocked apps Flow from UseCase", e)
                }
                .collect { blockedPackagesList ->
                    blockedPackagesCache = blockedPackagesList.toSet()
                }
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        if (event.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) return

        val currentPackage = event.packageName?.toString() ?: return

        if (currentPackage in blockedPackagesCache) {
            if (lastBlockedPackage != currentPackage) {
                lastBlockedPackage = currentPackage
                showBlockOverlay()
            }
        } else {
            if (blockView != null && currentPackage != packageName) {
                hideBlockOverlay()
            }
        }
    }

    private fun showBlockOverlay() {
        if (blockView != null) return

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                    WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
            PixelFormat.TRANSLUCENT
        ).apply { gravity = Gravity.CENTER }

        val lifecycleOwner = ServiceLifecycleOwner()
        lifecycleOwner.start()
        overlayLifecycleOwner = lifecycleOwner

        blockView = ComposeView(this).apply {
            setViewTreeLifecycleOwner(lifecycleOwner)
            setViewTreeViewModelStoreOwner(lifecycleOwner)
            setViewTreeSavedStateRegistryOwner(lifecycleOwner)

            setContent {
                FocusLockScreen(onClose = {
                    val homeIntent = Intent(Intent.ACTION_MAIN).apply {
                        addCategory(Intent.CATEGORY_HOME)
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    startActivity(homeIntent)
                })
            }
        }

        windowManager.addView(blockView, params)
    }

    private fun hideBlockOverlay() {
        blockView?.let { view ->
            if (view.isAttachedToWindow) {
                try {
                    windowManager.removeViewImmediate(view)
                } catch (e: Exception) {
                    Log.e(logTag, "Error while removing block overlay", e)
                }
            }

            blockView = null
            lastBlockedPackage = null

            overlayLifecycleOwner?.stop()
            overlayLifecycleOwner = null
        }
    }

    override fun onInterrupt() {}

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        hideBlockOverlay()
    }
}