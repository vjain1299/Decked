package com.ani.decked

import android.content.Context
import android.content.res.AssetManager
import android.view.ViewGroup
import java.util.stream.Collectors.toList

class Splay(con : Context, aManager : AssetManager, viewGroup : ViewGroup, deck : List<Card>, totalWidth : Int, totalHeight : Int, xVal : Int = 0, yVal : Int = 0) : ArrayList<Card>() {
    val context = con
    val assets = aManager
    var width = totalWidth
    var height = totalHeight
    var x = xVal
    var y = yVal
    val CARD_IMAGE_HEIGHT = 1056
    val CARD_IMAGE_WIDTH = 691
    var card_width = CARD_IMAGE_WIDTH * height / CARD_IMAGE_HEIGHT
    var cardViews : ArrayList<CardDisplayView>
    var layout = viewGroup

    init {
        addAll(deck)
        cardViews = map { card ->
            CardDisplayView(card, context, assets, card_width, height)
        } as ArrayList<CardDisplayView>
        setCardPositions()
        cardViews.forEach { cardView ->
            cardView.showCard(layout)
        }
    }

    override fun add(element: Card): Boolean {
        val result = super.add(element)
        if(result) {
            cardViews.add(CardDisplayView(element, context, assets, card_width, height))
            setCardPositions()
        }
        cardViews.last().showCard(layout)
        return result
    }
    override fun clear() {
        super.clear()
        cardViews.clear()
        cardViews.forEach {view ->
            layout.removeView(view)
        }
    }
    fun reconstructFromDeck(deck : Deck) {
        clear()
        for (card in deck) {
            add(card)
        }
    }

    private fun setCardPositions() {
        var startX = 0
        val offset : Int
        if((cardViews.count() * card_width) <= width) {
            startX = (width - (cardViews.count() * card_width)) / 2
            offset = card_width
        }
        else {
            offset = (width - card_width) / (size - 1)
        }
        cardViews.forEachIndexed { index , cardView ->
            cardView.x =  x + (index * offset) + startX.toFloat()
            cardView.y = y.toFloat()
        }
    }
    fun setTopLeft(xVal : Int, yVal: Int) {
        x = xVal
        y = yVal
    }
    fun flip() {
        cardViews.forEach {cardView -> cardView.flip() }
    }
    override fun toString() :String {
        return (toList().fold("") { string, card -> string + card.toString() })
    }

}