package com.ani.decked

import android.content.Context
import android.content.res.AssetManager
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.ani.decked.GameState.nPlayers
import com.google.common.math.Quantiles
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class CircleHolder(context: Context, assets : AssetManager, cardWidth : Int, var xCenter : Float, var yCenter : Float) : View(context) {
    val listOfViews : MutableList<ImageView> = mutableListOf()
    var dX = 0f
    var dY = 0f
    val CARD_IMAGE_HEIGHT = 1056
    val CARD_IMAGE_WIDTH = 691
    var scaleGestureDetector : ScaleGestureDetector
    var card_width : Int = cardWidth
    set(value) {
        field = value
        listOfViews.forEach { view ->
            view.layoutParams.width = value
            view.layoutParams.height = card_height
        }
    }
    val card_height : Int
        get() = CARD_IMAGE_HEIGHT * card_width / CARD_IMAGE_WIDTH
    init {
        for(i in 0 until nPlayers) {
            val image = ImageView(context)
            image.layoutParams = ViewGroup.LayoutParams(card_width, card_height)
            CardDisplayView.setCardImage(image, "circle_holder.png", assets)
            listOfViews.add(image)
            image.scaleX = 1f
            image.scaleY = 1f
            image.setOnTouchListener { image, event ->
                onTouchEvent(event)
            }
        }
        setViewPositions()
        scaleGestureDetector = ScaleGestureDetector(context, object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                listOfViews.forEach { card ->
                    card.animate().scaleX(scaleX * detector.scaleFactor)
                        .scaleY(scaleY * detector.scaleFactor)
                        .setDuration(0).start()
                }
                return super.onScale(detector)
            }

            override fun onScaleEnd(detector: ScaleGestureDetector) {
                setViewPositions()
                super.onScaleEnd(detector)
            }
        })
    }
    fun setViewPositions() {
        val circleRadius = ((listOfViews.first().width * listOfViews.first().scaleX) /2) / cos((((nPlayers - 2) * PI) / (2 * nPlayers)))
        val increment = (2* PI)/ nPlayers
        listOfViews.forEachIndexed { i, view ->
            val thisAngle = (i * increment) + PI /2
            view.x = ((-1 * cos(thisAngle) * circleRadius) + xCenter).toFloat()
            view.y = ((sin(thisAngle) * circleRadius) + yCenter).toFloat()
            view.rotation = ((-1 * thisAngle * (180/ PI)) + 90).toFloat()
        }
    }
    override fun onTouchEvent(event: MotionEvent): Boolean {
        scaleGestureDetector.onTouchEvent(event)
        return when(event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                dX = xCenter - event.rawX
                dY = yCenter - event.rawY
                true
            }
            MotionEvent.ACTION_MOVE -> {
                listOfViews.forEach { view ->
                    view.animate()
                        .x(event.rawX + dX)
                        .y(event.rawY + dY)
                        .setDuration(0)
                        .start()
                }
                true
            }
            MotionEvent.ACTION_OUTSIDE -> {
                true
            }
            MotionEvent.ACTION_UP -> {
                xCenter = event.rawX + dX
                yCenter = event.rawY + dY
                setViewPositions()
                true
            }
            else -> {
                super.onTouchEvent(event)
            }
        }
    }
    fun showCircle(layout : ViewGroup) {
        listOfViews.forEach {
            layout.addView(it)
        }
    }
}