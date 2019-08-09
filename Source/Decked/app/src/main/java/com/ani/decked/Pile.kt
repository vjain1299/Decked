package com.ani.decked

import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.ImageView
import java.io.IOException

class Pile(image : ImageView, deck : Deck, a : AssetManager) {
    val FACEDOWN = 0
    val FACEUP = 1
    val pileImage = image
    var mDeck = deck
    val assets = a
    var direction = FACEDOWN

    private fun getBitmapFromAssets(fileName : String) : Bitmap? {
        return try {
            BitmapFactory.decodeStream(assets.open(fileName))
        } catch (e : IOException) {
            e.printStackTrace()
            null
        }
    }
    private fun setCardImage(imageView : ImageView, card : Card) {
        setCardImage(imageView, card.imagePath)
    }
    private fun setCardImage(imageView : ImageView, fileName: String) {
        val assetsBitmap: Bitmap? = getBitmapFromAssets(fileName)
        imageView.setImageBitmap(assetsBitmap)
    }
    fun updateImageView() {
        if(mDeck.isEmpty()) return
        if(direction == FACEDOWN) setCardImage(pileImage, "purple_back.png")
        setCardImage(pileImage, mDeck.peek())
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
        //Opponents cards will be flipped down during game play unless otherwise specified
    }
    fun resetDeck(count : Int) {
        mDeck = Deck(count)
    }
}