package com.example.features.groups

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.R
import com.example.domain.models.ChatMessage
import com.example.domain.models.Conversation
import com.example.domain.models.MessageTypes
import com.example.domain.models.UserProfile
import com.example.features.chats.FullScreenMediaPreviewDialog
import com.example.ui.components.WhatsChatAvatar
import com.example.ui.components.WhatsChatCheckCircle
import com.example.ui.components.WhatsChatConfirmDialog
import com.example.ui.theme.WhatsChatThemeTokens

private enum class GroupInfoTab(val label: String) {
  MEMBERS("Members"),
  MEDIA("Media"),
  SETTINGS("Settings"),
}

@Composable
fun GroupInfoScreen(
  conversation: Conversation,
  messages: List<ChatMessage>,
  members: List<UserProfile>,
  allUsers: List<UserProfile>,
  currentUserId: String,
  onBackClick: () -> Unit,
  onVoiceCallClick: () -> Unit,
  onVideoCallClick: () -> Unit,
  onToggleMute: () -> Unit,
  onOpenMediaSharing: () -> Unit,
  onUpdateGroupInfo: (name: String, description: String) -> Unit,
  onChangeGroupAvatarByAdmin: (avatarUri: String) -> Unit,
  onSaveMemberTag: (userId: String, tag: String) -> Unit,
  onAddMembers: (List<String>) -> Unit,
  onToggleAdmin: (String) -> Unit,
  onRemoveMember: (String) -> Unit,
  onOpenMemberProfile: (String) -> Unit,
  onUpdateChatSettings: (
    chatLock: Boolean?,
    restrictedChat: Boolean?,
    translateMessages: Boolean?,
    mediaVisibility: Boolean?,
    disappearingMessages: String?,
    isFavourite: Boolean?,
  ) -> Unit,
  onCreateSimilarGroup: (memberIds: List<String>) -> Unit,
  onClearChat: () -> Unit,
  onLeaveGroup: () -> Unit,
) {
  val colors = WhatsChatThemeTokens.colors
  val context = LocalContext.current
  val isCurrentUserAdmin = currentUserId in conversation.adminIds

  var activeTab by remember { mutableStateOf(GroupInfoTab.MEMBERS) }
  var showEditDialog by remember { mutableStateOf(false) }
  var showAddMemberDialog by remember { mutableStateOf(false) }
  var showLeaveConfirm by remember { mutableStateOf(false) }
  var showClearConfirm by remember { mutableStateOf(false) }
  var showReportConfirm by remember { mutableStateOf(false) }
  var showMemberSearch by remember { mutableStateOf(false) }
  var showOverflowMenu by remember { mutableStateOf(false) }
  var showEncryptionDialog by remember { mutableStateOf(false) }
  var showMediaVisibilityDialog by remember { mutableStateOf(false) }
  var selectedMemberMenu by remember { mutableStateOf<UserProfile?>(null) }
  var tagEditTargetUser by remember { mutableStateOf<UserProfile?>(null) }
  var tagDraft by remember { mutableStateOf("") }
  var previewMediaMessage by remember { mutableStateOf<ChatMessage?>(null) }

  var draftName by remember(conversation.title) { mutableStateOf(conversation.title) }
  var draftDesc by remember(conversation.description) { mutableStateOf(conversation.description) }
  var memberSearchQuery by remember { mutableStateOf("") }
  var addSelection by remember { mutableStateOf<List<String>>(emptyList()) }

  val adminPhotoPicker = rememberLauncherForActivityResult(
    ActivityResultContracts.PickVisualMedia(),
  ) { uri ->
    if (uri != null) {
      onChangeGroupAvatarByAdmin(uri.toString())
    }
  }

  val filteredMembers = remember(members, memberSearchQuery) {
    val q = memberSearchQuery.trim().lowercase()
    if (q.isEmpty()) members else members.filter {
      it.displayName.lowercase().contains(q) || it.username.lowercase().contains(q)
    }
  }

  val mediaMessages = remember(messages) {
    messages.filter {
      it.messageType == MessageTypes.IMAGE || it.messageType == MessageTypes.VIDEO
    }
  }

  val nonMembers = remember(allUsers, members) {
    val memberSet = members.map { it.id }.toSet()
    allUsers.filterNot { it.id in memberSet || it.isBlocked }
  }

  Scaffold(
    containerColor = colors.backgroundPrimary,
    topBar = {
      Surface(color = colors.backgroundSecondary) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 6.dp, vertical = 6.dp),
          verticalAlignment = Alignment.CenterVertically,
        ) {
          IconButton(
            onClick = onBackClick,
            modifier = Modifier.testTag("top_bar_back"),
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "Back",
              tint = colors.textPrimary,
            )
          }
          Spacer(modifier = Modifier.weight(1f))
          Box {
            IconButton(
              onClick = { showOverflowMenu = true },
              modifier = Modifier.testTag("group_info_more_button"),
            ) {
              Icon(
                imageVector = Icons.Filled.MoreVert,
                contentDescription = "Group options",
                tint = colors.textPrimary,
              )
            }
            DropdownMenu(
              expanded = showOverflowMenu,
              onDismissRequest = { showOverflowMenu = false },
              containerColor = colors.card,
            ) {
              DropdownMenuItem(
                text = { Text("Edit group name & description", color = colors.textPrimary) },
                leadingIcon = { Icon(Icons.Filled.Edit, null, tint = colors.mintAccent) },
                onClick = {
                  showOverflowMenu = false
                  showEditDialog = true
                },
              )
              if (isCurrentUserAdmin) {
                DropdownMenuItem(
                  text = { Text("Change group photo (Admin)", color = colors.textPrimary) },
                  leadingIcon = { Icon(Icons.Filled.AddPhotoAlternate, null, tint = colors.mintAccent) },
                  onClick = {
                    showOverflowMenu = false
                    adminPhotoPicker.launch(
                      PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly),
                    )
                  },
                )
              }
              DropdownMenuItem(
                text = {
                  Text(
                    if (conversation.isMuted) "Unmute notifications" else "Mute notifications",
                    color = colors.textPrimary,
                  )
                },
                leadingIcon = {
                  Icon(
                    if (conversation.isMuted) Icons.Filled.Notifications else Icons.Filled.NotificationsOff,
                    null,
                    tint = colors.mintAccent,
                  )
                },
                onClick = {
                  showOverflowMenu = false
                  onToggleMute()
                },
              )
            }
          }
        }
      }
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
          .widthIn(max = 660.dp)
          .fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp),
      ) {
        // Group Header matching uploaded reference
        item {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .background(colors.backgroundSecondary)
              .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
          ) {
            Box(
              modifier = Modifier
                .size(104.dp)
                .clickable {
                  if (isCurrentUserAdmin) {
                    adminPhotoPicker.launch(
                      PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly),
                    )
                  } else {
                    Toast
                      .makeText(context, "Only group admins can change the group picture", Toast.LENGTH_SHORT)
                      .show()
                  }
                }
                .testTag("group_avatar_picker"),
            ) {
              WhatsChatAvatar(
                displayName = conversation.title,
                size = 100.dp,
                avatarResName = conversation.avatarResName,
                avatarUri = conversation.avatarUri,
                isGroup = true,
                showOnlineIndicator = false,
              )
              if (isCurrentUserAdmin) {
                Box(
                  modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(colors.primaryGreen)
                    .border(2.dp, colors.backgroundSecondary, CircleShape),
                  contentAlignment = Alignment.Center,
                ) {
                  Icon(
                    imageVector = Icons.Filled.CameraAlt,
                    contentDescription = "Change group picture (Admin only)",
                    tint = colors.backgroundPrimary,
                    modifier = Modifier.size(17.dp),
                  )
                }
              }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text(
              text = conversation.title,
              style = MaterialTheme.typography.headlineSmall,
              color = colors.textPrimary,
              fontWeight = FontWeight.SemiBold,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "Group · ${members.size} members",
              style = MaterialTheme.typography.bodyMedium,
              color = colors.textSecondary,
            )
            if (conversation.description.isNotBlank()) {
              Spacer(modifier = Modifier.height(6.dp))
              Text(
                text = conversation.description,
                style = MaterialTheme.typography.bodySmall,
                color = colors.textSecondary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
              )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 3 Quick Pill Buttons: Voice, Video, Search (matching screenshot 1)
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
              GroupPillAction(
                icon = Icons.Filled.Call,
                label = "Voice",
                onClick = onVoiceCallClick,
                modifier = Modifier.weight(1f),
              )
              GroupPillAction(
                icon = Icons.Filled.Videocam,
                label = "Video",
                onClick = onVideoCallClick,
                modifier = Modifier.weight(1f),
              )
              GroupPillAction(
                icon = Icons.Filled.Search,
                label = "Search",
                onClick = {
                  activeTab = GroupInfoTab.MEMBERS
                  showMemberSearch = !showMemberSearch
                },
                modifier = Modifier.weight(1f),
              )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 3-tab selector: Members | Media | Settings
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
              GroupInfoTab.entries.forEach { tab ->
                val selected = tab == activeTab
                Column(
                  horizontalAlignment = Alignment.CenterHorizontally,
                  modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { activeTab = tab }
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .testTag("group_tab_${tab.name.lowercase()}"),
                ) {
                  Text(
                    text = tab.label,
                    style = MaterialTheme.typography.titleSmall,
                    color = if (selected) colors.textPrimary else colors.textSecondary,
                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                  )
                  Spacer(modifier = Modifier.height(6.dp))
                  Box(
                    modifier = Modifier
                      .width(56.dp)
                      .height(3.dp)
                      .clip(RoundedCornerShape(999.dp))
                      .background(if (selected) colors.primaryGreen else Color.Transparent),
                  )
                }
              }
            }
          }
          HorizontalDivider(color = colors.divider)
        }

        when (activeTab) {
          GroupInfoTab.MEMBERS -> {
            item {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
              ) {
                Text(
                  text = "${members.size} members",
                  style = MaterialTheme.typography.titleSmall,
                  color = colors.textSecondary,
                )
                IconButton(
                  onClick = { showMemberSearch = !showMemberSearch },
                  modifier = Modifier.size(32.dp),
                ) {
                  Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = "Search members",
                    tint = colors.textSecondary,
                  )
                }
              }

              if (showMemberSearch) {
                OutlinedTextField(
                  value = memberSearchQuery,
                  onValueChange = { memberSearchQuery = it },
                  placeholder = { Text("Search members...", color = colors.textDisabled) },
                  leadingIcon = { Icon(Icons.Filled.Search, null, tint = colors.textSecondary) },
                  singleLine = true,
                  shape = RoundedCornerShape(14.dp),
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 4.dp),
                )
              }

              // + Add members row matching screenshot 1
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .clickable { showAddMemberDialog = true }
                  .padding(horizontal = 20.dp, vertical = 10.dp)
                  .testTag("group_add_members_row"),
                verticalAlignment = Alignment.CenterVertically,
              ) {
                Box(
                  modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(colors.primaryGreen),
                  contentAlignment = Alignment.Center,
                ) {
                  Icon(
                    imageVector = Icons.Filled.PersonAdd,
                    contentDescription = "Add members",
                    tint = colors.backgroundPrimary,
                    modifier = Modifier.size(22.dp),
                  )
                }
                Spacer(modifier = Modifier.width(14.dp))
                Text(
                  text = "Add members",
                  style = MaterialTheme.typography.bodyLarge,
                  color = colors.textPrimary,
                  fontWeight = FontWeight.Medium,
                )
              }
            }

            items(filteredMembers, key = { it.id }) { member ->
              val isMe = member.id == currentUserId
              val isAdmin = member.id in conversation.adminIds
              val memberTag = conversation.memberTags[member.id]

              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .clickable {
                    if (isMe) {
                      tagEditTargetUser = member
                      tagDraft = memberTag.orEmpty()
                    } else {
                      selectedMemberMenu = member
                    }
                  }
                  .padding(horizontal = 20.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
              ) {
                WhatsChatAvatar(
                  displayName = member.displayName,
                  size = 44.dp,
                  avatarResName = member.avatarResName,
                  avatarUri = member.avatarUri,
                  isOnline = member.isOnline,
                )
                Spacer(modifier = Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                  Text(
                    text = if (isMe) "You" else member.displayName,
                    style = MaterialTheme.typography.bodyLarge,
                    color = colors.textPrimary,
                    fontWeight = FontWeight.Medium,
                  )
                  if (isMe) {
                    Text(
                      text = memberTag ?: "Add member tag",
                      style = MaterialTheme.typography.bodySmall,
                      color = colors.mintAccent,
                      modifier = Modifier.testTag("you_member_tag"),
                    )
                  } else if (!memberTag.isNullOrBlank()) {
                    Text(
                      text = memberTag,
                      style = MaterialTheme.typography.bodySmall,
                      color = colors.mintAccent,
                    )
                  } else {
                    Text(
                      text = "@${member.username}",
                      style = MaterialTheme.typography.bodySmall,
                      color = colors.textSecondary,
                    )
                  }
                }
                if (isAdmin) {
                  Surface(
                    color = colors.primaryGreen.copy(alpha = 0.14f),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, colors.primaryGreen.copy(alpha = 0.45f)),
                  ) {
                    Text(
                      text = "Group Admin",
                      style = MaterialTheme.typography.labelSmall,
                      color = colors.mintAccent,
                      fontWeight = FontWeight.SemiBold,
                      modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    )
                  }
                }
              }
            }
          }

          GroupInfoTab.MEDIA -> {
            item {
              Column(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(horizontal = 20.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
              ) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically,
                ) {
                  Text(
                    text = "Shared Media (${mediaMessages.size})",
                    style = MaterialTheme.typography.titleSmall,
                    color = colors.textPrimary,
                  )
                  TextButton(onClick = onOpenMediaSharing) {
                    Text("Open Gallery", color = colors.mintAccent)
                  }
                }

                if (mediaMessages.isEmpty()) {
                  val fallbackDrawables = listOf(
                    R.drawable.img_sample_landscape,
                    R.drawable.img_welcome_illustration,
                    R.drawable.img_avatar_sarah,
                  )
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                  ) {
                    fallbackDrawables.forEach { resId ->
                      Image(
                        painter = painterResource(id = resId),
                        contentDescription = "Sample group media",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                          .weight(1f)
                          .aspectRatio(1f)
                          .clip(RoundedCornerShape(12.dp))
                          .clickable(onClick = onOpenMediaSharing),
                      )
                    }
                  }
                } else {
                  mediaMessages.chunked(3).forEach { rowItems ->
                    Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                      rowItems.forEach { msg ->
                        Box(
                          modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(colors.card)
                            .clickable { previewMediaMessage = msg },
                        ) {
                          if (!msg.mediaUri.isNullOrBlank()) {
                            AsyncImage(
                              model = msg.mediaUri,
                              contentDescription = msg.content,
                              contentScale = ContentScale.Crop,
                              modifier = Modifier.fillMaxSize(),
                            )
                          } else {
                            Image(
                              painter = painterResource(id = R.drawable.img_sample_landscape),
                              contentDescription = msg.content,
                              contentScale = ContentScale.Crop,
                              modifier = Modifier.fillMaxSize(),
                            )
                          }
                          if (msg.messageType == MessageTypes.VIDEO) {
                            Icon(
                              imageVector = Icons.Filled.PlayArrow,
                              contentDescription = "Video",
                              tint = Color.White,
                              modifier = Modifier
                                .align(Alignment.Center)
                                .size(30.dp),
                            )
                          }
                        }
                      }
                      repeat(3 - rowItems.size) {
                        Spacer(modifier = Modifier.weight(1f))
                      }
                    }
                  }
                }
              }
            }
          }

          GroupInfoTab.SETTINGS -> {
            item {
              Column(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(vertical = 8.dp),
              ) {
                GroupSettingsActionRow(
                  icon = Icons.Filled.AddPhotoAlternate,
                  title = "Media visibility",
                  subtitle = if (conversation.mediaVisibility) "Show newly downloaded media" else "Off",
                  onClick = { showMediaVisibilityDialog = true },
                )
                GroupSettingsActionRow(
                  icon = Icons.Filled.Lock,
                  title = "Encryption",
                  subtitle = "Messages and calls are end-to-end encrypted. Tap to verify.",
                  onClick = { showEncryptionDialog = true },
                )
                GroupSettingsSwitchRow(
                  icon = Icons.Filled.Security,
                  title = "Chat lock",
                  subtitle = "Lock and hide this chat on this device.",
                  checked = conversation.chatLock,
                  onCheckedChange = { locked ->
                    onUpdateChatSettings(locked, null, null, null, null, null)
                  },
                )
                GroupSettingsSwitchRow(
                  icon = Icons.Filled.Shield,
                  title = "Restricted chat",
                  subtitle = if (conversation.restrictedChat) "On" else "Off",
                  checked = conversation.restrictedChat,
                  onCheckedChange = { restricted ->
                    onUpdateChatSettings(null, restricted, null, null, null, null)
                  },
                )
                GroupSettingsSwitchRow(
                  icon = Icons.Filled.Language,
                  title = "Translate messages",
                  subtitle = if (conversation.translateMessages) "Auto-translate incoming text" else null,
                  checked = conversation.translateMessages,
                  onCheckedChange = { translate ->
                    onUpdateChatSettings(null, null, translate, null, null, null)
                  },
                )

                HorizontalDivider(color = colors.divider, modifier = Modifier.padding(vertical = 8.dp))

                // Create a similar group (matching screenshot 3 & 5)
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                      val memberIds = conversation.participantIds.filter { it != currentUserId }
                      onCreateSimilarGroup(memberIds)
                    }
                    .padding(horizontal = 20.dp, vertical = 14.dp)
                    .testTag("create_similar_group_row"),
                  verticalAlignment = Alignment.CenterVertically,
                ) {
                  Box(
                    modifier = Modifier
                      .size(40.dp)
                      .clip(CircleShape)
                      .background(colors.primaryGreen),
                    contentAlignment = Alignment.Center,
                  ) {
                    Icon(
                      imageVector = Icons.Filled.GroupAdd,
                      contentDescription = "Create a similar group",
                      tint = colors.backgroundPrimary,
                      modifier = Modifier.size(20.dp),
                    )
                  }
                  Spacer(modifier = Modifier.width(16.dp))
                  Column(modifier = Modifier.weight(1f)) {
                    Text(
                      text = "Create a similar group",
                      style = MaterialTheme.typography.bodyLarge,
                      color = colors.textPrimary,
                      fontWeight = FontWeight.Medium,
                    )
                    Text(
                      text = "Start with the same members that you can add or remove.",
                      style = MaterialTheme.typography.bodySmall,
                      color = colors.textSecondary,
                    )
                  }
                }

                HorizontalDivider(color = colors.divider, modifier = Modifier.padding(vertical = 6.dp))

                GroupSettingsActionRow(
                  icon = if (conversation.isFavourite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                  title = if (conversation.isFavourite) "Remove from Favourites" else "Add to Favourites",
                  iconTint = if (conversation.isFavourite) colors.mintAccent else colors.textSecondary,
                  onClick = {
                    onUpdateChatSettings(null, null, null, null, null, !conversation.isFavourite)
                    Toast.makeText(
                      context,
                      if (!conversation.isFavourite) "Added to Favourites" else "Removed from Favourites",
                      Toast.LENGTH_SHORT,
                    ).show()
                  },
                )
                GroupSettingsActionRow(
                  icon = Icons.Filled.ListAlt,
                  title = "Add to list",
                  onClick = {
                    Toast.makeText(context, "Added to custom group list", Toast.LENGTH_SHORT).show()
                  },
                )
                GroupSettingsActionRow(
                  icon = Icons.Filled.Upload,
                  title = "Export chat",
                  onClick = {
                    Toast.makeText(context, "Chat history prepared for export", Toast.LENGTH_SHORT).show()
                  },
                )
                GroupSettingsActionRow(
                  icon = Icons.Filled.DeleteSweep,
                  title = "Clear chat",
                  textColor = colors.danger,
                  iconTint = colors.danger,
                  onClick = { showClearConfirm = true },
                )

                HorizontalDivider(color = colors.divider, modifier = Modifier.padding(vertical = 6.dp))

                GroupSettingsActionRow(
                  icon = Icons.AutoMirrored.Filled.ExitToApp,
                  title = "Leave group",
                  textColor = colors.danger,
                  iconTint = colors.danger,
                  onClick = { showLeaveConfirm = true },
                  testTag = "group_leave_button",
                )
                GroupSettingsActionRow(
                  icon = Icons.Filled.Report,
                  title = "Report group",
                  textColor = colors.danger,
                  iconTint = colors.danger,
                  onClick = { showReportConfirm = true },
                )

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                  text = conversation.createdByText,
                  style = MaterialTheme.typography.bodySmall,
                  color = colors.textSecondary,
                  modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp),
                )
              }
            }
          }
        }
      }
    }
  }

  previewMediaMessage?.let { mediaMsg ->
    FullScreenMediaPreviewDialog(
      message = mediaMsg,
      onDismiss = { previewMediaMessage = null },
    )
  }

  if (showEditDialog) {
    AlertDialog(
      onDismissRequest = { showEditDialog = false },
      containerColor = colors.card,
      shape = RoundedCornerShape(20.dp),
      title = { Text("Edit Group Info", color = colors.textPrimary) },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          OutlinedTextField(
            value = draftName,
            onValueChange = { draftName = it },
            label = { Text("Group Name") },
            singleLine = true,
          )
          OutlinedTextField(
            value = draftDesc,
            onValueChange = { draftDesc = it },
            label = { Text("Description") },
            maxLines = 3,
          )
        }
      },
      confirmButton = {
        TextButton(
          onClick = {
            onUpdateGroupInfo(draftName, draftDesc)
            showEditDialog = false
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

  tagEditTargetUser?.let { target ->
    AlertDialog(
      onDismissRequest = { tagEditTargetUser = null },
      containerColor = colors.card,
      shape = RoundedCornerShape(20.dp),
      title = { Text("Member Tag", color = colors.textPrimary) },
      text = {
        OutlinedTextField(
          value = tagDraft,
          onValueChange = { if (it.length <= 24) tagDraft = it },
          label = { Text("Tag for ${if (target.id == currentUserId) "You" else target.displayName}") },
          placeholder = { Text("e.g. Coordinator, Developer") },
          singleLine = true,
        )
      },
      confirmButton = {
        TextButton(
          onClick = {
            onSaveMemberTag(target.id, tagDraft)
            tagEditTargetUser = null
          },
        ) {
          Text("Save", color = colors.mintAccent)
        }
      },
      dismissButton = {
        TextButton(onClick = { tagEditTargetUser = null }) {
          Text("Cancel", color = colors.textSecondary)
        }
      },
    )
  }

  if (showAddMemberDialog) {
    AlertDialog(
      onDismissRequest = { showAddMemberDialog = false },
      containerColor = colors.card,
      shape = RoundedCornerShape(20.dp),
      title = { Text("Add Members", color = colors.textPrimary) },
      text = {
        if (nonMembers.isEmpty()) {
          Text("All available contacts are already in this group.", color = colors.textSecondary)
        } else {
          Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            nonMembers.forEach { user ->
              val selected = user.id in addSelection
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(12.dp))
                  .clickable {
                    addSelection = if (selected) addSelection - user.id else addSelection + user.id
                  }
                  .padding(vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
              ) {
                WhatsChatAvatar(
                  displayName = user.displayName,
                  size = 38.dp,
                  avatarResName = user.avatarResName,
                  showOnlineIndicator = false,
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                  Text(user.displayName, color = colors.textPrimary)
                  Text("@${user.username}", style = MaterialTheme.typography.bodySmall, color = colors.textSecondary)
                }
                WhatsChatCheckCircle(selected = selected)
              }
            }
          }
        }
      },
      confirmButton = {
        TextButton(
          onClick = {
            if (addSelection.isNotEmpty()) {
              onAddMembers(addSelection)
              addSelection = emptyList()
            }
            showAddMemberDialog = false
          },
        ) {
          Text("Add", color = colors.mintAccent)
        }
      },
      dismissButton = {
        TextButton(onClick = { showAddMemberDialog = false }) {
          Text("Cancel", color = colors.textSecondary)
        }
      },
    )
  }

  selectedMemberMenu?.let { member ->
    val isAdmin = member.id in conversation.adminIds
    AlertDialog(
      onDismissRequest = { selectedMemberMenu = null },
      containerColor = colors.card,
      shape = RoundedCornerShape(20.dp),
      title = { Text(member.displayName, color = colors.textPrimary) },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
          Text("@${member.username}", color = colors.textSecondary)
          Spacer(modifier = Modifier.height(6.dp))
          TextButton(
            onClick = {
              selectedMemberMenu = null
              onOpenMemberProfile(member.id)
            },
          ) {
            Text("View Account", color = colors.textPrimary)
          }
          TextButton(
            onClick = {
              tagEditTargetUser = member
              tagDraft = conversation.memberTags[member.id].orEmpty()
              selectedMemberMenu = null
            },
          ) {
            Text("Edit Member Tag", color = colors.mintAccent)
          }
          if (isCurrentUserAdmin) {
            TextButton(
              onClick = {
                onToggleAdmin(member.id)
                selectedMemberMenu = null
              },
            ) {
              Text(
                if (isAdmin) "Dismiss as Admin" else "Make Group Admin",
                color = colors.mintAccent,
              )
            }
            TextButton(
              onClick = {
                onRemoveMember(member.id)
                selectedMemberMenu = null
              },
            ) {
              Text("Remove from Group", color = colors.danger)
            }
          }
        }
      },
      confirmButton = {},
      dismissButton = {
        TextButton(onClick = { selectedMemberMenu = null }) {
          Text("Close", color = colors.textSecondary)
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
          "Show newly received media from this group in your device's gallery?",
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
          Text("Yes", color = colors.mintAccent)
        }
      },
      dismissButton = {
        TextButton(
          onClick = {
            onUpdateChatSettings(null, null, null, false, null, null)
            showMediaVisibilityDialog = false
          },
        ) {
          Text("No", color = colors.textSecondary)
        }
      },
    )
  }

  if (showEncryptionDialog) {
    AlertDialog(
      onDismissRequest = { showEncryptionDialog = false },
      containerColor = colors.card,
      shape = RoundedCornerShape(20.dp),
      title = { Text("End-to-end Encryption", color = colors.textPrimary) },
      text = {
        Text(
          "Messages, voice notes, and calls in ${conversation.title} are secured across both Internet and direct Bluetooth transport modes.",
          color = colors.textSecondary,
        )
      },
      confirmButton = {
        TextButton(onClick = { showEncryptionDialog = false }) {
          Text("OK", color = colors.mintAccent)
        }
      },
    )
  }

  if (showClearConfirm) {
    WhatsChatConfirmDialog(
      title = "Clear this chat?",
      message = "All local messages in ${conversation.title} will be cleared.",
      confirmLabel = "Clear chat",
      isDestructive = true,
      onConfirm = {
        showClearConfirm = false
        onClearChat()
      },
      onDismiss = { showClearConfirm = false },
    )
  }

  if (showLeaveConfirm) {
    WhatsChatConfirmDialog(
      title = "Leave Group?",
      message = "You will no longer receive messages from ${conversation.title}.",
      confirmLabel = "Leave group",
      isDestructive = true,
      onConfirm = {
        showLeaveConfirm = false
        onLeaveGroup()
      },
      onDismiss = { showLeaveConfirm = false },
    )
  }

  if (showReportConfirm) {
    WhatsChatConfirmDialog(
      title = "Report Group?",
      message = "Report ${conversation.title} for spam or abuse?",
      confirmLabel = "Report",
      isDestructive = true,
      onConfirm = {
        showReportConfirm = false
        Toast.makeText(context, "Group report recorded locally", Toast.LENGTH_SHORT).show()
      },
      onDismiss = { showReportConfirm = false },
    )
  }
}

