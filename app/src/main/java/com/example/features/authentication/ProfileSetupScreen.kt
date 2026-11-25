package com.example.features.authentication

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.unit.dp
import com.example.core.common.UsernameValidator
import com.example.domain.models.UserProfile
import com.example.ui.components.WhatsChatAvatar
import com.example.ui.components.WhatsChatPrimaryButton
import com.example.ui.components.WhatsChatSecondaryButton
import com.example.ui.components.WhatsChatTextField
import com.example.ui.components.WhatsChatTopBar
import com.example.ui.theme.WhatsChatThemeTokens

@Composable
fun ProfileSetupScreen(
  currentUser: UserProfile,
  onBackClick: () -> Unit,
  onSaveProfile: (
    displayName: String,
    username: String,
    about: String,
    avatarUri: String?,
    onDone: () -> Unit,
    onError: (String) -> Unit,
  ) -> Unit,
  onContinueToChats: () -> Unit,
) {
  val colors = WhatsChatThemeTokens.colors
  var displayName by remember { mutableStateOf(currentUser.displayName) }
  var username by remember { mutableStateOf(currentUser.username) }
  var about by remember { mutableStateOf(currentUser.about) }
  var avatarUri by remember { mutableStateOf(currentUser.avatarUri) }
  var errorText by remember { mutableStateOf<String?>(null) }

  val photoPickerLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.PickVisualMedia(),
  ) { uri ->
    if (uri != null) {
      avatarUri = uri.toString()
    }
  }

  Scaffold(
    containerColor = colors.backgroundPrimary,
    topBar = {
      WhatsChatTopBar(
        title = "Setup Your Profile",
        onBackClick = onBackClick,
        actions = {
          TextButton(
            onClick = onContinueToChats,
            modifier = Modifier.testTag("profile_setup_skip"),
          ) {
            Text(
              text = "Skip",
              color = colors.mintAccent,
              fontWeight = FontWeight.SemiBold,
            )
          }
        },
      )
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
          .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        Text(
          text = "Add your profile photo and short bio so friends can recognize you.",
          style = MaterialTheme.typography.bodyMedium,
          color = colors.textSecondary,
        )

        Spacer(modifier = Modifier.height(24.dp))

        Box(
          modifier = Modifier
            .size(116.dp)
            .clickable {
              photoPickerLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly),
              )
            }
            .testTag("profile_setup_avatar"),
        ) {
          WhatsChatAvatar(
            displayName = displayName.ifBlank { username },
            size = 112.dp,
            avatarResName = currentUser.avatarResName,
            avatarUri = avatarUri,
            showOnlineIndicator = false,
          )
          Box(
            modifier = Modifier
              .align(Alignment.BottomEnd)
              .size(36.dp)
              .clip(CircleShape)
              .background(colors.primaryGreen)
              .border(2.dp, colors.backgroundPrimary, CircleShape),
            contentAlignment = Alignment.Center,
          ) {
            Icon(
              imageVector = Icons.Filled.CameraAlt,
              contentDescription = "Choose photo",
              tint = colors.backgroundPrimary,
              modifier = Modifier.size(18.dp),
            )
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        WhatsChatSecondaryButton(
          text = "Choose Photo",
          onClick = {
            photoPickerLauncher.launch(
              PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly),
            )
          },
          modifier = Modifier.fillMaxWidth(0.55f),
          testTag = "profile_setup_choose_photo",
        )

        Spacer(modifier = Modifier.height(24.dp))

        WhatsChatTextField(
          value = displayName,
          onValueChange = { displayName = it },
          label = "Display Name",
          placeholder = "Alex Kumar",
          leadingIcon = Icons.Filled.Person,
          testTag = "profile_setup_name_input",
        )

        Spacer(modifier = Modifier.height(14.dp))

        WhatsChatTextField(
          value = username,
          onValueChange = {
            username = it
            errorText = null
          },
          label = "Username",
          placeholder = "@alexkumar",
          leadingIcon = Icons.Filled.AlternateEmail,
          isError = errorText != null,
          errorText = errorText,
          testTag = "profile_setup_username_input",
        )

        Spacer(modifier = Modifier.height(14.dp))

        WhatsChatTextField(
          value = about,
          onValueChange = { if (it.length <= 160) about = it },
          label = "About / Bio (Optional)",
          placeholder = "Available on Internet & nearby Bluetooth",
          leadingIcon = Icons.Filled.Info,
          singleLine = false,
          maxLines = 3,
          supportingText = "${about.length}/160 characters",
          testTag = "profile_setup_about_input",
        )

        Spacer(modifier = Modifier.height(28.dp))

        WhatsChatPrimaryButton(
          text = "Save Profile",
          onClick = {
            val validation = UsernameValidator.validate(username)
            if (!validation.isValid) {
              errorText = validation.errorMessage
              return@WhatsChatPrimaryButton
            }
            onSaveProfile(
              displayName,
              username,
              about,
              avatarUri,
              onContinueToChats,
            ) { err -> errorText = err }
          },
          testTag = "profile_setup_save_button",
        )
      }
    }
  }
}
