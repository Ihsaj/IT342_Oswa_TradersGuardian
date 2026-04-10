package com.tradersguardian.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tradersguardian.data.model.UiState
import com.tradersguardian.ui.components.*
import com.tradersguardian.ui.theme.*
import com.tradersguardian.viewmodel.RegisterViewModel

@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: RegisterViewModel = viewModel()
) {
    val uiState        by viewModel.uiState.collectAsState()
    val firstName      by viewModel.firstName.collectAsState()
    val lastName       by viewModel.lastName.collectAsState()
    val email          by viewModel.email.collectAsState()
    val username       by viewModel.username.collectAsState()
    val password       by viewModel.password.collectAsState()
    val confirmPw      by viewModel.confirmPw.collectAsState()
    val termsAgreed    by viewModel.termsAgreed.collectAsState()
    val strength       by viewModel.passwordStrength.collectAsState()

    val fnErr     by viewModel.firstNameError.collectAsState()
    val lnErr     by viewModel.lastNameError.collectAsState()
    val emailErr  by viewModel.emailError.collectAsState()
    val userErr   by viewModel.usernameError.collectAsState()
    val pwErr     by viewModel.passwordError.collectAsState()
    val cpwErr    by viewModel.confirmPwError.collectAsState()
    val termsErr  by viewModel.termsError.collectAsState()

    val focusManager      = LocalFocusManager.current
    val snackbarHostState = remember { SnackbarHostState() }
    var showSuccess       by remember { mutableStateOf(false) }

    LaunchedEffect(uiState) {
        when (val s = uiState) {
            is UiState.Success -> showSuccess = true
            is UiState.Error   -> snackbarHostState.showSnackbar(s.message)
            else               -> Unit
        }
    }

    // ── Success dialog ─────────────────────────────────────────────────────
    if (showSuccess) {
        Dialog(onDismissRequest = {}) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(Surface)
                    .border(1.dp, BorderColor, RoundedCornerShape(16.dp))
                    .padding(32.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(SuccessGreen.copy(alpha = 0.12f))
                        .border(2.dp, SuccessGreen, CircleShape)
                ) {
                    Text("✓", color = SuccessGreen, fontSize = 28.sp)
                }
                Text("Account Created!", style = MaterialTheme.typography.titleLarge, color = TextPrimary)
                Text("Welcome to Trader's Guardian. You're all set.", color = TextMuted,
                    style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center)
                TgButton(text = "Go to Login", onClick = onRegisterSuccess)
            }
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
                .drawBehind {
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(AccentCyan.copy(alpha = 0.07f), Color.Transparent),
                            center = Offset(size.width / 2, size.height * 0.3f),
                            radius = size.width * 0.8f
                        ),
                        radius = size.width * 0.8f,
                        center = Offset(size.width / 2, size.height * 0.3f)
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
                    "Trader's Guardian",
                    style = MaterialTheme.typography.headlineLarge.copy(letterSpacing = (-0.5).sp),
                    color = TextPrimary
                )
                Spacer(Modifier.height(4.dp))
                Text("Create your trading account", style = MaterialTheme.typography.bodyMedium, color = TextMuted)
                Spacer(Modifier.height(32.dp))

                // ── Card ───────────────────────────────────────────────────
                TgCard {

                    // First + Last name row
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        TgTextField(
                            value = firstName,
                            onValueChange = { viewModel.firstName.value = it },
                            label = "First Name",
                            placeholder = "John",
                            errorMessage = fnErr,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Right) }),
                            modifier = Modifier.weight(1f)
                        )
                        TgTextField(
                            value = lastName,
                            onValueChange = { viewModel.lastName.value = it },
                            label = "Last Name",
                            placeholder = "Doe",
                            errorMessage = lnErr,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(Modifier.height(16.dp))

                    TgTextField(
                        value = email,
                        onValueChange = { viewModel.email.value = it },
                        label = "Email",
                        placeholder = "trader@example.com",
                        errorMessage = emailErr,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
                    )

                    Spacer(Modifier.height(16.dp))

                    TgTextField(
                        value = username,
                        onValueChange = { viewModel.username.value = it },
                        label = "Username",
                        placeholder = "traderpro99",
                        errorMessage = userErr,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
                    )

                    Spacer(Modifier.height(16.dp))

                    TgTextField(
                        value = password,
                        onValueChange = { viewModel.updatePassword(it) },
                        label = "Password",
                        placeholder = "••••••••",
                        isPassword = true,
                        errorMessage = pwErr,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
                    )

                    Spacer(Modifier.height(8.dp))
                    PasswordStrengthBar(strength = strength)

                    Spacer(Modifier.height(16.dp))

                    TgTextField(
                        value = confirmPw,
                        onValueChange = { viewModel.confirmPw.value = it },
                        label = "Confirm Password",
                        placeholder = "••••••••",
                        isPassword = true,
                        errorMessage = cpwErr,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() })
                    )

                    Spacer(Modifier.height(16.dp))

                    // Terms checkbox
                    Row(
                        verticalAlignment = Alignment.Top,
                        modifier = Modifier.clickable { viewModel.termsAgreed.value = !termsAgreed }
                    ) {
                        Checkbox(
                            checked = termsAgreed,
                            onCheckedChange = { viewModel.termsAgreed.value = it },
                            colors = CheckboxDefaults.colors(
                                checkedColor = AccentCyan,
                                uncheckedColor = BorderColor,
                                checkmarkColor = BgDark
                            ),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(10.dp))
                        Text(
                            buildAnnotatedString {
                                withStyle(SpanStyle(color = TextMuted)) { append("I agree to the ") }
                                withStyle(SpanStyle(color = AccentCyan)) { append("Terms of Service") }
                                withStyle(SpanStyle(color = TextMuted)) { append(" and ") }
                                withStyle(SpanStyle(color = AccentCyan)) { append("Privacy Policy") }
                            },
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                    if (termsErr != null) {
                        Text(termsErr!!, color = ErrorRed, style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 4.dp, start = 4.dp))
                    }

                    Spacer(Modifier.height(20.dp))

                    TgButton(
                        text = "Create Account",
                        onClick = { focusManager.clearFocus(); viewModel.register() },
                        isLoading = uiState is UiState.Loading
                    )

                    Spacer(Modifier.height(20.dp))
                    TgDivider()
                    Spacer(Modifier.height(12.dp))

                    TextButton(
                        onClick = onNavigateToLogin,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            buildAnnotatedString {
                                withStyle(SpanStyle(color = TextMuted)) { append("Already have an account? ") }
                                withStyle(SpanStyle(color = AccentCyan, fontWeight = FontWeight.SemiBold)) {
                                    append("Sign in here")
                                }
                            },
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}
