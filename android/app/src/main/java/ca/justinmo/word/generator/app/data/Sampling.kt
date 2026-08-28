package ca.justinmo.word.generator.app.data

import kotlin.math.pow
import kotlin.random.Random

/**
 * Common logic for temperature-aware weighted sampling.
 *
 * @param items List of items to sample from.
 * @param getCount Function to extract the count/weight from an item.
 * @param random Random number generator.
 * @param temperature Sampling temperature. 1.0 is identity. 
 *                    Lower is more predictable, higher is more random.
 */
fun <T> List<T>.weightedRandom(
    random: Random,
    temperature: Float = 1.0f,
    getCount: (T) -> Int
): T {
    if (isEmpty()) throw NoSuchElementException("Empty list")
    if (size == 1) return first()

    if (temperature == 1.0f) {
        val sum = sumOf { getCount(it) }
        if (sum <= 0) return random().also { /* fallback */ }
        val randomInt = random.nextInt(sum)
        var accumulator = 0
        for (item in this) {
            accumulator += getCount(item)
            if (accumulator > randomInt) return item
        }
    } else {
        // Temperature-aware logic: weight' = weight ^ (1/T)
        val exponent = 1.0 / temperature.toDouble()
        val adjustedWeights = map { getCount(it).toDouble().pow(exponent) }
        val sum = adjustedWeights.sum()
        val randomDouble = random.nextDouble() * sum
        var accumulator = 0.0
        for (i in indices) {
            accumulator += adjustedWeights[i]
            if (accumulator >= randomDouble) return this[i]
        }
    }
    return last()
}

fun <T> Array<T>.weightedRandom(
    random: Random,
    temperature: Float = 1.0f,
    getCount: (T) -> Int
): T = asList().weightedRandom(random, temperature, getCount)
