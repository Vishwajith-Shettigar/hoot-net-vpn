package com.example.hoot_net.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import javax.inject.Singleton

@Dao
interface VPNConfigDao {
  @Insert
  fun insertConfig(vpnConfigEntity: VPNConfigEntity)

  @Query("SELECT * FROM vpn_configs WHERE  region = :regionName")
  fun getConfig(regionName: String): VPNConfigEntity?
}