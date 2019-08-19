package com.ani.decked

import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.lang.Math.*
import kotlin.math.absoluteValue

class MainActivity : AppCompatActivity() {
    lateinit var mFirestore: FirebaseFirestore
    lateinit var mFirebaseAuth : FirebaseAuth
    lateinit var mPile : Pile
    var nPlayers : Int = 1
    var nPiles : Int = 1
    lateinit var gameCode : String

    lateinit var mySplay: Splay
    var opponentSplays : MutableMap<String, Splay>? = null
    lateinit var playerCardStrings : MutableMap<String, String>
    lateinit var tablePiles : MutableList<String>
    var gameObject : GameContainer = GameContainer()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Flip", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
            mPile.flip()
            mySplay.flip()
        }
        val nDecks = intent.extras?.get("decks") ?: 1
        mPile = Pile(Deck(nDecks as Int), assets, this)

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

            }
            MotionEvent.ACTION_MOVE -> {

            }
            MotionEvent.ACTION_UP -> {

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
        mFirebaseAuth = FirebaseAuth.getInstance()
        var currentUser = mFirebaseAuth.currentUser
        if(currentUser == null) {
            mFirebaseAuth.signInAnonymously()
                .addOnCompleteListener(this) { task ->
                    if(task.isSuccessful) {
                        currentUser = mFirebaseAuth.currentUser
                    }
                    else {
                        Toast.makeText(baseContext, "Authentication Failed", Toast.LENGTH_SHORT).show()
                    }
                }
        }
        mFirestore = FirebaseFirestore.getInstance()
        getFirestoreData()
    }

    private fun createNewGameLayout() {
        val screenWidth = Resources.getSystem().displayMetrics.widthPixels
        val screenHeight = Resources.getSystem().displayMetrics.heightPixels
        val horizontalIncrement = screenWidth/16f
        val verticalIncrement = screenHeight/26f
        //mySplay alignment
        mySplay = Splay(baseContext, assets, constraintContentLayout , Deck.cardsToDeck(Card(13,1)), 600, 200)
        mySplay.height = 8 * verticalIncrement.toInt()
        mySplay.width = (12 * horizontalIncrement).toInt()
        // val xCenter = (screenWidth - mySplay.width) / 2
        mySplay.setTopLeft((2 * horizontalIncrement).toInt(), screenHeight - (8 * verticalIncrement).toInt())

        //Pile setup
        mPile.x = imageView.x
        mPile.y = imageView.y
        mPile.layoutParams.width = imageView.width
        mPile.layoutParams.height = imageView.height
        mPile.showPile(constraintContentLayout)

        //User Setup
        val numberOfSplaysPerSide = (kotlin.math.floor((0.5 * (nPlayers - 4)).absoluteValue) * (nPlayers - 4)/kotlin.math.max((nPlayers - 4).absoluteValue, 1)).toInt() + 1
        val numberOfSplaysOnTop = nPlayers - (numberOfSplaysPerSide * 2) - 1
        val verticalMargin = (screenHeight - (4 * verticalIncrement)) / (numberOfSplaysPerSide + 1)
        val horizontalMargin = (screenWidth - (4 * horizontalIncrement)) / (numberOfSplaysOnTop + 1)
        var i = 0
        for ((k,v) in opponentSplays!!) {
            i++
            if(i <= numberOfSplaysPerSide) {
                v.width = verticalIncrement.toInt() * 4
                v.height = horizontalIncrement.toInt() * 4
                v.setTopLeft(-(horizontalIncrement * 2).toInt(), ((i - 1) * v.width) + (i * verticalMargin).toInt())
            }
        }
        //TODO: Implement Layout Creation
    }
    fun getFirestoreData() {
        gameCode = intent.extras?.get("gameCode") as String
        val gameDocRef = mFirestore.collection("games").document(gameCode)
         if(gameObject.playerNum == null)
            gameDocRef.get()
                .addOnSuccessListener { result ->
                    if (result == null) {
                        Log.w("Getting Firestore Data", "Result is null.")
                    }
                    gameObject = result?.toObject(GameContainer::class.java) ?: GameContainer()
                    getFirestoreVars()
                    createNewGameLayout()
                }
                .addOnFailureListener { e ->
                    Log.w("Getting Firestore Data", "Exception: $e")
                    gameObject = GameContainer()
                }
        gameDocRef.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
            if(documentSnapshot != null) {
                gameObject = documentSnapshot.toObject(GameContainer::class.java)?: GameContainer()
                getFirestoreVars()
            }
            else {
                Toast.makeText(baseContext, "Exception: " + firebaseFirestoreException?.localizedMessage, Toast.LENGTH_LONG).show()
            }
        }
    }
    fun updateFirestore() {
        mFirestore.collection("games").document(gameCode).set(gameObject, SetOptions.merge() /* TODO: This might cause future problems*/)
    }
    fun getFirestoreVars() {
        nPlayers = gameObject.playerNum?:1
        tablePiles = gameObject.table!!
        nPiles = tablePiles.size
        if(gameObject.players.isNullOrEmpty()) {
            gameObject.players = mutableMapOf(Pair(Preferences.playerName, mySplay.toString()))
        }
        playerCardStrings = gameObject.players!!
        updateOpponentSplays()
    }
    fun setFirestoreVars() {
        gameObject.table = tablePiles
        if(gameObject.players == null) {
            gameObject.players = mutableMapOf(Pair(Preferences.playerName, mySplay.toString()))
        }
        else {
            gameObject.players!![Preferences.playerName] = mySplay.toString()
        }
    }
    fun updateMySplay() {
        val newDeck = Deck.stringToDeck(gameObject.players!![Preferences.playerName]?: mySplay.toString())
        mySplay.reconstructFromDeck(newDeck)
    }
    fun updateOpponentSplays() {
        if(opponentSplays == null) {
            opponentSplays = mutableMapOf()
        }
        else {
            for ((k, v) in playerCardStrings) {
                opponentSplays!![k] = Splay(this, assets, constraintContentLayout, Deck.stringToDeck(v), 100, 100)
            }
        }
    }
}
