package com.tradersguardian

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowCompat
import com.tradersguardian.data.repository.AuthRepository
import com.tradersguardian.ui.theme.TradersGuardianTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Make status bar transparent & use dark icons
        WindowCompat.setDecorFitsSystemWindows(window, false)

        val repository     = AuthRepository(this)
        val startRoute     = if (repository.isLoggedIn()) Routes.DASHBOARD else Routes.LOGIN

        setContent {
            TradersGuardianTheme {
                AppNavGraph(startDestination = startRoute)
            }
        }
    }
}
