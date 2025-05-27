package com.example.hoot_net.data.local

import android.content.Context
import android.content.SharedPreferences
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SharedPreferenceHelper @Inject constructor(context: Context) {

  private val sharedPreferences: SharedPreferences =
    context.getSharedPreferences("my_prefs", Context.MODE_PRIVATE)

  fun saveRegionName(key: String, value: String) {
    sharedPreferences.edit().putString(key, value).apply()
  }

  fun getRegionName(key: String, default: String = ""): String {
    return sharedPreferences.getString(key, default) ?: default
  }

  fun saveIsNewSession(key: String, value: Boolean) {
    sharedPreferences.edit().putBoolean(key, value).apply()
  }

  fun getIsNewSession(key: String, default: Boolean = false): Boolean {
    return sharedPreferences.getBoolean(key, default)
  }

  fun clear() {
    sharedPreferences.edit().clear().apply()
  }
}
