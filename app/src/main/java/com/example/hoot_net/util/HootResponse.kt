package com.example.hoot_net.util

import android.os.Message

sealed class HootResponse< out T> {
  data class Success< T>(val data:T): HootResponse<T>()
  data class Error(val exception: Exception?=null,val message: String?=null): HootResponse<Nothing>()
}