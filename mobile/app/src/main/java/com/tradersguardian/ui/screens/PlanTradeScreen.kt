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
                Text(
                    "Risk Manager",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted
                )
                Text(
                    "Trade Planner",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight    = FontWeight.Bold,
                        letterSpacing = (-0.5).sp
                    ),
                    color = TextPrimary
                )
            }

            // ── Error banner ──────────────────────────────────────────────
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

            // ── TRADE DETAILS section ─────────────────────────────────────
            Text(
                "TRADE DETAILS",
                style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.2.sp),
                color = TextMuted,
                fontWeight = FontWeight.SemiBold
            )

            // Grouped input card (fields separated by dividers)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Surface)
                    .border(1.dp, BorderColor, RoundedCornerShape(16.dp))
            ) {
                // INSTRUMENT
                FieldGroup(label = "INSTRUMENT") {
                    TgTextField(
                        value         = symbol,
                        onValueChange = { viewModel.symbol.value = it.uppercase() },
                        label         = "",
                        placeholder   = "e.g. EUR/USD, AAPL"
                    )
                }
                HorizontalDivider(color = BorderColor, thickness = 0.5.dp)

                // BUY / SELL toggle
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {
                    Text(
                        "DIRECTION",
                        style     = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
                        color     = TextMuted,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("BUY", "SELL").forEach { type ->
                            val isSelected = tradeType == type
                            val bg  = if (isSelected) (if (type == "BUY") SuccessBg else ErrorBg) else Surface2
                            val fg  = if (isSelected) (if (type == "BUY") SuccessGreen else ErrorRed) else TextMuted
                            val bdr = if (isSelected) fg else BorderColor
                            Button(
                                onClick  = { viewModel.tradeType.value = type },
                                modifier = Modifier.weight(1f).height(40.dp),
                                shape    = RoundedCornerShape(8.dp),
                                colors   = ButtonDefaults.buttonColors(containerColor = bg, contentColor = fg),
                                border   = androidx.compose.foundation.BorderStroke(1.dp, bdr),
                                elevation = ButtonDefaults.buttonElevation(0.dp)
                            ) {
                                Text(type, style = MaterialTheme.typography.labelLarge, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
                            }
                        }
                    }
                }
                HorizontalDivider(color = BorderColor, thickness = 0.5.dp)

                // ENTRY PRICE
                FieldGroup(label = "ENTRY PRICE") {
                    TgTextField(
                        value         = entryPrice,
                        onValueChange = { viewModel.entryPrice.value = it },
                        label         = "",
                        placeholder   = "0.00000",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                    )
                }
                HorizontalDivider(color = BorderColor, thickness = 0.5.dp)

                // STOP LOSS
                FieldGroup(label = "STOP LOSS") {
                    TgTextField(
                        value         = stopLoss,
                        onValueChange = { viewModel.stopLoss.value = it },
                        label         = "",
                        placeholder   = "0.00000",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                    )
                }
                HorizontalDivider(color = BorderColor, thickness = 0.5.dp)

                // TAKE PROFIT
                FieldGroup(label = "TAKE PROFIT") {
                    TgTextField(
                        value         = takeProfit,
                        onValueChange = { viewModel.takeProfit.value = it },
                        label         = "",
                        placeholder   = "0.00000",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                    )
                }
            }

            // ── CALCULATED METRICS section ────────────────────────────────
            Text(
                "CALCULATED METRICS",
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
                val riskAmt   = calculated?.riskAmount
                val posSz     = calculated?.positionSize
                val rr        = calculated?.rr

                MetricRow(
                    label = "Risk Amount",
                    value = if (riskAmt != null) "${"$"}${"%.2f".format(riskAmt)}" else "${"$"}${"%.2f".format(settings.accountBalance * settings.riskPerTrade / 100)}",
                    valueColor = AccentCyan
                )
                HorizontalDivider(color = BorderColor, thickness = 0.5.dp)
                MetricRow(
                    label = "Position Size",
                    value = if (posSz != null) "${"%.4f".format(posSz)}" else "—"
                )
                HorizontalDivider(color = BorderColor, thickness = 0.5.dp)
                MetricRow(
                    label = "Risk : Reward",
                    value = if (rr != null) "1:${"%.2f".format(rr)}" else "—",
                    valueColor = if (rr != null && rr >= 2.0) SuccessGreen else if (rr != null) WarningAmber else TextMuted
                )
            }

            // Notes (collapsible) — keep simple
            if (notes.isNotEmpty() || true) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Surface)
                        .border(1.dp, BorderColor, RoundedCornerShape(14.dp))
                        .padding(14.dp)
                ) {
                    Text(
                        "NOTES (OPTIONAL)",
                        style     = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
                        color     = TextMuted,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value           = notes,
                        onValueChange   = { viewModel.notes.value = it },
                        placeholder     = { Text("Trade rationale, confluences…", color = TextDisabled) },
                        minLines        = 2,
                        maxLines        = 4,
                        colors          = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor    = AccentCyan,
                            unfocusedBorderColor  = BorderColor,
                            focusedContainerColor = InputBg,
                            unfocusedContainerColor = InputBg,
                            focusedTextColor      = TextPrimary,
                            unfocusedTextColor    = TextPrimary
                        ),
                        shape    = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // ── Validate / Calculate button ───────────────────────────────
            Button(
                onClick  = {
                    if (calculated == null) viewModel.calculate()
                    else viewModel.submit()
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape    = RoundedCornerShape(14.dp),
                colors   = ButtonDefaults.buttonColors(
                    containerColor = AccentCyan,
                    contentColor   = BgDark
                ),
                enabled  = submitState !is UiState.Loading
            ) {
                if (submitState is UiState.Loading) {
                    CircularProgressIndicator(color = BgDark, strokeWidth = 2.dp, modifier = Modifier.size(20.dp))
                } else {
                    Text(
                        if (calculated == null) "Validate Trade" else "Save Trade Plan",
                        style      = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

// ── Helpers ───────────────────────────────────────────────────────────────────

@Composable
private fun FieldGroup(label: String, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(
            label,
            style     = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
            color     = TextMuted,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.height(4.dp))
        content()
    }
}

@Composable
private fun MetricRow(label: String, value: String, valueColor: androidx.compose.ui.graphics.Color = TextPrimary) {
    Row(
        verticalAlignment   = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 16.dp)
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
        Text(value, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = valueColor)
    }
}
