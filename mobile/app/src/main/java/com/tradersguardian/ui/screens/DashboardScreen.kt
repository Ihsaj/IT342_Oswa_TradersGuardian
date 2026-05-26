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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tradersguardian.R
import com.tradersguardian.data.model.*
import com.tradersguardian.ui.components.*
import com.tradersguardian.ui.theme.*
import com.tradersguardian.viewmodel.DashboardViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun DashboardScreen(
    onNavigate: (String) -> Unit,
    viewModel: DashboardViewModel = viewModel()
) {
    val dashboardState by viewModel.dashboardState.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.loadDashboard()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    Scaffold(
        containerColor = BgMid,
        topBar = { DashboardTopBar() }
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
private fun DashboardTopBar() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(BgMid)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .height(48.dp)
                .padding(horizontal = 20.dp)
        ) {
            NavLogoBadge()
            Spacer(Modifier.width(10.dp))
            Text(
                "Trader's Guardian",
                style      = MaterialTheme.typography.titleSmall,
                color      = TextPrimary,
                fontWeight = FontWeight.Bold
            )
        }
        HorizontalDivider(color = BorderColor.copy(alpha = 0.4f), thickness = 0.5.dp)
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

    val balance      = s.accountBalance
    val riskPct      = s.riskPerTrade
    val lossLimitPct = s.dailyLossLimit
    val lossLimitAmt = balance * lossLimitPct / 100.0
    val currLossAmt  = s.currentDailyLoss // Now stores actual dollar amount of losses
    val progress     = if (lossLimitAmt > 0) (currLossAmt / lossLimitAmt).toFloat().coerceIn(0f, 1f) else 0f
    val winRate      = if (t.winTrades + t.lossTrades > 0) t.winTrades.toFloat() / (t.winTrades + t.lossTrades).toFloat() else 0f

    val progressColor = when {
        progress > 0.8f -> ErrorRed
        progress > 0.5f -> WarningAmber
        else            -> AccentCyan
    }

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
        // ── Welcome card ──────────────────────────────────────────────────────
        WelcomeCard(wins = t.winTrades, losses = t.lossTrades, winRate = winRate)

        // ── Plan CTA (gradient) ───────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Brush.horizontalGradient(listOf(AccentCyan, AccentCyanDim)))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication        = null,
                    onClick           = { onNavigate("plan-trade") }
                ),
            contentAlignment = Alignment.Center
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Icon(painterResource(R.drawable.ic_trending_up), null, tint = BgDark, modifier = Modifier.size(18.dp))
                Text("Plan New Trade", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = BgDark)
            }
        }

        // ── Account Summary ───────────────────────────────────────────────────
        TgCard {
            SectionHeader("Account Summary", R.drawable.ic_dollar)

            // 2×2 chip grid
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                StatChip("Account Balance", "$${"%.0f".format(balance)}", TextPrimary,   R.drawable.ic_dollar,       AccentCyan,   Modifier.weight(1f))
                StatChip("Risk Per Trade",  "$riskPct%",                  AccentCyan,    R.drawable.ic_trending_up,  AccentCyan,   Modifier.weight(1f))
            }
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                StatChip("Daily Limit",   "$${"%.0f".format(lossLimitAmt)}",   WarningAmber,                                 R.drawable.ic_clock,    WarningAmber, Modifier.weight(1f))
                StatChip("Current Loss",  "$${"%.2f".format(currLossAmt)}",   if (progress > 0.75f) ErrorRed else TextPrimary, R.drawable.ic_x_circle, if (progress > 0.75f) ErrorRed else TextMuted, Modifier.weight(1f))
            }
            Spacer(Modifier.height(20.dp))

            // Progress header row
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically,
                modifier              = Modifier.fillMaxWidth().padding(bottom = 10.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text("Daily Loss Progress", color = TextMuted, style = MaterialTheme.typography.bodySmall)
                    Text(
                        "$${"%.2f".format(currLossAmt)} / $${"%.2f".format(lossLimitAmt)}",
                        color = TextDisabled,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(progressColor.copy(alpha = 0.15f))
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text(
                        "${"%.0f".format(animatedProgress * 100)}%",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = progressColor
                    )
                }
            }
            LinearProgressIndicator(
                progress   = { animatedProgress },
                modifier   = Modifier.fillMaxWidth().height(10.dp).clip(RoundedCornerShape(99.dp)),
                color      = progressColor,
                trackColor = Surface2,
                strokeCap  = StrokeCap.Round
            )
            if (progress > 0.8f) {
                Spacer(Modifier.height(10.dp))
                Row(
                    verticalAlignment     = Alignment.CenterVertically,
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
                StatRow("Total Trades",  "${t.totalTrades}",       R.drawable.ic_trending_up,  AccentCyan.copy(0.12f), AccentCyan,   AccentCyan)
                HorizontalDivider(color = BorderColor, thickness = 0.5.dp)
                StatRow("Approved",      "${t.approvedTrades}",    R.drawable.ic_check_circle, SuccessBg,              SuccessGreen, SuccessGreen)
                HorizontalDivider(color = BorderColor, thickness = 0.5.dp)
                StatRow("Disapproved",   "${t.disapprovedTrades}", R.drawable.ic_x_circle,     ErrorBg,                ErrorRed,     ErrorRed)
                HorizontalDivider(color = BorderColor, thickness = 0.5.dp)
                StatRow(
                    label      = "Win Rate",
                    value      = if (t.totalTrades > 0) "${"%.0f".format(winRate * 100)}%" else "—",
                    iconRes    = R.drawable.ic_check_circle,
                    iconBg     = if (winRate >= 0.5f) SuccessBg    else WarningBg,
                    iconTint   = if (winRate >= 0.5f) SuccessGreen else WarningAmber,
                    valueColor = if (winRate >= 0.5f) SuccessGreen else WarningAmber
                )
            }
        }

        // ── Quick Actions ─────────────────────────────────────────────────────
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            Text("Quick Actions", style = MaterialTheme.typography.titleSmall, color = TextSecondary)
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(Surface2)
                    .border(1.dp, BorderColor, RoundedCornerShape(6.dp))
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text("3 shortcuts", style = MaterialTheme.typography.labelSmall, color = TextMuted)
            }
        }
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            ActionCard("Plan New Trade",   "Validate your trade before entering", R.drawable.ic_trending_up, AccentCyan)   { onNavigate("plan-trade") }
            ActionCard("Trade History",    "Review your past trading decisions",  R.drawable.ic_clock,       PendingBlue)  { onNavigate("history") }
            ActionCard("Account Settings", "Configure your risk parameters",      R.drawable.ic_settings,    WarningAmber) { onNavigate("settings") }
        }

        Spacer(Modifier.height(16.dp))
    }
}

