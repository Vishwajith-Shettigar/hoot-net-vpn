package com.example.hoot_net.data.local

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

val REGION_NAME_KEY="region_name"
val VPN_CONNECTED_KEY="vpn_status"
val NEW_SESSION_KEY="new_session"

@Singleton
class SharedPreferenceHelper @Inject constructor(@ApplicationContext context: Context) {

  private val sharedPreferences: SharedPreferences =
    context.getSharedPreferences("hoot-net-prefs", Context.MODE_PRIVATE)

  fun saveRegionName(value: String) {
    sharedPreferences.edit().putString(REGION_NAME_KEY, value).apply()
  }

  fun getRegionName( default: String = ""): String {
    return sharedPreferences.getString(REGION_NAME_KEY, default) ?: default
  }

  fun saveIsVpnConnected( value: Boolean) {
    sharedPreferences.edit().putBoolean(VPN_CONNECTED_KEY, value).apply()
  }

  fun getIsVpnConnected( default: Boolean = false): Boolean {
    return sharedPreferences.getBoolean(VPN_CONNECTED_KEY, default)
  }

  fun saveIsNewSession( value: Boolean) {
    sharedPreferences.edit().putBoolean(NEW_SESSION_KEY, value).apply()
  }

  fun getIsNewSession( default: Boolean = false): Boolean {
    return sharedPreferences.getBoolean(NEW_SESSION_KEY, default)
  }

  fun clear() {
    sharedPreferences.edit().clear().apply()
  }
}
