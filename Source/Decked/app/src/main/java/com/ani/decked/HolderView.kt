package com.ani.decked

import android.content.Context
import android.content.Intent
import android.content.res.AssetManager
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.ViewGroup
import android.widget.ImageView

class HolderView(holderType : String, context : Context, assets : AssetManager) : ImageView(context){
    var scaleGestureDetector : ScaleGestureDetector
    var gestureDetector : GestureDetector
    var dX = 0f
    var dY = 0f
    init {
        scaleX = 1f
        scaleY = 1f
        rotation = 0f
        layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        CardDisplayView.setCardImage(this, "${holderType}_holder.png", assets)
        gestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onDoubleTapEvent(e: MotionEvent?): Boolean {
                when(e?.actionMasked) {
                    MotionEvent.ACTION_DOWN -> {
                        animate().rotation(rotation + 90f).setDuration(1000).start()
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
            MotionEvent.ACTION_UP -> {
                true
            }
            else -> return super.onTouchEvent(event)
        }
    }
}