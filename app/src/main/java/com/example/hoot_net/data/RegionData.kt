package com.example.hoot_net.data

data class Region(
  val name: String,
  val country: String,
  val iconUrl: String,
  val isLocked: Boolean,
  val baseUrl: String
)

fun getRegions(): List<Region> {
  return listOf(
    Region(
      "South-mumbai",
      "IN",
      "https://firebasestorage.googleapis.com/v0/b/time-capsule-android.appspot.com/o/vpn-country-flags%2Fin.png?alt=media&token=0198b537-0b1a-4e95-8e8a-0ff7c93fc826",
      false,
      baseUrl ="http://4.240.97.90:3000/"
    ),
    Region(
      "London-2",
      "UK",
      "https://firebasestorage.googleapis.com/v0/b/time-capsule-android.appspot.com/o/vpn-country-flags%2Funited-kingdom.png?alt=media&token=5d17c117-5162-4f93-b3a4-9e3868981f30",
      true,
      "http://4.240.97.90:3000/"
    )
  )

}