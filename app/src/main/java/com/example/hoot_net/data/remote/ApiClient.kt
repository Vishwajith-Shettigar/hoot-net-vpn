package com.example.hoot_net.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiClient {
  private var retrofit: Retrofit? = null
  private var currentBaseUrl: String? = null

  fun getClient(baseUrl: String): VPNApiService {
    if (retrofit == null || baseUrl != currentBaseUrl) {
      currentBaseUrl = baseUrl
      retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    }
    return retrofit!!.create(VPNApiService::class.java)
  }
}
