package com.example.features.chats

import android.Manifest
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EmojiEmotions
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Reply
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.core.permissions.PermissionHelper
import com.example.core.permissions.WhatsChatFeaturePermission
import com.example.domain.models.ChatMessage
import com.example.domain.models.ConnectionStatus
import com.example.domain.models.Conversation
import com.example.domain.models.DeliveryStates
import com.example.domain.models.MessageTransport
import com.example.domain.models.MessageTypes
import com.example.ui.components.ConnectionStatusPill
import com.example.ui.components.WhatsChatAvatar
import com.example.ui.components.WhatsChatConfirmDialog
import com.example.ui.components.WhatsChatEmptyState
import com.example.ui.theme.WhatsChatThemeTokens
import kotlinx.coroutines.delay

@Composable
fun IndividualChatScreen(
  conversation: Conversation,
  messages: List<ChatMessage>,
  currentUserId: String,
  connectionStatus: ConnectionStatus,
  isInternetOnline: Boolean,
  onBackClick: () -> Unit,
  onOpenHeaderProfileOrGroup: () -> Unit,
  onOpenVoiceCall: () -> Unit,
  onOpenVideoCall: () -> Unit,
  onOpenMediaSharing: () -> Unit,
  onOpenConnectionPreferences: () -> Unit,
  onToggleSimulatedInternet: (Boolean) -> Unit,
  onSendMessage: (text: String, replyTo: ChatMessage?) -> Unit,
  onSendPickedMedia: (mediaUri: String, isVideo: Boolean, caption: String) -> Unit,
  onSendVoiceNote: (durationSeconds: Int) -> Unit,
  onSendSticker: (emoji: String, label: String) -> Unit,
  onSendGif: (gifLabel: String, badgeEmoji: String) -> Unit,
  onEditMessage: (messageId: String, newText: String) -> Unit,
  onDeleteMessage: (messageId: String) -> Unit,
  onReactToMessage: (messageId: String, emoji: String) -> Unit,
  onMarkRead: () -> Unit,
) {
  val colors = WhatsChatThemeTokens.colors
  val context = LocalContext.current
  val clipboard = LocalClipboardManager.current
  val listState = rememberLazyListState()

  var messageInput by remember { mutableStateOf("") }
  var showEmojiDrawer by remember { mutableStateOf(false) }
  var recentEmojis by remember { mutableStateOf(listOf("😐", "😀", "👍", "❤️", "🔥", "🌿")) }
  var isRecordingVoice by remember { mutableStateOf(false) }
  var recordingSeconds by remember { mutableIntStateOf(0) }
  var pendingPickedMedia by remember { mutableStateOf<PendingPickedMedia?>(null) }
  var previewMessage by remember { mutableStateOf<ChatMessage?>(null) }

  var replyTarget by remember { mutableStateOf<ChatMessage?>(null) }
  var selectedMessageForMenu by remember { mutableStateOf<ChatMessage?>(null) }
  var editingMessage by remember { mutableStateOf<ChatMessage?>(null) }
  var editDraft by remember { mutableStateOf("") }
  var showMoreMenu by remember { mutableStateOf(false) }
  var showPermissionRationale by remember { mutableStateOf<WhatsChatFeaturePermission?>(null) }

  // Zero-permission Photo/Video Picker triggered by Camera icon
  val galleryPickerLauncher = rememberLauncherForActivityResult(
    ActivityResultContracts.PickVisualMedia(),
  ) { uri ->
    if (uri != null) {
      val mimeType = context.contentResolver.getType(uri).orEmpty()
      val isVideo = mimeType.startsWith("video/")
      pendingPickedMedia = PendingPickedMedia(uri = uri.toString(), isVideo = isVideo)
    }
  }

  val micPermissionLauncher = rememberLauncherForActivityResult(
    ActivityResultContracts.RequestPermission(),
  ) { granted ->
    if (granted) {
      recordingSeconds = 0
      isRecordingVoice = true
    } else {
      Toast.makeText(context, "Microphone permission denied", Toast.LENGTH_SHORT).show()
    }
  }

  LaunchedEffect(isRecordingVoice) {
    if (isRecordingVoice) {
      recordingSeconds = 0
      while (isRecordingVoice) {
        delay(1000L)
        recordingSeconds++
      }
    }
  }

  LaunchedEffect(conversation.id) {
    onMarkRead()
  }

  LaunchedEffect(messages.size) {
    if (messages.isNotEmpty()) {
      listState.animateScrollToItem(messages.lastIndex)
    }
  }

  Scaffold(
    containerColor = colors.backgroundPrimary,
    topBar = {
      Surface(color = colors.backgroundSecondary) {
        Column(modifier = Modifier.statusBarsPadding()) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 6.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
          ) {
            IconButton(
              onClick = onBackClick,
              modifier = Modifier.testTag("chat_back_button"),
            ) {
              Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = colors.textPrimary,
              )
            }

            Row(
              modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(12.dp))
                .clickable(onClick = onOpenHeaderProfileOrGroup)
                .padding(horizontal = 4.dp, vertical = 4.dp),
              verticalAlignment = Alignment.CenterVertically,
            ) {
              WhatsChatAvatar(
                displayName = conversation.title,
                size = 40.dp,
                avatarResName = conversation.avatarResName,
                avatarUri = conversation.avatarUri,
                isGroup = conversation.isGroup,
                isOnline = conversation.isOnline,
              )
              Spacer(modifier = Modifier.width(10.dp))
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = conversation.title,
                  style = MaterialTheme.typography.titleSmall,
                  color = colors.textPrimary,
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis,
                )
                Text(
                  text = if (conversation.isGroup) {
                    "${conversation.participantIds.size} members · @${conversation.username}"
                  } else if (conversation.isOnline) {
                    "@${conversation.username} · online"
                  } else {
                    "@${conversation.username} · offline"
                  },
                  style = MaterialTheme.typography.labelSmall,
                  color = if (conversation.isOnline) colors.mintAccent else colors.textSecondary,
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis,
                )
              }
            }

            IconButton(
              onClick = onOpenVideoCall,
              modifier = Modifier.testTag("chat_video_call_button"),
            ) {
              Icon(
                imageVector = Icons.Filled.Videocam,
                contentDescription = "Video Call",
                tint = colors.mintAccent,
              )
            }

            IconButton(
              onClick = onOpenVoiceCall,
              modifier = Modifier.testTag("chat_voice_call_button"),
            ) {
              Icon(
                imageVector = Icons.Filled.Call,
                contentDescription = "Voice Call",
                tint = colors.mintAccent,
              )
            }

            Box {
              IconButton(
                onClick = { showMoreMenu = true },
                modifier = Modifier.testTag("chat_more_menu"),
              ) {
                Icon(
                  imageVector = Icons.Filled.MoreVert,
                  contentDescription = "Chat Options",
                  tint = colors.textPrimary,
                )
              }
              DropdownMenu(
                expanded = showMoreMenu,
                onDismissRequest = { showMoreMenu = false },
                containerColor = colors.card,
              ) {
                DropdownMenuItem(
                  text = {
                    Text(
                      if (conversation.isGroup) "Group Info" else "View Profile",
                      color = colors.textPrimary,
                    )
                  },
                  onClick = {
                    showMoreMenu = false
                    onOpenHeaderProfileOrGroup()
                  },
                )
                DropdownMenuItem(
                  text = { Text("Connection Preferences", color = colors.textPrimary) },
                  onClick = {
                    showMoreMenu = false
                    onOpenConnectionPreferences()
                  },
                )
                DropdownMenuItem(
                  text = {
                    Text(
                      if (isInternetOnline) "Simulate Internet Offline" else "Simulate Internet Online",
                      color = colors.mintAccent,
                    )
                  },
                  onClick = {
                    showMoreMenu = false
                    onToggleSimulatedInternet(!isInternetOnline)
                  },
                )
              }
            }
          }

          Row(
            modifier = Modifier
              .fillMaxWidth()
              .background(colors.backgroundPrimary.copy(alpha = 0.65f))
              .padding(horizontal = 16.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
          ) {
            ConnectionStatusPill(
              status = connectionStatus,
              onClick = onOpenConnectionPreferences,
            )
            Text(
              text = if (conversation.disappearingMessages != "Off") {
                "Disappearing: ${conversation.disappearingMessages}"
              } else {
                "Unified Internet & Bluetooth"
              },
              style = MaterialTheme.typography.labelSmall,
              color = colors.textSecondary,
            )
          }
        }
      }
    },
    bottomBar = {
      Surface(
        color = colors.backgroundSecondary,
        border = BorderStroke(0.5.dp, colors.divider),
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .imePadding(),
        ) {
          AnimatedVisibility(visible = replyTarget != null) {
            replyTarget?.let { target ->
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .background(colors.card)
                  .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
              ) {
                Box(
                  modifier = Modifier
                    .width(3.dp)
                    .height(30.dp)
                    .background(colors.primaryGreen, RoundedCornerShape(2.dp)),
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                  Text(
                    text = "Replying to ${target.senderName}",
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.mintAccent,
                    fontWeight = FontWeight.SemiBold,
                  )
                  Text(
                    text = target.content,
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                  )
                }
                IconButton(onClick = { replyTarget = null }) {
                  Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "Cancel reply",
                    tint = colors.textSecondary,
                    modifier = Modifier.size(18.dp),
                  )
                }
              }
            }
          }

          if (isRecordingVoice) {
            VoiceRecordingComposerBar(
              elapsedSeconds = recordingSeconds,
              onCancel = {
                isRecordingVoice = false
                recordingSeconds = 0
              },
              onSend = {
                val finalDuration = recordingSeconds.coerceAtLeast(1)
                isRecordingVoice = false
                recordingSeconds = 0
                onSendVoiceNote(finalDuration)
              },
            )
          } else {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 8.dp),
              verticalAlignment = Alignment.CenterVertically,
            ) {
              Surface(
                color = colors.card,
                shape = RoundedCornerShape(26.dp),
                border = BorderStroke(1.dp, colors.divider),
                modifier = Modifier.weight(1f),
              ) {
                Row(
                  modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                  verticalAlignment = Alignment.CenterVertically,
                ) {
                  IconButton(
                    onClick = { showEmojiDrawer = !showEmojiDrawer },
                    modifier = Modifier.testTag("chat_emoji_button"),
                  ) {
                    Icon(
                      imageVector = if (showEmojiDrawer) Icons.Filled.Keyboard else Icons.Filled.EmojiEmotions,
                      contentDescription = "Emoji, Stickers & GIFs",
                      tint = if (showEmojiDrawer) colors.mintAccent else colors.textSecondary,
                    )
                  }

                  Box(
                    modifier = Modifier
                      .weight(1f)
                      .padding(vertical = 10.dp),
                  ) {
                    if (messageInput.isEmpty()) {
                      Text(
                        text = "Message",
                        style = MaterialTheme.typography.bodyLarge,
                        color = colors.textDisabled,
                      )
                    }
                    BasicTextField(
                      value = messageInput,
                      onValueChange = { messageInput = it },
                      textStyle = MaterialTheme.typography.bodyLarge.copy(color = colors.textPrimary),
                      cursorBrush = SolidColor(colors.mintAccent),
                      maxLines = 4,
                      modifier = Modifier
                        .fillMaxWidth()
                        .testTag("chat_message_input"),
                    )
                  }

                  IconButton(
                    onClick = onOpenMediaSharing,
                    modifier = Modifier.testTag("chat_attach_button"),
                  ) {
                    Icon(
                      imageVector = Icons.Filled.AttachFile,
                      contentDescription = "Attach media",
                      tint = colors.textSecondary,
                    )
                  }

                  IconButton(
                    onClick = {
                      galleryPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo),
                      )
                    },
                    modifier = Modifier.testTag("chat_camera_button"),
                  ) {
                    Icon(
                      imageVector = Icons.Filled.CameraAlt,
                      contentDescription = "Select photo or video from gallery",
                      tint = colors.textSecondary,
                    )
                  }
                }
              }

              Spacer(modifier = Modifier.width(8.dp))

              val hasText = messageInput.isNotBlank()
              Surface(
                color = colors.primaryGreen,
                shape = CircleShape,
                modifier = Modifier
                  .size(48.dp)
                  .clip(CircleShape)
                  .clickable {
                    if (hasText) {
                      onSendMessage(messageInput, replyTarget)
                      messageInput = ""
                      replyTarget = null
                    } else {
                      val perm = PermissionHelper.microphonePermission()
                      if (PermissionHelper.hasAllPermissions(context, listOf(perm))) {
                        isRecordingVoice = true
                      } else {
                        showPermissionRationale = WhatsChatFeaturePermission.MICROPHONE_AUDIO
                      }
                    }
                  }
                  .testTag(if (hasText) "chat_send_button" else "chat_mic_button"),
              ) {
                Box(contentAlignment = Alignment.Center) {
                  Icon(
                    imageVector = if (hasText) Icons.AutoMirrored.Filled.Send else Icons.Filled.Mic,
                    contentDescription = if (hasText) "Send message" else "Record voice message",
                    tint = colors.backgroundPrimary,
                    modifier = Modifier.size(22.dp),
                  )
                }
              }
            }
          }

          AnimatedVisibility(visible = showEmojiDrawer && !isRecordingVoice) {
            EmojiGifStickerDrawer(
              recentEmojis = recentEmojis,
              onEmojiSelected = { emoji ->
                messageInput += emoji
                recentEmojis = (listOf(emoji) + recentEmojis.filterNot { it == emoji }).take(8)
              },
              onBackspace = {
                if (messageInput.isNotEmpty()) {
                  val lastCount = Character.charCount(messageInput.codePointBefore(messageInput.length))
                  messageInput = messageInput.dropLast(lastCount)
                }
              },
              onGifSelected = { gif ->
                onSendGif(gif.label, gif.emoji)
                showEmojiDrawer = false
              },
              onStickerSelected = { sticker ->
                onSendSticker(sticker.emoji, sticker.label)
                showEmojiDrawer = false
              },
            )
          }
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
      ChatWallpaperPattern()

      if (messages.isEmpty()) {
        WhatsChatEmptyState(
          icon = Icons.Filled.EmojiEmotions,
          title = "Start the Conversation",
          description = "Messages sent via Internet or nearby Bluetooth appear seamlessly in this single timeline.",
        )
      } else {
        LazyColumn(
          state = listState,
          modifier = Modifier
            .widthIn(max = 720.dp)
            .fillMaxSize(),
          contentPadding = PaddingValues(horizontal = 12.dp, vertical = 12.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
          items(messages, key = { it.messageId }) { message ->
            val isOutgoing = message.senderId == currentUserId
            ChatMessageBubble(
              message = message,
              isOutgoing = isOutgoing,
              isGroup = conversation.isGroup,
              onMediaClick = { previewMessage = message },
              onLongPress = { selectedMessageForMenu = message },
            )
          }
        }
      }
    }
  }

  // Picked photo/video preview + caption dialog
  pendingPickedMedia?.let { picked ->
    PickedMediaCaptionDialog(
      media = picked,
      onDismiss = { pendingPickedMedia = null },
      onSend = { caption ->
        onSendPickedMedia(picked.uri, picked.isVideo, caption)
        pendingPickedMedia = null
      },
    )
  }

  // Full-screen Image or Video preview viewer
  previewMessage?.let { mediaMsg ->
    FullScreenMediaPreviewDialog(
      message = mediaMsg,
      onDismiss = { previewMessage = null },
    )
  }

  // Message context menu dialog
  selectedMessageForMenu?.let { msg ->
    AlertDialog(
      onDismissRequest = { selectedMessageForMenu = null },
      containerColor = colors.card,
      shape = RoundedCornerShape(20.dp),
      title = {
        Text(
          text = "Message Options",
          style = MaterialTheme.typography.titleMedium,
          color = colors.textPrimary,
        )
      },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
          ) {
            listOf("👍", "❤️", "🔥", "😂", "🌿").forEach { emoji ->
              Text(
                text = emoji,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                  .clip(CircleShape)
                  .background(colors.elevated)
                  .clickable {
                    onReactToMessage(msg.messageId, emoji)
                    selectedMessageForMenu = null
                  }
                  .padding(8.dp),
              )
            }
          }
          Spacer(modifier = Modifier.height(6.dp))
          MessageOptionRow(Icons.Filled.Reply, "Reply") {
            replyTarget = msg
            selectedMessageForMenu = null
          }
          MessageOptionRow(Icons.Filled.ContentCopy, "Copy Text") {
            clipboard.setText(AnnotatedString(msg.content))
            selectedMessageForMenu = null
            Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
          }
          if (msg.senderId == currentUserId && msg.messageType == MessageTypes.TEXT) {
            MessageOptionRow(Icons.Filled.Edit, "Edit Message") {
              editingMessage = msg
              editDraft = msg.content
              selectedMessageForMenu = null
            }
          }
          MessageOptionRow(Icons.Filled.Delete, "Delete Message", isDanger = true) {
            onDeleteMessage(msg.messageId)
            selectedMessageForMenu = null
          }
        }
      },
      confirmButton = {},
      dismissButton = {
        TextButton(onClick = { selectedMessageForMenu = null }) {
          Text("Close", color = colors.textSecondary)
        }
      },
    )
  }

  editingMessage?.let { target ->
    AlertDialog(
      onDismissRequest = { editingMessage = null },
      containerColor = colors.card,
      shape = RoundedCornerShape(20.dp),
      title = { Text("Edit Message", color = colors.textPrimary) },
      text = {
        OutlinedTextField(
          value = editDraft,
          onValueChange = { editDraft = it },
          singleLine = false,
          maxLines = 4,
          modifier = Modifier.fillMaxWidth(),
        )
      },
      confirmButton = {
        TextButton(
          onClick = {
            onEditMessage(target.messageId, editDraft)
            editingMessage = null
          },
        ) {
          Text("Save", color = colors.mintAccent)
        }
      },
      dismissButton = {
        TextButton(onClick = { editingMessage = null }) {
          Text("Cancel", color = colors.textSecondary)
        }
      },
    )
  }

  showPermissionRationale?.let { perm ->
    WhatsChatConfirmDialog(
      title = perm.title,
      message = perm.rationale,
      confirmLabel = "Grant Permission",
      onConfirm = {
        showPermissionRationale = null
        if (perm == WhatsChatFeaturePermission.MICROPHONE_AUDIO) {
          micPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
      },
      onDismiss = { showPermissionRationale = null },
    )
  }
}

