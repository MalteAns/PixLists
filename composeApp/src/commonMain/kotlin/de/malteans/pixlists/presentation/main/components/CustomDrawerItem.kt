package de.malteans.pixlists.presentation.main.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun CustomDrawerItem(
    icon: @Composable () -> Unit,
    label: String,
    badge: @Composable () -> Unit = {},
    selected: Boolean,
    onClick: () -> Unit,
    colors: NavigationDrawerItemColors = NavigationDrawerItemDefaults.colors(
        selectedContainerColor = MaterialTheme.colorScheme.tertiaryContainer,
        selectedIconColor = MaterialTheme.colorScheme.onTertiaryContainer,
        selectedTextColor = MaterialTheme.colorScheme.onTertiaryContainer,
        selectedBadgeColor = MaterialTheme.colorScheme.onTertiaryContainer,
    ),
    modifier: Modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
) {
    NavigationDrawerItem(
        icon = icon,
        label = { Text(label) },
        badge = badge,
        selected = selected,
        onClick = onClick,
        colors = colors,
        modifier = modifier
    )
}