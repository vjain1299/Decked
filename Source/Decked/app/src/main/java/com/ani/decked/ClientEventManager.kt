package com.ani.decked

import android.text.TextUtils.split
import android.util.Log
import java.lang.Integer.parseInt
import java.lang.NumberFormatException
import com.ani.decked.GameState
import com.ani.decked.GameState.nPiles
import com.ani.decked.GameState.nPlayers
import com.ani.decked.GameState.splays
import com.ani.decked.GameState.tablePiles
import java.io.OutputStream
import java.nio.charset.Charset
import java.util.*

object ClientEventManager {
    val CIRCLE = 0
    val PILE = 1
    val SPLAY = 2
    val startGameString = "${Preferences.name}->startGame"
    val endGameString = "${Preferences.name}->endGame"

    fun parse(input : String) : String?{
        val stringArray = input.split("->")
        val playerName = stringArray[0]
        val commands = stringArray[1].split(", ")
        if(commands[1] == "startGame") startGame(commands)
        return null
    }
    fun startGame(input : List<String>) {
        nPlayers = parseInt(input[1])
        nPiles = parseInt(input[2])
    }
}