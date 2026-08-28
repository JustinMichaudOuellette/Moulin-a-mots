package net.justinmo.wordsmith

import java.io.File

operator fun String.div(other: String): String = "$this${File.separator}$other"