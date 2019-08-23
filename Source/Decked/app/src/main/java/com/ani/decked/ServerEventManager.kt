package com.ani.decked

import com.ani.decked.GameState.nPiles
import com.ani.decked.GameState.nPlayers
import com.ani.decked.GameState.names
import java.lang.Integer.parseInt

class ServerEventManager() {
    val CIRCLE = 0
    val PILE = 1
    val SPLAY = 2
    val startGameString: String
        get() = "${Preferences.name}->startGame, $nPlayers, $nPiles"
    val endGameString: String
        get() = "${Preferences.name}->endGame"

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
                val commands = stringArray[2].split(", ")

            }
        }
        return null
    }
    fun act(command : String) {
        // command is formatted as such: "$to $toKey? from $from? $fromKey?: $card"
        // Splits along the colon to isolate the card
        // Establishing the card in question
        val toFromAndCard = command.split(": ")
        val card = Card.stringToCard(toFromAndCard[1])
        // splits along the " from " in order to get
        // to on the left [0], and from on the right [1]
        val toAndFrom = toFromAndCard[0].split(" from ")
        // If the split results in no change, then there is no fromAndKey element
        // Otherwise, establish both the to and the from
        val toAndKey = toAndFrom.first().split(" ")
        var fromAndKey : List<String>? = null
        if(toAndFrom.count() > 1) {
            fromAndKey = toAndFrom[1].split(" ")
        }
        //
        val to : Int = parseInt(toAndKey.first())
        val toKey : String? = if(toAndKey.size > 1) toAndKey[1]

        when(from) {
            SPLAY -> {
                when(to) {
                    SPLAY -> {

                    }
                    PILE -> {

                    }
                    CIRCLE -> {

                    }
                }
            }
            PILE -> {
                when(to) {
                    SPLAY -> {

                    }
                    PILE -> {

                    }
                    CIRCLE -> {

                    }
                }
            }
            CIRCLE -> {
                when(to) {
                    SPLAY -> {

                    }
                    PILE -> {

                    }
                    CIRCLE -> {

                    }
                }
            }
            null -> {
                when(to) {
                    SPLAY -> {

                    }
                    PILE -> {

                    }
                    CIRCLE -> {

                    }
                }
            }
        }
    }
    fun write(to: Int, key : String?, from : Int?, keyFrom : String?, card : Card) : String {

    }
}