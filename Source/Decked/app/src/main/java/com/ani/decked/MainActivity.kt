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
import com.ani.decked.GameState
import com.ani.decked.GameState.gameCode
import com.ani.decked.GameState.gameObject
import com.ani.decked.GameState.mPile
import com.ani.decked.GameState.nPiles
import com.ani.decked.GameState.nPlayers
import com.ani.decked.GameState.names
import com.ani.decked.GameState.playerCardStrings
import com.ani.decked.GameState.splays
import com.ani.decked.GameState.tablePiles
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {
    lateinit var mFirestore: FirebaseFirestore
    lateinit var mFirebaseAuth : FirebaseAuth
    lateinit var serverObject : ServerObject
    lateinit var mCircle: Circle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Flip", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
            mPile?.flip()
            splays[Preferences.name]?.flip()
        }
        val nDecks = intent.extras?.get("decks") ?: 1
        mPile = mPile?:Pile(Deck(nDecks as Int), assets, this)
        if(intent.getBooleanExtra("isGameHost", false))
            thread {
                serverObject = ServerObject()
            }
        nPlayers = intent.getIntExtra("nPlayers", nPlayers)
        for(i in 1..nPlayers) {
            names.add(intent.getStringExtra("player$i"))
        }
        mCircle = Circle(this, assets, constraintContentLayout, 200, Resources.getSystem().displayMetrics.widthPixels/2, Resources.getSystem().displayMetrics.heightPixels/2)
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
                mPile?.resetDeck(1)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

     override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == 5){
            if(resultCode == 5){
                Preferences.name = data?.getStringExtra("username")?:"Player"
                Preferences.color = data?.getStringExtra("color")?:"purple"
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
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
        mCircle.setViewPositions()
        mCircle.showCircle()
    }
    //override fun onResume() {
    //    createNewGameLayout() //TODO: Ensure that we add cards where cards are due
    //    super.onResume()
    // }

    private fun createNewGameLayout() {
        val screenWidth = Resources.getSystem().displayMetrics.widthPixels
        val screenHeight = Resources.getSystem().displayMetrics.heightPixels
        val horizontalIncrement = screenWidth/16f
        val verticalIncrement = screenHeight/26f
        //mySplay alignment
        splays = mutableMapOf(Pair(Preferences.name,Splay(this, assets, constraintContentLayout , Deck.cardsToDeck(Card(13,1)), 600, 200)))
        splays[Preferences.name]?.height = 8 * verticalIncrement.toInt()
        splays[Preferences.name]?.width = (12 * horizontalIncrement).toInt()
        // val xCenter = (screenWidth - mySplay.width) / 2
        splays[Preferences.name]?.setTopLeft((2 * horizontalIncrement).toInt(), screenHeight - (8 * verticalIncrement).toInt())

        //Pile setup
        mPile?.x = imageView.x
        mPile?.y = imageView.y
        mPile?.layoutParams?.width = imageView.width
        mPile?.layoutParams?.height = imageView.height
        mPile?.showPile(constraintContentLayout)

        //User Setup
        val numberOfSplaysPerSide = (kotlin.math.floor((0.5 * (nPlayers - 4)).absoluteValue) * (nPlayers - 4)/kotlin.math.max((nPlayers - 4).absoluteValue, 1)).toInt() + 1
        val numberOfSplaysOnTop = nPlayers - (numberOfSplaysPerSide * 2) - 1
        val verticalMargin = (screenHeight - (4 * verticalIncrement)) / (numberOfSplaysPerSide + 1)
        val horizontalMargin = (screenWidth - (4 * horizontalIncrement)) / (numberOfSplaysOnTop + 1)
        var i = 0
        for ((k,v) in splays) {
            i++
            if(i > nPlayers) break
            if(k == Preferences.name) continue
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
                    createNewGameLayout()
                }
                .addOnFailureListener { e ->
                    Log.w("Getting Firestore Data", "Exception: $e")
                    gameObject = GameContainer()
                }
        gameDocRef.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
            if(documentSnapshot != null) {
                gameObject = documentSnapshot.toObject(GameContainer::class.java)?: GameContainer()
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
            gameObject.players = mutableMapOf(Pair(Preferences.name, splays[Preferences.name].toString()))
        }
        playerCardStrings = gameObject.players!!
        // updateOpponentSplays()
    }
    fun setFirestoreVars() {
        gameObject.table = tablePiles
        if(gameObject.players == null) {
            gameObject.players = mutableMapOf(Pair(Preferences.name, splays[Preferences.name].toString()))
        }
        else {
            gameObject.players!![Preferences.name] = splays[Preferences.name].toString()
        }
    }
    fun updateMySplay() {
        val newDeck = Deck.stringToDeck(gameObject.players!![Preferences.name]?: splays[Preferences.name].toString())
        splays[Preferences.name]?.reconstructFromDeck(newDeck)
    }
    fun updateOpponentSplays() {
        for ((k, v) in playerCardStrings) {
            splays[k] = Splay(this, assets, constraintContentLayout, Deck.stringToDeck(v), 100, 100)
        }
    }
}
