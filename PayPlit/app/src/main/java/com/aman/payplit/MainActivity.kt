package com.aman.payplit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.aman.payplit.presentation.MainViewModel
import com.aman.payplit.presentation.navigation.PayPlitNavigation
import com.aman.payplit.ui.theme.PayPlitTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var mainViewModel: MainViewModel // Inject the session check logic

    override fun onCreate(savedInstanceState: Bundle?) {
        // Install Splash Screen
        installSplashScreen()

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            PayPlitTheme {
                val navController = rememberNavController()
                Surface(color = MaterialTheme.colorScheme.background) {
                    // 🔥 Pass the dynamic start destination to your NavGraph
                    PayPlitNavigation(
                        navController = navController,
                        startDestination = mainViewModel.startDestination.value
                    )
                }
            }
        }
    }
}