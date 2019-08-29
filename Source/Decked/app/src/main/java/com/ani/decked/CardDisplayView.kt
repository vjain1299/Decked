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
import com.ani.decked.GameState.checkTouch
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

class CardDisplayView(c : Card?, con : Context, a : AssetManager, w : Int? = null, h : Int? = null, parentCol : Any? = null) : ImageView(con) {
    var card = c
    var assets = a
    private var dX : Float = 0f
    private var dY : Float = 0f
    var parent : Any? = parentCol

     init {
         setCardImage()
         layoutParams = ViewGroup.LayoutParams(w?:ViewGroup.LayoutParams.WRAP_CONTENT, h?:ViewGroup.LayoutParams.WRAP_CONTENT)
         scaleX = 1f
         scaleY = 1f
     }
     fun setCardImage() {
        val file = card?.imagePath?: "gray_back.png"
        val assetsBitmap: Bitmap? = getBitmapFromAssets(file, assets)
        setImageBitmap(assetsBitmap)
    }
    fun flip() {
        card?.flip()
        animate().scaleX(0.01f).setDuration(1000).start()
        setCardImage()
        animate().scaleX(1f).setDuration(1000).setStartDelay(1000).start()
    }
    fun showCard( layout : ViewGroup) {
        layout.addView(this)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return when(event?.action) {
            MotionEvent.ACTION_DOWN -> {
                if(card == null) return super.onTouchEvent(event)
                dX = x - event.rawX
                dY = y - event.rawY
                val viewGroup = getParent() as ViewGroup
                viewGroup.removeView(this)
                viewGroup.addView(this)
                true
            }
            MotionEvent.ACTION_MOVE -> {
                if(card == null || parent is Circle) return super.onTouchEvent(event)
                this.animate()
                    .x(event.rawX + dX)
                    .y(event.rawY + dY)
                    .setDuration(0)
                    .start()
                true
            }
            MotionEvent.ACTION_OUTSIDE -> {
                true
            }
            MotionEvent.ACTION_UP -> {
                if(card == null || parent is Circle) return super.onTouchEvent(event)
                if(parent is Splay) {
                    (parent as Splay).remove(this)
                    parent = null
                }
                else if(parent is Pile) {
                    (parent as Pile).remove(card)
                    parent = null
                }
                else if(parent is Circle) {
                    (parent as Circle).remove(card)
                    parent = null
                }
                checkTouch(event, this)
                //TODO: Figure out how to put cards in desired order
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
        fun setCardImage(imageView: ImageView, card: Card?, assets : AssetManager) {
            val assetsBitmap: Bitmap? = getBitmapFromAssets(card?.imagePath?:"empty_card.png", assets)
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