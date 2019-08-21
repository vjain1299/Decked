package com.ani.decked

import java.util.*

object Preferences {

    var name: String = "Player"
    var color: String = "purple"
    var playerName : String = "Player" + Math.random()

    fun getColorPath(): String{
        return "${color}_back.png"
    }

}