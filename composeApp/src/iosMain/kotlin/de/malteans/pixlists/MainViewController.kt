package de.malteans.pixlists

import androidx.compose.ui.window.ComposeUIViewController
import de.malteans.pixlists.di.initKoin

fun MainViewController() = ComposeUIViewController(
    configure = {
        initKoin()
    }
) { App() }