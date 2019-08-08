package com.ani.decked

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.widget.ImageView

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.io.IOException

class MainActivity : AppCompatActivity() {
    lateinit var mDeck : Deck
    lateinit var dealtCards : Deck
    lateinit var cardHands : MutableList<Deck>
    var players : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        players = 3
        cardHands = MutableList(players) { startSize ->
            Deck(0)
        }
        fab.setOnClickListener { view ->
            Snackbar.make(view, "New Deck", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
            mDeck.shuffle()
        }
        firstPile.setOnClickListener {
            if(mDeck.deckOfCards.isNotEmpty()) {
                cardHands[0].push(mDeck.pop())
                setCardImage(firstPile, cardHands[0].peek())
            }
        }
        secondPile.setOnClickListener {
            if(mDeck.deckOfCards.isNotEmpty()) {
                cardHands[1].push(mDeck.pop())
                setCardImage(secondPile, cardHands[1].peek())
            }
        }
        thirdPile.setOnClickListener {
            if(mDeck.deckOfCards.isNotEmpty()) {
                cardHands[2].push(mDeck.pop())
                setCardImage(thirdPile, cardHands[2].peek())
            }
        }
        mDeck = Deck(1)
        dealtCards = Deck(0)
        setCardImage(imageView, "purple_back.png")
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> {
                val toSettings = Intent(this, SettingsActivity::class.java)
                startActivity(toSettings)
                true
            }
            R.id.action_text -> {
                mDeck = Deck(1)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                if(!isInBounds(event, imageView)) return true
            }
            MotionEvent.ACTION_MOVE -> {
                imageView.x = event.x - imageView.width/2
                imageView.y = event.y - imageView.height/2
            }
            MotionEvent.ACTION_UP -> {
                imageView.x = event.x - imageView.width/2
                imageView.y = event.y - imageView.height/2
                //Add handling of players here
                var piles = listOf(firstPile, secondPile, thirdPile)
                for(i in 0 until 3) {
                    var pile = piles[i]
                    if(isInBounds(event, pile)) {
                        if(mDeck.deckOfCards.isNotEmpty()) {
                            cardHands[i].push(mDeck.pop())
                            setCardImage(pile, cardHands[i].peek())
                        }
                    }
                }
            }
        }
        return super.onTouchEvent(event)
    }
    private fun getBitmapFromAssets(fileName : String) : Bitmap? {
        return try {
            BitmapFactory.decodeStream(assets.open(fileName))
        } catch (e : IOException) {
            e.printStackTrace()
            null
        }
    }
    private fun setCardImage(imageView : ImageView, card : Card) {
        setCardImage(imageView, card.imagePath)
    }
    private fun setCardImage(imageView : ImageView, fileName: String) {
        val assetsBitmap: Bitmap? = getBitmapFromAssets(fileName)
        imageView.setImageBitmap(assetsBitmap)
    }
    private fun isInBounds(event: MotionEvent, imageView : ImageView) : Boolean {
        val margin = 200
        val leftBound = imageView.x
        val rightBound = imageView.x + imageView.width
        val topBound = imageView.y + margin
        val bottomBound = imageView.y + imageView.height + margin
        val x = event.x
        val y = event.y

        if (x < leftBound || x > rightBound || y < topBound || y > bottomBound) {
            return false
        }
        return true
    }
}
