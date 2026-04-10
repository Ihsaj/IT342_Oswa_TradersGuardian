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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tradersguardian.R
import com.tradersguardian.ui.theme.*

// ── Text Field ───────────────────────────────────────────────────────────────

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
    modifier: Modifier = Modifier
) {
    var passwordVisible by remember { mutableStateOf(false) }
    val isFocused = remember { mutableStateOf(false) }

    val borderColor by animateColorAsState(
        targetValue = when {
            errorMessage != null -> ErrorRed
            isFocused.value      -> AccentCyan
            else                 -> BorderColor
        },
        animationSpec = tween(200), label = "border"
    )

    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = Color(0xFFCCCCCC),
            modifier = Modifier.padding(bottom = 6.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = Color(0xFF555555)) },
            singleLine = true,
            visualTransformation = if (isPassword && !passwordVisible)
                PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            trailingIcon = if (isPassword) {
                {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            painter = painterResource(
                                if (passwordVisible) R.drawable.ic_eye_off else R.drawable.ic_eye
                            ),
                            contentDescription = if (passwordVisible) "Hide" else "Show",
                            tint = if (passwordVisible) AccentCyan else TextMuted
                        )
                    }
                }
            } else null,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor   = AccentCyan,
                unfocusedBorderColor = if (errorMessage != null) ErrorRed else BorderColor,
                errorBorderColor     = ErrorRed,
                focusedContainerColor   = InputBg,
                unfocusedContainerColor = InputBg,
                cursorColor          = AccentCyan,
                focusedTextColor     = TextPrimary,
                unfocusedTextColor   = TextPrimary,
            ),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
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

// ── Primary Button ───────────────────────────────────────────────────────────

@Composable
fun TgButton(
    text: String,
    onClick: () -> Unit,
    isLoading: Boolean = false,
    modifier: Modifier = Modifier,
    containerColor: Color = AccentCyan,
    contentColor: Color = BgDark
) {
    Button(
        onClick = { if (!isLoading) onClick() },
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor   = contentColor,
            disabledContainerColor = containerColor.copy(alpha = 0.7f)
        ),
        enabled = !isLoading
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(22.dp),
                color = contentColor,
                strokeWidth = 2.5.dp
            )
        } else {
            Text(
                text = text,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.3.sp
                )
            )
        }
    }
}

// ── Secondary / Cancel Button ─────────────────────────────────────────────────

@Composable
fun TgOutlinedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(46.dp),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = Surface2,
            contentColor   = TextPrimary
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor)
    ) {
        Text(text, style = MaterialTheme.typography.labelLarge)
    }
}

// ── Logo Badge (PNG replaceable) ─────────────────────────────────────────────
// ✏️ To use your own logo: replace R.drawable.ic_logo_placeholder
//    with your actual drawable resource, e.g. R.drawable.my_logo

@Composable
fun LogoBadge(
    size: Int = 64,
    cornerRadius: Int = 16
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(size.dp)
            .shadow(
                elevation = 16.dp,
                shape = RoundedCornerShape(cornerRadius.dp),
                ambientColor = AccentCyan.copy(alpha = 0.4f),
                spotColor = AccentCyan.copy(alpha = 0.4f)
            )
            .clip(RoundedCornerShape(cornerRadius.dp))
            .background(AccentCyan)
    ) {
        // ✏️ LOGO SLOT — replace with your own image:
        // Image(
        //     painter = painterResource(R.drawable.your_logo),
        //     contentDescription = "Logo",
        //     contentScale = ContentScale.Crop,
        //     modifier = Modifier.fillMaxSize()
        // )
        Icon(
            painter = painterResource(R.drawable.ic_logo_placeholder),
            contentDescription = "Trader's Guardian",
            tint = BgDark,
            modifier = Modifier.size((size * 0.53f).dp)
        )
    }
}

// ── Small Nav Logo Badge ─────────────────────────────────────────────────────

@Composable
fun NavLogoBadge() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(36.dp)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(9.dp),
                ambientColor = AccentCyan.copy(alpha = 0.3f),
                spotColor = AccentCyan.copy(alpha = 0.3f)
            )
            .clip(RoundedCornerShape(9.dp))
            .background(AccentCyan)
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_logo_placeholder),
            contentDescription = "Logo",
            tint = BgDark,
            modifier = Modifier.size(20.dp)
        )
    }
}

// ── Divider with text ────────────────────────────────────────────────────────

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
            modifier = Modifier.padding(horizontal = 12.dp)
        )
        HorizontalDivider(modifier = Modifier.weight(1f), color = BorderColor)
    }
}

// ── Card container ───────────────────────────────────────────────────────────

@Composable
fun TgCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Surface)
            .border(1.dp, BorderColor, RoundedCornerShape(16.dp))
            .padding(32.dp),
        content = content
    )
}

// ── Password Strength Bar ────────────────────────────────────────────────────

@Composable
fun PasswordStrengthBar(strength: Int) {
    val labels = listOf("Enter a password", "Too weak", "Could be stronger", "Getting there", "Strong password")
    val colors = listOf(BorderColor, ErrorRed, WarningOrange, AccentCyan, SuccessGreen)

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            repeat(4) { index ->
                val filled = index < strength
                val color by animateColorAsState(
                    if (filled && strength > 0) colors[strength] else BorderColor,
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
