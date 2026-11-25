package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.repository.MockWhatsChatRepository
import com.example.domain.models.BluetoothConnectionState
import com.example.domain.models.ChatMessage
import com.example.domain.models.ConnectionMode
import com.example.domain.models.MediaCategory
import com.example.domain.models.MessageTypes
import com.example.domain.models.NearbyDeviceItem
import com.example.domain.models.NotificationSettingsState
import com.example.domain.models.PrivacySettingsState
import com.example.domain.models.SampleMediaItem
import com.example.domain.models.UserProfile
import com.example.domain.repositories.AuthRepository
import com.example.domain.repositories.BluetoothRepository
import com.example.domain.repositories.ChatRepository
import com.example.domain.repositories.SettingsRepository
import com.example.domain.repositories.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WhatsChatViewModel(
  private val repository: MockWhatsChatRepository = MockWhatsChatRepository(),
) : ViewModel(),
  AuthRepository by repository,
  ChatRepository by repository,
  UserRepository by repository,
  BluetoothRepository by repository,
  SettingsRepository by repository {

  private val _toastMessage = MutableStateFlow<String?>(null)
  val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

  fun showToast(message: String) {
    _toastMessage.value = message
  }

  fun clearToast() {
    _toastMessage.value = null
  }

  fun performLogin(
    username: String,
    password: String,
    rememberMe: Boolean,
    onSuccess: () -> Unit,
    onError: (String) -> Unit,
  ) {
    viewModelScope.launch {
      repository.login(username, password, rememberMe)
        .onSuccess { onSuccess() }
        .onFailure { onError(it.message ?: "Login failed") }
    }
  }

  fun performRegistration(
    username: String,
    password: String,
    email: String?,
    onSuccess: () -> Unit,
    onError: (String) -> Unit,
  ) {
    viewModelScope.launch {
      repository.register(username, password, email)
        .onSuccess { onSuccess() }
        .onFailure { onError(it.message ?: "Registration failed") }
    }
  }

  fun performProfileSetup(
    displayName: String,
    username: String,
    about: String,
    avatarUri: String?,
    onSuccess: () -> Unit,
    onError: (String) -> Unit,
  ) {
    viewModelScope.launch {
      repository.completeProfileSetup(displayName, username, about, avatarUri)
        .onSuccess { onSuccess() }
        .onFailure { onError(it.message ?: "Could not save profile") }
    }
  }

  fun performUpdateProfile(
    displayName: String,
    username: String,
    about: String,
    avatarUri: String?,
    onSuccess: () -> Unit,
    onError: (String) -> Unit,
  ) {
    viewModelScope.launch {
      repository.updateCurrentUserProfile(displayName, username, about, avatarUri)
        .onSuccess {
          showToast("Profile updated locally")
          onSuccess()
        }
        .onFailure { onError(it.message ?: "Could not update profile") }
    }
  }

  fun saveStatusBubble(status: String) {
    viewModelScope.launch {
      repository.updateStatusBubble(status)
      showToast("Status updated")
    }
  }

  fun performLogout(onDone: () -> Unit) {
    viewModelScope.launch {
      repository.logout()
      onDone()
    }
  }

  fun openChatWithUser(user: UserProfile, onOpened: (String) -> Unit) {
    viewModelScope.launch {
      val conv = repository.openOrCreateDirectConversation(user)
      onOpened(conv.id)
    }
  }

  fun createNewGroup(
    name: String,
    description: String,
    memberIds: List<String>,
    avatarUri: String? = null,
    onCreated: (String) -> Unit,
  ) {
    viewModelScope.launch {
      val created = repository.createGroup(name, description, memberIds, avatarUri)
      onCreated(created.id)
    }
  }

  fun sendTextMessage(
    conversationId: String,
    text: String,
    replyTo: ChatMessage? = null,
  ) {
    val clean = text.trim()
    if (clean.isEmpty()) return
    viewModelScope.launch {
      repository.sendMessage(
        conversationId = conversationId,
        content = clean,
        messageType = MessageTypes.TEXT,
        replyToMessage = replyTo,
      )
    }
  }

  fun sendPickedMediaMessage(
    conversationId: String,
    mediaUri: String,
    isVideo: Boolean,
    caption: String,
  ) {
    viewModelScope.launch {
      val type = if (isVideo) MessageTypes.VIDEO else MessageTypes.IMAGE
      repository.sendMessage(
        conversationId = conversationId,
        content = caption.trim(),
        messageType = type,
        attachmentName = if (isVideo) "gallery_video.mp4" else "gallery_photo.jpg",
        attachmentSize = if (isVideo) "4.6 MB" else "1.4 MB",
        mediaUri = mediaUri,
        durationSeconds = if (isVideo) 18 else 0,
      )
    }
  }

  fun sendSampleMediaItems(
    conversationId: String,
    items: List<SampleMediaItem>,
    caption: String,
  ) {
    if (items.isEmpty()) return
    viewModelScope.launch {
      items.forEachIndexed { index, item ->
        val type = when (item.category) {
          MediaCategory.PHOTOS -> MessageTypes.IMAGE
          MediaCategory.VIDEOS -> MessageTypes.VIDEO
          MediaCategory.DOCUMENTS -> MessageTypes.FILE
          MediaCategory.AUDIO -> MessageTypes.AUDIO
        }
        val text = if (index == 0 && caption.isNotBlank()) caption.trim() else item.title
        repository.sendMessage(
          conversationId = conversationId,
          content = text,
          messageType = type,
          attachmentName = item.title,
          attachmentSize = item.sizeText,
          mediaResName = item.previewResName,
          durationSeconds = if (type == MessageTypes.AUDIO) 19 else if (type == MessageTypes.VIDEO) 42 else 0,
        )
      }
      showToast("Added ${items.size} attachment(s) to local chat")
    }
  }

  fun sendVoiceNote(conversationId: String, durationSeconds: Int) {
    val safeDuration = durationSeconds.coerceAtLeast(1)
    val durationText = "%d:%02d".format(safeDuration / 60, safeDuration % 60)
    viewModelScope.launch {
      repository.sendMessage(
        conversationId = conversationId,
        content = "Voice message",
        messageType = MessageTypes.AUDIO,
        attachmentName = "voice_note_${System.currentTimeMillis() % 1000}.m4a",
        attachmentSize = "$durationText · ${safeDuration * 14} KB",
        durationSeconds = safeDuration,
      )
    }
  }

  fun sendSticker(conversationId: String, emoji: String, label: String) {
    viewModelScope.launch {
      repository.sendMessage(
        conversationId = conversationId,
        content = label,
        messageType = MessageTypes.STICKER,
        stickerEmoji = emoji,
      )
    }
  }

  fun sendGif(conversationId: String, gifLabel: String, badgeEmoji: String) {
    viewModelScope.launch {
      repository.sendMessage(
        conversationId = conversationId,
        content = gifLabel,
        messageType = MessageTypes.GIF,
        gifLabel = gifLabel,
        stickerEmoji = badgeEmoji,
      )
    }
  }

  fun editChatMessage(conversationId: String, messageId: String, newText: String) {
    viewModelScope.launch { repository.editMessage(conversationId, messageId, newText) }
  }

  fun deleteChatMessage(conversationId: String, messageId: String) {
    viewModelScope.launch { repository.deleteMessage(conversationId, messageId) }
  }

  fun reactToMessage(conversationId: String, messageId: String, emoji: String) {
    viewModelScope.launch { repository.toggleReaction(conversationId, messageId, emoji) }
  }

  fun markRead(conversationId: String) {
    viewModelScope.launch { repository.markConversationRead(conversationId) }
  }

  fun pinConversation(conversationId: String) {
    viewModelScope.launch { repository.togglePinConversation(conversationId) }
  }

  fun muteConversation(conversationId: String) {
    viewModelScope.launch { repository.toggleMuteConversation(conversationId) }
  }

  fun archiveConversationAction(conversationId: String) {
    viewModelScope.launch { repository.archiveConversation(conversationId) }
  }

  fun deleteConversationAction(conversationId: String) {
    viewModelScope.launch { repository.deleteConversation(conversationId) }
  }

  fun clearChatHistory(conversationId: String) {
    viewModelScope.launch {
      repository.clearConversationMessages(conversationId)
      showToast("Chat history cleared")
    }
  }

  fun saveGroupInfo(conversationId: String, name: String, description: String) {
    viewModelScope.launch {
      repository.updateGroupInfo(conversationId, name, description)
      showToast("Group info updated")
    }
  }

  fun changeGroupAvatarByAdmin(conversationId: String, avatarUri: String) {
    viewModelScope.launch {
      val updated = repository.updateGroupAvatar(conversationId, avatarUri)
      if (updated) {
        showToast("Group picture updated")
      } else {
        showToast("Only group admins can change the group picture")
      }
    }
  }

  fun saveGroupMemberTag(conversationId: String, userId: String, tag: String) {
    viewModelScope.launch {
      repository.updateGroupMemberTag(conversationId, userId, tag)
      showToast("Member tag updated")
    }
  }

  fun updateChatSettings(
    conversationId: String,
    chatLock: Boolean? = null,
    restrictedChat: Boolean? = null,
    translateMessages: Boolean? = null,
    mediaVisibility: Boolean? = null,
    disappearingMessages: String? = null,
    isFavourite: Boolean? = null,
  ) {
    viewModelScope.launch {
      repository.updateConversationSettings(
        conversationId = conversationId,
        chatLock = chatLock,
        restrictedChat = restrictedChat,
        translateMessages = translateMessages,
        mediaVisibility = mediaVisibility,
        disappearingMessages = disappearingMessages,
        isFavourite = isFavourite,
      )
    }
  }

  fun addMembersToGroup(conversationId: String, ids: List<String>) {
    viewModelScope.launch {
      repository.addGroupMembers(conversationId, ids)
      showToast("Added ${ids.size} member(s)")
    }
  }

  fun removeMemberFromGroup(conversationId: String, userId: String) {
    viewModelScope.launch { repository.removeGroupMember(conversationId, userId) }
  }

  fun toggleAdminStatus(conversationId: String, userId: String) {
    viewModelScope.launch { repository.toggleGroupAdmin(conversationId, userId) }
  }

  fun leaveGroupAction(conversationId: String, onDone: () -> Unit) {
    viewModelScope.launch {
      repository.leaveGroup(conversationId)
      onDone()
    }
  }

  fun toggleUserContact(userId: String) {
    viewModelScope.launch { repository.toggleContact(userId) }
  }

  fun blockUserAction(userId: String) {
    viewModelScope.launch {
      repository.blockUser(userId)
      showToast("User blocked")
    }
  }

  fun unblockUserAction(userId: String) {
    viewModelScope.launch {
      repository.unblockUser(userId)
      showToast("User unblocked")
    }
  }

  fun toggleBluetoothEnabled(enabled: Boolean) {
    viewModelScope.launch { repository.setBluetoothEnabled(enabled) }
  }

  fun startBluetoothScan() {
    viewModelScope.launch { repository.startScan() }
  }

  fun stopBluetoothScan() {
    viewModelScope.launch { repository.stopScan() }
  }

  fun initiateBluetoothConnection(deviceId: String) {
    viewModelScope.launch { repository.connectToDevice(deviceId) }
  }

  fun cancelBluetoothConnection(deviceId: String) {
    viewModelScope.launch { repository.cancelConnection(deviceId) }
  }

  fun disconnectBluetoothDevice(deviceId: String) {
    viewModelScope.launch {
      repository.disconnectDevice(deviceId)
      showToast("Bluetooth device disconnected")
    }
  }

  fun forgetBluetoothDevice(deviceId: String) {
    viewModelScope.launch {
      repository.forgetDevice(deviceId)
      showToast("Device removed from paired list")
    }
  }

  fun saveConnectionMode(mode: ConnectionMode) {
    viewModelScope.launch {
      repository.setConnectionMode(mode)
      showToast("Connection preference set to ${mode.label}")
    }
  }

  fun toggleSimulatedInternet(online: Boolean) {
    viewModelScope.launch { repository.setInternetSimulatedOnline(online) }
  }

  fun savePrivacySettings(settings: PrivacySettingsState) {
    viewModelScope.launch { repository.updatePrivacySettings(settings) }
  }

  fun saveNotificationSettings(settings: NotificationSettingsState) {
    viewModelScope.launch { repository.updateNotificationSettings(settings) }
  }

  fun getConnectedBluetoothDevice(): NearbyDeviceItem? {
    return devices.value.firstOrNull {
      it.connectionState == BluetoothConnectionState.CONNECTED
    }
  }
}
