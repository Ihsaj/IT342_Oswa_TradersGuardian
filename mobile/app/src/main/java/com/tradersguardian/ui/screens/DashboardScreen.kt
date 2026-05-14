package com.tradersguardian.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tradersguardian.R
import com.tradersguardian.data.model.*
import com.tradersguardian.ui.components.*
import com.tradersguardian.ui.theme.*
import com.tradersguardian.viewmodel.DashboardViewModel

@Composable
fun DashboardScreen(
    onNavigate: (String) -> Unit,
    onLogout: () -> Unit,
    viewModel: DashboardViewModel = viewModel()
) {
    val dashboardState by viewModel.dashboardState.collectAsState()
    val showLogout     by viewModel.showLogoutDialog.collectAsState()

    // ── Logout confirmation ────────────────────────────────────────────────
    if (showLogout) {
        Dialog(onDismissRequest = { viewModel.showLogoutDialog.value = false }) {
            Column(
                verticalArrangement = Arrangement.spacedBy(20.dp),
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Surface)
                    .border(1.dp, BorderColor, RoundedCornerShape(20.dp))
                    .padding(28.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(ErrorBg)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_logout),
                        contentDescription = null,
                        tint = ErrorRed,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Sign out?", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                    Text(
                        "You'll need to sign back in to access your account.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted,
                        lineHeight = 18.sp
                    )
                }
                HorizontalDivider(color = BorderColor)
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    TgOutlinedButton(
                        text = "Cancel",
                        onClick = { viewModel.showLogoutDialog.value = false },
                        modifier = Modifier.weight(1f)
                    )
                    Button(
                        onClick = { viewModel.logout(onLogout) },
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ErrorRed,
                            contentColor   = Color.White
                        )
                    ) {
                        Text("Log Out", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    Scaffold(
        containerColor = BgMid,
        topBar = { DashboardTopBar(onLogout = { viewModel.showLogoutDialog.value = true }) }
    ) { padding ->
        when (val state = dashboardState) {
            is UiState.Loading -> {
                Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        CircularProgressIndicator(color = AccentCyan, strokeWidth = 2.5.dp)
                        Text("Loading dashboard…", color = TextMuted, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
            is UiState.Error -> {
                Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        Icon(painterResource(R.drawable.ic_x_circle), null, tint = ErrorRed, modifier = Modifier.size(40.dp))
                        Text(state.message, color = ErrorRed, style = MaterialTheme.typography.bodyMedium)
                        TgButton("Retry", onClick = { viewModel.loadDashboard() }, modifier = Modifier.width(140.dp))
                    }
                }
            }
            is UiState.Success -> DashboardContent(
                data      = state.data,
                onNavigate = onNavigate,
                modifier  = Modifier.padding(padding)
            )
            else -> Unit
        }
    }
}

// ── Top Bar ───────────────────────────────────────────────────────────────────

@Composable
private fun DashboardTopBar(onLogout: () -> Unit) {
    Surface(
        color         = NavBar,
        tonalElevation= 0.dp,
        modifier      = Modifier
            .fillMaxWidth()
            .border(width = 0.5.dp, color = BorderColor, shape = RoundedCornerShape(0.dp))
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .height(56.dp)
                .padding(horizontal = 20.dp)
        ) {
            NavLogoBadge()
            Spacer(Modifier.width(10.dp))
            Text(
                "Trader's Guardian",
                style = MaterialTheme.typography.titleSmall,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.weight(1f))
            IconButton(onClick = onLogout) {
                Icon(
                    painter = painterResource(R.drawable.ic_logout),
                    contentDescription = "Logout",
                    tint = TextMuted,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

// ── Dashboard content ─────────────────────────────────────────────────────────

@Composable
private fun DashboardContent(
    data: DashboardData,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val s = data.settings
    val t = data.stats

    val balance       = s.accountBalance
    val riskPct       = s.riskPerTrade
    val lossLimitPct  = s.dailyLossLimit
    val currLossPct   = s.currentDailyLoss
    val lossLimitAmt  = balance * lossLimitPct / 100.0
    val currLossAmt   = balance * currLossPct  / 100.0
    val progress      = if (lossLimitAmt > 0) (currLossAmt / lossLimitAmt).toFloat().coerceIn(0f, 1f) else 0f

    val animatedProgress by animateFloatAsState(
        targetValue   = progress,
        animationSpec = tween(900, easing = EaseOutCubic),
        label         = "progress"
    )

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 20.dp)
    ) {
        // Page header
        Column {
            Text("Dashboard", style = MaterialTheme.typography.headlineLarge.copy(letterSpacing = (-0.5).sp), color = TextPrimary)
            Text("Monitor your trading performance", style = MaterialTheme.typography.bodySmall, color = TextMuted, modifier = Modifier.padding(top = 4.dp))
        }

        // ── Plan CTA ─────────────────────────────────────────────────────────
        Button(
            onClick = { onNavigate("plan-trade") },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape  = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AccentCyan, contentColor = BgDark)
        ) {
            Icon(painterResource(R.drawable.ic_trending_up), null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text("Plan New Trade", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        }

        // ── Account Summary ───────────────────────────────────────────────────
        TgCard {
            SectionHeader("Account Summary", R.drawable.ic_dollar)

            // 2×2 grid of stats
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                StatBadge("Account Balance", "$${"%.0f".format(balance)}", TextPrimary, Modifier.weight(1f))
                StatBadge("Risk Per Trade", "$riskPct%", AccentCyan, Modifier.weight(1f))
            }
            Spacer(Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                StatBadge("Daily Limit", "$lossLimitPct%", WarningAmber, Modifier.weight(1f))
                StatBadge("Current Loss", "${"%.2f".format(currLossPct)}%", if (progress > 0.75f) ErrorRed else TextPrimary, Modifier.weight(1f))
            }
            Spacer(Modifier.height(20.dp))

            // Progress bar
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
            ) {
                Text("Daily Loss Progress", color = TextMuted, style = MaterialTheme.typography.bodySmall)
                Text("$${  "%.2f".format(currLossAmt)} / $${"%.2f".format(lossLimitAmt)}", color = TextMuted, style = MaterialTheme.typography.bodySmall)
            }
            LinearProgressIndicator(
                progress      = { animatedProgress },
                modifier      = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(99.dp)),
                color         = when {
                    progress > 0.8f -> ErrorRed
                    progress > 0.5f -> WarningAmber
                    else            -> AccentCyan
                },
                trackColor    = Surface2,
                strokeCap     = StrokeCap.Round
            )
            if (progress > 0.8f) {
                Spacer(Modifier.height(10.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(ErrorBg)
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Icon(painterResource(R.drawable.ic_x_circle), null, tint = ErrorRed, modifier = Modifier.size(14.dp))
                    Text("Approaching daily loss limit", style = MaterialTheme.typography.bodySmall, color = ErrorRed)
                }
            }
        }

        // ── Quick Statistics ──────────────────────────────────────────────────
        TgCard {
            SectionHeader("Quick Statistics", R.drawable.ic_bar_chart)
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                StatRow(
                    label     = "Total Trades",
                    value     = "${t.totalTrades}",
                    iconRes   = R.drawable.ic_trending_up,
                    iconBg    = AccentCyan.copy(0.12f),
                    iconTint  = AccentCyan,
                    valueColor= AccentCyan
                )
                HorizontalDivider(color = BorderColor, thickness = 0.5.dp)
                StatRow(
                    label     = "Approved",
                    value     = "${t.approvedTrades}",
                    iconRes   = R.drawable.ic_check_circle,
                    iconBg    = SuccessBg,
                    iconTint  = SuccessGreen,
                    valueColor= SuccessGreen
                )
                HorizontalDivider(color = BorderColor, thickness = 0.5.dp)
                StatRow(
                    label     = "Disapproved",
                    value     = "${t.disapprovedTrades}",
                    iconRes   = R.drawable.ic_x_circle,
                    iconBg    = ErrorBg,
                    iconTint  = ErrorRed,
                    valueColor= ErrorRed
                )
            }
        }

        // ── Quick Actions ─────────────────────────────────────────────────────
        Text("Quick Actions", style = MaterialTheme.typography.titleSmall, color = TextSecondary)
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            ActionCard("Plan New Trade",    "Validate your trade before entering",    R.drawable.ic_trending_up, AccentCyan)  { onNavigate("plan-trade") }
            ActionCard("Trade History",     "Review your past trading decisions",     R.drawable.ic_clock,       PendingBlue) { onNavigate("history") }
            ActionCard("Account Settings",  "Configure your risk parameters",         R.drawable.ic_settings,    WarningAmber){ onNavigate("settings") }
        }

        // Bottom spacer for nav bar
        Spacer(Modifier.height(16.dp))
    }
}

