package org.csc.kotlin2021.mastermind

import java.util.*

interface Player {
    fun guess(): String
    fun receiveEvaluation(complete: Boolean, positions: Int, letters: Int)
    fun incorrectInput(guess: String, condition: String)
}

class CliPlayer : Player {
    private val scanner = Scanner(System.`in`)

    override fun guess(): String {
        print("Your guess: ")
        return scanner.next()
    }

    override fun receiveEvaluation(complete: Boolean, positions: Int, letters: Int) {
        if (complete) {
            println("You are correct!")
        } else {
            println("Positions: $positions; letters: $letters.")
        }
    }

    override fun incorrectInput(guess: String, condition: String) {
        println("Incorrect input: $guess. $condition.")
    }
}
