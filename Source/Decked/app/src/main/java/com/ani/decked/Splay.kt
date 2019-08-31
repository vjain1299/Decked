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

class Splay(con : Context, aManager : AssetManager, deck : List<Card>, totalWidth : Int, totalHeight : Int, xVal : Float = 0f, yVal : Float = 0f) : ImageView(con) {
    private val assets = aManager
    private val cards = Deck()
    private val CARD_IMAGE_HEIGHT = 1056
    private val CARD_IMAGE_WIDTH = 691
    private var card_width = CARD_IMAGE_WIDTH * totalHeight / CARD_IMAGE_HEIGHT
    private var center = Pair(totalWidth/2f, totalHeight/2f)
    get() = Pair(x + layoutParams.width/2, y + layoutParams.height/2)

    init {
        cards.addAll(deck)
        x = xVal
        y = yVal
        layoutParams = ViewGroup.LayoutParams(totalWidth, totalHeight)
        updateImage()
    }
    override fun setRotation(value : Float) {
        super.setRotation(value)
        updateImage()
    }
    fun isInBounds(event: MotionEvent) : Boolean {
        var left = 0f
        var top = 0f
        var right = 0f
        var bottom = 0f
        when((rotation / 90).toInt() % 2) {
            0 -> {
                left = center.first - (layoutParams.width/2)
                right = center.first + (layoutParams.width/2)
                top = center.second - (layoutParams.height/2)
                bottom = center.second + (layoutParams.height/2)
            }
            1 -> {
                left = center.first - (layoutParams.height/2)
                right = center.first + (layoutParams.height/2)
                top = center.second - (layoutParams.width/2)
                bottom = center.second + (layoutParams.width/2)
            }
        }
        val xVal = event.rawX
        val yVal = event.rawY

        if (xVal < left || xVal > right || yVal < top || yVal > bottom) {
            return false
        }
        return true

    }

    fun add(element: Card): Boolean {
        val result = cards.add(element)
        updateImage()
        return result
    }
    fun add(index : Int, element: Card) {
        cards.add(index, element)
        updateImage()
    }

    fun remove(element: Card): Boolean {
        val result = cards.remove(element)
        updateImage()
        return result
    }
    fun reconstructFromDeck(deck : Deck) {
        cards.clear()
        for (card in deck) {
            add(card)
        }
        updateImage()
    }

    fun indexOfEvent(event : MotionEvent) : Int {
        //Returns index of the card that the current event is on top of
        if(count() <= 1) return -1
        var startX = 0
        val offset : Int
        if((count() * card_width) <= width) {
            startX = (width - (count() * card_width)) / 2
            offset = card_width
        }
        else {
            offset = (width - card_width) / (count() - 1)
        }
        val relativeX = event.rawX - (x + startX)
        val index : Int = (relativeX/offset).toInt()
        return if(index > count()) count() - 1 else index
    }

    fun flip() {
        cards.forEach {
                card -> card.flip()
        }
        updateImage()
    }

    override fun toString() :String {
        return (cards.toList().fold("") { string, card -> string + card.toString() })
    }
    fun getCombinedImage() : Bitmap {
        if(count() == 0) return CardDisplayView.getBitmapFromAssets("empty_splay.png", assets)!!
        val bit = Bitmap.createBitmap(layoutParams.width, layoutParams.height, CardDisplayView.getBitmapFromAssets("gray_back.png", assets)!!.config)
        val canvas = Canvas(bit)
        var startX = 0
        val offset : Int
        if ((count() * card_width) <= width) {
            startX = (width - (count() * card_width)) / 2
            offset = card_width
        } else {
            offset = (width - card_width) / (count() - 1)
        }
        cards.forEachIndexed { index, card ->
            val viewX = (index * offset) + startX
            val draw = BitmapDrawable(resources, CardDisplayView.getBitmapFromAssets(card.imagePath, assets))
            draw.setBounds(viewX, 0, viewX + card_width, layoutParams.height)
            draw.draw(canvas)
        }
        return bit
    }
    fun showSplay(layout : ViewGroup) {
        layout.addView(this)
    }
    private fun updateImage() {
        setImageBitmap(getCombinedImage())
    }
    fun count() = cards.count()
    private class GetBitmap : AsyncTask<Splay, Unit, Unit>() {
        override fun doInBackground(vararg p0: Splay?) {

        }

        override fun onPostExecute(result: Unit?) {

        }
    }
}