package com.ani.decked

import android.app.Activity
import android.content.Context
import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.MotionEventCompat
import java.io.IOException

class CardDisplayView(c : Card?, con : MainActivity, a : AssetManager, w : Int? = null, h : Int? = null, parentCol : ArrayList<Card>? = null) : ImageView(con) {
    var card = c
    var assets = a
    val mainActivity = con
    private var dX : Float = 0f
    private var dY : Float = 0f
    var parent : ArrayList<Card>? = parentCol

     init {
         setCardImage()
         layoutParams = ViewGroup.LayoutParams(w?:ViewGroup.LayoutParams.WRAP_CONTENT, h?:ViewGroup.LayoutParams.WRAP_CONTENT)
     }
    private fun setCardImage() {
        val file = card?.imagePath?: "gray_back.png"
        val assetsBitmap: Bitmap? = getBitmapFromAssets(file, assets)
        setImageBitmap(assetsBitmap)
    }
    fun flip() {
        card?.flip()
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
                parent?.remove(card)
                parent = null
                mainActivity.checkTouch(event, this)
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
        fun getFromImageView(image : ImageView, card : Card, assets: AssetManager, dir : Int, con: MainActivity) : CardDisplayView {
            val result = CardDisplayView(card, con, assets, image.width, image.height)
            result.card?.direction = dir
            return result
        }
    }
}