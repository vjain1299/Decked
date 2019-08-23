package com.ani.decked

import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.content_main.*
import kotlin.math.absoluteValue

class LayoutConfigActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_layout_config)
    }
    private fun createNewGameLayout() {
        val screenWidth = Resources.getSystem().displayMetrics.widthPixels
        val screenHeight = Resources.getSystem().displayMetrics.heightPixels
        val horizontalIncrement = screenWidth/16f
        val verticalIncrement = screenHeight/26f
        //mySplay alignment
        GameState.splays = mutableMapOf(Pair(Preferences.name,Splay(this, assets, constraintContentLayout , Deck.cardsToDeck(Card(13,1)), 600, 200)))
        GameState.splays[Preferences.name]?.height = 8 * verticalIncrement.toInt()
        GameState.splays[Preferences.name]?.width = (12 * horizontalIncrement).toInt()
        // val xCenter = (screenWidth - mySplay.width) / 2
        GameState.splays[Preferences.name]?.setTopLeft((2 * horizontalIncrement).toInt(), screenHeight - (8 * verticalIncrement).toInt())

        //Pile setup
        GameState.mPile?.x = imageView.x
        GameState.mPile?.y = imageView.y - 200
        GameState.mPile?.layoutParams?.width = imageView.width
        GameState.mPile?.layoutParams?.height = imageView.height
        GameState.mPile?.showPile(constraintContentLayout)

        //User Setup
        val numberOfSplaysPerSide = (kotlin.math.floor((0.5 * (GameState.nPlayers - 4)).absoluteValue) * (GameState.nPlayers - 4)/kotlin.math.max((GameState.nPlayers - 4).absoluteValue, 1)).toInt() + 1
        val numberOfSplaysOnTop = GameState.nPlayers - (numberOfSplaysPerSide * 2) - 1
        val verticalMargin = (screenHeight - (4 * verticalIncrement)) / (numberOfSplaysPerSide + 1)
        val horizontalMargin = (screenWidth - (4 * horizontalIncrement)) / (numberOfSplaysOnTop + 1)
        var i = 0
        for ((k,v) in GameState.splays) {
            i++
            if(i > GameState.nPlayers) break
            if(k == Preferences.name) continue
            if(i <= numberOfSplaysPerSide) {
                v.width = verticalIncrement.toInt() * 4
                v.height = horizontalIncrement.toInt() * 4
                v.setTopLeft(-(horizontalIncrement * 2).toInt(), ((i - 1) * v.width) + (i * verticalMargin).toInt())
            }
            if(i > numberOfSplaysPerSide && i <= GameState.nPlayers - numberOfSplaysPerSide) {

            }
        }
        //TODO: Implement Layout Creation
    }
}
