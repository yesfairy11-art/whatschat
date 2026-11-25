package com.example.navigation

object WhatsChatRoutes {
  const val SPLASH = "splash"
  const val WELCOME = "welcome"
  const val LOGIN = "login"
  const val REGISTRATION = "registration"
  const val PROFILE_SETUP = "profile_setup"
  const val MAIN_CHATS = "main_chats"
  const val INDIVIDUAL_CHAT = "individual_chat/{conversationId}"
  const val CREATE_GROUP = "create_group"
  const val GROUP_INFO = "group_info/{conversationId}"
  const val GROUP_VIDEO_CALL = "group_video_call/{conversationId}"
  const val VOICE_CALL = "voice_call/{conversationId}"
  const val MEDIA_SHARING = "media_sharing/{conversationId}"
  const val SEARCH_USERS = "search_users"
  const val CALLS_TAB = "calls_tab"
  const val CONTACTS_TAB = "contacts_tab"
  const val SETTINGS = "settings"
  const val PRIVACY_SETTINGS = "privacy_settings"
  const val BLOCKED_USERS = "blocked_users"
  const val NOTIFICATIONS = "notifications"
  const val PROFILE = "profile/{userId}"
  const val NEARBY_DEVICES = "nearby_devices"
  const val BLUETOOTH_CONNECTION = "bluetooth_connection/{deviceId}"
  const val BLUETOOTH_DEVICE_MANAGEMENT = "bluetooth_device_management"
  const val CONNECTION_PREFERENCES = "connection_preferences"

  fun individualChat(conversationId: String) = "individual_chat/$conversationId"
  fun createGroup(preselectedIds: List<String> = emptyList()): String {
    return if (preselectedIds.isEmpty()) CREATE_GROUP else "$CREATE_GROUP?preselected=${preselectedIds.joinToString(",")}"
  }
  fun groupInfo(conversationId: String) = "group_info/$conversationId"
  fun groupVideoCall(conversationId: String) = "group_video_call/$conversationId"
  fun voiceCall(conversationId: String) = "voice_call/$conversationId"
  fun mediaSharing(conversationId: String) = "media_sharing/$conversationId"
  fun profile(userId: String) = "profile/$userId"
  fun bluetoothConnection(deviceId: String) = "bluetooth_connection/$deviceId"
}
