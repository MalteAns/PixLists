package de.malteans.pixlists.core.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import org.jetbrains.compose.resources.painterResource
import pixlists.composeapp.generated.resources.Res
import pixlists.composeapp.generated.resources.app_icon

@Composable
fun CustomTopBar(
    title: String,
    openDrawer: () -> Unit,
    navigationIcon: @Composable () -> Unit = {
        IconButton(openDrawer) {
            Surface(
                shape = RoundedCornerShape(50),
                color = MaterialTheme.colorScheme.surface,
            ) {
                Image(
                    painter = painterResource(Res.drawable.app_icon),
                    contentDescription = "Menu",
                    contentScale = ContentScale.Inside,
                )
            }
        }
    },
    actions: @Composable RowScope.() -> Unit = {},
    colors: TopAppBarColors = TopAppBarDefaults.topAppBarColors(
        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
        titleContentColor = MaterialTheme.colorScheme.onPrimary,
        actionIconContentColor = MaterialTheme.colorScheme.onPrimary,
    ),
    modifier: Modifier = Modifier
        .clip(
            MaterialTheme.shapes.extraLarge.copy(
                topStart = CornerSize(0),
                topEnd = CornerSize(0),
            )
        )
) {
    CustomTopBar(
        title = { Text(text = title) },
        openDrawer = openDrawer,
        navigationIcon = navigationIcon,
        actions = actions,
        colors = colors,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTopBar(
    title: @Composable () -> Unit,
    openDrawer: () -> Unit,
    navigationIcon: @Composable () -> Unit = {
        IconButton(openDrawer) {
            Surface(
                shape = RoundedCornerShape(50),
                color = MaterialTheme.colorScheme.surface,
            ) {
                Image(
                    painter = painterResource(Res.drawable.app_icon),
                    contentDescription = "Menu",
                    contentScale = ContentScale.Inside,
                )
            }
        }
    },
    actions: @Composable RowScope.() -> Unit = {},
    colors: TopAppBarColors = TopAppBarDefaults.topAppBarColors(
        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
        titleContentColor = MaterialTheme.colorScheme.onPrimary,
        actionIconContentColor = MaterialTheme.colorScheme.onPrimary,
    ),
    modifier: Modifier = Modifier
        .clip(
            MaterialTheme.shapes.extraLarge.copy(
                topStart = CornerSize(0),
                topEnd = CornerSize(0),
            )
        )
) {
    CenterAlignedTopAppBar(
        title = title,
        actions = actions,
        navigationIcon = navigationIcon,
        colors = colors,
        modifier = modifier,
    )
}