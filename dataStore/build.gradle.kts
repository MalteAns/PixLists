plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)
}

kotlin {

    // Target declarations - add or remove as needed below. These define
    // which platforms this KMP module supports.
    // See: https://kotlinlang.org/docs/multiplatform-discover-project.html#targets
    androidLibrary {
        namespace = "de.malteans.datastore"
        compileSdk = 36
        minSdk = 30
    }

    jvm("desktop")

    // For iOS targets, this is also where you should
    // configure native binary output. For more information, see:
    // https://kotlinlang.org/docs/multiplatform-build-native-binaries.html#build-xcframeworks

    // A step-by-step guide on how to include this library in an XCode
    // project can be found here:
    // https://developer.android.com/kotlin/multiplatform/migrate
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "dataStoreKit"
            isStatic = true
        }
    }

    sourceSets {
        val desktopMain by getting

        androidMain {
            dependencies {
                implementation(libs.compose.ui.tooling.preview)
                implementation(libs.androidx.activity.compose)
            }
        }

        commonMain {
            dependencies {
                implementation(libs.kotlin.stdlib)

                implementation(libs.bundles.compose)
                implementation(libs.compose.material3)

                api(libs.datastore)
                api(libs.datastore.preferences)
            }
        }

        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
        }

        iosMain {
            dependencies {
            }
        }
    }
}