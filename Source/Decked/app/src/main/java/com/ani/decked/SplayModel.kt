package com.ani.decked

import android.content.res.AssetManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.os.AsyncTask
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlin.concurrent.thread

class SplayModel(private val deck : Deck, private val width : Int, private val height : Int, private val assets : AssetManager, private val resources: Resources) : ViewModel(){
    val bitmapData : MutableLiveData<Bitmap> = MutableLiveData()
    init {
        bitmapData.value = CardDisplayView.getBitmapFromAssets("empty_splay.png", assets)!!
    }
    private fun updateCombinedImage() {
        val bitmapGenerator = BitmapGenerator()
        bitmapGenerator.execute(Triple(deck, Pair(width, height), Pair(assets,resources)))
        bitmapData.postValue(bitmapGenerator.get())
    }
    fun add(element: Card): Boolean {
        val result = deck.add(element)
        updateCombinedImage()
        return result
    }
    fun add(index : Int, element: Card) {
        deck.add(index, element)
        updateCombinedImage()
    }

    fun remove(element: Card): Boolean {
        val result = deck.remove(element)
        updateCombinedImage()
        return result
    }

    fun flip() {
        deck.forEach { it.flip() }
        updateCombinedImage()
    }

    fun count() = deck.count()

    private class BitmapGenerator : AsyncTask<Triple<Deck, Pair<Int, Int>, Pair<AssetManager, Resources>>, Unit, Bitmap?>() {
        override fun doInBackground(vararg data : Triple<Deck, Pair<Int, Int>, Pair<AssetManager, Resources>>?) : Bitmap?{
            val info = data.first()
            val assets = info?.third?.first
            val resources = info?.third?.second
            if(info!=null) {
                val deck = info.first
                val width = info.second.first
                val height = info.second.second
                val CARD_IMAGE_HEIGHT = 1056
                val CARD_IMAGE_WIDTH = 691
                val card_width = CARD_IMAGE_WIDTH * height / CARD_IMAGE_HEIGHT

                if(deck.count() == 0) return CardDisplayView.getBitmapFromAssets("empty_splay.png", assets!!)!!
                val bit = Bitmap.createBitmap(width, height, CardDisplayView.getBitmapFromAssets("gray_back.png", assets!!)!!.config)
                val canvas = Canvas(bit)
                var startX = 0
                val offset : Int
                if ((deck.count() * card_width) <= width) {
                    startX = (width - (deck.count() * card_width)) / 2
                    offset = card_width
                } else {
                    offset = (width - card_width) / (deck.count() - 1)
                }
                deck.forEachIndexed { index, card ->
                    val viewX = (index * offset) + startX
                    val draw = BitmapDrawable(resources, CardDisplayView.getBitmapFromAssets(card.imagePath, assets))
                    draw.setBounds(viewX, 0, viewX + card_width, height)
                    draw.draw(canvas)
                }
                return bit
            }
            return null
        }
    }
    override fun toString() : String {
        return (deck.toList().fold("") { string, card -> string + card.toString() })?:""
    }
}