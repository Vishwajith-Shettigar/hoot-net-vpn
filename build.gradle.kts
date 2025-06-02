// Top-level build file where you can add configuration options common to all sub-projects/modules.


buildscript {
  repositories {
    google()
    mavenCentral()
  }

  dependencies{
    classpath("com.android.tools.build:gradle:7.4.2")
    classpath("com.google.gms:google-services:4.4.2")
    classpath ("com.google.android.libraries.mapsplatform.secrets-gradle-plugin:secrets-gradle-plugin:2.0.1")

  }

}

plugins {
  alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
  id("com.google.devtools.ksp") version "2.1.21-2.0.1" apply false
  id("com.google.dagger.hilt.android") version "2.56.2" apply false
  id ("com.google.gms.google-services") version "4.4.2" apply false
  id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin") version "2.0.1" apply false

    id ("org.jetbrains.kotlin.plugin.serialization") version "1.8.20" apply false
}