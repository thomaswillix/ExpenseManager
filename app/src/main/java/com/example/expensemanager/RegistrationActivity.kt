package com.example.expensemanager

import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.os.Bundle
import android.widget.Button
import android.widget.EditText

class RegistrationActivity : AppCompatActivity() {

    val mEmail = findViewById<EditText>(R.id.email_registration)
    val mPass = findViewById<EditText>(R.id.password_registration)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)
        registration()
    }
    private fun registration(){
        val email = mEmail.editableText.toString()
        val password = mPass.editableText.toString()
        val btnReg = findViewById<Button>(R.id.btn_registration)
        btnReg.setOnClickListener(View.OnClickListener {

        })
    }
}