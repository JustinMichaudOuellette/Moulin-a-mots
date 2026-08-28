package ca.justinmo.word.generator.app.main

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.DrawerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ca.justinmo.word.generator.app.R
import ca.justinmo.word.generator.app.ui.OptionRow
import ca.justinmo.word.generator.app.ui.WordGeneratorTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun AppModalDrawer(
    drawerState: DrawerState,
    navigationActions: WordGeneratorNavigationActions,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    BackHandler(enabled = true) {
        if (drawerState.isClosed) {
            coroutineScope.launch { drawerState.open() }
        } else {
            (context as? Activity)?.finish()
        }
    }
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppDrawer(
                navigateToGenerator = { navigationActions.navigateToGenerator() },
                navigateToExploration = { navigationActions.navigateToExploration() },
                navigateSessionHistory = { navigationActions.navigateToSessionHistory() },
                navigateToFavorites = { navigationActions.navigateToFavorites() },
                navigateToAbout = { navigationActions.navigateToAbout() },
                navigateToSettings = { navigationActions.navigateToSettings() },
                closeDrawer = { coroutineScope.launch { drawerState.close() } }
            )
        }
    ) {
        content()
    }
}

@Composable
private fun AppDrawer(
    navigateToGenerator: () -> Unit,
    navigateToExploration: () -> Unit,
    navigateSessionHistory: () -> Unit,
    navigateToFavorites: () -> Unit,
    navigateToAbout: () -> Unit,
    navigateToSettings: () -> Unit,
    closeDrawer: () -> Unit,
    modifier: Modifier = Modifier
) {
    ModalDrawerSheet(
        modifier = modifier,
        drawerContainerColor = MaterialTheme.colorScheme.background,
        windowInsets = WindowInsets.safeDrawing
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .verticalScroll(rememberScrollState())
        ) {
            DrawerHeader()
            OptionRow(
                painter = painterResource(id = R.drawable.baseline_refresh_24),
                label = stringResource(id = R.string.random_word),
                action = {
                    navigateToGenerator()
                    closeDrawer()
                }
            )
            OptionRow(
                painter = rememberVectorPainter(Icons.Default.Search),
                label = stringResource(id = R.string.exploration_title),
                action = {
                    navigateToExploration()
                    closeDrawer()
                }
            )
            OptionRow(
                painter = painterResource(id = R.drawable.baseline_history_24),
                label = stringResource(id = R.string.session_history_title),
                action = {
                    navigateSessionHistory()
                    closeDrawer()
                }
            )
            OptionRow(
                painter = painterResource(id = R.drawable.baseline_favorite_border_24),
                label = stringResource(id = R.string.favorites_title),
                action = {
                    navigateToFavorites()
                    closeDrawer()
                }
            )
            OptionRow(
                painter = rememberVectorPainter(Icons.Outlined.Settings),
                label = stringResource(id = R.string.settings_title),
                action = {
                    navigateToSettings()
                    closeDrawer()
                }
            )
            OptionRow(
                painter = painterResource(id = R.drawable.baseline_info_outline_24),
                label = stringResource(id = R.string.about_title),
                action = {
                    navigateToAbout()
                    closeDrawer()
                }
            )
        }
    }
}

@Composable
private fun DrawerHeader(
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 32.dp)
    ) {
        Text(
            text = stringResource(id = R.string.app_name),
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            autoSize = TextAutoSize.StepBased(minFontSize = 12.sp, maxFontSize = 112.sp),
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.ExtraLight),
        )
    }
}

@Preview("Drawer contents")
@Composable
fun PreviewAppDrawer() {
    WordGeneratorTheme {
        Surface(modifier = Modifier.height(300.dp).width(300.dp)) {
            AppDrawer(
                navigateToGenerator = {},
                navigateToExploration = {},
                navigateSessionHistory = {},
                navigateToFavorites = {},
                navigateToAbout = {},
                navigateToSettings = {},
                closeDrawer = {}
            )
        }
    }
}