@Composable
private fun GroupPillAction(
  icon: ImageVector,
  label: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val colors = WhatsChatThemeTokens.colors
  Column(
    modifier = modifier,
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    Surface(
      color = colors.card,
      shape = RoundedCornerShape(999.dp),
      border = BorderStroke(1.dp, colors.divider),
      modifier = Modifier
        .fillMaxWidth()
        .height(48.dp)
        .clip(RoundedCornerShape(999.dp))
        .clickable(onClick = onClick),
    ) {
      Box(contentAlignment = Alignment.Center) {
        Icon(
          imageVector = icon,
          contentDescription = label,
          tint = colors.textPrimary,
          modifier = Modifier.size(22.dp),
        )
      }
    }
    Spacer(modifier = Modifier.height(6.dp))
    Text(
      text = label,
      style = MaterialTheme.typography.bodySmall,
      color = colors.textSecondary,
    )
  }
}

@Composable
private fun GroupSettingsActionRow(
  icon: ImageVector,
  title: String,
  subtitle: String? = null,
  textColor: Color = WhatsChatThemeTokens.colors.textPrimary,
  iconTint: Color = WhatsChatThemeTokens.colors.textSecondary,
  onClick: () -> Unit,
  testTag: String = "group_settings_row",
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
private fun GroupSettingsSwitchRow(
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
