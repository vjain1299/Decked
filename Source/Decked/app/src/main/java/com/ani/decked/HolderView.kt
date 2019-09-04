package com.ani.decked

import android.content.Context
import android.content.Intent
import android.content.res.AssetManager
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.os.bundleOf
import java.lang.Math.abs
import java.util.*

//Simple image view that enables simple touch commands
// Single tap, double tap, scale, and drag are the commands in here
// This is for the layout configuration
class HolderView(var holderType : String, context : Context, var assets : AssetManager, dir : Int = 1) : ImageView(context){
    var scaleGestureDetector : ScaleGestureDetector
    var gestureDetector : GestureDetector
    var dX = 0f
    var dY = 0f
    var suffix = ""
    var direction = dir
    set(value) {
        field = value
        suffix = if(direction == 0) "_flipped" else ""
        CardDisplayView.setCardImage(this, filename, assets)
    }
    var filename = "${holderType.toLowerCase(Locale.getDefault())}_holder$suffix.png"
        get() = "${holderType.toLowerCase(Locale.getDefault())}_holder$suffix.png"

    init {
        scaleX = 1f
        scaleY = 1f
        rotation = 0f
        layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        CardDisplayView.setCardImage(this, filename, assets)
        gestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
                direction = kotlin.math.abs(direction - 1)
                return super.onSingleTapConfirmed(e)
            }
            override fun onDoubleTapEvent(e: MotionEvent?): Boolean {
                when(e?.actionMasked) {
                    MotionEvent.ACTION_DOWN -> {
                        when((rotation/90).toInt()) {
                            0 -> {
                                animate().rotation(90f).setDuration(1000).start()
                            }
                            1 -> {
                                animate().rotation(180f).setDuration(1000).start()
                            }
                            2 -> {
                                animate().rotation(270f).setDuration(1000).start()
                            }
                            3 -> {
                                animate().rotation(0f).setDuration(1000).start()
                            }
                        }
                    }
                }
                return super.onDoubleTapEvent(e)
            }
        })
        scaleGestureDetector = ScaleGestureDetector(context, object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                animate().scaleX(scaleX * detector.scaleFactor)
                    .scaleY(scaleY * detector.scaleFactor)
                    .setDuration(0).start()
                return super.onScale(detector)
            }
        })
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {

        scaleGestureDetector.onTouchEvent(event)
        gestureDetector.onTouchEvent(event)
        return when(event?.actionMasked) {
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
            MotionEvent.ACTION_OUTSIDE -> {
                true
            }
            MotionEvent.ACTION_UP -> {
                true
            }
            else -> return super.onTouchEvent(event)
        }
    }
    fun toBundle(index : Int? = null) : Bundle {
        val bundle = bundleOf()
        bundle.putFloat("${holderType.capitalize()}${index?:""}X",x)
        bundle.putFloat("${holderType.capitalize()}${index?:""}Y",y)
        bundle.putInt("${holderType.capitalize()}${index?:""}Width", (width * scaleX).toInt())
        bundle.putInt("${holderType.capitalize()}${index?:""}Height", (height * scaleY).toInt())
        bundle.putFloat("${holderType.capitalize()}${index?:""}Rotation",rotation)
        bundle.putInt("${holderType.capitalize()}${index?:""}Direction", direction)
        return bundle
    }
}