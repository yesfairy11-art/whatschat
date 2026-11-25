package com.example.features.profile

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.unit.dp
import com.example.core.common.UsernameValidator
import com.example.domain.models.ChatMessage
import com.example.domain.models.Conversation
import com.example.domain.models.MessageTypes
import com.example.domain.models.UserProfile
import com.example.ui.components.SettingsRowItem
import com.example.ui.components.WhatsChatAvatar
import com.example.ui.components.WhatsChatConfirmDialog
import com.example.ui.components.WhatsChatPrimaryButton
import com.example.ui.components.WhatsChatTextField
import com.example.ui.components.WhatsChatTopBar
import com.example.ui.theme.WhatsChatThemeTokens

@Composable
fun ProfileScreen(
  user: UserProfile,
  isCurrentUser: Boolean,
  conversation: Conversation? = null,
  sharedMediaMessages: List<ChatMessage> = emptyList(),
  isUsernameAvailable: (String) -> Boolean,
  onBackClick: () -> Unit,
  onUpdateProfile: (
    displayName: String,
    username: String,
    about: String,
    avatarUri: String?,
    onDone: () -> Unit,
    onError: (String) -> Unit,
  ) -> Unit,
  onOpenPrivacySettings: () -> Unit,
  onMessageUser: () -> Unit,
  onOpenVoiceCall: () -> Unit = {},
  onOpenVideoCall: () -> Unit = {},
  onOpenMediaSharing: () -> Unit = {},
  onToggleContact: () -> Unit,
  onToggleBlock: () -> Unit,
  onUpdateChatSettings: (
    chatLock: Boolean?,
    restrictedChat: Boolean?,
    translateMessages: Boolean?,
    mediaVisibility: Boolean?,
    disappearingMessages: String?,
    isFavourite: Boolean?,
  ) -> Unit = { _, _, _, _, _, _ -> },
) {
  val colors = WhatsChatThemeTokens.colors
  val context = LocalContext.current

  var showEditDialog by remember { mutableStateOf(false) }
  var showUsernameDialog by remember { mutableStateOf(false) }
  var showOverflowMenu by remember { mutableStateOf(false) }
  var showDisappearingDialog by remember { mutableStateOf(false) }
  var showMediaVisibilityDialog by remember { mutableStateOf(false) }
  var showReportConfirm by remember { mutableStateOf(false) }

  var draftName by remember(user.displayName) { mutableStateOf(user.displayName) }
  var draftAbout by remember(user.about) { mutableStateOf(user.about) }
  var draftUsername by remember(user.username) { mutableStateOf(user.username) }
  var usernameError by remember { mutableStateOf<String?>(null) }

  val photoPicker = rememberLauncherForActivityResult(
    ActivityResultContracts.PickVisualMedia(),
  ) { uri ->
    if (uri != null && isCurrentUser) {
      onUpdateProfile(
        user.displayName,
        user.username,
        user.about,
        uri.toString(),
        {},
        {},
      )
    }
  }

  val mediaCount = sharedMediaMessages.count {
    it.messageType == MessageTypes.IMAGE || it.messageType == MessageTypes.VIDEO || it.messageType == MessageTypes.FILE
  }.coerceAtLeast(2)

  Scaffold(
    containerColor = colors.backgroundPrimary,
    topBar = {
      WhatsChatTopBar(
        title = if (isCurrentUser) "My Profile" else "",
        onBackClick = onBackClick,
        actions = {
          if (!isCurrentUser) {
            Box {
              IconButton(onClick = { showOverflowMenu = true }) {
                Icon(Icons.Filled.MoreVert, contentDescription = "Account options", tint = colors.textPrimary)
              }
              DropdownMenu(
                expanded = showOverflowMenu,
                onDismissRequest = { showOverflowMenu = false },
                containerColor = colors.card,
              ) {
                DropdownMenuItem(
                  text = {
                    Text(
                      if (user.isContact) "Remove from Contacts" else "Add to Contacts",
                      color = colors.textPrimary,
                    )
                  },
                  onClick = {
                    showOverflowMenu = false
                    onToggleContact()
                  },
                )
                DropdownMenuItem(
                  text = { Text("View in address book", color = colors.textPrimary) },
                  onClick = {
                    showOverflowMenu = false
                    Toast.makeText(context, "@${user.username}", Toast.LENGTH_SHORT).show()
                  },
                )
              }
            }
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
      if (isCurrentUser) {
        LazyColumn(
          modifier = Modifier
            .widthIn(max = 620.dp)
            .fillMaxSize(),
          contentPadding = PaddingValues(horizontal = 20.dp, vertical = 18.dp),
          verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
          item {
            Column(
              modifier = Modifier.fillMaxWidth(),
              horizontalAlignment = Alignment.CenterHorizontally,
            ) {
              Box(
                modifier = Modifier
                  .size(116.dp)
                  .clickable {
                    photoPicker.launch(
                      PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly),
                    )
                  },
              ) {
                WhatsChatAvatar(
                  displayName = user.displayName,
                  size = 112.dp,
                  avatarResName = user.avatarResName,
                  avatarUri = user.avatarUri,
                  isOnline = user.isOnline,
                )
                Box(
                  modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(colors.primaryGreen)
                    .border(2.dp, colors.backgroundPrimary, CircleShape),
                  contentAlignment = Alignment.Center,
                ) {
                  Icon(
                    imageVector = Icons.Filled.CameraAlt,
                    contentDescription = "Change profile photo",
                    tint = colors.backgroundPrimary,
                    modifier = Modifier.size(18.dp),
                  )
                }
              }

              Spacer(modifier = Modifier.height(14.dp))
              Text(
                text = user.displayName,
                style = MaterialTheme.typography.headlineSmall,
                color = colors.textPrimary,
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = "@${user.username}",
                style = MaterialTheme.typography.titleSmall,
                color = colors.mintAccent,
              )
            }
          }

          item {
            Surface(
              color = colors.card,
              shape = RoundedCornerShape(16.dp),
              border = BorderStroke(1.dp, colors.divider),
              modifier = Modifier.fillMaxWidth(),
            ) {
              Column(modifier = Modifier.padding(16.dp)) {
                Text(
                  text = "About Me",
                  style = MaterialTheme.typography.labelMedium,
                  color = colors.mintAccent,
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                  text = user.about.ifBlank { "Hey there! I am using WhatsChat." },
                  style = MaterialTheme.typography.bodyLarge,
                  color = colors.textPrimary,
                )
              }
            }
          }

          item {
            WhatsChatPrimaryButton(
              text = "Edit Profile",
              icon = Icons.Filled.Edit,
              onClick = {
                draftName = user.displayName
                draftAbout = user.about
                showEditDialog = true
              },
              testTag = "profile_edit_button",
            )
          }
          item {
            SettingsRowItem(
              icon = Icons.Filled.AlternateEmail,
              title = "Change Username",
              subtitle = "Current: @${user.username} (3-30 chars)",
              onClick = {
                draftUsername = user.username
                usernameError = null
                showUsernameDialog = true
              },
              testTag = "profile_change_username_row",
            )
          }
          item {
            SettingsRowItem(
              icon = Icons.Filled.Info,
              title = "Account Information",
              subtitle = "Email: ${user.email ?: "Optional (not set)"} · Phone: Not required",
              showChevron = false,
              onClick = {},
            )
          }
          item {
            SettingsRowItem(
              icon = Icons.Filled.Lock,
              title = "Privacy Controls",
              subtitle = "Last seen, profile photo & read receipts",
              onClick = onOpenPrivacySettings,
            )
          }
        }
      } else {
        // Other-user Account view matching uploaded reference (12.11.40_PM_19c67ce1.jpeg)
        val chatLock = conversation?.chatLock == true
        val restrictedChat = conversation?.restrictedChat == true
        val translateMessages = conversation?.translateMessages == true
        val mediaVisibility = conversation?.mediaVisibility ?: false
        val disappearing = conversation?.disappearingMessages ?: "7 days"
        val isFavourite = conversation?.isFavourite == true

        LazyColumn(
          modifier = Modifier
            .widthIn(max = 640.dp)
            .fillMaxSize(),
          contentPadding = PaddingValues(bottom = 28.dp),
        ) {
          item {
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .background(colors.backgroundSecondary)
                .padding(horizontal = 20.dp, vertical = 14.dp),
              horizontalAlignment = Alignment.CenterHorizontally,
            ) {
              WhatsChatAvatar(
                displayName = user.displayName,
                size = 112.dp,
                avatarResName = user.avatarResName,
                avatarUri = user.avatarUri,
                isOnline = user.isOnline,
              )
              Spacer(modifier = Modifier.height(14.dp))
              Text(
                text = user.displayName,
                style = MaterialTheme.typography.headlineSmall,
                color = colors.textPrimary,
                fontWeight = FontWeight.SemiBold,
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = "@${user.username} · ${if (user.isOnline) "Online" else user.lastSeenText}",
                style = MaterialTheme.typography.bodySmall,
                color = if (user.isOnline) colors.mintAccent else colors.textSecondary,
              )
              Spacer(modifier = Modifier.height(16.dp))

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
              ) {
                OtherUserQuickAction(
                  icon = Icons.Filled.ChatBubble,
                  label = "Message",
                  onClick = onMessageUser,
                  modifier = Modifier.weight(1f),
                  testTag = "profile_message_user_button",
                )
                OtherUserQuickAction(
                  icon = Icons.Filled.Call,
                  label = "Audio",
                  onClick = onOpenVoiceCall,
                  modifier = Modifier.weight(1f),
                )
                OtherUserQuickAction(
                  icon = Icons.Filled.Videocam,
                  label = "Video",
                  onClick = onOpenVideoCall,
                  modifier = Modifier.weight(1f),
                )
              }
            }
            HorizontalDivider(color = colors.divider)
          }

          item {
            // Media, links and docs row
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onOpenMediaSharing)
                .padding(horizontal = 20.dp, vertical = 16.dp),
              verticalAlignment = Alignment.CenterVertically,
            ) {
              Text(
                text = "Media, links and docs",
                style = MaterialTheme.typography.bodyLarge,
                color = colors.textPrimary,
                modifier = Modifier.weight(1f),
              )
              Text(
                text = mediaCount.toString(),
                style = MaterialTheme.typography.bodyMedium,
                color = colors.textSecondary,
              )
              Spacer(modifier = Modifier.width(4.dp))
              Icon(
                imageVector = Icons.Filled.ChevronRight,
                contentDescription = null,
                tint = colors.textSecondary,
              )
            }
            HorizontalDivider(color = colors.divider)
          }

          item {
            OtherUserOptionRow(
              icon = Icons.Filled.FolderOpen,
              title = "Manage storage",
              subtitle = "33 kB",
              onClick = {
                Toast.makeText(context, "Local Room storage: 33 kB", Toast.LENGTH_SHORT).show()
              },
            )
            OtherUserOptionRow(
              icon = Icons.Filled.AddPhotoAlternate,
              title = "Media visibility",
              subtitle = if (mediaVisibility) "On" else "Off",
              onClick = { showMediaVisibilityDialog = true },
            )
            OtherUserOptionRow(
              icon = Icons.Filled.BookmarkBorder,
              title = "Kept messages",
              onClick = {
                Toast.makeText(context, "No kept messages yet", Toast.LENGTH_SHORT).show()
              },
            )
            OtherUserOptionRow(
              icon = Icons.Filled.Timer,
              title = "Disappearing messages",
              subtitle = disappearing,
              onClick = { showDisappearingDialog = true },
              testTag = "profile_disappearing_row",
            )
            OtherUserSwitchRow(
              icon = Icons.Filled.Security,
              title = "Chat lock",
              subtitle = "Lock and hide this chat on this device.",
              checked = chatLock,
              onCheckedChange = { locked ->
                onUpdateChatSettings(locked, null, null, null, null, null)
              },
            )
            OtherUserSwitchRow(
              icon = Icons.Filled.Shield,
              title = "Restricted chat",
              subtitle = if (restrictedChat) "On" else "Off",
              checked = restrictedChat,
              onCheckedChange = { restricted ->
                onUpdateChatSettings(null, restricted, null, null, null, null)
              },
            )
            OtherUserSwitchRow(
              icon = Icons.Filled.Language,
              title = "Translate messages",
              checked = translateMessages,
              onCheckedChange = { translate ->
                onUpdateChatSettings(null, null, translate, null, null, null)
              },
            )
            OtherUserOptionRow(
              icon = if (isFavourite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
              title = if (isFavourite) "Remove from Favourites" else "Add to Favourites",
              iconTint = if (isFavourite) colors.mintAccent else colors.textSecondary,
              onClick = {
                onUpdateChatSettings(null, null, null, null, null, !isFavourite)
              },
            )
            OtherUserOptionRow(
              icon = Icons.Filled.PersonAdd,
              title = if (user.isContact) "Remove from Contacts" else "Add to Contacts",
              onClick = onToggleContact,
              testTag = "profile_toggle_contact_button",
            )

            HorizontalDivider(color = colors.divider, modifier = Modifier.padding(vertical = 6.dp))

            OtherUserOptionRow(
              icon = Icons.Filled.Block,
              title = if (user.isBlocked) "Unblock @${user.username}" else "Block @${user.username}",
              textColor = colors.danger,
              iconTint = colors.danger,
              onClick = onToggleBlock,
              testTag = "profile_block_user_button",
            )
            OtherUserOptionRow(
              icon = Icons.Filled.Report,
              title = "Report ${user.displayName}",
              textColor = colors.danger,
              iconTint = colors.danger,
              onClick = { showReportConfirm = true },
            )
          }
        }
      }
    }
  }

  if (showDisappearingDialog) {
    val options = listOf("Off", "24 hours", "7 days", "90 days")
    val currentSelection = conversation?.disappearingMessages ?: "7 days"
    AlertDialog(
      onDismissRequest = { showDisappearingDialog = false },
      containerColor = colors.card,
      shape = RoundedCornerShape(20.dp),
      title = { Text("Disappearing messages", color = colors.textPrimary) },
      text = {
        Column {
          options.forEach { opt ->
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .clickable {
                  onUpdateChatSettings(null, null, null, null, opt, null)
                  showDisappearingDialog = false
                }
                .padding(vertical = 6.dp),
              verticalAlignment = Alignment.CenterVertically,
            ) {
              RadioButton(
                selected = opt == currentSelection,
                onClick = {
                  onUpdateChatSettings(null, null, null, null, opt, null)
                  showDisappearingDialog = false
                },
                colors = RadioButtonDefaults.colors(selectedColor = colors.primaryGreen),
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(opt, color = colors.textPrimary)
            }
          }
        }
      },
      confirmButton = {},
      dismissButton = {
        TextButton(onClick = { showDisappearingDialog = false }) {
          Text("Cancel", color = colors.textSecondary)
        }
      },
    )
  }

  if (showMediaVisibilityDialog) {
    AlertDialog(
      onDismissRequest = { showMediaVisibilityDialog = false },
      containerColor = colors.card,
      shape = RoundedCornerShape(20.dp),
      title = { Text("Media visibility", color = colors.textPrimary) },
      text = {
        Text(
          "Show newly downloaded media from ${user.displayName} in your gallery?",
          color = colors.textSecondary,
        )
      },
      confirmButton = {
        TextButton(
          onClick = {
            onUpdateChatSettings(null, null, null, true, null, null)
            showMediaVisibilityDialog = false
          },
        ) {
          Text("On", color = colors.mintAccent)
        }
      },
      dismissButton = {
        TextButton(
          onClick = {
            onUpdateChatSettings(null, null, null, false, null, null)
            showMediaVisibilityDialog = false
          },
        ) {
          Text("Off", color = colors.textSecondary)
        }
      },
    )
  }

  if (showReportConfirm) {
    WhatsChatConfirmDialog(
      title = "Report ${user.displayName}?",
      message = "Report @${user.username} for spam or unwanted messages?",
      confirmLabel = "Report",
      isDestructive = true,
      onConfirm = {
        showReportConfirm = false
        Toast.makeText(context, "Report submitted locally", Toast.LENGTH_SHORT).show()
      },
      onDismiss = { showReportConfirm = false },
    )
  }

  if (showEditDialog) {
    AlertDialog(
      onDismissRequest = { showEditDialog = false },
      containerColor = colors.card,
      shape = RoundedCornerShape(20.dp),
      title = { Text("Edit Profile", color = colors.textPrimary) },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
          WhatsChatTextField(
            value = draftName,
            onValueChange = { draftName = it },
            label = "Display Name",
          )
          WhatsChatTextField(
            value = draftAbout,
            onValueChange = { if (it.length <= 160) draftAbout = it },
            label = "About",
            singleLine = false,
            maxLines = 3,
            supportingText = "${draftAbout.length}/160",
          )
        }
      },
      confirmButton = {
        TextButton(
          onClick = {
            onUpdateProfile(
              draftName,
              user.username,
              draftAbout,
              null,
              { showEditDialog = false },
              {},
            )
          },
        ) {
          Text("Save", color = colors.mintAccent)
        }
      },
      dismissButton = {
        TextButton(onClick = { showEditDialog = false }) {
          Text("Cancel", color = colors.textSecondary)
        }
      },
    )
  }

  if (showUsernameDialog) {
    val validation = UsernameValidator.validate(draftUsername)
    val available = validation.isValid &&
      (validation.normalized == user.username || isUsernameAvailable(validation.normalized))

    AlertDialog(
      onDismissRequest = { showUsernameDialog = false },
      containerColor = colors.card,
      shape = RoundedCornerShape(20.dp),
      title = { Text("Change @Username", color = colors.textPrimary) },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Text(
            text = "Usernames are 3-30 characters using letters, numbers, and underscores only.",
            style = MaterialTheme.typography.bodySmall,
            color = colors.textSecondary,
          )
          WhatsChatTextField(
            value = draftUsername,
            onValueChange = {
              draftUsername = it
              usernameError = null
            },
            label = "New Username",
            leadingIcon = Icons.Filled.AlternateEmail,
            isError = usernameError != null || (draftUsername.isNotEmpty() && (!validation.isValid || !available)),
            errorText = usernameError ?: when {
              !validation.isValid -> validation.errorMessage
              !available -> "@${validation.normalized} is unavailable"
              else -> null
            },
            testTag = "profile_new_username_input",
          )
        }
      },
      confirmButton = {
        TextButton(
          onClick = {
            if (!validation.isValid) {
              usernameError = validation.errorMessage
              return@TextButton
            }
            if (!available) {
              usernameError = "@${validation.normalized} is unavailable"
              return@TextButton
            }
            onUpdateProfile(
              user.displayName,
              validation.normalized,
              user.about,
              null,
              { showUsernameDialog = false },
              { err -> usernameError = err },
            )
          },
        ) {
          Text("Update Username", color = colors.mintAccent)
        }
      },
      dismissButton = {
        TextButton(onClick = { showUsernameDialog = false }) {
          Text("Cancel", color = colors.textSecondary)
        }
      },
    )
  }
}

