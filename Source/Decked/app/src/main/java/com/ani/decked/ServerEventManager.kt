package com.ani.decked

import com.ani.decked.GameState.nPiles
import com.ani.decked.GameState.nPlayers

class ServerEventManager() {
    val CIRCLE = 0
    val PILE = 1
    val SPLAY = 2
    var names = mutableListOf(Preferences.name)
    val startGameString: String
        get() = "${Preferences.name}->startGame, $nPlayers, $nPiles"

    fun parse(input : String) {
        val stringArray = input.split("->")
        val playerName = stringArray[0]
        if(stringArray[1] == "startGame") {
            names.add(playerName)
            return
        }
        val commands = stringArray[1].split(", ")
    }
}