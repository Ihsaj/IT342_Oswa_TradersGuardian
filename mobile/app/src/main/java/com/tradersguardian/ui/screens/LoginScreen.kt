package com.tradersguardian.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tradersguardian.data.model.UiState
import com.tradersguardian.ui.components.*
import com.tradersguardian.ui.theme.*
import com.tradersguardian.viewmodel.LoginViewModel
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit,
    viewModel: LoginViewModel = viewModel()
) {
    val uiState       by viewModel.uiState.collectAsState()
    val email         by viewModel.email.collectAsState()
    val password      by viewModel.password.collectAsState()
    val emailError    by viewModel.emailError.collectAsState()
    val passwordError by viewModel.passwordError.collectAsState()
    val focusManager  = LocalFocusManager.current
    val snackbar      = remember { SnackbarHostState() }

    LaunchedEffect(uiState) {
        when (val s = uiState) {
            is UiState.Success -> onLoginSuccess()
            is UiState.Error   -> snackbar.showSnackbar(s.message)
            else               -> Unit
        }
    }

    Scaffold(
        containerColor = BgDark,
        snackbarHost   = { TgSnackbarHost(snackbar) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .drawBehind {
                    // Radial cyan glow behind the card
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                AccentCyan.copy(alpha = 0.09f),
                                Color.Transparent
                            ),
                            center = Offset(size.width / 2f, size.height * 0.38f),
                            radius = size.width * 0.85f
                        ),
                        radius = size.width * 0.85f,
                        center = Offset(size.width / 2f, size.height * 0.38f)
                    )
                }
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 56.dp),
                verticalArrangement = Arrangement.Center
            ) {
                // ── Brand ──────────────────────────────────────────────────────
                LogoBadge(size = 72, cornerRadius = 18)
                Spacer(Modifier.height(16.dp))
                Text(
                    text  = "Trader's Guardian",
                    style = MaterialTheme.typography.headlineLarge.copy(letterSpacing = (-0.5).sp),
                    color = TextPrimary
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text  = "Sign in to your trading account",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextMuted
                )
                Spacer(Modifier.height(32.dp))

                // ── Card ───────────────────────────────────────────────────────
                TgCard {
                    TgTextField(
                        value = email,
                        onValueChange = { viewModel.email.value = it },
                        label = "Email",
                        placeholder = "trader@example.com",
                        errorMessage = emailError,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction    = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        )
                    )
                    Spacer(Modifier.height(14.dp))

                    TgTextField(
                        value = password,
                        onValueChange = { viewModel.password.value = it },
                        label = "Password",
                        placeholder = "••••••••",
                        isPassword = true,
                        errorMessage = passwordError,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction    = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { focusManager.clearFocus(); viewModel.login() }
                        )
                    )

                    // Forgot password
                    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                        TextButton(onClick = { /* TODO */ }, contentPadding = PaddingValues(vertical = 4.dp)) {
                            Text(
                                "Forgot password?",
                                color = AccentCyan,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                    Spacer(Modifier.height(4.dp))

                    TgButton(
                        text      = "Login",
                        onClick   = { focusManager.clearFocus(); viewModel.login() },
                        isLoading = uiState is UiState.Loading
                    )
                    Spacer(Modifier.height(20.dp))
                    TgDivider()
                    Spacer(Modifier.height(16.dp))

                    TextButton(
                        onClick  = onNavigateToRegister,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            buildAnnotatedString {
                                withStyle(SpanStyle(color = TextMuted)) { append("Don't have an account? ") }
                                withStyle(SpanStyle(color = AccentCyan, fontWeight = FontWeight.SemiBold)) {
                                    append("Register here")
                                }
                            },
                            textAlign = TextAlign.Center,
                            style     = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}
