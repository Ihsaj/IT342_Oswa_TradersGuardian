package com.tradersguardian.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
                        colors = ButtonDefaults.buttonColors(containerColor = ErrorRed, contentColor = Color.White)
                    ) {
                        Text("Log Out", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    Scaffold(
        containerColor = BgDark,
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
                data       = state.data,
                onNavigate = onNavigate,
                onLogout   = { viewModel.showLogoutDialog.value = true },
                modifier   = Modifier.padding(padding)
            )
            else -> Unit
        }
    }
}

// ── Dashboard Content ─────────────────────────────────────────────────────────

@Composable
private fun DashboardContent(
    data: DashboardData,
    onNavigate: (String) -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val s = data.settings
    val t = data.stats

    val balance      = s.accountBalance
    val riskPct      = s.riskPerTrade
    val lossLimitPct = s.dailyLossLimit
    val currLossPct  = s.currentDailyLoss
    val riskAmount   = balance * riskPct / 100.0
    val lossLimitAmt = balance * lossLimitPct / 100.0
    val currLossAmt  = balance * currLossPct / 100.0
    val progress     = if (lossLimitAmt > 0) (currLossAmt / lossLimitAmt).toFloat().coerceIn(0f, 1f) else 0f

    val animatedProgress by animateFloatAsState(
        targetValue   = progress,
        animationSpec = tween(900, easing = EaseOutCubic),
        label         = "progress"
    )
    val progressColor = when {
        progress > 0.8f -> ErrorRed
        progress > 0.5f -> WarningAmber
        else            -> AccentCyan
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {

        // ── Page header ───────────────────────────────────────────────────
        Row(
            verticalAlignment   = Alignment.Top,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                Text(
                    "Welcome back",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted
                )
                Text(
                    "Dashboard",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight    = FontWeight.Bold,
                        letterSpacing = (-0.5).sp
                    ),
                    color = TextPrimary
                )
            }
            // Logout circular button
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(Surface)
                    .border(1.dp, BorderColor, CircleShape)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onLogout
                    )
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_logout),
                    contentDescription = "Logout",
                    tint = TextMuted,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        // ── Account Balance card ──────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Surface)
                .border(1.dp, BorderColor, RoundedCornerShape(16.dp))
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text("Account Balance", style = MaterialTheme.typography.bodySmall, color = TextMuted)
            Row(
                verticalAlignment   = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(
                        "${"$"}${"%.0f".format(balance)}",
                        style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimary
                    )
                    Text(
                        "Risk per trade: ",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )
                    Text(
                        "${"$"}${"%.2f".format(riskAmount)}",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                        color = AccentCyan
                    )
                }
                // Circular daily loss gauge
                Box(contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(
                        progress     = { animatedProgress },
                        modifier     = Modifier.size(72.dp),
                        color        = progressColor,
                        trackColor   = Surface2,
                        strokeWidth  = 6.dp,
                        strokeCap    = StrokeCap.Round
                    )
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "${"%.1f".format(currLossPct)}%",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = progressColor,
                            fontSize = 12.sp
                        )
                        Text(
                            "Daily\nLoss",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMuted,
                            fontSize = 8.sp,
                            lineHeight = 10.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Daily loss progress", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                Text(
                    "${"$"}${"%.2f".format(currLossAmt)} / ${"$"}${"%.2f".format(lossLimitAmt)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted
                )
            }
            Spacer(Modifier.height(4.dp))
            LinearProgressIndicator(
                progress    = { animatedProgress },
                modifier    = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(99.dp)),
                color       = progressColor,
                trackColor  = Surface2,
                strokeCap   = StrokeCap.Round
            )
        }

        // ── Quick Stats row (3 mini-cards) ────────────────────────────────
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            MiniStatCard(
                iconRes   = R.drawable.ic_bar_chart,
                iconBg    = AccentCyan.copy(0.12f),
                iconTint  = AccentCyan,
                value     = "${t.totalTrades}",
                label     = "Total Trades",
                modifier  = Modifier.weight(1f)
            )
            MiniStatCard(
                iconRes   = R.drawable.ic_check_circle,
                iconBg    = SuccessBg,
                iconTint  = SuccessGreen,
                value     = "${t.approvedTrades}",
                label     = "Approved",
                valueColor = SuccessGreen,
                modifier  = Modifier.weight(1f)
            )
            MiniStatCard(
                iconRes   = R.drawable.ic_x_circle,
                iconBg    = ErrorBg,
                iconTint  = ErrorRed,
                value     = "${t.disapprovedTrades}",
                label     = "Disapproved",
                valueColor = ErrorRed,
                modifier  = Modifier.weight(1f)
            )
        }

        // ── Risk Parameters section ───────────────────────────────────────
        Text(
            "RISK PARAMETERS",
            style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.2.sp),
            color = TextMuted,
            fontWeight = FontWeight.SemiBold
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Surface)
                .border(1.dp, BorderColor, RoundedCornerShape(16.dp))
        ) {
            RiskParamRow(
                label    = "Risk Per Trade",
                subLabel = "${"$"}${"%.2f".format(riskAmount)} max",
                value    = "$riskPct%",
                valueColor = AccentCyan
            )
            HorizontalDivider(color = BorderColor, thickness = 0.5.dp)
            RiskParamRow(
                label    = "Daily Loss Limit",
                subLabel = "${"$"}${"%.2f".format(lossLimitAmt)} max",
                value    = "$lossLimitPct%",
                valueColor = WarningAmber
            )
        }

        // ── Quick Actions section ─────────────────────────────────────────
        Text(
            "QUICK ACTIONS",
            style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.2.sp),
            color = TextMuted,
            fontWeight = FontWeight.SemiBold
        )
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            ActionCard("Plan New Trade",   "Validate your trade before entering",  R.drawable.ic_trending_up, AccentCyan)   { onNavigate("plan-trade") }
            ActionCard("Trade History",    "Review your past trading decisions",    R.drawable.ic_clock,       PendingBlue)  { onNavigate("history") }
            ActionCard("Account Settings", "Configure your risk parameters",        R.drawable.ic_settings,    WarningAmber) { onNavigate("settings") }
        }

        Spacer(Modifier.height(16.dp))
    }
}

