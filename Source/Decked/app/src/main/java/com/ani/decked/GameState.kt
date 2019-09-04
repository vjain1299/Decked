package com.ani.decked

import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.ImageView
import kotlinx.android.synthetic.main.content_main.*

object GameState {
    var gameCode : String = "game1"
    var splays : MutableMap<String, Pair<Splay, SplayModel>> = mutableMapOf()
    var tablePiles : MutableList<Pile> = mutableListOf()
    var ipAddress : String = ""
    var names = mutableListOf<String>()
    var circles : MutableList<Circle> = mutableListOf()
    var nPlayers : Int = 1
    var nPiles : Int = 1
    var nDecks : Int = 1
    var hasCircle : Boolean = false
    var isGameHost : Boolean = false
    var clientObject : ClientObject? = null
    var serverObject : ServerObject? = null
    var playerName : String = "Player"

    fun checkTouch(event: MotionEvent, cardView : CardDisplayView?) {
        //if you let go of a card on top of a splay, add it into that splay
        for((key ,splay) in splays) {
            if (splay.first.isInBounds(event)) {
                if (cardView?.card != null) {
                    (cardView.getParent() as ViewGroup).removeView(cardView)
                    cardView.parent = splay
                    val calculatedIndex = splay.first.indexOfEvent(event, splay.second.count()) + 1
                    val index = if(calculatedIndex > splay.second.count()) splay.second.count() else calculatedIndex
                    splay.second.add(index , cardView.card!!)
                    return
                }
            }
        }
        // same as above with circle but there is not loop; if you are touching any of them, you wanna put it in that one spot
        for(circle in circles) {
            for ((k, v) in circle.nameAndCard) {
                if (isInBounds(event, v)) {
                    if (cardView?.getParent() != null) {
                        (cardView.getParent() as ViewGroup).removeView(cardView)
                        cardView.parent = circle
                        circle.nameAndCard[Preferences.name]?.push(cardView.card!!)
                        circle.setViewPositions()
                    }
                }
            }
        }
        // same as above, just add into pile
        for(pile in tablePiles) {
            if(isInBounds(event, pile)) {
                if (cardView?.getParent() != null) {
                    (cardView.getParent() as ViewGroup).removeView(cardView)
                    cardView.parent = pile
                    pile.push(cardView.card!!)
                }
            }
        }
    }

    // It's checking if it's in the bounds of that image view (circle or pile above)
    private fun isInBounds(event: MotionEvent, imageView : ImageView) : Boolean {
        val leftBound = imageView.x
        val rightBound = imageView.x + if(imageView.width != 0) imageView.width else imageView.layoutParams.width
        val topBound = imageView.y
        val bottomBound = imageView.y + if(imageView.height != 0) imageView.height else imageView.layoutParams.height
        val x = event.rawX
        val y = event.rawY

        if (x < leftBound || x > rightBound || y < topBound || y > bottomBound) {
            return false
        }
        return true
    }
}