package com.ani.decked

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.PopupWindow
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import androidx.preference.EditTextPreference
import androidx.preference.PreferenceFragmentCompat
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_configuration.*
import kotlinx.android.synthetic.main.settings_activity.*

class ConfigurationActivity : AppCompatActivity() {
    lateinit var mFirestore : FirebaseFirestore
    lateinit var mFirebaseAuth : FirebaseAuth
    val CODE_LENGTH = 9

    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                editText.visibility = View.INVISIBLE
                gameJoinButton.visibility = View.INVISIBLE
                floatingActionButton.visibility = View.VISIBLE
                includeConfig.visibility = View.VISIBLE
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_dashboard -> {
                includeConfig.visibility = View.INVISIBLE
                floatingActionButton.visibility = View.INVISIBLE
                editText.visibility = View.VISIBLE
                gameJoinButton.visibility = View.VISIBLE
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_configuration)

        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)

        val settingsFragment = SettingsFragment()

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settings, settingsFragment)
            .commit()

        floatingActionButton.setOnClickListener { view ->
            Toast.makeText(baseContext, "Generating Game", Snackbar.LENGTH_LONG).show()
            val nPlayers = settingsFragment.nPlayers
            val nDecks = settingsFragment.nDecks
            val nPiles = settingsFragment.nPiles
            val gameCode = generateGameCode()
            generateGame(nPlayers, nDecks, nPiles, gameCode, view)

            /* val newGameIntent = Intent(this, MainActivity::class.java)
            newGameIntent.putExtra("players", nPlayers)
            newGameIntent.putExtra("decks", nDecks)
            newGameIntent.putExtra("piles", nPiles)
            startActivity(newGameIntent) */
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
        editText.addTextChangedListener { editableText ->
                gameJoinButton.isEnabled = editableText.toString().isNotBlank()
        }

        val games = mFirestore.collection("games")

        gameJoinButton.setOnClickListener {view ->
            val docRef = games.document(editText.text.toString())
            val doc = docRef.get()
            doc.addOnCompleteListener { task ->
                if(task.isSuccessful) {
                    val startGame = Intent(this, MainActivity::class.java)
                    startGame.putExtra("gameID", editText.text.toString())
                    startActivity(startGame)
                }
                else {
                    Snackbar.make(view, "Game not found", Snackbar.LENGTH_LONG)
                }
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
    private fun generateGame(nPlayers : Int, nDecks : Int, nPiles : Int, gameCode : String, view : View) {
        val gameContainer = GameContainer(nPlayers)
        //TODO: Implement Multiple Piles and Decks
        mFirestore.collection("games").document(gameCode).set(gameContainer)
            .addOnSuccessListener { AlertDialog.Builder(baseContext).create().setMessage("Game Code: $gameCode") }
            .addOnFailureListener { Toast.makeText(baseContext, "Failed to create game", Snackbar.LENGTH_LONG).show() }
    }
    private class SettingsFragment : PreferenceFragmentCompat() {
        var nPlayers = 1
        var nDecks = 1
        var nPiles = 1
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
        }
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            val playerNum = findPreference<EditTextPreference>("playerNum")
            val deckNum = findPreference<EditTextPreference>("deckNum")
            val pileNum = findPreference<EditTextPreference>("pileNum")

            if(playerNum!!.text == null) {
                playerNum.setDefaultValue(1)
            }
            else nPlayers = playerNum.text.toInt()
            if(deckNum!!.text == null) {
                deckNum.setDefaultValue(1)
            }
            else nDecks = deckNum.text.toInt()
            if(pileNum!!.text == null) {
                pileNum.setDefaultValue(1)
            }
            else nPiles = pileNum.text.toInt()

            playerNum.setOnPreferenceChangeListener { preference, newValue ->
                (preference as EditTextPreference).text = newValue as String
                nPlayers = newValue.toInt()
                true
            }
            deckNum.setOnPreferenceChangeListener { preference, newValue ->
                (preference as EditTextPreference).text = newValue as String
                nDecks = newValue.toInt()
                true
            }
            pileNum.setOnPreferenceChangeListener { preference, newValue ->
                (preference as EditTextPreference).text = newValue as String
                nPiles = newValue.toInt()
                true
            }
        }
    }
}

