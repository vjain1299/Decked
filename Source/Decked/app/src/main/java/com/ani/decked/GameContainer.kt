package com.ani.decked


data class GameContainer(
    var playerNum : Int? = null,
    var table : String? = null,
    //TODO: Implement Multiple Piles
    var players : Map<String, String>? = null
)