package com.ani.decked


data class GameContainer(
    var playerNum : Int? = null,
    // TODO: Add Timestamp -> var timeStamp :
    var table : MutableList<String>? = null,
    var players : MutableMap<String, String>? = null
)