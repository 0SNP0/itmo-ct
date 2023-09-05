package org.csc.kotlin2021.mastermind

import java.util.*

const val MANUAL_SET = 'm'
const val PRINT_SECRET = 'p'

fun main() {
    cli()
}

val scanner = Scanner(System.`in`)
fun cli() {
    val settings = cliSettings()
    val rules = settings.rules
    println("Press Enter")
    val secret = getSecret(rules)
    println("Secret has generated!")
    val (attempts, complete) = playMastermind(rules, secret, settings.attempts, CliPlayer())
    println(
        if (complete) {
            "Complete for $attempts attempts"
        } else {
            "Wasted attempts: $attempts"
        }
    )
}

fun cliSettings(): Settings {
    val settings = Settings()
    while (true) {
        println(settings)
        print("Enter number of argument to change or 0 to continue): > ")
        when (scanner.next()) {
            "1" -> readValue { settings.diffLetters = it }
            "2" -> readValue { settings.numberOfLetters = it }
            "3" -> readValue { settings.length = it }
            "4" -> readValue { settings.attempts = it }
            "0" -> break
            else -> println("Incorrect input!")
        }
    }
    println("$settings\nOk!")
    return settings
}

class Settings {
    var differentLetters = defaultRules.differentLetters
    var diffLetters: Int
        get() = if (differentLetters) 1 else 0
        set(value) = setValue(value, allowedMode) { differentLetters = it == 1 }
    var numberOfLetters = defaultRules.numberOfLetters
        set(value) = setValue(value, allowedNumber) { field = it }
    var length = defaultRules.length
        set(value) = setValue(value, allowedLength) { field = it }
    var attempts = INF_NUM
        set(value) = setValue(value, allowedAttempts) { field = it }
    val rules: Rules
        get() = Rules(differentLetters, numberOfLetters, length)

    override fun toString() = "Current settings:\n" +
            "1. Letters must be different - $diffLetters ($differentLetters)\n" +
            "2. Number of letters - $numberOfLetters\n" +
            "3. Length of sequence - $length\n" +
            "4. Number of attempts - $attempts" + if (attempts == -1) " (inf)" else ""

    private val allowedMode
        get() = Values(
            "It should be 0 or 1, " +
                    "and 1 only if length <= number of letters"
        ) { it == 0 || (it == 1 && length <= numberOfLetters) }
    private val allowedNumber
        get() = Values(
            "It should be from 1 to $MAX_LETTERS_NUM" +
                    if (differentLetters) " and not less than length of sequence" else ""
        ) { it in 1..MAX_LETTERS_NUM && (!differentLetters || it >= length) }
    private val allowedLength
        get() = Values(
            "It should be positive number" +
                    if (differentLetters) " and not more than number of letters" else ""
        ) { it > 0 && (!differentLetters || it <= numberOfLetters) }
    private val allowedAttempts = Values("It should be positive number or $INF_NUM (inf)") { it == INF_NUM || it > 0 }
}

class Values(private val string: String, private val condition: (Int) -> Boolean) {
    operator fun contains(value: Int) = condition(value)
    override fun toString() = string
}

fun setValue(value: Int, allowed: Values, set: (Int) -> Unit) {
    if (value in allowed) {
        set(value)
    } else {
        incorrectValue(value, allowed)
    }
}

fun readValue(set: (Int) -> Unit) {
    print("Enter value: > ")
    if (scanner.hasNextInt()) {
        set(scanner.nextInt())
    } else {
        incorrectValue(scanner.next(), "It should be number")
    }
}

fun getSecret(rules: Rules, mode: String = readLine() ?: ""): String {
    val secret =
        if (MANUAL_SET in mode) {
            cliSetSecret(rules)
        } else {
            generateSecret(rules)
        }
    if (PRINT_SECRET in mode) println(secret)
    return secret
}

fun cliSetSecret(rules: Rules): String {
    var secret: String
    do {
        println("Enter correct secret: ")
        secret = scanner.next()
    } while (!rules.check(secret))
    println("Ok!")
    return secret
}

fun <T, U> incorrectValue(value: T, allowed: U) = println("Incorrect value: $value. $allowed")
