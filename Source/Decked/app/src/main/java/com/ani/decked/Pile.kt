package com.ani.decked

import android.app.Activity
import android.content.Context
import android.content.res.AssetManager
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.GestureDetectorCompat
import com.ani.decked.CardDisplayView.Companion.setCardImage

class Pile(deck : Deck, a : AssetManager, baseContext : MainActivity) : ImageView(baseContext) {
    var mDeck = deck
    val assets = a
    private var dX = 0f
    private var dY = 0f
    var newCardView : CardDisplayView? = null
    private val activity = baseContext

    init {
        updateImageView()
        layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    private fun updateImageView() {
        if(mDeck.isEmpty()) setCardImage(this , "gray_back.png", assets)
        setCardImage(this, mDeck.peek(), assets)
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
        mDeck.forEach { card -> card.flip() }
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
                newCardView = CardDisplayView(pop(), activity, assets, width, height)
                newCardView!!.x = x
                newCardView!!.y = y
                newCardView?.showCard(parent as ViewGroup)
                true
            }
            MotionEvent.ACTION_MOVE -> {
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
                activity.checkTouch(event, newCardView)
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
        return super.performClick()
    }
}