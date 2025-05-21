package com.example.hoot_net.data.remote

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

data class WGConfig(
  val `interface`: WGInterface,
  val peer: WGPeer
)
