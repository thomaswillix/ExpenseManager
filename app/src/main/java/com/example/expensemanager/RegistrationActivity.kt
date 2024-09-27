package com.example.expensemanager

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.TextView

class RegistrationActivity : AppCompatActivity() {

    val mEmail = findViewById<EditText>(R.id.email_registration)
    val mPass = findViewById<EditText>(R.id.password_registration)
    val btnReg = findViewById<Button>(R.id.btn_registration)
    val mSignIn = findViewById<TextView>(R.id.signin)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)
        registration()
    }
    private fun registration(){
        btnReg.setOnClickListener(View.OnClickListener {
            val email = mEmail.text.toString().trim()
            val password = mPass.text.toString().trim()

            if (TextUtils.isEmpty(email)){
                mEmail.setError("Email is required")
            }
            if (TextUtils.isEmpty(password)){
                mEmail.setError("Password is required")
            }
        })
        //Login activity
        mSignIn.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        })
    }
}