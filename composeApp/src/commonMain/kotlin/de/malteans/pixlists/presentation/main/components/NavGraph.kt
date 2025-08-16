package de.malteans.pixlists.presentation.main.components

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import de.malteans.pixlists.app.Route
import de.malteans.pixlists.presentation.list.ListScreenRoot
import de.malteans.pixlists.presentation.list.LoadingScreen
import de.malteans.pixlists.presentation.manageColors.ManageColorsScreenRoot

@Composable
fun NavGraph(
    navController: NavHostController,
    openDrawer: () -> Unit,
    setCurScreen: (Screen) -> Unit,
    setPixListId: (Long?) -> Unit,
) {
    NavHost(
        navController = navController,
        startDestination = Route.ListScreen(),
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
        popEnterTransition = { EnterTransition.None },
        popExitTransition = { ExitTransition.None },
    ) {
        // List Screen
        composable<Route.ListScreen> {
            val args = it.toRoute<Route.ListScreen>()
            setCurScreen(Screen.LIST)
            setPixListId(args.curPixListId)
            ListScreenRoot(
                openDrawer = openDrawer,
                curPixListId = args.curPixListId,
            )
        }
        // Manage Colors Screen
        composable<Route.ManageColorsScreen> {
            setCurScreen(Screen.MANAGE_COLORS)
            setPixListId(null)
            ManageColorsScreenRoot(
                openDrawer = openDrawer
            )
        }
        // Loading Screen
        composable<Route.LoadingScreen> {
            setCurScreen(Screen.LIST)
            LoadingScreen(openDrawer = openDrawer)
        }
    }
}