@Composable
private fun OtherUserQuickAction(
  icon: ImageVector,
  label: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  testTag: String = "profile_quick_action",
) {
  val colors = WhatsChatThemeTokens.colors
  Surface(
    color = colors.card,
    shape = RoundedCornerShape(14.dp),
    border = BorderStroke(1.dp, colors.divider),
    modifier = modifier
      .clip(RoundedCornerShape(14.dp))
      .clickable(onClick = onClick)
      .testTag(testTag),
  ) {
    Column(
      modifier = Modifier.padding(vertical = 10.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      Icon(
        imageVector = icon,
        contentDescription = label,
        tint = colors.mintAccent,
        modifier = Modifier.size(20.dp),
      )
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = label,
        style = MaterialTheme.typography.labelSmall,
        color = colors.textPrimary,
      )
    }
  }
}

@Composable
private fun OtherUserOptionRow(
  icon: ImageVector,
  title: String,
  subtitle: String? = null,
  textColor: Color = WhatsChatThemeTokens.colors.textPrimary,
  iconTint: Color = WhatsChatThemeTokens.colors.textSecondary,
  onClick: () -> Unit,
  testTag: String = "profile_option_row",
) {
  val colors = WhatsChatThemeTokens.colors
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clickable(onClick = onClick)
      .padding(horizontal = 20.dp, vertical = 14.dp)
      .testTag(testTag),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Icon(
      imageVector = icon,
      contentDescription = null,
      tint = iconTint,
      modifier = Modifier.size(24.dp),
    )
    Spacer(modifier = Modifier.width(20.dp))
    Column(modifier = Modifier.weight(1f)) {
      Text(
        text = title,
        style = MaterialTheme.typography.bodyLarge,
        color = textColor,
      )
      if (!subtitle.isNullOrBlank()) {
        Text(
          text = subtitle,
          style = MaterialTheme.typography.bodySmall,
          color = colors.textSecondary,
        )
      }
    }
  }
}

