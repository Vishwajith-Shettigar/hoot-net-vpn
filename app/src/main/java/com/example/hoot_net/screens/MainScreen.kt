package com.example.hoot_net.screens

import android.app.Activity
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.hoot_net.R
import com.example.hoot_net.WgTunnel
import com.example.hoot_net.data.Region
import com.example.hoot_net.data.getRegions
import com.example.hoot_net.ui.theme.cherryBomb
import com.example.hoot_net.viewmodel.MainViewModel
import com.example.hoot_net.viewmodel.Status
import com.wireguard.android.backend.Backend
import com.wireguard.android.backend.GoBackend.VpnService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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
fun MainScreen(
  modifier: Modifier,
  viewmodel: MainViewModel = hiltViewModel(),
  backend: Backend,
  tunnel: WgTunnel
) {


  val scope = rememberCoroutineScope()

  Log.d("hoot-net", "composition check ")

  val uiState by viewmodel.state.collectAsState()

  var waveColors by remember {
    mutableStateOf<List<Color>>(grayscaleColors)
  }

  var ringColors by remember {
    mutableStateOf<List<Color>>(grayscaleColors)
  }

  var selectedRegion by remember {
    mutableStateOf<Region?>(null)
  }

  LaunchedEffect(Unit) {
    viewmodel.setUp()

  }

  val context = LocalContext.current

  val vpnPermissionLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.StartActivityForResult()
  ) { result ->
    if (result.resultCode == Activity.RESULT_OK) {
      scope.launch(Dispatchers.IO) {
        viewmodel.connect(
          regionName = selectedRegion!!.name,
          backend = backend,
          baseUrl = selectedRegion!!.baseUrl,
          tunnel = tunnel
        )
      }

    }
  }

  val intentPrepare = VpnService.prepare(context)

  LaunchedEffect(uiState.status) {
    Log.d("hoot-net", "status")
    when (uiState.status) {
      Status.CONNECTED -> {
        Log.d("hoot-net", " Connected ------------")

        waveColors = rgbColors
        ringColors = rgbColors
      }

      Status.DISCONNECTED -> {
        Log.d("hoot-net", " Dusconnected ------------")

        waveColors = grayscaleColors
        ringColors = grayscaleColors
      }

      Status.CONNECTING -> {
        Log.d("hoot-net", " Connectingggggg ------------")

        waveColors = rgbColors
        ringColors = grayscaleColors
      }
    }
  }

  var showDisconnectDialog by remember {
    mutableStateOf(false)
  }

  var showSelectRegionDialog by remember {
    mutableStateOf(false)
  }

  LaunchedEffect(uiState.selecetdRegion) {
    selectedRegion = getRegions().find {
      it.name == uiState.selecetdRegion
    }
  }


  if (showDisconnectDialog) {
    CustomDialog(isDiconnectDialog = true, positiveClick = {
      // Disconnnect
      Log.d("hoot-net", "close ###")
      showDisconnectDialog = false

      viewmodel.discnnect(backend, tunnel)
    }) {
      showDisconnectDialog = false
    }
  }

  if (showSelectRegionDialog) {
    CustomDialog(isDiconnectDialog = false, positiveClick = { showSelectRegionDialog = false }) {
      showSelectRegionDialog = false
    }
  }

  val bottomSheetState = rememberBottomSheetScaffoldState(
    bottomSheetState = rememberStandardBottomSheetState(
      initialValue = SheetValue.PartiallyExpanded,
      skipHiddenState = true
    )
  )
  BottomSheetScaffold(
    scaffoldState = bottomSheetState, sheetPeekHeight = 150.dp,
    sheetContainerColor = Color.Blue.copy(0.4f), sheetContent = {
      LazyColumn {
        items(getRegions().size) {
          RegionCard(getRegions()[it], uiState.selecetdRegion == getRegions()[it].name) {
            viewmodel.updateSelectedRegion(getRegions()[it].name)
          }
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
      Column(
        modifier = Modifier
          .align(Alignment.Center)
          .offset(y = -50.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        WaveGlowButton(
          onClick = {
            if (uiState.status == Status.CONNECTED) {
              showDisconnectDialog = true

            } else {
              if (selectedRegion != null) {
                Log.d("hoot-net", "Region is noy null")
                if (intentPrepare != null) {
                  vpnPermissionLauncher.launch(intentPrepare)
                } else {
                  scope.launch(Dispatchers.IO) {
                    viewmodel.connect(
                      selectedRegion!!.name,
                      selectedRegion!!.baseUrl,
                      backend = backend,
                      tunnel = tunnel
                    )
                  }
                }
              } else {
                Log.d("hoot-net", "Region is  null")
                showSelectRegionDialog = true

              }
            }
          },
          modifier = Modifier,
          waveColors,
          ringColors
        )
      }

      uiState.selecetdRegion?.let {
        Column(
          modifier = Modifier
            .align(Alignment.BottomCenter)
            .offset(y = -250.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Text(
            text =
              when (uiState.status) {
                Status.CONNECTED ->
                  "Connected"

                Status.DISCONNECTED -> ""
                Status.CONNECTING -> "Connecting..."
              },
            style = TextStyle(
              color = Color.Gray,
              fontWeight = FontWeight.W700
            )
          )
          Spacer(modifier = Modifier.height(20.dp))

          selectedRegion?.let {
            SelectedRegionCard(ringColors = ringColors, it)
          }

        }
      }
        ?: run {
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

            android.view.MotionEvent.ACTION_UP -> {
              isPressed = false
              onClick()
              true
            }

            android.view.MotionEvent.ACTION_CANCEL -> {
              isPressed = false
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
@Composable
fun RegionCard(region: Region, isSelected: Boolean, onClick: () -> Unit) {

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

          android.view.MotionEvent.ACTION_UP -> {
            isPressed = false
            if (!region.isLocked)
              onClick()
            true
          }

          android.view.MotionEvent.ACTION_CANCEL -> {
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
      .then(
        if (isSelected)
          Modifier.border(width = 1.dp, color = Color.White, shape = RoundedCornerShape(40.dp))
        else
          Modifier

      )
      .padding(horizontal = 20.dp)
  ) {
    Box(
      modifier = Modifier
        .size(50.dp)
        .weight(0.2f)
    ) {
      AsyncImage(
        modifier = Modifier.align(Alignment.CenterStart),
        model = region.iconUrl, contentDescription = "",
      )
    }

    Column(modifier = Modifier.weight(0.6f), horizontalAlignment = Alignment.Start) {
      Text(
        text = region.name, style = TextStyle.Default.copy(
          fontWeight = FontWeight.ExtraBold,
          color = Color.White
        ),
        fontSize = 18.sp
      )
      Spacer(modifier = Modifier.height(7.dp))
      Text(
        text = region.country, style = TextStyle.Default.copy(
          fontWeight = FontWeight.Bold,
          color = Color.White
        )
      )
    }
    if (region.isLocked)
      Icon(
        imageVector = Icons.Default.Lock,
        modifier = Modifier
          .weight(0.2f)
          .size(35.dp), contentDescription = ""
      )
    else
      Image(
        painter = painterResource(R.drawable.secured),
        modifier = Modifier
          .weight(0.2f)
          .size(35.dp),
        contentDescription = ""
      )
  }
}


fun getRegion(regionName: String): Region {
  return getRegions().find {
    it.name == regionName
  }!!
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SelectedRegionCard(ringColors: List<Color> = listOf(), selectedRegion: Region) {

  Row(
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.Start,
    modifier = Modifier
      .height(70.dp)
      .fillMaxWidth()
      .padding(horizontal = 55.dp)
      .clip(shape = RoundedCornerShape(15.dp))
      .background(Color.White.copy(alpha = 0.3f))
      .border(
        width = 1.dp, shape = RoundedCornerShape(15.dp), brush = Brush.linearGradient(
          colors = listOf(
            ringColors[0], ringColors[1], ringColors[2]
          )
        )
      )
      .padding(horizontal = 20.dp)
  ) {
    Box(
      modifier = Modifier
        .size(40.dp)
        .weight(0.2f)
    ) {
      AsyncImage(
        modifier = Modifier.align(Alignment.CenterStart),
        model = selectedRegion.iconUrl,
        contentDescription = "",

        )
    }

    Column(modifier = Modifier.weight(0.6f), horizontalAlignment = Alignment.Start) {
      Text(
        text = selectedRegion.name, style = TextStyle.Default.copy(
          fontWeight = FontWeight.ExtraBold,
          color = Color.Black
        ),
        fontSize = 15.sp
      )
      Spacer(modifier = Modifier.height(7.dp))
      Text(
        text = selectedRegion.country, style = TextStyle.Default.copy(
          fontWeight = FontWeight.Bold,
          color = Color.Black
        ),
        fontSize = 14.sp
      )
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

fun Color.darken(factor: Float = 0.8f): Color {
  return Color(
    red = red * factor,
    green = green * factor,
    blue = blue * factor,
    alpha = alpha
  )
}

@Composable
fun CustomDialog(
  isDiconnectDialog: Boolean,
  positiveClick: () -> Unit = {},
  dismiss: () -> Unit = {},
) {

  var isPressed by remember {
    mutableStateOf(false)
  }

  val scale by animateFloatAsState(
    targetValue = if (isPressed) 0.8f else 1f,
    animationSpec = tween(100),
    label = "Press animation"
  )

  var isPressed2 by remember {
    mutableStateOf(false)
  }


  val scale2 by animateFloatAsState(
    targetValue = if (isPressed2) 0.8f else 1f,
    animationSpec = tween(100),
    label = "Press animation"
  )


  Dialog(onDismissRequest = {
  }) {
    Box(
    ) {
      Image(
        painter = painterResource(R.drawable.logo), contentDescription = "",
        Modifier
          .size(60.dp)
          .align(Alignment.TopStart)
          .zIndex(11f)
      )

      Box(
        modifier = Modifier
          .width(330.dp)
          .height(150.dp)
          .shadow(10.dp, shape = RoundedCornerShape(30.dp))
          .clip(
            shape = RoundedCornerShape(30.dp)
          )
          .background(Color.Red.darken(10f))
          .padding(20.dp)
          .border(2.dp, Color.Black, RoundedCornerShape(30.dp))
          .zIndex(7f)
      ) {

        Text(
          text = if (isDiconnectDialog) "Would you like to Disconnect?" else "Hi there! Swipe up to choose a region before proceeding.",
          modifier = Modifier
            .align(Alignment.CenterStart)
            .zIndex(5f)
            .padding(horizontal = 10.dp),
          style = TextStyle.Default.copy(color = Color.White, fontWeight = FontWeight.Bold)

        )
      }

      Box(
        modifier = Modifier
          .width(90.dp)
          .height(100.dp)
          .pointerInteropFilter {
            when (it.action) {

              android.view.MotionEvent.ACTION_DOWN -> {
                isPressed2 = true
                true
              }

              android.view.MotionEvent.ACTION_UP -> {
                isPressed2 = false
                dismiss()
                true
              }

              android.view.MotionEvent.ACTION_CANCEL -> {
                isPressed2 = false
                true

              }

              else -> false
            }
          }
          .graphicsLayer {
            scaleX = scale2
            scaleY = scale2
          }

          .offset(x = -20.dp, y = 29.dp)
          .clip(shape = CircleShape)
          .background(Color.Blue.darken(4f))
          .align(Alignment.BottomEnd)


      ) {
        Text(
          text = if (isDiconnectDialog) "No" else "Ok", modifier = Modifier
            .align(Alignment.BottomCenter)
            .offset(y = -10.dp)
            .zIndex(6f),
          style = TextStyle.Default.copy(color = Color.White, fontWeight = FontWeight.Bold)

        )
      }

      if (isDiconnectDialog)
        Box(
          modifier = Modifier
            .width(90.dp)
            .height(100.dp)
            .graphicsLayer {
              scaleX = scale
              scaleY = scale
            }
            .pointerInteropFilter {
              when (it.action) {
                android.view.MotionEvent.ACTION_DOWN -> {
                  isPressed = true
                  true
                }

                android.view.MotionEvent.ACTION_UP -> {
                  isPressed = false
                  Log.d("hoot-net", "Actiob upp###")
                  positiveClick()
                  true
                }

                android.view.MotionEvent.ACTION_CANCEL -> {
                  isPressed = false
                  true

                }

                else -> false
              }
            }
            .offset(x = 20.dp, y = 29.dp)
            .clip(shape = CircleShape)
            .background(Color.DarkGray.darken(1f))
            .align(Alignment.BottomStart)


        ) {
          Text(
            text = "Yes", modifier = Modifier
              .align(Alignment.BottomCenter)
              .offset(y = -10.dp)
              .zIndex(6f),
            style = TextStyle.Default.copy(color = Color.White, fontWeight = FontWeight.Bold)

          )
        }
    }

  }
}


@Preview(showBackground = true)
@Composable
fun ChatScreen() {
  var boxWidthPx by remember { mutableStateOf(0f) }
  val density = LocalDensity.current
  val offsetX = with(density) { (boxWidthPx).toDp() }
  val screenWidthDp = LocalConfiguration.current.screenWidthDp.toFloat()
  val halfScreenWidth = screenWidthDp * 0.5f
  val w = (screenWidthDp * 0.07).dp

  Box(modifier = Modifier.fillMaxSize()) {


    Box(
      modifier = Modifier
        .align(Alignment.Center)
        .size(width = (halfScreenWidth.dp), height = 120.dp),
    ) {
      MessageBubble()

      Text(
        text = "Okay",
        fontSize = 18.sp,
        modifier = Modifier
          .align(Alignment.BottomEnd)
          .offset(x = -w, y = (-8).dp),
        color = Color.Black
      )
    }
  }
}


@Composable
fun MessageBubble(modifier: Modifier = Modifier) {
  Canvas(modifier = modifier.fillMaxSize()) {
    val width = size.width
    val height = size.height

    val bubblePath = Path().apply {
      val radius = 50f

      // Start from top-left
      moveTo(radius, 0f)
      // Top edge
      lineTo(width - radius, 0f)
      quadraticBezierTo(width, 0f, width, radius)
      // Right edge
      lineTo(width, height - radius * 2)
      quadraticBezierTo(width, height - radius, width - radius, height - radius)
      // Bottom bump
      lineTo(width - 40, height - radius)
      quadraticBezierTo(width - 120, height + 50f, width * 0.6f, height - radius)
      // Bottom edge
      lineTo(radius, height - radius)
      quadraticBezierTo(0f, height - radius, 0f, height - radius * 2)
      // Left edge
      lineTo(0f, radius)
      quadraticBezierTo(0f, 0f, radius, 0f)

      close()
    }

    drawPath(path = bubblePath, color = Color(0xFFD01AE9))
  }
}
