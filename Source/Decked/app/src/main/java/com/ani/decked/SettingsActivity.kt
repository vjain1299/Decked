package com.ani.decked

import android.net.Uri
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.EditTextPreference
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import kotlinx.android.synthetic.main.activity_main.*

//name, color of cards

//bundled with root_preferences_settings_settings.xml

//bundled with root_preferences_settings.xml


class SettingsActivity : AppCompatActivity() {

    val RESULT_SUCCESS = 5
    private lateinit var settings: SettingsFragment
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        settings = SettingsFragment()
        setContentView(R.layout.settings_activity)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settings, settings)
            .commit()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        val savedSettings = Intent(this, MainActivity::class.java)
        savedSettings.putExtra("username", settings.findPreference<EditTextPreference>("name")?.text)
        savedSettings.putExtra("color",settings.findPreference<ListPreference>("color")?.value)
        setResult(RESULT_SUCCESS, savedSettings)
        finish()
        super.onBackPressed()
    }

    private class SettingsFragment : PreferenceFragmentCompat() {

        //var userName : String? = "" /*TODO: This is just here for variable purposes, I do believe that this needs to be moved to the main actiivity*/
        //var color : String = "" // Same comment as above for this
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences_settings, rootKey)
        }

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            //val prefName = findPreference<EditTextPreference>("name")
            //val cardColor = findPreference<ListPreference>("color") /*TODO: Does this mean that I would need to create a cardColor in MainActivity?*/

            /*TODO: Do we need to create a new variable to save the player's name in the main activity?*/
            //userName = prefName?.text
            //color = cardColor!!.value /*TODO: I think we can set the default color to begin with and then just set it here to whatever value shows up from the settings*/

        }
    }
}