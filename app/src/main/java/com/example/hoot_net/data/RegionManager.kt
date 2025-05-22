package com.example.hoot_net.data

import com.example.hoot_net.data.local.VPNConfigDao
import com.example.hoot_net.data.local.VPNConfigEntity
import com.example.hoot_net.data.remote.ApiClient
import com.example.hoot_net.data.remote.WGConfig
import com.example.hoot_net.data.remote.WGInterface
import com.example.hoot_net.data.remote.WGPeer
import javax.inject.Inject
import retrofit2.awaitResponse


class RegionManager @Inject constructor(
  val apiClient: ApiClient,
  val vpnConfigDao: VPNConfigDao
) {
  suspend fun getClientConfig(regionName: String, baseUrl: String): WGConfig? {
    return try {
      val localConfig = vpnConfigDao.getConfig(regionName)
      if (localConfig != null) {
        // Convert DB entity to WGConfig
         convertEntityToWGConfig(localConfig)
      }

      val client = apiClient.getClient(baseUrl)
      val remoteConfig = client.getNewClientConfig()
       remoteConfig.awaitResponse().body()
    } catch (e: Exception) {
      e.printStackTrace()
      null
    }
  }

  fun convertEntityToWGConfig(entity: VPNConfigEntity): WGConfig {
    val remoteInterface = WGInterface(
      privateKey = entity.wgInterface.privateKey,
      address = entity.wgInterface.address,
      dns = entity.wgInterface.dns
    )

    val remotePeer = WGPeer(
      publicKey = entity.peer.publicKey,
      endpoint = entity.peer.endpoint,
      allowedIPs = entity.peer.allowedIPs,
      persistentKeepalive = entity.peer.persistentKeepalive
    )
    return WGConfig(
      `interface` = remoteInterface,
      peer = remotePeer
    )
  }
}