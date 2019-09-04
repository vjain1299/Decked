package com.ani.decked

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_configuration.*
import kotlinx.android.synthetic.main.activity_join_game.*
import java.lang.Exception

//It's the same stuff as the hostGameActivity but this is for the join game button
//It gets the document from firebase wheras the hostgameactivity sets the document
class JoinGameActivity : AppCompatActivity() {
    lateinit var mFirestore : FirebaseFirestore
    lateinit var mFirebaseAuth : FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join_game)
        goToLayout.setOnClickListener {
            try {
                GameState.playerName = nameText.text.toString()
            } catch (e: Exception){
                Snackbar.make(it, "Please enter a name", Snackbar.LENGTH_LONG)
                return@setOnClickListener
            }
            val games = mFirestore.collection("games")
            val docRef = games.document(gameCodeText.text.toString())
            val doc = docRef.get()
            doc.addOnCompleteListener { task ->
                if(task.isSuccessful) {
                    GameState.ipAddress = ipAddressText.text.toString()
                    GameState.nPlayers = (task.result!!["nPlayers"] as Long).toInt()
                    GameState.nPiles = (task.result!!["nPiles"] as Long).toInt()
                    GameState.hasCircle = task.result!!["hasCircle"] as Boolean
                    startActivity(Intent(this, LayoutConfigActivity::class.java))
                }
                else {
                    Snackbar.make(it, "Game not found", Snackbar.LENGTH_LONG)
                }
            }
        }
    }
    override fun onStart() {
        super.onStart()
        mFirestore = FirebaseFirestore.getInstance()
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
    }
}
