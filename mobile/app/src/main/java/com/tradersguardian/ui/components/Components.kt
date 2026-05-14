package com.tradersguardian.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tradersguardian.R
import com.tradersguardian.ui.theme.*

// ── Text Field ────────────────────────────────────────────────────────────────

@Composable
fun TgTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String = "",
    isPassword: Boolean = false,
    errorMessage: String? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    modifier: Modifier = Modifier,
    trailingContent: (@Composable () -> Unit)? = null
) {
    var passwordVisible by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Medium),
            color = TextSecondary,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = TextDisabled, style = MaterialTheme.typography.bodyMedium) },
            singleLine = true,
            visualTransformation = if (isPassword && !passwordVisible)
                PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            trailingIcon = when {
                isPassword -> {
                    {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                painter = painterResource(
                                    if (passwordVisible) R.drawable.ic_eye_off else R.drawable.ic_eye
                                ),
                                contentDescription = if (passwordVisible) "Hide" else "Show",
                                tint = if (passwordVisible) AccentCyan else TextMuted,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
                trailingContent != null -> trailingContent
                else -> null
            },
            isError = errorMessage != null,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor    = AccentCyan,
                unfocusedBorderColor  = if (errorMessage != null) ErrorRed else BorderColor,
                errorBorderColor      = ErrorRed,
                focusedContainerColor    = InputBg,
                unfocusedContainerColor  = InputBg,
                errorContainerColor      = InputBg,
                cursorColor           = AccentCyan,
                focusedTextColor      = TextPrimary,
                unfocusedTextColor    = TextPrimary,
                errorTextColor        = TextPrimary,
                focusedLabelColor     = AccentCyan,
                unfocusedLabelColor   = TextMuted,
            ),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        )
        if (errorMessage != null) {
            Text(
                text = errorMessage,
                color = ErrorRed,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 4.dp, start = 4.dp)
            )
        }
    }
}

// ── Primary CTA Button ────────────────────────────────────────────────────────

@Composable
fun TgButton(
    text: String,
    onClick: () -> Unit,
    isLoading: Boolean = false,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
    containerColor: Color = AccentCyan,
    contentColor: Color = BgDark
) {
    Button(
        onClick = { if (!isLoading && enabled) onClick() },
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor         = containerColor,
            contentColor           = contentColor,
            disabledContainerColor = containerColor.copy(alpha = 0.4f),
            disabledContentColor   = contentColor.copy(alpha = 0.4f)
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 0.dp,
            pressedElevation = 0.dp
        ),
        enabled = !isLoading && enabled
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = contentColor,
                strokeWidth = 2.dp
            )
        } else {
            Text(
                text = text,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.2.sp
                )
            )
        }
    }
}

// ── Outlined Secondary Button ─────────────────────────────────────────────────

@Composable
fun TgOutlinedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    borderColor: Color = BorderColor,
    textColor: Color = TextSecondary
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = Surface2,
            contentColor   = textColor
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor)
    ) {
        Text(text, style = MaterialTheme.typography.labelLarge)
    }
}

// ── Card container ────────────────────────────────────────────────────────────

@Composable
fun TgCard(
    modifier: Modifier = Modifier,
    borderColor: Color = BorderColor,
    padding: Dp = 24.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Surface)
            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
            .padding(padding),
        content = content
    )
}

// ── Stat Badge ────────────────────────────────────────────────────────────────

@Composable
fun StatBadge(
    label: String,
    value: String,
    valueColor: Color = TextPrimary,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = TextMuted
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = valueColor
        )
    }
}

// ── Logo Badge ────────────────────────────────────────────────────────────────

@Composable
fun LogoBadge(size: Int = 64, cornerRadius: Int = 16) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(size.dp)
            .shadow(
                elevation   = 20.dp,
                shape       = RoundedCornerShape(cornerRadius.dp),
                ambientColor= AccentCyan.copy(alpha = 0.35f),
                spotColor   = AccentCyan.copy(alpha = 0.35f)
            )
            .clip(RoundedCornerShape(cornerRadius.dp))
            .background(AccentCyan)
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_logo_placeholder),
            contentDescription = "Trader's Guardian",
            tint = BgDark,
            modifier = Modifier.size((size * 0.53f).dp)
        )
    }
}

