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
import com.tradersguardian.viewmodel.PlanTradeViewModel

@Composable
fun PlanTradeScreen(
    viewModel: PlanTradeViewModel = viewModel()
) {
    val symbol      by viewModel.symbol.collectAsState()
    val tradeType   by viewModel.tradeType.collectAsState()
    val entryPrice  by viewModel.entryPrice.collectAsState()
    val stopLoss    by viewModel.stopLoss.collectAsState()
    val takeProfit  by viewModel.takeProfit.collectAsState()
    val notes       by viewModel.notes.collectAsState()
    val settings    by viewModel.settings.collectAsState()
    val calculated  by viewModel.calculated.collectAsState()
    val submitState by viewModel.submitState.collectAsState()
    val error       by viewModel.error.collectAsState()

    val snackbar = remember { SnackbarHostState() }

    LaunchedEffect(submitState) {
        when (val s = submitState) {
            is UiState.Success -> {
                snackbar.showSnackbar("Trade plan saved!")
                viewModel.resetForm()
            }
            is UiState.Error -> snackbar.showSnackbar(s.message)
            else -> Unit
        }
    }

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
                Text("Plan New Trade", style = MaterialTheme.typography.headlineLarge.copy(letterSpacing = (-0.5).sp), color = TextPrimary)
                Text("Calculate risk before entering the market", style = MaterialTheme.typography.bodySmall, color = TextMuted, modifier = Modifier.padding(top = 4.dp))
            }

            // Error banner
            if (error != null) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(ErrorBg)
                        .border(1.dp, ErrorRed.copy(0.3f), RoundedCornerShape(10.dp))
                        .padding(14.dp)
                ) {
                    Icon(painterResource(R.drawable.ic_x_circle), null, tint = ErrorRed, modifier = Modifier.size(16.dp))
                    Text(error!!, style = MaterialTheme.typography.bodySmall, color = ErrorRed)
                }
            }

            // Account params card
            TgCard {
                SectionHeader("Account Parameters", R.drawable.ic_dollar)
                InfoRow("Account Balance", "$${"%.2f".format(settings.accountBalance)}")
                InfoRow("Risk Per Trade",  "${settings.riskPerTrade}%", AccentCyan)
                InfoRow("Daily Loss Limit","${settings.dailyLossLimit}%", WarningAmber)
                InfoRow("Max Risk Amount", "$${"%.2f".format(settings.accountBalance * settings.riskPerTrade / 100)}", ErrorRed)
            }

            // Trade Setup card
            TgCard {
                SectionHeader("Trade Setup", R.drawable.ic_trending_up)

                TgTextField(
                    value = symbol,
                    onValueChange = { viewModel.symbol.value = it.uppercase() },
                    label = "Symbol *",
                    placeholder = "e.g. EUR/USD, BTC/USD"
                )
                Spacer(Modifier.height(14.dp))

                // Trade direction toggle
                Text("Direction", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Medium), color = TextSecondary, modifier = Modifier.padding(bottom = 8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("BUY", "SELL").forEach { type ->
                        val isSelected = tradeType == type
                        val bg  = if (isSelected) (if (type == "BUY") SuccessBg else ErrorBg) else Surface2
                        val fg  = if (isSelected) (if (type == "BUY") SuccessGreen else ErrorRed) else TextMuted
                        val bdr = if (isSelected) fg else BorderColor
                        Button(
                            onClick = { viewModel.tradeType.value = type },
                            modifier = Modifier.weight(1f).height(44.dp),
                            shape  = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = bg, contentColor = fg),
                            border = androidx.compose.foundation.BorderStroke(1.dp, bdr),
                            elevation = ButtonDefaults.buttonElevation(0.dp)
                        ) {
                            Text(type, style = MaterialTheme.typography.labelLarge, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
                        }
                    }
                }
                Spacer(Modifier.height(14.dp))

                TgTextField(
                    value = entryPrice,
                    onValueChange = { viewModel.entryPrice.value = it },
                    label = "Entry Price *",
                    placeholder = "0.00",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )
                Spacer(Modifier.height(14.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    TgTextField(
                        value = stopLoss,
                        onValueChange = { viewModel.stopLoss.value = it },
                        label = "Stop Loss *",
                        placeholder = "0.00",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f)
                    )
                    TgTextField(
                        value = takeProfit,
                        onValueChange = { viewModel.takeProfit.value = it },
                        label = "Take Profit *",
                        placeholder = "0.00",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(Modifier.height(14.dp))

                // Notes
                Text("Notes (optional)", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Medium), color = TextSecondary, modifier = Modifier.padding(bottom = 6.dp))
                OutlinedTextField(
                    value = notes,
                    onValueChange = { viewModel.notes.value = it },
                    placeholder = { Text("Trade rationale, confluences…", color = TextDisabled) },
                    minLines = 3,
                    maxLines = 5,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor    = AccentCyan,
                        unfocusedBorderColor  = BorderColor,
                        focusedContainerColor = InputBg,
                        unfocusedContainerColor = InputBg,
                        focusedTextColor      = TextPrimary,
                        unfocusedTextColor    = TextPrimary
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(18.dp))

                // Calculate button
                TgOutlinedButton(
                    text = "Calculate Risk",
                    onClick = { viewModel.calculate() },
                    borderColor = AccentCyan,
                    textColor   = AccentCyan
                )
            }

            // Risk Analysis card
            if (calculated != null) {
                val calc = calculated!!
                val rrColor = when {
                    calc.rr >= 2.0 -> SuccessGreen
                    calc.rr >= 1.0 -> WarningAmber
                    else           -> ErrorRed
                }
                TgCard(borderColor = AccentCyan.copy(0.35f)) {
                    SectionHeader("Risk Analysis", R.drawable.ic_bar_chart)
                    InfoRow("Risk Amount",     "$${"%.2f".format(calc.riskAmount)}", ErrorRed)
                    InfoRow("Position Size",   "${"%.4f".format(calc.positionSize)}")
                    InfoRow("Potential Reward","$${"%.2f".format(calc.rewardAmount)}", SuccessGreen)
                    InfoRow("Risk/Reward",     "1:${"%.2f".format(calc.rr)}", rrColor)
                    Spacer(Modifier.height(14.dp))

                    // R:R verdict
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (calc.rr >= 2.0) SuccessBg else WarningBg)
                            .padding(14.dp)
                    ) {
                        val (icon, msg) = if (calc.rr >= 2.0)
                            R.drawable.ic_check_circle to "Good risk/reward ratio (≥ 1:2)"
                        else
                            R.drawable.ic_x_circle to "Consider improving your R:R ratio"
                        Icon(painterResource(icon), null, tint = rrColor, modifier = Modifier.size(16.dp))
                        Text(msg, style = MaterialTheme.typography.bodySmall, color = rrColor)
                    }
                    Spacer(Modifier.height(18.dp))

                    TgButton(
                        text      = if (submitState is UiState.Loading) "Saving…" else "Save Trade Plan",
                        onClick   = { viewModel.submit() },
                        isLoading = submitState is UiState.Loading
                    )
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}
