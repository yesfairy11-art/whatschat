package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
  @PrimaryKey val userId: String,
  val displayName: String,
  val username: String,
  val about: String,
  val email: String?,
  val avatarUri: String?,
  val isOnline: Boolean,
  val lastSeenAt: Long,
  val isContact: Boolean,
  val isBlocked: Boolean,
)

@Entity(tableName = "conversations")
data class ConversationEntity(
  @PrimaryKey val conversationId: String,
  val title: String,
  val usernameOrHandle: String,
  val isGroup: Boolean,
  val description: String,
  val avatarUri: String?,
  val lastMessagePreview: String,
  val lastMessageTimestamp: Long,
  val unreadCount: Int,
  val isPinned: Boolean,
  val isMuted: Boolean,
  val isArchived: Boolean,
)

@Entity(
  tableName = "conversation_members",
  primaryKeys = ["conversationId", "userId"],
)
data class ConversationMemberEntity(
  val conversationId: String,
  val userId: String,
  val isAdmin: Boolean,
  val joinedAt: Long,
)

@Entity(tableName = "messages")
data class MessageEntity(
  @PrimaryKey val messageId: String,
  val conversationId: String,
  val senderId: String,
  val receiverId: String?,
  val content: String,
  val messageType: String,
  val transport: String,
  val timestamp: Long,
  val deliveryStatus: String,
  val replyToMessageId: String? = null,
  val isEdited: Boolean = false,
)

@Entity(tableName = "attachments")
data class AttachmentEntity(
  @PrimaryKey val attachmentId: String,
  val messageId: String,
  val fileName: String,
  val mimeType: String,
  val fileSizeBytes: Long,
  val localUri: String?,
  val remoteUrl: String?,
)

@Entity(tableName = "bluetooth_devices")
data class BluetoothDeviceEntity(
  @PrimaryKey val deviceId: String,
  val deviceName: String,
  val linkedUserId: String?,
  val isPaired: Boolean,
  val isAuthorized: Boolean,
  val lastConnectedAt: Long?,
)

@Entity(tableName = "connection_records")
data class ConnectionRecordEntity(
  @PrimaryKey val recordId: String,
  val transport: String,
  val targetId: String,
  val status: String,
  val connectedAt: Long,
  val disconnectedAt: Long?,
)

@Entity(tableName = "delivery_states")
data class DeliveryStateEntity(
  @PrimaryKey val messageId: String,
  val state: String,
  val transportUsed: String,
  val retryCount: Int,
  val updatedAt: Long,
)

@Entity(tableName = "sync_states")
data class SyncStateEntity(
  @PrimaryKey val entityKey: String,
  val lastSyncedVersion: Long,
  val lastSyncTimestamp: Long,
  val pendingCount: Int,
)
