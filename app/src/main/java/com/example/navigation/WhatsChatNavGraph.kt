package com.example.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.domain.models.BluetoothConnectionState
import com.example.domain.models.VideoCallParticipant
import com.example.features.authentication.LoginScreen
import com.example.features.authentication.ProfileSetupScreen
import com.example.features.authentication.RegistrationScreen
import com.example.features.authentication.SplashScreen
import com.example.features.authentication.WelcomeScreen
import com.example.features.calls.CallsHubScreen
import com.example.features.calls.GroupVideoCallScreen
import com.example.features.calls.VoiceCallScreen
import com.example.features.chats.IndividualChatScreen
import com.example.features.chats.MainChatsScreen
import com.example.features.contacts.SearchUsersScreen
import com.example.features.groups.CreateGroupScreen
import com.example.features.groups.GroupInfoScreen
import com.example.features.media.MediaSharingScreen
import com.example.features.nearby.BluetoothConnectionScreen
import com.example.features.nearby.BluetoothDeviceManagementScreen
import com.example.features.nearby.ConnectionPreferencesScreen
import com.example.features.nearby.NearbyDevicesScreen
import com.example.features.profile.ProfileScreen
import com.example.features.settings.BlockedUsersScreen
import com.example.features.settings.NotificationsScreen
import com.example.features.settings.PrivacySettingsScreen
import com.example.features.settings.SettingsScreen
import com.example.ui.WhatsChatViewModel
import com.example.ui.components.MainNavDestination

