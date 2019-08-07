package com.ani.decked

class Card(num : Int, s : Int) {
    val number = num
    val suit = s
    val SPADES  = 1
    val HEARTS = 2
    val DIAMONDS = 3
    val CLUBS = 4
    val imagePath : String = getCardImagePath()

    private fun getCardImagePath() : String {
        var path = ""
        when (number) {
            1 -> path += "a"
            2 -> path += "two"
            3 -> path += "three"
            4 -> path += "four"
            5 -> path += "five"
            6 -> path += "six"
            7 -> path += "seven"
            8 -> path += "eight"
            9 -> path += "nine"
            10 -> path += "ten"
            11 -> path += "j"
            12 -> path += "q"
            13 -> path += "k"
        }
        when (suit) {
            SPADES -> path += "s"
            HEARTS -> path += "h"
            DIAMONDS -> path += "d"
            CLUBS -> path += "c"
        }
        return path
    }
}