package com.ani.decked

class Card(num : Int, s : Int) {
    val number = num
    val suit = s
    val SPADES  = 1
    val HEARTS = 2
    val DIAMONDS = 3
    val CLUBS = 4
    val FACEDOWN = 0
    val FACEUP = 1
    var direction : Int = FACEUP
    var imagePath : String = getCardImagePath()
    get() {
        field = getCardImagePath()
        return getCardImagePath()
    }

    private fun getCardImagePath() : String {
        if(direction == FACEDOWN) {
            //TODO: Link in settings to get the right image
            return "purple_back.png"
        }
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
        path += ".png"
        return path
    }
    fun flip() {
        when (direction) {
            FACEDOWN -> direction = FACEUP
            FACEUP -> direction = FACEDOWN
        }
    }
    override fun toString() : String {
        var result = ""
        result += number
        when(suit) {
            SPADES -> result += "S"
            HEARTS -> result += "H"
            DIAMONDS -> result += "D"
            CLUBS -> result += "C"
        }
        return(result + "_")
    }

    companion object {
        const val SPADES  = 1
        const val HEARTS = 2
        const val DIAMONDS = 3
        const val CLUBS = 4

        fun stringToCard(card : String) : Card {
            var suit : Int = SPADES
            when(card[card.length - 2]) {
                'S' -> suit = SPADES
                'H' -> suit = HEARTS
                'D' -> suit = DIAMONDS
                'C' -> suit = CLUBS
            }
            val value = card.substring(0,card.length - 2).toInt()
            return(Card(value, suit))
        }
        override operator fun equals(other: Any?): Boolean {
            return(other.toString() == toString())
        }
    }
}