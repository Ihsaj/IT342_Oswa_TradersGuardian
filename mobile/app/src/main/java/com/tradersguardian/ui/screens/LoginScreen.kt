package com.tradersguardian.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit,
    viewModel: LoginViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val email by viewModel.email.collectAsState()
    val password by viewModel.password.collectAsState()
    val emailError by viewModel.emailError.collectAsState()
    val passwordError by viewModel.passwordError.collectAsState()
    val focusManager = LocalFocusManager.current
    val snackbarHostState = remember { SnackbarHostState() }

    // Handle state changes
    LaunchedEffect(uiState) {
        when (val s = uiState) {
            is UiState.Success -> onLoginSuccess()
            is UiState.Error   -> snackbarHostState.showSnackbar(s.message)
            else               -> Unit
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = Surface,
                    contentColor = TextPrimary,
                    actionColor = AccentCyan,
                    shape = RoundedCornerShape(8.dp)
                )
            }
        },
        containerColor = BgDark
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                // Subtle grid + radial glow background
                .drawBehind {
                    // Radial glow
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                AccentCyan.copy(alpha = 0.07f),
                                Color.Transparent
                            ),
                            center = Offset(size.width / 2, size.height * 0.4f),
                            radius = size.width * 0.8f
                        ),
                        radius = size.width * 0.8f,
                        center = Offset(size.width / 2, size.height * 0.4f)
                    )
                }
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 48.dp),
                verticalArrangement = Arrangement.Center
            ) {

                // ── Brand ──────────────────────────────────────────────────
                LogoBadge(size = 72, cornerRadius = 18)

                Spacer(Modifier.height(16.dp))


                Text(
                    text = "Trader's Guardian",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        letterSpacing = (-0.5).sp
                    ),
                    color = TextPrimary
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    text = "Sign in to your trading account",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextMuted
                )

                Spacer(Modifier.height(32.dp))

                // ── Card ───────────────────────────────────────────────────
                TgCard {
                    // Email
                    TgTextField(
                        value = email,
                        onValueChange = { viewModel.email.value = it },
                        label = "Email",
                        placeholder = "trader@example.com",
                        errorMessage = emailError,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        )
                    )

                    Spacer(Modifier.height(16.dp))

                    // Password
                    TgTextField(
                        value = password,
                        onValueChange = { viewModel.password.value = it },
                        label = "Password",
                        placeholder = "••••••••",
                        isPassword = true,
                        errorMessage = passwordError,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { focusManager.clearFocus(); viewModel.login() }
                        )
                    )

                    Spacer(Modifier.height(8.dp))

                    // Forgot password
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                        TextButton(onClick = { /* TODO: forgot password */ }) {
                            Text(
                                text = "Forgot password?",
                                color = AccentCyan,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }

                    Spacer(Modifier.height(8.dp))

                    // Login Button
                    TgButton(
                        text = "Login",
                        onClick = { focusManager.clearFocus(); viewModel.login() },
                        isLoading = uiState is UiState.Loading
                    )

                    Spacer(Modifier.height(20.dp))

                    TgDivider()

                    Spacer(Modifier.height(20.dp))

                    // Register link
                    TextButton(
                        onClick = onNavigateToRegister,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = buildAnnotatedString {
                                withStyle(SpanStyle(color = TextMuted)) { append("Don't have an account? ") }
                                withStyle(SpanStyle(color = AccentCyan, fontWeight = FontWeight.SemiBold)) {
                                    append("Register here")
                                }
                            },
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}
