package org.csc.kotlin2021.mastermind.generation

import org.csc.kotlin2021.mastermind.MAX_LETTERS_NUM
import org.csc.kotlin2021.mastermind.Rules
import org.csc.kotlin2021.mastermind.generateSecret
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

internal class BaseGeneratorTest {
    companion object {
        @JvmStatic
        fun sequences(): List<Arguments> {
            val res = mutableListOf(
                Arguments.of(MAX_LETTERS_NUM, MAX_LETTERS_NUM, 100)
            )
            for (n in 2..MAX_LETTERS_NUM step 3) {
                for (l in 1..n step 2) {
                    res += Arguments.of(l, n, l - 1)
                }
            }
            return res
        }
    }

    @ParameterizedTest
    @MethodSource("sequences")
    fun testBaseGenerator(length: Int, numberOfLetters: Int, count: Int) =
        testGenerator(length = length, numberOfLetters = numberOfLetters, differentLetters = true, count = count)
}

fun testGenerator(length: Int, numberOfLetters: Int, differentLetters: Boolean, count: Int) {
    val set = mutableSetOf<String>()
    repeat(count) {
        val checker = Rules(differentLetters, numberOfLetters, length)
        val sequence = generateSecret(checker)
        Assertions.assertTrue(
            checker.check(sequence), "'$sequence' is not correct for " +
                    "length=$length and numberofLetters=$numberOfLetters"
        )
        set += sequence
    }
    Assertions.assertTrue(set.size > count - 2, "A lot of repeated sequences")
}
