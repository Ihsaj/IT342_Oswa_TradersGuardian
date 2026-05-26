package com.tradersguardian.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tradersguardian.R
import com.tradersguardian.data.model.TradePlan
import com.tradersguardian.data.model.UiState
import com.tradersguardian.ui.components.*
import com.tradersguardian.ui.theme.*
import com.tradersguardian.viewmodel.HistoryViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel = viewModel()
) {
    val tradesState   by viewModel.trades.collectAsState()
    val filter        by viewModel.filterStatus.collectAsState()
    val actionLoading by viewModel.actionLoading.collectAsState()
    val actionError   by viewModel.actionError.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) { viewModel.loadTrades() }

    var disapproveId     by remember { mutableStateOf<Long?>(null) }
    var disapproveReason by remember { mutableStateOf("") }
    var deleteId         by remember { mutableStateOf<Long?>(null) }
    var outcomeId        by remember { mutableStateOf<Long?>(null) }
    var lossAmount       by remember { mutableStateOf("") }

    // Show snackbar for action errors
    LaunchedEffect(actionError) {
        actionError?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearActionError()
        }
    }

    // Disapprove dialog
    if (disapproveId != null) {
        Dialog(onDismissRequest = { disapproveId = null; disapproveReason = "" }) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Surface)
                    .border(1.dp, BorderColor, RoundedCornerShape(20.dp))
                    .padding(24.dp)
            ) {
                Text("Reject Trade", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                OutlinedTextField(
                    value = disapproveReason,
                    onValueChange = { disapproveReason = it },
                    placeholder = { Text("Reason (optional)", color = TextDisabled) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AccentCyan, unfocusedBorderColor = BorderColor,
                        focusedContainerColor = InputBg, unfocusedContainerColor = InputBg,
                        focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    TgOutlinedButton("Cancel", onClick = { disapproveId = null; disapproveReason = "" }, modifier = Modifier.weight(1f))
                    Button(
                        onClick = {
                            viewModel.disapprove(disapproveId!!, disapproveReason)
                            disapproveId = null; disapproveReason = ""
                        },
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape  = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ErrorRed, contentColor = androidx.compose.ui.graphics.Color.White)
                    ) { Text("Reject", fontWeight = FontWeight.Bold) }
                }
            }
        }
    }

    // Delete confirm dialog
    if (deleteId != null) {
        AlertDialog(
            onDismissRequest = { deleteId = null },
            containerColor   = Surface,
            shape            = RoundedCornerShape(20.dp),
            title  = { Text("Delete Trade Plan?", color = TextPrimary) },
            text   = { Text("This action cannot be undone.", color = TextMuted) },
            confirmButton = {
                Button(
                    onClick = { viewModel.delete(deleteId!!); deleteId = null },
                    colors  = ButtonDefaults.buttonColors(containerColor = ErrorRed)
                ) { Text("Delete", color = androidx.compose.ui.graphics.Color.White) }
            },
            dismissButton = {
                TgOutlinedButton("Cancel", onClick = { deleteId = null }, modifier = Modifier.width(100.dp))
            }
        )
    }

    // Loss amount dialog
    if (outcomeId != null) {
        Dialog(onDismissRequest = { outcomeId = null; lossAmount = "" }) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Surface)
                    .border(1.dp, BorderColor, RoundedCornerShape(20.dp))
                    .padding(24.dp)
            ) {
                Text("Record Loss", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                Text("Enter the loss amount", style = MaterialTheme.typography.bodyMedium, color = TextMuted)

                OutlinedTextField(
                    value = lossAmount,
                    onValueChange = { lossAmount = it },
                    placeholder = { Text("Loss amount ($)", color = TextDisabled) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AccentCyan, unfocusedBorderColor = BorderColor,
                        focusedContainerColor = InputBg, unfocusedContainerColor = InputBg,
                        focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    TgOutlinedButton("Cancel", onClick = { outcomeId = null; lossAmount = "" }, modifier = Modifier.weight(1f))
                    Button(
                        onClick = {
                            val amount = lossAmount.toDoubleOrNull()
                            val lossAmt = if (amount != null) -Math.abs(amount) else null
                            viewModel.recordOutcome(outcomeId!!, "LOSS", lossAmt)
                            outcomeId = null; lossAmount = ""
                        },
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape  = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ErrorRed, contentColor = androidx.compose.ui.graphics.Color.White),
                        enabled = lossAmount.isNotBlank()
                    ) { Text("Save", fontWeight = FontWeight.Bold) }
                }
            }
        }
    }

    Scaffold(
        containerColor = BgMid,
        snackbarHost = { TgSnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(Modifier.height(20.dp))
            // Header
            Text("Trade History", style = MaterialTheme.typography.headlineLarge.copy(letterSpacing = (-0.5).sp), color = TextPrimary)
            Text("Review and manage your past decisions", style = MaterialTheme.typography.bodySmall, color = TextMuted, modifier = Modifier.padding(top = 4.dp, bottom = 16.dp))

            // Filter tabs
            val filters = listOf("ALL", "PENDING", "APPROVED", "DISAPPROVED")
            ScrollableTabRow(
                selectedTabIndex = filters.indexOf(filter),
                containerColor   = BgMid,
                contentColor     = AccentCyan,
                edgePadding      = 0.dp,
                indicator = { tabPositions ->
                    val idx = filters.indexOf(filter)
                    if (idx in tabPositions.indices) {
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[idx]),
                            color    = AccentCyan
                        )
                    }
                },
                divider = { HorizontalDivider(color = BorderColor) }
            ) {
                filters.forEach { f ->
                    Tab(
                        selected = filter == f,
                        onClick  = { viewModel.filterStatus.value = f },
                        text = {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    f.lowercase().replaceFirstChar { it.uppercase() },
                                    style = MaterialTheme.typography.labelLarge,
                                    color = if (filter == f) AccentCyan else TextMuted
                                )
                                when (val state = tradesState) {
                                    is UiState.Success -> {
                                        val count = when (f) {
                                            "ALL" -> state.data.count { it.status.uppercase() != "PENDING" }
                                            else -> state.data.count { it.status.uppercase() == f }
                                        }
                                        if (count > 0) {
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(10.dp))
                                                    .background(if (filter == f) AccentCyan.copy(alpha = 0.2f) else Surface2)
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Text(
                                                    count.toString(),
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = if (filter == f) AccentCyan else TextMuted
                                                )
                                            }
                                        }
                                    }
                                    else -> Unit
                                }
                            }
                        }
                    )
                }
            }
            Spacer(Modifier.height(16.dp))

            // Content
            when (val state = tradesState) {
                is UiState.Loading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = AccentCyan, strokeWidth = 2.5.dp)
                    }
                }
                is UiState.Error -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(14.dp)) {
                            Text(state.message, color = ErrorRed, style = MaterialTheme.typography.bodyMedium)
                            TgButton("Retry", { viewModel.loadTrades() }, modifier = Modifier.width(130.dp))
                        }
                    }
                }
                is UiState.Success -> {
                    val all      = state.data
                    val filtered = when (filter) {
                        "ALL" -> all.filter { it.status.uppercase() != "PENDING" }
                        else -> all.filter { it.status.uppercase() == filter }
                    }

                    if (filtered.isEmpty()) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Icon(painterResource(R.drawable.ic_clock), null, tint = TextDisabled, modifier = Modifier.size(48.dp))
                                Text("No trade plans found", color = TextMuted, style = MaterialTheme.typography.bodyMedium)
                                Text("Filter: $filter", color = TextDisabled, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    } else {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            items(filtered, key = { it.id }) { trade ->
                                TradeCard(
                                    trade         = trade,
                                    actionLoading = actionLoading,
                                    onApprove     = { viewModel.approve(trade.id) },
                                    onDisapprove  = { disapproveId = trade.id },
                                    onDelete      = { deleteId = trade.id },
                                    onWinClick    = { viewModel.recordOutcome(trade.id, "WIN", null) },
                                    onLossClick   = { outcomeId = trade.id; lossAmount = "" }
                                )
                            }
                            item { Spacer(Modifier.height(16.dp)) }
                        }
                    }
                }
                else -> Unit
            }
        }
    }
}

