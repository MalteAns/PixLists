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
import de.malteans.pixlists.lists.presentation.ListScreenRoot
import de.malteans.pixlists.lists.presentation.LoadingScreen
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
    val setCurScreen: (CurScreen) -> Unit = { screen ->
        setCurState(screen, null)
    }
    val setCurList: (Long?) -> Unit = { pixListId ->
        setCurState(CurScreen.LIST, pixListId)
    }

    NavHost(
        navController = navController,
        startDestination = Route.ListNav,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
        popEnterTransition = { EnterTransition.None },
        popExitTransition = { ExitTransition.None },
    ) {
        navigation<Route.ListNav>(
            startDestination = Route.List.View(null)
        ) {
            composable<Route.List.Loading> {
                setCurScreen(CurScreen.LIST)
                LoadingScreen(openDrawer = openDrawer)
            }
            composable<Route.List.View> {
                val args = it.toRoute<Route.List.View>()
                setCurList(args.curPixListId)
                ListScreenRoot(
                    openDrawer = openDrawer,
                    curPixListId = args.curPixListId,
                )
            }
        }
        navigation<Route.ColorsNav>(
            startDestination = Route.Colors.Overview
        ) {
            composable<Route.Colors.Overview> {
                setCurScreen(CurScreen.MANAGE_COLORS)
                ManageColorsScreenRoot(
                    openDrawer = openDrawer
                )
            }
        }
        navigation<Route.SettingsNav>(
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
        navigation<Route.LegalNav>(
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