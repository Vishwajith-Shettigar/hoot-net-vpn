package com.example.hoot_net.viewmodel

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.example.hoot_net.BuildConfig
import com.example.hoot_net.WgTunnel
import com.example.hoot_net.data.RegionManager
import com.example.hoot_net.data.getRegions
import com.example.hoot_net.data.local.SharedPreferenceHelper
import com.example.hoot_net.data.remote.WGConfig
import com.example.hoot_net.util.HootResponse
import com.wireguard.android.backend.Backend
import com.wireguard.android.backend.BackendException
import com.wireguard.android.backend.Tunnel
import com.wireguard.config.Config
import com.wireguard.config.InetEndpoint
import com.wireguard.config.InetNetwork
import com.wireguard.config.Interface
import com.wireguard.config.Peer
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import java.net.InetAddress
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

enum class Status {
  CONNECTED,
  DISCONNECTED,
  CONNECTING
}

data class UIState(
  var status: Status = Status.DISCONNECTED,
  var selecetdRegion: String? = null,
)

@HiltViewModel
class MainViewModel @Inject constructor(
  private val regionManager: RegionManager,
  private val sharedPreferenceHelper: SharedPreferenceHelper
) : ViewModel() {


  private val _state: MutableStateFlow<UIState> = MutableStateFlow(UIState())
  val state: StateFlow<UIState> = _state.asStateFlow()

  fun updateSelectedRegion(regionName: String) {
    _state.update {
      it.copy(selecetdRegion = regionName)
    }
  }

  fun setUp() {
    if (sharedPreferenceHelper.getIsVpnConnected()) {
      if (sharedPreferenceHelper.getIsNewSession()) {
        _state.update {
          it.copy(
            status = Status.CONNECTED,
            selecetdRegion = sharedPreferenceHelper.getRegionName()
          )
        }
      }
    }
  }

  fun discnnect(backend: Backend, tunnel: WgTunnel) {
    viewModelScope.launch(Dispatchers.IO) {
      if (_state.value.status == Status.CONNECTED) {
        Log.d("hoot-net", "connected ####")
        if (sharedPreferenceHelper.getIsNewSession()) {
          Log.d("hoot-net", "new sesssion ####")

          val defaultRegion = getRegions()[0]
          connect(defaultRegion.name, defaultRegion.baseUrl, backend, tunnel)
          backend.setState(tunnel, Tunnel.State.DOWN, null)
        } else {
          Log.d("hoot-net", "old sesssion ####")

          backend.setState(tunnel, Tunnel.State.DOWN, null)
        }
        sharedPreferenceHelper.saveIsVpnConnected(false)
        _state.update {
          it.copy(status = Status.DISCONNECTED)
        }
        Log.d("hoot-net", "Disconnectinggg ## ####")

      }
    }
  }

  fun startConnection(regionName: String, baseUrl: String, backend: Backend, tunnel: WgTunnel) {
    viewModelScope.launch(Dispatchers.IO) {
      connect(regionName, baseUrl, backend, tunnel)
    }
  }

  suspend fun connect(regionName: String, baseUrl: String, backend: Backend, tunnel: WgTunnel) {

    if (_state.value.status != Status.CONNECTED)
      _state.update {
        it.copy(status = Status.CONNECTING)
      }


    val res = regionManager.getClientConfig(regionName = regionName, baseUrl = baseUrl)
    when (res) {
      is HootResponse.Error -> {
        Log.d("hoot-net", "error")

      }

      is HootResponse.Success<WGConfig?> -> {
        Log.d("hoot-net", "Success")

        if (res.data != null) {
          connectWireguard(
            backend = backend,
            tunnel = tunnel,
            wgConfig = res.data,
            regionName = regionName
          )
        } else {
          _state.update {
            it.copy(status = Status.DISCONNECTED)
          }

        }
      }
    }

  }

  suspend fun connectWireguard(
    backend: Backend,
    tunnel: WgTunnel,
    wgConfig: WGConfig,
    regionName: String
  ) {
    val interfaceBuilder = Interface.Builder()
    val peerBuilder = Peer.Builder()
    Log.d("hoot-net", wgConfig.toString())


    repeat(5) { attempt ->
      try {
        if (backend.getState(tunnel) == Tunnel.State.UP) {
          Log.d("hoot-net", "Tunnel already UP. Tearing down...")
          backend.setState(tunnel, Tunnel.State.DOWN, null)
          withContext(Dispatchers.Main) {
            _state.update {
              it.copy(status = Status.DISCONNECTED)
            }

          }

        } else {
          Log.d("hoot-net", "Attempting to bring tunnel UP (try ${attempt + 1})")

          backend.setState(
            tunnel, Tunnel.State.UP, Config.Builder()
              .setInterface(
                interfaceBuilder
                  .addAddress(InetNetwork.parse(wgConfig.`interface`.address))
                  .parsePrivateKey(wgConfig.`interface`.privateKey)
                  .build()
              )
              .addPeer(
                peerBuilder
                  .addAllowedIp(InetNetwork.parse("0.0.0.0/0"))
                  .setEndpoint(InetEndpoint.parse(wgConfig.peer.endpoint))
                  .parsePublicKey(wgConfig.peer.publicKey)
                  .setPersistentKeepalive(25)
                  .build()
              )
              .build()
          )
          sharedPreferenceHelper.saveIsVpnConnected(true)
          sharedPreferenceHelper.saveRegionName(regionName)
          sharedPreferenceHelper.saveIsNewSession(false)
          withContext(Dispatchers.Main) {
            _state.update {
              it.copy(status = Status.CONNECTED)
            }
          }
        }

        return

      } catch (e: BackendException) {
        Log.d("hoot-net", "Error: ${e.reason}")
        if (e.reason == BackendException.Reason.UNABLE_TO_START_VPN) {
          val delayMillis = 1000L * (attempt + 1)
          Log.d("hoot-net", "Retrying in ${delayMillis}ms...")
          delay(delayMillis)
        } else {
        }
      }
    }

    Log.e("hoot-net", "Failed to start VPN after 5 attempts")

  }

  fun getSelectedRegionDetails() {

  }

}