package com.ani.decked


data class GameContainer(
    var playerNum : Int? = null,
    var table : Array<String>? = null,
    //TODO: Implement Multiple Piles
    var players : MutableMap<String, String>? = null
)