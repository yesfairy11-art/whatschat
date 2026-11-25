package com.example.features.settings

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.domain.models.ConnectionMode
import com.example.domain.models.UserProfile
import com.example.features.chats.ChatWallpaperPattern
import com.example.ui.components.MainNavDestination
import com.example.ui.components.WhatsChatAvatar
import com.example.ui.components.WhatsChatBottomBar
import com.example.ui.components.WhatsChatConfirmDialog
import com.example.ui.theme.WhatsChatThemeTokens

private enum class SettingsInfoDialog {
  ACCOUNT,
  STORAGE,
  APPEARANCE,
  HELP,
  QR_CODE,
}

private data class AccountMenuItem(
  val icon: ImageVector,
  val title: String,
  val subtitle: String,
  val testTag: String,
  val isDanger: Boolean = false,
  val onClick: () -> Unit,
)

@Composable
fun SettingsScreen(
  currentUser: UserProfile,
  connectionMode: ConnectionMode,
  onNavigateBottom: (MainNavDestination) -> Unit,
  onOpenProfile: () -> Unit,
  onSaveStatusBubble: (String) -> Unit,
  onOpenPrivacy: () -> Unit,
  onOpenNotifications: () -> Unit,
  onOpenConnectionPreferences: () -> Unit,
  onOpenBluetoothDevices: () -> Unit,
  onOpenSearchUsers: () -> Unit,
  onLogoutConfirmed: () -> Unit,
) {
  val colors = WhatsChatThemeTokens.colors
  val context = LocalContext.current
  var activeInfoDialog by remember { mutableStateOf<SettingsInfoDialog?>(null) }
  var showLogoutConfirm by remember { mutableStateOf(false) }
  var showStatusDialog by remember { mutableStateOf(false) }
  var statusDraft by remember(currentUser.statusBubble) { mutableStateOf(currentUser.statusBubble) }
  var showSearch by remember { mutableStateOf(false) }
  var searchQuery by remember { mutableStateOf("") }

  val menuItems = remember(currentUser, connectionMode) {
    listOf(
      AccountMenuItem(
        icon = Icons.Filled.Devices,
        title = "Linked devices",
        subtitle = "Bluetooth & nearby WhatsChat connections",
        testTag = "settings_bluetooth_row",
        onClick = onOpenBluetoothDevices,
      ),
      AccountMenuItem(
        icon = Icons.Filled.Key,
        title = "Account",
        subtitle = "Username @${currentUser.username}, security & profile",
        testTag = "settings_account_row",
        onClick = { activeInfoDialog = SettingsInfoDialog.ACCOUNT },
      ),
      AccountMenuItem(
        icon = Icons.Filled.Lock,
        title = "Privacy",
        subtitle = "Blocked accounts, last seen, read receipts",
        testTag = "settings_privacy_row",
        onClick = onOpenPrivacy,
      ),
      AccountMenuItem(
        icon = Icons.Filled.ListAlt,
        title = "Lists",
        subtitle = "Manage contacts & group discovery",
        testTag = "settings_lists_row",
        onClick = onOpenSearchUsers,
      ),
      AccountMenuItem(
        icon = Icons.Filled.ChatBubbleOutline,
        title = "Chats",
        subtitle = "Connection mode (${connectionMode.label}), unified history",
        testTag = "settings_connection_row",
        onClick = onOpenConnectionPreferences,
      ),
      AccountMenuItem(
        icon = Icons.Filled.Palette,
        title = "Appearance",
        subtitle = "Dark Teal & Emerald theme, adaptive app icon",
        testTag = "settings_appearance_row",
        onClick = { activeInfoDialog = SettingsInfoDialog.APPEARANCE },
      ),
      AccountMenuItem(
        icon = Icons.Filled.Notifications,
        title = "Notifications",
        subtitle = "Message, group & call tones",
        testTag = "settings_notifications_row",
        onClick = onOpenNotifications,
      ),
      AccountMenuItem(
        icon = Icons.Filled.Storage,
        title = "Storage and data",
        subtitle = "Room offline cache · 18.4 MB used",
        testTag = "settings_storage_row",
        onClick = { activeInfoDialog = SettingsInfoDialog.STORAGE },
      ),
      AccountMenuItem(
        icon = Icons.AutoMirrored.Filled.HelpOutline,
        title = "Help and feedback",
        subtitle = "Help centre, dual transport guide",
        testTag = "settings_help_row",
        onClick = { activeInfoDialog = SettingsInfoDialog.HELP },
      ),
      AccountMenuItem(
        icon = Icons.Filled.GroupAdd,
        title = "Invite a friend",
        subtitle = "Share @${currentUser.username} on WhatsChat",
        testTag = "settings_invite_row",
        onClick = {
          Toast.makeText(context, "Invite link copied for @${currentUser.username}", Toast.LENGTH_SHORT).show()
        },
      ),
      AccountMenuItem(
        icon = Icons.AutoMirrored.Filled.ExitToApp,
        title = "Logout",
        subtitle = "Sign out of @${currentUser.username}",
        testTag = "settings_logout_row",
        isDanger = true,
        onClick = { showLogoutConfirm = true },
      ),
    )
  }

  val filteredItems = remember(menuItems, searchQuery) {
    val q = searchQuery.trim().lowercase()
    if (q.isEmpty()) menuItems else menuItems.filter {
      it.title.lowercase().contains(q) || it.subtitle.lowercase().contains(q)
    }
  }

  Scaffold(
    containerColor = colors.backgroundPrimary,
    bottomBar = {
      WhatsChatBottomBar(
        currentDestination = MainNavDestination.ACCOUNT,
        currentUser = currentUser,
        onNavigate = onNavigateBottom,
      )
    },
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding),
      contentAlignment = Alignment.TopCenter,
    ) {
      LazyColumn(
        modifier = Modifier
          .widthIn(max = 640.dp)
          .fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp),
      ) {
        // Account Banner Header matching uploaded screenshots
        item {
          Box(modifier = Modifier.fillMaxWidth()) {
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .height(148.dp)
                .background(colors.backgroundSecondary),
            ) {
              ChatWallpaperPattern()
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .statusBarsPadding()
                  .padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
              ) {
                IconButton(
                  onClick = {
                    showSearch = !showSearch
                    if (!showSearch) searchQuery = ""
                  },
                ) {
                  Icon(
                    imageVector = if (showSearch) Icons.Filled.Close else Icons.Filled.Search,
                    contentDescription = "Search settings",
                    tint = colors.textPrimary,
                  )
                }
                IconButton(onClick = { activeInfoDialog = SettingsInfoDialog.QR_CODE }) {
                  Icon(
                    imageVector = Icons.Filled.QrCode2,
                    contentDescription = "Show QR code",
                    tint = colors.textPrimary,
                  )
                }
                IconButton(
                  onClick = onOpenProfile,
                  modifier = Modifier.testTag("settings_profile_card"),
                ) {
                  Icon(
                    imageVector = Icons.Filled.Edit,
                    contentDescription = "Edit Profile",
                    tint = colors.textPrimary,
                  )
                }
              }
            }

            Column(
              modifier = Modifier
                .fillMaxWidth()
                .padding(top = 82.dp),
              horizontalAlignment = Alignment.CenterHorizontally,
            ) {
              // Status bubble pill ("What's happening?") overlapping avatar top edge
              Surface(
                color = colors.card,
                shape = RoundedCornerShape(999.dp),
                border = BorderStroke(1.dp, colors.divider),
                modifier = Modifier
                  .clickable {
                    statusDraft = currentUser.statusBubble
                    showStatusDialog = true
                  }
                  .testTag("account_status_pill"),
              ) {
                Text(
                  text = currentUser.statusBubble.ifBlank { "What's happening?" },
                  style = MaterialTheme.typography.bodySmall,
                  color = colors.textSecondary,
                  modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                )
              }

              Box(
                modifier = Modifier
                  .offset(y = (-6).dp)
                  .clickable(onClick = onOpenProfile),
              ) {
                WhatsChatAvatar(
                  displayName = currentUser.displayName,
                  size = 108.dp,
                  avatarResName = currentUser.avatarResName,
                  avatarUri = currentUser.avatarUri,
                  isOnline = true,
                )
              }

              Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                  .clip(RoundedCornerShape(10.dp))
                  .clickable(onClick = onOpenProfile)
                  .padding(horizontal = 10.dp, vertical = 2.dp),
              ) {
                Text(
                  text = currentUser.displayName,
                  style = MaterialTheme.typography.headlineSmall,
                  color = colors.textPrimary,
                  fontWeight = FontWeight.SemiBold,
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                  imageVector = Icons.Filled.KeyboardArrowDown,
                  contentDescription = "View profile",
                  tint = colors.textSecondary,
                  modifier = Modifier.size(20.dp),
                )
              }

              Text(
                text = "@${currentUser.username}",
                style = MaterialTheme.typography.bodyMedium,
                color = colors.textSecondary,
              )
              Spacer(modifier = Modifier.height(10.dp))
            }
          }

          if (showSearch) {
            OutlinedTextField(
              value = searchQuery,
              onValueChange = { searchQuery = it },
              placeholder = { Text("Search account settings...", color = colors.textDisabled) },
              singleLine = true,
              shape = RoundedCornerShape(14.dp),
              modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 6.dp),
            )
          }

          HorizontalDivider(color = colors.divider)
        }

        items(filteredItems, key = { it.title }) { item ->
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clickable(onClick = item.onClick)
              .padding(horizontal = 22.dp, vertical = 14.dp)
              .testTag(item.testTag),
            verticalAlignment = Alignment.CenterVertically,
          ) {
            Icon(
              imageVector = item.icon,
              contentDescription = null,
              tint = if (item.isDanger) colors.danger else colors.textSecondary,
              modifier = Modifier.size(24.dp),
            )
            Spacer(modifier = Modifier.width(20.dp))
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = item.title,
                style = MaterialTheme.typography.bodyLarge,
                color = if (item.isDanger) colors.danger else colors.textPrimary,
                fontWeight = FontWeight.Medium,
              )
              Text(
                text = item.subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = colors.textSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
              )
            }
          }
        }
      }
    }
  }

  if (showStatusDialog) {
    AlertDialog(
      onDismissRequest = { showStatusDialog = false },
      containerColor = colors.card,
      shape = RoundedCornerShape(20.dp),
      title = { Text("Update Status Bubble", color = colors.textPrimary) },
      text = {
        OutlinedTextField(
          value = statusDraft,
          onValueChange = { if (it.length <= 42) statusDraft = it },
          label = { Text("Status") },
          placeholder = { Text("What's happening?") },
          singleLine = true,
        )
      },
      confirmButton = {
        TextButton(
          onClick = {
            onSaveStatusBubble(statusDraft)
            showStatusDialog = false
          },
        ) {
          Text("Save", color = colors.mintAccent)
        }
      },
      dismissButton = {
        TextButton(onClick = { showStatusDialog = false }) {
          Text("Cancel", color = colors.textSecondary)
        }
      },
    )
  }

  activeInfoDialog?.let { dialog ->
    if (dialog == SettingsInfoDialog.QR_CODE) {
      AlertDialog(
        onDismissRequest = { activeInfoDialog = null },
        containerColor = colors.card,
        shape = RoundedCornerShape(22.dp),
        title = { Text("My WhatsChat QR Code", color = colors.textPrimary) },
        text = {
          Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp),
          ) {
            Surface(
              color = Color.White,
              shape = RoundedCornerShape(16.dp),
              modifier = Modifier.size(160.dp),
            ) {
              Box(contentAlignment = Alignment.Center) {
                Icon(
                  imageVector = Icons.Filled.QrCode2,
                  contentDescription = "QR code for @${currentUser.username}",
                  tint = Color(0xFF031B1D),
                  modifier = Modifier.size(132.dp),
                )
              }
            }
            Text(
              text = "@${currentUser.username}",
              style = MaterialTheme.typography.titleMedium,
              color = colors.mintAccent,
              fontWeight = FontWeight.Bold,
            )
            Text(
              text = "Friends can scan or search your @username to chat over Internet or Bluetooth.",
              style = MaterialTheme.typography.bodySmall,
              color = colors.textSecondary,
            )
          }
        },
        confirmButton = {
          TextButton(onClick = { activeInfoDialog = null }) {
            Text("Done", color = colors.mintAccent)
          }
        },
      )
    } else {
      val (title, body) = when (dialog) {
        SettingsInfoDialog.ACCOUNT -> "Account" to
          "Display Name: ${currentUser.displayName}\nUsername: @${currentUser.username}\nEmail: ${currentUser.email ?: "Not set (optional)"}\n\nManage your profile photo, name, and @username from your Profile screen."
        SettingsInfoDialog.STORAGE -> "Storage & Local Room Cache" to
          "WhatsChat uses an offline-first Room SQLite database.\n\n• Messages & Metadata: 4.2 MB\n• Media Cache: 14.2 MB\n• Pending Sync Queue: 1 message"
        SettingsInfoDialog.APPEARANCE -> "Appearance" to
          "WhatsChat uses the Dark Teal (#031B1D) & Emerald (#00C982) Material 3 design system with adaptive launcher iconography."
        SettingsInfoDialog.HELP -> "Help & Dual Connection Guide" to
          "1. Automatic (Default): Uses Internet when available, falls back to an authorized nearby Bluetooth device, or queues messages as PENDING.\n2. Both transports share a single unified chat history."
        SettingsInfoDialog.QR_CODE -> "" to ""
      }
      AlertDialog(
        onDismissRequest = { activeInfoDialog = null },
        containerColor = colors.card,
        shape = RoundedCornerShape(20.dp),
        title = { Text(title, color = colors.textPrimary, fontWeight = FontWeight.SemiBold) },
        text = { Text(body, color = colors.textSecondary, style = MaterialTheme.typography.bodyMedium) },
        confirmButton = {
          if (dialog == SettingsInfoDialog.ACCOUNT) {
            TextButton(
              onClick = {
                activeInfoDialog = null
                onOpenProfile()
              },
            ) {
              Text("Edit Profile", color = colors.mintAccent)
            }
          } else {
            TextButton(onClick = { activeInfoDialog = null }) {
              Text("Close", color = colors.mintAccent)
            }
          }
        },
        dismissButton = if (dialog == SettingsInfoDialog.ACCOUNT) {
          {
            TextButton(onClick = { activeInfoDialog = null }) {
              Text("Close", color = colors.textSecondary)
            }
          }
        } else {
          null
        },
      )
    }
  }

  if (showLogoutConfirm) {
    WhatsChatConfirmDialog(
      title = "Log Out?",
      message = "Return to the Welcome screen? Your local workspace stays ready.",
      confirmLabel = "Logout",
      isDestructive = true,
      onConfirm = {
        showLogoutConfirm = false
        onLogoutConfirmed()
      },
      onDismiss = { showLogoutConfirm = false },
    )
  }
}
