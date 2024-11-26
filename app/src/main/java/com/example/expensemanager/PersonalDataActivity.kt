package com.example.expensemanager

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.expensemanager.databinding.ActivityPersonalDataBinding

class PersonalDataActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPersonalDataBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Cargar el fragmento ProfileFragment al iniciar la actividad
        binding = ActivityPersonalDataBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, ProfileFragment())
                .commit()
        }
    }
}