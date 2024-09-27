package com.example.expensemanager

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    val mEmail = findViewById<EditText>(R.id.email_login)
    val mPass = findViewById<EditText>(R.id.password_login)
    val btnLogin = findViewById<Button>(R.id.btn_login)
    val mForgetPass = findViewById<TextView>(R.id.forget_password)
    val mSignUpHere = findViewById<TextView>(R.id.signup_reg)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        loginDetails()
    }
    fun loginDetails(){
        btnLogin.setOnClickListener(View.OnClickListener {
            val email = mEmail.text.toString().trim()
            val password = mPass.text.toString().trim()

            if (TextUtils.isEmpty(email)){
                mEmail.setError("Email is required")
            }
            if (TextUtils.isEmpty(password)){
                mEmail.setError("Password is required")
            }
        })

        //Registration activity
        mSignUpHere.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this, RegistrationActivity::class.java))
        })
        //Reset password activity
        mForgetPass.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this, ResetActivity::class.java))
        })
    }
}