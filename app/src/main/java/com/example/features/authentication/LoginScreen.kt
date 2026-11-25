package com.example.features.authentication

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.core.common.UsernameValidator
import com.example.ui.components.MockPreviewNoticeBanner
import com.example.ui.components.WhatsChatConfirmDialog
import com.example.ui.components.WhatsChatLogoBadge
import com.example.ui.components.WhatsChatPrimaryButton
import com.example.ui.components.WhatsChatTextField
import com.example.ui.components.WhatsChatTopBar
import com.example.ui.theme.WhatsChatThemeTokens

@Composable
fun LoginScreen(
  onBackClick: () -> Unit,
  onLoginSubmit: (
    username: String,
    password: String,
    rememberMe: Boolean,
    onDone: () -> Unit,
    onError: (String) -> Unit,
  ) -> Unit,
  onLoginSuccess: () -> Unit,
  onCreateAccountClick: () -> Unit,
) {
  val colors = WhatsChatThemeTokens.colors
  var username by remember { mutableStateOf("alexkumar") }
  var password by remember { mutableStateOf("123456") }
  var passwordVisible by remember { mutableStateOf(false) }
  var rememberMe by remember { mutableStateOf(true) }
  var isLoading by remember { mutableStateOf(false) }
  var usernameError by remember { mutableStateOf<String?>(null) }
  var formError by remember { mutableStateOf<String?>(null) }
  var showForgotDialog by remember { mutableStateOf(false) }

  Scaffold(
    containerColor = colors.backgroundPrimary,
    topBar = {
      WhatsChatTopBar(title = "", onBackClick = onBackClick)
    },
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
        .navigationBarsPadding()
        .imePadding(),
      contentAlignment = Alignment.TopCenter,
    ) {
      Column(
        modifier = Modifier
          .widthIn(max = 520.dp)
          .fillMaxSize()
          .verticalScroll(rememberScrollState())
          .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        WhatsChatLogoBadge(size = 72.dp)
        Spacer(modifier = Modifier.height(16.dp))

        Text(
          text = "Welcome Back",
          style = MaterialTheme.typography.displaySmall,
          color = colors.textPrimary,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = "Sign in with your @username to continue",
          style = MaterialTheme.typography.bodyMedium,
          color = colors.textSecondary,
        )

        Spacer(modifier = Modifier.height(24.dp))

        Surface(
          color = colors.card,
          shape = RoundedCornerShape(20.dp),
          modifier = Modifier.fillMaxWidth(),
        ) {
          Column(modifier = Modifier.padding(18.dp)) {
            WhatsChatTextField(
              value = username,
              onValueChange = {
                username = it
                usernameError = null
                formError = null
              },
              label = "Username",
              placeholder = "@username",
              leadingIcon = Icons.Filled.AlternateEmail,
              isError = usernameError != null,
              errorText = usernameError,
              keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Ascii,
                imeAction = ImeAction.Next,
              ),
              testTag = "login_username_input",
            )

            Spacer(modifier = Modifier.height(14.dp))

            WhatsChatTextField(
              value = password,
              onValueChange = {
                password = it
                formError = null
              },
              label = "Password",
              placeholder = "Enter password",
              leadingIcon = Icons.Filled.Lock,
              visualTransformation = if (passwordVisible) {
                VisualTransformation.None
              } else {
                PasswordVisualTransformation()
              },
              trailingContent = {
                IconButton(
                  onClick = { passwordVisible = !passwordVisible },
                  modifier = Modifier.testTag("login_password_toggle"),
                ) {
                  Icon(
                    imageVector = if (passwordVisible) {
                      Icons.Filled.VisibilityOff
                    } else {
                      Icons.Filled.Visibility
                    },
                    contentDescription = if (passwordVisible) "Hide password" else "Show password",
                    tint = colors.textSecondary,
                  )
                }
              },
              keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done,
              ),
              testTag = "login_password_input",
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween,
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { rememberMe = !rememberMe },
              ) {
                Checkbox(
                  checked = rememberMe,
                  onCheckedChange = { rememberMe = it },
                  colors = CheckboxDefaults.colors(
                    checkedColor = colors.primaryGreen,
                    checkmarkColor = colors.backgroundPrimary,
                    uncheckedColor = colors.textSecondary,
                  ),
                )
                Text(
                  text = "Remember me",
                  style = MaterialTheme.typography.bodySmall,
                  color = colors.textSecondary,
                )
              }

              TextButton(
                onClick = { showForgotDialog = true },
                modifier = Modifier.testTag("login_forgot_password"),
              ) {
                Text(
                  text = "Forgot Password?",
                  style = MaterialTheme.typography.bodySmall,
                  color = colors.mintAccent,
                  fontWeight = FontWeight.Medium,
                )
              }
            }

            if (formError != null) {
              Spacer(modifier = Modifier.height(8.dp))
              Text(
                text = formError!!,
                color = colors.danger,
                style = MaterialTheme.typography.bodySmall,
              )
            }

            Spacer(modifier = Modifier.height(16.dp))

            WhatsChatPrimaryButton(
              text = "Login",
              isLoading = isLoading,
              onClick = {
                val validation = UsernameValidator.validate(username)
                if (!validation.isValid) {
                  usernameError = validation.errorMessage
                  return@WhatsChatPrimaryButton
                }
                isLoading = true
                onLoginSubmit(
                  username,
                  password,
                  rememberMe,
                  {
                    isLoading = false
                    onLoginSuccess()
                  },
                  { err ->
                    isLoading = false
                    formError = err
                  },
                )
              },
              testTag = "login_submit_button",
            )
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        MockPreviewNoticeBanner(
          text = "Frontend phase: Authentication uses local mock validation until the Node.js + PostgreSQL backend is connected.",
        )

        Spacer(modifier = Modifier.height(20.dp))

        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.Center,
        ) {
          Text(
            text = "Don't have an account?",
            style = MaterialTheme.typography.bodyMedium,
            color = colors.textSecondary,
          )
          TextButton(
            onClick = onCreateAccountClick,
            modifier = Modifier.testTag("login_register_link"),
          ) {
            Text(
              text = "Create Account",
              style = MaterialTheme.typography.bodyMedium,
              color = colors.mintAccent,
              fontWeight = FontWeight.SemiBold,
            )
          }
        }
      }
    }
  }

  if (showForgotDialog) {
    WhatsChatConfirmDialog(
      title = "Password Recovery",
      message = "Account recovery will connect to your self-hosted Node.js backend in the backend phase. For this frontend preview, enter any 4+ character password with a valid username.",
      confirmLabel = "Got it",
      onConfirm = { showForgotDialog = false },
      onDismiss = { showForgotDialog = false },
    )
  }
}
