package de.malteans.pixlists.lists.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import de.malteans.pixlists.core.presentation.components.CustomTopBar

@Composable
fun LoadingScreen(openDrawer: () -> Unit) {
    Scaffold (
        topBar = {
            CustomTopBar(
                title = {},
                openDrawer = openDrawer
            )
        }
    ) { paddingValues ->
        Box (
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            CircularProgressIndicator(Modifier.align(Alignment.Center))
        }
    }
}