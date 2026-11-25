package com.example.features.authentication

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.core.common.PasswordStrength
import com.example.core.common.UsernameValidator
import com.example.ui.components.WhatsChatConfirmDialog
import com.example.ui.components.WhatsChatPrimaryButton
import com.example.ui.components.WhatsChatTextField
import com.example.ui.components.WhatsChatTopBar
import com.example.ui.theme.WhatsChatThemeTokens

@Composable
fun RegistrationScreen(
  isUsernameAvailable: (String) -> Boolean,
  onBackClick: () -> Unit,
  onRegisterSubmit: (
    username: String,
    password: String,
    email: String?,
    onDone: () -> Unit,
    onError: (String) -> Unit,
  ) -> Unit,
  onRegistrationSuccess: () -> Unit,
  onLoginClick: () -> Unit,
) {
  val colors = WhatsChatThemeTokens.colors
  var username by remember { mutableStateOf("") }
  var email by remember { mutableStateOf("") }
  var password by remember { mutableStateOf("") }
  var confirmPassword by remember { mutableStateOf("") }
  var passwordVisible by remember { mutableStateOf(false) }
  var confirmVisible by remember { mutableStateOf(false) }
  var agreedToTerms by remember { mutableStateOf(true) }
  var isLoading by remember { mutableStateOf(false) }
  var formError by remember { mutableStateOf<String?>(null) }
  var showTermsDialog by remember { mutableStateOf(false) }

  val validation = remember(username) { UsernameValidator.validate(username) }
  val available = remember(username, validation) {
    validation.isValid && isUsernameAvailable(validation.normalized)
  }
  val passwordStrength = remember(password) {
    UsernameValidator.evaluatePasswordStrength(password)
  }
  val passwordsMatch = confirmPassword.isEmpty() || password == confirmPassword

  Scaffold(
    containerColor = colors.backgroundPrimary,
    topBar = {
      WhatsChatTopBar(title = "Create Account", onBackClick = onBackClick)
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
      ) {
        Text(
          text = "Join WhatsChat",
          style = MaterialTheme.typography.headlineSmall,
          color = colors.textPrimary,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = "Username-based account · No phone number required",
          style = MaterialTheme.typography.bodySmall,
          color = colors.textSecondary,
        )

        Spacer(modifier = Modifier.height(20.dp))

        WhatsChatTextField(
          value = username,
          onValueChange = {
            username = it
            formError = null
          },
          label = "Username",
          placeholder = "e.g. alex_dev24",
          leadingIcon = Icons.Filled.AlternateEmail,
          trailingContent = {
            if (username.isNotBlank()) {
              Icon(
                imageVector = if (available) Icons.Filled.CheckCircle else Icons.Filled.Cancel,
                contentDescription = if (available) "Username available" else "Username unavailable",
                tint = if (available) colors.online else colors.danger,
                modifier = Modifier.size(20.dp),
              )
            }
          },
          isError = username.isNotEmpty() && (!validation.isValid || !available),
          errorText = when {
            username.isEmpty() -> null
            !validation.isValid -> validation.errorMessage
            !available -> "@${validation.normalized} is already taken"
            else -> null
          },
          supportingText = if (username.isEmpty() || available) {
            if (available) {
              "@${validation.normalized} is available (3-30 chars, letters, numbers, _)"
            } else {
              "3-30 chars · letters, numbers, and underscores only · no spaces"
            }
          } else {
            null
          },
          keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Ascii,
            imeAction = ImeAction.Next,
          ),
          testTag = "register_username_input",
        )

        Spacer(modifier = Modifier.height(14.dp))

        WhatsChatTextField(
          value = email,
          onValueChange = { email = it },
          label = "Email address (Optional)",
          placeholder = "you@example.com",
          leadingIcon = Icons.Filled.Email,
          keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Next,
          ),
          testTag = "register_email_input",
        )

        Spacer(modifier = Modifier.height(14.dp))

        WhatsChatTextField(
          value = password,
          onValueChange = {
            password = it
            formError = null
          },
          label = "Password",
          placeholder = "Minimum 6 characters",
          leadingIcon = Icons.Filled.Lock,
          visualTransformation = if (passwordVisible) {
            VisualTransformation.None
          } else {
            PasswordVisualTransformation()
          },
          trailingContent = {
            IconButton(onClick = { passwordVisible = !passwordVisible }) {
              Icon(
                imageVector = if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                contentDescription = "Toggle password visibility",
                tint = colors.textSecondary,
              )
            }
          },
          keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Next,
          ),
          testTag = "register_password_input",
        )

        if (password.isNotEmpty()) {
          Spacer(modifier = Modifier.height(8.dp))
          val strengthColor = when (passwordStrength) {
            PasswordStrength.WEAK -> colors.danger
            PasswordStrength.MEDIUM -> colors.warning
            PasswordStrength.STRONG -> colors.online
            PasswordStrength.EMPTY -> colors.divider
          }
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
          ) {
            LinearProgressIndicator(
              progress = { passwordStrength.fraction },
              color = strengthColor,
              trackColor = colors.card,
              modifier = Modifier
                .weight(1f)
                .height(5.dp)
                .clip(RoundedCornerShape(999.dp)),
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
              text = passwordStrength.label,
              style = MaterialTheme.typography.labelSmall,
              color = strengthColor,
              fontWeight = FontWeight.Medium,
            )
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        WhatsChatTextField(
          value = confirmPassword,
          onValueChange = {
            confirmPassword = it
            formError = null
          },
          label = "Confirm Password",
          placeholder = "Re-enter password",
          leadingIcon = Icons.Filled.Lock,
          visualTransformation = if (confirmVisible) {
            VisualTransformation.None
          } else {
            PasswordVisualTransformation()
          },
          trailingContent = {
            IconButton(onClick = { confirmVisible = !confirmVisible }) {
              Icon(
                imageVector = if (confirmVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                contentDescription = "Toggle confirm password visibility",
                tint = colors.textSecondary,
              )
            }
          },
          isError = !passwordsMatch,
          errorText = if (!passwordsMatch) "Passwords do not match" else null,
          keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Done,
          ),
          testTag = "register_confirm_password_input",
        )

        Spacer(modifier = Modifier.height(14.dp))

        Surface(
          color = colors.card,
          shape = RoundedCornerShape(14.dp),
          modifier = Modifier
            .fillMaxWidth()
            .clickable { agreedToTerms = !agreedToTerms },
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
          ) {
            Checkbox(
              checked = agreedToTerms,
              onCheckedChange = { agreedToTerms = it },
              colors = CheckboxDefaults.colors(
                checkedColor = colors.primaryGreen,
                checkmarkColor = colors.backgroundPrimary,
                uncheckedColor = colors.textSecondary,
              ),
            )
            Text(
              text = "I agree to the ",
              style = MaterialTheme.typography.bodySmall,
              color = colors.textSecondary,
            )
            Text(
              text = "Terms & Privacy Policy",
              style = MaterialTheme.typography.bodySmall,
              color = colors.mintAccent,
              fontWeight = FontWeight.SemiBold,
              modifier = Modifier.clickable { showTermsDialog = true },
            )
          }
        }

        if (formError != null) {
          Spacer(modifier = Modifier.height(10.dp))
          Text(
            text = formError!!,
            style = MaterialTheme.typography.bodySmall,
            color = colors.danger,
          )
        }

        Spacer(modifier = Modifier.height(20.dp))

        WhatsChatPrimaryButton(
          text = "Create Account",
          isLoading = isLoading,
          onClick = {
            when {
              !validation.isValid -> formError = validation.errorMessage
              !available -> formError = "@${validation.normalized} is already taken"
              password.length < 6 -> formError = "Password must be at least 6 characters"
              password != confirmPassword -> formError = "Passwords do not match"
              !agreedToTerms -> formError = "Please agree to Terms & Privacy Policy"
              else -> {
                isLoading = true
                onRegisterSubmit(
                  username,
                  password,
                  email.takeIf { it.isNotBlank() },
                  {
                    isLoading = false
                    onRegistrationSuccess()
                  },
                  { err ->
                    isLoading = false
                    formError = err
                  },
                )
              }
            }
          },
          testTag = "register_submit_button",
        )

        Spacer(modifier = Modifier.height(14.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.Center,
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Text(
            text = "Already have an account?",
            style = MaterialTheme.typography.bodyMedium,
            color = colors.textSecondary,
          )
          TextButton(onClick = onLoginClick, modifier = Modifier.testTag("register_login_link")) {
            Text(
              text = "Log In",
              style = MaterialTheme.typography.bodyMedium,
              color = colors.mintAccent,
              fontWeight = FontWeight.SemiBold,
            )
          }
        }
      }
    }
  }

  if (showTermsDialog) {
    WhatsChatConfirmDialog(
      title = "Terms & Privacy",
      message = "WhatsChat identifies users by @username without requiring a phone number. Bluetooth discovery never broadcasts personal profile details until you consent to connect.",
      confirmLabel = "Accept",
      onConfirm = {
        agreedToTerms = true
        showTermsDialog = false
      },
      onDismiss = { showTermsDialog = false },
    )
  }
}
