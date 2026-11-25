package com.example.features.chats

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material.icons.automirrored.filled.CallMade
import androidx.compose.material.icons.automirrored.filled.CallReceived
import androidx.compose.material.icons.filled.AddComment
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.domain.models.CallHistoryItem
import com.example.domain.models.CallType
import com.example.domain.models.ConnectionMode
import com.example.domain.models.ConnectionStatus
import com.example.domain.models.Conversation
import com.example.domain.models.DeliveryStates
import com.example.domain.models.MessageTransport
import com.example.domain.models.UserProfile
import com.example.ui.components.ConnectionStatusPill
import com.example.ui.components.MainNavDestination
import com.example.ui.components.WhatsChatAvatar
import com.example.ui.components.WhatsChatBottomBar
import com.example.ui.components.WhatsChatConfirmDialog
import com.example.ui.components.WhatsChatEmptyState
import com.example.ui.components.WhatsChatLogoBadge
import com.example.ui.theme.WhatsChatThemeTokens

private enum class ChatsHubFilter(val label: String) {
  CHATS("Chats"),
  GROUPS("Groups"),
  CALLS("Calls"),
}

@Composable
fun MainChatsScreen(
  currentUser: UserProfile,
  conversations: List<Conversation>,
  callHistory: List<CallHistoryItem>,
  connectionMode: ConnectionMode,
  connectionStatus: ConnectionStatus,
  connectedBtDeviceName: String?,
  onOpenConversation: (String) -> Unit,
  onOpenVoiceCall: (String) -> Unit,
  onOpenVideoCall: (String) -> Unit,
  onOpenSearchUsers: () -> Unit,
  onOpenCreateGroup: () -> Unit,
  onOpenNearbyDevices: () -> Unit,
  onOpenConnectionPreferences: () -> Unit,
  onOpenProfile: () -> Unit,
  onNavigateBottom: (MainNavDestination) -> Unit,
  onTogglePin: (String) -> Unit,
  onToggleMute: (String) -> Unit,
  onArchiveConversation: (String) -> Unit,
  onDeleteConversation: (String) -> Unit,
) {
  val colors = WhatsChatThemeTokens.colors
  var selectedTab by remember { mutableStateOf(ChatsHubFilter.CHATS) }
  var isSearchActive by remember { mutableStateOf(false) }
  var searchQuery by remember { mutableStateOf("") }
  var showOverflowMenu by remember { mutableStateOf(false) }
  var actionTargetConversation by remember { mutableStateOf<Conversation?>(null) }
  var deleteTargetConversation by remember { mutableStateOf<Conversation?>(null) }

  val filteredConversations = remember(conversations, selectedTab, searchQuery) {
    val active = conversations.filterNot { it.isArchived }
    val byTab = when (selectedTab) {
      ChatsHubFilter.CHATS -> active
      ChatsHubFilter.GROUPS -> active.filter { it.isGroup }
      ChatsHubFilter.CALLS -> emptyList()
    }
    val q = searchQuery.trim().lowercase()
    val searched = if (q.isEmpty()) {
      byTab
    } else {
      byTab.filter {
        it.title.lowercase().contains(q) ||
          it.username.lowercase().contains(q) ||
          it.lastMessagePreview.lowercase().contains(q)
      }
    }
    searched.sortedWith(
      compareByDescending<Conversation> { it.isPinned }.thenByDescending { it.lastMessageTimestamp },
    )
  }

  val filteredCalls = remember(callHistory, searchQuery) {
    val q = searchQuery.trim().lowercase()
    if (q.isEmpty()) {
      callHistory
    } else {
      callHistory.filter {
        it.participantName.lowercase().contains(q) || it.participantUsername.lowercase().contains(q)
      }
    }
  }

  Scaffold(
    containerColor = colors.backgroundPrimary,
    topBar = {
      Surface(color = colors.backgroundSecondary) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
          ) {
            WhatsChatLogoBadge(size = 36.dp)
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "WhatsChat",
                style = MaterialTheme.typography.titleLarge,
                color = colors.textPrimary,
                fontWeight = FontWeight.Bold,
              )
              Text(
                text = "Mode: ${connectionMode.label}",
                style = MaterialTheme.typography.labelSmall,
                color = colors.textSecondary,
              )
            }

            IconButton(
              onClick = {
                isSearchActive = !isSearchActive
                if (!isSearchActive) searchQuery = ""
              },
              modifier = Modifier.testTag("main_search_button"),
            ) {
              Icon(
                imageVector = if (isSearchActive) Icons.Filled.Close else Icons.Filled.Search,
                contentDescription = "Search chats",
                tint = colors.textPrimary,
              )
            }

            Box(modifier = Modifier.clickable(onClick = onOpenProfile).testTag("main_avatar_button")) {
              WhatsChatAvatar(
                displayName = currentUser.displayName,
                size = 38.dp,
                avatarResName = currentUser.avatarResName,
                avatarUri = currentUser.avatarUri,
                isOnline = true,
              )
            }

            Box {
              IconButton(
                onClick = { showOverflowMenu = true },
                modifier = Modifier.testTag("main_more_menu"),
              ) {
                Icon(
                  imageVector = Icons.Filled.MoreVert,
                  contentDescription = "More options",
                  tint = colors.textPrimary,
                )
              }
              DropdownMenu(
                expanded = showOverflowMenu,
                onDismissRequest = { showOverflowMenu = false },
                containerColor = colors.card,
              ) {
                DropdownMenuItem(
                  text = { Text("New Group", color = colors.textPrimary) },
                  leadingIcon = {
                    Icon(Icons.Filled.GroupAdd, contentDescription = null, tint = colors.mintAccent)
                  },
                  onClick = {
                    showOverflowMenu = false
                    onOpenCreateGroup()
                  },
                )
                DropdownMenuItem(
                  text = { Text("Search Users", color = colors.textPrimary) },
                  leadingIcon = {
                    Icon(Icons.Filled.Search, contentDescription = null, tint = colors.mintAccent)
                  },
                  onClick = {
                    showOverflowMenu = false
                    onOpenSearchUsers()
                  },
                )
                DropdownMenuItem(
                  text = { Text("Nearby Devices", color = colors.textPrimary) },
                  leadingIcon = {
                    Icon(Icons.Filled.Bluetooth, contentDescription = null, tint = colors.bluetoothAccent)
                  },
                  onClick = {
                    showOverflowMenu = false
                    onOpenNearbyDevices()
                  },
                )
                DropdownMenuItem(
                  text = { Text("Connection Preferences", color = colors.textPrimary) },
                  leadingIcon = {
                    Icon(Icons.Filled.Wifi, contentDescription = null, tint = colors.mintAccent)
                  },
                  onClick = {
                    showOverflowMenu = false
                    onOpenConnectionPreferences()
                  },
                )
              }
            }
          }

          if (isSearchActive) {
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
              value = searchQuery,
              onValueChange = { searchQuery = it },
              placeholder = { Text("Filter chats, @usernames, or calls...", color = colors.textDisabled) },
              leadingIcon = {
                Icon(Icons.Filled.Search, contentDescription = null, tint = colors.textSecondary)
              },
              singleLine = true,
              shape = RoundedCornerShape(14.dp),
              colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = colors.card,
                unfocusedContainerColor = colors.card,
                focusedBorderColor = colors.primaryGreen,
                unfocusedBorderColor = colors.divider,
                focusedTextColor = colors.textPrimary,
                unfocusedTextColor = colors.textPrimary,
              ),
              modifier = Modifier
                .fillMaxWidth()
                .testTag("main_search_input"),
            )
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Connection status & Bluetooth shortcut row
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
          ) {
            ConnectionStatusPill(
              status = connectionStatus,
              onClick = onOpenConnectionPreferences,
            )

            Surface(
              color = colors.card,
              shape = RoundedCornerShape(999.dp),
              border = BorderStroke(1.dp, colors.divider),
              modifier = Modifier
                .clickable(onClick = onOpenNearbyDevices)
                .testTag("main_nearby_pill"),
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically,
              ) {
                Icon(
                  imageVector = Icons.Filled.Bluetooth,
                  contentDescription = "Nearby Bluetooth",
                  tint = colors.bluetoothAccent,
                  modifier = Modifier.size(14.dp),
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                  text = connectedBtDeviceName?.substringBefore("·")?.trim() ?: "Scan Nearby",
                  style = MaterialTheme.typography.labelSmall,
                  color = colors.textPrimary,
                  maxLines = 1,
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Chats / Groups / Calls sub-tabs
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
          ) {
            ChatsHubFilter.entries.forEach { tab ->
              val selected = selectedTab == tab
              Surface(
                color = if (selected) colors.primaryGreen.copy(alpha = 0.18f) else colors.card,
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(
                  1.dp,
                  if (selected) colors.primaryGreen else colors.divider,
                ),
                modifier = Modifier
                  .weight(1f)
                  .clickable { selectedTab = tab }
                  .testTag("hub_tab_${tab.name.lowercase()}"),
              ) {
                Text(
                  text = tab.label,
                  style = MaterialTheme.typography.labelMedium,
                  color = if (selected) colors.mintAccent else colors.textSecondary,
                  fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                  modifier = Modifier.padding(vertical = 9.dp),
                  textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                )
              }
            }
          }
        }
      }
    },
    bottomBar = {
      WhatsChatBottomBar(
        currentDestination = MainNavDestination.CHATS,
        currentUser = currentUser,
        onNavigate = onNavigateBottom,
      )
    },
    floatingActionButton = {
      FloatingActionButton(
        onClick = {
          if (selectedTab == ChatsHubFilter.GROUPS) onOpenCreateGroup() else onOpenSearchUsers()
        },
        containerColor = colors.primaryGreen,
        contentColor = colors.backgroundPrimary,
        shape = CircleShape,
        modifier = Modifier.testTag("main_new_chat_fab"),
      ) {
        Icon(
          imageVector = if (selectedTab == ChatsHubFilter.GROUPS) {
            Icons.Filled.GroupAdd
          } else {
            Icons.Filled.AddComment
          },
          contentDescription = "New Chat",
        )
      }
    },
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding),
      contentAlignment = Alignment.TopCenter,
    ) {
      if (selectedTab == ChatsHubFilter.CALLS) {
        if (filteredCalls.isEmpty()) {
          WhatsChatEmptyState(
            icon = Icons.Filled.Call,
            title = "No Call History",
            description = "Voice and group video calls (up to 4 participants) will appear here.",
          )
        } else {
          LazyColumn(
            modifier = Modifier
              .widthIn(max = 680.dp)
              .fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
          ) {
            items(filteredCalls, key = { it.id }) { call ->
              CallHistoryRow(
                item = call,
                onCallClick = {
                  if (call.callType == CallType.VIDEO) {
                    onOpenVideoCall(call.conversationId)
                  } else {
                    onOpenVoiceCall(call.conversationId)
                  }
                },
              )
            }
          }
        }
      } else {
        if (filteredConversations.isEmpty()) {
          WhatsChatEmptyState(
            icon = Icons.Filled.ChatBubbleOutline,
            title = if (searchQuery.isNotBlank()) "No Matching Chats" else "No Conversations Yet",
            description = "Find friends by @username or discover nearby WhatsChat devices over Bluetooth.",
            actionLabel = "Search Users",
            onAction = onOpenSearchUsers,
          )
        } else {
          LazyColumn(
            modifier = Modifier
              .widthIn(max = 680.dp)
              .fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
          ) {
            items(filteredConversations, key = { it.id }) { conv ->
              ChatListItemRow(
                conversation = conv,
                onClick = { onOpenConversation(conv.id) },
                onLongPress = { actionTargetConversation = conv },
              )
            }
          }
        }
      }
    }
  }

  // Long-press conversation options dialog
  actionTargetConversation?.let { target ->
    WhatsChatConfirmDialog(
      title = target.title,
      message = "@${target.username} · Choose an action for this conversation",
      confirmLabel = if (target.isPinned) "Unpin Chat" else "Pin Chat",
      onConfirm = {
        onTogglePin(target.id)
        actionTargetConversation = null
      },
      onDismiss = { actionTargetConversation = null },
    )
    // Better: custom modal dialog with Pin, Mute, Archive, Delete!
  }

  if (actionTargetConversation != null) {
    val target = actionTargetConversation!!
    androidx.compose.material3.AlertDialog(
      onDismissRequest = { actionTargetConversation = null },
      containerColor = colors.card,
      shape = RoundedCornerShape(20.dp),
      title = {
        Text(
          text = target.title,
          style = MaterialTheme.typography.titleMedium,
          color = colors.textPrimary,
        )
      },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
          Text(
            text = "@${target.username}",
            style = MaterialTheme.typography.bodySmall,
            color = colors.textSecondary,
          )
          Spacer(modifier = Modifier.height(8.dp))
          ConversationActionRow(
            icon = Icons.Filled.PushPin,
            label = if (target.isPinned) "Unpin Conversation" else "Pin Conversation",
            onClick = {
              onTogglePin(target.id)
              actionTargetConversation = null
            },
          )
          ConversationActionRow(
            icon = Icons.Filled.NotificationsOff,
            label = if (target.isMuted) "Unmute Notifications" else "Mute Notifications",
            onClick = {
              onToggleMute(target.id)
              actionTargetConversation = null
            },
          )
          ConversationActionRow(
            icon = Icons.Filled.Close,
            label = "Archive Conversation",
            onClick = {
              onArchiveConversation(target.id)
              actionTargetConversation = null
            },
          )
          HorizontalDivider(color = colors.divider, modifier = Modifier.padding(vertical = 4.dp))
          ConversationActionRow(
            icon = Icons.Filled.Delete,
            label = "Delete Conversation",
            tint = colors.danger,
            onClick = {
              actionTargetConversation = null
              deleteTargetConversation = target
            },
          )
        }
      },
      confirmButton = {},
      dismissButton = {
        androidx.compose.material3.TextButton(onClick = { actionTargetConversation = null }) {
          Text("Close", color = colors.textSecondary)
        }
      },
    )
  }

  deleteTargetConversation?.let { target ->
    WhatsChatConfirmDialog(
      title = "Delete Chat?",
      message = "Remove local conversation history with ${target.title}?",
      confirmLabel = "Delete",
      isDestructive = true,
      onConfirm = {
        onDeleteConversation(target.id)
        deleteTargetConversation = null
      },
      onDismiss = { deleteTargetConversation = null },
    )
  }
}

