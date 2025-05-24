package com.example.hoot_net.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hoot_net.R
import com.example.hoot_net.ui.theme.cherryBomb
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(modifier: Modifier) {
  val scope = rememberCoroutineScope()

  val bottomSheetState = rememberBottomSheetScaffoldState(
    bottomSheetState = rememberStandardBottomSheetState(
      initialValue = SheetValue.PartiallyExpanded,
      skipHiddenState = false
    )
  )
  BottomSheetScaffold(
    scaffoldState = bottomSheetState, sheetPeekHeight = 100.dp,
    sheetContainerColor = Color.Blue.copy(alpha = 0.35f), sheetContent = {
      Column(
        Modifier
          .fillMaxWidth()
          .height(200.dp)
          .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Text("VPN Status", fontWeight = FontWeight.Bold)
        Text("Server: Netherlands")
      }
      Column(
        Modifier
          .fillMaxWidth()
          .height(200.dp)
          .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Text("VPN Status", fontWeight = FontWeight.Bold)
        Text("Server: Netherlands")
      }
    },
    sheetShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
  ) {
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(Color.White)
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .align(Alignment.TopStart)
          .padding(top = 40.dp, start = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Image(
            painter = painterResource(R.drawable.logo),
            contentDescription = "",
            modifier = Modifier.size(60.dp)
          )
          val gradient = Brush.linearGradient(
            colors = listOf(Color.Blue, Color.Red, Color.Green)
          )
          Text(
            text = "れっど ふらっぐ",
            style = TextStyle(
              brush = gradient,
              fontFamily = FontFamily(fonts = listOf(cherryBomb)),
              fontSize = 26.sp
            )
          )
        }
      }
      WaveGlowButton({}, modifier = Modifier.align(Alignment.Center))
    }
  }
}

@Composable
fun WaveGlowButton(
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val infiniteTransition = rememberInfiniteTransition()

  val wave1Radius = infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 520f,
    animationSpec = infiniteRepeatable(
      tween(2000, easing = FastOutSlowInEasing),
      RepeatMode.Restart
    )
  )
  val wave2Radius = infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 420f,
    animationSpec = infiniteRepeatable(
      tween(2000, easing = FastOutSlowInEasing),
      RepeatMode.Restart
    )
  )

  val wave3Radius = infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 320f,
    animationSpec = infiniteRepeatable(
      tween(2000, easing = FastOutSlowInEasing),
      RepeatMode.Restart
    )
  )

  val waveAlpha = infiniteTransition.animateFloat(
    initialValue = 0.3f,
    targetValue = 0f,
    animationSpec = infiniteRepeatable(
      tween(2000, easing = FastOutSlowInEasing),
      RepeatMode.Restart
    )
  )


  Box(
    contentAlignment = Alignment.Center,
    modifier = modifier.size(200.dp)
  ) {
    // Wave 1
    Canvas(modifier = Modifier.fillMaxSize()) {
      drawCircle(
        color = Color.Red,
        radius = wave1Radius.value,
        center = center,
        alpha = waveAlpha.value
      )
      drawCircle(
        color = Color.Blue,
        radius = wave2Radius.value,
        center = center,
        alpha = waveAlpha.value
      )
      drawCircle(
        color = Color.Green,
        radius = wave3Radius.value,
        center = center,
        alpha = waveAlpha.value
      )
    }
    Box(
      modifier = Modifier
        .size(120.dp)
        .clip(CircleShape)
        .border(width = 2.dp, shape = CircleShape, color = Color.White)
        .background(Color.LightGray)
        .clickable { onClick() },
      contentAlignment = Alignment.Center
    ) {
      androidx.compose.material3.Icon(
        imageVector = Icons.Default.Person,
        contentDescription = "Power",
        tint = Color.White,
        modifier = Modifier.size(32.dp)
      )
    }
  }
}


