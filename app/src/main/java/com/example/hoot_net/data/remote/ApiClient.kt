package com.example.hoot_net.data.remote

import javax.inject.Inject
import javax.inject.Singleton
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Singleton
class ApiClient @Inject constructor() {
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
