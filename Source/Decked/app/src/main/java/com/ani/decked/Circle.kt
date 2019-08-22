package com.ani.decked

import android.content.Context
import android.content.res.AssetManager
import android.view.ViewGroup
import com.ani.decked.GameState.nPlayers
import kotlin.math.PI
import kotlin.math.cos

class Circle(val context : Context, val assets: AssetManager, val layout : ViewGroup, var card_width : Int = 0, var xCenter : Int = 0, var yCenter : Int = 0) {
    val nameAndCard : MutableMap<String, Card?> = mutableMapOf()
    val cardViews : MutableMap<String, CardDisplayView?> = mutableMapOf()
    val CARD_IMAGE_HEIGHT = 1056
    val CARD_IMAGE_WIDTH = 691
    var card_height = CARD_IMAGE_HEIGHT * card_width / CARD_IMAGE_WIDTH

    //function: override [] operator which will call the operator on nameAndCard
    operator fun set(name: String, card : Card) {
        if(nameAndCard[name] != null) {
            layout.removeView(cardViews[name])
        }
        nameAndCard[name] = card
        cardViews[name] = CardDisplayView(card, context, assets, card_width, card_height, this)
        setViewPositions()
        cardViews[name]?.showCard(layout)
    }
    operator fun get(name: String) : Card? {
        return nameAndCard[name]
    }
    private fun clearImages() {
        cardViews.clear()
        removeImages()
    }
    private fun removeImages() {
        for((k,view) in cardViews) {
            layout.removeView(view)
        }
    }
    private fun setViewPositions() {
        val circleRadius = (card_width /2) / cos((((nPlayers - 2) * PI) / (2 * nPlayers)))
    }
    fun remove(card : Card?) {
        if(card == null) return
            for((k,v) in nameAndCard) {
                if(v == card) {
                nameAndCard[k] = null
                cardViews[k]?.card = null
                }
            }
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