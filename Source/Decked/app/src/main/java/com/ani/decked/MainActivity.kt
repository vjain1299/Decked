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
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity() {
    lateinit var mFirestore: FirebaseFirestore
    lateinit var mFirebaseAuth : FirebaseAuth
    lateinit var mPile : Pile
    lateinit var dealtCards : Deck
    lateinit var cardHands : MutableList<Pile>
    var players : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        players = 3
        var piles = listOf(firstPile, secondPile, thirdPile)
        cardHands = MutableList(players) { startSize ->
            Pile(piles[startSize], Deck(0), assets)
        }
        fab.setOnClickListener { view ->
            Snackbar.make(view, "New Deck", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
            mPile.shuffle()
        }
        mPile = Pile(imageView, Deck(1), assets)
        dealtCards = Deck(0)
        mPile.updateImageView()
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
                mPile.resetDeck(1)
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
                        if(!mPile.isEmpty()) {
                            cardHands[i].push(mPile.pop())
                        }
                    }
                }
            }
        }
        return super.onTouchEvent(event)
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

    override fun onStart() {
        super.onStart()
        mFirestore = FirebaseFirestore.getInstance()
        mFirebaseAuth = FirebaseAuth.getInstance()
    }

}
