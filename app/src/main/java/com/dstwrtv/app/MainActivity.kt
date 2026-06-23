package com.dstwrtv.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dstwrtv.app.ui.HomeScreen
import com.dstwrtv.app.ui.theme.MyApplicationTheme
import com.dstwrtv.app.viewmodel.MainViewModel
import com.dstwrtv.app.viewmodel.MainViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Enable premium modern full-bleed screen rendering
        enableEdgeToEdge()
        
        setContent {
            MyApplicationTheme {
                val app = application as DstwrApplication
                val viewModel: MainViewModel = viewModel(
                    factory = MainViewModelFactory(app, app.repository)
                )
                
                HomeScreen(viewModel = viewModel)
            }
        }
    }
}
