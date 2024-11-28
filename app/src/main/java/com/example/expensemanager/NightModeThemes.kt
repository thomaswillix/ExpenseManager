package com.example.expensemanager

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate

open class NightModeThemes : AppCompatActivity() {
    lateinit var sharedPreferences :SharedPreferences
    var nightMode: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)

        sharedPreferences = getSharedPreferences("Mode", Context.MODE_PRIVATE)
        nightMode = sharedPreferences.getBoolean("night",false)
        if (nightMode){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
    }
}