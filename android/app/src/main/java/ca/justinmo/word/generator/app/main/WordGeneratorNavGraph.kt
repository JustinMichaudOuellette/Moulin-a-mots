package ca.justinmo.word.generator.app.main

import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ca.justinmo.word.generator.app.features.about.AboutScreen
import ca.justinmo.word.generator.app.features.exploration.ExplorationScreen
import ca.justinmo.word.generator.app.features.favorites.FavoritesScreen
import ca.justinmo.word.generator.app.features.generator.GeneratorScreen
import ca.justinmo.word.generator.app.features.sessionhistory.SessionHistoryScreen
import ca.justinmo.word.generator.app.features.settings.SettingsScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun WordGeneratorNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed),
    startDestination: String = WordGeneratorDestinations.GENERATOR_ROUTE,
    navActions: WordGeneratorNavigationActions = remember(navController) {
        WordGeneratorNavigationActions(navController)
    },
) {
    val appViewModel: WordGeneratorViewModel = viewModel(factory = WordGeneratorViewModel.Factory)
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
    ) {
        composable(WordGeneratorDestinations.GENERATOR_ROUTE) {
            AppModalDrawer(drawerState, navActions) {
                GeneratorScreen(
                    viewModel = appViewModel,
                    openDrawer = { coroutineScope.launch { drawerState.open() } },
                )
            }
        }
        composable(WordGeneratorDestinations.EXPLORATION_ROUTE) {
            AppModalDrawer(drawerState, navActions) {
                ExplorationScreen(
                    viewModel = appViewModel,
                    openDrawer = { coroutineScope.launch { drawerState.open() } },
                )
            }
        }
        composable(WordGeneratorDestinations.SESSION_HISTORY_ROUTE) {
            AppModalDrawer(drawerState, navActions) {
                SessionHistoryScreen(
                    viewModel = appViewModel,
                    openDrawer = { coroutineScope.launch { drawerState.open() } },
                )
            }
        }
        composable(WordGeneratorDestinations.FAVORITES_ROUTE) {
            AppModalDrawer(drawerState, navActions) {
                FavoritesScreen(
                    viewModel = appViewModel,
                    openDrawer = { coroutineScope.launch { drawerState.open() } },
                )
            }
        }
        composable(WordGeneratorDestinations.SETTINGS_ROUTE) {
            AppModalDrawer(drawerState, navActions) {
                SettingsScreen(
                    viewModel = appViewModel,
                    openDrawer = { coroutineScope.launch { drawerState.open() } },
                )
            }
        }
        composable(WordGeneratorDestinations.ABOUT_ROUTE) {
            AppModalDrawer(drawerState, navActions) {
                AboutScreen(
                    openDrawer = { coroutineScope.launch { drawerState.open() } },
                )
            }
        }
    }
}
