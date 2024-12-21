package com.example.expensemanager

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.expensemanager.databinding.ActivityConfigurationBinding
import com.google.firebase.auth.FirebaseAuth

class ConfigurationActivity : AppCompatActivity() {

    private var nightMode: Boolean = false
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var sharedPreferences: SharedPreferences

    // View Binding
    private lateinit var binding: ActivityConfigurationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize view binding
        binding = ActivityConfigurationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up the toolbar
        setSupportActionBar(binding.myToolbarCfg)
        supportActionBar?.apply {
            show()
            setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24)
            setDisplayHomeAsUpEnabled(true)
        }

        // Load theme preference
        sharedPreferences = getSharedPreferences("Mode", Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
        nightMode = sharedPreferences.getBoolean("night", false)

        // Set initial state of the switch
        binding.switch1.isChecked = nightMode
        /*AppCompatDelegate.setDefaultNightMode(
            if (nightMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )*/

        // Handle switch toggle
        /*binding.switch1.setOnCheckedChangeListener { _, isChecked ->
            nightMode = isChecked
            applyTheme()
        }*/

        // Handle logout button click
        binding.logoutButton.setOnClickListener { showDialog() }
    }

    private fun showDialog() {
        val builder = AlertDialog.Builder(this)
        val view = layoutInflater.inflate(R.layout.dialog_logout, null)
        builder.setView(view)
        val dialog = builder.create()

        view.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnReturn)
            .setOnClickListener { dialog.dismiss() }

        view.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnLogout)
            .setOnClickListener {
                dialog.dismiss()
                logoutUser()
            }

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
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

    private fun applyTheme() {
        // Apply theme based on the updated nightMode value
        if (nightMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
        editor.putBoolean("night", nightMode)
        editor.apply()
    }
}
