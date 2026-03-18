package de.malteans.pixlists.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.mikepenz.aboutlibraries.ui.compose.produceLibraries
import de.malteans.legal.presentation.navigation.LegalRoute
import de.malteans.legal.presentation.screens.ImprintScreen
import de.malteans.legal.presentation.screens.LicensesScreen
import de.malteans.legal.presentation.screens.PrivacyScreen
import de.malteans.pixlists.colors.presentation.ManageColorsScreenRoot
import de.malteans.pixlists.core.presentation.main.components.CurScreen
import de.malteans.pixlists.dashboard.presentation.DashboardScreenRoot
import de.malteans.pixlists.lists.presentation.stats.ListStatsScreenRoot
import de.malteans.pixlists.lists.presentation.view.ListViewScreenRoot
import de.malteans.pixlists.lists.presentation.view.LoadingListViewScreen
import de.malteans.pixlists.settings.presentation.SettingsScreenRoot
import org.jetbrains.compose.resources.stringResource
import pixlists.composeapp.generated.resources.Res
import pixlists.composeapp.generated.resources.privacy_policy_path

@Composable
fun NavGraph(
    navController: NavHostController,
    openDrawer: () -> Unit,
    setCurState: (CurScreen, Long?) -> Unit,
) {
    fun setCurScreen(screen: CurScreen, listId: Long? = null) {
        setCurState(screen, listId)
    }

    NavHost(
        navController = navController,
        startDestination = Route.Dashboard,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
        popEnterTransition = { EnterTransition.None },
        popExitTransition = { ExitTransition.None },
    ) {
        navigation<Route.Dashboard>(
            startDestination = Route.Dashboard.View
        ) {
            composable<Route.Dashboard.View> {
                setCurScreen(CurScreen.DASHBOARD)
                DashboardScreenRoot(
                    openDrawer = openDrawer,
                    openList = { listId -> navController.navigate(Route.List.View(listId)) }
                )
            }
        }
        navigation<Route.List>(
            startDestination = Route.List.View(null)
        ) {
            composable<Route.List.Loading> {
                setCurScreen(CurScreen.LIST)
                LoadingListViewScreen(openDrawer = openDrawer)
            }
            composable<Route.List.View> {
                val args = it.toRoute<Route.List.View>()
                setCurScreen(CurScreen.LIST, args.curPixListId)
                ListViewScreenRoot(
                    curPixListId = args.curPixListId,
                    openDrawer = openDrawer,
                    openStats = { args.curPixListId?.let { listId ->
                        navController.navigate(Route.List.Stats(listId))
                    } }
                )
            }
            composable<Route.List.Stats> {
                val args = it.toRoute<Route.List.Stats>()
                setCurScreen(CurScreen.LIST, args.listId)
                ListStatsScreenRoot(
                    listId = args.listId,
                    navigateBack = navController::popBackStack,
                )
            }
        }
        navigation<Route.Colors>(
            startDestination = Route.Colors.Overview
        ) {
            composable<Route.Colors.Overview> {
                setCurScreen(CurScreen.MANAGE_COLORS)
                ManageColorsScreenRoot(
                    openDrawer = openDrawer
                )
            }
        }
        navigation<Route.Settings>(
            startDestination = Route.Settings.Overview
        ) {
            composable<Route.Settings.Overview> (
                popEnterTransition = { slideInHorizontally { -it } },
                exitTransition = { slideOutHorizontally { -it } },
            ) {
                setCurScreen(CurScreen.SETTINGS)
                SettingsScreenRoot(
                    openDrawer = openDrawer,
                    onNavigateTo = navController::navigate,
                    onNavigateToLegalRoute = navController::navigate,
                )
            }
        }
        navigation<Route.Legal>(
            startDestination = LegalRoute.Imprint,
            enterTransition = { slideInHorizontally { it } },
            popExitTransition = { slideOutHorizontally { it } },
        ) {
            composable<LegalRoute.Imprint> {
                setCurScreen(CurScreen.LEGALS)
                ImprintScreen(
                    navigateBack = navController::popBackStack,
                )
            }
            composable<LegalRoute.Privacy> {
                setCurScreen(CurScreen.LEGALS)
                var htmlData by remember { mutableStateOf<String?>(null) }
                val privacyPath = stringResource(Res.string.privacy_policy_path)
                LaunchedEffect(Unit) {
                    htmlData = Res.readBytes(privacyPath).decodeToString()
                }
                PrivacyScreen(
                    htmlData = htmlData,
                    navigateBack = navController::popBackStack,
                )
            }
            composable<LegalRoute.Licenses> {
                setCurScreen(CurScreen.LEGALS)
                val libraries by produceLibraries {
                    Res.readBytes("files/aboutlibraries.json").decodeToString()
                }
                LicensesScreen(
                    libraries = libraries,
                    navigateBack = navController::popBackStack,
                )
            }
        }
    }
}