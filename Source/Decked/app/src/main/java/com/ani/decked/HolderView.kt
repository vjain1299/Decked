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
    var gestureDetector : ScaleGestureDetector
    var dX = 0f
    var dY = 0f
    init {
        scaleX = 1f
        scaleY = 1f
        layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        CardDisplayView.setCardImage(this, "${holderType}_holder.png", assets)
        gestureDetector = ScaleGestureDetector(context, object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            var prevWidth = width
            var prevHeight = height
            override fun onScaleBegin(detector: ScaleGestureDetector?): Boolean {
                prevHeight = height
                prevWidth = width
                return super.onScaleBegin(detector)
            }
            override fun onScale(detector: ScaleGestureDetector?): Boolean {
                layoutParams.width = (prevWidth + detector!!.currentSpanX).toInt()
                layoutParams.height = (prevHeight + detector.currentSpanY).toInt()
                return super.onScale(detector)
            }
        })
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        gestureDetector.onTouchEvent(event)
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
                true
            }
            else -> return super.onTouchEvent(event)
        }
    }
}