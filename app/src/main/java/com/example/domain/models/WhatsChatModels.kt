package com.example.domain.models

enum class ConnectionMode(val label: String, val description: String) {
  AUTOMATIC(
    label = "Automatic",
    description = "Prefer Internet when online, fall back to authorized nearby Bluetooth, otherwise keep messages pending.",
  ),
  INTERNET_ONLY(
    label = "Internet Only",
    description = "Use Internet communication only via Node.js/WebSocket backend.",
  ),
  BLUETOOTH_ONLY(
    label = "Bluetooth Only",
    description = "Use direct Bluetooth communication only when a compatible nearby device is connected.",
  ),
}

enum class ConnectionStatus(val label: String) {
  AUTOMATIC("Automatic"),
  INTERNET_CONNECTED("Internet Connected"),
  BLUETOOTH_CONNECTED("Bluetooth Connected"),
  PENDING("Message Pending"),
  OFFLINE("Offline"),
}

object MessageTransport {
  const val INTERNET = "INTERNET"
  const val BLUETOOTH = "BLUETOOTH"
}

object MessageTypes {
  const val TEXT = "TEXT"
  const val IMAGE = "IMAGE"
  const val VIDEO = "VIDEO"
  const val FILE = "FILE"
  const val AUDIO = "AUDIO"
  const val STICKER = "STICKER"
  const val GIF = "GIF"
}

object DeliveryStates {
  const val PENDING = "PENDING"
  const val SENDING = "SENDING"
  const val SENT = "SENT"
  const val DELIVERED = "DELIVERED"
  const val FAILED = "FAILED"
}

enum class PrivacyVisibility(val label: String) {
  EVERYONE("Everyone"),
  MY_CONTACTS("My Contacts"),
  NOBODY("Nobody"),
}

enum class CallType {
  VOICE,
  VIDEO,
}

enum class CallState(val label: String) {
  CALLING("Calling..."),
  RINGING("Ringing..."),
  CONNECTING("Connecting..."),
  CONNECTED("Connected (Mock Preview)"),
  RECONNECTING("Reconnecting..."),
  ENDED("Call Ended"),
  REJECTED("Call Declined"),
}

enum class MediaCategory(val label: String) {
  PHOTOS("Photos"),
  VIDEOS("Videos"),
  DOCUMENTS("Documents"),
  AUDIO("Audio"),
}

enum class BluetoothConnectionState(val label: String) {
  DISCONNECTED("Not Connected"),
  SCANNING("Scanning"),
  REQUESTING("Requesting Permission"),
  CONNECTING("Connecting..."),
  PAIRING("Authorizing Device..."),
  CONNECTED("Connected"),
  FAILED("Connection Failed"),
}

data class UserProfile(
  val id: String,
  val displayName: String,
  val username: String,
  val about: String,
  val statusBubble: String = "What's happening?",
  val email: String? = null,
  val avatarResName: String? = null,
  val avatarUri: String? = null,
  val isOnline: Boolean = false,
  val lastSeenText: String = "Last seen recently",
  val isContact: Boolean = false,
  val isBlocked: Boolean = false,
  val sharedMediaCount: Int = 12,
  val commonGroupsCount: Int = 2,
)

data class ChatMessage(
  val messageId: String,
  val conversationId: String,
  val senderId: String,
  val receiverId: String?,
  val content: String,
  val messageType: String = MessageTypes.TEXT,
  val transport: String = MessageTransport.INTERNET,
  val timestamp: Long,
  val deliveryStatus: String = DeliveryStates.SENT,
  val senderName: String = "",
  val senderAvatarResName: String? = null,
  val replyToMessageId: String? = null,
  val replyPreview: String? = null,
  val attachmentName: String? = null,
  val attachmentSize: String? = null,
  val mediaResName: String? = null,
  val mediaUri: String? = null,
  val durationSeconds: Int = 0,
  val stickerEmoji: String? = null,
  val gifLabel: String? = null,
  val reactions: List<String> = emptyList(),
  val isEdited: Boolean = false,
  val formattedTime: String = "",
)

data class GroupMember(
  val user: UserProfile,
  val isAdmin: Boolean = false,
)

data class Conversation(
  val id: String,
  val title: String,
  val username: String,
  val isGroup: Boolean = false,
  val description: String = "",
  val avatarResName: String? = null,
  val avatarUri: String? = null,
  val participantIds: List<String> = emptyList(),
  val adminIds: List<String> = emptyList(),
  val memberTags: Map<String, String> = emptyMap(),
  val lastMessagePreview: String = "",
  val lastMessageTime: String = "",
  val lastMessageTimestamp: Long = 0L,
  val lastMessageDelivery: String = DeliveryStates.DELIVERED,
  val lastMessageTransport: String = MessageTransport.INTERNET,
  val unreadCount: Int = 0,
  val isOnline: Boolean = false,
  val isPinned: Boolean = false,
  val isMuted: Boolean = false,
  val isArchived: Boolean = false,
  val isTyping: Boolean = false,
  val chatLock: Boolean = false,
  val restrictedChat: Boolean = false,
  val translateMessages: Boolean = false,
  val mediaVisibility: Boolean = true,
  val disappearingMessages: String = "Off",
  val isFavourite: Boolean = false,
  val createdByText: String = "Created by Alex Kumar, 24/11/2026",
)

data class CallHistoryItem(
  val id: String,
  val conversationId: String,
  val participantName: String,
  val participantUsername: String,
  val avatarResName: String? = null,
  val isGroup: Boolean = false,
  val callType: CallType = CallType.VOICE,
  val isOutgoing: Boolean = true,
  val isMissed: Boolean = false,
  val timestampText: String,
  val durationText: String,
  val transport: String = MessageTransport.INTERNET,
)

data class NearbyDeviceItem(
  val id: String,
  val deviceName: String,
  val compatibilityLabel: String = "WhatsChat Compatible",
  val signalStrengthDbm: Int = -54,
  val isPaired: Boolean = false,
  val connectionState: BluetoothConnectionState = BluetoothConnectionState.DISCONNECTED,
  val lastConnectedText: String = "Never",
  val linkedUsername: String? = null,
)

data class SampleMediaItem(
  val id: String,
  val title: String,
  val sizeText: String,
  val subtitle: String,
  val category: MediaCategory,
  val previewResName: String? = null,
)

data class PrivacySettingsState(
  val lastSeen: PrivacyVisibility = PrivacyVisibility.EVERYONE,
  val onlineStatus: PrivacyVisibility = PrivacyVisibility.EVERYONE,
  val profilePhoto: PrivacyVisibility = PrivacyVisibility.EVERYONE,
  val about: PrivacyVisibility = PrivacyVisibility.EVERYONE,
  val readReceipts: Boolean = true,
  val typingIndicators: Boolean = true,
)

data class NotificationSettingsState(
  val messageNotifications: Boolean = true,
  val groupNotifications: Boolean = true,
  val callNotifications: Boolean = true,
  val soundEnabled: Boolean = true,
  val selectedSound: String = "Emerald Chime",
  val vibrationEnabled: Boolean = true,
  val showPreview: Boolean = true,
  val doNotDisturb: Boolean = false,
)

data class VideoCallParticipant(
  val id: String,
  val name: String,
  val username: String,
  val avatarResName: String? = null,
  val isMuted: Boolean = false,
  val isCameraOff: Boolean = false,
  val isSpeaking: Boolean = false,
  val qualityLabel: String = "HD Mock",
)
