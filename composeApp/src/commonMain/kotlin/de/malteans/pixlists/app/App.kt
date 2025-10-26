package de.malteans.pixlists.app

import androidx.compose.runtime.Composable
import de.malteans.pixlists.presentation.main.MainScreen
import de.malteans.pixlists.presentation.theme.PixListsTheme

@Composable
fun App() {
    PixListsTheme {
        MainScreen()
    }
}