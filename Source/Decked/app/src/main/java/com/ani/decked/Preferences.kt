package com.ani.decked

import java.util.*

object Preferences {

    var name: String = "Player"
    var color: String = "purple"

    fun getColorPath(): String{
        return "${color}_back.png"
    }

}