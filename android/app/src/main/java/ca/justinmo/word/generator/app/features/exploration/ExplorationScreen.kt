package ca.justinmo.word.generator.app.features.exploration

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ca.justinmo.word.generator.app.R
import ca.justinmo.word.generator.app.ui.Black
import ca.justinmo.word.generator.app.ui.ConstraintType
import ca.justinmo.word.generator.app.ui.ExplorationUiState
import ca.justinmo.word.generator.app.ui.MediumGrey
import ca.justinmo.word.generator.app.ui.MainTopAppBar
import ca.justinmo.word.generator.app.ui.WordBottomSheet
import ca.justinmo.word.generator.app.ui.WordRow
import ca.justinmo.word.generator.app.data.WordInfo
import ca.justinmo.word.generator.app.main.WordGeneratorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExplorationScreen(
    openDrawer: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: WordGeneratorViewModel,
) {
    val uiState by viewModel.explorationUiState.collectAsStateWithLifecycle()
    val bottomSheetWord = remember { mutableStateOf<WordInfo?>(null) }
    val sheetState = rememberModalBottomSheetState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = { MainTopAppBar(R.string.exploration_title, openDrawer) }
    ) { paddingValues ->
        ExplorationContent(
            uiState = uiState,
            onQueryChanged = viewModel::onExplorationQueryChanged,
            onTypeChanged = viewModel::onExplorationTypeChanged,
            onLoadMore = viewModel::onExplorationLoadMore,
            bottomSheetWord = bottomSheetWord,
            generatorViewModel = viewModel,
            modifier = Modifier.padding(paddingValues)
        )
        bottomSheetWord.value?.let { word ->
            WordBottomSheet(bottomSheetWord, word, sheetState, viewModel)
        }
    }
}

@Composable
private fun ExplorationContent(
    uiState: ExplorationUiState,
    onQueryChanged: (String) -> Unit,
    onTypeChanged: (ConstraintType) -> Unit,
    onLoadMore: () -> Unit,
    bottomSheetWord: MutableState<WordInfo?>,
    generatorViewModel: WordGeneratorViewModel,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = dimensionResource(id = R.dimen.horizontal_margin))
    ) {
        val placeholderResId = if (uiState.type == ConstraintType.PREFIX) R.string.exploration_input_prefix_placeholder else R.string.exploration_input_suffix_placeholder
        TextField(
            value = uiState.query,
            onValueChange = onQueryChanged,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            placeholder = { Text(stringResource(placeholderResId)) },
            singleLine = true,
            trailingIcon = {
                if (uiState.query.isNotEmpty()) {
                    IconButton(onClick = { onQueryChanged("") }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear"
                        )
                    }
                }
            },
            shape = MaterialTheme.shapes.medium,
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
                unfocusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
            )
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FilterChip(
                selected = uiState.type == ConstraintType.PREFIX,
                onClick = { onTypeChanged(ConstraintType.PREFIX) },
                label = { Text(stringResource(id = R.string.prefix_label)) },
                modifier = Modifier.padding(end = 8.dp)
            )
            FilterChip(
                selected = uiState.type == ConstraintType.SUFFIX,
                onClick = { onTypeChanged(ConstraintType.SUFFIX) },
                label = { Text(stringResource(id = R.string.suffix_label)) }
            )
        }

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(uiState.results) { wordInfo ->
                val annotatedWord = buildAnnotatedString {
                    val word = wordInfo.word
                    val query = uiState.query
                    if (uiState.type == ConstraintType.PREFIX && word.startsWith(query, ignoreCase = true)) {
                        withStyle(SpanStyle(color = Black)) {
                            append(word.take(query.length))
                        }
                        withStyle(SpanStyle(color = MediumGrey)) {
                            append(word.drop(query.length))
                        }
                    } else if (uiState.type == ConstraintType.SUFFIX && word.endsWith(query, ignoreCase = true)) {
                        withStyle(SpanStyle(color = MediumGrey)) {
                            append(word.dropLast(query.length))
                        }
                        withStyle(SpanStyle(color = Black)) {
                            append(word.takeLast(query.length))
                        }
                    } else {
                        append(word)
                    }
                }
                WordRow(
                    wordInfo = wordInfo,
                    onClickVoiceover = { generatorViewModel.readWord(wordInfo.word) },
                    onClickMore = { bottomSheetWord.value = wordInfo },
                    annotatedWord = annotatedWord,
                )
            }
            if (uiState.isLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            } else if (uiState.query.isNotEmpty() && uiState.results.isNotEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        TextButton(onClick = onLoadMore) {
                            Text(stringResource(id = R.string.load_more))
                        }
                    }
                }
            }
        }
    }
}
