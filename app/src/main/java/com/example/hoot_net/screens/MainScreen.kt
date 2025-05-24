package com.example.hoot_net.screens

import android.graphics.Paint
import android.text.Layout
import android.widget.Space
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.colorspace.WhitePoint
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.hoot_net.R
import com.example.hoot_net.ui.theme.cherryBomb

val grayscaleColors = listOf(
  Color.Gray,
  Color.LightGray,
  Color.Gray
)

val rgbColors = listOf(
  Color.Red,
  Color.Blue,
  Color.Green
)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(modifier: Modifier) {
  val scope = rememberCoroutineScope()

  val waveColors by remember {
    mutableStateOf<List<Color>>(rgbColors)
  }

  val ringColors by remember {
    mutableStateOf<List<Color>>(rgbColors)

  }

  val bottomSheetState = rememberBottomSheetScaffoldState(
    bottomSheetState = rememberStandardBottomSheetState(
      initialValue = SheetValue.PartiallyExpanded,
      skipHiddenState = true
    )
  )
  BottomSheetScaffold(
    scaffoldState = bottomSheetState, sheetPeekHeight = 160.dp,
    sheetContainerColor = Color.Blue.copy(alpha = 0.35f), sheetContent = {
      LazyColumn {
        items(49) {
          RegionCard()
          Spacer(modifier = Modifier.height(10.dp))
        }
      }
    },
    sheetShape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp)
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
      val composition by rememberLottieComposition(
        LottieCompositionSpec.Asset("jumpingbird.json")
      )
      WaveGlowButton(
        onClick = {},
        modifier = Modifier
          .align(Alignment.Center)
          .offset(y = -50.dp),
        waveColors,
        ringColors
      )

      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
          .align(Alignment.BottomCenter)
          .offset(y = -260.dp),
      ) {
        Text(
          text = "Swipe up to select a region",
          color = Color.LightGray
        )
      }
      LottieAnimation(
        modifier = Modifier
          .size(200.dp)
          .align(Alignment.BottomCenter)
          .offset(y = -140.dp),
        composition = composition,
        iterations = LottieConstants.IterateForever,
      )

    }
  }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun WaveGlowButton(
  onClick: () -> Unit,
  modifier: Modifier = Modifier, waveColors: List<Color>, ringColors: List<Color>
) {
  val infiniteTransition = rememberInfiniteTransition()


  var isPressed by remember {
    mutableStateOf(false)
  }

  val scale by animateFloatAsState(
    targetValue = if (isPressed) 0.92f else 1f,
    animationSpec = tween(100),
    label = "Press animation"
  )

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
        color = waveColors[0],
        radius = wave1Radius.value,
        center = center,
        alpha = waveAlpha.value
      )
      drawCircle(
        color = waveColors[1],
        radius = wave2Radius.value,
        center = center,
        alpha = waveAlpha.value
      )
      drawCircle(
        color = waveColors[2],
        radius = wave3Radius.value,
        center = center,
        alpha = waveAlpha.value
      )
    }
    Box(
      modifier = Modifier
        .graphicsLayer {
          scaleX = scale
          scaleY = scale
        }
        .size(120.dp)
        .clip(CircleShape)
        .border(
          width = 3.dp, shape = CircleShape, brush = Brush.linearGradient(
            colors = listOf(
              ringColors[0], ringColors[1], ringColors[2]
            )
          )
        )
        .background(Color.LightGray)
        .pointerInteropFilter {
          when (it.action) {
            android.view.MotionEvent.ACTION_DOWN -> {
              isPressed = true
              true
            }

            android.view.MotionEvent.ACTION_UP, android.view.MotionEvent.ACTION_CANCEL -> {
              isPressed = false
              onClick()
              true

            }

            else -> false
          }
        },
      contentAlignment = Alignment.Center
    ) {
      Image(
        modifier = Modifier
          .size(80.dp), painter = painterResource(R.drawable.connected_2), contentDescription = ""
      )
    }
  }
}

@OptIn(ExperimentalComposeUiApi::class)
@Preview
@Composable
fun RegionCard() {
  var isPressed by remember {
    mutableStateOf(false)
  }

  val scale by animateFloatAsState(
    targetValue = if (isPressed) 0.92f else 1f,
    animationSpec = tween(100),
    label = "Press animation"
  )
  Row(
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.Start,
    modifier = Modifier
      .graphicsLayer {
        scaleX = scale
        scaleY = scale
      }
      .height(80.dp)
      .pointerInteropFilter {
        when (it.action) {
          android.view.MotionEvent.ACTION_DOWN -> {
            isPressed = true
            true
          }

          android.view.MotionEvent.ACTION_UP, android.view.MotionEvent.ACTION_CANCEL -> {
            isPressed = false
            true

          }

          else -> false
        }
      }
      .fillMaxWidth()
      .padding(horizontal = 10.dp)
      .clip(shape = RoundedCornerShape(40.dp))
      .background(color = Color.Black.copy(alpha = 0.3f))
      .border(width = 1.dp, color = Color.White, shape = RoundedCornerShape(40.dp))
      .padding(horizontal = 20.dp)
  ) {
    Box(modifier = Modifier
      .size(50.dp)
      .weight(0.2f)) {
      Image(
        modifier = Modifier.align(Alignment.CenterStart),
        painter = painterResource(R.drawable.`in`), contentDescription = "",

        )
    }

    Column(modifier = Modifier.weight(0.6f), horizontalAlignment = Alignment.Start) {
      Text(
        text = "South-mumbai", style = TextStyle.Default.copy(fontWeight = FontWeight.ExtraBold),
        fontSize = 18.sp
      )
      Text(text = "In", style = TextStyle.Default.copy(fontWeight = FontWeight.Bold))
    }
    Image(
      painter = painterResource(R.drawable.secured),
      modifier = Modifier
        .weight(0.2f)
        .size(35.dp),
      contentDescription = ""
    )
  }
}
