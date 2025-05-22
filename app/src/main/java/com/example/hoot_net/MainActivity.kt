package com.example.hoot_net

import android.os.Bundle
import android.util.Log
import android.widget.Toast
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import com.example.hoot_net.data.remote.ApiClient
import com.example.hoot_net.data.remote.VPNApiService
import com.example.hoot_net.data.remote.WGConfig
import com.example.hoot_net.ui.theme.HootnetTheme
import com.wireguard.android.backend.GoBackend
import com.wireguard.android.backend.Tunnel
import com.wireguard.config.Config
import com.wireguard.config.InetEndpoint
import com.wireguard.config.InetNetwork
import com.wireguard.config.Interface
import com.wireguard.config.Peer
import dagger.hilt.EntryPoint
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.awaitResponse


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

  @Inject
  lateinit var apiClient: ApiClient

  lateinit var tunnel: WgTunnel
  val backend = GoBackend(this)
  lateinit var vpnApiService: VPNApiService

  override fun onCreate(savedInstanceState: Bundle?) {
    tunnel = WgTunnel()
    super.onCreate(savedInstanceState)

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

  fun connect() {
    val intentPrepare = GoBackend.VpnService.prepare(this)
    if (intentPrepare != null)
      startActivityForResult(intentPrepare, 0)

    val interfaceBuilder = Interface.Builder()
    val peerBuilder = Peer.Builder()


    lifecycleScope.launch {
      try {
        if (backend.getState(tunnel) == Tunnel.State.UP) {
          Log.d("hoot-net", "Down")

          backend.setState(tunnel, Tunnel.State.DOWN, null)
        } else {
          Log.d("hoot-net", "Up")

          backend.setState(
            tunnel, Tunnel.State.UP, Config.Builder()
              .setInterface(
                interfaceBuilder.addAddress(InetNetwork.parse("10.200.200.2/24"))
                  .parsePrivateKey(BuildConfig.TEMP_PRIVATE_KEY)
                  .build()
              ).addPeer(
                peerBuilder.addAllowedIp(InetNetwork.parse("0.0.0.0/0")).setEndpoint(
                  InetEndpoint.parse("${BuildConfig.BASE_URL}:51820")
                ).parsePublicKey(BuildConfig.TEMP_PUBLIC_KEY).build()
              )
              .build()
          )
        }
      } catch (e: Exception) {
        Log.d("hoot-net", e.message.toString())
      }
    }

  }

  @Composable
  fun ConnectButton(modifier: Modifier = Modifier) {

    Box(modifier = modifier.fillMaxSize()) {
      Button(modifier = Modifier.align(Alignment.Center), onClick = { connect() }) {
        Text(text = "Connect")
      }

      Button(
        onClick =
          {
            val res =
              lifecycleScope.launch {
                vpnApiService.getNewClientConfig().enqueue(object : Callback<WGConfig> {
                  override fun onResponse(
                    call: Call<WGConfig?>,
                    response: Response<WGConfig?>
                  ) {
                    Log.d("hoot-net", response.body().toString())

                  }

                  override fun onFailure(
                    call: Call<WGConfig?>,
                    t: Throwable
                  ) {
                    Log.d("hoot-net", t.message.toString().toString())
                  }

                })
              }
          }) {

        Text("Get client")

      }

      Button(modifier = Modifier.align(Alignment.BottomCenter), onClick = {
        backend.setState(tunnel, Tunnel.State.DOWN, null)
      }) {
        Text(text = "Disconnect")
      }
    }

  }
}