@Composable
private fun ConversationActionRow(
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  label: String,
  tint: androidx.compose.ui.graphics.Color = WhatsChatThemeTokens.colors.textPrimary,
  onClick: () -> Unit,
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(12.dp))
      .clickable(onClick = onClick)
      .padding(horizontal = 8.dp, vertical = 12.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = Modifier.size(20.dp))
    Spacer(modifier = Modifier.width(12.dp))
    Text(text = label, style = MaterialTheme.typography.bodyLarge, color = tint)
  }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ChatListItemRow(
  conversation: Conversation,
  onClick: () -> Unit,
  onLongPress: () -> Unit,
) {
  val colors = WhatsChatThemeTokens.colors
  Surface(
    color = colors.card,
    shape = RoundedCornerShape(16.dp),
    border = if (conversation.isPinned) {
      BorderStroke(1.dp, colors.primaryGreen.copy(alpha = 0.35f))
    } else {
      null
    },
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(16.dp))
      .combinedClickable(
        onClick = onClick,
        onLongClick = onLongPress,
      )
      .testTag("chat_item_${conversation.id}"),
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 14.dp, vertical = 12.dp),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      WhatsChatAvatar(
        displayName = conversation.title,
        size = 52.dp,
        avatarResName = conversation.avatarResName,
        avatarUri = conversation.avatarUri,
        isGroup = conversation.isGroup,
        isOnline = conversation.isOnline,
      )

      Spacer(modifier = Modifier.width(12.dp))

      Column(modifier = Modifier.weight(1f)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Text(
            text = conversation.title,
            style = MaterialTheme.typography.titleSmall,
            color = colors.textPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f, fill = false),
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "@${conversation.username}",
            style = MaterialTheme.typography.labelSmall,
            color = colors.textSecondary,
            maxLines = 1,
          )
          Spacer(modifier = Modifier.weight(1f))
          Text(
            text = conversation.lastMessageTime,
            style = MaterialTheme.typography.labelSmall,
            color = if (conversation.unreadCount > 0) colors.mintAccent else colors.textSecondary,
          )
        }

        Spacer(modifier = Modifier.height(5.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
        ) {
          // Delivery status & transport icon
          val deliveryIcon = when (conversation.lastMessageDelivery) {
            DeliveryStates.PENDING -> Icons.Filled.Schedule
            DeliveryStates.DELIVERED -> Icons.Filled.DoneAll
            else -> Icons.Filled.Done
          }
          val deliveryColor = when (conversation.lastMessageDelivery) {
            DeliveryStates.PENDING -> colors.warning
            DeliveryStates.DELIVERED -> colors.mintAccent
            else -> colors.textSecondary
          }
          Icon(
            imageVector = deliveryIcon,
            contentDescription = conversation.lastMessageDelivery,
            tint = deliveryColor,
            modifier = Modifier.size(15.dp),
          )
          Spacer(modifier = Modifier.width(4.dp))
          Icon(
            imageVector = if (conversation.lastMessageTransport == MessageTransport.BLUETOOTH) {
              Icons.Filled.Bluetooth
            } else {
              Icons.Filled.Wifi
            },
            contentDescription = conversation.lastMessageTransport,
            tint = if (conversation.lastMessageTransport == MessageTransport.BLUETOOTH) {
              colors.bluetoothAccent
            } else {
              colors.textSecondary
            },
            modifier = Modifier.size(13.dp),
          )
          Spacer(modifier = Modifier.width(6.dp))

          Text(
            text = conversation.lastMessagePreview,
            style = MaterialTheme.typography.bodySmall,
            color = colors.textSecondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
          )

          if (conversation.isMuted) {
            Spacer(modifier = Modifier.width(6.dp))
            Icon(
              imageVector = Icons.Filled.NotificationsOff,
              contentDescription = "Muted",
              tint = colors.textDisabled,
              modifier = Modifier.size(15.dp),
            )
          }

          if (conversation.isPinned) {
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
              imageVector = Icons.Filled.PushPin,
              contentDescription = "Pinned",
              tint = colors.mintAccent,
              modifier = Modifier.size(14.dp),
            )
          }

          if (conversation.unreadCount > 0) {
            Spacer(modifier = Modifier.width(8.dp))
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(999.dp))
                .background(colors.primaryGreen)
                .padding(horizontal = 8.dp, vertical = 2.dp),
              contentAlignment = Alignment.Center,
            ) {
              Text(
                text = conversation.unreadCount.toString(),
                style = MaterialTheme.typography.labelSmall,
                color = colors.backgroundPrimary,
                fontWeight = FontWeight.Bold,
              )
            }
          }
        }
      }
    }
  }
}

