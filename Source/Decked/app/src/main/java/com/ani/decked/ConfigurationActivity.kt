package com.ani.decked

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.AttributeSet
import android.view.View
import android.widget.PopupWindow
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.ani.decked.GameState.gameCode
import com.ani.decked.GameState.ipAddress
import com.ani.decked.GameState.nPiles
import com.ani.decked.GameState.serverObject
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_configuration.*
import java.lang.Integer.parseInt
import kotlin.concurrent.thread
import androidx.preference.CheckBoxPreference
import com.ani.decked.GameState.hasCircle
import com.ani.decked.GameState.nDecks
import com.ani.decked.GameState.nPlayers
import com.ani.decked.GameState.splays
import com.ani.decked.GameState.tablePiles


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

        val settingsFragment = SettingsFragment(this)
        tablePiles.clear()
        splays.clear()


        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settings, settingsFragment)
            .commit()

        floatingActionButton.setOnClickListener { view ->
            Toast.makeText(baseContext, "Generating Game", Toast.LENGTH_LONG).show()
            nPlayers = parseInt(settingsFragment.findPreference<EditTextPreference>("playerNum")?.text?:"0")
            nDecks = parseInt(settingsFragment.findPreference<EditTextPreference>("deckNum")?.text?:"0")
            nPiles = parseInt(settingsFragment.findPreference<EditTextPreference>("pileNum")?.text?:"0")
            hasCircle = settingsFragment.findPreference<CheckBoxPreference>("addCircle")?.isChecked ?: false
            gameCode = generateGameCode()
            generateGame()
            val newGameIntent = Intent(this, MainActivity::class.java)
            newGameIntent.putExtras(bundleOf(Pair("startGame",true),Pair("gameCode", gameCode), Pair("nPlayers", nPlayers), Pair("isGameHost", true), Pair("nPiles", nPiles), Pair("nDecks", nDecks)))
            newGameIntent.putExtras(intent)
            startActivity(newGameIntent)
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
                    ipAddress = task.result!!["ipAddress"] as String
                    thread {
                        GameState.clientObject = ClientObject(ipAddress, GameState.clientEventManager!!)
                    }
                    startActivity(Intent(this, MainActivity::class.java))
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
    private fun generateGame() {
        thread { serverObject = ServerObject() }
    }

    class SettingsFragment(private val activity: Activity) : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
        }
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            val playerNum = findPreference<EditTextPreference>("playerNum")
            val deckNum = findPreference<EditTextPreference>("deckNum")
            val pileNum = findPreference<EditTextPreference>("pileNum")
            val layoutMover = findPreference<Preference>("layoutConfig")
            layoutMover?.setOnPreferenceClickListener {
                val configureIntent = Intent(activity, LayoutConfigActivity::class.java)
                nPlayers = parseInt(findPreference<EditTextPreference>("playerNum")?.text?:"0")
                nDecks = parseInt(findPreference<EditTextPreference>("deckNum")?.text?:"0")
                nPiles = parseInt(findPreference<EditTextPreference>("pileNum")?.text?:"0")
                hasCircle = findPreference<CheckBoxPreference>("addCircle")?.isChecked ?: false
                startActivity(configureIntent)
                true
            }
            playerNum?.setOnBindEditTextListener { editText ->
                editText.inputType = InputType.TYPE_CLASS_NUMBER
            }
            deckNum?.setOnBindEditTextListener { editText ->
                editText.inputType = InputType.TYPE_CLASS_NUMBER
            }
            pileNum?.setOnBindEditTextListener { editText ->
                editText.inputType = InputType.TYPE_CLASS_NUMBER
            }
        }
    }
}

