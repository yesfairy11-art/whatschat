package com.example.features.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
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
import androidx.compose.ui.unit.dp
import com.example.domain.models.PrivacySettingsState
import com.example.domain.models.PrivacyVisibility
import com.example.ui.components.SettingsRowItem
import com.example.ui.components.SettingsSwitchRow
import com.example.ui.components.WhatsChatTopBar
import com.example.ui.theme.WhatsChatThemeTokens

private enum class PrivacyTargetField(val title: String) {
  LAST_SEEN("Last Seen Visibility"),
  ONLINE_STATUS("Online Status Visibility"),
  PROFILE_PHOTO("Profile Photo Visibility"),
  ABOUT("About Visibility"),
}

@Composable
fun PrivacySettingsScreen(
  privacyState: PrivacySettingsState,
  blockedCount: Int,
  onBackClick: () -> Unit,
  onUpdatePrivacy: (PrivacySettingsState) -> Unit,
  onOpenBlockedUsers: () -> Unit,
) {
  val colors = WhatsChatThemeTokens.colors
  var editingField by remember { mutableStateOf<PrivacyTargetField?>(null) }

  Scaffold(
    containerColor = colors.backgroundPrimary,
    topBar = {
      WhatsChatTopBar(
        title = "Privacy Settings",
        subtitle = "Control what others see on WhatsChat",
        onBackClick = onBackClick,
      )
    },
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
        .navigationBarsPadding(),
      contentAlignment = Alignment.TopCenter,
    ) {
      LazyColumn(
        modifier = Modifier
          .widthIn(max = 640.dp)
          .fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
      ) {
        item {
          Text(
            text = "Who can see my personal info",
            style = MaterialTheme.typography.titleSmall,
            color = colors.mintAccent,
          )
        }
        item {
          SettingsRowItem(
            icon = Icons.Filled.Visibility,
            title = "Last Seen",
            trailingValue = privacyState.lastSeen.label,
            onClick = { editingField = PrivacyTargetField.LAST_SEEN },
            testTag = "privacy_last_seen",
          )
        }
        item {
          SettingsRowItem(
            icon = Icons.Filled.Visibility,
            title = "Online Status",
            trailingValue = privacyState.onlineStatus.label,
            onClick = { editingField = PrivacyTargetField.ONLINE_STATUS },
            testTag = "privacy_online_status",
          )
        }
        item {
          SettingsRowItem(
            icon = Icons.Filled.AccountBox,
            title = "Profile Photo",
            trailingValue = privacyState.profilePhoto.label,
            onClick = { editingField = PrivacyTargetField.PROFILE_PHOTO },
            testTag = "privacy_profile_photo",
          )
        }
        item {
          SettingsRowItem(
            icon = Icons.Filled.Info,
            title = "About",
            trailingValue = privacyState.about.label,
            onClick = { editingField = PrivacyTargetField.ABOUT },
            testTag = "privacy_about",
          )
        }

        item {
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = "Messaging Privacy",
            style = MaterialTheme.typography.titleSmall,
            color = colors.mintAccent,
          )
        }

        item {
          SettingsSwitchRow(
            icon = Icons.Filled.DoneAll,
            title = "Read Receipts",
            subtitle = "Share when you have read incoming messages",
            checked = privacyState.readReceipts,
            onCheckedChange = {
              onUpdatePrivacy(privacyState.copy(readReceipts = it))
            },
            testTag = "privacy_read_receipts",
          )
        }

        item {
          SettingsSwitchRow(
            icon = Icons.Filled.Keyboard,
            title = "Typing Indicators",
            subtitle = "Show when you are typing in a conversation",
            checked = privacyState.typingIndicators,
            onCheckedChange = {
              onUpdatePrivacy(privacyState.copy(typingIndicators = it))
            },
            testTag = "privacy_typing_indicators",
          )
        }

        item {
          SettingsRowItem(
            icon = Icons.Filled.Block,
            title = "Blocked Contacts",
            subtitle = "Manage users who cannot message or call you",
            trailingValue = "$blockedCount",
            iconTint = colors.danger,
            onClick = onOpenBlockedUsers,
            testTag = "privacy_blocked_contacts",
          )
        }

        item {
          Spacer(modifier = Modifier.height(4.dp))
          Surface(
            color = colors.card,
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, colors.divider),
            modifier = Modifier.fillMaxWidth(),
          ) {
            Row(
              modifier = Modifier.padding(16.dp),
              verticalAlignment = Alignment.Top,
            ) {
              Icon(
                imageVector = Icons.Filled.Lock,
                contentDescription = null,
                tint = colors.mintAccent,
              )
              Spacer(modifier = Modifier.width(12.dp))
              Column {
                Text(
                  text = "Account & Bluetooth Privacy",
                  style = MaterialTheme.typography.titleSmall,
                  color = colors.textPrimary,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = "Phone numbers are never required. Changes take effect immediately in local UI state and will sync to your PostgreSQL account once connected.",
                  style = MaterialTheme.typography.bodySmall,
                  color = colors.textSecondary,
                )
              }
            }
          }
        }
      }
    }
  }

  editingField?.let { field ->
    val currentSelection = when (field) {
      PrivacyTargetField.LAST_SEEN -> privacyState.lastSeen
      PrivacyTargetField.ONLINE_STATUS -> privacyState.onlineStatus
      PrivacyTargetField.PROFILE_PHOTO -> privacyState.profilePhoto
      PrivacyTargetField.ABOUT -> privacyState.about
    }
    AlertDialog(
      onDismissRequest = { editingField = null },
      containerColor = colors.card,
      shape = RoundedCornerShape(20.dp),
      title = { Text(field.title, color = colors.textPrimary) },
      text = {
        Column {
          PrivacyVisibility.entries.forEach { option ->
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clickable {
                  val updated = when (field) {
                    PrivacyTargetField.LAST_SEEN -> privacyState.copy(lastSeen = option)
                    PrivacyTargetField.ONLINE_STATUS -> privacyState.copy(onlineStatus = option)
                    PrivacyTargetField.PROFILE_PHOTO -> privacyState.copy(profilePhoto = option)
                    PrivacyTargetField.ABOUT -> privacyState.copy(about = option)
                  }
                  onUpdatePrivacy(updated)
                  editingField = null
                }
                .padding(vertical = 8.dp),
              verticalAlignment = Alignment.CenterVertically,
            ) {
              RadioButton(
                selected = option == currentSelection,
                onClick = null,
                colors = RadioButtonDefaults.colors(selectedColor = colors.mintAccent),
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(text = option.label, color = colors.textPrimary)
            }
          }
        }
      },
      confirmButton = {
        TextButton(onClick = { editingField = null }) {
          Text("Close", color = colors.textSecondary)
        }
      },
    )
  }
}
