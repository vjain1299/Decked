package com.ani.decked

import com.ani.decked.GameState.nPiles
import com.ani.decked.GameState.nPlayers
import com.ani.decked.GameState.names

class ServerEventManager() {
    val CIRCLE = 0
    val PILE = 1
    val SPLAY = 2
    val startGameString: String
        get() = "${Preferences.name}->startGame, $nPlayers, $nPiles"

    fun parse(input : String) : String? {
        val stringArray = input.split("->")
        val playerName = stringArray[0]
        when(stringArray[1]) {
            "startGame" -> {
                names.add(playerName)
                return null
            }
            "quitGame" -> {
                return null
            }
            "echo" -> {
                val commands = stringArray[1].split(", ")

            }
            else -> {
                val commands = stringArray[1].split(", ")
            }
        }
        return null
    }
}