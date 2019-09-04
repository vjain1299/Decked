package com.ani.decked

import android.content.Context
import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.os.AsyncTask
import android.os.Handler
import android.text.method.TextKeyListener.clear
import android.util.Log
import android.view.MotionEvent
import android.view.ViewGroup
import android.view.animation.RotateAnimation
import android.widget.ImageView
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.drawToBitmap
import java.util.stream.Collectors.toList
import kotlin.concurrent.thread

//Splay is JUST a view, there is NO DATA here

class Splay(con : Context, totalWidth : Int, totalHeight : Int, xVal : Float = 0f, yVal : Float = 0f) : ImageView(con) {
    private val CARD_IMAGE_HEIGHT = 1056
    private val CARD_IMAGE_WIDTH = 691
    private var card_width = CARD_IMAGE_WIDTH * totalHeight / CARD_IMAGE_HEIGHT
    init {
        x = xVal
        y = yVal
        layoutParams = ViewGroup.LayoutParams(totalWidth, totalHeight) //totalWidth and totalHeight is of the splay
        // Every view has layout params, width and height are protected properties of the imageView class, can't be set, only gotten
    }
    private fun center() : Pair<Float, Float> = Pair(x + layoutParams.width/2, y + layoutParams.height/2)
    //This checks if on top of splay, but also factors in rotated splays
    fun isInBounds(event: MotionEvent) : Boolean {
        var left = 0f
        var top = 0f
        var right = 0f
        var bottom = 0f
        when((rotation / 90).toInt() % 2) {
            0 -> {
                left = center().first - (layoutParams.width/2)
                right = center().first + (layoutParams.width/2)
                top = center().second - (layoutParams.height/2)
                bottom = center().second + (layoutParams.height/2)
            }
            1 -> {
                left = center().first - (layoutParams.height/2)
                right = center().first + (layoutParams.height/2)
                top = center().second - (layoutParams.width/2)
                bottom = center().second + (layoutParams.width/2)
            }
        }
        val xVal = event.rawX
        val yVal = event.rawY - 100

        if (xVal < left || xVal > right || yVal < top || yVal > bottom) {
            return false
        }
        return true

    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return true // Fill this in later
    }

    //Used to put the cards in whatever order we want (need to factor in rotation still)
    fun indexOfEvent(event : MotionEvent, count : Int) : Int {
        //Returns index of the card that the current event is on top of
        if(count <= 1) return -1
        var startX = 0
        val offset : Int
        if((count * card_width) <= width) {
            startX = (width - (count * card_width)) / 2
            offset = card_width
        }
        else {
            offset = (width - card_width) / (count - 1)
        }
        val relativeX = event.rawX - (x + startX)
        val index : Int = (relativeX/offset).toInt()
        return if(index > count) count - 1 else index
    }
    fun showSplay(layout : ViewGroup) {
        if(this.parent == null) {
            layout.addView(this)
        }
    }
}