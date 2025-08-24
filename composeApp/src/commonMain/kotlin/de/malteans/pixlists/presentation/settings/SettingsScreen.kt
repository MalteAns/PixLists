package de.malteans.pixlists.presentation.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Upload
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.malteans.pixlists.app.Route
import de.malteans.pixlists.presentation.components.CustomTopBar
import de.malteans.pixlists.presentation.components.SnackbarManager
import de.malteans.pixlists.presentation.settings.components.SettingsItem
import de.malteans.pixlists.presentation.settings.components.rememberExportJsonLauncher
import de.malteans.pixlists.presentation.settings.components.rememberImportJsonLauncher
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import pixlists.composeapp.generated.resources.Res
import pixlists.composeapp.generated.resources.export_failed
import pixlists.composeapp.generated.resources.export_success
import pixlists.composeapp.generated.resources.import_failed
import pixlists.composeapp.generated.resources.import_success
import pixlists.composeapp.generated.resources.settings
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@Composable
fun SettingsScreenRoot(
    viewModel: SettingsViewModel = koinViewModel(),
    onNavigateTo: (Route) -> Unit,
    openDrawer: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    SettingsScreen(
        state = state,
        onAction = { action ->
            when (action) {
                is SettingsAction.OpenDrawer -> openDrawer()
                is SettingsAction.OnNavigateTo -> onNavigateTo(action.route)
                else -> viewModel.onAction(action)
            }
        },
    )
}

@OptIn(ExperimentalTime::class)
@Composable
fun SettingsScreen(
    state: SettingState,
    onAction: (SettingsAction) -> Unit,
) {
    val scope = rememberCoroutineScope()

    // Export -------------------------------------------------------------------------------------
    val currentLocalDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    val exportSuccess = stringResource(Res.string.export_success)
    val exportFailed = stringResource(Res.string.export_failed)
    val launchExport = rememberExportJsonLauncher(
        suggestedFileName = "pixlists-export_${currentLocalDate.year}-${currentLocalDate.month.number}-${currentLocalDate.day}.json",
        getData = {
            state.exportData ?: throw IllegalStateException("Export data is null")
        },
    ) { success ->
        onAction(SettingsAction.ResetExportData)
        scope.launch {
            SnackbarManager.showSnackbar(
                message = if (success) exportSuccess else exportFailed,
                withDismissAction = true,
            )
        }
    }

    LaunchedEffect(state.exportData) {
        if (state.exportData != null) {
            launchExport(state.exportData)
        }
    }

    // Import -------------------------------------------------------------------------------------
    val importSuccess = stringResource(Res.string.import_success)
    val importFailed = stringResource(Res.string.import_failed)
    val launchImport = rememberImportJsonLauncher { result ->
        scope.launch {
            result
                .onSuccess { data ->
                    onAction(SettingsAction.OnImportData(data))
                    SnackbarManager.showSnackbar(
                        message = importSuccess
                    )
                }
                .onFailure {
                    SnackbarManager.showSnackbar(
                        message = importFailed
                    )
                }
        }
    }

    LaunchedEffect(state.importError) {
        if (state.importError != null) {
            scope.launch {
                SnackbarManager.showSnackbar(
                    message = state.importError
                )
                onAction(SettingsAction.ClearImportError)
            }
        }
    }

    // Main Content -------------------------------------------------------------------------------
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
                title = "Export Data",
                subtitle = "Export your data as JSON file",
                onClick = { onAction(SettingsAction.OnExportData) },
                trailingIcon = Icons.Outlined.Download,
            )
            SettingsItem(
                title = "Import Data",
                subtitle = "Import exported data from JSON file",
                onClick = { launchImport() },
                trailingIcon = Icons.Outlined.Upload,
            )
            SettingsItem(
                title = "Licenses",
                subtitle = "Open source licenses used in PixLists",
                onClick = { onAction(SettingsAction.OnNavigateTo(Route.LicensesScreen)) },
                trailingIcon = Icons.Outlined.Info,
            )
        }
    }

    // Loading ------------------------------------------------------------------------------------
    AnimatedVisibility(
        visible = state.isLoading,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background.copy(alpha = 0.6f)),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}