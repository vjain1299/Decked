package com.ani.decked

import android.text.TextUtils.split
import android.util.Log
import java.lang.Integer.parseInt
import java.lang.NumberFormatException
import com.ani.decked.GameState
import com.ani.decked.GameState.circles
import com.ani.decked.GameState.nPiles
import com.ani.decked.GameState.nPlayers
import com.ani.decked.GameState.names
import com.ani.decked.GameState.splays
import com.ani.decked.GameState.tablePiles
import com.ani.decked.ServerEventManager.act
import java.io.OutputStream
import java.nio.charset.Charset
import java.util.*


// This essentially takes a string input, splits it on a bunch of delimiters (any character/string sequence the end or beginning of a section of text, like spaces, _, etc.)
// The write function will send a packet that has the person's name and the event that just occured in the game through the usage of TCP sockets
// ServerEventManager basically the same
object ClientEventManager {
    val CIRCLE = 0
    val PILE = 1
    val SPLAY = 2
    val startGameString = "${Preferences.name}->startGame"
    val endGameString = "${Preferences.name}->endGame"

    fun parse(input : String, mainActivity: MainActivity) : String?{
        val stringArray = input.split("->")
        val playerName = stringArray[0]
        when(stringArray[1]) {
            "startGame" -> {
                startGame(stringArray[2].split(", "))
                return null
            }
            "names" -> {
                names.add(playerName)
                return null
            }
            "quitGame" -> {
                return null
            }
            else -> {
                mainActivity.runOnUiThread {
                    act(stringArray[1], playerName)
                }
            }
        }
        return null
    }
    fun act(command : String, player : String) {
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
        val to = parseInt(toAndKey.first())
        val toKey : String? = if(toAndKey.size > 1) toAndKey[1] else null
        val from = if(fromAndKey != null) parseInt(fromAndKey.first()) else null
        val fromKey : String? = if(fromAndKey?.count()?:0 > 1) fromAndKey!![1] else null

        when(from) {
            SPLAY -> {
                when(to) {
                    SPLAY -> {
                        //Could add animations here
                        splays[fromKey]?.second?.remove(card)
                        splays[toKey]?.second?.add(card)
                    }
                    PILE -> {
                        splays[fromKey]?.second?.remove(card)
                        tablePiles[parseInt(toKey!!)].push(card)
                    }
                    CIRCLE -> {
                        splays[fromKey]?.second?.remove(card)
                        circles[0][player] = card
                    }
                }
            }
            PILE -> {
                when(to) {
                    SPLAY -> {
                        tablePiles[parseInt(fromKey!!)].pop()
                        splays[toKey]?.second?.add(card)
                    }
                    PILE -> {
                        tablePiles[parseInt(fromKey!!)].pop()
                        tablePiles[parseInt(toKey!!)].push(card)
                    }
                    CIRCLE -> {
                        tablePiles[parseInt(fromKey!!)].pop()
                        circles[0].nameAndCard[player]?.push(card)
                    }
                }
            }
            CIRCLE -> {
                when(to) {
                    SPLAY -> {
                        circles[0].nameAndCard[player]?.pop()
                        splays[toKey]?.second?.add(card)
                    }
                    PILE -> {
                        circles[0].nameAndCard[player]?.pop()
                        tablePiles[parseInt(toKey!!)].push(card)
                    }
                    CIRCLE -> {
                        circles[0].nameAndCard[player]?.pop()
                        circles[0].nameAndCard[player]?.push(card)
                    }
                }
            }
            null -> {
                when(to) {
                    SPLAY -> {
                        splays[toKey]?.second?.add(card)
                    }
                    PILE -> {
                        tablePiles[parseInt(toKey!!)].push(card)
                    }
                    CIRCLE -> {
                        circles[0].nameAndCard[player]?.push(card)
                    }
                }
            }
        }
    }
    fun startGame(input : List<String>) {
        nPlayers = parseInt(input[1])
        nPiles = parseInt(input[2])
    }
    fun write(to: Int, key : String?, from : Int?, keyFrom : String?, card : Card) : String {
        return("${Preferences.name}->echo->$to $key from $from $keyFrom: $card\n")
    }
}