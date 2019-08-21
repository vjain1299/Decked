package com.ani.decked

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.EditTextPreference
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat

//name, color of cards

//bundled with root_preferences_settings_settings.xml

//bundled with root_preferences_settings.xml


class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settings, SettingsFragment())
            .commit()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private class SettingsFragment : PreferenceFragmentCompat() {

        var userName : String = "" /*TODO: This is just here for variable purposes, I do believe that this needs to be moved to the main actiivity*/
        var color : String = "" // Same comment as above for this
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences_settings, rootKey)
        }

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            val prefName = findPreference<EditTextPreference>("prefName")
            val cardColor = findPreference<ListPreference>("cardColor") /*TODO: Does this mean that I would need to create a cardColor in MainActivity?*/

            if(prefName!!.text == null) {
                prefName.setDefaultValue("Player") /*TODO: What do we want our default value to be?*/
            }
            /*TODO: Do we need to create a new variable to save the player's name in the main activity?*/
            else userName = prefName.text

            color = cardColor!!.value /*TODO: I think we can set the default color to begin with and then just set it here to whatever value shows up from the settings*/

        }
    }
}