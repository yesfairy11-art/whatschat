package com.example.features.groups

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
import com.example.domain.models.UserProfile
import com.example.ui.components.WhatsChatAvatar
import com.example.ui.components.WhatsChatCheckCircle
import com.example.ui.components.WhatsChatPrimaryButton
import com.example.ui.components.WhatsChatTextField
import com.example.ui.components.WhatsChatTopBar
import com.example.ui.theme.WhatsChatThemeTokens

@Composable
fun CreateGroupScreen(
  availableUsers: List<UserProfile>,
  initialSelectedIds: List<String> = emptyList(),
  onBackClick: () -> Unit,
  onCreateGroup: (name: String, description: String, memberIds: List<String>) -> Unit,
) {
  val colors = WhatsChatThemeTokens.colors
  var groupName by remember { mutableStateOf("") }
  var groupDescription by remember { mutableStateOf("") }
  var searchQuery by remember { mutableStateOf("") }
  var selectedIds by remember(initialSelectedIds) { mutableStateOf(initialSelectedIds) }
  var groupAvatarUri by remember { mutableStateOf<String?>(null) }
  var nameError by remember { mutableStateOf<String?>(null) }

  val photoPicker = rememberLauncherForActivityResult(
    ActivityResultContracts.PickVisualMedia(),
  ) { uri ->
    if (uri != null) groupAvatarUri = uri.toString()
  }

  val nonBlockedUsers = remember(availableUsers, searchQuery) {
    val q = searchQuery.trim().removePrefix("@").lowercase()
    availableUsers
      .filterNot { it.isBlocked }
      .filter {
        q.isEmpty() || it.displayName.lowercase().contains(q) || it.username.lowercase().contains(q)
      }
  }

  val selectedUsers = remember(availableUsers, selectedIds) {
    availableUsers.filter { it.id in selectedIds }
  }

  Scaffold(
    containerColor = colors.backgroundPrimary,
    topBar = {
      WhatsChatTopBar(
        title = "Create Group",
        subtitle = "${selectedIds.size} selected",
        onBackClick = onBackClick,
      )
    },
    bottomBar = {
      Surface(color = colors.backgroundSecondary) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .imePadding()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        ) {
          WhatsChatPrimaryButton(
            text = "Create Group",
            enabled = groupName.isNotBlank() && selectedIds.isNotEmpty(),
            onClick = {
              if (groupName.isBlank()) {
                nameError = "Group name is required"
                return@WhatsChatPrimaryButton
              }
              onCreateGroup(groupName, groupDescription, selectedIds)
            },
            testTag = "create_group_submit_button",
          )
        }
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
          .widthIn(max = 640.dp)
          .fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
      ) {
        item {
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
          ) {
            Box(
              modifier = Modifier
                .size(72.dp)
                .clickable {
                  photoPicker.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly),
                  )
                }
                .testTag("create_group_avatar"),
            ) {
              WhatsChatAvatar(
                displayName = groupName.ifBlank { "Group" },
                size = 72.dp,
                avatarUri = groupAvatarUri,
                isGroup = true,
                showOnlineIndicator = false,
              )
              Box(
                modifier = Modifier
                  .align(Alignment.BottomEnd)
                  .size(26.dp)
                  .clip(CircleShape)
                  .background(colors.primaryGreen)
                  .border(1.5.dp, colors.backgroundPrimary, CircleShape),
                contentAlignment = Alignment.Center,
              ) {
                Icon(
                  imageVector = Icons.Filled.CameraAlt,
                  contentDescription = "Select group image",
                  tint = colors.backgroundPrimary,
                  modifier = Modifier.size(14.dp),
                )
              }
            }

            Spacer(modifier = Modifier.width(14.dp))

            WhatsChatTextField(
              value = groupName,
              onValueChange = {
                groupName = it
                nameError = null
              },
              label = "Group Name",
              placeholder = "e.g. Friends Group",
              leadingIcon = Icons.Filled.Group,
              isError = nameError != null,
              errorText = nameError,
              modifier = Modifier.weight(1f),
              testTag = "create_group_name_input",
            )
          }
        }

        item {
          WhatsChatTextField(
            value = groupDescription,
            onValueChange = { groupDescription = it },
            label = "Group Description (Optional)",
            placeholder = "Add topic or rules",
            leadingIcon = Icons.Filled.Description,
            testTag = "create_group_description_input",
          )
        }

        if (selectedUsers.isNotEmpty()) {
          item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
              items(selectedUsers, key = { it.id }) { user ->
                InputChip(
                  selected = true,
                  onClick = { selectedIds = selectedIds - user.id },
                  label = { Text(user.displayName, style = MaterialTheme.typography.bodySmall) },
                  trailingIcon = {
                    Icon(
                      imageVector = Icons.Filled.Close,
                      contentDescription = "Remove ${user.displayName}",
                      modifier = Modifier.size(16.dp),
                    )
                  },
                  colors = InputChipDefaults.inputChipColors(
                    selectedContainerColor = colors.elevated,
                    selectedLabelColor = colors.textPrimary,
                    selectedTrailingIconColor = colors.mintAccent,
                  ),
                )
              }
            }
          }
        }

        item {
          WhatsChatTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = "Add Members",
            placeholder = "Search contacts by @username or name",
            leadingIcon = Icons.Filled.Search,
            testTag = "create_group_search_input",
          )
        }

        items(nonBlockedUsers, key = { it.id }) { user ->
          val isSelected = user.id in selectedIds
          Surface(
            color = colors.card,
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
              .fillMaxWidth()
              .clickable {
                selectedIds = if (isSelected) selectedIds - user.id else selectedIds + user.id
              }
              .testTag("group_member_option_${user.username}"),
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
              verticalAlignment = Alignment.CenterVertically,
            ) {
              WhatsChatAvatar(
                displayName = user.displayName,
                size = 44.dp,
                avatarResName = user.avatarResName,
                isOnline = user.isOnline,
              )
              Spacer(modifier = Modifier.width(12.dp))
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = user.displayName,
                  style = MaterialTheme.typography.bodyLarge,
                  color = colors.textPrimary,
                )
                Text(
                  text = "@${user.username}",
                  style = MaterialTheme.typography.bodySmall,
                  color = colors.textSecondary,
                )
              }
              WhatsChatCheckCircle(selected = isSelected)
            }
          }
        }
      }
    }
  }
}
