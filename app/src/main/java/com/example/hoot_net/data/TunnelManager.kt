package com.example.hoot_net.data

import com.example.hoot_net.data.remote.ApiClient
import com.example.hoot_net.data.remote.WGConfig
import javax.inject.Inject


class TunnelManager @Inject constructor(
  apiClient: ApiClient
) {


  fun getClientConfig(regionName: String): WGConfig?
  {
return null
  }
}