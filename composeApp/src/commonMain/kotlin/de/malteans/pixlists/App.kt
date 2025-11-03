package de.malteans.pixlists

import androidx.compose.runtime.Composable
import de.malteans.pixlists.core.presentation.main.MainScreen
import de.malteans.pixlists.core.presentation.theme.PixListsTheme

@Composable
fun App() {
    PixListsTheme {
        MainScreen()
    }
}