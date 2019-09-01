package com.ani.decked

import android.content.Intent
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity

import kotlinx.android.synthetic.main.activity_start_screen.*
import kotlinx.android.synthetic.main.content_start_screen.*

class StartScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_screen)
        setSupportActionBar(toolbar)

        hostGameButton.setOnClickListener {
            GameState.isGameHost = true
            startActivity(Intent(this, HostGameActivity::class.java))
        }
        joinGameButton.setOnClickListener {
            GameState.isGameHost = false
            startActivity(Intent(this, JoinGameActivity::class.java))
        }
    }
}
