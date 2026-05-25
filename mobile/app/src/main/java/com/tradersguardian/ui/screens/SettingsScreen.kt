package com.tradersguardian.ui.screens

import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tradersguardian.R
import com.tradersguardian.data.model.UiState
import com.tradersguardian.ui.components.*
import com.tradersguardian.ui.theme.*
import com.tradersguardian.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = viewModel()
) {
    val loadState      by viewModel.loadState.collectAsState()
    val saveState      by viewModel.saveState.collectAsState()
    val accountBalance by viewModel.accountBalance.collectAsState()
    val riskPerTrade   by viewModel.riskPerTrade.collectAsState()
    val dailyLossLimit by viewModel.dailyLossLimit.collectAsState()

    val snackbar = remember { SnackbarHostState() }

    LaunchedEffect(saveState) {
        when (val s = saveState) {
            is UiState.Success -> { snackbar.showSnackbar("Settings saved!"); viewModel.resetSaveState() }
            is UiState.Error   -> { snackbar.showSnackbar(s.message);          viewModel.resetSaveState() }
            else               -> Unit
        }
    }

    // Live computed values
    val balance  = accountBalance.toDoubleOrNull() ?: 0.0
    val riskPct  = riskPerTrade.toDoubleOrNull()   ?: 0.0
    val dailyPct = dailyLossLimit.toDoubleOrNull() ?: 0.0
    val maxRisk  = balance * riskPct  / 100.0
    val maxDaily = balance * dailyPct / 100.0

    // Today's loss tracker (from load state if available)
    val currentLoss      = 0.0   // will be surfaced from viewmodel when added
    val lossProgress     = if (maxDaily > 0) (currentLoss / maxDaily).toFloat().coerceIn(0f, 1f) else 0f
    val animProgress by animateFloatAsState(
        targetValue   = lossProgress,
        animationSpec = tween(900, easing = EaseOutCubic),
        label         = "lossProgress"
    )
    val progressColor = when {
        lossProgress > 0.8f -> ErrorRed
        lossProgress > 0.5f -> WarningAmber
        else                -> SuccessGreen
    }

    Scaffold(
        containerColor = BgDark,
        snackbarHost   = { TgSnackbarHost(snackbar) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(padding)
                .padding(horizontal = 20.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // ── Header ────────────────────────────────────────────────────
            Column {
                Text("Configuration", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                Text(
                    "Account Settings",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight    = FontWeight.Bold,
                        letterSpacing = (-0.5).sp
                    ),
                    color = TextPrimary
                )
            }

            // ── TODAY'S LOSS TRACKER ──────────────────────────────────────
            Text(
                "TODAY'S LOSS TRACKER",
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
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.Top,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            "${"$"}${"%.2f".format(currentLoss)}",
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimary
                        )
                        Text(
                            "of ${"$"}${"%.2f".format(maxDaily)} limit",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted
                        )
                    }
                    Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            "${"%.1f".format(lossProgress * 100)}%",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = progressColor
                        )
                        Text("used today", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                    }
                }

                LinearProgressIndicator(
                    progress    = { animProgress },
                    modifier    = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(99.dp)),
                    color       = progressColor,
                    trackColor  = Surface2,
                    strokeCap   = StrokeCap.Round
                )

                // Reset button
                Button(
                    onClick  = { /* TODO: reset daily counter */ },
                    modifier = Modifier.fillMaxWidth().height(44.dp),
                    shape    = RoundedCornerShape(10.dp),
                    colors   = ButtonDefaults.buttonColors(
                        containerColor = Surface2,
                        contentColor   = TextSecondary
                    )
                ) {
                    Icon(
                        painterResource(R.drawable.ic_clock),
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Reset Daily Counter",
                        style      = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // ── RISK PARAMETERS ───────────────────────────────────────────
            Text(
                "RISK PARAMETERS",
                style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.2.sp),
                color = TextMuted,
                fontWeight = FontWeight.SemiBold
            )

            when (loadState) {
                is UiState.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Surface),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = AccentCyan, strokeWidth = 2.dp, modifier = Modifier.size(28.dp))
                    }
                }
                is UiState.Error -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Surface)
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text("Failed to load settings", color = ErrorRed, style = MaterialTheme.typography.bodySmall)
                            TgOutlinedButton("Retry", onClick = { viewModel.loadSettings() }, modifier = Modifier.width(120.dp))
                        }
                    }
                }
                else -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Surface)
                            .border(1.dp, BorderColor, RoundedCornerShape(16.dp))
                            .padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        // Account Balance
                        SettingsParamField(
                            iconRes   = R.drawable.ic_dollar,
                            iconBg    = AccentCyan.copy(0.15f),
                            iconTint  = AccentCyan,
                            title     = "Account Balance",
                            subtitle  = "Your total trading capital",
                            prefix    = "$",
                            value     = accountBalance,
                            onValueChange = { viewModel.accountBalance.value = it },
                            placeholder   = "10000"
                        )

                        HorizontalDivider(color = BorderColor, thickness = 0.5.dp)

                        // Risk Per Trade
                        SettingsParamField(
                            iconRes   = R.drawable.ic_bar_chart,
                            iconBg    = SuccessGreen.copy(0.15f),
                            iconTint  = SuccessGreen,
                            title     = "Risk Per Trade",
                            subtitle  = "Max risk: ${"$"}${"%.2f".format(maxRisk)} per trade",
                            prefix    = "%",
                            value     = riskPerTrade,
                            onValueChange = { viewModel.riskPerTrade.value = it },
                            placeholder   = "2"
                        )

                        HorizontalDivider(color = BorderColor, thickness = 0.5.dp)

                        // Daily Loss Limit
                        SettingsParamField(
                            iconRes   = R.drawable.ic_trending_up,
                            iconBg    = WarningAmber.copy(0.15f),
                            iconTint  = WarningAmber,
                            title     = "Daily Loss Limit",
                            subtitle  = "Max daily loss: ${"$"}${"%.2f".format(maxDaily)}",
                            prefix    = "%",
                            value     = dailyLossLimit,
                            onValueChange = { viewModel.dailyLossLimit.value = it },
                            placeholder   = "5"
                        )
                    }

                    // Save button
                    Button(
                        onClick  = { viewModel.save() },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape    = RoundedCornerShape(14.dp),
                        colors   = ButtonDefaults.buttonColors(
                            containerColor = AccentCyan,
                            contentColor   = BgDark
                        ),
                        enabled  = saveState !is UiState.Loading
                    ) {
                        if (saveState is UiState.Loading) {
                            CircularProgressIndicator(color = BgDark, strokeWidth = 2.dp, modifier = Modifier.size(20.dp))
                        } else {
                            Text("Save Settings", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

// ── Settings field row with icon badge ───────────────────────────────────────

@Composable
private fun SettingsParamField(
    iconRes: Int,
    iconBg: Color,
    iconTint: Color,
    title: String,
    subtitle: String,
    prefix: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(iconBg)
            ) {
                Icon(painterResource(iconRes), null, tint = iconTint, modifier = Modifier.size(20.dp))
            }
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(title,    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold), color = TextPrimary)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = TextMuted)
            }
        }

        // Prefixed input
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(InputBg)
                .border(1.dp, BorderColor, RoundedCornerShape(10.dp))
                .padding(horizontal = 16.dp, vertical = 4.dp)
        ) {
            Text(
                "$prefix ",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                color = TextSecondary
            )
            TextField(
                value         = value,
                onValueChange = onValueChange,
                placeholder   = { Text(placeholder, color = TextDisabled) },
                colors        = TextFieldDefaults.colors(
                    focusedContainerColor   = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor   = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor        = TextPrimary,
                    unfocusedTextColor      = TextPrimary,
                    cursorColor             = AccentCyan
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine    = true,
                modifier      = Modifier.fillMaxWidth()
            )
        }
    }
}
