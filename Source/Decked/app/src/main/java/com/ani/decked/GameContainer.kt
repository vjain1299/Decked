package com.ani.decked


data class GameContainer(
    var playerNum : Int? = null,
    var table : String? = null,
    var players : Map<String, Map<String, String>?>? = null
)