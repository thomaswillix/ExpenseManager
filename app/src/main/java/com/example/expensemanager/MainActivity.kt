package com.example.expensemanager

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    // Firebase
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        auth = FirebaseAuth.getInstance()
        loginDetails()
    }
    fun loginDetails(){
        val mEmail = findViewById<EditText>(R.id.email_login)
        val mPass = findViewById<EditText>(R.id.password_login)
        val btnLogin = findViewById<Button>(R.id.btn_login)
        val mForgetPass = findViewById<TextView>(R.id.forget_password)
        val mSignUpHere = findViewById<TextView>(R.id.signup_reg)

        btnLogin.setOnClickListener(View.OnClickListener {
            val email = mEmail.text.toString().trim()
            val password = mPass.text.toString().trim()

            if (TextUtils.isEmpty(email)){
                mEmail.setError("Email is required")
            }
            if (TextUtils.isEmpty(password)){
                mPass.setError("Password is required")
            }
            if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
                val progressDialog = ProgressDialog(this)
                progressDialog.setMessage("Please wait")
                progressDialog.show()
                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(
                    OnCompleteListener { task ->
                        if (task.isSuccessful) {
                            progressDialog.dismiss()
                            Toast.makeText(
                                applicationContext,
                                "Login was completed successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                            startActivity(Intent(applicationContext, HomeActivity::class.java))
                        } else {
                            progressDialog.dismiss()
                            Toast.makeText(
                                applicationContext,
                                "Login failed",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                )
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