// ── Mini stat card ────────────────────────────────────────────────────────────

@Composable
private fun MiniStatCard(
    iconRes: Int, iconBg: Color, iconTint: Color,
    value: String, label: String,
    valueColor: Color = TextPrimary,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(Surface)
            .border(1.dp, BorderColor, RoundedCornerShape(14.dp))
            .padding(14.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(34.dp).clip(RoundedCornerShape(8.dp)).background(iconBg)
        ) {
            Icon(painterResource(iconRes), null, tint = iconTint, modifier = Modifier.size(18.dp))
        }
        Text(value, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = valueColor)
        Text(label, style = MaterialTheme.typography.labelSmall, color = TextMuted, lineHeight = 14.sp)
    }
}

// ── Risk param row ────────────────────────────────────────────────────────────

@Composable
private fun RiskParamRow(label: String, subLabel: String, value: String, valueColor: Color) {
    Row(
        verticalAlignment   = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(label, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold), color = TextPrimary)
            Text(subLabel, style = MaterialTheme.typography.bodySmall, color = TextMuted)
        }
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
        verticalAlignment   = Alignment.CenterVertically,
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
            Text(title,    style = MaterialTheme.typography.titleSmall, color = TextPrimary)
            Text(subtitle, style = MaterialTheme.typography.bodySmall,  color = TextMuted)
        }
        Icon(
            painterResource(R.drawable.ic_chevron_right),
            contentDescription = null,
            tint = TextDisabled,
            modifier = Modifier.size(18.dp)
        )
    }
}
