package org.csc.kotlin2021.mastermind.generation

import org.csc.kotlin2021.mastermind.MAX_LETTERS_NUM
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

internal class SmartGeneratorTest {
    companion object {
        @JvmStatic
        fun sequences(): List<Arguments> {
            val res = mutableListOf(
                Arguments.of(MAX_LETTERS_NUM, MAX_LETTERS_NUM, 100)
            )
            for (n in 1..MAX_LETTERS_NUM step 3) {
                for (l in 1..MAX_LETTERS_NUM step 2) {
                    res += Arguments.of(l, n, minOf(n, l) - 1)
                }
            }
            return res
        }
    }

    @ParameterizedTest
    @MethodSource("sequences")
    fun testSmartGenerator(length: Int, numberOfLetters: Int, count: Int) =
        testGenerator(length = length, numberOfLetters = numberOfLetters, differentLetters = false, count = count)
}
