package de.malteans.pixlists.presentation.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Download
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.malteans.legal.presentation.components.LegalsList
import de.malteans.legal.presentation.navigation.LegalRoute
import de.malteans.pixlists.navigation.Route
import de.malteans.pixlists.presentation.components.CustomTopBar
import de.malteans.pixlists.presentation.components.SnackbarManager
import de.malteans.pixlists.presentation.settings.components.SettingsItem
import de.malteans.pixlists.presentation.theme.containerColor
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.openFilePicker
import io.github.vinceglb.filekit.dialogs.openFileSaver
import io.github.vinceglb.filekit.readString
import io.github.vinceglb.filekit.writeString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.json.Json
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import pixlists.composeapp.generated.resources.Res
import pixlists.composeapp.generated.resources.settings
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@Composable
fun SettingsScreenRoot(
    viewModel: SettingsViewModel = koinViewModel(),
    onNavigateTo: (Route) -> Unit,
    onNavigateToLegalRoute: (LegalRoute) -> Unit,
    openDrawer: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    SettingsScreen(
        state = state,
        onAction = { action ->
            when (action) {
                is SettingsAction.OpenDrawer -> openDrawer()
                is SettingsAction.OnNavigateTo -> onNavigateTo(action.route)
                is SettingsAction.OnNavigateToLegalRoute -> onNavigateToLegalRoute(action.legalRoute)
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
    LaunchedEffect(state.exportData) {
        val currentLocalDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        if (state.exportData != null) {
            val file = FileKit.openFileSaver(
                suggestedName = "pixlists-export_${currentLocalDate.year}-${currentLocalDate.month.number}-${currentLocalDate.day}",
                extension = "json",
            )
            file?.writeString(Json.encodeToString(state.exportData))
        }
    }

    // Import -------------------------------------------------------------------------------------
    LaunchedEffect(state.importError) {
        if (state.importError != null) {
            scope.launch(Dispatchers.IO) {
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
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(top = 16.dp)
                .padding(horizontal = 8.dp)
                .padding(paddingValues)
        ) {
            Column(
                verticalArrangement = spacedBy(4.dp),
                modifier = Modifier.clip(MaterialTheme.shapes.medium)
            ) {
                SettingsItem(
                    title = "Export Data",
                    description = "Export your data as JSON file",
                    icon = Icons.Outlined.Download,
                    onClick = { onAction(SettingsAction.OnExportData) },
                    modifier = Modifier.fillMaxWidth()
                )
                SettingsItem(
                    title = "Import Data",
                    description = "Import exported data from JSON file",
                    icon = Icons.Outlined.Upload,
                    onClick = {
                        scope.launch(Dispatchers.Main) {
                            val file = FileKit.openFilePicker(type = FileKitType.File("application/json"))
                                ?: return@launch
                            val jsonData = Json.parseToJsonElement(file.readString())
                            onAction(SettingsAction.OnImportData(jsonData))
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Spacer(Modifier.height(16.dp))
            LegalsList(
                tileContainerColor = MaterialTheme.colorScheme.containerColor,
                navigateToLegalScreen = { legalRoute ->
                    onAction(SettingsAction.OnNavigateToLegalRoute(legalRoute))
                },
                modifier = Modifier.clip(MaterialTheme.shapes.medium)
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