@Composable
fun WhatsChatNavGraph(viewModel: WhatsChatViewModel) {
  val navController = rememberNavController()

  val isLoggedIn by viewModel.isLoggedIn.collectAsStateWithLifecycle()
  val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
  val conversations by viewModel.conversations.collectAsStateWithLifecycle()
  val messagesByConv by viewModel.messagesByConversation.collectAsStateWithLifecycle()
  val callHistory by viewModel.callHistory.collectAsStateWithLifecycle()
  val sampleMedia by viewModel.sampleMedia.collectAsStateWithLifecycle()
  val users by viewModel.users.collectAsStateWithLifecycle()
  val blockedUsers by viewModel.blockedUsers.collectAsStateWithLifecycle()
  val isBluetoothEnabled by viewModel.isBluetoothEnabled.collectAsStateWithLifecycle()
  val isScanning by viewModel.isScanning.collectAsStateWithLifecycle()
  val devices by viewModel.devices.collectAsStateWithLifecycle()
  val connectionMode by viewModel.connectionMode.collectAsStateWithLifecycle()
  val connectionStatus by viewModel.connectionStatus.collectAsStateWithLifecycle()
  val isInternetSimulatedOnline by viewModel.isInternetSimulatedOnline.collectAsStateWithLifecycle()
  val privacySettings by viewModel.privacySettings.collectAsStateWithLifecycle()
  val notificationSettings by viewModel.notificationSettings.collectAsStateWithLifecycle()

  fun navigateMainTab(destination: MainNavDestination) {
    val targetRoute = when (destination) {
      MainNavDestination.CHATS -> WhatsChatRoutes.MAIN_CHATS
      MainNavDestination.CALLS -> WhatsChatRoutes.CALLS_TAB
      MainNavDestination.CONTACTS -> WhatsChatRoutes.CONTACTS_TAB
      MainNavDestination.ACCOUNT -> WhatsChatRoutes.SETTINGS
    }
    navController.navigate(targetRoute) {
      popUpTo(WhatsChatRoutes.MAIN_CHATS) {
        saveState = true
      }
      launchSingleTop = true
      restoreState = true
    }
  }

  NavHost(
    navController = navController,
    startDestination = WhatsChatRoutes.SPLASH,
    enterTransition = { fadeIn(animationSpec = tween(220)) },
    exitTransition = { fadeOut(animationSpec = tween(220)) },
  ) {
    // Screen 01 — Splash
    composable(WhatsChatRoutes.SPLASH) {
      SplashScreen(
        isLoggedIn = isLoggedIn,
        onNavigateNext = { loggedIn ->
          val next = if (loggedIn) WhatsChatRoutes.MAIN_CHATS else WhatsChatRoutes.WELCOME
          navController.navigate(next) {
            popUpTo(WhatsChatRoutes.SPLASH) { inclusive = true }
          }
        },
      )
    }

    // Screen 02 — Welcome
    composable(WhatsChatRoutes.WELCOME) {
      WelcomeScreen(
        onCreateAccountClick = { navController.navigate(WhatsChatRoutes.REGISTRATION) },
        onLoginClick = { navController.navigate(WhatsChatRoutes.LOGIN) },
      )
    }

    // Screen 03 — Username Login
    composable(WhatsChatRoutes.LOGIN) {
      LoginScreen(
        onBackClick = { navController.popBackStack() },
        onLoginSubmit = { username, password, rememberMe, onDone, onError ->
          viewModel.performLogin(username, password, rememberMe, onDone, onError)
        },
        onLoginSuccess = {
          navController.navigate(WhatsChatRoutes.MAIN_CHATS) {
            popUpTo(navController.graph.findStartDestination().id) { inclusive = true }
          }
        },
        onCreateAccountClick = {
          navController.navigate(WhatsChatRoutes.REGISTRATION) {
            popUpTo(WhatsChatRoutes.LOGIN) { inclusive = true }
          }
        },
      )
    }

    // Screen 04 — Registration
    composable(WhatsChatRoutes.REGISTRATION) {
      RegistrationScreen(
        isUsernameAvailable = { viewModel.isUsernameAvailable(it) },
        onBackClick = { navController.popBackStack() },
        onRegisterSubmit = { username, password, email, onDone, onError ->
          viewModel.performRegistration(username, password, email, onDone, onError)
        },
        onRegistrationSuccess = {
          navController.navigate(WhatsChatRoutes.PROFILE_SETUP)
        },
        onLoginClick = {
          navController.navigate(WhatsChatRoutes.LOGIN) {
            popUpTo(WhatsChatRoutes.REGISTRATION) { inclusive = true }
          }
        },
      )
    }

    // Screen 05 — Profile Setup
    composable(WhatsChatRoutes.PROFILE_SETUP) {
      ProfileSetupScreen(
        currentUser = currentUser,
        onBackClick = { navController.popBackStack() },
        onSaveProfile = { displayName, username, about, avatarUri, onDone, onError ->
          viewModel.performProfileSetup(displayName, username, about, avatarUri, onDone, onError)
        },
        onContinueToChats = {
          navController.navigate(WhatsChatRoutes.MAIN_CHATS) {
            popUpTo(navController.graph.findStartDestination().id) { inclusive = true }
          }
        },
      )
    }

    // Screen 06 — Main Chats
    composable(WhatsChatRoutes.MAIN_CHATS) {
      val connectedBtName = devices.firstOrNull {
        it.connectionState == BluetoothConnectionState.CONNECTED
      }?.deviceName
      MainChatsScreen(
        currentUser = currentUser,
        conversations = conversations,
        callHistory = callHistory,
        connectionMode = connectionMode,
        connectionStatus = connectionStatus,
        connectedBtDeviceName = connectedBtName,
        onOpenConversation = { convId ->
          navController.navigate(WhatsChatRoutes.individualChat(convId))
        },
        onOpenVoiceCall = { convId ->
          navController.navigate(WhatsChatRoutes.voiceCall(convId))
        },
        onOpenVideoCall = { convId ->
          navController.navigate(WhatsChatRoutes.groupVideoCall(convId))
        },
        onOpenSearchUsers = { navController.navigate(WhatsChatRoutes.SEARCH_USERS) },
        onOpenCreateGroup = { navController.navigate(WhatsChatRoutes.createGroup()) },
        onOpenNearbyDevices = { navController.navigate(WhatsChatRoutes.NEARBY_DEVICES) },
        onOpenConnectionPreferences = {
          navController.navigate(WhatsChatRoutes.CONNECTION_PREFERENCES)
        },
        onOpenProfile = {
          navController.navigate(WhatsChatRoutes.profile(currentUser.id))
        },
        onNavigateBottom = ::navigateMainTab,
        onTogglePin = viewModel::pinConversation,
        onToggleMute = viewModel::muteConversation,
        onArchiveConversation = viewModel::archiveConversationAction,
        onDeleteConversation = viewModel::deleteConversationAction,
      )
    }

    // Calls Bottom Navigation Tab
    composable(WhatsChatRoutes.CALLS_TAB) {
      CallsHubScreen(
        currentUser = currentUser,
        callHistory = callHistory,
        onNavigateBottom = ::navigateMainTab,
        onStartVoiceCall = { convId -> navController.navigate(WhatsChatRoutes.voiceCall(convId)) },
        onStartVideoCall = { convId -> navController.navigate(WhatsChatRoutes.groupVideoCall(convId)) },
        onOpenSearchUsers = { navController.navigate(WhatsChatRoutes.SEARCH_USERS) },
      )
    }

    // Screen 07 — Individual Chat (Unified Internet + Bluetooth)
    composable(
      route = WhatsChatRoutes.INDIVIDUAL_CHAT,
      arguments = listOf(navArgument("conversationId") { type = NavType.StringType }),
    ) { backStackEntry ->
      val convId = backStackEntry.arguments?.getString("conversationId") ?: return@composable
      val conversation = conversations.find { it.id == convId }
      if (conversation == null) {
        navController.popBackStack()
        return@composable
      }
      val messages = messagesByConv[convId].orEmpty()

      IndividualChatScreen(
        conversation = conversation,
        messages = messages,
        currentUserId = currentUser.id,
        connectionStatus = connectionStatus,
        isInternetOnline = isInternetSimulatedOnline,
        onBackClick = { navController.popBackStack() },
        onOpenHeaderProfileOrGroup = {
          if (conversation.isGroup) {
            navController.navigate(WhatsChatRoutes.groupInfo(convId))
          } else {
            val peerId = conversation.participantIds.firstOrNull { it != currentUser.id }
              ?: currentUser.id
            navController.navigate(WhatsChatRoutes.profile(peerId))
          }
        },
        onOpenVoiceCall = { navController.navigate(WhatsChatRoutes.voiceCall(convId)) },
        onOpenVideoCall = { navController.navigate(WhatsChatRoutes.groupVideoCall(convId)) },
        onOpenMediaSharing = {
          navController.navigate(WhatsChatRoutes.mediaSharing(convId))
        },
        onOpenConnectionPreferences = {
          navController.navigate(WhatsChatRoutes.CONNECTION_PREFERENCES)
        },
        onToggleSimulatedInternet = viewModel::toggleSimulatedInternet,
        onSendMessage = { text, replyTo ->
          viewModel.sendTextMessage(convId, text, replyTo)
        },
        onSendPickedMedia = { uri, isVideo, caption ->
          viewModel.sendPickedMediaMessage(convId, uri, isVideo, caption)
        },
        onSendVoiceNote = { duration ->
          viewModel.sendVoiceNote(convId, duration)
        },
        onSendSticker = { emoji, label ->
          viewModel.sendSticker(convId, emoji, label)
        },
        onSendGif = { gifLabel, badgeEmoji ->
          viewModel.sendGif(convId, gifLabel, badgeEmoji)
        },
        onEditMessage = { messageId, newText ->
          viewModel.editChatMessage(convId, messageId, newText)
        },
        onDeleteMessage = { messageId ->
          viewModel.deleteChatMessage(convId, messageId)
        },
        onReactToMessage = { messageId, emoji ->
          viewModel.reactToMessage(convId, messageId, emoji)
        },
        onMarkRead = { viewModel.markRead(convId) },
      )
    }

    // Screen 08 — Create Group
    composable(
      route = "${WhatsChatRoutes.CREATE_GROUP}?preselected={preselected}",
      arguments = listOf(
        navArgument("preselected") {
          type = NavType.StringType
          defaultValue = ""
        },
      ),
    ) { backStackEntry ->
      val preselectedRaw = backStackEntry.arguments?.getString("preselected").orEmpty()
      val preselectedIds = preselectedRaw.split(",").filter { it.isNotBlank() }

      CreateGroupScreen(
        availableUsers = users,
        initialSelectedIds = preselectedIds,
        onBackClick = { navController.popBackStack() },
        onCreateGroup = { name, description, memberIds ->
          viewModel.createNewGroup(name, description, memberIds) { createdId ->
            navController.navigate(WhatsChatRoutes.individualChat(createdId)) {
              popUpTo(WhatsChatRoutes.MAIN_CHATS)
            }
          }
        },
      )
    }

    // Screen 09 — Group Information
    composable(
      route = WhatsChatRoutes.GROUP_INFO,
      arguments = listOf(navArgument("conversationId") { type = NavType.StringType }),
    ) { backStackEntry ->
      val convId = backStackEntry.arguments?.getString("conversationId") ?: return@composable
      val conversation = conversations.find { it.id == convId }
      if (conversation == null) {
        navController.popBackStack()
        return@composable
      }
      val memberProfiles = conversation.participantIds.mapNotNull { id ->
        if (id == currentUser.id) currentUser else users.find { it.id == id }
      }
      val groupMessages = messagesByConv[convId].orEmpty()

      GroupInfoScreen(
        conversation = conversation,
        messages = groupMessages,
        members = memberProfiles,
        allUsers = users,
        currentUserId = currentUser.id,
        onBackClick = { navController.popBackStack() },
        onVoiceCallClick = { navController.navigate(WhatsChatRoutes.voiceCall(convId)) },
        onVideoCallClick = { navController.navigate(WhatsChatRoutes.groupVideoCall(convId)) },
        onToggleMute = { viewModel.muteConversation(convId) },
        onOpenMediaSharing = { navController.navigate(WhatsChatRoutes.mediaSharing(convId)) },
        onUpdateGroupInfo = { name, desc -> viewModel.saveGroupInfo(convId, name, desc) },
        onChangeGroupAvatarByAdmin = { uri -> viewModel.changeGroupAvatarByAdmin(convId, uri) },
        onSaveMemberTag = { uid, tag -> viewModel.saveGroupMemberTag(convId, uid, tag) },
        onAddMembers = { ids -> viewModel.addMembersToGroup(convId, ids) },
        onToggleAdmin = { uid -> viewModel.toggleAdminStatus(convId, uid) },
        onRemoveMember = { uid -> viewModel.removeMemberFromGroup(convId, uid) },
        onOpenMemberProfile = { uid -> navController.navigate(WhatsChatRoutes.profile(uid)) },
        onUpdateChatSettings = { lock, restricted, translate, visibility, disappearing, fav ->
          viewModel.updateChatSettings(
            conversationId = convId,
            chatLock = lock,
            restrictedChat = restricted,
            translateMessages = translate,
            mediaVisibility = visibility,
            disappearingMessages = disappearing,
            isFavourite = fav,
          )
        },
        onCreateSimilarGroup = { memberIds ->
          navController.navigate(WhatsChatRoutes.createGroup(memberIds))
        },
        onClearChat = { viewModel.clearChatHistory(convId) },
        onLeaveGroup = {
          viewModel.leaveGroupAction(convId) {
            navController.navigate(WhatsChatRoutes.MAIN_CHATS) {
              popUpTo(WhatsChatRoutes.MAIN_CHATS) { inclusive = true }
            }
          }
        },
      )
    }

    // Screen 10 — Group Video Call (Max 4 participants)
    composable(
      route = WhatsChatRoutes.GROUP_VIDEO_CALL,
      arguments = listOf(navArgument("conversationId") { type = NavType.StringType }),
    ) { backStackEntry ->
      val convId = backStackEntry.arguments?.getString("conversationId") ?: ""
      val conv = conversations.find { it.id == convId }
      val participants = buildList {
        add(
          VideoCallParticipant(
            id = currentUser.id,
            name = currentUser.displayName,
            username = currentUser.username,
            avatarResName = currentUser.avatarResName,
            isSpeaking = false,
          ),
        )
        val peerIds = conv?.participantIds?.filter { it != currentUser.id }.orEmpty()
        peerIds.take(3).forEachIndexed { idx, pid ->
          val u = users.find { it.id == pid }
          if (u != null) {
            add(
              VideoCallParticipant(
                id = u.id,
                name = u.displayName,
                username = u.username,
                avatarResName = u.avatarResName,
                isMuted = idx == 1,
                isSpeaking = idx == 0,
              ),
            )
          }
        }
      }

      GroupVideoCallScreen(
        callTitle = conv?.title ?: "Group Video Call",
        participants = participants,
        onEndCall = { navController.popBackStack() },
      )
    }

    // Screen 11 — Voice Call
    composable(
      route = WhatsChatRoutes.VOICE_CALL,
      arguments = listOf(navArgument("conversationId") { type = NavType.StringType }),
    ) { backStackEntry ->
      val convId = backStackEntry.arguments?.getString("conversationId") ?: ""
      val conv = conversations.find { it.id == convId }
      VoiceCallScreen(
        contactName = conv?.title ?: "Sarah Johnson",
        username = conv?.username ?: "sarah_j",
        avatarResName = conv?.avatarResName,
        isGroup = conv?.isGroup == true,
        onEndCall = { navController.popBackStack() },
      )
    }

    // Screen 12 — Media Sharing
    composable(
      route = WhatsChatRoutes.MEDIA_SHARING,
      arguments = listOf(navArgument("conversationId") { type = NavType.StringType }),
    ) { backStackEntry ->
      val convId = backStackEntry.arguments?.getString("conversationId") ?: ""
      MediaSharingScreen(
        mediaItems = sampleMedia,
        onCloseClick = { navController.popBackStack() },
        onSendSelected = { selectedItems, caption ->
          viewModel.sendSampleMediaItems(convId, selectedItems, caption)
          navController.popBackStack()
        },
      )
    }

    // Screen 13 — Search Users (Standalone from FAB/Menu)
    composable(WhatsChatRoutes.SEARCH_USERS) {
      SearchUsersScreen(
        currentUser = currentUser,
        users = users,
        showBottomBar = false,
        onBackClick = { navController.popBackStack() },
        onNavigateBottom = ::navigateMainTab,
        onToggleContact = viewModel::toggleUserContact,
        onMessageUser = { user ->
          viewModel.openChatWithUser(user) { convId ->
            navController.navigate(WhatsChatRoutes.individualChat(convId))
          }
        },
        onOpenUserProfile = { userId ->
          navController.navigate(WhatsChatRoutes.profile(userId))
        },
      )
    }

    // Screen 13 (Contacts Bottom Tab variant)
    composable(WhatsChatRoutes.CONTACTS_TAB) {
      SearchUsersScreen(
        currentUser = currentUser,
        users = users,
        showBottomBar = true,
        onBackClick = null,
        onNavigateBottom = ::navigateMainTab,
        onToggleContact = viewModel::toggleUserContact,
        onMessageUser = { user ->
          viewModel.openChatWithUser(user) { convId ->
            navController.navigate(WhatsChatRoutes.individualChat(convId))
          }
        },
        onOpenUserProfile = { userId ->
          navController.navigate(WhatsChatRoutes.profile(userId))
        },
      )
    }

    // Screen 14 — Account Hub (formerly Settings tab)
    composable(WhatsChatRoutes.SETTINGS) {
      SettingsScreen(
        currentUser = currentUser,
        connectionMode = connectionMode,
        onNavigateBottom = ::navigateMainTab,
        onOpenProfile = { navController.navigate(WhatsChatRoutes.profile(currentUser.id)) },
        onSaveStatusBubble = viewModel::saveStatusBubble,
        onOpenPrivacy = { navController.navigate(WhatsChatRoutes.PRIVACY_SETTINGS) },
        onOpenNotifications = { navController.navigate(WhatsChatRoutes.NOTIFICATIONS) },
        onOpenConnectionPreferences = {
          navController.navigate(WhatsChatRoutes.CONNECTION_PREFERENCES)
        },
        onOpenBluetoothDevices = {
          navController.navigate(WhatsChatRoutes.BLUETOOTH_DEVICE_MANAGEMENT)
        },
        onOpenSearchUsers = { navController.navigate(WhatsChatRoutes.SEARCH_USERS) },
        onLogoutConfirmed = {
          viewModel.performLogout {
            navController.navigate(WhatsChatRoutes.WELCOME) {
              popUpTo(navController.graph.findStartDestination().id) { inclusive = true }
            }
          }
        },
      )
    }

    // Screen 15 — Privacy Settings
    composable(WhatsChatRoutes.PRIVACY_SETTINGS) {
      PrivacySettingsScreen(
        privacyState = privacySettings,
        blockedCount = blockedUsers.size,
        onBackClick = { navController.popBackStack() },
        onUpdatePrivacy = viewModel::savePrivacySettings,
        onOpenBlockedUsers = { navController.navigate(WhatsChatRoutes.BLOCKED_USERS) },
      )
    }

    // Screen 16 — Blocked Users
    composable(WhatsChatRoutes.BLOCKED_USERS) {
      BlockedUsersScreen(
        blockedUsers = blockedUsers,
        allUsers = users,
        onBackClick = { navController.popBackStack() },
        onUnblockUser = viewModel::unblockUserAction,
        onBlockUser = viewModel::blockUserAction,
        onOpenUserProfile = { userId -> navController.navigate(WhatsChatRoutes.profile(userId)) },
      )
    }

    // Screen 17 — Notifications
    composable(WhatsChatRoutes.NOTIFICATIONS) {
      NotificationsScreen(
        notificationState = notificationSettings,
        onBackClick = { navController.popBackStack() },
        onUpdateNotifications = viewModel::saveNotificationSettings,
      )
    }

    // Screen 18 — Profile Page (My Profile & Other-User Account View)
    composable(
      route = WhatsChatRoutes.PROFILE,
      arguments = listOf(navArgument("userId") { type = NavType.StringType }),
    ) { backStackEntry ->
      val userId = backStackEntry.arguments?.getString("userId") ?: currentUser.id
      val targetUser = if (userId == currentUser.id) {
        currentUser
      } else {
        users.find { it.id == userId } ?: currentUser
      }
      val isMe = targetUser.id == currentUser.id
      val directConv = if (isMe) {
        null
      } else {
        conversations.find { !it.isGroup && targetUser.id in it.participantIds }
      }
      val sharedMedia = directConv?.let { messagesByConv[it.id] }.orEmpty()

      ProfileScreen(
        user = targetUser,
        isCurrentUser = isMe,
        conversation = directConv,
        sharedMediaMessages = sharedMedia,
        isUsernameAvailable = { viewModel.isUsernameAvailable(it) },
        onBackClick = { navController.popBackStack() },
        onUpdateProfile = { displayName, username, about, avatarUri, onDone, onError ->
          viewModel.performUpdateProfile(displayName, username, about, avatarUri, onDone, onError)
        },
        onOpenPrivacySettings = { navController.navigate(WhatsChatRoutes.PRIVACY_SETTINGS) },
        onMessageUser = {
          viewModel.openChatWithUser(targetUser) { convId ->
            navController.navigate(WhatsChatRoutes.individualChat(convId))
          }
        },
        onOpenVoiceCall = {
          viewModel.openChatWithUser(targetUser) { convId ->
            navController.navigate(WhatsChatRoutes.voiceCall(convId))
          }
        },
        onOpenVideoCall = {
          viewModel.openChatWithUser(targetUser) { convId ->
            navController.navigate(WhatsChatRoutes.groupVideoCall(convId))
          }
        },
        onOpenMediaSharing = {
          viewModel.openChatWithUser(targetUser) { convId ->
            navController.navigate(WhatsChatRoutes.mediaSharing(convId))
          }
        },
        onToggleContact = { viewModel.toggleUserContact(targetUser.id) },
        onToggleBlock = {
          if (targetUser.isBlocked) {
            viewModel.unblockUserAction(targetUser.id)
          } else {
            viewModel.blockUserAction(targetUser.id)
          }
        },
        onUpdateChatSettings = { lock, restricted, translate, visibility, disappearing, fav ->
          directConv?.let { conv ->
            viewModel.updateChatSettings(
              conversationId = conv.id,
              chatLock = lock,
              restrictedChat = restricted,
              translateMessages = translate,
              mediaVisibility = visibility,
              disappearingMessages = disappearing,
              isFavourite = fav,
            )
          }
        },
      )
    }

    // Screen 19 — Nearby Devices
    composable(WhatsChatRoutes.NEARBY_DEVICES) {
      NearbyDevicesScreen(
        isBluetoothEnabled = isBluetoothEnabled,
        isScanning = isScanning,
        devices = devices,
        onBackClick = { navController.popBackStack() },
        onToggleBluetooth = viewModel::toggleBluetoothEnabled,
        onStartScan = viewModel::startBluetoothScan,
        onStopScan = viewModel::stopBluetoothScan,
        onConnectDevice = { deviceId ->
          navController.navigate(WhatsChatRoutes.bluetoothConnection(deviceId))
        },
        onManageDevicesClick = {
          navController.navigate(WhatsChatRoutes.BLUETOOTH_DEVICE_MANAGEMENT)
        },
      )
    }

    // Screen 20 — Bluetooth Connection
    composable(
      route = WhatsChatRoutes.BLUETOOTH_CONNECTION,
      arguments = listOf(navArgument("deviceId") { type = NavType.StringType }),
    ) { backStackEntry ->
      val deviceId = backStackEntry.arguments?.getString("deviceId") ?: ""
      val device = devices.find { it.id == deviceId } ?: devices.firstOrNull()
      if (device == null) {
        navController.popBackStack()
        return@composable
      }

      BluetoothConnectionScreen(
        device = device,
        onBackClick = { navController.popBackStack() },
        onStartConnect = { viewModel.initiateBluetoothConnection(device.id) },
        onCancelConnect = { viewModel.cancelBluetoothConnection(device.id) },
        onDisconnect = { viewModel.disconnectBluetoothDevice(device.id) },
        onOpenChatWithLinkedUser = {
          val linkedUser = users.find { it.username == device.linkedUsername } ?: users.first()
          viewModel.openChatWithUser(linkedUser) { convId ->
            navController.navigate(WhatsChatRoutes.individualChat(convId))
          }
        },
      )
    }

    // Screen 21 — Bluetooth Device Management
    composable(WhatsChatRoutes.BLUETOOTH_DEVICE_MANAGEMENT) {
      BluetoothDeviceManagementScreen(
        devices = devices,
        onBackClick = { navController.popBackStack() },
        onConnectDevice = { deviceId ->
          navController.navigate(WhatsChatRoutes.bluetoothConnection(deviceId))
        },
        onDisconnectDevice = viewModel::disconnectBluetoothDevice,
        onForgetDevice = viewModel::forgetBluetoothDevice,
        onScanNearbyClick = { navController.navigate(WhatsChatRoutes.NEARBY_DEVICES) },
      )
    }

    // Screen 22 — Connection Preferences
    composable(WhatsChatRoutes.CONNECTION_PREFERENCES) {
      ConnectionPreferencesScreen(
        currentMode = connectionMode,
        isInternetSimulatedOnline = isInternetSimulatedOnline,
        onBackClick = { navController.popBackStack() },
        onSaveMode = viewModel::saveConnectionMode,
        onToggleSimulatedInternet = viewModel::toggleSimulatedInternet,
      )
    }
  }
}
