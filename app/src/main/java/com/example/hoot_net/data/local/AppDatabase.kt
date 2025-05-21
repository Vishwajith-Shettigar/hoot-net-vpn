package com.example.hoot_net.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities=[VPNConfigEntity::class],version=1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
  abstract fun vpnConfigDao(): VPNConfigDao
}