package com.example.hoot_net.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.hoot_net.R
import com.example.hoot_net.data.local.SharedPreferenceHelper
import com.example.hoot_net.ui.theme.cherryBomb
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun SplashScreen(sharedPreferenceHelper: SharedPreferenceHelper, navigate: () -> Unit) {

  val scope = rememberCoroutineScope()

  LaunchedEffect(Unit) {

    sharedPreferenceHelper.saveIsNewSession(true)

    scope.launch {
      delay(2000)
      navigate()
    }
  }

  val composition by rememberLottieComposition(
    LottieCompositionSpec.Asset("loading.json")
  )

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(Color.White)
  ) {
    Image(
      modifier = Modifier
        .align(Alignment.Center)
        .size(160.dp),
      painter = painterResource(R.drawable.logo), contentDescription = ""
    )

    Row(
      modifier = Modifier
        .align(Alignment.BottomCenter)
        .padding(vertical = 20.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      val gradient = Brush.linearGradient(
        colors = listOf(Color.Blue, Color.Red, Color.Green)
      )

      Text(
        text = "れっど ふらっぐ",
        style = TextStyle(
          brush = gradient,
          fontFamily = FontFamily(fonts = listOf(cherryBomb)),
          fontSize = 16.sp
        )
      )
      LottieAnimation(
        composition,
        iterations = LottieConstants.IterateForever,
        modifier = Modifier.size(40.dp)
      )
    }
  }


}

