package com.example.features.settings

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.DoNotDisturbOn
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Vibration
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.core.permissions.PermissionHelper
import com.example.domain.models.NotificationSettingsState
import com.example.ui.components.SettingsRowItem
import com.example.ui.components.SettingsSwitchRow
import com.example.ui.components.WhatsChatTopBar
import com.example.ui.theme.WhatsChatThemeTokens

@Composable
fun NotificationsScreen(
  notificationState: NotificationSettingsState,
  onBackClick: () -> Unit,
  onUpdateNotifications: (NotificationSettingsState) -> Unit,
) {
  val colors = WhatsChatThemeTokens.colors
  val context = LocalContext.current
  val notifPerms = remember { PermissionHelper.notificationPermissions() }
  var hasNotifPerm by remember {
    mutableStateOf(PermissionHelper.hasAllPermissions(context, notifPerms))
  }
  var showSoundDialog by remember { mutableStateOf(false) }

  val permLauncher = rememberLauncherForActivityResult(
    ActivityResultContracts.RequestMultiplePermissions(),
  ) { result ->
    hasNotifPerm = result.values.all { it }
    Toast.makeText(
      context,
      if (hasNotifPerm) "Notifications permitted" else "Notification permission denied",
      Toast.LENGTH_SHORT,
    ).show()
  }

  Scaffold(
    containerColor = colors.backgroundPrimary,
    topBar = {
      WhatsChatTopBar(
        title = "Notifications",
        subtitle = "Messages, calls, sound & Do Not Disturb",
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
        if (!hasNotifPerm && notifPerms.isNotEmpty()) {
          item {
            Surface(
              color = colors.card,
              shape = RoundedCornerShape(16.dp),
              border = BorderStroke(1.dp, colors.warning.copy(alpha = 0.55f)),
              modifier = Modifier
                .fillMaxWidth()
                .clickable { permLauncher.launch(notifPerms.toTypedArray()) },
            ) {
              Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
              ) {
                Icon(
                  imageVector = Icons.Filled.NotificationsActive,
                  contentDescription = null,
                  tint = colors.warning,
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                  Text(
                    text = "System Notification Permission Required",
                    style = MaterialTheme.typography.titleSmall,
                    color = colors.textPrimary,
                  )
                  Text(
                    text = "Tap to allow WhatsChat alerts on Android 13+.",
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textSecondary,
                  )
                }
              }
            }
          }
        }

        item {
          SettingsSwitchRow(
            icon = Icons.Filled.Message,
            title = "Message Notifications",
            subtitle = "Alert for incoming direct chats",
            checked = notificationState.messageNotifications,
            onCheckedChange = {
              onUpdateNotifications(notificationState.copy(messageNotifications = it))
            },
            testTag = "notif_messages_switch",
          )
        }
        item {
          SettingsSwitchRow(
            icon = Icons.Filled.Group,
            title = "Group Notifications",
            subtitle = "Alert for group conversations",
            checked = notificationState.groupNotifications,
            onCheckedChange = {
              onUpdateNotifications(notificationState.copy(groupNotifications = it))
            },
            testTag = "notif_groups_switch",
          )
        }
        item {
          SettingsSwitchRow(
            icon = Icons.Filled.Call,
            title = "Call Notifications",
            subtitle = "Ring for incoming voice & video calls",
            checked = notificationState.callNotifications,
            onCheckedChange = {
              onUpdateNotifications(notificationState.copy(callNotifications = it))
            },
            testTag = "notif_calls_switch",
          )
        }

        item {
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "Alerts & Behavior",
            style = MaterialTheme.typography.titleSmall,
            color = colors.mintAccent,
          )
        }

        item {
          SettingsSwitchRow(
            icon = Icons.Filled.MusicNote,
            title = "Sound",
            subtitle = "Play notification chime",
            checked = notificationState.soundEnabled,
            onCheckedChange = {
              onUpdateNotifications(notificationState.copy(soundEnabled = it))
            },
            testTag = "notif_sound_switch",
          )
        }
        item {
          SettingsRowItem(
            icon = Icons.Filled.MusicNote,
            title = "Notification Tone",
            trailingValue = notificationState.selectedSound,
            onClick = { showSoundDialog = true },
            testTag = "notif_tone_row",
          )
        }
        item {
          SettingsSwitchRow(
            icon = Icons.Filled.Vibration,
            title = "Vibration",
            subtitle = "Vibrate on incoming messages and calls",
            checked = notificationState.vibrationEnabled,
            onCheckedChange = {
              onUpdateNotifications(notificationState.copy(vibrationEnabled = it))
            },
            testTag = "notif_vibrate_switch",
          )
        }
        item {
          SettingsSwitchRow(
            icon = Icons.Filled.Visibility,
            title = "Notification Preview",
            subtitle = "Show sender name & message text in alerts",
            checked = notificationState.showPreview,
            onCheckedChange = {
              onUpdateNotifications(notificationState.copy(showPreview = it))
            },
            testTag = "notif_preview_switch",
          )
        }
        item {
          SettingsSwitchRow(
            icon = Icons.Filled.DoNotDisturbOn,
            title = "Do Not Disturb",
            subtitle = "Silence all alerts temporarily",
            checked = notificationState.doNotDisturb,
            onCheckedChange = {
              onUpdateNotifications(notificationState.copy(doNotDisturb = it))
            },
            testTag = "notif_dnd_switch",
          )
        }
      }
    }
  }

  if (showSoundDialog) {
    val tones = listOf("Emerald Chime", "Teal Drop", "Mint Pulse", "Classic Ping", "Silent")
    AlertDialog(
      onDismissRequest = { showSoundDialog = false },
      containerColor = colors.card,
      shape = RoundedCornerShape(20.dp),
      title = { Text("Choose Notification Sound", color = colors.textPrimary) },
      text = {
        Column {
          tones.forEach { tone ->
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clickable {
                  onUpdateNotifications(notificationState.copy(selectedSound = tone))
                  showSoundDialog = false
                }
                .padding(vertical = 8.dp),
              verticalAlignment = Alignment.CenterVertically,
            ) {
              RadioButton(
                selected = notificationState.selectedSound == tone,
                onClick = null,
                colors = RadioButtonDefaults.colors(selectedColor = colors.mintAccent),
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(tone, color = colors.textPrimary)
            }
          }
        }
      },
      confirmButton = {
        TextButton(onClick = { showSoundDialog = false }) {
          Text("Close", color = colors.textSecondary)
        }
      },
    )
  }
}
