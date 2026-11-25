package com.example.domain.repositories

import com.example.domain.models.CallHistoryItem
import com.example.domain.models.ChatMessage
import com.example.domain.models.ConnectionMode
import com.example.domain.models.ConnectionStatus
import com.example.domain.models.Conversation
import com.example.domain.models.NearbyDeviceItem
import com.example.domain.models.NotificationSettingsState
import com.example.domain.models.PrivacySettingsState
import com.example.domain.models.SampleMediaItem
import com.example.domain.models.UserProfile
import kotlinx.coroutines.flow.StateFlow

interface AuthRepository {
  val isLoggedIn: StateFlow<Boolean>
  val currentUser: StateFlow<UserProfile>

  suspend fun login(username: String, password: String, rememberMe: Boolean): Result<UserProfile>
  suspend fun register(username: String, password: String, email: String?): Result<UserProfile>
  suspend fun completeProfileSetup(
    displayName: String,
    username: String,
    about: String,
    avatarUri: String?,
  ): Result<UserProfile>
  suspend fun logout()
  fun isUsernameAvailable(username: String): Boolean
}

interface ChatRepository {
  val conversations: StateFlow<List<Conversation>>
  val messagesByConversation: StateFlow<Map<String, List<ChatMessage>>>
  val callHistory: StateFlow<List<CallHistoryItem>>
  val sampleMedia: StateFlow<List<SampleMediaItem>>

  fun getConversation(conversationId: String): Conversation?
  suspend fun openOrCreateDirectConversation(user: UserProfile): Conversation
  suspend fun createGroup(
    name: String,
    description: String,
    memberIds: List<String>,
    avatarUri: String? = null,
  ): Conversation
  suspend fun sendMessage(
    conversationId: String,
    content: String,
    messageType: String,
    attachmentName: String? = null,
    attachmentSize: String? = null,
    mediaResName: String? = null,
    mediaUri: String? = null,
    durationSeconds: Int = 0,
    stickerEmoji: String? = null,
    gifLabel: String? = null,
    replyToMessage: ChatMessage? = null,
  )
  suspend fun editMessage(conversationId: String, messageId: String, newContent: String)
  suspend fun deleteMessage(conversationId: String, messageId: String)
  suspend fun toggleReaction(conversationId: String, messageId: String, emoji: String)
  suspend fun markConversationRead(conversationId: String)
  suspend fun togglePinConversation(conversationId: String)
  suspend fun toggleMuteConversation(conversationId: String)
  suspend fun archiveConversation(conversationId: String)
  suspend fun deleteConversation(conversationId: String)
  suspend fun clearConversationMessages(conversationId: String)
  suspend fun updateGroupInfo(conversationId: String, name: String, description: String)
  suspend fun updateGroupAvatar(conversationId: String, avatarUri: String): Boolean
  suspend fun updateGroupMemberTag(conversationId: String, userId: String, tag: String)
  suspend fun updateConversationSettings(
    conversationId: String,
    chatLock: Boolean? = null,
    restrictedChat: Boolean? = null,
    translateMessages: Boolean? = null,
    mediaVisibility: Boolean? = null,
    disappearingMessages: String? = null,
    isFavourite: Boolean? = null,
  )
  suspend fun addGroupMembers(conversationId: String, memberIds: List<String>)
  suspend fun removeGroupMember(conversationId: String, userId: String)
  suspend fun toggleGroupAdmin(conversationId: String, userId: String)
  suspend fun leaveGroup(conversationId: String)
}

interface UserRepository {
  val users: StateFlow<List<UserProfile>>
  val blockedUsers: StateFlow<List<UserProfile>>

  fun searchUsers(query: String): List<UserProfile>
  fun getUserById(userId: String): UserProfile?
  suspend fun toggleContact(userId: String)
  suspend fun blockUser(userId: String)
  suspend fun unblockUser(userId: String)
  suspend fun updateCurrentUserProfile(
    displayName: String,
    username: String,
    about: String,
    avatarUri: String?,
  ): Result<UserProfile>
  suspend fun updateStatusBubble(statusBubble: String)
}

interface BluetoothRepository {
  val isBluetoothEnabled: StateFlow<Boolean>
  val isScanning: StateFlow<Boolean>
  val devices: StateFlow<List<NearbyDeviceItem>>
  val activeConnectingDeviceId: StateFlow<String?>

  suspend fun setBluetoothEnabled(enabled: Boolean)
  suspend fun startScan()
  suspend fun stopScan()
  suspend fun connectToDevice(deviceId: String)
  suspend fun cancelConnection(deviceId: String)
  suspend fun disconnectDevice(deviceId: String)
  suspend fun forgetDevice(deviceId: String)
}

interface SettingsRepository {
  val connectionMode: StateFlow<ConnectionMode>
  val connectionStatus: StateFlow<ConnectionStatus>
  val isInternetSimulatedOnline: StateFlow<Boolean>
  val privacySettings: StateFlow<PrivacySettingsState>
  val notificationSettings: StateFlow<NotificationSettingsState>

  suspend fun setConnectionMode(mode: ConnectionMode)
  suspend fun setInternetSimulatedOnline(online: Boolean)
  suspend fun updatePrivacySettings(settings: PrivacySettingsState)
  suspend fun updateNotificationSettings(settings: NotificationSettingsState)
}