@Composable
private fun CallHistoryRow(
  item: CallHistoryItem,
  onCallClick: () -> Unit,
) {
  val colors = WhatsChatThemeTokens.colors
  Surface(
    color = colors.card,
    shape = RoundedCornerShape(16.dp),
    modifier = Modifier
      .fillMaxWidth()
      .clickable(onClick = onCallClick),
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 14.dp, vertical = 12.dp),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      WhatsChatAvatar(
        displayName = item.participantName,
        size = 48.dp,
        avatarResName = item.avatarResName,
        isGroup = item.isGroup,
        showOnlineIndicator = false,
      )
      Spacer(modifier = Modifier.width(12.dp))
      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = item.participantName,
          style = MaterialTheme.typography.titleSmall,
          color = if (item.isMissed) colors.danger else colors.textPrimary,
        )
        Spacer(modifier = Modifier.height(3.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = if (item.isOutgoing) {
              Icons.AutoMirrored.Filled.CallMade
            } else {
              Icons.AutoMirrored.Filled.CallReceived
            },
            contentDescription = null,
            tint = if (item.isMissed) colors.danger else colors.mintAccent,
            modifier = Modifier.size(14.dp),
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "${item.timestampText} · ${item.durationText}",
            style = MaterialTheme.typography.bodySmall,
            color = colors.textSecondary,
          )
        }
      }
      IconButton(onClick = onCallClick) {
        Icon(
          imageVector = if (item.callType == CallType.VIDEO) {
            Icons.Filled.Videocam
          } else {
            Icons.Filled.Call
          },
          contentDescription = "Start call preview",
          tint = colors.mintAccent,
        )
      }
    }
  }
}
