package com.ani.decked

import java.lang.IndexOutOfBoundsException
import java.util.*

class Deck(numOfDecks : Int = 0) : ArrayList<Card>() {
    val numberOfDecks = numOfDecks
    init {
        getDeck()
    }

    private fun getDeck() {
        for (deck in 0 until numberOfDecks) {
            for (value in 1.. 13) {
                for (suit in 1.. 4) {
                    add(Card(value, suit))
                }
            }
        }
    }
    fun push(deck : Deck) {
        deck.forEach { card -> add(card) }
    }
    fun push(card : Card) {
        add(size, card)
    }
    companion object {
        fun stringToDeck(str: String): Deck {
            val mDeck = Deck(0)
            var mStr = str
            while (true) {
                val x = mStr.indexOf("_")
                if (x == -1) break
                val card = mStr.substring(0, x)
                mDeck.add(Card.stringToCard(card))
                mStr = mStr.substring(x + 1, mStr.length)
            }
            return mDeck
        }
        fun cardsToDeck(vararg cards : Card) : Deck{
            val mDeck = Deck()
            cards.forEach { card -> mDeck.add(card) }
            return mDeck
        }
        fun cardsToDeck(cards : List<Card>) : Deck{
            val mDeck = Deck()
            cards.forEach { card -> mDeck.add(card) }
            return mDeck
        }
    }
    override fun toString() :String {
        return (toList().fold("") { string, card -> string + card.toString() })
    }
    fun pop() : Card {
        return(removeAt(size - 1))
    }
    fun peek() : Card? {
        try {
            return (get(size - 1))
        }
        catch(e : IndexOutOfBoundsException) {
            return null
        }
    }
}