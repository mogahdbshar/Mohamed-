package com.dstwrtv.app

import android.os.Bundle
import android.content.res.Configuration
import android.app.PictureInPictureParams
import android.os.Build
import android.util.Rational
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dstwrtv.app.ui.HomeScreen
import com.dstwrtv.app.ui.theme.MyApplicationTheme
import com.dstwrtv.app.viewmodel.MainViewModel
import com.dstwrtv.app.viewmodel.MainViewModelFactory

class MainActivity : ComponentActivity() {
    private val isInPipMode = mutableStateOf(false)
    private var mainViewModel: MainViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Enable premium modern full-bleed screen rendering
        enableEdgeToEdge(
            statusBarStyle = androidx.activity.SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
            navigationBarStyle = androidx.activity.SystemBarStyle.dark(android.graphics.Color.TRANSPARENT)
        )
        
        setContent {
            MyApplicationTheme {
                val app = application as DstwrApplication
                val viewModel: MainViewModel = viewModel(
                    factory = MainViewModelFactory(app, app.repository)
                )
                mainViewModel = viewModel
                
                val selectedChannel by viewModel.selectedChannel.collectAsState()
                
                LaunchedEffect(selectedChannel) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        try {
                            val builder = PictureInPictureParams.Builder()
                            builder.setAspectRatio(Rational(16, 9))
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                builder.setAutoEnterEnabled(selectedChannel != null)
                                builder.setSeamlessResizeEnabled(true)
                            }
                            setPictureInPictureParams(builder.build())
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
                
                HomeScreen(viewModel = viewModel, isInPipMode = isInPipMode.value)
            }
        }
    }

    override fun onPictureInPictureModeChanged(isInPictureInPictureMode: Boolean, newConfig: Configuration) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)
        isInPipMode.value = isInPictureInPictureMode
    }

    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        if (mainViewModel?.selectedChannel?.value != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                try {
                    val params = PictureInPictureParams.Builder()
                        .setAspectRatio(Rational(16, 9))
                        .build()
                    enterPictureInPictureMode(params)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}
