package com.example.hoot_net.data.local

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName="vpn_configs")
data class VPNConfigEntity(
  @PrimaryKey val region: String,
  @Embedded(prefix = "iface_") val wgInterface : WGInterface,
  @Embedded(prefix = "peer_") val peer: WGPeer
)

data class WGInterface(
  val privateKey: String,
  val address: String,
  val dns: String
)

data class WGPeer(
  val publicKey: String,
  val endpoint: String,
  val allowedIPs: String,
  val persistentKeepalive: Int
)
