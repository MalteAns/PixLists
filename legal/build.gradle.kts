plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)

    alias(libs.plugins.jetbrains.kotlin.serialization)
}

kotlin {
    androidLibrary {
        namespace = "de.malteans.legal"
        compileSdk = 36
        minSdk = 30

        experimentalProperties["android.experimental.kmp.enableAndroidResources"] = true
    }

    jvm("desktop")

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "legalKit"
            isStatic = true
        }
    }

    sourceSets {
        val desktopMain by getting
        commonMain {
            dependencies {
                implementation(libs.kotlin.stdlib)

                implementation(libs.bundles.compose)
                implementation(libs.compose.material3)
                implementation(libs.compose.material.icons.extended) // More Icons

                // Back Handler
                implementation(libs.ui.backhandler)

                // About Libraries
                implementation(libs.aboutlibraries.compose.core)
                implementation(libs.aboutlibraries.compose.m3)

                // WebView
                api("io.github.kevinnzou:compose-webview-multiplatform:2.0.3")
            }
        }

        androidMain {
            dependencies {
                implementation(libs.compose.ui.tooling.preview)
                implementation(libs.androidx.activity.compose)
            }
        }

        desktopMain.dependencies {

        }

        iosMain {
            dependencies {

            }
        }
    }
}