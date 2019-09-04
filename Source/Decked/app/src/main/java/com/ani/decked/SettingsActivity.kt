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

    //An intent is an action you intend to run; a bundle is something the intent has; intent-->ticket, bundle-->luggage
    override fun onBackPressed() {
        val savedSettings = Intent(this, MainActivity::class.java)
        savedSettings.putExtra("username", settings.findPreference<EditTextPreference>("name")?.text)
        savedSettings.putExtra("color",settings.findPreference<ListPreference>("color")?.value)
        setResult(RESULT_SUCCESS, savedSettings) //when asked for a result, setResult will create the result
        finish() //returns the result from setResult()
        super.onBackPressed()
    }

    class SettingsFragment : PreferenceFragmentCompat() {


        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences_settings, rootKey)
        }

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

        }
    }
}