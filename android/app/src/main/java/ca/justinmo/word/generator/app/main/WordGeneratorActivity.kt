

package ca.justinmo.word.generator.app.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import ca.justinmo.word.generator.app.ui.WordGeneratorTheme

/**
 * Main activity for the word generator.
 */
class WordGeneratorActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WordGeneratorTheme {
                WordGeneratorNavGraph()
            }
        }
    }
}
