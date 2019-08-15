package com.ani.decked

import android.content.Context
import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.ViewGroup
import android.widget.ImageView
import java.io.IOException

class CardDisplayView(c : Card, con : Context, a : AssetManager, w : Int? = null, h : Int? = null) : ImageView(con) {
    var card = c
    var assets = a
    val FACEDOWN = 0
    val FACEUP = 1
    var direction = FACEUP

     init {
         setCardImage()
         layoutParams = ViewGroup.LayoutParams(w?:ViewGroup.LayoutParams.WRAP_CONTENT, h?:ViewGroup.LayoutParams.WRAP_CONTENT)
     }
    private fun setCardImage() {
        val file = if (direction == FACEUP) card.imagePath else "purple_back.png"
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
    }
}