// ── Welcome card ──────────────────────────────────────────────────────────────

@Composable
private fun WelcomeCard(wins: Long, losses: Long, winRate: Float) {
    var currentTime by remember { mutableStateOf(Calendar.getInstance().timeInMillis) }
    
    LaunchedEffect(Unit) {
        while (true) {
            kotlinx.coroutines.delay(60000) // Update every minute
            currentTime = Calendar.getInstance().timeInMillis
        }
    }
    
    val calendar = Calendar.getInstance().apply { timeInMillis = currentTime }
    val hour     = calendar.get(Calendar.HOUR_OF_DAY)
    val greeting = when {
        hour < 12 -> "Good Morning"
        hour < 17 -> "Good Afternoon"
        else      -> "Good Evening"
    }
    val today    = SimpleDateFormat("EEEE, MMM d", Locale.getDefault()).format(calendar.time)
    val winColor = if (winRate >= 0.5f) SuccessGreen else WarningAmber
    val winBg    = if (winRate >= 0.5f) SuccessBg    else WarningBg
    val lossRate = if (wins + losses > 0) losses.toFloat() / (wins + losses).toFloat() else 0f

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Brush.linearGradient(listOf(AccentCyan.copy(alpha = 0.07f), Surface)))
            .border(1.dp, AccentCyan.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
    ) {
        Row(
            modifier              = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 18.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(greeting, style = MaterialTheme.typography.labelSmall, color = AccentCyan)
                Text("Trader", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = TextPrimary)
                Spacer(Modifier.height(8.dp))
                Text(today, style = MaterialTheme.typography.labelSmall, color = TextDisabled)
            }
            // Win and Loss rate circles
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(winBg)
                            .border(2.dp, winColor.copy(alpha = 0.5f), CircleShape)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                if (winRate > 0f) "${"%.0f".format(winRate * 100)}%" else "—",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = winColor
                            )
                            Text("Win", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                        }
                    }
                    Text("Win Rate", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(ErrorBg)
                            .border(2.dp, ErrorRed.copy(alpha = 0.5f), CircleShape)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                if (lossRate > 0f) "${"%.0f".format(lossRate * 100)}%" else "—",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = ErrorRed
                            )
                            Text("Loss", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                        }
                    }
                    Text("Loss Rate", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                }
            }
        }
    }
}

// ── Stat chip ─────────────────────────────────────────────────────────────────

@Composable
private fun StatChip(
    label: String,
    value: String,
    valueColor: Color,
    iconRes: Int,
    iconTint: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Surface2)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(painterResource(iconRes), contentDescription = null, tint = iconTint, modifier = Modifier.size(16.dp))
        Text(label, style = MaterialTheme.typography.labelSmall, color = TextMuted)
        Text(value, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = valueColor)
    }
}

// ── Stat row ──────────────────────────────────────────────────────────────────

@Composable
private fun StatRow(
    label: String, value: String,
    iconRes: Int, iconBg: Color, iconTint: Color, valueColor: Color
) {
    Row(
        verticalAlignment     = Alignment.CenterVertically,
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
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Surface)
            .border(1.dp, BorderColor, RoundedCornerShape(14.dp))
            .height(IntrinsicSize.Min)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication        = null,
                onClick           = onClick
            )
    ) {
        Box(
            modifier = Modifier
                .width(3.dp)
                .fillMaxHeight()
                .clip(RoundedCornerShape(topStart = 14.dp, bottomStart = 14.dp))
                .background(iconTint.copy(alpha = 0.6f))
        )
        Row(
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            modifier              = Modifier.padding(start = 19.dp, end = 16.dp, top = 16.dp, bottom = 16.dp)
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
                tint               = TextDisabled,
                modifier           = Modifier.size(18.dp)
            )
        }
    }
}
