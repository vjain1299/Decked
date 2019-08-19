package com.ani.decked

import android.content.Context
import android.content.res.AssetManager
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.ImageView
import com.ani.decked.CardDisplayView.Companion.setCardImage

class Pile(deck : Deck, a : AssetManager, baseContext : Context) : ImageView(baseContext) {
    val FACEDOWN = 0
    val FACEUP = 1
    var mDeck = deck
    val assets = a
    var direction = FACEUP
    private var dX = 0f
    private var dY = 0f
    var newCardView : CardDisplayView? = null

    init {
        updateImageView()
        layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    private fun updateImageView() {
        if(mDeck.isEmpty()) setCardImage(this , "gray_back.png", assets)
        if(direction == FACEDOWN) setCardImage(this, "purple_back.png", assets)
        else setCardImage(this, mDeck.peek(), assets)
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
        updateImageView()
    }
    fun showPile(layout: ViewGroup) {
        layout.addView(this)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return when(event?.action) {
            MotionEvent.ACTION_DOWN -> {
                dX = x - event.rawX
                dY = y - event.rawY
                newCardView = CardDisplayView(pop(), context, assets, width, height)
                true
            }
            MotionEvent.ACTION_MOVE -> {
                newCardView!!.x = x
                newCardView!!.y = y
                newCardView!!.showCard(parent as ViewGroup)
                newCardView!!.animate()
                    .x(event.rawX + dX)
                    .y(event.rawY + dY)
                    .setDuration(0)
                    .start()
                true
            }
            MotionEvent.ACTION_UP -> {
                if(event.downTime < 500) {
                    performClick()
                }
                true
            }
            else -> super.onTouchEvent(event)
        }
    }
    override fun performClick(): Boolean {
        this.animate()
            .yBy(height/4f)
            .setDuration(250)
            .start()
        //TODO: Insert raise animation here
        return super.performClick()
    }
}