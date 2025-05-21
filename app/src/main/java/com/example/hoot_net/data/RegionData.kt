package com.example.hoot_net.data

data class Region(
  val name: String,
  val country: String,
  val iconUrl: String,
  val isLocked: Boolean
)

fun getRegions(): List<Region>{
  return listOf(
    Region("South-mumbai","India","",false)
  )

}