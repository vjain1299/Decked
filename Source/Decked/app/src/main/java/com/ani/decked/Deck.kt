package com.ani.decked

import java.util.*

class Deck(numOfDecks : Int) {
    val numberOfDecks = numOfDecks
    var deckOfCards : Stack<Card> = getDeck()

    fun getDeck() : Stack<Card> {
        var tempDeck = Stack<Card>()
        for (deck in 0 until numberOfDecks) {
            for (cardVal in 1 until 14) {
                for (cardSuit in 1 until 5) {
                    tempDeck.push(Card(cardVal,cardSuit))
                }
            }
        }
        return tempDeck
    }
    fun shuffle() {
        deckOfCards.shuffle()
    }
    fun pop() : Card {
        return deckOfCards.pop()
    }
    fun push(c : Card) {
        deckOfCards.push(c)
    }
    fun peek() : Card {
        return deckOfCards.peek()
    }
    companion object {
        fun stringToDeck(str: String): Deck {
            var mDeck = Deck(0)
            var mStr = str
            while (true) {
                var x = mStr.indexOf("_")
                if (x == -1) break
                var card = mStr.substring(0, x)
                mDeck.push(Card.stringToCard(card))
                mStr = mStr.substring(x + 1, mStr.length)
            }
            return mDeck
        }
    }
    override fun toString() :String {
        var listOfCards = deckOfCards.toList()
        return (listOfCards.fold("") { string, card -> string + card.toString() })
    }
}