package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entities.AttachmentEntity
import com.example.data.local.entities.BluetoothDeviceEntity
import com.example.data.local.entities.ConnectionRecordEntity
import com.example.data.local.entities.ConversationEntity
import com.example.data.local.entities.ConversationMemberEntity
import com.example.data.local.entities.DeliveryStateEntity
import com.example.data.local.entities.MessageEntity
import com.example.data.local.entities.SyncStateEntity
import com.example.data.local.entities.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
  @Query("SELECT * FROM users ORDER BY displayName ASC")
  fun observeUsers(): Flow<List<UserEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun upsertUser(user: UserEntity)
}

@Dao
interface ConversationDao {
  @Query("SELECT * FROM conversations WHERE isArchived = 0 ORDER BY isPinned DESC, lastMessageTimestamp DESC")
  fun observeConversations(): Flow<List<ConversationEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun upsertConversation(conversation: ConversationEntity)

  @Query("DELETE FROM conversations WHERE conversationId = :conversationId")
  suspend fun deleteConversation(conversationId: String)
}

@Dao
interface ConversationMemberDao {
  @Query("SELECT * FROM conversation_members WHERE conversationId = :conversationId")
  fun observeMembers(conversationId: String): Flow<List<ConversationMemberEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun upsertMembers(members: List<ConversationMemberEntity>)
}

@Dao
interface MessageDao {
  @Query("SELECT * FROM messages WHERE conversationId = :conversationId ORDER BY timestamp ASC")
  fun observeMessages(conversationId: String): Flow<List<MessageEntity>>

  @Query("SELECT * FROM messages WHERE deliveryStatus = 'PENDING' ORDER BY timestamp ASC")
  fun observePendingMessages(): Flow<List<MessageEntity>>

  @Insert(onConflict = OnConflictStrategy.IGNORE)
  suspend fun insertMessageIdempotent(message: MessageEntity): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun upsertMessage(message: MessageEntity)

  @Query("DELETE FROM messages WHERE messageId = :messageId")
  suspend fun deleteMessage(messageId: String)
}

@Dao
interface AttachmentDao {
  @Query("SELECT * FROM attachments WHERE messageId = :messageId")
  fun observeAttachments(messageId: String): Flow<List<AttachmentEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun upsertAttachment(attachment: AttachmentEntity)
}

@Dao
interface BluetoothDeviceDao {
  @Query("SELECT * FROM bluetooth_devices ORDER BY lastConnectedAt DESC")
  fun observeDevices(): Flow<List<BluetoothDeviceEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun upsertDevice(device: BluetoothDeviceEntity)

  @Query("DELETE FROM bluetooth_devices WHERE deviceId = :deviceId")
  suspend fun deleteDevice(deviceId: String)
}

@Dao
interface ConnectionRecordDao {
  @Query("SELECT * FROM connection_records ORDER BY connectedAt DESC LIMIT 50")
  fun observeRecords(): Flow<List<ConnectionRecordEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertRecord(record: ConnectionRecordEntity)
}

@Dao
interface DeliveryStateDao {
  @Query("SELECT * FROM delivery_states WHERE messageId = :messageId")
  fun observeDeliveryState(messageId: String): Flow<DeliveryStateEntity?>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun upsertDeliveryState(state: DeliveryStateEntity)
}

@Dao
interface SyncStateDao {
  @Query("SELECT * FROM sync_states WHERE entityKey = :key")
  fun observeSyncState(key: String): Flow<SyncStateEntity?>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun upsertSyncState(state: SyncStateEntity)
}