// ── Small Nav Logo Badge ──────────────────────────────────────────────────────

@Composable
fun NavLogoBadge() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(32.dp)
            .shadow(8.dp, RoundedCornerShape(8.dp), ambientColor = AccentCyan.copy(0.25f), spotColor = AccentCyan.copy(0.25f))
            .clip(RoundedCornerShape(8.dp))
            .background(AccentCyan)
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_logo_placeholder),
            contentDescription = "Logo",
            tint = BgDark,
            modifier = Modifier.size(18.dp)
        )
    }
}

// ── Divider with text ─────────────────────────────────────────────────────────

@Composable
fun TgDivider(text: String = "or") {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        HorizontalDivider(modifier = Modifier.weight(1f), color = BorderColor)
        Text(
            text = text,
            color = TextMuted,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(horizontal = 14.dp)
        )
        HorizontalDivider(modifier = Modifier.weight(1f), color = BorderColor)
    }
}

// ── Password Strength Bar ─────────────────────────────────────────────────────

@Composable
fun PasswordStrengthBar(strength: Int) {
    val labels = listOf("Enter a password", "Too weak", "Could be stronger", "Getting there", "Strong ✓")
    val colors = listOf(BorderColor, ErrorRed, WarningAmber, AccentCyan, SuccessGreen)

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            repeat(4) { index ->
                val color by animateColorAsState(
                    if (index < strength && strength > 0) colors[strength] else BorderColor,
                    animationSpec = tween(300), label = "bar$index"
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(3.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(color)
                )
            }
        }
        Text(
            text = if (strength == 0) labels[0] else labels[strength],
            color = if (strength > 0) colors[strength] else TextMuted,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

// ── Status Chip ───────────────────────────────────────────────────────────────

@Composable
fun StatusChip(status: String) {
    val (bg, fg, label) = when (status.uppercase()) {
        "APPROVED"    -> Triple(SuccessBg, SuccessGreen, "Approved")
        "DISAPPROVED" -> Triple(ErrorBg,   ErrorRed,     "Rejected")
        else          -> Triple(PendingBg, PendingBlue,  "Pending")
    }
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bg)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
            color = fg
        )
    }
}

// ── Trade Type Chip ───────────────────────────────────────────────────────────

@Composable
fun TradeTypeChip(type: String) {
    val (bg, fg) = if (type.uppercase() == "BUY")
        SuccessBg to SuccessGreen else ErrorBg to ErrorRed
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bg)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = type.uppercase(),
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = fg
        )
    }
}

// ── Info Row ──────────────────────────────────────────────────────────────────

@Composable
fun InfoRow(label: String, value: String, valueColor: Color = TextPrimary) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = TextMuted)
        Text(value, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold), color = valueColor)
    }
    HorizontalDivider(color = BorderColor, thickness = 0.5.dp)
}

// ── Section Header ────────────────────────────────────────────────────────────

@Composable
fun SectionHeader(title: String, iconRes: Int) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.padding(bottom = 18.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(34.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(AccentCyan.copy(alpha = 0.12f))
        ) {
            Icon(
                painter = painterResource(iconRes),
                contentDescription = null,
                tint = AccentCyan,
                modifier = Modifier.size(18.dp)
            )
        }
        Text(title, style = MaterialTheme.typography.titleSmall, color = TextPrimary)
    }
}

// ── Snackbar wrapper ──────────────────────────────────────────────────────────

@Composable
fun TgSnackbarHost(hostState: SnackbarHostState) {
    SnackbarHost(hostState) { data ->
        Snackbar(
            snackbarData   = data,
            containerColor = Surface,
            contentColor   = TextPrimary,
            actionColor    = AccentCyan,
            shape          = RoundedCornerShape(10.dp),
            modifier       = Modifier.padding(bottom = 80.dp)   // above bottom nav
        )
    }
}
