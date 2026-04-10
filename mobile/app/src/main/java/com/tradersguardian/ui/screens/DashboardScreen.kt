package com.tradersguardian.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tradersguardian.R
import com.tradersguardian.data.model.DashboardData
import com.tradersguardian.data.model.UiState
import com.tradersguardian.ui.components.*
import com.tradersguardian.ui.theme.*
import com.tradersguardian.viewmodel.DashboardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onLogout: () -> Unit,
    viewModel: DashboardViewModel = viewModel()
) {
    val dashboardState  by viewModel.dashboardState.collectAsState()
    val showLogout      by viewModel.showLogoutDialog.collectAsState()
    var selectedNav     by remember { mutableIntStateOf(0) }

    // ── Logout confirmation dialog ─────────────────────────────────────────
    if (showLogout) {
        Dialog(onDismissRequest = { viewModel.showLogoutDialog.value = false }) {
            Column(
                verticalArrangement = Arrangement.spacedBy(20.dp),
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(Surface)
                    .border(1.dp, BorderColor, RoundedCornerShape(16.dp))
                    .padding(28.dp)
            ) {
                // Icon
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(ErrorRed.copy(alpha = 0.1f))
                        .border(1.dp, ErrorRed.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_logout),
                        contentDescription = null,
                        tint = ErrorRed,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "Sign out of Trader's Guardian?",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary
                    )
                    Text(
                        "You'll need to sign back in to access your trading account and performance data.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted,
                        lineHeight = 18.sp
                    )
                }
                HorizontalDivider(color = BorderColor)
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    TgOutlinedButton(
                        text = "Stay Logged In",
                        onClick = { viewModel.showLogoutDialog.value = false },
                        modifier = Modifier.weight(1f)
                    )
                    Button(
                        onClick = { viewModel.logout(onLogout) },
                        modifier = Modifier.weight(1f).height(46.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ErrorRed,
                            contentColor = Color.White
                        )
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_logout),
                            contentDescription = null,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text("Yes, Log Out", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    Scaffold(
        containerColor = BgDark,
        topBar = {
            // ── Navbar ─────────────────────────────────────────────────────
            Surface(
                color = NavDark,
                tonalElevation = 0.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(width = 1.dp, color = BorderColor,
                        shape = RoundedCornerShape(0.dp))
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .padding(horizontal = 20.dp)
                ) {
                    // Logo + Brand name
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.padding(end = 16.dp)
                    ) {
                        NavLogoBadge()
                        Text(
                            "Trader's Guardian",
                            style = MaterialTheme.typography.titleSmall,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Nav links
                    Row(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        NavItems.forEachIndexed { index, item ->
                            NavChip(
                                label = item.label,
                                iconRes = item.iconRes,
                                isActive = selectedNav == index,
                                onClick = { selectedNav = index }
                            )
                        }
                    }

                    // Logout
                    TextButton(
                        onClick = { viewModel.showLogoutDialog.value = true },
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_logout),
                            contentDescription = "Logout",
                            tint = TextMuted,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text("Logout", color = TextMuted, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    ) { padding ->
        when (val state = dashboardState) {
            is UiState.Loading -> {
                Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = AccentCyan)
                }
            }
            is UiState.Error -> {
                Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(state.message, color = ErrorRed, style = MaterialTheme.typography.bodyMedium)
                        TgButton("Retry", onClick = { viewModel.loadDashboard() }, modifier = Modifier.width(140.dp))
                    }
                }
            }
            is UiState.Success -> DashboardContent(data = state.data, modifier = Modifier.padding(padding))
            else -> Unit
        }
    }
}

// ── Dashboard content ─────────────────────────────────────────────────────────

@Composable
private fun DashboardContent(data: DashboardData, modifier: Modifier = Modifier) {
    Column(
        verticalArrangement = Arrangement.spacedBy(20.dp),
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 32.dp)
    ) {
        // Page header
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                Text("Dashboard",
                    style = MaterialTheme.typography.headlineLarge.copy(letterSpacing = (-0.5).sp),
                    color = TextPrimary)
                Text("Monitor your trading account and performance",
                    style = MaterialTheme.typography.bodySmall, color = TextMuted,
                    modifier = Modifier.padding(top = 4.dp))
            }
            Button(
                onClick = { /* TODO: Plan New Trade */ },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AccentCyan, contentColor = BgDark)
            ) {
                Icon(painterResource(R.drawable.ic_trending_up), null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text("Plan New Trade", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
            }
        }

        // ── Account Summary Card ──────────────────────────────────────────
        SectionCard(title = "Account Summary", iconRes = R.drawable.ic_dollar) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                SummaryItem("Account Balance", "$${"%,.0f".format(data.balance)}", TextPrimary)
                SummaryItem("Risk Per Trade",  "${data.riskPct}%",              AccentCyan)
                SummaryItem("Daily Loss Limit","${data.lossLimitPct}%",         WarningOrange)
                SummaryItem("Current Daily Loss", "${"%.2f".format(data.currentLossPct)}%", TextPrimary)
            }

            Spacer(Modifier.height(20.dp))

            val lossLimitAmt   = data.balance * (data.lossLimitPct / 100)
            val currentLossAmt = data.balance * (data.currentLossPct / 100)
            val progress       = if (lossLimitAmt > 0) (currentLossAmt / lossLimitAmt).toFloat() else 0f

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
            ) {
                Text("Daily Loss Progress", color = TextMuted, style = MaterialTheme.typography.bodySmall)
                Text("$${"%.2f".format(currentLossAmt)} / $${"%.2f".format(lossLimitAmt)}",
                    color = TextMuted, style = MaterialTheme.typography.bodySmall)
            }

            val animatedProgress by animateFloatAsState(
                targetValue = progress.coerceIn(0f, 1f),
                animationSpec = tween(800, easing = EaseOutCubic), label = "progress"
            )
            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(99.dp)),
                color = if (progress > 0.8f) ErrorRed else AccentCyan,
                trackColor = Surface2,
                strokeCap = StrokeCap.Round
            )
        }

        // ── Quick Statistics Card ─────────────────────────────────────────
        SectionCard(title = "Quick Statistics", iconRes = R.drawable.ic_bar_chart) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                StatChip(
                    label = "Total Trades",
                    value = "${data.totalTrades}",
                    iconRes = R.drawable.ic_trending_up,
                    iconBg = AccentCyan.copy(alpha = 0.12f),
                    iconTint = AccentCyan,
                    valueColor = AccentCyan,
                    modifier = Modifier.weight(1f)
                )
                StatChip(
                    label = "Approved",
                    value = "${data.approvedTrades}",
                    iconRes = R.drawable.ic_check_circle,
                    iconBg = SuccessGreen.copy(alpha = 0.12f),
                    iconTint = SuccessGreen,
                    valueColor = SuccessGreen,
                    modifier = Modifier.weight(1f)
                )
                StatChip(
                    label = "Disapproved",
                    value = "${data.disapprovedTrades}",
                    iconRes = R.drawable.ic_x_circle,
                    iconBg = ErrorRed.copy(alpha = 0.12f),
                    iconTint = ErrorRed,
                    valueColor = ErrorRed,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // ── Quick Action Cards ────────────────────────────────────────────
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            ActionCard(
                title = "Plan New Trade",
                subtitle = "Validate your next trade before execution",
                iconRes = R.drawable.ic_trending_up,
                onClick = { /* TODO */ },
                modifier = Modifier.weight(1f)
            )
            ActionCard(
                title = "View History",
                subtitle = "Review your past trading decisions",
                iconRes = R.drawable.ic_bar_chart,
                onClick = { /* TODO */ },
                modifier = Modifier.weight(1f)
            )
            ActionCard(
                title = "Account Settings",
                subtitle = "Configure your risk parameters",
                iconRes = R.drawable.ic_settings,
                onClick = { /* TODO */ },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

// ── Reusable sub-components ───────────────────────────────────────────────────

@Composable
private fun SectionCard(
    title: String,
    iconRes: Int,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Surface)
            .border(1.dp, BorderColor, RoundedCornerShape(14.dp))
            .padding(24.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.padding(bottom = 20.dp)
        ) {
            Icon(painterResource(iconRes), null, tint = AccentCyan, modifier = Modifier.size(20.dp))
            Text(title, style = MaterialTheme.typography.titleMedium, color = TextPrimary)
        }
        content()
    }
}

