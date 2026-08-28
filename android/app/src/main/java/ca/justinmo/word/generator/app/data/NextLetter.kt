package ca.justinmo.word.generator.app.data

import kotlin.random.Random

data class NextLetter(val next_letter: Int, val count: Int)

fun Array<NextLetter>.weightedRandom(random: Random, temperature: Float = 1.0f): NextLetter =
    weightedRandom(random, temperature) { it.count }
