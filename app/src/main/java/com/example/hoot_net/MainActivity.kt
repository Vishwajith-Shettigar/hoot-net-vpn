package com.example.hoot_net

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import com.example.hoot_net.data.RegionManager
import com.example.hoot_net.data.local.SharedPreferenceHelper
import com.example.hoot_net.data.remote.ApiClient
import com.example.hoot_net.data.remote.VPNApiService
import com.example.hoot_net.screens.MainScreen
import com.example.hoot_net.screens.SplashScreen
import com.example.hoot_net.ui.theme.HootnetTheme
import com.example.hoot_net.viewmodel.MainViewModel
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
import dagger.hilt.android.lifecycle.HiltViewModel
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

  @Inject
  lateinit var sharedPreferenceHelper: SharedPreferenceHelper

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
//          ConnectButton(modifier = Modifier.padding(innerPadding))
          SplashScreen(sharedPreferenceHelper)
          MainScreen(modifier = Modifier.padding(innerPadding),backend=backend,tunnel=tunnel)
        }
      }
    }
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (requestCode == 100 && resultCode == RESULT_OK) {
//      connect()
    }
  }




//  @Composable
//  fun ConnectButton(modifier: Modifier = Modifier) {
//
//    Box(modifier = modifier.fillMaxSize()) {
//      Button(modifier = Modifier.align(Alignment.Center), onClick = { requestVpnPermission() }) {
//        Text(text = "Connect")
//      }
//
//      Button(
//        onClick =
//          {
//            Log.d("hoot-net", "hello")
//
//
//            lifecycleScope.launch(Dispatchers.IO) {
//
//              val res =
//                regionManager.getClientConfig("South-mumbai", BuildConfig.BASE_URL.toString())
////              val res= apiClient.getClient(BuildConfig.BASE_URL).getNewClientConfig()
//              Log.d("hoot-net", res.toString())
//            }
//          }) {
//
//        Text("Get client")
//
//      }
//
//      Button(modifier = Modifier.align(Alignment.BottomCenter), onClick = {
//
//      }) {
//        Text(text = "Disconnect")
//      }
//    }
//
//  }
}





