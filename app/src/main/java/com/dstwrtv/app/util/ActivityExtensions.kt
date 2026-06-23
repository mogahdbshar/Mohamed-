package com.dstwrtv.app.util

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.ActivityInfo
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

fun Context.findActivity(): Activity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    return null
}

fun Activity.toggleFullscreen(isFullscreen: Boolean) {
    val window = this.window
    val insetsController = WindowCompat.getInsetsController(window, window.decorView)
    
    if (isFullscreen) {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        insetsController.hide(WindowInsetsCompat.Type.systemBars())
        insetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    } else {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        insetsController.show(WindowInsetsCompat.Type.systemBars())
    }
}
