package com.ani.decked

import android.util.Log
import java.lang.Integer.parseInt
import java.lang.NumberFormatException
import com.ani.decked.GameState
import com.ani.decked.GameState.circles
import com.ani.decked.GameState.tablePiles

class GameEventManager(private val mainActivity: MainActivity) {
    val CIRCLE = 0
    val PILE = 1
    val SPLAY = 2

    fun parse(string : String) {
        val commands = string.split(", ")
        for(command in commands) {
            val action = command.split(": ")
            act(action)
        }
    }
    fun write() {

    }
    private fun act(action : List<String>) {
        val desiredElement = findAndCheckElement(action[0])
        when(desiredElement?.first) {
            SPLAY -> {

            }
            PILE -> {

            }
            CIRCLE -> {

            }
        }
    }
    private fun findAndCheckElement(elementName : String) : Pair<Int, String>?{
        val typeAndSpot = elementName.split(' ')
        if(typeAndSpot.count() < 2) {
            return null
        }
        try {
            val typeVal = parseInt(typeAndSpot[0])
            when(typeVal) {
                PILE -> {
                    tablePiles.count() >= parseInt(typeAndSpot[1])
                }
                CIRCLE -> {
                    circles.count() >= parseInt(typeAndSpot[1])
                }
            }
            return Pair(parseInt(typeAndSpot[0]), typeAndSpot[1])
        }
        catch(e : NumberFormatException) {
            Log.w("GameEventManager", "Parse Failed: Invalid Format", e)
        }
        return null
    }
}