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
        setContentView(R.layout.activity_layout_config) //This inflates the xml file
        for(i in 1..nPiles) {
            val image = HolderView("pile",this, assets)
            listOfPileImages.add(image)
        }
        for(i in 1..nPlayers) {
            if(i == 1) {
                val image = HolderView("mySplay",this, assets)
                mySplayImage = image
            }
            else {
                val image = HolderView("splay",this, assets)
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
        with(mySplayImage) {
            layoutParams.height = (8 * verticalIncrement).toInt()
            layoutParams.width = (12 * horizontalIncrement).toInt()
            x = 2 * horizontalIncrement
            y =  screenHeight - (7 * verticalIncrement)
        }
        val numberOfSplaysPerSide = (kotlin.math.floor((0.5 * (nPlayers - 4)).absoluteValue) * (nPlayers - 4)/kotlin.math.max((nPlayers - 4).absoluteValue, 1)).toInt() + 1
        val numberOfSplaysOnTop = nPlayers - (numberOfSplaysPerSide * 2) - 1
        val verticalMargin = ((screenHeight - (7 * verticalIncrement.toInt() ) - if(numberOfSplaysOnTop > 1) (3 * verticalIncrement).toInt() else 0) - (numberOfSplaysPerSide * 6 * verticalIncrement)) / (numberOfSplaysPerSide + 1)
        val horizontalMargin = (screenWidth - (6 * horizontalIncrement * numberOfSplaysOnTop)) / (numberOfSplaysOnTop + 1)

        //Left side layout
        for(i in 0 until numberOfSplaysPerSide) {
            //set the height and width of the splay
            with(listOfSplayImages[i]) {
                layoutParams.width = (6 * verticalIncrement).toInt()
                layoutParams.height = (4 * horizontalIncrement).toInt()
                rotation = 90f
                x = -2*horizontalIncrement
                y = ((i + 1) * verticalMargin) + (i * 6 * verticalIncrement) + if(numberOfSplaysOnTop > 1) (3 * verticalIncrement).toInt() else 0
            }
        }
        var offset = numberOfSplaysPerSide
        //Top layout
        for(i in 0 until (numberOfSplaysOnTop)) {
            //set the height and width of the splay
            with(listOfSplayImages[i + offset]) {
                layoutParams.width = (6 * verticalIncrement).toInt()
                layoutParams.height = (4 * horizontalIncrement).toInt()
                rotation = 180f
                x = ((i + 1) * horizontalMargin) + (i * 6 * horizontalIncrement)
                y = -1 * verticalIncrement
            }
        }
        offset += numberOfSplaysOnTop
        //Right side layout
        for(i in 0 until numberOfSplaysPerSide) {
            //set the height and width of the splay
            with(listOfSplayImages[i + offset]) {
                layoutParams.width = (6 * verticalIncrement).toInt()
                layoutParams.height = (4 * horizontalIncrement).toInt()
                rotation = 270f
                x = screenWidth - 4*horizontalIncrement
                y = ((i + 1) * verticalMargin) + (i * 6 * verticalIncrement) + if(numberOfSplaysOnTop > 1) (3 * verticalIncrement).toInt() else 0
            }
        }
        for(i in 0 until nPiles) {
            with(listOfPileImages[i]) {
                layoutParams.width = (4 * horizontalIncrement).toInt()
                x = (screenWidth / 2) - (2 * horizontalIncrement)
                y = 4 * verticalIncrement
            }
        }
        if(circleImage != null) {
            with(circleImage!!) {
                xCenter = 7.05f * horizontalIncrement
                yCenter = 13.7f * verticalIncrement
                circleImage!!.card_width = horizontalIncrement.toInt() * 2
                circleImage!!.setViewPositions()
            }
        }

    }

    //If you press back, don't go back, just start the game
    override fun onBackPressed() {
        startActivity(getDepartIntent())
    }

    // You're pressing the soft nav bar back button, just call the onBackPressed()
    override fun onNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    //This generated the intent that contains the data we need to start the game (the positions of each object)
    //A bundle is the only way to transfer data between activities and apps because it only holds primitives
    private fun getDepartIntent() : Intent {
        val newIntent = Intent(this, MainActivity::class.java)
        newIntent.putExtras(mySplayImage.toBundle())
        if(circleImage != null) {
            newIntent.putExtras(circleImage!!.toBundle())
        }
        listOfPileImages.forEachIndexed { i, view ->
            newIntent.putExtras(view.toBundle(i))
        }
        listOfSplayImages.forEachIndexed { i, view ->
            newIntent.putExtras(view.toBundle(i))
        }
        newIntent.putExtra("startGame", true)
        return newIntent
    }
}
