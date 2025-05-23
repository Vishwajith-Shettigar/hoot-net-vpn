package com.example.hoot_net

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.example.hoot_net.data.RegionManager
import com.example.hoot_net.data.remote.ApiClient
import com.example.hoot_net.data.remote.VPNApiService
import com.example.hoot_net.ui.theme.HootnetTheme
import com.wireguard.android.backend.BackendException
import com.wireguard.android.backend.BackendException.Reason.UNABLE_TO_START_VPN
import com.wireguard.android.backend.GoBackend
import com.wireguard.android.backend.Tunnel
import com.wireguard.config.Config
import com.wireguard.config.InetEndpoint
import com.wireguard.config.InetNetwork
import com.wireguard.config.Interface
import com.wireguard.config.Peer
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

  @Inject
  lateinit var apiClient: ApiClient

  @Inject
  lateinit var regionManager: RegionManager

  val backend = GoBackend(this)

  lateinit var tunnel: WgTunnel

  lateinit var vpnApiService: VPNApiService

  override fun onCreate(savedInstanceState: Bundle?) {

    super.onCreate(savedInstanceState)
    tunnel = WgTunnel()
    vpnApiService = apiClient.getClient(BuildConfig.BASE_URL)
    enableEdgeToEdge()
    setContent {
      HootnetTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
          ConnectButton(modifier = Modifier.padding(innerPadding))
        }
      }
    }
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (requestCode == 100 && resultCode == RESULT_OK) {
      connect()
    }
  }

  fun requestVpnPermission() {
    val intentPrepare = GoBackend.VpnService.prepare(this)
    if (intentPrepare != null) {
      startActivityForResult(intentPrepare, 100)
    } else {
      connect()
    }
  }


  fun connect() {
    val interfaceBuilder = Interface.Builder()
    val peerBuilder = Peer.Builder()

    lifecycleScope.launch {
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
                    .parsePrivateKey(BuildConfig.TEMP_PRIVATE_KEY.toString())
                    .build()
                )
                .addPeer(
                  peerBuilder
                    .addAllowedIp(InetNetwork.parse("0.0.0.0/0"))
                    .setEndpoint(InetEndpoint.parse(BuildConfig.END_POINT.toString()))
                    .parsePublicKey(BuildConfig.TEMP_PUBLIC_KEY.toString())
                    .build()
                )
                .build()
            )
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


  @Composable
  fun ConnectButton(modifier: Modifier = Modifier) {

    Box(modifier = modifier.fillMaxSize()) {
      Button(modifier = Modifier.align(Alignment.Center), onClick = { requestVpnPermission() }) {
        Text(text = "Connect")
      }

      Button(
        onClick =
          {
            Log.d("hoot-net", "hello")


            lifecycleScope.launch(Dispatchers.IO) {

              val res =
                regionManager.getClientConfig("South-mumbai", BuildConfig.BASE_URL.toString())
//              val res= apiClient.getClient(BuildConfig.BASE_URL).getNewClientConfig()
              Log.d("hoot-net", res.toString())
            }
          }) {

        Text("Get client")

      }

      Button(modifier = Modifier.align(Alignment.BottomCenter), onClick = {

      }) {
        Text(text = "Disconnect")
      }
    }

  }
}



