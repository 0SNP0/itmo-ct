package org.csc.kotlin2021.mastermind.matching

import org.csc.kotlin2021.mastermind.MAX_LETTERS_NUM
import org.csc.kotlin2021.mastermind.compare
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

internal class SequenceMatchingTest {

    companion object {
        @JvmStatic
        fun sequences() = listOf(
            Arguments.of("ABCD", "ABCD", 4, 0),
            Arguments.of("ACEB", "BCDF", 1, 1),
            Arguments.of("ACEB", "BCDF", 1, 1),
            Arguments.of("DAA", "DDD", 1, 1),
            Arguments.of("DDD", "ADA", 1, 0),
            Arguments.of("ABBC", "BDEF", 0, 1),
            Arguments.of("BDEF", "ABBC", 0, 1),
            Arguments.of("ABBC", "FEDB", 0, 1),
            Arguments.of("FEDB", "ABBC", 0, 1),
            Arguments.of("AAAMN", "MNOAA", 0, 3),
            Arguments.of("QWERTYUIOPASDFGHJKLZXCVBNM", "QRUPDHLCNWTIAFJZVMEYOSGKXB", 2, 24),
            Arguments.of("QWERTYUIOPASDFGHJKLZXCVBNM", "Q" + "WERTYUIOPASDFGHJKLZXCVBNM".reversed(), 2, 24)
        )
    }

    @ParameterizedTest
    @MethodSource("sequences")
    fun testSequenceMatching(initial: String, actual: String, expectedFullMatch: Int, expectedPartMatch: Int) {
        val (actualFullMatch, actualPartMatch) = compare(initial, actual, MAX_LETTERS_NUM)
        Assertions.assertEquals(
            expectedFullMatch, actualFullMatch, "Full matches don't equal! " +
                    "Actual full match count = $actualFullMatch, expected full match count = $expectedFullMatch"
        )
        Assertions.assertEquals(
            expectedPartMatch, actualPartMatch, "Part matches don't equal! " +
                    "Part full part count = $actualPartMatch, expected part match count = $expectedPartMatch"
        )
    }
}
