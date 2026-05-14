package com.tradersguardian

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.tradersguardian.R
import com.tradersguardian.ui.screens.*
import com.tradersguardian.ui.theme.*

// ── Bottom nav items ──────────────────────────────────────────────────────────

private data class NavItem(
    val route:   String,
    val label:   String,
    val iconRes: Int
)

private val bottomNavItems = listOf(
    NavItem(Routes.DASHBOARD, "Home",    R.drawable.ic_grid),
    NavItem(Routes.PLAN,      "Plan",    R.drawable.ic_trending_up),
    NavItem(Routes.HISTORY,   "History", R.drawable.ic_clock),
    NavItem(Routes.SETTINGS,  "Settings",R.drawable.ic_settings),
)

// ── Auth graph (no bottom bar) ────────────────────────────────────────────────

@Composable
fun AppNavGraph(startDestination: String) {
    val navController = rememberNavController()
    val backEntry     by navController.currentBackStackEntryAsState()
    val currentRoute  = backEntry?.destination?.route

    val showBottomBar = currentRoute in bottomNavItems.map { it.route }

    Scaffold(
        containerColor = BgMid,
        bottomBar = {
            if (showBottomBar) {
                TgBottomBar(
                    currentRoute = currentRoute ?: Routes.DASHBOARD,
                    onNavigate   = { route ->
                        navController.navigate(route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState    = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController    = navController,
            startDestination = startDestination,
            modifier         = Modifier.padding(innerPadding)
        ) {
            // ── Auth ─────────────────────────────────────────────────────────
            composable(Routes.LOGIN) {
                LoginScreen(
                    onLoginSuccess      = {
                        navController.navigate(Routes.DASHBOARD) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onNavigateToRegister = { navController.navigate(Routes.REGISTER) }
                )
            }

            composable(Routes.REGISTER) {
                RegisterScreen(
                    onRegisterSuccess = {
                        navController.navigate(Routes.LOGIN) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onNavigateToLogin = { navController.popBackStack() }
                )
            }

            // ── Main tabs (share bottom bar) ──────────────────────────────────
            composable(Routes.DASHBOARD) {
                DashboardScreen(
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true; restoreState = true
                        }
                    },
                    onLogout = {
                        navController.navigate(Routes.LOGIN) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }

            composable(Routes.PLAN) {
                PlanTradeScreen()
            }

            composable(Routes.HISTORY) {
                HistoryScreen()
            }

            composable(Routes.SETTINGS) {
                SettingsScreen()
            }
        }
    }
}

// ── Bottom Navigation Bar ─────────────────────────────────────────────────────

@Composable
private fun TgBottomBar(
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(NavBar)
            .border(
                width = 0.5.dp,
                color = BorderColor,
                shape = RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp)
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .height(64.dp)
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            bottomNavItems.forEach { item ->
                val isActive = currentRoute == item.route
                NavigationBarItem(
                    selected = isActive,
                    onClick  = { onNavigate(item.route) },
                    icon = {
                        Column(horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
                            Icon(
                                painter = painterResource(item.iconRes),
                                contentDescription = item.label,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    },
                    label = {
                        Text(
                            text  = item.label,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                                fontSize   = 10.sp
                            )
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor   = AccentCyan,
                        selectedTextColor   = AccentCyan,
                        unselectedIconColor = TextMuted,
                        unselectedTextColor = TextMuted,
                        indicatorColor      = AccentCyan.copy(alpha = 0.12f)
                    ),
                    alwaysShowLabel = true
                )
            }
        }
    }
}