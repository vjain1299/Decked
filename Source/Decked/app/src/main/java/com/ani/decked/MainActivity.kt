package com.ani.decked

import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.view.*
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import androidx.core.app.ActivityCompat.startActivityForResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import com.ani.decked.GameState.gameCode
import com.ani.decked.GameState.isGameHost
import com.ani.decked.GameState.mCircle
import com.ani.decked.GameState.mPile
import com.ani.decked.GameState.nDecks
import com.ani.decked.GameState.nPiles
import com.ani.decked.GameState.nPlayers
import com.ani.decked.GameState.names
import com.ani.decked.GameState.splays

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Presents layout to user
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        //Gets data from intent
        nPiles = intent.getIntExtra("nPiles", nPiles)
        nDecks = intent.getIntExtra("nDecks", nDecks)
        nPlayers = intent.getIntExtra("nPlayers", nPlayers)
        gameCode = intent.extras?.get("gameCode") as String
        isGameHost = intent.getBooleanExtra("isGameHost", false)

        //Gets names from intent
        for(i in 1..nPlayers) { if(i == 1) names.add("Player") else names.add("Player$i") }

        mCircle = mCircle?:Circle(this, assets, constraintContentLayout, 200, Resources.getSystem().displayMetrics.widthPixels/2 - 100, (Resources.getSystem().displayMetrics.heightPixels/2) - 300)
        val screenWidth = Resources.getSystem().displayMetrics.widthPixels
        val screenHeight = Resources.getSystem().displayMetrics.heightPixels
        val horizontalIncrement = screenWidth/16f
        val verticalIncrement = screenHeight/26f
        splays = if(splays.isEmpty()) mutableMapOf(Pair(Preferences.name,Splay(this, assets, constraintContentLayout , Deck.cardsToDeck(Card(13,1)), 600, 200))) else splays
        splays[Preferences.name]?.height = 8 * verticalIncrement.toInt()
        splays[Preferences.name]?.width = (12 * horizontalIncrement).toInt()
        splays[Preferences.name]?.setTopLeft((2 * horizontalIncrement).toInt(), screenHeight - (8 * verticalIncrement).toInt())
        mPile = Pile(Deck(nDecks), assets, this)
        mPile?.x = imageView.x
        mPile?.y = imageView.y - 200
        mPile?.layoutParams?.width = imageView.width
        mPile?.layoutParams?.height = imageView.height
        if(mPile?.parent == null) {
            mPile!!.showPile(constraintContentLayout)
        }
        //All event handlers should go below this point
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Flip", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
            mPile?.flip()
            splays[Preferences.name]?.flip()
        }
    }
    override fun onStart() {
        super.onStart()
        mCircle?.setViewPositions()
        mCircle?.showCircle()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                val toSettings = Intent(this, SettingsActivity::class.java)
                startActivityForResult(toSettings, 5)
                true
            }
            R.id.action_shuffle -> {
                mPile?.shuffle()
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
}
