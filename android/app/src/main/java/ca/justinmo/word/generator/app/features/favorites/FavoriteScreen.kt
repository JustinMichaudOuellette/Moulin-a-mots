package ca.justinmo.word.generator.app.features.favorites

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import ca.justinmo.word.generator.app.R
import ca.justinmo.word.generator.app.ui.MainTopAppBar
import ca.justinmo.word.generator.app.ui.WordBottomSheet
import ca.justinmo.word.generator.app.ui.WordRow
import ca.justinmo.word.generator.app.data.WordInfo
import ca.justinmo.word.generator.app.main.WordGeneratorViewModel
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    openDrawer: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: WordGeneratorViewModel,
) {
    val bottomSheetWord = remember {
        mutableStateOf<WordInfo?>(null)
    }
    val sheetState = rememberModalBottomSheetState()
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = { MainTopAppBar(R.string.favorites_title, openDrawer) }
    ) { paddingValues ->
        FavoritesContent(
            favorites = viewModel.favorites(),
            viewModel,
            bottomSheetWord,
            modifier = Modifier.padding(paddingValues)
        )
        bottomSheetWord.value?.let { word ->
            WordBottomSheet(bottomSheetWord, word, sheetState, viewModel)
        }
    }
}

@Composable
fun FavoritesContent(
    favorites: Flow<List<WordInfo>>,
    viewModel: WordGeneratorViewModel,
    bottomSheetWord: MutableState<WordInfo?>,
    modifier: Modifier,
) {
    val favoriteState = favorites.collectAsState(initial = emptyList())
    if (favoriteState.value.isEmpty()) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(id = R.string.no_favorites),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    } else {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = dimensionResource(id = R.dimen.horizontal_margin))
        ) {
            LazyColumn {
                items(favoriteState.value.size) { i ->
                    val wordInfo = favoriteState.value[i]
                    WordRow(
                        wordInfo = wordInfo,
                        onClickVoiceover = {
                            viewModel.readWord(wordInfo.word)
                        },
                        onClickMore = {
                            bottomSheetWord.value = wordInfo
                        },
                    )
                }
            }
        }
    }
}