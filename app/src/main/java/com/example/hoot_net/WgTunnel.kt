package com.example.hoot_net

import com.wireguard.android.backend.Tunnel

class WgTunnel:Tunnel {
  override fun getName(): String {
    return "wgTubbel"
  }

  override fun onStateChange(newState: Tunnel.State) {
  }
}
