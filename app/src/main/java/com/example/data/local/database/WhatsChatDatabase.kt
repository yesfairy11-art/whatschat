package com.example.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.local.dao.AttachmentDao
import com.example.data.local.dao.BluetoothDeviceDao
import com.example.data.local.dao.ConnectionRecordDao
import com.example.data.local.dao.ConversationDao
import com.example.data.local.dao.ConversationMemberDao
import com.example.data.local.dao.DeliveryStateDao
import com.example.data.local.dao.MessageDao
import com.example.data.local.dao.SyncStateDao
import com.example.data.local.dao.UserDao
import com.example.data.local.entities.AttachmentEntity
import com.example.data.local.entities.BluetoothDeviceEntity
import com.example.data.local.entities.ConnectionRecordEntity
import com.example.data.local.entities.ConversationEntity
import com.example.data.local.entities.ConversationMemberEntity
import com.example.data.local.entities.DeliveryStateEntity
import com.example.data.local.entities.MessageEntity
import com.example.data.local.entities.SyncStateEntity
import com.example.data.local.entities.UserEntity

@Database(
  entities = [
    UserEntity::class,
    ConversationEntity::class,
    ConversationMemberEntity::class,
    MessageEntity::class,
    AttachmentEntity::class,
    BluetoothDeviceEntity::class,
    ConnectionRecordEntity::class,
    DeliveryStateEntity::class,
    SyncStateEntity::class,
  ],
  version = 1,
  exportSchema = false,
)
abstract class WhatsChatDatabase : RoomDatabase() {
  abstract fun userDao(): UserDao
  abstract fun conversationDao(): ConversationDao
  abstract fun conversationMemberDao(): ConversationMemberDao
  abstract fun messageDao(): MessageDao
  abstract fun attachmentDao(): AttachmentDao
  abstract fun bluetoothDeviceDao(): BluetoothDeviceDao
  abstract fun connectionRecordDao(): ConnectionRecordDao
  abstract fun deliveryStateDao(): DeliveryStateDao
  abstract fun syncStateDao(): SyncStateDao

  companion object {
    @Volatile
    private var INSTANCE: WhatsChatDatabase? = null

    fun getInstance(context: Context): WhatsChatDatabase {
      return INSTANCE ?: synchronized(this) {
        INSTANCE ?: Room.databaseBuilder(
          context.applicationContext,
          WhatsChatDatabase::class.java,
          "whatschat_local.db",
        ).build().also { INSTANCE = it }
      }
    }
  }
}
