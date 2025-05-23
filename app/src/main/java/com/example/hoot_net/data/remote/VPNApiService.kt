package com.example.hoot_net.data.remote

import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET

interface VPNApiService {
  @GET("new-client")
  suspend fun getNewClientConfig(): Response <WGConfig>
}
