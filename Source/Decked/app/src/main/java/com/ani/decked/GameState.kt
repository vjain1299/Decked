package com.ani.decked

import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.ImageView
import kotlinx.android.synthetic.main.content_main.*

object GameState {
    var gameCode : String = "game1"
    var splays : MutableMap<String, Splay> = mutableMapOf()
    var playerCardStrings : MutableMap<String, String> = mutableMapOf()
    var tablePiles : MutableList<String> = mutableListOf()
    var circles : ArrayList<Circle> = ArrayList()
    var gameObject : GameContainer = GameContainer()
    var names = mutableListOf<String>()
    var mPile : Pile? = null
    var mCircle: Circle? = null
    var nPlayers : Int = 1
    var nPiles : Int = 1

    fun checkTouch(event: MotionEvent, cardView : CardDisplayView?) {
        for((key ,splay) in splays) {
            if (isInBounds(event, splay)) {
                if (cardView?.card != null) {
                    (cardView.getParent() as ViewGroup).removeView(cardView)
                    cardView.parent = splay
                    val calculatedIndex = splay.indexOfEvent(event) + 1
                    val index = if(calculatedIndex > splay.count()) splay.count() else calculatedIndex
                    splay.add(index , cardView.card!!)
                    return
                }
            }
        }
        if(mCircle == null) return
        for((k,v) in mCircle!!.cardViews) {
            if(isInBounds(event, v)) {
                if (cardView?.getParent() != null) {
                    (cardView?.getParent() as ViewGroup).removeView(cardView)
                    cardView.parent = mCircle
                    mCircle!!.nameAndCard[Preferences.name] = cardView?.card
                    mCircle!!.cardViews[Preferences.name]?.card = cardView?.card
                    mCircle!!.cardViews[Preferences.name]?.setCardImage()
                }
            }
        }
    }
    private fun isInBounds(event: MotionEvent, imageView : ImageView) : Boolean {
        val leftBound = imageView.x
        val rightBound = imageView.x + imageView.width
        val topBound = imageView.y
        val bottomBound = imageView.y + imageView.height
        val x = event.rawX
        val y = event.rawY

        if (x < leftBound || x > rightBound || y < topBound || y > bottomBound) {
            return false
        }
        return true
    }

    private fun isInBounds(event: MotionEvent, splay : Splay) : Boolean {
        val leftBound = splay.x
        val rightBound = splay.x + splay.width
        val topBound = splay.y
        val bottomBound = splay.y + splay.height
        val x = event.rawX
        val y = event.rawY

        if (x < leftBound || x > rightBound || y < topBound || y > bottomBound) {
            return false
        }
        return true
    }
}