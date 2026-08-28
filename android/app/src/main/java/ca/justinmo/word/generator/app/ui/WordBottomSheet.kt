package ca.justinmo.word.generator.app.ui

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.core.app.ShareCompat
import ca.justinmo.word.generator.app.R
import ca.justinmo.word.generator.app.data.WordInfo
import ca.justinmo.word.generator.app.main.WordGeneratorViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WordBottomSheet(
    showBottomSheet: MutableState<WordInfo?>,
    word: WordInfo,
    sheetState: SheetState,
    generatorViewModel: WordGeneratorViewModel,
) {
    // https://developer.android.com/develop/ui/compose/components/bottom-sheets
    ModalBottomSheet(
        onDismissRequest = {
            showBottomSheet.value = null
        },
        sheetState = sheetState
    ) {

        val coroutineScope = rememberCoroutineScope()
        fun hideBottomSheet() {
            coroutineScope.launch {
                sheetState.hide()
                showBottomSheet.value = null
            }
        }

        val context = LocalContext.current
        OptionRow(
            painter = painterResource(R.drawable.baseline_share_24),
            label = stringResource(R.string.share)) {
            ShareCompat.IntentBuilder(context)
                .setType("text/plain")
                .setText(word.word)
                .startChooser()
            hideBottomSheet()
        }
        val isFavorite = generatorViewModel.sessionFavorites.contains(word.word)
        OptionRow(
            painter = painterResource(if (isFavorite) R.drawable.baseline_favorite_24 else R.drawable.baseline_favorite_border_24),
            label = stringResource(if (isFavorite) R.string.remove_favorite else R.string.add_favorite),
        ) {
            generatorViewModel.onClickFavorite(word.word, isFavorite)
            hideBottomSheet()
        }
    }
}
