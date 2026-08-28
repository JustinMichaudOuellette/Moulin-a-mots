package net.justinmo.wordsmith

import java.text.Collator
import java.util.*

object SetUtil {
    fun Set<String>.toSortedArray(language: Language) = filter {
        it.length >= language.depth
    }.sortedWith(Collator.getInstance(Locale.forLanguageTag(language.code)))
}