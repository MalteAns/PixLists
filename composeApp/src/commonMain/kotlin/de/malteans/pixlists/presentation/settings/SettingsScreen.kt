package de.malteans.pixlists.presentation.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import de.malteans.pixlists.app.Route
import de.malteans.pixlists.presentation.components.CustomTopBar
import de.malteans.pixlists.presentation.settings.components.SettingsItem
import org.jetbrains.compose.resources.stringResource
import pixlists.composeapp.generated.resources.Res
import pixlists.composeapp.generated.resources.settings

@Composable
fun SettingsScreenRoot(
    onNavigateTo: (Route) -> Unit,
    openDrawer: () -> Unit
) {
    SettingsScreen(
        onAction = { action ->
            when (action) {
                is SettingsAction.OpenDrawer -> openDrawer()
                is SettingsAction.OnNavigateTo -> onNavigateTo(action.route)
            }
        }
    )
}

@Composable
fun SettingsScreen(
    onAction: (SettingsAction) -> Unit,
) {
    Scaffold(
        topBar = {
            CustomTopBar(
                title = { Text(stringResource(Res.string.settings)) },
                openDrawer = { onAction(SettingsAction.OpenDrawer) },
                actions = {}
            )
        },
    ) { pad ->
        Column(
            modifier = Modifier
                .padding(pad)
        ) {
            SettingsItem(
                title = "Licenses",
                subtitle = "Open source licenses used in PixLists",
                onClick = { onAction(SettingsAction.OnNavigateTo(Route.LicensesScreen)) },
                trailingIcon = Icons.Outlined.Info,
            )
        }
    }
}