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
import com.ani.decked.GameState.checkTouch

class Pile(deck : Deck, a : AssetManager, baseContext : Context, width : Int? = null, height : Int? = null, xVal : Float = 0f, yVal : Float = 0f) : ImageView(baseContext) {
    var mDeck = deck
    val assets = a
    private var dX = 0f
    private var dY = 0f
    var newCardView : CardDisplayView? = null
    private val activity = baseContext

    init {
        updateImageView()
        layoutParams = ViewGroup.LayoutParams(width?:ViewGroup.LayoutParams.WRAP_CONTENT, height?:ViewGroup.LayoutParams.WRAP_CONTENT)
        x = xVal
        y = yVal
        scaleX = 1f
        scaleY = 1f
    }

    private fun updateImageView() {
        if(mDeck.isEmpty()) setCardImage(this , "empty_card.png", assets)
        setCardImage(this, mDeck.peek(), assets)
    }
    fun pop() : Card {
        val card = mDeck.pop()
        updateImageView()
        return card
    }
    fun remove(card : Card?) {
        if(card == null) return
        mDeck.remove(card)
        updateImageView()
    }
    fun push(c : Card) {
        mDeck.push(c)
        updateImageView()
    }
    fun peek() : Card? {
        return(mDeck.peek())
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
        animate().scaleX(0.01f).setDuration(10000).start()
        updateImageView()
        animate().scaleX(1f).setDuration(10000).setStartDelay(10000).start()
        //Opponents cards will be flipped down during game play unless otherwise specified
    }
    fun resetDeck(count : Int) {
        mDeck = Deck(count)
        updateImageView()
    }
    fun showPile(layout: ViewGroup) {
        layout.addView(this)
    }
    fun clear() {
        this.clear()
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return when(event?.action) {
            MotionEvent.ACTION_DOWN -> {
                if(mDeck.isEmpty()) return super.onTouchEvent(event)
                dX = x - event.rawX
                dY = y - event.rawY
                newCardView = CardDisplayView(pop(), activity, assets, width, height)
                newCardView!!.x = x
                newCardView!!.y = y
                newCardView?.showCard(parent as ViewGroup)
                true
            }
            MotionEvent.ACTION_MOVE -> {
                if(mDeck.isEmpty()) return super.onTouchEvent(event)
                newCardView!!.animate()
                    .x(event.rawX + dX)
                    .y(event.rawY + dY)
                    .setDuration(0)
                    .start()
                true
            }
            MotionEvent.ACTION_UP -> {
                if(mDeck.isEmpty()) return super.onTouchEvent(event)
                if(event.downTime < 500) {
                    performClick()
                }
                checkTouch(event, newCardView)
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