@Composable
private fun OtherUserSwitchRow(
  icon: ImageVector,
  title: String,
  subtitle: String? = null,
  checked: Boolean,
  onCheckedChange: (Boolean) -> Unit,
) {
  val colors = WhatsChatThemeTokens.colors
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clickable { onCheckedChange(!checked) }
      .padding(horizontal = 20.dp, vertical = 12.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Icon(
      imageVector = icon,
      contentDescription = null,
      tint = colors.textSecondary,
      modifier = Modifier.size(24.dp),
    )
    Spacer(modifier = Modifier.width(20.dp))
    Column(modifier = Modifier.weight(1f)) {
      Text(
        text = title,
        style = MaterialTheme.typography.bodyLarge,
        color = colors.textPrimary,
      )
      if (!subtitle.isNullOrBlank()) {
        Text(
          text = subtitle,
          style = MaterialTheme.typography.bodySmall,
          color = colors.textSecondary,
        )
      }
    }
    Switch(
      checked = checked,
      onCheckedChange = onCheckedChange,
      colors = SwitchDefaults.colors(
        checkedThumbColor = colors.backgroundPrimary,
        checkedTrackColor = colors.primaryGreen,
        uncheckedThumbColor = colors.textSecondary,
        uncheckedTrackColor = colors.elevated,
      ),
    )
  }
}
