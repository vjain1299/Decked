package com.ani.decked

import android.content.Context
import android.content.res.AssetManager
import android.util.Log
import android.view.MotionEvent
import android.view.ViewGroup
import android.view.animation.RotateAnimation

class Splay(con : Context, aManager : AssetManager, viewGroup : ViewGroup, deck : List<Card>, totalWidth : Int, totalHeight : Int, xVal : Int = 0, yVal : Int = 0,var rotation : Float = 0f) : ArrayList<Card>() {
    val context = con
    val assets = aManager
    var width = totalWidth
        set(w) {
            field = w
            if((rotation % 180).toInt() != 0) {
                card_width = CARD_IMAGE_WIDTH * width / CARD_IMAGE_HEIGHT
                cardViews.forEach { image ->
                    image.layoutParams.width = card_width
                    image.layoutParams.height = height
                }
            }
        }

    val center
        get() = Pair(x + width/2f, y + height/2f)
    var height = totalHeight
        set(h) {
            field = h
            if((rotation % 180).toInt() == 0) {
                card_width = CARD_IMAGE_WIDTH * height / CARD_IMAGE_HEIGHT
                cardViews.forEach { image ->
                    image.layoutParams.width = card_width
                    image.layoutParams.height = height
                }
            }
        }
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
            CardDisplayView(card, context, assets, card_width, height, this)
        } as ArrayList<CardDisplayView>
        setCardPositions()
        cardViews.forEach { cardView ->
            cardView.rotation = (rotation)
            cardView.showCard(layout)
        }
        when((rotation % 90).toInt()) {
            1 -> {
                width = height.also { height = width }
                x += width
            }
            2 -> {
                x += width
                y += height
            }
            3 -> {
                width = height.also { height = width}
                x += width
            }
        }
    }

    override fun add(element: Card): Boolean {
        val result = super.add(element)
        if(result) {
            cardViews.add(CardDisplayView(element, context , assets, card_width, height, this))
            setCardPositions()
        }
        cardViews.last().showCard(layout)
        return result
    }
    override fun add(index : Int, element: Card) {
        if(index > size /*|| index < 0 */) return
        super.add(index, element)
        cardViews.add(index, CardDisplayView(element, context , assets, card_width, height, this))
        setCardPositions()
        removeImages()
        cardViews.forEach { cardView ->
            cardView.showCard(layout)
        }
    }

    fun remove(element: CardDisplayView): Boolean {
        val result = cardViews.remove(element)
        super.remove(element.card!!)
        setCardPositions()
        return result
    }
    override fun remove(element : Card) : Boolean {
        val index = indexOf(element)
        val result = super.remove(element)
        if(index != -1) {
            cardViews.removeAt(index)
        }
        setCardPositions()
        removeImages()
        cardViews.forEach { cardView ->
            cardView.showCard(layout)
        }
        return result
    }
    private fun clearImages() {
        cardViews.clear()
        removeImages()
    }
    private fun removeImages() {
        cardViews.forEach { view ->
            layout.removeView(view)
        }
    }
    fun reconstructFromDeck(deck : Deck) {
        clear()
        clearImages()
        for (card in deck) {
            add(card)
        }
    }

    fun indexOfEvent(event : MotionEvent) : Int {
        //Returns index of the card that the current event is on top of
        if(count() <= 1) return -1
        var startX = 0
        val offset : Int
        if((cardViews.count() * card_width) <= width) {
            startX = (width - (cardViews.count() * card_width)) / 2
            offset = card_width
        }
        else {
            offset = (width - card_width) / (count() - 1)
        }
        val relativeX = event.rawX - (x + startX)
        val index : Int = (relativeX/offset).toInt()
        return if(index > count()) count() - 1 else index
    }

    private fun setCardPositions() {
        when((rotation / 90).toInt()) {
            0 -> {
                var startX = 0
                val offset : Int
                if ((cardViews.count() * card_width) <= width) {
                    startX = (width - (cardViews.count() * card_width)) / 2
                    offset = card_width
                } else {
                    offset = (width - card_width) / (size - 1)
                }
                cardViews.forEachIndexed { index, cardView ->
                    cardView.x = x + (index * offset) + startX.toFloat()
                    cardView.y = y.toFloat()
                }
            }
            1 -> {
                var startY = 0
                val offset : Int
                if ((cardViews.count() * card_width) <= height) {
                    startY = (height - (cardViews.count() * card_width)) / 2
                    offset = card_width
                } else {
                    offset = (height - card_width) / (size - 1)
                }
                cardViews.forEachIndexed { index, cardView ->
                    cardView.y = y + (index * offset) + startY.toFloat()
                    cardView.x = x.toFloat() + width
                    cardView.rotation = 90f
                }
            }
            2 -> {
                var startX = 0
                val offset : Int
                if ((cardViews.count() * card_width) <= width) {
                    startX = (width - (cardViews.count() * card_width)) / 2
                    offset = card_width
                } else {
                    offset = (width - card_width) / (size - 1)
                }
                cardViews.forEachIndexed { index, cardView ->
                    cardView.x = width - (x + (index * offset) + startX.toFloat())
                    cardView.y = height + y.toFloat()
                    cardView.rotation = 180f
                }
            }
            3 -> {
                var startY = 0
                val offset : Int
                if ((cardViews.count() * card_width) <= height) {
                    startY = (height - (cardViews.count() * card_width)) / 2
                    offset = card_width
                } else {
                    offset = (height - card_width) / (size - 1)
                }
                cardViews.forEachIndexed { index, cardView ->
                    cardView.y = y + (index * offset) + startY.toFloat()
                    cardView.x = x.toFloat()
                    cardView.rotation = 270f
                }
            }
        }
    }
    fun setTopLeft(xVal : Int, yVal: Int) {
        x = xVal
        y = yVal
        removeImages()
        setCardPositions()
        cardViews.forEach { cardView ->
            cardView.showCard(layout)
        }
    }
    fun flip() {
        cardViews.forEach {
                cardView -> cardView.flip()
        }
        forEach {
            it.flip()
        }
    }
    fun getCardViewFromCard(card : Card) : CardDisplayView {
        return cardViews[indexOf(card)]
    }
    override fun toString() :String {
        return (toList().fold("") { string, card -> string + card.toString() })
    }

}