package ca.justinmo.word.generator.app.data

import kotlin.random.Random

data class PreviousLetter(val chain: String, val count: Int)

fun Array<PreviousLetter>.weightedRandom(random: Random, temperature: Float = 1.0f): PreviousLetter =
    weightedRandom(random, temperature) { it.count }
