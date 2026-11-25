package com.example.data.remote.websocket

import com.example.domain.models.ChatMessage
import kotlinx.coroutines.flow.Flow

/**
 * WebSocket client contract prepared for real-time Internet messaging
 * against the self-hosted Node.js WebSocket server.
 */
enum class WebSocketStatus {
  DISCONNECTED,
  CONNECTING,
  CONNECTED,
  RECONNECTING,
}

interface WhatsChatWebSocketClient {
  val status: Flow<WebSocketStatus>
  val incomingMessages: Flow<ChatMessage>

  suspend fun connect(endpointUrl: String, authToken: String)
  suspend fun disconnect()
  suspend fun sendMessageFrame(message: ChatMessage): Boolean
  suspend fun sendTypingIndicator(conversationId: String, isTyping: Boolean)
  suspend fun sendReadAck(conversationId: String, messageId: String)
}
