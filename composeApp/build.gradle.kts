plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.kotlin.compose)

    alias(libs.plugins.jetbrains.kotlin.serialization)

    alias(libs.plugins.ksp)
    alias(libs.plugins.room)

    alias(libs.plugins.aboutLibraries)
}

aboutLibraries {
    export {
        outputFile = file("src/commonMain/composeResources/files/aboutlibraries.json")
        prettyPrint = true
    }
    library {
        // Enable the duplication mode, allows to merge, or link dependencies which relate
        duplicationMode = com.mikepenz.aboutlibraries.plugin.DuplicateMode.MERGE
    }
}

kotlin {
    androidLibrary {
        namespace = "de.malteans.pixlists.composeapp"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()
        withJava()
        androidResources {
            enable = true
        }
    }
    
    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
            binaryOption("bundleId", "de.malteans.pixlists")
        }
    }

    room {
        schemaDirectory("$projectDir/schemas")
    }

    sourceSets {
        
        androidMain.dependencies {
            implementation(projects.dataStore)

            implementation(libs.koin.android)
            implementation(libs.koin.androidx.compose)
        }
        commonMain.dependencies {
            implementation(projects.legal)
            implementation(projects.dataStore)

            implementation(libs.kotlinx.serialization.json)

            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtime.compose)

            implementation(libs.bundles.compose)
            implementation(libs.jetbrains.compose.navigation)

            implementation(libs.compose.material3)
            implementation(libs.compose.material.icons.extended) // More Icons

            // Koin
            api(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
            implementation(libs.koin.compose.viewmodel)

            // Room
            implementation(libs.androidx.room.runtime)
            implementation(libs.sqlite.bundled)

            // Datetime
            implementation(libs.kotlinx.datetime)

            // Reorderable
            implementation(libs.reorderable)

            // About Libraries
            implementation(libs.aboutlibraries.compose.m3)
            
            // FileKit
            implementation(libs.bundles.filekit)

            // Graphs (Vico)
            implementation(libs.bundles.vico)
        }
        iosMain.dependencies {
            implementation(projects.dataStore)
        }
    }
}

dependencies {
    debugImplementation(libs.compose.ui.tooling)
    add("kspAndroid", libs.androidx.room.compiler)
    add("kspIosArm64", libs.androidx.room.compiler)
    add("kspIosSimulatorArm64", libs.androidx.room.compiler)
}
