package com.ani.decked

import android.content.Context
import android.content.res.AssetManager
import android.view.ViewGroup
import android.view.animation.RotateAnimation
import com.ani.decked.GameState.nPlayers
import com.ani.decked.GameState.names
import com.ani.decked.Preferences.name
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class Circle(val context : Context, val assets: AssetManager, val layout : ViewGroup, var card_width : Int = 0, var xCenter : Int = 0, var yCenter : Int = 0) {
    val nameAndCard : MutableMap<String, Pile> = mutableMapOf()
    val CARD_IMAGE_HEIGHT = 1056
    val CARD_IMAGE_WIDTH = 691
    var card_height = CARD_IMAGE_HEIGHT * card_width / CARD_IMAGE_WIDTH
    // names is in GameState and is adding in all the Piles
    init {
        names.forEach { name ->
            val pile = Pile(Deck(),assets, context)
            pile.layoutParams.width = card_width
            pile.layoutParams.height = card_height
            nameAndCard[name] = pile
        }
    }

    //set actually pushes, it actually adds whatever card you give it to the pile
    //function: override [] operator which will call the operator on nameAndCard
    operator fun set(name: String, card : Card) {
        layout.removeView(nameAndCard[name])
        nameAndCard[name]?.push(card)
        setViewPositions()
        nameAndCard[name]?.showPile(layout)
    }

    //Just looks at the top card
    operator fun get(name: String) : Card? {
        return nameAndCard[name]?.peek()
    }
    private fun clearImages() {
        nameAndCard.clear()
        removeImages()
    }
    //takes circle off board (no existo in UI)
    private fun removeImages() {
        for((k,view) in nameAndCard) {
            layout.removeView(view)
        }
    }
    fun setViewPositions() {
        val circleRadius = (card_width /2) / cos((((nPlayers - 2) * PI) / (2 * nPlayers)))
        val increment = (2*PI)/ nPlayers
        for((k,view) in nameAndCard){
            val thisAngle = ((names.indexOf(k) - (names.indexOf(name)))* increment) + PI/2
            view.x = ((-1 * cos(thisAngle) * circleRadius) + xCenter).toFloat()
            view.y = ((sin(thisAngle) * circleRadius) + yCenter).toFloat()

            view.rotation = ((-1 * thisAngle * (180/PI)) + 90).toFloat()
//            val rotate : RotateAnimation = RotateAnimation(0f, (-1 * angle * (180/PI)).toFloat(),  view.x, view.y)
//            rotate.duration = 0
//            view.animation = rotate
//            rotate.fillAfter = true
//            rotate.start()
        }
    }
    fun remove(card : Card?) {
        if(card == null) return
        for((k,v) in nameAndCard) {
            if (v.peek() == card) {
                nameAndCard[k]?.pop()
            }
        }
    }

    fun showCircle(vg : ViewGroup){
        removeImages()
        for((k,v) in nameAndCard){
            v.showPile(vg)
        }
    }
    //You also need a toString function and a fromString function

    // Understand what shape the final product will be
    // E.g. 6 cards will make a hexagon
    // Then you know that the length of each side will be equal to width of card
    // Each top corner will be touching the adjacent cards
    // The angle that the cards make will be the exterior angle of the shape which is (360/nPlayers)
    // You know that bottom_card's topLeft == cardToTheLeft's topRight
    // We know that cardToTheLeft.topLeft.y = topRight.y - sin(360/nPlayers) * card_width, cardToTheLeft.topLeft.x = topRight.x - cos(360/nPlayers)
    // We know that cardToTheRight.topLeft.y = topRight.y - sin(360/nPlayers) * card_width, cardToTheRight.topLeft.x = topRight.x + cos(360/nPlayers)

    //Lets say that all calculations occur as a function of the center of the circle.
    // You want to produce a vector of displacement to tell you where each topLeft is

    // Givens: nPlayers, center_point, card_width, cardIndex
    // Given a shape


    //Idea 1: instead of writing this as an ArrayList, you make it a map
    //  map would be <name, card>
    //  cross-check the name to the location of the player with that name -> translate that location into the Circle's location -> put that card in that location

    //PSEUDOCODE:
    //  circle_radius: (card_width/2) / cos(((n-2)*180)/2n)
    //  Start angle 0 occurs at point |
    //  180/nPlayers = angle_of_first_spot
    //  each increment = 360/nPlayers
    //  Position displacement can be obtained by x = -cos(angle_of_spot) * circle_radius, y = sin(angle_of_spot) * circle_radius
    //  Absolute Position can be retrieved by adding Position displacement vector to center point
    //    ___
    //  /     \
    // |   o   |
    //  \__|__/
}