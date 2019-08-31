package com.ani.decked

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.os.bundleOf
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_host_game.*
import java.lang.Exception

class HostGameActivity : AppCompatActivity() {
    val CODE_LENGTH = 9
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_host_game)
        goToLayout.setOnClickListener {
            Snackbar.make(it, "Configure the elements on the screen.", Snackbar.LENGTH_LONG)
            try {
                GameState.nPlayers = playerNum.text.toString().toInt()
                GameState.nPiles = pileNum.text.toString().toInt()
                GameState.nDecks = deckNum.text.toString().toInt()
                GameState.hasCircle = circleToggle.isChecked
                GameState.gameCode = generateGameCode()
                GameState.playerName = nameText.text.toString()
                Toast.makeText(baseContext, "Generating Game", Toast.LENGTH_LONG).show()
                startActivity(Intent(this, LayoutConfigActivity::class.java))
            } catch (e : Exception) {
                Snackbar.make(it, "Please ensure that all fields are properly filled out.", Snackbar.LENGTH_LONG)
            }
        }
    }
    private fun generateGameCode() : String {
        var result = ""
        for (i in 1..CODE_LENGTH) {
            var newChar = 48 + Math.random() * 31
            if(newChar > 57) newChar +=12
            result += newChar.toChar()
        }
        return result
    }
}
