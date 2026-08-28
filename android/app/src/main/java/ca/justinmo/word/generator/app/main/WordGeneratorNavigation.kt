package ca.justinmo.word.generator.app.main

import androidx.navigation.NavHostController
import ca.justinmo.word.generator.app.main.WordGeneratorScreens.ABOUT_SCREEN
import ca.justinmo.word.generator.app.main.WordGeneratorScreens.EXPLORATION_SCREEN
import ca.justinmo.word.generator.app.main.WordGeneratorScreens.FAVORITES_SCREEN
import ca.justinmo.word.generator.app.main.WordGeneratorScreens.GENERATOR_SCREEN
import ca.justinmo.word.generator.app.main.WordGeneratorScreens.SESSION_HISTORY_SCREEN
import ca.justinmo.word.generator.app.main.WordGeneratorScreens.SETTINGS_SCREEN

/**
 * Screens used in [WordGeneratorDestinations]
 */
private object WordGeneratorScreens {
    const val GENERATOR_SCREEN = "generator"
    const val EXPLORATION_SCREEN = "exploration"
    const val FAVORITES_SCREEN = "favorites"
    const val ABOUT_SCREEN = "about"
    const val SESSION_HISTORY_SCREEN = "session_history"
    const val SETTINGS_SCREEN = "settings"
}

/**
 * Destinations used in the [WordGeneratorActivity]
 */
object WordGeneratorDestinations {
    const val ABOUT_ROUTE = ABOUT_SCREEN
    const val GENERATOR_ROUTE = GENERATOR_SCREEN
    const val EXPLORATION_ROUTE = EXPLORATION_SCREEN
    const val FAVORITES_ROUTE = FAVORITES_SCREEN
    const val SESSION_HISTORY_ROUTE = SESSION_HISTORY_SCREEN
    const val SETTINGS_ROUTE = SETTINGS_SCREEN
}

/**
 * Models the navigation actions in the app.
 */
class WordGeneratorNavigationActions(private val navController: NavHostController) {

    fun navigateToGenerator() {
        navController.navigate(GENERATOR_SCREEN) {
            popUpTo(navController.graph.id) {
                inclusive = true
            }
            launchSingleTop = true
        }
    }

    fun navigateToExploration() {
        navController.navigate(EXPLORATION_SCREEN) {
            popUpTo(navController.graph.id) {
                inclusive = true
            }
            launchSingleTop = true
        }
    }

    fun navigateToFavorites() {
        navController.navigate(FAVORITES_SCREEN) {
            popUpTo(navController.graph.id) {
                inclusive = true
            }
            launchSingleTop = true
        }
    }

    fun navigateToAbout() {
        navController.navigate(ABOUT_SCREEN) {
            popUpTo(navController.graph.id) {
                inclusive = true
            }
            launchSingleTop = true
        }
    }

    fun navigateToSessionHistory() {
        navController.navigate(SESSION_HISTORY_SCREEN) {
            popUpTo(navController.graph.id) {
                inclusive = true
            }
            launchSingleTop = true
        }
    }

    fun navigateToSettings() {
        navController.navigate(SETTINGS_SCREEN) {
            popUpTo(navController.graph.id) {
                inclusive = true
            }
            launchSingleTop = true
        }
    }
}
