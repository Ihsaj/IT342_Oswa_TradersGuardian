package com.tradersguardian

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.tradersguardian.ui.screens.LoginScreen
import com.tradersguardian.ui.screens.RegisterScreen
import com.tradersguardian.ui.screens.DashboardScreen

@Composable
fun AppNavGraph(startDestination: String) {
    val navController = rememberNavController()

    NavHost(
        navController    = navController,
        startDestination = startDestination
    ) {
        composable(Routes.LOGIN) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Routes.DASHBOARD) {
                        popUpTo(0) { inclusive = true }  // clears entire back stack
                    }
                },
                onNavigateToRegister = { navController.navigate(Routes.REGISTER) }
            )
        }

        composable(Routes.REGISTER) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0) { inclusive = true }  // clears entire back stack
                    }
                },
                onNavigateToLogin = { navController.popBackStack() }
            )
        }

        composable(Routes.DASHBOARD) {
            DashboardScreen(
                onLogout = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0) { inclusive = true }  // clears entire back stack → can't go back to dashboard
                    }
                }
            )
        }
    }
}