package com.example.sync

import com.example.domain.models.ChatMessage
import com.example.domain.models.DeliveryStates

/**
 * Coordinates idempotent synchronization between local Room storage,
 * direct Bluetooth deliveries, and the future Node.js/PostgreSQL backend.
 */
interface MessageSyncManager {
  suspend fun enqueueForSync(message: ChatMessage)
  suspend fun syncPendingMessages(): Int
}

interface DeliveryManager {
  suspend fun scheduleRetry(messageId: String)
  suspend fun markDelivered(messageId: String, transport: String)
}

class ConflictResolver {
  /**
   * Deduplicates messages received across both Bluetooth and Internet transports
   * using their globally unique messageId while preferring the stronger delivery state.
   */
  fun resolveDuplicate(existing: ChatMessage, incoming: ChatMessage): ChatMessage {
    val rank = mapOf(
      DeliveryStates.PENDING to 0,
      DeliveryStates.SENDING to 1,
      DeliveryStates.FAILED to 1,
      DeliveryStates.SENT to 2,
      DeliveryStates.DELIVERED to 3,
    )
    val existingRank = rank[existing.deliveryStatus] ?: 0
    val incomingRank = rank[incoming.deliveryStatus] ?: 0
    return if (incomingRank >= existingRank) incoming else existing
  }
}
