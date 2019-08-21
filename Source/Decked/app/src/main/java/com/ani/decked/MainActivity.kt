package com.ani.decked

import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.*
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity;
import android.widget.ImageView
import android.widget.Toast
import androidx.core.view.GestureDetectorCompat
import androidx.preference.PreferenceFragmentCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.settings_activity.*
import java.lang.Math.*
import kotlin.math.absoluteValue

class MainActivity : AppCompatActivity() {
    lateinit var mFirestore: FirebaseFirestore
    lateinit var mFirebaseAuth : FirebaseAuth
    lateinit var mPile : Pile
    var nPlayers : Int = 1
    var nPiles : Int = 1
    lateinit var gameCode : String
    var splays : MutableMap<String, Splay> = mutableMapOf()
    lateinit var playerCardStrings : MutableMap<String, String>
    lateinit var tablePiles : MutableList<String>
    lateinit var circles : ArrayList<Card> //TODO: Need to implement circles on table here
    var gameObject : GameContainer = GameContainer()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Flip", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
            mPile.flip()
            splays[Preferences.playerName]?.flip()
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
                startActivityForResult(toSettings, 5)
                true
            }
            R.id.action_text -> {
                mPile.resetDeck(1)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

     override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == 5){
            if(resultCode == 5){
                Preferences.playerName = data?.getStringExtra("username")?:"Player"
                Preferences.color = data?.getStringExtra("color")?:"purple"
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    fun checkTouch(event: MotionEvent, cardView : CardDisplayView?) {
        for((key ,splay) in splays) {
            if (isInBounds(event, splay)) {
                if (cardView?.card != null) {
                    constraintContentLayout.removeView(cardView)
                    cardView.parent = splay
                    val calculatedIndex = splay.indexOfEvent(event) + 1
                    val index = if(calculatedIndex > splay.count()) splay.count() else calculatedIndex
                    splay.add(index , cardView.card!!)
                    return
                }
            }
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
        val leftBound = imageView.x
        val rightBound = imageView.x + imageView.width
        val topBound = imageView.y
        val bottomBound = imageView.y + imageView.height
        val x = event.rawX
        val y = event.rawY

        if (x < leftBound || x > rightBound || y < topBound || y > bottomBound) {
            return false
        }
        return true
    }

    private fun isInBounds(event: MotionEvent, splay : Splay) : Boolean {
        val leftBound = splay.x
        val rightBound = splay.x + splay.width
        val topBound = splay.y
        val bottomBound = splay.y + splay.height
        val x = event.rawX
        val y = event.rawY

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
    override fun onResume() {
        createNewGameLayout() //TODO: Ensure that we add cards where cards are due
        super.onResume()
    }

    private fun createNewGameLayout() {
        val screenWidth = Resources.getSystem().displayMetrics.widthPixels
        val screenHeight = Resources.getSystem().displayMetrics.heightPixels
        val horizontalIncrement = screenWidth/16f
        val verticalIncrement = screenHeight/26f
        //mySplay alignment
        splays = mutableMapOf(Pair(Preferences.playerName,Splay(this, assets, constraintContentLayout , Deck.cardsToDeck(Card(13,1)), 600, 200)))
        splays[Preferences.playerName]?.height = 8 * verticalIncrement.toInt()
        splays[Preferences.playerName]?.width = (12 * horizontalIncrement).toInt()
        // val xCenter = (screenWidth - mySplay.width) / 2
        splays[Preferences.playerName]?.setTopLeft((2 * horizontalIncrement).toInt(), screenHeight - (8 * verticalIncrement).toInt())

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
        for ((k,v) in splays) {
            i++
            if(i > nPlayers) break
            if(k == Preferences.playerName) continue
            if(i <= numberOfSplaysPerSide) {
                v.width = verticalIncrement.toInt() * 4
                v.height = horizontalIncrement.toInt() * 4
                v.setTopLeft(-(horizontalIncrement * 2).toInt(), ((i - 1) * v.width) + (i * verticalMargin).toInt())
            }
            if(i > numberOfSplaysPerSide && i <= nPlayers - numberOfSplaysPerSide) {

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
            gameObject.players = mutableMapOf(Pair(Preferences.playerName, splays[Preferences.playerName].toString()))
        }
        playerCardStrings = gameObject.players!!
        updateOpponentSplays()
    }
    fun setFirestoreVars() {
        gameObject.table = tablePiles
        if(gameObject.players == null) {
            gameObject.players = mutableMapOf(Pair(Preferences.playerName, splays[Preferences.playerName].toString()))
        }
        else {
            gameObject.players!![Preferences.playerName] = splays[Preferences.playerName].toString()
        }
    }
    fun updateMySplay() {
        val newDeck = Deck.stringToDeck(gameObject.players!![Preferences.playerName]?: splays[Preferences.playerName].toString())
        splays[Preferences.playerName]?.reconstructFromDeck(newDeck)
    }
    fun updateOpponentSplays() {
        for ((k, v) in playerCardStrings) {
            splays[k] = Splay(this, assets, constraintContentLayout, Deck.stringToDeck(v), 100, 100)
        }
    }
}
