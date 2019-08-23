package com.ani.decked

import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.ImageView
import kotlinx.android.synthetic.main.content_main.*

object GameState {
    var gameCode : String = "game1"
    var splays : MutableMap<String, Splay> = mutableMapOf()
    var tablePiles : MutableList<String> = mutableListOf()
    var circles : ArrayList<Circle> = ArrayList()
    var ipAddress : String = ""
    var names = mutableListOf<String>()
    var mPile : Pile? = null
    var mCircle: Circle? = null
    var nPlayers : Int = 1
    var nPiles : Int = 1
    var nDecks : Int = 1
    var isGameHost : Boolean = false
    var clientObject : ClientObject? = null
    var serverObject : ServerObject? = null
    var clientEventManager : ClientEventManager? = null
    var serverEventManager : ServerEventManager? = null

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
        for((k,v) in mCircle!!.nameAndCard) {
            if(isInBounds(event, v)) {
                if (cardView?.getParent() != null) {
                    (cardView.getParent() as ViewGroup).removeView(cardView)
                    cardView.parent = mCircle
                    mCircle!!.nameAndCard[Preferences.name]?.push(cardView.card!!)
                    mCircle!!.setViewPositions()
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