@Composable
private fun MessageOptionRow(
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  label: String,
  isDanger: Boolean = false,
  onClick: () -> Unit,
) {
  val colors = WhatsChatThemeTokens.colors
  val tint = if (isDanger) colors.danger else colors.textPrimary
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(12.dp))
      .clickable(onClick = onClick)
      .padding(horizontal = 8.dp, vertical = 10.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = Modifier.size(20.dp))
    Spacer(modifier = Modifier.width(12.dp))
    Text(text = label, style = MaterialTheme.typography.bodyLarge, color = tint)
  }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ChatMessageBubble(
  message: ChatMessage,
  isOutgoing: Boolean,
  isGroup: Boolean,
  onMediaClick: () -> Unit,
  onLongPress: () -> Unit,
) {
  val colors = WhatsChatThemeTokens.colors
  val bubbleColor = if (isOutgoing) colors.outgoingBubble else colors.incomingBubble
  val bubbleShape = RoundedCornerShape(
    topStart = 16.dp,
    topEnd = 16.dp,
    bottomStart = if (isOutgoing) 16.dp else 4.dp,
    bottomEnd = if (isOutgoing) 4.dp else 16.dp,
  )
  var isExpanded by remember(message.messageId) { mutableStateOf(false) }

  Column(
    modifier = Modifier.fillMaxWidth(),
    horizontalAlignment = if (isOutgoing) Alignment.End else Alignment.Start,
  ) {
    if (message.messageType == MessageTypes.STICKER) {
      Column(
        horizontalAlignment = if (isOutgoing) Alignment.End else Alignment.Start,
        modifier = Modifier
          .clip(RoundedCornerShape(16.dp))
          .combinedClickable(onClick = {}, onLongClick = onLongPress)
          .padding(horizontal = 6.dp, vertical = 2.dp)
          .testTag("message_bubble_${message.messageId}"),
      ) {
        Text(
          text = message.stickerEmoji ?: "😎",
          fontSize = 62.sp,
        )
        Surface(
          color = colors.card.copy(alpha = 0.85f),
          shape = RoundedCornerShape(999.dp),
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
          ) {
            Text(
              text = message.formattedTime,
              style = MaterialTheme.typography.labelSmall,
              color = colors.textSecondary,
            )
            if (isOutgoing) {
              Icon(
                imageVector = Icons.Filled.DoneAll,
                contentDescription = message.deliveryStatus,
                tint = colors.mintAccent,
                modifier = Modifier.size(13.dp),
              )
            }
          }
        }
      }
      return@Column
    }

    Surface(
      color = bubbleColor,
      shape = bubbleShape,
      modifier = Modifier
        .widthIn(max = 310.dp)
        .clip(bubbleShape)
        .combinedClickable(
          onClick = {
            if (message.messageType == MessageTypes.IMAGE || message.messageType == MessageTypes.VIDEO) {
              onMediaClick()
            }
          },
          onLongClick = onLongPress,
        )
        .testTag("message_bubble_${message.messageId}"),
    ) {
      Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp)) {
        if (isGroup && !isOutgoing) {
          Text(
            text = message.senderName,
            style = MaterialTheme.typography.labelSmall,
            color = colors.mintAccent,
            fontWeight = FontWeight.SemiBold,
          )
          Spacer(modifier = Modifier.height(3.dp))
        }

        if (!message.replyPreview.isNullOrBlank()) {
          Surface(
            color = colors.backgroundPrimary.copy(alpha = 0.4f),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
              .fillMaxWidth()
              .padding(bottom = 6.dp),
          ) {
            Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .width(3.dp)
                  .height(20.dp)
                  .background(colors.mintAccent, RoundedCornerShape(2.dp)),
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = message.replyPreview,
                style = MaterialTheme.typography.labelSmall,
                color = colors.textSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
              )
            }
          }
        }

        when (message.messageType) {
          MessageTypes.IMAGE -> {
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .height(170.dp)
                .clip(RoundedCornerShape(12.dp))
                .clickable(onClick = onMediaClick),
            ) {
              if (!message.mediaUri.isNullOrBlank()) {
                AsyncImage(
                  model = message.mediaUri,
                  contentDescription = message.content.ifBlank { "Shared image" },
                  contentScale = ContentScale.Crop,
                  modifier = Modifier.fillMaxSize(),
                )
              } else {
                val resId = when (message.mediaResName) {
                  "img_welcome_illustration" -> R.drawable.img_welcome_illustration
                  "img_avatar_sarah" -> R.drawable.img_avatar_sarah
                  else -> R.drawable.img_sample_landscape
                }
                Image(
                  painter = painterResource(id = resId),
                  contentDescription = message.attachmentName ?: "Shared image",
                  contentScale = ContentScale.Crop,
                  modifier = Modifier.fillMaxSize(),
                )
              }
            }
            if (message.content.isNotBlank()) {
              Spacer(modifier = Modifier.height(6.dp))
              Text(
                text = message.content,
                style = MaterialTheme.typography.bodyMedium,
                color = colors.textPrimary,
              )
            }
          }

          MessageTypes.VIDEO -> {
            val durSec = if (message.durationSeconds > 0) message.durationSeconds else 18
            val durLabel = "%d:%02d".format(durSec / 60, durSec % 60)
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .height(165.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(colors.backgroundPrimary)
                .clickable(onClick = onMediaClick),
              contentAlignment = Alignment.Center,
            ) {
              if (!message.mediaUri.isNullOrBlank()) {
                AsyncImage(
                  model = message.mediaUri,
                  contentDescription = "Video thumbnail",
                  contentScale = ContentScale.Crop,
                  modifier = Modifier.fillMaxSize(),
                )
              } else {
                Image(
                  painter = painterResource(id = R.drawable.img_sample_landscape),
                  contentDescription = "Video thumbnail",
                  contentScale = ContentScale.Crop,
                  modifier = Modifier.fillMaxSize(),
                )
              }
              Box(
                modifier = Modifier
                  .size(48.dp)
                  .clip(CircleShape)
                  .background(Color.Black.copy(alpha = 0.65f)),
                contentAlignment = Alignment.Center,
              ) {
                Icon(
                  imageVector = Icons.Filled.PlayArrow,
                  contentDescription = "Play video preview",
                  tint = Color.White,
                  modifier = Modifier.size(28.dp),
                )
              }
              Surface(
                color = Color.Black.copy(alpha = 0.6f),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier
                  .align(Alignment.BottomStart)
                  .padding(8.dp),
              ) {
                Row(
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                  verticalAlignment = Alignment.CenterVertically,
                ) {
                  Icon(
                    imageVector = Icons.Filled.Videocam,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(13.dp),
                  )
                  Spacer(modifier = Modifier.width(4.dp))
                  Text(
                    text = durLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White,
                  )
                }
              }
            }
            if (message.content.isNotBlank()) {
              Spacer(modifier = Modifier.height(6.dp))
              Text(
                text = message.content,
                style = MaterialTheme.typography.bodyMedium,
                color = colors.textPrimary,
              )
            }
          }

          MessageTypes.GIF -> {
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(
                  Brush.linearGradient(
                    listOf(Color(0xFF0A6C58), Color(0xFF183A3E)),
                  ),
                )
                .padding(12.dp),
            ) {
              Surface(
                color = Color.Black.copy(alpha = 0.55f),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.align(Alignment.TopStart),
              ) {
                Text(
                  text = "GIF",
                  style = MaterialTheme.typography.labelSmall,
                  color = Color.White,
                  fontWeight = FontWeight.Bold,
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                )
              }
              Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally,
              ) {
                Text(text = message.stickerEmoji ?: "👋", fontSize = 36.sp)
                Text(
                  text = message.gifLabel ?: message.content,
                  style = MaterialTheme.typography.titleSmall,
                  color = Color.White,
                  fontWeight = FontWeight.Bold,
                )
              }
            }
          }

          MessageTypes.FILE -> {
            Surface(
              color = colors.backgroundPrimary.copy(alpha = 0.35f),
              shape = RoundedCornerShape(12.dp),
              modifier = Modifier.fillMaxWidth(),
            ) {
              Row(
                modifier = Modifier.padding(10.dp),
                verticalAlignment = Alignment.CenterVertically,
              ) {
                Icon(
                  imageVector = Icons.Filled.Description,
                  contentDescription = "File attachment",
                  tint = colors.mintAccent,
                  modifier = Modifier.size(28.dp),
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                  Text(
                    text = message.attachmentName ?: "Document",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                  )
                  Text(
                    text = message.attachmentSize ?: "Local file",
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.textSecondary,
                  )
                }
              }
            }
            if (message.content.isNotBlank() && message.content != message.attachmentName) {
              Spacer(modifier = Modifier.height(6.dp))
              Text(
                text = message.content,
                style = MaterialTheme.typography.bodyMedium,
                color = colors.textPrimary,
              )
            }
          }

          MessageTypes.AUDIO -> {
            VoiceNotePlayerContent(message = message, isOutgoing = isOutgoing)
          }

          else -> {
            val isLong = message.content.length > 220
            val visibleText = if (isLong && !isExpanded) {
              message.content.take(220).trimEnd() + "..."
            } else {
              message.content
            }
            Text(
              text = visibleText,
              style = MaterialTheme.typography.bodyLarge,
              color = colors.textPrimary,
            )
            if (isLong) {
              Text(
                text = if (isExpanded) "Show less" else "Read more",
                style = MaterialTheme.typography.labelMedium,
                color = colors.mintAccent,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                  .clickable { isExpanded = !isExpanded }
                  .padding(top = 4.dp),
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Row(
          modifier = Modifier.align(Alignment.End),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
          if (message.isEdited) {
            Text(
              text = "edited",
              style = MaterialTheme.typography.labelSmall,
              color = colors.textSecondary,
            )
          }
          Icon(
            imageVector = if (message.transport == MessageTransport.BLUETOOTH) {
              Icons.Filled.Bluetooth
            } else {
              Icons.Filled.Wifi
            },
            contentDescription = message.transport,
            tint = if (message.transport == MessageTransport.BLUETOOTH) {
              colors.bluetoothAccent
            } else {
              colors.textSecondary.copy(alpha = 0.8f)
            },
            modifier = Modifier.size(12.dp),
          )
          Text(
            text = message.formattedTime,
            style = MaterialTheme.typography.labelSmall,
            color = colors.textSecondary,
          )
          if (isOutgoing) {
            val statusIcon = when (message.deliveryStatus) {
              DeliveryStates.PENDING -> Icons.Filled.Schedule
              DeliveryStates.DELIVERED -> Icons.Filled.DoneAll
              else -> Icons.Filled.Done
            }
            val statusTint = when (message.deliveryStatus) {
              DeliveryStates.PENDING -> colors.warning
              DeliveryStates.DELIVERED -> colors.mintAccent
              DeliveryStates.FAILED -> colors.danger
              else -> colors.textSecondary
            }
            Icon(
              imageVector = statusIcon,
              contentDescription = message.deliveryStatus,
              tint = statusTint,
              modifier = Modifier.size(14.dp),
            )
          }
        }
      }
    }

    if (message.reactions.isNotEmpty()) {
      Surface(
        color = colors.card,
        shape = RoundedCornerShape(999.dp),
        border = BorderStroke(1.dp, colors.divider),
        modifier = Modifier.padding(top = 2.dp),
      ) {
        Text(
          text = message.reactions.joinToString(" "),
          style = MaterialTheme.typography.labelSmall,
          modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
        )
      }
    }
  }
}
