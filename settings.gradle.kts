import java.io.FileInputStream
import java.util.Properties

pluginManagement {
  repositories {

    google()
    gradlePluginPortal()
    mavenCentral()
  }

}


// Load the secret.properties file
val secretPropertiesFile =  File(rootDir, "secrets.properties") // Use new File() for settings.gradle
val secretProperties =  Properties()

if (secretPropertiesFile.exists()) {
  secretProperties.load( FileInputStream(secretPropertiesFile))
}


dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "hoot-net"
include(":app")
 