@Composable
private fun SummaryItem(label: String, value: String, valueColor: Color) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = TextMuted)
        Text(value,
            style = MaterialTheme.typography.headlineMedium.copy(
                fontSize = 22.sp, fontWeight = FontWeight.Bold
            ),
            color = valueColor
        )
    }
}

@Composable
private fun StatChip(
    label: String,
    value: String,
    iconRes: Int,
    iconBg: Color,
    iconTint: Color,
    valueColor: Color,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Surface2)
            .border(1.dp, BorderColor, RoundedCornerShape(10.dp))
            .padding(16.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(iconBg)
        ) {
            Icon(painterResource(iconRes), null, tint = iconTint, modifier = Modifier.size(22.dp))
        }
        Column {
            Text(label, style = MaterialTheme.typography.bodySmall, color = TextMuted)
            Text(value,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontSize = 22.sp, fontWeight = FontWeight.Bold
                ),
                color = valueColor
            )
        }
    }
}

@Composable
private fun ActionCard(
    title: String,
    subtitle: String,
    iconRes: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(Surface)
            .border(1.dp, BorderColor, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(24.dp)
    ) {
        Icon(painterResource(iconRes), null, tint = AccentCyan, modifier = Modifier.size(26.dp))
        Text(title, style = MaterialTheme.typography.titleSmall, color = TextPrimary)
        Text(subtitle, style = MaterialTheme.typography.bodySmall, color = TextMuted, lineHeight = 16.sp)
    }
}

@Composable
private fun NavChip(label: String, iconRes: Int, isActive: Boolean, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isActive) AccentCyan else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 7.dp)
    ) {
        Icon(
            painterResource(iconRes), null,
            tint = if (isActive) BgDark else TextMuted,
            modifier = Modifier.size(15.dp)
        )
        Text(
            label,
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Normal),
            color = if (isActive) BgDark else TextMuted
        )
    }
}

// Nav items data
private data class NavItem(val label: String, val iconRes: Int)
private val NavItems = listOf(
    NavItem("Dashboard", R.drawable.ic_grid),
    NavItem("Plan Trade", R.drawable.ic_trending_up),
    NavItem("History",    R.drawable.ic_clock),
    NavItem("Settings",   R.drawable.ic_settings),
)
