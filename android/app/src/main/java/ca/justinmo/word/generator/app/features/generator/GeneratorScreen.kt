package ca.justinmo.word.generator.app.features.generator

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ca.justinmo.word.generator.app.R
import ca.justinmo.word.generator.app.ui.MainTopAppBar
import ca.justinmo.word.generator.app.ui.VoiceoverButton
import ca.justinmo.word.generator.app.ui.GeneratorUiState
import ca.justinmo.word.generator.app.data.WordInfo
import ca.justinmo.word.generator.app.main.WordGeneratorViewModel

@Composable
fun GeneratorScreen(
    viewModel: WordGeneratorViewModel,
    openDrawer: () -> Unit,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    viewModel.initialize(LocalContext.current)

    DisposableEffect(Unit) {
        onDispose {
            viewModel.pauseAutoPlay()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            MainTopAppBar(
                openDrawer = openDrawer,
                title = R.string.random_word,
            )
        },
    ) { paddingValues ->
        val uiState by viewModel.generatorUiState.collectAsStateWithLifecycle()
        val word = uiState.word
        if (word != null) {
            GeneratorScreenContent(
                modifier = Modifier.padding(paddingValues),
                onClickFavorite = {
                    viewModel.onClickFavorite(word.word, word.isFavorite)
                },
                onClickVoiceover = {
                    viewModel.readWord(word.word)
                },
                onClickPlay = {
                    viewModel.onClickPlay(word)
                },
                onClickRefresh = {
                    viewModel.loadNextWord()
                },
                uiState = uiState,
            )
        }
    }
}

@Composable
private fun GeneratorScreenContent(
    modifier: Modifier,
    onClickFavorite: () -> Unit,
    onClickVoiceover: () -> Unit,
    onClickPlay: () -> Unit,
    onClickRefresh: () -> Unit,
    uiState: GeneratorUiState,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = dimensionResource(id = R.dimen.horizontal_margin)),
    ) {
        uiState.word?.let { word ->
            Row(modifier = Modifier.weight(1.0f, fill = true),
                verticalAlignment = Alignment.CenterVertically) {
                    GeneratedContent(word)
            }
        }
        Row(
            horizontalArrangement = Arrangement.SpaceAround,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp)
        ) {
            FavoriteButton(uiState.word?.isFavorite, onClickFavorite, modifier = Modifier.size(64.dp))
            VoiceoverButton(onClickVoiceover, modifier = Modifier.size(64.dp), iconSize = 32.dp)
            PlayButton(uiState.isPlaying, onClickPlay, modifier = Modifier.size(64.dp))
            RefreshButton(onClickRefresh, modifier = Modifier.size(64.dp))
        }
    }
}

@Composable
private fun GeneratedContent(word: WordInfo) {
    Text(
        text = word.word,
        style = MaterialTheme.typography.headlineSmall,
        maxLines = 1,
        autoSize = TextAutoSize.StepBased(minFontSize = 12.sp, maxFontSize = 112.sp),
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth(),
    )
}

@Composable
private fun FavoriteButton(isFavorite: Boolean?, onClickFavorite: () -> Unit, modifier: Modifier = Modifier) {
    IconButton(onClick = onClickFavorite, modifier = modifier) {
        Icon(
            imageVector = ImageVector.vectorResource(
                if (isFavorite == true)
                    R.drawable.baseline_favorite_24
                else R.drawable.baseline_favorite_border_24),
            stringResource(id = R.string.open_drawer),
            modifier = Modifier.size(32.dp))
    }
}

@Composable
private fun PlayButton(isPlaying: Boolean, onClickPlay: () -> Unit, modifier: Modifier = Modifier) {
    IconButton(onClick = onClickPlay, modifier = modifier) {
        Icon(
            imageVector = if (isPlaying) ImageVector.vectorResource(R.drawable.baseline_stop_24) else Icons.Filled.PlayArrow,
            stringResource(id = R.string.open_drawer),
            modifier = Modifier.size(32.dp))
    }
}

@Composable
private fun RefreshButton(onClickRefresh: () -> Unit, modifier: Modifier = Modifier) {
    IconButton(onClick = onClickRefresh, modifier = modifier) {
        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.baseline_refresh_24),
            stringResource(id = R.string.open_drawer),
            modifier = Modifier.size(32.dp))
    }
}

@Preview
@Composable
private fun GeneratorScreenPreview() {
    Surface(modifier = Modifier.height(300.dp).width(200.dp)) {
        GeneratorScreenContent(
            modifier = Modifier,
            onClickFavorite = {},
            onClickVoiceover = {},
            onClickPlay = {},
            onClickRefresh = {},
            uiState = GeneratorUiState(word = WordInfo("Fraison", isFavorite = false), isPlaying = false),
        )
    }
}

@Preview
@Composable
private fun FavoriteButtonFavoritePreview() {
    Surface {
        FavoriteButton(
            onClickFavorite = {},
            isFavorite = true,
        )
    }
}

@Preview
@Composable
private fun PlayButtonPlayingPreview() {
    Surface {
        PlayButton(isPlaying = true, onClickPlay = {})
    }
}