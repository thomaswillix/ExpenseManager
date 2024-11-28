package com.example.expensemanager

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat

class ConfigurationActivity : AppCompatActivity() {
    private var nightMode : Boolean = false
    private lateinit var  editor : SharedPreferences.Editor
    private lateinit var  sharedPreferences : SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_configuration)

        val toolbar : androidx.appcompat.widget.Toolbar = findViewById(R.id.myToolbarCfg)
        setSupportActionBar(toolbar)
        supportActionBar!!.show()
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val switch = findViewById<SwitchCompat>(R.id.switch1)

        sharedPreferences = getSharedPreferences("Mode", Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
        nightMode = sharedPreferences.getBoolean("night",false)

        if (nightMode){
            switch.isChecked = true
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
        switch.setOnClickListener { myThemes() }
    }


    private fun myThemes() {
        if (!nightMode){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            editor.putBoolean("night", false)
            editor.apply()

        }else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            editor.putBoolean("night",true)
            editor.apply()
        }
    }
}