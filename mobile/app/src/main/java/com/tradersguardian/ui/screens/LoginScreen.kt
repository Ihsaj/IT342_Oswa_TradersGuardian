package com.tradersguardian.ui.screens

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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
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
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(AccentCyan.copy(alpha = 0.07f), Color.Transparent),
                            center = Offset(size.width / 2f, size.height * 0.28f),
                            radius = size.width * 0.9f
                        ),
                        radius = size.width * 0.9f,
                        center = Offset(size.width / 2f, size.height * 0.28f)
                    )
                }
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 28.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Spacer(Modifier.height(48.dp))

                // ── Logo ────────────────────────────────────────────────────
                LogoBadge(size = 80, cornerRadius = 20)
                Spacer(Modifier.height(20.dp))

                Text(
                    text  = "Trader's Guardian",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight    = FontWeight.Bold,
                        letterSpacing = (-0.5).sp
                    ),
                    color = TextPrimary
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text  = "Professional risk management",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextMuted
                )
                Spacer(Modifier.height(36.dp))

                // ── Fields (no card, direct on background) ──────────────────
                // Combined field container
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Surface)
                ) {
                    // Email field
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp)
                    ) {
                        Text(
                            "EMAIL",
                            style     = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
                            color     = TextMuted,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(Modifier.height(6.dp))
                        TgTextField(
                            value        = email,
                            onValueChange = { viewModel.email.value = it },
                            label        = "",
                            placeholder  = "trader@example.com",
                            errorMessage = emailError,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Email,
                                imeAction    = ImeAction.Next
                            ),
                            keyboardActions = KeyboardActions(
                                onNext = { focusManager.moveFocus(FocusDirection.Down) }
                            )
                        )
                    }

                    HorizontalDivider(color = BorderColor, thickness = 0.5.dp)

                    // Password field
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp)
                    ) {
                        Text(
                            "PASSWORD",
                            style     = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
                            color     = TextMuted,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(Modifier.height(6.dp))
                        TgTextField(
                            value        = password,
                            onValueChange = { viewModel.password.value = it },
                            label        = "",
                            placeholder  = "••••••••",
                            isPassword   = true,
                            errorMessage = passwordError,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password,
                                imeAction    = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = { focusManager.clearFocus(); viewModel.login() }
                            )
                        )
                    }
                }

                Spacer(Modifier.height(20.dp))

                // ── Sign In button (full-width cyan) ────────────────────────
                Button(
                    onClick  = { focusManager.clearFocus(); viewModel.login() },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape    = RoundedCornerShape(14.dp),
                    colors   = ButtonDefaults.buttonColors(
                        containerColor = AccentCyan,
                        contentColor   = BgDark
                    ),
                    enabled  = uiState !is UiState.Loading
                ) {
                    if (uiState is UiState.Loading) {
                        CircularProgressIndicator(
                            color     = BgDark,
                            strokeWidth = 2.dp,
                            modifier  = Modifier.size(20.dp)
                        )
                    } else {
                        Text(
                            "Sign In",
                            style      = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(Modifier.height(10.dp))

                // ── Biometrics button (dark outlined) ───────────────────────
                Button(
                    onClick  = { /* TODO: biometrics */ },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape    = RoundedCornerShape(14.dp),
                    colors   = ButtonDefaults.buttonColors(
                        containerColor = Surface,
                        contentColor   = TextPrimary
                    )
                ) {
                    Icon(
                        painter = painterResource(com.tradersguardian.R.drawable.ic_settings), // fingerprint placeholder
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = TextSecondary
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(
                        "Use Biometrics",
                        style      = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(Modifier.height(28.dp))

                // ── Register link ───────────────────────────────────────────
                TextButton(
                    onClick  = onNavigateToRegister,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        buildAnnotatedString {
                            withStyle(SpanStyle(color = TextMuted)) { append("Don't have an account? ") }
                            withStyle(SpanStyle(color = AccentCyan, fontWeight = FontWeight.Bold)) {
                                append("Create Account")
                            }
                        },
                        textAlign = TextAlign.Center,
                        style     = MaterialTheme.typography.bodyMedium
                    )
                }

                Spacer(Modifier.height(32.dp))
            }
        }
    }
}
