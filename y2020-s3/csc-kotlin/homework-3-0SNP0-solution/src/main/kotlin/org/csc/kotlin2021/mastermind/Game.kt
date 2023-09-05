package org.csc.kotlin2021.mastermind

const val FIRST_LETTER = 'A'
const val MAX_LETTERS_NUM = 26
const val INF_NUM = -1
val defaultRules = Rules(differentLetters = true, numberOfLetters = 8, length = 4)
val allLetters = FIRST_LETTER until FIRST_LETTER + MAX_LETTERS_NUM

fun playMastermind(
    rules: Rules = defaultRules,
    secret: String = generateSecret(rules),
    limit: Int = INF_NUM,
    player: Player = CliPlayer()
): GameResult {
    var attempts = 0

    var complete = false
    do {
        val guess = player.guess()

        if (!rules.check(guess)) {
            player.incorrectInput(guess, rules.toString())
        } else {
            val (positions, letters) = compare(secret, guess, rules.numberOfLetters)
            complete = positions == secret.length && letters == 0
            player.receiveEvaluation(complete, positions, letters)
        }

        ++attempts
    } while (!complete && attempts != limit)

    return GameResult(attempts, complete)
}

fun compare(secret: String, guess: String, numberOfLetters: Int): GuessResult {
    val result = GuessResult()
    // mark if these letters have already occurred in the corresponding line
    val inSecret = Array(numberOfLetters) { false }
    val inGuess = Array(numberOfLetters) { false }

    for (i in secret.indices) {
        val sNum = num(secret[i])
        val gNum = num(guess[i])

        if (sNum == gNum) {
            // exact match
            ++result.positions
            // just for a secret, because one letter can be both a bull and a cow
            inSecret[sNum] = true
        } else {
            // no more than one match for one letter
            if (inSecret[gNum] && !inGuess[gNum]) {
                ++result.letters
            }
            inGuess[gNum] = true
            if (inGuess[sNum] && !inSecret[sNum]) {
                ++result.letters
            }
            inSecret[sNum] = true
        }
    }
    return result
}

class Rules(val differentLetters: Boolean, val numberOfLetters: Int, val length: Int) {
    override fun toString(): String = "It should consist of $length " +
            (if (differentLetters) "different " else "") +
            "letters in $FIRST_LETTER..${FIRST_LETTER + numberOfLetters - 1}"

    fun check(str: String): Boolean = (str.length == length) && inRange(str) && (!differentLetters || noRepeat(str))

    private fun noRepeat(str: String): Boolean = str.length == str.toSet().size

    private fun inRange(str: String): Boolean = str.all { it in FIRST_LETTER until FIRST_LETTER + numberOfLetters }
}

fun generateSecret(rules: Rules): String =
    if (rules.differentLetters) {
        generateDifferent(rules.length, rules.numberOfLetters)
    } else {
        generateRepeated(rules.length, rules.numberOfLetters)
    }

fun generateDifferent(length: Int, number: Int): String =
    (FIRST_LETTER until FIRST_LETTER + number).shuffled().slice(0 until length).joinToString(separator = "")

fun generateRepeated(length: Int, number: Int): String =
    (0 until length).joinToString(separator = "") { "${(FIRST_LETTER until FIRST_LETTER + number).random()}" }

data class GuessResult(var positions: Int = 0, var letters: Int = 0)

data class GameResult(val attempts: Int, val complete: Boolean)

fun num(letter: Char) = if (letter in allLetters) letter - FIRST_LETTER else throw IndexOutOfBoundsException()
