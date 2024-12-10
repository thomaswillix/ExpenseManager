package com.example.expensemanager

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import com.google.firebase.auth.FirebaseAuth

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

        /*if (nightMode){
            switch.isChecked = true
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
        switch.setOnClickListener { myThemes() }*/
        val logout : Button = findViewById(R.id.logoutButton)
        logout.setOnClickListener{
            showDialog()
        }

    }

    private fun showDialog() {
        val builder = AlertDialog.Builder(this)
        val view = layoutInflater.inflate(R.layout.dialog_logout, null)
        builder.setView(view)
        val dialog = builder.create()
        view.findViewById<Button>(R.id.btnReturn).setOnClickListener {
            dialog.dismiss()
        }
        view.findViewById<Button>(R.id.btnLogout).setOnClickListener {
            dialog.dismiss()
            logoutUser()
        }
        if (dialog.window != null){
            dialog.window!!.setBackgroundDrawable(ColorDrawable(0))
        }
        dialog.show()
    }

    private fun logoutUser() {
        FirebaseAuth.getInstance().signOut()
        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()

        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)

        finish()
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