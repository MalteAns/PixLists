plugins {
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.kotlinAndroid) apply false
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.compose.multiplatform) apply false
    alias(libs.plugins.compose.compiler) apply false

    alias(libs.plugins.jetbrains.kotlin.serialization) apply false

    alias(libs.plugins.android.kotlin.multiplatform.library) apply false
    alias(libs.plugins.aboutLibraries) apply false
}

repositories {
    mavenCentral()
}
