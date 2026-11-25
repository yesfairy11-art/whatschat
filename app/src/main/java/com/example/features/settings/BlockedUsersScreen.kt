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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.PersonAddDisabled
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.unit.dp
import com.example.domain.models.UserProfile
import com.example.ui.components.WhatsChatAvatar
import com.example.ui.components.WhatsChatConfirmDialog
import com.example.ui.components.WhatsChatEmptyState
import com.example.ui.components.WhatsChatTopBar
import com.example.ui.theme.WhatsChatThemeTokens

@Composable
fun BlockedUsersScreen(
  blockedUsers: List<UserProfile>,
  allUsers: List<UserProfile>,
  onBackClick: () -> Unit,
  onUnblockUser: (String) -> Unit,
  onBlockUser: (String) -> Unit,
  onOpenUserProfile: (String) -> Unit,
) {
  val colors = WhatsChatThemeTokens.colors
  var unblockConfirmTarget by remember { mutableStateOf<UserProfile?>(null) }
  var showBlockPicker by remember { mutableStateOf(false) }

  val blockableUsers = remember(allUsers) { allUsers.filterNot { it.isBlocked } }

  Scaffold(
    containerColor = colors.backgroundPrimary,
    topBar = {
      WhatsChatTopBar(
        title = "Blocked Users",
        subtitle = "${blockedUsers.size} blocked",
        onBackClick = onBackClick,
        actions = {
          IconButton(
            onClick = { showBlockPicker = true },
            modifier = Modifier.testTag("blocked_add_button"),
          ) {
            Icon(
              imageVector = Icons.Filled.PersonAddDisabled,
              contentDescription = "Block a user",
              tint = colors.mintAccent,
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
        .navigationBarsPadding(),
      contentAlignment = Alignment.TopCenter,
    ) {
      if (blockedUsers.isEmpty()) {
        WhatsChatEmptyState(
          icon = Icons.Filled.Block,
          title = "No Blocked Users",
          description = "Users you block will no longer be able to send you messages or start calls.",
          actionLabel = if (blockableUsers.isNotEmpty()) "Block a User" else null,
          onAction = { showBlockPicker = true },
        )
      } else {
        LazyColumn(
          modifier = Modifier
            .widthIn(max = 640.dp)
            .fillMaxSize(),
          contentPadding = PaddingValues(horizontal = 20.dp, vertical = 14.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
          items(blockedUsers, key = { it.id }) { user ->
            Surface(
              color = colors.card,
              shape = RoundedCornerShape(16.dp),
              border = BorderStroke(1.dp, colors.divider),
              modifier = Modifier
                .fillMaxWidth()
                .clickable { onOpenUserProfile(user.id) }
                .testTag("blocked_user_${user.username}"),
            ) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
              ) {
                WhatsChatAvatar(
                  displayName = user.displayName,
                  size = 46.dp,
                  avatarResName = user.avatarResName,
                  showOnlineIndicator = false,
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                  Text(
                    text = user.displayName,
                    style = MaterialTheme.typography.titleSmall,
                    color = colors.textPrimary,
                  )
                  Text(
                    text = "@${user.username}",
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textSecondary,
                  )
                }
                OutlinedButton(
                  onClick = { unblockConfirmTarget = user },
                  shape = RoundedCornerShape(10.dp),
                  border = BorderStroke(1.dp, colors.primaryGreen),
                  colors = ButtonDefaults.outlinedButtonColors(contentColor = colors.mintAccent),
                  contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                  modifier = Modifier.testTag("unblock_button_${user.username}"),
                ) {
                  Text("Unblock", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.SemiBold)
                }
              }
            }
          }

          item {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = "You won't receive Internet or Bluetooth messages from blocked users.",
              style = MaterialTheme.typography.bodySmall,
              color = colors.textSecondary,
            )
          }
        }
      }
    }
  }

  unblockConfirmTarget?.let { target ->
    WhatsChatConfirmDialog(
      title = "Unblock ${target.displayName}?",
      message = "@${target.username} will be able to message and call you again.",
      confirmLabel = "Unblock",
      onConfirm = {
        onUnblockUser(target.id)
        unblockConfirmTarget = null
      },
      onDismiss = { unblockConfirmTarget = null },
    )
  }

  if (showBlockPicker) {
    AlertDialog(
      onDismissRequest = { showBlockPicker = false },
      containerColor = colors.card,
      shape = RoundedCornerShape(20.dp),
      title = { Text("Block User", color = colors.textPrimary) },
      text = {
        if (blockableUsers.isEmpty()) {
          Text("No other users available to block.", color = colors.textSecondary)
        } else {
          Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            blockableUsers.forEach { user ->
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(12.dp))
                  .clickable {
                    onBlockUser(user.id)
                    showBlockPicker = false
                  }
                  .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
              ) {
                WhatsChatAvatar(
                  displayName = user.displayName,
                  size = 38.dp,
                  avatarResName = user.avatarResName,
                  showOnlineIndicator = false,
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                  Text(user.displayName, color = colors.textPrimary)
                  Text("@${user.username}", style = MaterialTheme.typography.bodySmall, color = colors.textSecondary)
                }
              }
            }
          }
        }
      },
      confirmButton = {},
      dismissButton = {
        TextButton(onClick = { showBlockPicker = false }) {
          Text("Cancel", color = colors.textSecondary)
        }
      },
    )
  }
}
