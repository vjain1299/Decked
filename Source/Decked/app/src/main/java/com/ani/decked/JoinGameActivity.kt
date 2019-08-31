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
            val docRef = games.document(editText.text.toString())
            val doc = docRef.get()
            doc.addOnCompleteListener { task ->
                if(task.isSuccessful) {
                    GameState.ipAddress = task.result!!["ipAddress"] as String
                    startActivity(Intent(this, MainActivity::class.java))
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
