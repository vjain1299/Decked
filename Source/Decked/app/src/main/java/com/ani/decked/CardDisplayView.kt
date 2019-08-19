package com.ani.decked

import android.content.Context
import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.MotionEventCompat
import java.io.IOException

class CardDisplayView(c : Card?, con : Context, a : AssetManager, w : Int? = null, h : Int? = null) : ImageView(con) {
    var card = c
    var assets = a
    val FACEDOWN = 0
    val FACEUP = 1
    var direction = FACEUP
    private var dX : Float = 0f
    private var dY : Float = 0f

     init {
         setCardImage()
         layoutParams = ViewGroup.LayoutParams(w?:ViewGroup.LayoutParams.WRAP_CONTENT, h?:ViewGroup.LayoutParams.WRAP_CONTENT)
     }
    private fun setCardImage() {
        val file = if (direction == FACEUP) card?.imagePath?: "gray_back.png" else "purple_back.png"
        val assetsBitmap: Bitmap? = getBitmapFromAssets(file, assets)
        setImageBitmap(assetsBitmap)
    }
    fun flip() {
        when (direction) {
            FACEDOWN -> direction = FACEUP
            FACEUP -> direction = FACEDOWN
        }
        setCardImage()
    }
    fun showCard( layout : ViewGroup) {
        layout.addView(this)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return when(event?.action) {
            MotionEvent.ACTION_DOWN -> {
                dX = x - event.rawX
                dY = y - event.rawY
                true
            }
            MotionEvent.ACTION_MOVE -> {
                this.animate()
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
        //TODO: Insert raise animation here
        return super.performClick()
    }
    companion object {
        fun getBitmapFromAssets(fileName : String, assets : AssetManager) : Bitmap? {
            return try {
                BitmapFactory.decodeStream(assets.open(fileName))
            } catch (e : IOException) {
                e.printStackTrace()
                null
            }
        }
        fun setCardImage(imageView: ImageView, card: Card, assets : AssetManager) {
            val assetsBitmap: Bitmap? = getBitmapFromAssets(card.imagePath, assets)
            imageView.setImageBitmap(assetsBitmap)
        }
        fun setCardImage(imageView: ImageView, fileName: String, assets: AssetManager) {
            val assetsBitmap: Bitmap? = getBitmapFromAssets(fileName, assets)
            imageView.setImageBitmap(assetsBitmap)
        }
        fun getFromImageView(image : ImageView, card : Card, assets: AssetManager, dir : Int) : CardDisplayView {
            val result = CardDisplayView(card, image.context, assets, image.width, image.height)
            result.direction = dir
            return result
        }
    }
}