package com.ani.decked

import android.content.Intent
import android.content.res.Resources
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Layout
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.os.bundleOf
import com.ani.decked.GameState.hasCircle
import com.ani.decked.GameState.nPiles
import com.ani.decked.GameState.nPlayers
import com.ani.decked.GameState.splays
import kotlinx.android.synthetic.main.activity_layout_config.*
import kotlinx.android.synthetic.main.content_main.*
import kotlin.math.absoluteValue

class LayoutConfigActivity : AppCompatActivity() {
    private val listOfPileImages : MutableList<HolderView> = mutableListOf()
    private val listOfSplayImages : MutableList<HolderView> = mutableListOf()
    var circleImage : CircleHolder? = null
    lateinit var mySplayImage : HolderView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_layout_config)
        for(i in 1..nPiles) {
            val image = HolderView("pile",this, assets)
            listOfPileImages.add(image)
        }
        for(i in 1..nPlayers) {
            val image = HolderView("splay",this, assets)
            if(i == 1) {
                mySplayImage = image
            }
            else {
                listOfSplayImages.add(image)
            }
        }
        if(hasCircle) {
            val image = CircleHolder(this, assets, 200, 300f,300f)
            circleImage = image
        }
        setStartPositions()
        showAll()
    }
    private fun showAll() {
        listOfPileImages.forEach {contentLayout.addView(it)}
        listOfSplayImages.forEach {contentLayout.addView(it)}
        contentLayout.addView(mySplayImage)
        if(circleImage != null)     circleImage!!.showCircle(contentLayout)
    }
    private fun setStartPositions() {
        val screenWidth = Resources.getSystem().displayMetrics.widthPixels
        val screenHeight = Resources.getSystem().displayMetrics.heightPixels
        val horizontalIncrement = screenWidth/16f
        val verticalIncrement = screenHeight/26f
        mySplayImage.layoutParams.height = 8 * verticalIncrement.toInt()
        mySplayImage.layoutParams.width = (12 * horizontalIncrement).toInt()
        mySplayImage.x = 2 * horizontalIncrement
        mySplayImage.y =  screenHeight - (8 * verticalIncrement)

        val numberOfSplaysPerSide = (kotlin.math.floor((0.5 * (nPlayers - 4)).absoluteValue) * (nPlayers - 4)/kotlin.math.max((nPlayers - 4).absoluteValue, 1)).toInt() + 1
        val numberOfSplaysOnTop = nPlayers - (numberOfSplaysPerSide * 2) - 1
        val verticalMargin = (screenHeight - (4 * verticalIncrement)) / (numberOfSplaysPerSide + 1)
        val horizontalMargin = (screenWidth - (4 * horizontalIncrement)) / (numberOfSplaysOnTop + 1)
        var i = 0
    }

    override fun onBackPressed() {
        startActivity(getDepartIntent())
    }

    override fun onNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
    //TODO: Create circle-holder class that actually shows where circle would be
    //TODO: Fix issues with splay positioning not translating into MainActivity properly
    fun getDepartIntent() : Intent {
        val newIntent = Intent(this, ConfigurationActivity::class.java)
        val extras = bundleOf()
        extras.putInt("mySplayWidth", (mySplayImage.width * mySplayImage.scaleX).toInt())
        extras.putInt("mySplayHeight", (mySplayImage.height * mySplayImage.scaleY).toInt())
        extras.putFloat("mySplayX", mySplayImage.x)
        extras.putFloat("mySplayY", mySplayImage.y)
        extras.putFloat("mySplayRotation", mySplayImage.rotation)
        if(circleImage != null) {
            extras.putInt("CircleCardWidth", (circleImage!!.card_width))
            extras.putFloat("CircleX", circleImage!!.xCenter)
            extras.putFloat("CircleY", circleImage!!.yCenter)
        }
        listOfPileImages.forEachIndexed { i, view ->
            extras.putInt("Pile${i}Width", (view.width * view.scaleX).toInt())
            extras.putInt("Pile${i}Height", (view.height * view.scaleY).toInt())
            extras.putFloat("Pile${i}X", view.x)
            extras.putFloat("Pile${i}Y", view.y)
            extras.putFloat("Pile${i}Rotation", view.rotation)
        }
        listOfSplayImages.forEachIndexed { i, view ->
            extras.putInt("Splay${i}Width", (view.width * view.scaleX).toInt())
            extras.putInt("Splay${i}Height", (view.height * view.scaleY).toInt())
            extras.putFloat("Splay${i}X", view.x)
            extras.putFloat("Splay${i}Y", view.y)
            extras.putFloat("Splay${i}Rotation", view.rotation)
        }
        newIntent.putExtras(extras)
        //Add stuff here to add in layout positions
        return newIntent
    }
}
