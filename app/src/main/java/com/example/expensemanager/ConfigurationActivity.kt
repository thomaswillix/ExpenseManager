package com.example.expensemanager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class ConfigurationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_configuration)

        val toolbar : androidx.appcompat.widget.Toolbar = findViewById(R.id.myToolbarCfg)
        setSupportActionBar(toolbar)
        supportActionBar!!.show()
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }
}