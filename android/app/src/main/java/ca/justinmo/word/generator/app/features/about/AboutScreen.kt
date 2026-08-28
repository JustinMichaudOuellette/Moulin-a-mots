package ca.justinmo.word.generator.app.features.about

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ca.justinmo.word.generator.app.R
import ca.justinmo.word.generator.app.ui.MainTopAppBar
import ca.justinmo.word.generator.app.ui.WordGeneratorTheme

@Composable
fun AboutScreen(
    openDrawer: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = { MainTopAppBar(R.string.about_title, openDrawer) }
    ) { paddingValues ->
        val context = LocalContext.current
        AboutContent(
            modifier = Modifier.padding(paddingValues),
            appName = stringResource(R.string.app_name),
            appVersion = appVersion(context),
        )
    }
}

@Composable
fun AboutContent(
    modifier: Modifier,
    appName: String,
    appVersion: String,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = dimensionResource(id = R.dimen.horizontal_margin))
    ) {
        Text(
            text = appName,
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.ExtraLight),
            maxLines = 1,
            autoSize = TextAutoSize.StepBased(minFontSize = 12.sp, maxFontSize = 112.sp),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        AboutLabel(appVersion)
        val copyrightText = buildAnnotatedString {
            append("Copyright © ")
            withLink(
                LinkAnnotation.Url(
                    "https://www.justinmo.ca/",
                    styles = TextLinkStyles(style = SpanStyle(color = MaterialTheme.colorScheme.onSurface, textDecoration = TextDecoration.Underline))
                )
            ) {
                append("Justin Michaud-Ouellette")
            }
        }
        Text(
            text = copyrightText,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
fun AboutLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge,
        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
        textAlign = TextAlign.Center,
    )
}

private fun appName(context: Context): String {
    val applicationInfo = context.applicationInfo
    val stringId = applicationInfo.labelRes
    return if (stringId == 0) applicationInfo.nonLocalizedLabel.toString() else context.getString(stringId)
}

private fun appVersion(context: Context): String {
    return try {
        val packageInfo = (context as Activity).packageManager.getPackageInfo(context.packageName, 0)
        "Version ${packageInfo.versionName}"
    } catch (e: PackageManager.NameNotFoundException) {
        ""
    }
}

@Preview
@Composable
fun AboutContentPreview() {
    WordGeneratorTheme {
        Surface {
            AboutContent(Modifier, "Word Generator", "Version 1.0 Build 1")
        }
    }
}
