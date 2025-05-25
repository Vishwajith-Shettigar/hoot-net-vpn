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
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class Status {
  CONNECTED,
  DISCONNECTED,
  CONNECTING
}

data class UIState(
  var status: Status = Status.DISCONNECTED,
  val selecetdRegion: String? = null,
)

@HiltViewModel
class MainViewModel @Inject constructor(
  private val regionManager: RegionManager
) : ViewModel() {


  private val _state: MutableStateFlow<UIState> = MutableStateFlow(UIState())
  val state: StateFlow<UIState> = _state.asStateFlow()

  fun connect(regionName: String, baseUrl: String, backend: Backend, tunnel: WgTunnel) {
    _state.update {
      it.copy(status = Status.CONNECTING)
    }

    viewModelScope.launch {
      val res = regionManager.getClientConfig(regionName = regionName, baseUrl = baseUrl)
      when (res) {
        is HootResponse.Error -> {
        }

        is HootResponse.Success<WGConfig?> -> {

          if (res.data != null) {
            connectWireguard(backend = backend, tunnel = tunnel, wgConfig = res.data)
          } else {
            _state.update {
              it.copy(status = Status.DISCONNECTED)
            }

          }
        }
      }
    }

  }

  fun connectWireguard(backend: Backend, tunnel: WgTunnel, wgConfig: WGConfig) {
    val interfaceBuilder = Interface.Builder()
    val peerBuilder = Peer.Builder()

    viewModelScope.launch {
      repeat(5) { attempt ->
        try {
          if (backend.getState(tunnel) == Tunnel.State.UP) {
            Log.d("hoot-net", "Tunnel already UP. Tearing down...")
            backend.setState(tunnel, Tunnel.State.DOWN, null)
          } else {
            Log.d("hoot-net", "Attempting to bring tunnel UP (try ${attempt + 1})")

            backend.setState(
              tunnel, Tunnel.State.UP, Config.Builder()
                .setInterface(
                  interfaceBuilder
                    .addAddress(InetNetwork.parse("10.200.200.2/24"))
                    .parsePrivateKey(wgConfig.`interface`.privateKey)
                    .build()
                )
                .addPeer(
                  peerBuilder
                    .addAllowedIp(InetNetwork.parse("0.0.0.0/0"))
                    .setEndpoint(InetEndpoint.parse(wgConfig.peer.endpoint))
                    .parsePublicKey(wgConfig.peer.publicKey)
                    .build()
                )
                .build()
            )
          }

          _state.update {
            it.copy(status = Status.CONNECTED)
          }

          return@launch

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
  }

  fun getSelectedRegionDetails(){

  }

}