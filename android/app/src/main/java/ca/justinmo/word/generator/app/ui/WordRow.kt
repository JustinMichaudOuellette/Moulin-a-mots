package ca.justinmo.word.generator.app.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ca.justinmo.word.generator.app.R
import ca.justinmo.word.generator.app.data.WordInfo

@Composable
fun WordRow(
    wordInfo: WordInfo,
    onClickVoiceover: () -> Unit,
    onClickMore: () -> Unit,
    annotatedWord: AnnotatedString? = null
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp)
    ) {
        if (annotatedWord != null) {
            Text(
                text = annotatedWord,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
            )
        } else {
            Text(
                text = wordInfo.word,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
            )
        }
        VoiceoverButton(onClickVoiceover)
        MoreButton(onClickMore)
    }
}

@Composable
fun VoiceoverButton(
    onClickVoiceover: () -> Unit,
    modifier: Modifier = Modifier,
    iconSize: Dp = 24.dp
) {
    IconButton(onClick = onClickVoiceover, modifier = modifier) {
        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.baseline_record_voice_over_24),
            contentDescription = stringResource(id = R.string.open_drawer),
            modifier = Modifier.size(iconSize)
        )
    }
}


@Composable
fun MoreButton(onClickMore: () -> Unit) {
    IconButton(onClick = onClickMore) {
        Icon(
            imageVector = ImageVector.vectorResource(
                R.drawable.baseline_more_vert_24),
            stringResource(id = R.string.more_options))
    }
}

@Preview
@Composable
private fun WordRowPreview() {
    Surface(modifier = Modifier.width(200.dp)) {
        WordRow(WordInfo("Fraison", isFavorite = true), onClickVoiceover = {}, onClickMore = {})
    }
}

@Preview
@Composable
private fun VoiceoverButtonMutePreview() {
    Surface {
        VoiceoverButton(onClickVoiceover = {})
    }
}
