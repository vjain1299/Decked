package com.ani.decked

import android.content.Context
import android.content.res.AssetManager
import android.widget.ImageView
import com.ani.decked.CardDisplayView.Companion.setCardImage

class Pile(deck : Deck, a : AssetManager, image : ImageView? = null, baseContext : Context? = null) {
    val FACEDOWN = 0
    val FACEUP = 1
    val context = baseContext
    var mDeck = deck
    val assets = a
    var direction = FACEUP
    val pileImage = image?:generateImageView()

    init {
        updateImageView()
    }

    private fun generateImageView() : ImageView{
        return ImageView(context)
    }

    fun updateImageView() {
        if(mDeck.isEmpty()) return
        if(direction == FACEDOWN) setCardImage(pileImage, "purple_back.png", assets)
        else setCardImage(pileImage, mDeck.peek(), assets)
    }
    fun pop() : Card {
        val card = mDeck.pop()
        updateImageView()
        return card
    }
    fun push(c : Card) {
        mDeck.push(c)
        updateImageView()
    }
    fun shuffle() {
        mDeck.shuffle()
        updateImageView()
    }
    fun isEmpty() : Boolean {
        return mDeck.isEmpty()
    }
    fun flip() {
        when (direction) {
            FACEDOWN -> direction = FACEUP
            FACEUP -> direction = FACEDOWN
        }
        updateImageView()
        //Opponents cards will be flipped down during game play unless otherwise specified
    }
    fun resetDeck(count : Int) {
        mDeck = Deck(count)
    }
}