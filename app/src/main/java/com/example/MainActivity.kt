package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.HomeScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.MainViewModel
import com.example.viewmodel.MainViewModelFactory

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
