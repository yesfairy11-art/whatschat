package com.example.data.bluetooth.transport

import com.example.domain.models.ChatMessage
import kotlinx.coroutines.flow.Flow

/**
 * Bluetooth RFCOMM direct messaging transport contract.
 * Uses the exact same ChatMessage model and conversationId as Internet transport.
 */
interface BluetoothMessageTransport {
  val incomingBluetoothMessages: Flow<ChatMessage>

  suspend fun sendDirectMessage(deviceId: String, message: ChatMessage): Result<Unit>
  suspend fun sendDeliveryAck(deviceId: String, messageId: String)
}
