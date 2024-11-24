package com.example.expensemanager.Profile

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.expensemanager.R


class PersonalDataActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Cargar el fragmento ProfileFragment al iniciar la actividad
        setContentView(R.layout.activity_personal_data)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, ProfileFragment())
                .commit()
        }
    }
}