@Composable
private fun TradeCard(
    trade: TradePlan,
    actionLoading: Long?,
    onApprove: () -> Unit,
    onDisapprove: () -> Unit,
    onDelete: () -> Unit,
    onWinClick: () -> Unit = {},
    onLossClick: () -> Unit = {}
) {
    val isLoading = actionLoading == trade.id
    val dateStr = try {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val d   = sdf.parse(trade.createdAt)
        SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(d ?: Date())
    } catch (e: Exception) { trade.createdAt.take(10) }

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Surface)
            .border(1.dp, BorderColor, RoundedCornerShape(14.dp))
            .padding(16.dp)
    ) {
        // Header row
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                trade.symbol.uppercase(),
                style = MaterialTheme.typography.titleSmall,
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            TradeTypeChip(trade.tradeType)
            if (trade.outcome != null) {
                val outcomeLabel = when {
                    trade.outcome == "LOSS" && trade.profitLossAmount != null ->
                        "Loss: $${"%.2f".format(Math.abs(trade.profitLossAmount))}"
                    else -> trade.outcome.lowercase().replaceFirstChar { it.uppercase() }
                }
                val outcomeBg = if (trade.outcome == "WIN") SuccessBg else ErrorBg
                val outcomeFg = if (trade.outcome == "WIN") SuccessGreen else ErrorRed
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(outcomeBg)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        outcomeLabel,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = outcomeFg
                    )
                }
            } else {
                StatusChip(trade.status)
            }
        }

        HorizontalDivider(color = BorderColor, thickness = 0.5.dp)

        // Price details
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            PriceItem("Entry",  trade.entryPrice.toString(), TextPrimary)
            PriceItem("Stop Loss",  trade.stopLoss.toString(), ErrorRed)
            PriceItem("Take Profit",trade.takeProfit.toString(), SuccessGreen)
        }

        // Risk row
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            PriceItem("Risk", "${"%.1f".format(trade.riskPercent)}% / $${"%.2f".format(trade.riskAmount)}", WarningAmber)
            PriceItem("Pos. Size", "${"%.4f".format(trade.positionSize)}", TextMuted)
            Text(dateStr, style = MaterialTheme.typography.labelSmall, color = TextDisabled)
        }

        if (!trade.notes.isNullOrBlank()) {
            Text(
                "📝 ${trade.notes}",
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Surface2)
                    .padding(10.dp)
            )
        }

        if (trade.status.uppercase() == "DISAPPROVED" && !trade.disapprovalReason.isNullOrBlank()) {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(ErrorBg)
                    .border(1.dp, ErrorRed.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                    .padding(10.dp)
            ) {
                Text(
                    "Rejection Reason",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = ErrorRed
                )
                Text(
                    trade.disapprovalReason,
                    style = MaterialTheme.typography.bodySmall,
                    color = ErrorRed.copy(alpha = 0.85f)
                )
            }
        }

        // Actions
        if (isLoading) {
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(Modifier.size(22.dp), color = AccentCyan, strokeWidth = 2.dp)
            }
        } else {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (trade.status.uppercase() == "PENDING") {
                    Button(
                        onClick = onApprove,
                        modifier = Modifier.weight(1f).height(38.dp),
                        shape  = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SuccessBg, contentColor = SuccessGreen),
                        elevation = ButtonDefaults.buttonElevation(0.dp)
                    ) { Text("Approve", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold) }

                    Button(
                        onClick = onDisapprove,
                        modifier = Modifier.weight(1f).height(38.dp),
                        shape  = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ErrorBg, contentColor = ErrorRed),
                        elevation = ButtonDefaults.buttonElevation(0.dp)
                    ) { Text("Reject", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold) }
                } else if (trade.status.uppercase() == "APPROVED" && trade.outcome == null) {
                    Button(
                        onClick = onWinClick,
                        modifier = Modifier.weight(1f).height(38.dp),
                        shape  = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SuccessBg, contentColor = SuccessGreen),
                        elevation = ButtonDefaults.buttonElevation(0.dp)
                    ) { Text("Win", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold) }

                    Button(
                        onClick = onLossClick,
                        modifier = Modifier.weight(1f).height(38.dp),
                        shape  = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ErrorBg, contentColor = ErrorRed),
                        elevation = ButtonDefaults.buttonElevation(0.dp)
                    ) { Text("Loss", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold) }
                }
                OutlinedButton(
                    onClick = onDelete,
                    modifier = Modifier.height(38.dp),
                    shape  = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextMuted),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor)
                ) { Text("Delete", style = MaterialTheme.typography.labelLarge) }
            }
        }
    }
}

@Composable
private fun PriceItem(label: String, value: String, valueColor: androidx.compose.ui.graphics.Color) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = TextDisabled)
        Text(value, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold), color = valueColor)
    }
}
