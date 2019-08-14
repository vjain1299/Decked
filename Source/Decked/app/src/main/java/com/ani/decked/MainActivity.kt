package com.ani.decked

import android.content.Intent
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
    lateinit var cardHandSplay : Splay
    var players : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        players = 1
        cardHandSplay = Splay(baseContext, assets, constraintContentLayout , Deck(), 600, 200)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Flip", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
            mPile.flip()
            cardHandSplay.flip()
        }
        val nDecks = intent.extras?.get("decks") ?: 1
        mPile = Pile(Deck(nDecks as Int), assets, imageView)
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

            }
            MotionEvent.ACTION_UP -> {
                //Add handling of players here
                if(isInBounds(event, cardHandSplay)) {
                    if (!mPile.isEmpty()) {
                        cardHandSplay.add(mPile.pop())
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

    private fun isInBounds(event: MotionEvent, splay : Splay) : Boolean {
        val margin = 200
        val leftBound = splay.x
        val rightBound = splay.x + splay.width
        val topBound = splay.y + margin
        val bottomBound = splay.y + splay.height + margin
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
        val leftMargin = 100
        cardHandSplay.setTopLeft(leftMargin, 32)
        cardHandSplay.add(Card(13, 1))
    }

    fun setLayout() {
        val players = intent.extras?.get("players") ?: 1
        val piles = intent.extras?.get("piles") ?: 1
    }
}