// ── Stat row ──────────────────────────────────────────────────────────────────

@Composable
private fun StatRow(
    label: String, value: String,
    iconRes: Int, iconBg: Color, iconTint: Color, valueColor: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(38.dp).clip(RoundedCornerShape(10.dp)).background(iconBg)
        ) {
            Icon(painterResource(iconRes), null, tint = iconTint, modifier = Modifier.size(20.dp))
        }
        Text(label, style = MaterialTheme.typography.bodyMedium, color = TextMuted, modifier = Modifier.weight(1f))
        Text(value, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = valueColor)
    }
}

// ── Action card ───────────────────────────────────────────────────────────────

@Composable
private fun ActionCard(
    title: String, subtitle: String,
    iconRes: Int, iconTint: Color,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Surface)
            .border(1.dp, BorderColor, RoundedCornerShape(14.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(16.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(iconTint.copy(alpha = 0.12f))
        ) {
            Icon(painterResource(iconRes), null, tint = iconTint, modifier = Modifier.size(22.dp))
        }
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(title, style = MaterialTheme.typography.titleSmall, color = TextPrimary)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = TextMuted)
        }
        Icon(
            painterResource(R.drawable.ic_chevron_right),
            contentDescription = null,
            tint = TextDisabled,
            modifier = Modifier.size(18.dp)
        )
    }
}
