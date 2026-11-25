package com.example.features.calls

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
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.domain.models.CallHistoryItem
import com.example.domain.models.CallType
import com.example.domain.models.MessageTransport
import com.example.domain.models.UserProfile
import com.example.ui.components.MainNavDestination
import com.example.ui.components.MockPreviewNoticeBanner
import com.example.ui.components.WhatsChatAvatar
import com.example.ui.components.WhatsChatBottomBar
import com.example.ui.components.WhatsChatEmptyState
import com.example.ui.theme.WhatsChatThemeTokens

@Composable
fun CallsHubScreen(
  currentUser: UserProfile,
  callHistory: List<CallHistoryItem>,
  onNavigateBottom: (MainNavDestination) -> Unit,
  onStartVoiceCall: (String) -> Unit,
  onStartVideoCall: (String) -> Unit,
  onOpenSearchUsers: () -> Unit,
) {
  val colors = WhatsChatThemeTokens.colors

  Scaffold(
    containerColor = colors.backgroundPrimary,
    topBar = {
      Surface(color = colors.backgroundSecondary) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 14.dp),
        ) {
          Text(
            text = "Calls",
            style = MaterialTheme.typography.titleLarge,
            color = colors.textPrimary,
            fontWeight = FontWeight.Bold,
          )
          Text(
            text = "Voice & Group Video Calls (up to 4 participants)",
            style = MaterialTheme.typography.bodySmall,
            color = colors.textSecondary,
          )
        }
      }
    },
    bottomBar = {
      WhatsChatBottomBar(
        currentDestination = MainNavDestination.CALLS,
        currentUser = currentUser,
        onNavigate = onNavigateBottom,
      )
    },
    floatingActionButton = {
      FloatingActionButton(
        onClick = onOpenSearchUsers,
        containerColor = colors.primaryGreen,
        contentColor = colors.backgroundPrimary,
        shape = CircleShape,
        modifier = Modifier.testTag("calls_new_call_fab"),
      ) {
        Icon(imageVector = Icons.Filled.Call, contentDescription = "New Call")
      }
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
          .widthIn(max = 680.dp)
          .fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
      ) {
        item {
          MockPreviewNoticeBanner(
            text = "Frontend phase: Call screens show interactive UI previews. Live WebRTC / LiveKit audio & video will be connected later.",
          )
        }

        if (callHistory.isEmpty()) {
          item {
            Spacer(modifier = Modifier.height(24.dp))
            WhatsChatEmptyState(
              icon = Icons.Filled.Call,
              title = "No Recent Calls",
              description = "Start a voice or up to 4-person video call from any contact or group.",
              actionLabel = "Find Contacts",
              onAction = onOpenSearchUsers,
            )
          }
        } else {
          item {
            Text(
              text = "Recent Calls",
              style = MaterialTheme.typography.titleSmall,
              color = colors.mintAccent,
              modifier = Modifier.padding(top = 4.dp),
            )
          }
          items(callHistory, key = { it.id }) { call ->
            Surface(
              color = colors.card,
              shape = RoundedCornerShape(16.dp),
              border = BorderStroke(1.dp, colors.divider),
              modifier = Modifier
                .fillMaxWidth()
                .clickable {
                  if (call.callType == CallType.VIDEO) {
                    onStartVideoCall(call.conversationId)
                  } else {
                    onStartVoiceCall(call.conversationId)
                  }
                }
                .testTag("calls_history_${call.id}"),
            ) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
              ) {
                WhatsChatAvatar(
                  displayName = call.participantName,
                  size = 48.dp,
                  avatarResName = call.avatarResName,
                  isGroup = call.isGroup,
                  showOnlineIndicator = false,
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                  Text(
                    text = call.participantName,
                    style = MaterialTheme.typography.titleSmall,
                    color = if (call.isMissed) colors.danger else colors.textPrimary,
                  )
                  Spacer(modifier = Modifier.height(3.dp))
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                      imageVector = if (call.isOutgoing) {
                        Icons.AutoMirrored.Filled.CallMade
                      } else {
                        Icons.AutoMirrored.Filled.CallReceived
                      },
                      contentDescription = null,
                      tint = if (call.isMissed) colors.danger else colors.mintAccent,
                      modifier = Modifier.size(14.dp),
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Icon(
                      imageVector = if (call.transport == MessageTransport.BLUETOOTH) {
                        Icons.Filled.Bluetooth
                      } else {
                        Icons.Filled.Wifi
                      },
                      contentDescription = null,
                      tint = colors.textSecondary,
                      modifier = Modifier.size(13.dp),
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                      text = "${call.timestampText} · ${call.durationText}",
                      style = MaterialTheme.typography.bodySmall,
                      color = colors.textSecondary,
                    )
                  }
                }
                IconButton(onClick = { onStartVoiceCall(call.conversationId) }) {
                  Icon(
                    imageVector = Icons.Filled.Call,
                    contentDescription = "Voice Call",
                    tint = colors.mintAccent,
                  )
                }
                IconButton(onClick = { onStartVideoCall(call.conversationId) }) {
                  Icon(
                    imageVector = Icons.Filled.Videocam,
                    contentDescription = "Video Call",
                    tint = colors.mintAccent,
                  )
                }
              }
            }
          }
        }
      }
    }
  }
}
