package com.tradersguardian.ui.screens

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
            is UiState.Success -> {
                snackbar.showSnackbar("Settings saved successfully!")
                viewModel.resetSaveState()
            }
            is UiState.Error -> {
                snackbar.showSnackbar(s.message)
                viewModel.resetSaveState()
            }
            else -> Unit
        }
    }

    // Live preview values
    val balance  = accountBalance.toDoubleOrNull() ?: 0.0
    val riskPct  = riskPerTrade.toDoubleOrNull()   ?: 0.0
    val dailyPct = dailyLossLimit.toDoubleOrNull() ?: 0.0
    val maxRisk  = balance * riskPct  / 100.0
    val maxDaily = balance * dailyPct / 100.0

    Scaffold(
        containerColor = BgMid,
        snackbarHost   = { TgSnackbarHost(snackbar) }
    ) { padding ->
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 20.dp)
        ) {
            // Header
            Column {
                Text("Account Settings", style = MaterialTheme.typography.headlineLarge.copy(letterSpacing = (-0.5).sp), color = TextPrimary)
                Text("Configure your risk parameters", style = MaterialTheme.typography.bodySmall, color = TextMuted, modifier = Modifier.padding(top = 4.dp))
            }

            // Settings form
            TgCard {
                SectionHeader("Risk Parameters", R.drawable.ic_settings)

                when (loadState) {
                    is UiState.Loading -> {
                        Box(Modifier.fillMaxWidth().height(180.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = AccentCyan, strokeWidth = 2.dp, modifier = Modifier.size(28.dp))
                        }
                    }
                    is UiState.Error -> {
                        Box(Modifier.fillMaxWidth().height(120.dp), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Text("Failed to load settings", color = ErrorRed, style = MaterialTheme.typography.bodySmall)
                                TgOutlinedButton("Retry", onClick = { viewModel.loadSettings() }, modifier = Modifier.width(120.dp))
                            }
                        }
                    }
                    else -> {
                        Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
                            // Account Balance
                            Column {
                                TgTextField(
                                    value = accountBalance,
                                    onValueChange = { viewModel.accountBalance.value = it },
                                    label = "Account Balance ($)",
                                    placeholder = "10000",
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                                )
                                Text("Your total trading capital for risk calculations.", style = MaterialTheme.typography.bodySmall, color = TextDisabled, modifier = Modifier.padding(top = 5.dp, start = 4.dp))
                            }

                            // Risk Per Trade
                            Column {
                                TgTextField(
                                    value = riskPerTrade,
                                    onValueChange = { viewModel.riskPerTrade.value = it },
                                    label = "Risk Per Trade (%)",
                                    placeholder = "2",
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                                )
                                Text("Maximum % of account to risk on a single trade. Recommended: 1–2%.", style = MaterialTheme.typography.bodySmall, color = TextDisabled, modifier = Modifier.padding(top = 5.dp, start = 4.dp))
                            }

                            // Daily Loss Limit
                            Column {
                                TgTextField(
                                    value = dailyLossLimit,
                                    onValueChange = { viewModel.dailyLossLimit.value = it },
                                    label = "Daily Loss Limit (%)",
                                    placeholder = "5",
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                                )
                                Text("Stop trading when this daily loss threshold is reached. Recommended: 3–6%.", style = MaterialTheme.typography.bodySmall, color = TextDisabled, modifier = Modifier.padding(top = 5.dp, start = 4.dp))
                            }

                            TgButton(
                                text      = if (saveState is UiState.Loading) "Saving…" else "Save Settings",
                                onClick   = { viewModel.save() },
                                isLoading = saveState is UiState.Loading
                            )
                        }
                    }
                }
            }

            // Live Preview card
            TgCard(borderColor = AccentCyan.copy(0.25f)) {
                SectionHeader("Live Preview", R.drawable.ic_bar_chart)
                Column {
                    InfoRow("Account Balance",  "$${"%.2f".format(balance)}")
                    InfoRow("Risk Per Trade",    "$riskPct%", AccentCyan)
                    InfoRow("Max Risk Amount",   "$${"%.2f".format(maxRisk)}", ErrorRed)
                    InfoRow("Daily Loss Limit",  "$dailyPct%", WarningAmber)
                    InfoRow("Max Daily Loss",    "$${"%.2f".format(maxDaily)}", WarningAmber)
                }
            }

            // Risk Guidelines card
            TgCard {
                SectionHeader("💡 Risk Guidelines", R.drawable.ic_check_circle)
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    listOf(
                        "Risk 1–2% per trade (professional standard)",
                        "Never risk more than 5% in one day",
                        "A 2% risk with 1:2 R:R = 4% potential reward",
                        "Consistent small risks protect your capital long term"
                    ).forEach { tip ->
                        Row(
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .offset(y = 5.dp)
                                    .clip(RoundedCornerShape(50))
                                    .background(AccentCyan)
                            )
                            Text(tip, style = MaterialTheme.typography.bodySmall, color = TextMuted, lineHeight = 18.sp)
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}
