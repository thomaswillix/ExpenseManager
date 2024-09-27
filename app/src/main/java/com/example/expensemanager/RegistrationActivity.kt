package com.example.expensemanager

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth

class RegistrationActivity : AppCompatActivity() {

    // Firebase
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)
        auth = FirebaseAuth.getInstance()
        registration()
    }
    private fun registration(){
        val mEmail = findViewById<EditText>(R.id.email_registration)
        val mPass = findViewById<EditText>(R.id.password_registration)
        val btnReg = findViewById<Button>(R.id.btn_registration)
        val mSignIn = findViewById<TextView>(R.id.signin)

        btnReg.setOnClickListener(View.OnClickListener {
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
                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(
                    OnCompleteListener { task ->
                        if (task.isSuccessful) {
                            progressDialog.dismiss()
                            Toast.makeText(
                                applicationContext,
                                "Registration was completed successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                            startActivity(Intent(applicationContext, HomeActivity::class.java))
                        } else {
                            progressDialog.dismiss()
                            Toast.makeText(
                                applicationContext,
                                "Registration failed",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    })
                }
            })
            //Login activity
            mSignIn.setOnClickListener(View.OnClickListener {
                startActivity(Intent(this, MainActivity::class.java))
            })

        }

}