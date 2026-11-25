package com.example.features.contacts

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.PersonRemove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.domain.models.UserProfile
import com.example.ui.components.MainNavDestination
import com.example.ui.components.WhatsChatAvatar
import com.example.ui.components.WhatsChatBottomBar
import com.example.ui.components.WhatsChatEmptyState
import com.example.ui.theme.WhatsChatThemeTokens

@Composable
fun SearchUsersScreen(
  currentUser: UserProfile? = null,
  users: List<UserProfile>,
  showBottomBar: Boolean,
  onBackClick: (() -> Unit)?,
  onNavigateBottom: (MainNavDestination) -> Unit,
  onToggleContact: (String) -> Unit,
  onMessageUser: (UserProfile) -> Unit,
  onOpenUserProfile: (String) -> Unit,
) {
  val colors = WhatsChatThemeTokens.colors
  var query by remember { mutableStateOf("") }

  val results = remember(users, query) {
    val q = query.trim().removePrefix("@").lowercase()
    val nonBlocked = users.filterNot { it.isBlocked }
    if (q.isEmpty()) {
      nonBlocked
    } else {
      nonBlocked.filter {
        it.username.lowercase().contains(q) || it.displayName.lowercase().contains(q)
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
            .padding(horizontal = 16.dp, vertical = 12.dp),
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            if (onBackClick != null) {
              IconButton(
                onClick = onBackClick,
                modifier = Modifier.testTag("search_users_back"),
              ) {
                Icon(
                  imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                  contentDescription = "Back",
                  tint = colors.textPrimary,
                )
              }
            }
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = if (showBottomBar) "Contacts & Discovery" else "Search Users",
                style = MaterialTheme.typography.titleLarge,
                color = colors.textPrimary,
              )
              Text(
                text = "Find people by @username or display name",
                style = MaterialTheme.typography.bodySmall,
                color = colors.textSecondary,
              )
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            placeholder = { Text("Search @username or name...", color = colors.textDisabled) },
            leadingIcon = {
              Icon(Icons.Filled.Search, contentDescription = null, tint = colors.mintAccent)
            },
            trailingIcon = {
              if (query.isNotEmpty()) {
                IconButton(onClick = { query = "" }) {
                  Icon(Icons.Filled.Close, contentDescription = "Clear search", tint = colors.textSecondary)
                }
              }
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
              .testTag("search_users_input"),
          )
        }
      }
    },
    bottomBar = {
      if (showBottomBar) {
        WhatsChatBottomBar(
          currentDestination = MainNavDestination.CONTACTS,
          currentUser = currentUser,
          onNavigate = onNavigateBottom,
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
      if (results.isEmpty()) {
        WhatsChatEmptyState(
          icon = Icons.Filled.SearchOff,
          title = "No Users Found",
          description = "No matching @username or display name found for \"$query\".",
          actionLabel = "Clear Search",
          onAction = { query = "" },
        )
      } else {
        LazyColumn(
          modifier = Modifier
            .widthIn(max = 680.dp)
            .fillMaxSize(),
          contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
          items(results, key = { it.id }) { user ->
            Surface(
              color = colors.card,
              shape = RoundedCornerShape(16.dp),
              border = BorderStroke(1.dp, colors.divider),
              modifier = Modifier
                .fillMaxWidth()
                .clickable { onOpenUserProfile(user.id) }
                .testTag("user_result_${user.username}"),
            ) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
              ) {
                WhatsChatAvatar(
                  displayName = user.displayName,
                  size = 50.dp,
                  avatarResName = user.avatarResName,
                  isOnline = user.isOnline,
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                  Text(
                    text = user.displayName,
                    style = MaterialTheme.typography.titleSmall,
                    color = colors.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                  )
                  Text(
                    text = "@${user.username}",
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.mintAccent,
                  )
                  Text(
                    text = user.about,
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.textSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                  )
                }

                Spacer(modifier = Modifier.width(8.dp))

                OutlinedButton(
                  onClick = { onToggleContact(user.id) },
                  shape = RoundedCornerShape(10.dp),
                  border = BorderStroke(
                    1.dp,
                    if (user.isContact) colors.divider else colors.primaryGreen,
                  ),
                  colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = if (user.isContact) colors.textSecondary else colors.mintAccent,
                  ),
                  contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                  modifier = Modifier.testTag("add_contact_${user.username}"),
                ) {
                  Icon(
                    imageVector = if (user.isContact) Icons.Filled.PersonRemove else Icons.Filled.PersonAdd,
                    contentDescription = if (user.isContact) "Remove Contact" else "Add Contact",
                    modifier = Modifier.size(16.dp),
                  )
                  Spacer(modifier = Modifier.width(4.dp))
                  Text(
                    text = if (user.isContact) "Added" else "Add",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                  )
                }

                Spacer(modifier = Modifier.width(6.dp))

                IconButton(
                  onClick = { onMessageUser(user) },
                  modifier = Modifier.testTag("message_user_${user.username}"),
                ) {
                  Icon(
                    imageVector = Icons.Filled.ChatBubble,
                    contentDescription = "Message ${user.displayName}",
                    tint = colors.primaryGreen,
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
