package ca.justinmo.word.generator.app.features.sessionhistory

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import ca.justinmo.word.generator.app.R
import ca.justinmo.word.generator.app.ui.MainTopAppBar
import ca.justinmo.word.generator.app.ui.WordBottomSheet
import ca.justinmo.word.generator.app.ui.WordRow
import ca.justinmo.word.generator.app.data.WordInfo
import ca.justinmo.word.generator.app.main.WordGeneratorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionHistoryScreen(
    viewModel: WordGeneratorViewModel,
    openDrawer: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val bottomSheetWord = remember {
        mutableStateOf<WordInfo?>(null)
    }
    val sheetState = rememberModalBottomSheetState()
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = { MainTopAppBar(R.string.session_history_title, openDrawer) }
    ) { paddingValues ->
        SessionHistoryContent(
            items = viewModel.sessionHistory,
            favorites = viewModel.sessionFavorites,
            viewModel = viewModel,
            bottomSheetWord = bottomSheetWord,
            modifier = Modifier.padding(paddingValues)
        )
        bottomSheetWord.value?.let { word ->
            WordBottomSheet(bottomSheetWord, word, sheetState, viewModel)
        }
    }
}

@Composable
fun SessionHistoryContent(
    items: List<String>,
    favorites: Set<String>,
    viewModel: WordGeneratorViewModel,
    bottomSheetWord: MutableState<WordInfo?>,
    modifier: Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = dimensionResource(id = R.dimen.horizontal_margin))
    ) {
        LazyColumn {
            items(items.size) { i ->
                val wordInfo = WordInfo(items[i], favorites.contains(items[i]))
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