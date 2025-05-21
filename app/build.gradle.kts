plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.kotlin.android)
  alias(libs.plugins.kotlin.compose)
  id("com.google.devtools.ksp")
  id("kotlin-kapt")
  id("com.google.dagger.hilt.android")
  id ("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")


}

android {
  namespace = "com.example.hoot_net"
  compileSdk = 35

  defaultConfig {
    applicationId = "com.example.hoot_net"
    minSdk = 24
    targetSdk = 35
    versionCode = 1
    versionName = "1.0"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  buildTypes {
    release {
      isMinifyEnabled = false
      proguardFiles(
        getDefaultProguardFile("proguard-android-optimize.txt"),
        "proguard-rules.pro"
      )
    }
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
  }
  kotlinOptions {
    jvmTarget = "17"
  }
  buildFeatures {
    compose = true
    buildConfig = true

  }
  secrets {
    propertiesFileName = "secrets.properties"
    defaultPropertiesFileName = "local.defaults.properties"

    // Configure which keys should be ignored by the plugin by providing regular expressions.
    // "sdk.dir" is ignored by default.
    ignoreList.add("keyToIgnore") // Ignore the key "keyToIgnore"
    ignoreList.add("sdk.*")       // Ignore all keys matching the regexp "sdk.*"
  }
}
dependencies {

  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.lifecycle.runtime.ktx)
  implementation(libs.androidx.activity.compose)
  implementation(platform(libs.androidx.compose.bom))
  implementation(libs.androidx.ui)
  implementation(libs.androidx.ui.graphics)
  implementation(libs.androidx.ui.tooling.preview)
  implementation(libs.androidx.material3)
  implementation(libs.androidx.room.common.jvm)
  testImplementation(libs.junit)
  androidTestImplementation(libs.androidx.junit)
  androidTestImplementation(libs.androidx.espresso.core)
  androidTestImplementation(platform(libs.androidx.compose.bom))
  androidTestImplementation(libs.androidx.ui.test.junit4)
  debugImplementation(libs.androidx.ui.tooling)
  debugImplementation(libs.androidx.ui.test.manifest)
  implementation("com.wireguard.android:tunnel:1.0.20230706")

  implementation ("com.google.devtools.ksp:symbol-processing-api:2.1.21-2.0.1")


  kapt(libs.hilt.android.compiler)
  implementation(libs.hilt.android )

  implementation(libs.retrofit)
  implementation(libs.gson)
  implementation ("com.squareup.retrofit2:converter-gson:3.0.0")

  val room_version = "2.6.0"
  implementation(libs.androidx.room.runtime)
  implementation(libs.androidx.room.ktx)
  ksp("androidx.room:room-compiler:$room_version")
  implementation(libs.gson)


}