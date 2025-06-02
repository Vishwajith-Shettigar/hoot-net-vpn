package com.example.hoot_net

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.unit.dp
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.hoot_net.data.local.SharedPreferenceHelper
import com.example.hoot_net.screens.SplashScreen
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import javax.inject.Inject

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {

  @get:Rule
  val composeTestRule = createAndroidComposeRule<ComponentActivity>()

  @get:Rule
  val hiltRule = HiltAndroidRule(this)

  @Inject
  lateinit var sharedPreferenceHelper: SharedPreferenceHelper

  @Before
  fun setUp() {
    hiltRule.inject()
  }

  @Test
  fun splashScreen_contents_are_displayed() {
    composeTestRule.setContent {
      SplashScreen(PaddingValues(10.dp), sharedPreferenceHelper) {}
    }
    composeTestRule.onNodeWithText("れっど ふらっぐ").isDisplayed()
  }

  @Test
  fun useAppContext() {
    // Context of the app under test.
    val appContext = InstrumentationRegistry.getInstrumentation().targetContext
    assertEquals("com.example.hoot_net", appContext.packageName)
  }
}