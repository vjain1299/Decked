package com.ani.decked


data class GameContainer(
    var playerNum : Int? = null,
    var table : MutableList<String>? = null,
    var players : MutableMap<String, String>? = null
)