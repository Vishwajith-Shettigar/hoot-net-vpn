package com.example.hoot_net.data

import android.util.Log
import com.example.hoot_net.data.local.VPNConfigDao
import com.example.hoot_net.data.local.VPNConfigEntity
import com.example.hoot_net.data.remote.ApiClient
import com.example.hoot_net.data.remote.WGConfig
import com.example.hoot_net.data.remote.WGInterface
import com.example.hoot_net.data.remote.WGPeer
import com.example.hoot_net.util.HootResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RegionManager @Inject constructor(
  val apiClient: ApiClient,
  val vpnConfigDao: VPNConfigDao
) {
  suspend fun getClientConfig(regionName: String, baseUrl: String): HootResponse<WGConfig?> {
    return try {
      val localConfig = vpnConfigDao.getConfig(regionName)

      if (localConfig != null) {
        // Convert DB entity to WGConfig
        return HootResponse.Success(convertEntityToWGConfig(localConfig))

      }
      Log.d("hoot-net", "no local")

      val client = apiClient.getClient(baseUrl)
      val remoteConfigResponse = client.getNewClientConfig()

      if (remoteConfigResponse.isSuccessful && remoteConfigResponse.body() != null) {
        val vPNConfigEntity = convertWGConfigToEntity(remoteConfigResponse.body()!!, regionName)
        vpnConfigDao.insertConfig(vPNConfigEntity)
        return HootResponse.Success(remoteConfigResponse.body()!!)
      }
      HootResponse.Error(message = remoteConfigResponse.message())
    } catch (e: Exception) {
      Log.d("hoot-net", e.toString())
      e.printStackTrace()
      HootResponse.Error(exception = e)
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

  fun convertWGConfigToEntity(config: WGConfig, regionName: String): VPNConfigEntity {
    val entityInterface = com.example.hoot_net.data.local.WGInterface(
      privateKey = config.`interface`.privateKey,
      address = config.`interface`.address,
      dns = config.`interface`.dns
    )

    val entityPeer = com.example.hoot_net.data.local.WGPeer(
      publicKey = config.peer.publicKey,
      endpoint = config.peer.endpoint,
      allowedIPs = config.peer.allowedIPs,
      persistentKeepalive = config.peer.persistentKeepalive
    )

    return VPNConfigEntity(
      region = regionName,
      wgInterface = entityInterface,
      peer = entityPeer
    )
  }
}
