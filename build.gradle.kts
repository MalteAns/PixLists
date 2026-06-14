plugins {
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.compose.multiplatform) apply false
    alias(libs.plugins.kotlin.compose) apply false

    alias(libs.plugins.jetbrains.kotlin.serialization) apply false

    // Only Android (androidApp)
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.android.application) apply false

    alias(libs.plugins.android.kotlin.multiplatform.library) apply false
    alias(libs.plugins.aboutLibraries) apply false
}

repositories {
    mavenCentral()
}
