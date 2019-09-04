package com.ani.decked

import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.view.*
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.lifecycle.ViewModelProvider
import com.ani.decked.GameState.circles
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import com.ani.decked.GameState.gameCode
import com.ani.decked.GameState.hasCircle
import com.ani.decked.GameState.ipAddress
import com.ani.decked.GameState.isGameHost
import com.ani.decked.GameState.nDecks
import com.ani.decked.GameState.nPiles
import com.ani.decked.GameState.nPlayers
import com.ani.decked.GameState.names
import com.ani.decked.GameState.splays
import com.ani.decked.GameState.tablePiles
import com.google.android.gms.common.api.Api
import java.util.Collections.shuffle
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Presents layout to user
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        if(intent.getBooleanExtra("startGame", false)) {
            //Gets data from intent
            if(isGameHost) {
                thread { GameState.serverObject = ServerObject(this) }
            }
            else {
                thread { GameState.clientObject = ClientObject(ipAddress,this)}
            }

            //Gets names from intent
            for(i in 1..nPlayers) { if(i == 1) names.add("Player") else names.add("Player$i") }

            //Set element positions from intent

            splays[names[0]] = splays[names[0]] ?: Pair(Splay(
                this,
                intent.getIntExtra("MySplayWidth", 600),
                intent.getIntExtra("MySplayHeight", 200),
                intent.getFloatExtra("MySplayX", 0f),
                intent.getFloatExtra("MySplayY", 0f)).apply{ rotation = intent.getFloatExtra("MySplayRotation", 0f)} ,
                SplayModel(Deck(), intent.getIntExtra("MySplayWidth", 600), intent.getIntExtra("MySplayHeight", 200), assets, resources))
            if(intent.getIntExtra("MySplayDirection", 1) == 0) {
                splays[names[0]]?.second?.flip()
            }
            if (nPiles > 0) {
                tablePiles.add(
                    Pile(
                        Deck(nDecks), assets, this, intent.getIntExtra(
                            "Pile0Width",
                            ViewGroup.LayoutParams.WRAP_CONTENT
                        ), intent.getIntExtra("Pile0Height", ViewGroup.LayoutParams.WRAP_CONTENT),
                        intent.getFloatExtra("Pile0X", 0f), intent.getFloatExtra("Pile0Y", 0f)
                    )
                )
                tablePiles[0].rotation = intent.getFloatExtra("Pile0Rotation", 0f)
            }
            for (i in 1 until nPiles) {
                tablePiles.add(
                    Pile(
                        Deck(),
                        assets,
                        this,
                        intent.getIntExtra("Pile${i}Width", ViewGroup.LayoutParams.WRAP_CONTENT),
                        intent.getIntExtra("Pile${i}Height", ViewGroup.LayoutParams.WRAP_CONTENT),
                        intent.getFloatExtra("Pile${i}X", 0f),
                        intent.getFloatExtra("Pile${i}Y", 0f)
                    )
                )
                tablePiles[i].rotation = intent.getFloatExtra("Pile${i}Rotation", 0f)
            }
            tablePiles.run {
                forEachIndexed {index, pile ->
                    if(intent.getIntExtra("Pile${index}Direction", 1) == 0) {
                        pile.flip()
                    }
                }
            }
            if (hasCircle) {
                circles.add(
                    Circle(
                        this,
                        assets,
                        constraintContentLayout,
                        intent.getIntExtra("CircleCardWidth", 100),
                        intent.getFloatExtra("CircleX", 0f).toInt(),
                        intent.getFloatExtra("CircleY", 0f).toInt()
                    )
                )
            }
            for (i in 0 until (nPlayers - 1)) {
                splays[names[i + 1]] = Pair(Splay(this,
                    intent.getIntExtra("Splay${i}Width",200),
                    intent.getIntExtra("Splay${i}Height", 100),
                    intent.getFloatExtra("Splay${i}X", 0f),
                    intent.getFloatExtra("Splay${i}Y", 0f)).apply { rotation = intent.getFloatExtra("Splay${i}Rotation", 0f)},
                    SplayModel(Deck(),intent.getIntExtra("Splay${i}Width",200), intent.getIntExtra("Splay${i}Height", 100), assets, resources))
                if(intent.getIntExtra("Splay${i}Direction", 1) == 0) {
                    splays[names[i + 1]]?.second?.flip()
                }
            }
            //it takes that data from the splay model and observes it forever
            for((k,v) in splays) {
                //whenever the bitmap image changes, it will update it
                v.second.bitmapData.observeForever {
                    v.first.setImageBitmap(it)
                }
            }
        }
        //All event handlers should go below this point
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Flip", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
            tablePiles[0].animate().scaleX(0.01f).setDuration(1000).start()
            tablePiles[0].flip()
            tablePiles[0].animate().scaleX(1f).setDuration(1000).setStartDelay(1000).start()
            splays[Preferences.name]?.second?.flip()
        }
    }

    // All are being added to the UI
    override fun onStart() {
        super.onStart()
        circles.forEach {
            it.setViewPositions()
            it.showCircle(constraintContentLayout)
        }
        tablePiles.forEach {
            it.showPile(constraintContentLayout)
        }
        splays.forEach {
            it.value.first.showSplay(constraintContentLayout)
        }
    }

    //It inflates the menu --> means that it displays the xml file on the UI
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    //You can select either settings or the shuffle option
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                val toSettings = Intent(this, SettingsActivity::class.java)
                startActivityForResult(toSettings, 5)
                true
            }
            R.id.action_shuffle -> {
                tablePiles[0].shuffle()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    //This is how to pass the data back when we hit the back button
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
