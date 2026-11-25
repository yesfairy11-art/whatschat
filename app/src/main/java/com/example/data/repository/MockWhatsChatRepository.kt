package com.example.data.repository

import com.example.core.common.UsernameValidator
import com.example.domain.models.BluetoothConnectionState
import com.example.domain.models.CallHistoryItem
import com.example.domain.models.CallType
import com.example.domain.models.ChatMessage
import com.example.domain.models.ConnectionMode
import com.example.domain.models.ConnectionStatus
import com.example.domain.models.Conversation
import com.example.domain.models.DeliveryStates
import com.example.domain.models.MediaCategory
import com.example.domain.models.MessageTransport
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
import com.example.domain.usecases.ConnectionRouterUseCase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MockWhatsChatRepository :
  AuthRepository,
  ChatRepository,
  UserRepository,
  BluetoothRepository,
  SettingsRepository {

  private val router = ConnectionRouterUseCase()

  private val _isLoggedIn = MutableStateFlow(false)
  override val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

  private val initialMe = UserProfile(
    id = "user_me",
    displayName = "Alex Kumar",
    username = "alexkumar",
    about = "Life is better with WhatsChat! Always Connected.",
    statusBubble = "What's happening?",
    email = "alex.kumar@whatschat.example",
    avatarResName = "img_avatar_alex",
    isOnline = true,
    lastSeenText = "Online",
    isContact = false,
    sharedMediaCount = 128,
    commonGroupsCount = 4,
  )

  private val _currentUser = MutableStateFlow(initialMe)
  override val currentUser: StateFlow<UserProfile> = _currentUser.asStateFlow()

  private val initialUsers = listOf(
    UserProfile(
      id = "user_sarah",
      displayName = "Sarah Johnson",
      username = "sarah_j",
      about = "Designing calm spaces & traveling light.",
      avatarResName = "img_avatar_sarah",
      isOnline = true,
      lastSeenText = "Online",
      isContact = true,
      sharedMediaCount = 42,
      commonGroupsCount = 2,
    ),
    UserProfile(
      id = "user_david",
      displayName = "David Wilson",
      username = "david",
      about = "Offline-first systems & coffee.",
      isOnline = true,
      lastSeenText = "Online",
      isContact = true,
      sharedMediaCount = 18,
      commonGroupsCount = 2,
    ),
    UserProfile(
      id = "user_emma",
      displayName = "Emma Davis",
      username = "emma",
      about = "Product photographer & hiker.",
      isOnline = false,
      lastSeenText = "Last seen 14m ago",
      isContact = true,
      sharedMediaCount = 27,
      commonGroupsCount = 1,
    ),
    UserProfile(
      id = "user_michael",
      displayName = "Michael Brown",
      username = "michael",
      about = "Building Android experiences.",
      isOnline = true,
      lastSeenText = "Online",
      isContact = true,
      sharedMediaCount = 9,
      commonGroupsCount = 2,
    ),
    UserProfile(
      id = "user_sophia",
      displayName = "Sophia Martinez",
      username = "sophia",
      about = "Available via Internet & nearby Bluetooth.",
      isOnline = false,
      lastSeenText = "Last seen today at 2:10 PM",
      isContact = false,
      sharedMediaCount = 4,
      commonGroupsCount = 1,
    ),
    UserProfile(
      id = "user_liam",
      displayName = "Liam O'Connor",
      username = "liam_oc",
      about = "Audio engineer & cyclist.",
      isOnline = true,
      lastSeenText = "Online",
      isContact = true,
      sharedMediaCount = 15,
      commonGroupsCount = 1,
    ),
    UserProfile(
      id = "user_john_blocked",
      displayName = "John Doe",
      username = "john_doe_99",
      about = "User",
      isOnline = false,
      lastSeenText = "Hidden",
      isContact = false,
      isBlocked = true,
    ),
    UserProfile(
      id = "user_spam_blocked",
      displayName = "Promo Alerts",
      username = "promo_bot",
      about = "Automated messages",
      isOnline = false,
      lastSeenText = "Hidden",
      isContact = false,
      isBlocked = true,
    ),
  )

  private val _users = MutableStateFlow(initialUsers)
  override val users: StateFlow<List<UserProfile>> = _users.asStateFlow()

  private val _blockedUsers = MutableStateFlow(initialUsers.filter { it.isBlocked })
  override val blockedUsers: StateFlow<List<UserProfile>> = _blockedUsers.asStateFlow()

  private val now = System.currentTimeMillis()

  private val initialConversations = listOf(
    Conversation(
      id = "conv_sarah",
      title = "Sarah Johnson",
      username = "sarah_j",
      isGroup = false,
      avatarResName = "img_avatar_sarah",
      participantIds = listOf("user_me", "user_sarah"),
      lastMessagePreview = "Voice note (0:19) · Bluetooth",
      lastMessageTime = "10:27 AM",
      lastMessageTimestamp = now - 120_000L,
      lastMessageDelivery = DeliveryStates.DELIVERED,
      lastMessageTransport = MessageTransport.BLUETOOTH,
      unreadCount = 2,
      isOnline = true,
      isPinned = true,
      disappearingMessages = "7 days",
    ),
    Conversation(
      id = "conv_family_group",
      title = "Friends Group",
      username = "friends_group",
      isGroup = true,
      description = "A place for good friends — works online and nearby!",
      participantIds = listOf("user_me", "user_sarah", "user_david", "user_emma", "user_michael"),
      adminIds = listOf("user_me", "user_sarah"),
      lastMessagePreview = "David: Great! See everyone at 6 PM.",
      lastMessageTime = "10:15 AM",
      lastMessageTimestamp = now - 300_000L,
      lastMessageDelivery = DeliveryStates.DELIVERED,
      lastMessageTransport = MessageTransport.INTERNET,
      unreadCount = 4,
      isOnline = true,
      createdByText = "Created by Alex Kumar, 24/11/2026",
    ),
    Conversation(
      id = "conv_david",
      title = "David Wilson",
      username = "david",
      isGroup = false,
      participantIds = listOf("user_me", "user_david"),
      lastMessagePreview = "Sent WhatsChat_Architecture_v2.pdf",
      lastMessageTime = "9:48 AM",
      lastMessageTimestamp = now - 900_000L,
      lastMessageDelivery = DeliveryStates.DELIVERED,
      lastMessageTransport = MessageTransport.INTERNET,
      unreadCount = 0,
      isOnline = true,
    ),
    Conversation(
      id = "conv_work_team",
      title = "Work Team",
      username = "work_team",
      isGroup = true,
      description = "Engineering & Design Sprint Sync",
      participantIds = listOf("user_me", "user_michael", "user_liam", "user_emma"),
      adminIds = listOf("user_me"),
      lastMessagePreview = "Sprint walkthrough video · 1:12",
      lastMessageTime = "Yesterday",
      lastMessageTimestamp = now - 3_600_000L,
      lastMessageDelivery = DeliveryStates.SENT,
      lastMessageTransport = MessageTransport.INTERNET,
      unreadCount = 0,
      isMuted = true,
      createdByText = "Created by Alex Kumar, 20/11/2026",
    ),
    Conversation(
      id = "conv_emma",
      title = "Emma Davis",
      username = "emma",
      isGroup = false,
      participantIds = listOf("user_me", "user_emma"),
      lastMessagePreview = "Message queued until Emma is reachable",
      lastMessageTime = "Yesterday",
      lastMessageTimestamp = now - 7_200_000L,
      lastMessageDelivery = DeliveryStates.PENDING,
      lastMessageTransport = MessageTransport.BLUETOOTH,
      unreadCount = 0,
      isOnline = false,
    ),
  )

  private val _conversations = MutableStateFlow(initialConversations)
  override val conversations: StateFlow<List<Conversation>> = _conversations.asStateFlow()

  private val initialMessages: Map<String, List<ChatMessage>> = mapOf(
    "conv_sarah" to listOf(
      ChatMessage(
        messageId = "msg_s_1",
        conversationId = "conv_sarah",
        senderId = "user_me",
        receiverId = "user_sarah",
        content = "Hey Sarah! How are you doing today?",
        messageType = MessageTypes.TEXT,
        transport = MessageTransport.INTERNET,
        timestamp = now - 600_000L,
        deliveryStatus = DeliveryStates.DELIVERED,
        senderName = "Alex Kumar",
        senderAvatarResName = "img_avatar_alex",
        formattedTime = "10:22 AM",
      ),
      ChatMessage(
        messageId = "msg_s_2",
        conversationId = "conv_sarah",
        senderId = "user_sarah",
        receiverId = "user_me",
        content = "I'm great! Just arrived at the ridge overlook.",
        messageType = MessageTypes.TEXT,
        transport = MessageTransport.INTERNET,
        timestamp = now - 480_000L,
        deliveryStatus = DeliveryStates.DELIVERED,
        senderName = "Sarah Johnson",
        senderAvatarResName = "img_avatar_sarah",
        formattedTime = "10:24 AM",
        reactions = listOf("🌿"),
      ),
      ChatMessage(
        messageId = "msg_s_3",
        conversationId = "conv_sarah",
        senderId = "user_sarah",
        receiverId = "user_me",
        content = "Captured this view on the way up!",
        messageType = MessageTypes.IMAGE,
        transport = MessageTransport.INTERNET,
        timestamp = now - 360_000L,
        deliveryStatus = DeliveryStates.DELIVERED,
        senderName = "Sarah Johnson",
        senderAvatarResName = "img_avatar_sarah",
        mediaResName = "img_sample_landscape",
        attachmentName = "alpine_lake_morning.jpg",
        attachmentSize = "2.4 MB",
        formattedTime = "10:25 AM",
      ),
      ChatMessage(
        messageId = "msg_s_4",
        conversationId = "conv_sarah",
        senderId = "user_me",
        receiverId = "user_sarah",
        content = "Wow, that looks peaceful! Switching to Bluetooth nearby mode now since cell signal drops near the trail.",
        messageType = MessageTypes.TEXT,
        transport = MessageTransport.BLUETOOTH,
        timestamp = now - 240_000L,
        deliveryStatus = DeliveryStates.DELIVERED,
        senderName = "Alex Kumar",
        senderAvatarResName = "img_avatar_alex",
        replyToMessageId = "msg_s_3",
        replyPreview = "Photo · alpine_lake_morning.jpg",
        formattedTime = "10:26 AM",
      ),
      ChatMessage(
        messageId = "msg_s_5",
        conversationId = "conv_sarah",
        senderId = "user_sarah",
        receiverId = "user_me",
        content = "Voice note",
        messageType = MessageTypes.AUDIO,
        transport = MessageTransport.BLUETOOTH,
        timestamp = now - 180_000L,
        deliveryStatus = DeliveryStates.DELIVERED,
        senderName = "Sarah Johnson",
        senderAvatarResName = "img_avatar_sarah",
        attachmentName = "trail_voice_01.m4a",
        attachmentSize = "0:19 · 310 KB",
        durationSeconds = 19,
        formattedTime = "10:26 AM",
        reactions = listOf("👍"),
      ),
      ChatMessage(
        messageId = "msg_s_6",
        conversationId = "conv_sarah",
        senderId = "user_me",
        receiverId = "user_sarah",
        content = "Nice! Let's sync the trail map over Bluetooth too.",
        messageType = MessageTypes.TEXT,
        transport = MessageTransport.BLUETOOTH,
        timestamp = now - 120_000L,
        deliveryStatus = DeliveryStates.DELIVERED,
        senderName = "Alex Kumar",
        senderAvatarResName = "img_avatar_alex",
        formattedTime = "10:27 AM",
      ),
    ),
    "conv_family_group" to listOf(
      ChatMessage(
        messageId = "msg_g_1",
        conversationId = "conv_family_group",
        senderId = "user_sarah",
        receiverId = null,
        content = "Welcome everyone to the unified WhatsChat group!",
        messageType = MessageTypes.TEXT,
        transport = MessageTransport.INTERNET,
        timestamp = now - 900_000L,
        deliveryStatus = DeliveryStates.DELIVERED,
        senderName = "Sarah Johnson",
        senderAvatarResName = "img_avatar_sarah",
        formattedTime = "10:02 AM",
      ),
      ChatMessage(
        messageId = "msg_g_2",
        conversationId = "conv_family_group",
        senderId = "user_david",
        receiverId = null,
        content = "Great! See everyone at 6 PM.",
        messageType = MessageTypes.TEXT,
        transport = MessageTransport.INTERNET,
        timestamp = now - 300_000L,
        deliveryStatus = DeliveryStates.DELIVERED,
        senderName = "David Wilson",
        formattedTime = "10:15 AM",
        reactions = listOf("👍", "🔥"),
      ),
    ),
    "conv_david" to listOf(
      ChatMessage(
        messageId = "msg_d_1",
        conversationId = "conv_david",
        senderId = "user_david",
        receiverId = "user_me",
        content = "Here is the updated architecture specification.",
        messageType = MessageTypes.FILE,
        transport = MessageTransport.INTERNET,
        timestamp = now - 900_000L,
        deliveryStatus = DeliveryStates.DELIVERED,
        senderName = "David Wilson",
        attachmentName = "WhatsChat_Architecture_v2.pdf",
        attachmentSize = "1.8 MB · 7 pages · PDF",
        formattedTime = "9:48 AM",
      ),
    ),
    "conv_work_team" to listOf(
      ChatMessage(
        messageId = "msg_w_1",
        conversationId = "conv_work_team",
        senderId = "user_michael",
        receiverId = null,
        content = "Sprint walkthrough clip uploaded",
        messageType = MessageTypes.VIDEO,
        transport = MessageTransport.INTERNET,
        timestamp = now - 3_600_000L,
        deliveryStatus = DeliveryStates.SENT,
        senderName = "Michael Brown",
        mediaResName = "img_sample_landscape",
        attachmentName = "sprint_demo_clip.mp4",
        attachmentSize = "14.2 MB",
        durationSeconds = 72,
        formattedTime = "Yesterday",
      ),
    ),
    "conv_emma" to listOf(
      ChatMessage(
        messageId = "msg_e_1",
        conversationId = "conv_emma",
        senderId = "user_me",
        receiverId = "user_emma",
        content = "Hey Emma, ping me once you're within Bluetooth range!",
        messageType = MessageTypes.TEXT,
        transport = MessageTransport.BLUETOOTH,
        timestamp = now - 7_200_000L,
        deliveryStatus = DeliveryStates.PENDING,
        senderName = "Alex Kumar",
        senderAvatarResName = "img_avatar_alex",
        formattedTime = "Yesterday",
      ),
    ),
  )

  private val _messagesByConversation = MutableStateFlow(initialMessages)
  override val messagesByConversation: StateFlow<Map<String, List<ChatMessage>>> =
    _messagesByConversation.asStateFlow()

  private val _callHistory = MutableStateFlow(
    listOf(
      CallHistoryItem(
        id = "call_1",
        conversationId = "conv_sarah",
        participantName = "Sarah Johnson",
        participantUsername = "sarah_j",
        avatarResName = "img_avatar_sarah",
        callType = CallType.VOICE,
        isOutgoing = true,
        isMissed = false,
        timestampText = "Today, 9:40 AM",
        durationText = "04:18 (Mock)",
        transport = MessageTransport.INTERNET,
      ),
      CallHistoryItem(
        id = "call_2",
        conversationId = "conv_family_group",
        participantName = "Friends Group",
        participantUsername = "friends_group",
        isGroup = true,
        callType = CallType.VIDEO,
        isOutgoing = false,
        isMissed = false,
        timestampText = "Yesterday, 8:15 PM",
        durationText = "18:42 (Mock)",
        transport = MessageTransport.INTERNET,
      ),
      CallHistoryItem(
        id = "call_3",
        conversationId = "conv_david",
        participantName = "David Wilson",
        participantUsername = "david",
        callType = CallType.VOICE,
        isOutgoing = false,
        isMissed = true,
        timestampText = "Yesterday, 3:05 PM",
        durationText = "Missed",
        transport = MessageTransport.INTERNET,
      ),
    ),
  )
  override val callHistory: StateFlow<List<CallHistoryItem>> = _callHistory.asStateFlow()

  private val _sampleMedia = MutableStateFlow(
    listOf(
      SampleMediaItem(
        id = "media_1",
        title = "alpine_lake_morning.jpg",
        sizeText = "2.4 MB",
        subtitle = "1920 × 1080 · JPEG",
        category = MediaCategory.PHOTOS,
        previewResName = "img_sample_landscape",
      ),
      SampleMediaItem(
        id = "media_2",
        title = "whatschat_concept_art.jpg",
        sizeText = "1.1 MB",
        subtitle = "1600 × 1200 · JPEG",
        category = MediaCategory.PHOTOS,
        previewResName = "img_welcome_illustration",
      ),
      SampleMediaItem(
        id = "media_3",
        title = "profile_snapshot_sarah.jpg",
        sizeText = "840 KB",
        subtitle = "1080 × 1080 · JPEG",
        category = MediaCategory.PHOTOS,
        previewResName = "img_avatar_sarah",
      ),
      SampleMediaItem(
        id = "media_4",
        title = "sprint_demo_clip.mp4",
        sizeText = "14.2 MB",
        subtitle = "01:12 · 1080p MP4",
        category = MediaCategory.VIDEOS,
        previewResName = "img_sample_landscape",
      ),
      SampleMediaItem(
        id = "media_5",
        title = "bluetooth_handshake_test.mp4",
        sizeText = "6.8 MB",
        subtitle = "00:34 · 720p MP4",
        category = MediaCategory.VIDEOS,
        previewResName = "img_welcome_illustration",
      ),
      SampleMediaItem(
        id = "media_6",
        title = "WhatsChat_Architecture_v2.pdf",
        sizeText = "1.8 MB",
        subtitle = "PDF Document · 14 pages",
        category = MediaCategory.DOCUMENTS,
      ),
      SampleMediaItem(
        id = "media_7",
        title = "PostgreSQL_Schema_Draft.sql",
        sizeText = "42 KB",
        subtitle = "SQL Spec · UTF-8",
        category = MediaCategory.DOCUMENTS,
      ),
      SampleMediaItem(
        id = "media_8",
        title = "RFCOMM_Protocol_Notes.docx",
        sizeText = "620 KB",
        subtitle = "Word Document",
        category = MediaCategory.DOCUMENTS,
      ),
      SampleMediaItem(
        id = "media_9",
        title = "trail_voice_01.m4a",
        sizeText = "310 KB",
        subtitle = "00:19 · AAC Audio",
        category = MediaCategory.AUDIO,
      ),
      SampleMediaItem(
        id = "media_10",
        title = "emerald_chime_preview.ogg",
        sizeText = "128 KB",
        subtitle = "00:08 · OGG Audio",
        category = MediaCategory.AUDIO,
      ),
    ),
  )
  override val sampleMedia: StateFlow<List<SampleMediaItem>> = _sampleMedia.asStateFlow()

  private val _isBluetoothEnabled = MutableStateFlow(true)
  override val isBluetoothEnabled: StateFlow<Boolean> = _isBluetoothEnabled.asStateFlow()

  private val _isScanning = MutableStateFlow(false)
  override val isScanning: StateFlow<Boolean> = _isScanning.asStateFlow()

  private val _devices = MutableStateFlow(
    listOf(
      NearbyDeviceItem(
        id = "bt_pixel_8",
        deviceName = "Pixel 8 Pro · WhatsChat",
        compatibilityLabel = "WhatsChat v1.0 · RFCOMM Ready",
        signalStrengthDbm = -48,
        isPaired = true,
        connectionState = BluetoothConnectionState.CONNECTED,
        lastConnectedText = "Connected now",
        linkedUsername = "sarah_j",
      ),
      NearbyDeviceItem(
        id = "bt_oneplus_11",
        deviceName = "OnePlus 11 · WhatsChat",
        compatibilityLabel = "WhatsChat v1.0 · RFCOMM Ready",
        signalStrengthDbm = -61,
        isPaired = true,
        connectionState = BluetoothConnectionState.DISCONNECTED,
        lastConnectedText = "Today, 8:40 AM",
        linkedUsername = "david",
      ),
      NearbyDeviceItem(
        id = "bt_redmi_12",
        deviceName = "Redmi Note 12 · WhatsChat",
        compatibilityLabel = "WhatsChat Nearby Node",
        signalStrengthDbm = -72,
        isPaired = false,
        connectionState = BluetoothConnectionState.DISCONNECTED,
        lastConnectedText = "Nearby",
        linkedUsername = "emma",
      ),
      NearbyDeviceItem(
        id = "bt_galaxy_s24",
        deviceName = "Galaxy S24 · WhatsChat",
        compatibilityLabel = "WhatsChat Nearby Node",
        signalStrengthDbm = -79,
        isPaired = false,
        connectionState = BluetoothConnectionState.DISCONNECTED,
        lastConnectedText = "2 days ago",
        linkedUsername = "michael",
      ),
    ),
  )
  override val devices: StateFlow<List<NearbyDeviceItem>> = _devices.asStateFlow()

  private val _activeConnectingDeviceId = MutableStateFlow<String?>(null)
  override val activeConnectingDeviceId: StateFlow<String?> =
    _activeConnectingDeviceId.asStateFlow()

  private val _connectionMode = MutableStateFlow(ConnectionMode.AUTOMATIC)
  override val connectionMode: StateFlow<ConnectionMode> = _connectionMode.asStateFlow()

  private val _isInternetSimulatedOnline = MutableStateFlow(true)
  override val isInternetSimulatedOnline: StateFlow<Boolean> =
    _isInternetSimulatedOnline.asStateFlow()

  private val _connectionStatus = MutableStateFlow(ConnectionStatus.AUTOMATIC)
  override val connectionStatus: StateFlow<ConnectionStatus> = _connectionStatus.asStateFlow()

  private val _privacySettings = MutableStateFlow(PrivacySettingsState())
  override val privacySettings: StateFlow<PrivacySettingsState> = _privacySettings.asStateFlow()

  private val _notificationSettings = MutableStateFlow(NotificationSettingsState())
  override val notificationSettings: StateFlow<NotificationSettingsState> =
    _notificationSettings.asStateFlow()

  private fun recomputeEffectiveConnectionStatus() {
    val hasBtConnected = _devices.value.any {
      it.connectionState == BluetoothConnectionState.CONNECTED
    } && _isBluetoothEnabled.value
    val decision = router.resolveRoute(
      mode = _connectionMode.value,
      isInternetReachable = _isInternetSimulatedOnline.value,
      isBluetoothDeviceConnected = hasBtConnected,
    )
    _connectionStatus.value = when {
      _connectionMode.value == ConnectionMode.AUTOMATIC &&
        _isInternetSimulatedOnline.value -> ConnectionStatus.AUTOMATIC
      else -> decision.effectiveStatus
    }
  }

  override suspend fun login(
    username: String,
    password: String,
    rememberMe: Boolean,
  ): Result<UserProfile> {
    delay(450)
    val validation = UsernameValidator.validate(username)
    if (!validation.isValid) {
      return Result.failure(IllegalArgumentException(validation.errorMessage ?: "Invalid username"))
    }
    if (password.length < 4) {
      return Result.failure(IllegalArgumentException("Password must be at least 4 characters"))
    }
    val updated = _currentUser.value.copy(username = validation.normalized)
    _currentUser.value = updated
    _isLoggedIn.value = true
    return Result.success(updated)
  }

  override suspend fun register(
    username: String,
    password: String,
    email: String?,
  ): Result<UserProfile> {
    delay(450)
    val validation = UsernameValidator.validate(username)
    if (!validation.isValid) {
      return Result.failure(IllegalArgumentException(validation.errorMessage ?: "Invalid username"))
    }
    if (!isUsernameAvailable(validation.normalized)) {
      return Result.failure(IllegalArgumentException("@${validation.normalized} is already taken"))
    }
    val created = _currentUser.value.copy(
      username = validation.normalized,
      displayName = validation.normalized.replaceFirstChar { it.uppercase() },
      email = email?.takeIf { it.isNotBlank() },
    )
    _currentUser.value = created
    _isLoggedIn.value = true
    return Result.success(created)
  }

  override suspend fun completeProfileSetup(
    displayName: String,
    username: String,
    about: String,
    avatarUri: String?,
  ): Result<UserProfile> {
    val validation = UsernameValidator.validate(username)
    if (!validation.isValid) {
      return Result.failure(IllegalArgumentException(validation.errorMessage ?: "Invalid username"))
    }
    val cleanName = displayName.trim().ifEmpty { validation.normalized }
    val updated = _currentUser.value.copy(
      displayName = cleanName,
      username = validation.normalized,
      about = about.trim().ifEmpty { "Hey there! I am using WhatsChat." },
      avatarUri = avatarUri ?: _currentUser.value.avatarUri,
    )
    _currentUser.value = updated
    _isLoggedIn.value = true
    return Result.success(updated)
  }

  override suspend fun logout() {
    _isLoggedIn.value = false
  }

  override fun isUsernameAvailable(username: String): Boolean {
    val normalized = UsernameValidator.normalize(username)
    if (normalized.length < 3) return false
    val existingOtherUsers = _users.value.map { it.username.lowercase() }
    val reservedMock = setOf("admin", "whatschat", "support")
    return normalized !in existingOtherUsers && normalized !in reservedMock
  }

  override fun getConversation(conversationId: String): Conversation? {
    return _conversations.value.find { it.id == conversationId }
  }

  override suspend fun openOrCreateDirectConversation(user: UserProfile): Conversation {
    val existing = _conversations.value.find { !it.isGroup && it.participantIds.contains(user.id) }
    if (existing != null) return existing
    val created = Conversation(
      id = "conv_${user.id}",
      title = user.displayName,
      username = user.username,
      isGroup = false,
      avatarResName = user.avatarResName,
      participantIds = listOf(_currentUser.value.id, user.id),
      lastMessagePreview = "Tap to start chatting via Internet or Bluetooth",
      lastMessageTime = "Now",
      lastMessageTimestamp = System.currentTimeMillis(),
      lastMessageDelivery = DeliveryStates.SENT,
      lastMessageTransport = MessageTransport.INTERNET,
      unreadCount = 0,
      isOnline = user.isOnline,
    )
    _conversations.update { listOf(created) + it }
    return created
  }

  override suspend fun createGroup(
    name: String,
    description: String,
    memberIds: List<String>,
    avatarUri: String?,
  ): Conversation {
    val groupId = "conv_group_${UUID.randomUUID().toString().take(8)}"
    val handle = UsernameValidator.normalize(name).ifEmpty { "group_chat" }
    val allMembers = (listOf(_currentUser.value.id) + memberIds).distinct()
    val created = Conversation(
      id = groupId,
      title = name.trim(),
      username = handle,
      isGroup = true,
      description = description.trim().ifEmpty { "WhatsChat group conversation" },
      avatarUri = avatarUri,
      participantIds = allMembers,
      adminIds = listOf(_currentUser.value.id),
      lastMessagePreview = "Group created · ${allMembers.size} members",
      lastMessageTime = "Now",
      lastMessageTimestamp = System.currentTimeMillis(),
      lastMessageDelivery = DeliveryStates.SENT,
      lastMessageTransport = MessageTransport.INTERNET,
      unreadCount = 0,
      isOnline = true,
      createdByText = "Created by ${_currentUser.value.displayName}, Today",
    )
    val systemWelcome = ChatMessage(
      messageId = "msg_${UUID.randomUUID().toString().take(8)}",
      conversationId = groupId,
      senderId = _currentUser.value.id,
      receiverId = null,
      content = "Created group \"${created.title}\" with ${allMembers.size} participants.",
      messageType = MessageTypes.TEXT,
      transport = MessageTransport.INTERNET,
      timestamp = System.currentTimeMillis(),
      deliveryStatus = DeliveryStates.DELIVERED,
      senderName = _currentUser.value.displayName,
      senderAvatarResName = _currentUser.value.avatarResName,
      formattedTime = formatTime(System.currentTimeMillis()),
    )
    _messagesByConversation.update { it + (groupId to listOf(systemWelcome)) }
    _conversations.update { listOf(created) + it }
    return created
  }

  override suspend fun sendMessage(
    conversationId: String,
    content: String,
    messageType: String,
    attachmentName: String?,
    attachmentSize: String?,
    mediaResName: String?,
    mediaUri: String?,
    durationSeconds: Int,
    stickerEmoji: String?,
    gifLabel: String?,
    replyToMessage: ChatMessage?,
  ) {
    val hasBtConnected = _devices.value.any {
      it.connectionState == BluetoothConnectionState.CONNECTED
    } && _isBluetoothEnabled.value

    val decision = router.resolveRoute(
      mode = _connectionMode.value,
      isInternetReachable = _isInternetSimulatedOnline.value,
      isBluetoothDeviceConnected = hasBtConnected,
    )

    val timestamp = System.currentTimeMillis()
    val conv = getConversation(conversationId)
    val receiverId = if (conv != null && !conv.isGroup) {
      conv.participantIds.firstOrNull { it != _currentUser.value.id }
    } else {
      null
    }

    val message = ChatMessage(
      messageId = "msg_${UUID.randomUUID().toString().take(12)}",
      conversationId = conversationId,
      senderId = _currentUser.value.id,
      receiverId = receiverId,
      content = content,
      messageType = messageType,
      transport = decision.transport,
      timestamp = timestamp,
      deliveryStatus = decision.initialDeliveryState,
      senderName = _currentUser.value.displayName,
      senderAvatarResName = _currentUser.value.avatarResName,
      replyToMessageId = replyToMessage?.messageId,
      replyPreview = replyToMessage?.content?.take(48),
      attachmentName = attachmentName,
      attachmentSize = attachmentSize,
      mediaResName = mediaResName,
      mediaUri = mediaUri,
      durationSeconds = durationSeconds,
      stickerEmoji = stickerEmoji,
      gifLabel = gifLabel,
      formattedTime = formatTime(timestamp),
    )

    _messagesByConversation.update { currentMap ->
      val currentList = currentMap[conversationId].orEmpty()
      currentMap + (conversationId to (currentList + message))
    }

    val previewText = when (messageType) {
      MessageTypes.IMAGE -> if (content.isNotBlank()) "📷 $content" else "📷 Photo"
      MessageTypes.VIDEO -> if (content.isNotBlank()) "🎥 $content" else "🎥 Video"
      MessageTypes.FILE -> "📄 ${attachmentName ?: "Document"}"
      MessageTypes.AUDIO -> "🎤 Voice note (${formatDuration(durationSeconds)})"
      MessageTypes.STICKER -> "${stickerEmoji ?: "✨"} Sticker"
      MessageTypes.GIF -> "GIF · ${gifLabel ?: content}"
      else -> content
    }

    _conversations.update { list ->
      list.map { item ->
        if (item.id == conversationId) {
          item.copy(
            lastMessagePreview = previewText,
            lastMessageTime = message.formattedTime,
            lastMessageTimestamp = timestamp,
            lastMessageDelivery = decision.initialDeliveryState,
            lastMessageTransport = decision.transport,
            unreadCount = 0,
          )
        } else {
          item
        }
      }
    }
  }

  override suspend fun editMessage(conversationId: String, messageId: String, newContent: String) {
    val trimmed = newContent.trim()
    if (trimmed.isEmpty()) return
    _messagesByConversation.update { map ->
      val updated = map[conversationId].orEmpty().map { msg ->
        if (msg.messageId == messageId) msg.copy(content = trimmed, isEdited = true) else msg
      }
      map + (conversationId to updated)
    }
  }

  override suspend fun deleteMessage(conversationId: String, messageId: String) {
    _messagesByConversation.update { map ->
      val updated = map[conversationId].orEmpty().filterNot { it.messageId == messageId }
      map + (conversationId to updated)
    }
  }

  override suspend fun toggleReaction(conversationId: String, messageId: String, emoji: String) {
    _messagesByConversation.update { map ->
      val updated = map[conversationId].orEmpty().map { msg ->
        if (msg.messageId == messageId) {
          val nextReactions = if (emoji in msg.reactions) {
            msg.reactions - emoji
          } else {
            msg.reactions + emoji
          }
          msg.copy(reactions = nextReactions)
        } else {
          msg
        }
      }
      map + (conversationId to updated)
    }
  }

  override suspend fun markConversationRead(conversationId: String) {
    _conversations.update { list ->
      list.map { if (it.id == conversationId) it.copy(unreadCount = 0) else it }
    }
  }

  override suspend fun togglePinConversation(conversationId: String) {
    _conversations.update { list ->
      list.map { if (it.id == conversationId) it.copy(isPinned = !it.isPinned) else it }
    }
  }

  override suspend fun toggleMuteConversation(conversationId: String) {
    _conversations.update { list ->
      list.map { if (it.id == conversationId) it.copy(isMuted = !it.isMuted) else it }
    }
  }

  override suspend fun archiveConversation(conversationId: String) {
    _conversations.update { list ->
      list.map { if (it.id == conversationId) it.copy(isArchived = !it.isArchived) else it }
    }
  }

  override suspend fun deleteConversation(conversationId: String) {
    _conversations.update { list -> list.filterNot { it.id == conversationId } }
    _messagesByConversation.update { it - conversationId }
  }

  override suspend fun clearConversationMessages(conversationId: String) {
    _messagesByConversation.update { it + (conversationId to emptyList()) }
    _conversations.update { list ->
      list.map {
        if (it.id == conversationId) {
          it.copy(lastMessagePreview = "Chat cleared", unreadCount = 0)
        } else {
          it
        }
      }
    }
  }

  override suspend fun updateGroupInfo(conversationId: String, name: String, description: String) {
    _conversations.update { list ->
      list.map {
        if (it.id == conversationId) {
          it.copy(title = name.trim().ifEmpty { it.title }, description = description.trim())
        } else {
          it
        }
      }
    }
  }

  override suspend fun updateGroupAvatar(conversationId: String, avatarUri: String): Boolean {
    val conv = getConversation(conversationId) ?: return false
    if (_currentUser.value.id !in conv.adminIds) return false
    _conversations.update { list ->
      list.map {
        if (it.id == conversationId) it.copy(avatarUri = avatarUri) else it
      }
    }
    return true
  }

  override suspend fun updateGroupMemberTag(conversationId: String, userId: String, tag: String) {
    _conversations.update { list ->
      list.map { conv ->
        if (conv.id == conversationId) {
          val updatedTags = if (tag.isBlank()) {
            conv.memberTags - userId
          } else {
            conv.memberTags + (userId to tag.trim())
          }
          conv.copy(memberTags = updatedTags)
        } else {
          conv
        }
      }
    }
  }

  override suspend fun updateConversationSettings(
    conversationId: String,
    chatLock: Boolean?,
    restrictedChat: Boolean?,
    translateMessages: Boolean?,
    mediaVisibility: Boolean?,
    disappearingMessages: String?,
    isFavourite: Boolean?,
  ) {
    _conversations.update { list ->
      list.map { conv ->
        if (conv.id == conversationId) {
          conv.copy(
            chatLock = chatLock ?: conv.chatLock,
            restrictedChat = restrictedChat ?: conv.restrictedChat,
            translateMessages = translateMessages ?: conv.translateMessages,
            mediaVisibility = mediaVisibility ?: conv.mediaVisibility,
            disappearingMessages = disappearingMessages ?: conv.disappearingMessages,
            isFavourite = isFavourite ?: conv.isFavourite,
          )
        } else {
          conv
        }
      }
    }
  }

  override suspend fun addGroupMembers(conversationId: String, memberIds: List<String>) {
    _conversations.update { list ->
      list.map {
        if (it.id == conversationId) {
          it.copy(participantIds = (it.participantIds + memberIds).distinct())
        } else {
          it
        }
      }
    }
  }

  override suspend fun removeGroupMember(conversationId: String, userId: String) {
    _conversations.update { list ->
      list.map {
        if (it.id == conversationId) {
          it.copy(
            participantIds = it.participantIds.filterNot { id -> id == userId },
            adminIds = it.adminIds.filterNot { id -> id == userId },
          )
        } else {
          it
        }
      }
    }
  }

  override suspend fun toggleGroupAdmin(conversationId: String, userId: String) {
    _conversations.update { list ->
      list.map {
        if (it.id == conversationId) {
          val nextAdmins = if (userId in it.adminIds) {
            it.adminIds - userId
          } else {
            it.adminIds + userId
          }
          it.copy(adminIds = nextAdmins)
        } else {
          it
        }
      }
    }
  }

  override suspend fun leaveGroup(conversationId: String) {
    deleteConversation(conversationId)
  }

  override fun searchUsers(query: String): List<UserProfile> {
    val q = query.trim().removePrefix("@").lowercase()
    val nonBlocked = _users.value.filterNot { it.isBlocked }
    if (q.isEmpty()) return nonBlocked
    return nonBlocked.filter {
      it.username.lowercase().contains(q) || it.displayName.lowercase().contains(q)
    }
  }

  override fun getUserById(userId: String): UserProfile? {
    if (userId == _currentUser.value.id) return _currentUser.value
    return _users.value.find { it.id == userId }
  }

  override suspend fun toggleContact(userId: String) {
    _users.update { list ->
      list.map { if (it.id == userId) it.copy(isContact = !it.isContact) else it }
    }
  }

  override suspend fun blockUser(userId: String) {
    _users.update { list ->
      list.map { if (it.id == userId) it.copy(isBlocked = true, isContact = false) else it }
    }
    _blockedUsers.value = _users.value.filter { it.isBlocked }
  }

  override suspend fun unblockUser(userId: String) {
    _users.update { list ->
      list.map { if (it.id == userId) it.copy(isBlocked = false) else it }
    }
    _blockedUsers.value = _users.value.filter { it.isBlocked }
  }

  override suspend fun updateCurrentUserProfile(
    displayName: String,
    username: String,
    about: String,
    avatarUri: String?,
  ): Result<UserProfile> {
    val validation = UsernameValidator.validate(username)
    if (!validation.isValid) {
      return Result.failure(IllegalArgumentException(validation.errorMessage ?: "Invalid username"))
    }
    if (validation.normalized != _currentUser.value.username &&
      !isUsernameAvailable(validation.normalized)
    ) {
      return Result.failure(IllegalArgumentException("@${validation.normalized} is already taken"))
    }
    val updated = _currentUser.value.copy(
      displayName = displayName.trim().ifEmpty { _currentUser.value.displayName },
      username = validation.normalized,
      about = about.trim(),
      avatarUri = avatarUri ?: _currentUser.value.avatarUri,
    )
    _currentUser.value = updated
    return Result.success(updated)
  }

  override suspend fun updateStatusBubble(statusBubble: String) {
    val clean = statusBubble.trim().ifEmpty { "What's happening?" }
    _currentUser.update { it.copy(statusBubble = clean) }
  }

  override suspend fun setBluetoothEnabled(enabled: Boolean) {
    _isBluetoothEnabled.value = enabled
    if (!enabled) {
      _isScanning.value = false
      _devices.update { list ->
        list.map { it.copy(connectionState = BluetoothConnectionState.DISCONNECTED) }
      }
    }
    recomputeEffectiveConnectionStatus()
  }

  override suspend fun startScan() {
    if (!_isBluetoothEnabled.value) return
    _isScanning.value = true
  }

  override suspend fun stopScan() {
    _isScanning.value = false
  }

  override suspend fun connectToDevice(deviceId: String) {
    _activeConnectingDeviceId.value = deviceId
    updateDeviceState(deviceId, BluetoothConnectionState.CONNECTING)
    delay(700)
    if (_activeConnectingDeviceId.value != deviceId) return
    updateDeviceState(deviceId, BluetoothConnectionState.PAIRING)
    delay(700)
    if (_activeConnectingDeviceId.value != deviceId) return
    _devices.update { list ->
      list.map {
        if (it.id == deviceId) {
          it.copy(
            isPaired = true,
            connectionState = BluetoothConnectionState.CONNECTED,
            lastConnectedText = "Connected now",
          )
        } else {
          it
        }
      }
    }
    _activeConnectingDeviceId.value = null
    recomputeEffectiveConnectionStatus()
  }

  override suspend fun cancelConnection(deviceId: String) {
    if (_activeConnectingDeviceId.value == deviceId) {
      _activeConnectingDeviceId.value = null
    }
    updateDeviceState(deviceId, BluetoothConnectionState.DISCONNECTED)
    recomputeEffectiveConnectionStatus()
  }

  override suspend fun disconnectDevice(deviceId: String) {
    _devices.update { list ->
      list.map {
        if (it.id == deviceId) {
          it.copy(
            connectionState = BluetoothConnectionState.DISCONNECTED,
            lastConnectedText = "Just now",
          )
        } else {
          it
        }
      }
    }
    recomputeEffectiveConnectionStatus()
  }

  override suspend fun forgetDevice(deviceId: String) {
    _devices.update { list ->
      list.map {
        if (it.id == deviceId) {
          it.copy(
            isPaired = false,
            connectionState = BluetoothConnectionState.DISCONNECTED,
            lastConnectedText = "Never",
          )
        } else {
          it
        }
      }
    }
    recomputeEffectiveConnectionStatus()
  }

  private fun updateDeviceState(deviceId: String, state: BluetoothConnectionState) {
    _devices.update { list ->
      list.map { if (it.id == deviceId) it.copy(connectionState = state) else it }
    }
  }

  override suspend fun setConnectionMode(mode: ConnectionMode) {
    _connectionMode.value = mode
    recomputeEffectiveConnectionStatus()
  }

  override suspend fun setInternetSimulatedOnline(online: Boolean) {
    _isInternetSimulatedOnline.value = online
    recomputeEffectiveConnectionStatus()
  }

  override suspend fun updatePrivacySettings(settings: PrivacySettingsState) {
    _privacySettings.value = settings
  }

  override suspend fun updateNotificationSettings(settings: NotificationSettingsState) {
    _notificationSettings.value = settings
  }

  private fun formatTime(timestampMillis: Long): String {
    return SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(timestampMillis))
  }

  private fun formatDuration(seconds: Int): String {
    val safe = seconds.coerceAtLeast(1)
    return "%d:%02d".format(safe / 60, safe % 60)
  }
}
