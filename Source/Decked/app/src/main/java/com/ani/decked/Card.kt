package com.ani.decked

import java.lang.Integer.parseInt

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
            return Preferences.getColorPath()
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
        return(result + "${direction}_")
    }

    // the equivalent of having static methods in an object; it doesn't take up more memory than it needs to
    companion object {
        const val SPADES  = 1
        const val HEARTS = 2
        const val DIAMONDS = 3
        const val CLUBS = 4

        fun stringToCard(card : String) : Card {
            var suit : Int = SPADES
            when(card[card.length - 3]) {
                'S' -> suit = SPADES
                'H' -> suit = HEARTS
                'D' -> suit = DIAMONDS
                'C' -> suit = CLUBS
            }
            val value = card.substring(0,card.length - 3).toInt()
            val result = Card(value, suit)
            result.direction = card[card.length - 2].toInt()
            return(result)
        }
        override operator fun equals(other: Any?): Boolean {
            return(other.toString() == toString())
        }
        fun randomCards(numberOfCards : Int) : List<Card> {
            val cards = mutableListOf<Card>()
            for (i in 1..numberOfCards) {
                var value = (Math.random() * 13).toInt() + 1
                var suit = (Math.random() * 4).toInt() + 1
                cards.add(Card(value, suit))
            }
            return cards
        }
    }
}