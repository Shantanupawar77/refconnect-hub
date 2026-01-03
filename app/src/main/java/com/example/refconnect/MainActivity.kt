package com.example.refconnect

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowCompat
import com.example.refconnect.navigation.AppNavigation
import com.example.refconnect.ui.theme.RefConnectTheme
import com.example.refconnect.viewmodel.ViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val application = application as RefConnectApplication
        val viewModelFactory = ViewModelFactory(application.repository)

        setContent {
            RefConnectTheme(dynamicColor = false) {
                AppNavigation(viewModelFactory = viewModelFactory)
            }
        }
    }
}