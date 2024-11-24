package com.example.expensemanager

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity


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